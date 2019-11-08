package com.entopix.maui.main;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import com.entopix.maui.core.MauiCore;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stopwords.Stopwords;
import com.entopix.maui.tests.StructuredTest2;
import com.entopix.maui.utils.MauiFileUtils;
import com.entopix.maui.utils.MauiPTUtils;
import com.entopix.maui.utils.UI;

import weka.core.Utils;

/**
 * StandaloneMain - allows us to run the train or test options in the single standalone,
 * as well as run Maui on a single file or a single text string.
 * 
 * Passes on all but the first option.
 * 
 * Warning: models with names that does not contain "abstracts" or "fulltexts" in its names WILL
 * break some parts of the program when using the interactive menu.
 *
 * @author Richard Vowles - https://google.com/+RichardVowles, Alyona Medelyan - medelyan@gmail.com
 * @author Renato Correa
 * @author Rahmon Jorge
 */
public class StandaloneMain {
	
	public static final Scanner SCAN = new Scanner(System.in);
	
	private static String input;
	
	/** The models directory. Can be set using MauiFileUtils.setModelsDirPath */
	private static final File MODELS_DIR = new File(MauiFileUtils.getModelsDirPath());
	
	/** Indicates that the current model was trained under abstracts documents. */
	private static final int ABSTRACTS = 0;
	
	/** Indicates that the current model was trained under full texts documents. */
	private static final int FULLTEXTS = 1;
	
	private static int modelType = -1;
	
	private static File model = null;
	
	private static File testDoc = null;
	
	private static Stemmer stemmer = null;
	
	/* PATHS */
	
	/** Path to the abstracts documents folder. */
	private static final String ABS_PATH = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\abstracts";
	
	/** Path to the full texts documents folder. */
	private static final String FTS_PATH = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts";
	
	private static String trainDirPath = null;
	
	private static String testDirPath = null;
	
	public static void runWithArguments(String command, String[] args) throws Exception {
		String dataPath = MauiFileUtils.getDataPath();
		String documentsPath = dataPath + Utils.getOption('l', args);
		String modelPath = dataPath + Utils.getOption('m', args);
		String vocabFormat = Utils.getOption('f', args);
		String vocabPath = dataPath + Utils.getOption('v', args);
		Stemmer stemmer = (Stemmer) Class.forName(Utils.getOption('t', args)).newInstance(); //use full package name
		Stopwords stopwords = (Stopwords) Class.forName(Utils.getOption('s', args)).newInstance(); //here too
		String language = Utils.getOption('i', args);
		String encoding = Utils.getOption('e', args);
		boolean serialize = Boolean.parseBoolean(Utils.getOption('z', args));
		int numTopicsToExtract = Integer.parseInt(Utils.getOption('n', args));
		
		double cutOffTopicProbability = MauiCore.getCutOffTopicProbability();
		
		if (command.equals("train")) MauiCore.setupAndBuildModel(documentsPath, modelPath, vocabFormat, vocabPath, stemmer, stopwords, language);
		else if (command.equals("test")) MauiCore.setupAndRunTopicExtractor(documentsPath, modelPath, vocabPath, vocabFormat, stemmer, stopwords, language, encoding, cutOffTopicProbability, serialize, true);
		else if (command.equals("run")) MauiCore.runMauiWrapperOnFile(modelPath, new File(documentsPath), vocabFormat, stemmer, stopwords, vocabPath, language, numTopicsToExtract);
		else throw new Exception("Invalid command");
	}
	
	private static void runModelBuilder() throws Exception {
		MauiCore.setupAndBuildModel(trainDirPath, model.getPath(), stemmer);
	}
	
	private static void runTopicExtractor() throws MauiFilterException {
		MauiCore.setupAndRunTopicExtractor(model.getPath(), testDirPath, stemmer, true);
	}
	
	private static void runMauiWrapper() throws IOException, MauiFilterException {
		MauiCore.runMauiWrapperOnFile(testDoc, model.getPath(), stemmer);
	}
	
	/** Updates the modelType and stemmer based on model name */
	private static void updateModelSetup() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		//Updates model type
		modelType = (model.getName().contains("abstracts") ? ABSTRACTS : FULLTEXTS);
		//Updates model stemmer
		int start = MauiPTUtils.ordinalIndexOf(model.getName(), "_", 1);
		int end = MauiPTUtils.ordinalIndexOf(model.getName(), "_", 2);
		String stemmerName = "com.entopix.maui.stemmers." + model.getName().substring(start + 1, end);
		stemmer = (Stemmer) Class.forName(stemmerName).newInstance();
	}
	
	/** Updates standard paths based on the document type of the model. */
	private static void updatePaths() {
		trainDirPath = (modelType == ABSTRACTS ? ABS_PATH + "\\train30" : FTS_PATH + "\\train30");
		testDirPath = (modelType == ABSTRACTS ? ABS_PATH + "\\test60" : FTS_PATH + "\\test60");
		testDoc = new File(testDirPath + "\\Artigo32.txt");
	}
	
	private static void trainModelOption() throws Exception {
		System.out.println("\nEscolha o tipo de modelo: ");
		System.out.println("1 - Resumos");
		System.out.println("2 - Textos Completos");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) modelType = ABSTRACTS;
		else if (input.equals("2")) modelType = FULLTEXTS;
		else {
			UI.showInvalidOptionMessage();
			return;
		}
		updatePaths();
		System.out.println("\nDiretório de Treinamento: " + trainDirPath);
		System.out.println("Deseja alterar o diretório de treinamento?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			System.out.println("\n1 - Escolher da lista");
			System.out.println("2 - Diretório customizado");
			System.out.print("Opção: ");
			input = SCAN.nextLine();
			switch (input) {
			case "1":
				System.out.println();
				String browsingDir = (modelType == ABSTRACTS ? ABS_PATH : FTS_PATH);
				File trainDir = MauiFileUtils.chooseFileFromList(MauiFileUtils.filterFileList(browsingDir, "train"));
				trainDirPath = trainDir.getPath();
				break;
			case "2":
				System.out.println("\nDigite o caminho completo do diretório: ");
				System.out.print("Opção: ");
				input = SCAN.nextLine();
				if (MauiFileUtils.exists(input)) trainDirPath = input;
				else UI.showDirectoryNotFoundMessage(input);
				break;
			default:
				UI.showInvalidOptionMessage();
				return;
			}
		}
		System.out.println("\nStemizador: " + stemmer.getClass().getSimpleName());
		System.out.println("Deseja alterar o stemizador?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			Stemmer[] stemmers = MauiCore.getStemmerList();
			System.out.println();
			for (int i = 0; i < stemmers.length; i++) {
				System.out.println(i+1 + " - " + stemmers[i].getClass().getSimpleName());
			}
			System.out.print("Opção: ");
			input = SCAN.nextLine();
			stemmer = stemmers[Integer.parseInt(input) - 1];
			System.out.println("Stemizador " + stemmer.getClass().getSimpleName() + " selecionado.");
		}
		
		String modelName = MauiPTUtils.generateModelName(trainDirPath, stemmer);
		System.out.println("\nNome do modelo: " + modelName);
		System.out.println("Deseja alterar o nome do modelo?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			System.out.println("AVISO: Utilizar um nome de modelo customizado resultará em erros. Deseja continuar?");
			System.out.println("1 - Sim");
			System.out.println("[Enter] - Não");
			if (input.equals("1")) {
				System.out.print("Novo nome: ");
				input = SCAN.nextLine();
				if (!input.equals("")) model = new File(MODELS_DIR + "\\" + input);
				else {
					UI.showInvalidOptionMessage();
					return;
				}
			}
		}
		model = new File(MODELS_DIR + "\\" + modelName);
		runModelBuilder();
	}
	
	private static void selectModelOption() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		System.out.println("\nModelo Atual: " +  model.getName());
		model = MauiFileUtils.chooseFileFromList(MODELS_DIR.listFiles());
		updateModelSetup();
		updatePaths();
		System.out.println("Modelo " + model.getName() + " selecionado.");
	}
	
	private static void testModelOption() throws MauiFilterException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		System.out.println("\nModelo Atual: " + model.getName());
		System.out.println("Deseja alterar o modelo para o teste?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) selectModelOption();
		System.out.println("\nDiretório de teste: " + testDirPath);
		System.out.println("Deseja alterar o diretório de teste?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			System.out.println("\n1 - Escolher da lista");
			System.out.println("2 - Diretório customizado");
			System.out.print("Opção: ");
			input = SCAN.nextLine();
			switch (input) {
			case "1":
				System.out.println();
				String browsingDir = (modelType == ABSTRACTS ? ABS_PATH : FTS_PATH);
				File testDir = MauiFileUtils.chooseFileFromList(MauiFileUtils.filterFileList(browsingDir, "test"));
				testDirPath = testDir.getPath();
				break;
			case "2":
				System.out.print("\nDigite o caminho completo do diretório: ");
				input = SCAN.nextLine();
				if (MauiFileUtils.exists(input)) {
					testDirPath = input;
				} else {
					UI.showDirectoryNotFoundMessage(input);
					return;
				}
				break;
			}
		}
		runTopicExtractor();
	}
	
	private static void runModelOption() throws IOException, MauiFilterException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		System.out.println("\nModelo Atual: " + model.getName());
		System.out.println("Deseja alterar o modelo?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) selectModelOption();
		
		System.out.println("\nArquivo Selecionado: " + testDoc.getName());
		System.out.println("Deseja alterar o arquivo para o teste?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			System.out.println("\n1 - Escolher da lista");
			System.out.println("2 - Arquivo customizado");
			System.out.print("Opção: ");
			input = SCAN.nextLine();
			switch (input) {
			case "1":
				String browsingDir = (modelType == ABSTRACTS ? ABS_PATH : FTS_PATH) + "\\test60";
				File document = MauiFileUtils.chooseFileFromDirectory(browsingDir);
				testDoc = new File(document.getPath());
				break;
			case "2":
				System.out.println("Digite o caminho completo do arquivo de texto: ");
				input = SCAN.nextLine();
				if (MauiFileUtils.exists(input)) {
					testDoc = new File(input);
				} else {
					UI.showDirectoryNotFoundMessage(input);
					return;
				}
				break;
			}
		}
		runMauiWrapper();
	}

	private static void deleteModelsOption() throws IOException {
		System.out.println("\nIsto apagará todos os modelos. Deseja continuar? ");
		System.out.println("1 - Sim");
		System.out.println("2 - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1"))  {
			FileUtils.cleanDirectory(MODELS_DIR);
			System.out.println("Modelos excluídos com sucesso.");
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		/* RUNNING WITH NO ARGUMENTS */
		
		if (args == null || args.length == 0) {
		
			UI.instructUser("pt");
			UI.printPTCIMessage("pt");
			
			//Sets model from file if it exists
			if (!MauiFileUtils.isEmpty(MODELS_DIR)) {
				model = MODELS_DIR.listFiles()[0];
				updateModelSetup();
				updatePaths();
			} else { //If it doesn't, create a new model
				trainDirPath = FTS_PATH + "\\train30";
				testDirPath = FTS_PATH + "\\test60";
				testDoc = new File(testDirPath + "\\Artigo32.txt");
				stemmer = new PortugueseStemmer();	
				String modelName = MauiPTUtils.generateModelName(trainDirPath, stemmer);
				model = new File(MODELS_DIR.getPath() + "\\" + modelName);
				modelType = FULLTEXTS;
				runModelBuilder();
			}
			
			boolean exit = false;
			while (!exit) {
				System.out.println("\n1 - Criar modelo  ");
				System.out.println("2 - Selecionar modelo");
				System.out.println("3 - Testar modelo em diretório  ");
				System.out.println("4 - Executar modelo em arquivo  ");
				System.out.println("5 - Limpar diretório de modelos  ");
				System.out.println("6 - Executar teste estruturado  ");
				System.out.println("7 - Sobre");
				System.out.println("0 - Sair  ");
				System.out.print("Opção: ");
				input = SCAN.nextLine();
				
				if (input.equals("1")) trainModelOption();
				else if (input.equals("2")) selectModelOption();
				else if (input.equals("3")) testModelOption();
				else if (input.equals("4")) runModelOption();
				else if (input.equals("5")) deleteModelsOption();
				else if (input.equals("6")) StructuredTest2.runAllTests();
				else if (input.equals("7")) {
					UI.displayCredits();
					System.out.println("Aperte [enter] para continuar ou 0 para sair.");
					input = SCAN.nextLine();
					if (input.equals("")) continue;
					else if (input.equals("0")) exit = true;
				} else if (input.equals("0")) exit = true;
				else UI.showInvalidOptionMessage();	
			}
			SCAN.close();
		}
		
		/* RUNNING WITH ARGUMENTS */
		
		else {
			String command = args[0].toLowerCase(); 
			if ((!command.equals("train") && !command.equals("test") && !command.equals("run"))) {
				UI.instructUser(Utils.getOption('i', args));
				System.exit(-1);
			}
			String[] remainingArgs = new String[args.length - 1];
			System.arraycopy(args, 1, remainingArgs, 0, args.length-1);
			StandaloneMain.runWithArguments(command, remainingArgs);
		}
	}
}
