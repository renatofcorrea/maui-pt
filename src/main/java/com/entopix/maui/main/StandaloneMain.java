package com.entopix.maui.main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import com.entopix.maui.core.MauiCore;
import com.entopix.maui.core.ModelWrapper;
import com.entopix.maui.exceptions.EmptyModelsDirException;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.stemmers.LuceneBRStemmer;
import com.entopix.maui.stemmers.LuceneRSLPMinimalStemmer;
import com.entopix.maui.stemmers.LuceneRSLPStemmer;
import com.entopix.maui.stemmers.LuceneSavoyStemmer;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stemmers.WekaStemmerOrengo;
import com.entopix.maui.stemmers.WekaStemmerPorter;
import com.entopix.maui.stemmers.WekaStemmerSavoy;
import com.entopix.maui.stopwords.Stopwords;
import com.entopix.maui.tests.ResultMatrixes;
import com.entopix.maui.tests.StructuredTest;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.utils.MPTUtils;
import com.entopix.maui.utils.MauiFileUtils;
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
	
	/** Indicates that the current model was trained under abstracts or fulltexts documents. */
	private static final int ABSTRACTS = 0, FULLTEXTS = 1;
	
	/** Variable that holds the enums above.*/
	private static int modelType = -1;
	
	private static ModelWrapper model = null;
	
	private static File testDoc = null;
	
	private static Stemmer stemmer = new PortugueseStemmer();
	
	private static Stemmer[] stemmerList = {
			new PortugueseStemmer(),
			new LuceneRSLPStemmer(),
			new LuceneBRStemmer(),
			new LuceneSavoyStemmer(),
			new LuceneRSLPMinimalStemmer(),
			new WekaStemmerOrengo(),
			new WekaStemmerPorter(),
			new WekaStemmerSavoy(),
	};
	
	/** Path to the abstracts documents folder. */
	private static final String ABS_PATH = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\abstracts";
	
	/** Path to the full texts documents folder. */
	private static final String FTS_PATH = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts";
	
	/** Path to the test results file. */
	private static String resultsPath = MauiFileUtils.getDataPath() + "\\tests" + "\\test_results_" +MPTUtils.getDate();
	
	private static String trainDirPath = null, testDirPath = null;
	
	private static String modelPath, modelName;
	
	/**
	 * @param command "train", "test" or "run" <br>
	 * @param args valid arguments: <br>
	 * -l documents directory <br>
	 * -m model path <br>
	 * -v vocabulary path <br>
	 * -f vocabulary format <br>
	 * -i language <br>
	 * -e encoding <br>
	 * -z serialize vocabulary? <br>
	 * -n number of topics to extract <br>
	 * -s stopwords class <br>
	 * -t stemmer class <br>
	 * @throws Exception
	 */
	public static void runWithArguments(String command, String[] args) throws Exception {
		String dataPath = MauiFileUtils.getDataPath();
		String documentsPath = dataPath + Utils.getOption('l', args);
		String modelPath = dataPath + Utils.getOption('m', args);
		String vocabPath = dataPath + Utils.getOption('v', args);
		
		String vocabFormat = MauiCore.getVocabFormat();
		String input = Utils.getOption('f', args);
		if (MPTUtils.isValid(input)) vocabFormat = input;
		
		String language = MauiCore.getLanguage();
		input = Utils.getOption('i', args);
		if (MPTUtils.isValid(input)) language = input;
		
		String encoding = MauiCore.getEncoding();
		input = Utils.getOption('e', args);
		if (MPTUtils.isValid(input)) encoding = input;
		
		boolean serialize = MauiCore.isVocabSerialize();
		input = Utils.getOption('z', args);
		if (MPTUtils.isValid(Utils.getOption('z', args))) serialize = Boolean.parseBoolean(input);
		
		int numTopicsToExtract = MauiCore.getNumTopicsToExtract();
		input = Utils.getOption('n', args);
		if (MPTUtils.isValid(input)) numTopicsToExtract = Integer.parseInt(input);
		
		Stopwords stopwords = MauiCore.getStopwords();
		input = Utils.getOption('s', args);
		if (MPTUtils.isValid(input)) {
			stopwords = (Stopwords) Class.forName(MauiCore.getStopwordsPackage() + Utils.getOption('s', args)).newInstance();
		}
		
		Stemmer stemmer = MauiCore.getStemmer();
		input = Utils.getOption('t', args);
		if (MPTUtils.isValid(input)) {
			stemmer = (Stemmer) Class.forName(MauiCore.getStemmersPackage() + Utils.getOption('t', args)).newInstance();
		}
		
		MauiCore.setStemmer(stemmer);
		MauiCore.setStopwords(stopwords);
		MauiCore.setLanguage(language);
		MauiCore.setEncoding(encoding);
		MauiCore.setVocabPath(vocabPath);
		
		if (MPTUtils.isValid(vocabPath)) {
			MauiCore.setVocabFormat(vocabFormat);
		}
		
		if (command.equals("train")) {
			MauiCore.setModelPath(modelPath);
			MauiCore.setTrainDirPath(documentsPath);
			MauiCore.buildModel();
		}
		
		if (MauiFileUtils.exists(modelPath)) {
			MauiCore.setModelPath(modelPath);
			MauiCore.setModel((ModelWrapper) MauiFileUtils.deserializeObject(modelPath));
		} else {
			throw new Exception("Model " + modelPath + " was not found");
		}
		
		if (command.equals("test")) {
			MauiCore.setTestDirPath(documentsPath);
			MauiCore.setVocabSerialize(serialize);
			MauiCore.runTopicExtractor();
			
		} else if (command.equals("run")) {
			MauiCore.setTestDocFile(new File(documentsPath));
			MauiCore.setNumTopicsToExtract(numTopicsToExtract);
			MauiCore.runMauiWrapperOnFile();
		}
	}
	
	private static void runModelBuilder() throws Exception {
		MauiCore.setTrainDirPath(trainDirPath);
		MauiCore.setModelPath(modelPath);
		MauiCore.setStemmer(stemmer);
		MauiCore.buildModel();
	}
	
	private static List<MauiTopics> runTopicExtractor() throws Exception {
		MauiCore.setModelPath(modelPath);
		MauiCore.setTestDirPath(testDirPath);
		MauiCore.setStemmer(stemmer);
		MauiCore.setModel(model);
		return MauiCore.runTopicExtractor();
	}
	
	private static void runMauiWrapper() throws IOException, MauiFilterException {
		MauiCore.setTestDocFile(testDoc);
		MauiCore.setModelPath(modelPath);
		MauiCore.setStemmer(stemmer);
		MauiCore.setModel(model);
		MauiCore.runMauiWrapperOnFile();
	}
	
	/**
	 * Updates the model name, type and stemmer.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private static void updateModelSetup() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		modelName = new File(modelPath).getName();
		modelType = (model.getTrainingPath().contains("abstracts") ? ABSTRACTS : FULLTEXTS); //Updates model type
		stemmer = model.getStemmerUsed(); //Updates model stemmer
	}
	
	/** 
	 * Updates standard paths based on the document type of the model.
	 */
	private static void updatePaths() {
		trainDirPath = (modelType == ABSTRACTS ? ABS_PATH + "\\train30" : FTS_PATH + "\\train30");
		testDirPath = (modelType == ABSTRACTS ? ABS_PATH + "\\test60" : FTS_PATH + "\\test60");
		testDoc = new File(testDirPath + "\\Artigo32.txt");
	}
	
	private static void selectModel() throws InstantiationException, IllegalAccessException, ClassNotFoundException, EmptyModelsDirException {
		System.out.println("\nEscolha o modelo:");
		System.out.println("\n1 - Escolher da lista");
		System.out.println("2 - Escolher do arquivo");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			if (MauiFileUtils.isEmpty(MODELS_DIR)) {
				System.out.println("\nO diretório de modelos está vazio.");
				throw new EmptyModelsDirException();
			}
			chooseModelFromList();
		}
		else if (input.equals("2")) {
			System.out.println("\nDigite o caminho completo do modelo a ser carregado: ");
			System.out.print("Caminho: ");
			input = SCAN.nextLine();
			if (MPTUtils.isValid(input)) {
				modelPath = input;
				model = (ModelWrapper) MauiFileUtils.deserializeObject(modelPath);
				updateModelSetup();
				updatePaths();
				System.out.println("\nModelo " + modelName + " carregado.");
			}
		}
	}
	
	private static void chooseModelFromList() throws InstantiationException, IllegalAccessException, ClassNotFoundException, EmptyModelsDirException {
		if (MauiFileUtils.isEmpty(MODELS_DIR)) {
			System.out.println("\nO diretório de modelos está vazio.");
			throw new EmptyModelsDirException();
		}
		System.out.println();
		modelPath = MauiFileUtils.chooseFileFromList(MODELS_DIR.listFiles(), SCAN).getPath();
		model = (ModelWrapper) MauiFileUtils.deserializeObject(modelPath);
		updateModelSetup();
		updatePaths();
		System.out.println("\nModelo " + modelName + " carregado.");
	}
	
	private static void optionTrainModel() throws Exception {
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
			System.out.println("2 - Escolher do arquivo");
			System.out.print("Opção: ");
			input = SCAN.nextLine();
			switch (input) {
			case "1":
				System.out.println();
				String browsingDir = (modelType == ABSTRACTS ? ABS_PATH : FTS_PATH);
				File trainDir = MauiFileUtils.chooseFileFromList(MauiFileUtils.filterFileList(browsingDir, "train"), SCAN);
				trainDirPath = trainDir.getPath();
				break;
			case "2":
				System.out.println("\nDigite o caminho completo do diretório: ");
				System.out.print("Opção: ");
				input = SCAN.nextLine();
				if (MauiFileUtils.exists(input)) trainDirPath = input;
				else UI.showFileNotFoundMessage(input);
				break;
			default:
				UI.showInvalidOptionMessage();
				return;
			}
		}
		System.out.println("\nRadicalizador: " + stemmer.getClass().getSimpleName());
		System.out.println("Deseja alterar o radicalizador?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			System.out.println();
			for (int i = 0; i < stemmerList.length; i++) {
				System.out.println(i+1 + " - " + stemmerList[i].getClass().getSimpleName());
			}
			System.out.print("Opção: ");
			input = SCAN.nextLine();
			stemmer = stemmerList[Integer.parseInt(input) - 1];
			System.out.println("Radicalizador " + stemmer.getClass().getSimpleName() + " selecionado.");
		}
		
		modelName = MPTUtils.generateModelName(trainDirPath, stemmer);
		System.out.println("\nNome gerado: " + modelName);
		System.out.println("Deseja alterar o nome do modelo?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			System.out.print("Novo nome: ");
			input = SCAN.nextLine();
			if (!input.equals("")) {
				modelName = input;
				modelPath = MODELS_DIR + "\\" + modelName;
			}
			else {
				UI.showInvalidOptionMessage();
				return;
			}
		} else modelPath = MODELS_DIR + "\\" + modelName;
		
		runModelBuilder();
		model = MauiCore.getModel();
	}
	
	private static void optionTestOnDirectory() throws Exception {
		
		try {
			selectModel();
		} catch (EmptyModelsDirException e) {
			return;
		}
		
		//Choose directory
		File testDir = null;
		System.out.println("\nEscolha o diretório de teste:");
		System.out.println("\n1 - Escolher da lista");
		System.out.println("2 - Escolher do arquivo");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		switch (input) {
		case "1":
			System.out.println();
			String browsingDir = (modelType == ABSTRACTS ? ABS_PATH : FTS_PATH);
			testDir = MauiFileUtils.chooseFileFromList(MauiFileUtils.filterFileList(browsingDir, "test"), SCAN);
			testDirPath = testDir.getPath();
			break;
		case "2":
			System.out.print("\nDigite o caminho completo do diretório: ");
			input = SCAN.nextLine();
			if (MauiFileUtils.exists(input)) {
				testDirPath = input;
			} else {
				UI.showFileNotFoundMessage(input);
				return;
			}
			break;
		}
		
		List<MauiTopics> topics = runTopicExtractor();
		ResultMatrixes.buildAndSaveResultsWorkbook(topics, testDirPath, resultsPath);
	}
	
	private static void optionRunOnFile() throws IOException, MauiFilterException, InstantiationException, IllegalAccessException, ClassNotFoundException, EmptyModelsDirException {
		
		try {
			selectModel();
		} catch (EmptyModelsDirException e) {
			return;
		}
		System.out.println("\nArquivo Selecionado: " + testDoc.getName());
		System.out.println("Deseja alterar o arquivo para o teste?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			System.out.println("\n1 - Escolher da lista");
			System.out.println("2 - Escolher do arquivo");
			System.out.print("Opção: ");
			input = SCAN.nextLine();
			switch (input) {
			case "1":
				String browsingDir = (modelType == ABSTRACTS ? ABS_PATH : FTS_PATH) + "\\test60";
				File document = MauiFileUtils.chooseFileFromDirectory(browsingDir, SCAN);
				testDoc = new File(document.getPath());
				break;
			case "2":
				System.out.println("Digite o caminho completo do arquivo de texto: ");
				input = SCAN.nextLine();
				if (MauiFileUtils.exists(input)) {
					testDoc = new File(input);
				} else {
					UI.showFileNotFoundMessage(input);
					return;
				}
				break;
			}
		}
		
		runMauiWrapper();
	}

	private static void optionDeleteModels() throws IOException {
		if (MauiFileUtils.isEmpty(MODELS_DIR)) {
			System.out.println("\nO diretório de modelos está vazio.");
			return;
		}
		System.out.println("\n1 - Excluir um modelo");
		System.out.println("2 - Excluir todos os modelos");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			System.out.println();
			File model = MauiFileUtils.chooseFileFromList(MODELS_DIR.listFiles(), SCAN);
			String name = model.getName();
			model.delete();
			System.out.println("\nModelo " + name + " excluído.");
		} else if (input.equals("2")){
			System.out.println("\nIsto apagará todos os modelos. Deseja continuar? ");
			System.out.println("1 - Sim");
			System.out.println("2 - Não");
			System.out.print("Opção: ");
			input = SCAN.nextLine();
			if (input.equals("1"))  {
				FileUtils.cleanDirectory(MODELS_DIR);
				System.out.println("Todos os modelos excluídos com sucesso.");
			}
			model = null;
			modelPath = null;
			modelName = null;
		} else UI.showInvalidOptionMessage();
	}
	
	private static void optionEvaluateIndexing() throws Exception { //TODO: adicionar opção para testar em diretório
		System.out.println("\nInsira o caminho do arquivo contendo as palavras chave de saída do sistema de indexação.");
		System.out.print("Caminho: ");
		input = SCAN.nextLine();
		if (!MauiFileUtils.exists(input)) {
			UI.showFileNotFoundMessage(input);
			return;
		}
		String extractedTopicsPath = input;
		
		System.out.println("Insira o caminho do arquivo contendo as palavras chave originais do documento.");
		System.out.print("Caminho: ");
		input = SCAN.nextLine();
		if (!MauiFileUtils.exists(input)) {
			UI.showFileNotFoundMessage(input);
			return;
		}
		String originalTopicsPath = input;
		String filename = MPTUtils.removeSuffix(new File(input).getName());
		
		String[] extractedTopicsList = MauiFileUtils.readKeyFromFile(extractedTopicsPath);
		String[] manualTopicsList = MauiFileUtils.readKeyFromFile(originalTopicsPath);
		
		MauiCore.evaluateTopics(filename, manualTopicsList, extractedTopicsList , extractedTopicsList.length, true);
	}
	
	private static void optionShowCredits() {
		UI.displayCredits();
		System.out.println("Aperte [enter] para continuar...");
		input = SCAN.nextLine();
	}

	public static void main(String[] args) throws Exception {
		
		/* RUNNING WITH NO ARGUMENTS */
		
		if (args == null || args.length == 0) {
			UI.instructUser("pt");
			UI.printPTCIMessage("pt");
			
			while (true) {
				System.out.println("\n1 - Treinar modelo  ");
				System.out.println("2 - Excluir modelo  ");
				System.out.println("3 - Testar modelo em diretório  ");
				System.out.println("4 - Executar modelo em arquivo  ");
				System.out.println("5 - Executar teste estruturado  ");
				System.out.println("6 - Avaliar indexação  ");
				System.out.println("7 - Sobre");
				System.out.println("0 - Sair  ");
				System.out.print("Opção: ");
				if (SCAN.hasNext()) {
					input = SCAN.nextLine();
				}
				
				if (input.equals("1")) optionTrainModel();
				else if (input.equals("2")) optionDeleteModels();
				else if (input.equals("3")) optionTestOnDirectory();
				else if (input.equals("4")) optionRunOnFile();
				else if (input.equals("5")) StructuredTest.runAllTests();
				else if (input.equals("6")) optionEvaluateIndexing();
				else if (input.equals("7")) optionShowCredits();
				else if (input.equals("0")) break;
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
