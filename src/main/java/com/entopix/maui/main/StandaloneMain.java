package com.entopix.maui.main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import com.entopix.maui.core.MauiCore;
import com.entopix.maui.filters.MauiFilter;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stopwords.Stopwords;
import com.entopix.maui.stopwords.StopwordsPortuguese;
import com.entopix.maui.tests.StructuredTest2;
import com.entopix.maui.util.DataLoader;
import com.entopix.maui.util.Evaluator;
import com.entopix.maui.util.MauiDocument;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.util.Topic;
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
	
	//User I/O
	public static final Scanner SCAN = new Scanner(System.in);
	private static String input;
	
	//Model config
	private static final int ABSTRACTS = 0;
	private static final int FULLTEXTS = 1;
	private static final File MODELS_DIR = new File(MauiFileUtils.getModelsDirPath());
	
	private static int modelType = -1;
	private static File model = null;
	
	//TopicExtractor & ModelBuilder config
	private static Stopwords stopwords = new StopwordsPortuguese();
	private static Stemmer stemmer = null;
	private static String guiLanguage = "pt";
	private static String language = "pt";
	private static String encoding = "UTF-8";
	private static String vocabFormat = "skos";
	
	//Paths
	private static final String ABS_PATH = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\abstracts";
	private static final String FTS_PATH = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts";
	private static String vocabPath = MauiFileUtils.getVocabPath();
	private static String trainDirPath = null;
	private static String testDirPath = null;
	private static String testTxtDocPath = null; //urgently change this path to file
	
	public static void runWithArguments(String command, String[] args) throws Exception {
		String dataPath = MauiFileUtils.getDataPath();
		switch (command) {
		case "train":
			MauiModelBuilder modelBuilder = new MauiModelBuilder();
			modelBuilder.inputDirectoryName = dataPath + Utils.getOption('l', args);
			modelBuilder.modelName = dataPath + Utils.getOption('m', args);
			modelBuilder.vocabularyName = dataPath + Utils.getOption('v', args);
			modelBuilder.vocabularyFormat = Utils.getOption('f', args);
			modelBuilder.documentLanguage = Utils.getOption('i', args);
			modelBuilder.maxPhraseLength = 5;
			modelBuilder.minPhraseLength = 1;
			modelBuilder.minNumOccur = 1;
			modelBuilder.serialize = false; //true
			if (modelBuilder.documentLanguage.equals("pt")) {
				modelBuilder.stopwords = new StopwordsPortuguese();
				modelBuilder.stemmer = new PortugueseStemmer();
			} else {
				modelBuilder.stopwords = (Stopwords) Class.forName(Utils.getOption('s', args)).newInstance();
				modelBuilder.stemmer = (Stemmer) Class.forName(Utils.getOption('t', args)).newInstance();
			}
			
			modelBuilder.setPositionsFeatures(false);
			modelBuilder.setKeyphrasenessFeature(false);
			modelBuilder.setThesaurusFeatures(false);
			
			MauiFilter filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
			modelBuilder.saveModel(filter);
			break;
		case "test":
			MauiTopicExtractor topicExtractor = new MauiTopicExtractor();
			
			topicExtractor.inputDirectoryName = dataPath + Utils.getOption('l', args);
			topicExtractor.modelName = dataPath + Utils.getOption('m', args);
			topicExtractor.vocabularyName = dataPath + Utils.getOption('v', args);
			topicExtractor.vocabularyFormat = Utils.getOption('f', args);
			topicExtractor.documentLanguage = Utils.getOption('i', args);
			topicExtractor.cutOffTopicProbability = 0.12;
			topicExtractor.serialize = true;
			if (topicExtractor.documentLanguage.equals("pt")) {
				topicExtractor.stopwords = new StopwordsPortuguese();
				topicExtractor.stemmer = new PortugueseStemmer();
			} else {
				topicExtractor.stopwords = (Stopwords) Class.forName(Utils.getOption('s', args)).newInstance();
				topicExtractor.stemmer = (Stemmer) Class.forName(Utils.getOption('t', args)).newInstance();
			}
			
			topicExtractor.loadModel();

			List<MauiDocument> documents = DataLoader.loadTestDocuments(topicExtractor.inputDirectoryName);
			List<MauiTopics> topics = topicExtractor.extractTopics(documents);
			topicExtractor.printTopics(topics);
			Evaluator.evaluateTopics(topics);
			break;
		case "run":	
			String documentPath = dataPath + Utils.getOption('l', args);
			File document = new File(documentPath);
			String documentText = FileUtils.readFileToString(document, Charset.forName("UTF-8"));
			
			String modelPath = dataPath + Utils.getOption('m', args);
			String vocabPath = dataPath + Utils.getOption('v', args);
			String vocabularyFormat = Utils.getOption('f', args);
			int topicsPerDocument = 10;
			String documentLanguage = Utils.getOption('i', args);
			Stopwords stopwords = null;
			Stemmer stemmer = null;
			if (documentLanguage.equals("pt")) { //forces class initialization manually (ignores -s and -t arguments)
				stopwords = new StopwordsPortuguese();
				stemmer = new PortugueseStemmer();
			} else {
				stopwords = (Stopwords) Class.forName(Utils.getOption('s', args)).newInstance();
				stemmer = (Stemmer) Class.forName(Utils.getOption('t', args)).newInstance();
			}
			
			MauiWrapper mauiWrapper = null;
			mauiWrapper = new MauiWrapper(modelPath, vocabPath, vocabularyFormat, stopwords,stemmer, documentLanguage);
			mauiWrapper.setModelParameters(vocabPath, stemmer, stopwords, documentLanguage); 

	        ArrayList<Topic> keywords = mauiWrapper.extractTopicsFromText(documentText, topicsPerDocument);
	        for (Topic keyword : keywords) {
	        	System.out.println("Keyword: " + keyword.getTitle() + " " + keyword.getProbability());
	        }
			break;
		}
	}
	
	public static void runNoArguments() throws Exception {
		if (guiLanguage.equals("pt")) {
			runOptionsMenuPT();
		}
	}
	
	private static void runModelBuilder() throws Exception {
		MauiCore.setupAndBuildModel(trainDirPath, model.getPath(), vocabFormat, vocabPath, stemmer, stopwords, language, encoding);
	}
	
	private static void runTopicExtractor() throws MauiFilterException {
		MauiCore.setupAndRunTopicExtractor(testDirPath, model.getPath(), vocabPath, vocabFormat, stemmer, stopwords, language, encoding, 0.12, true, true);
	}
	
	private static void initConfigs() throws Exception {
		//Sets model from file if it exists
		if (!MauiFileUtils.isEmpty(MODELS_DIR)) {
			model = MODELS_DIR.listFiles()[0];
			updateModelType();
			updatePaths();
		}
		//Creates a new model
		else {
			trainDirPath = FTS_PATH + "\\train30";
			testDirPath = FTS_PATH + "\\test60";
			testTxtDocPath = testDirPath + "\\Artigo32.txt";
			stemmer = new PortugueseStemmer();
			
			String modelName = MauiPTUtils.generateModelName(trainDirPath, stemmer);
			model = new File(MODELS_DIR.getPath() + "\\" + modelName);
			modelType = FULLTEXTS;
			runModelBuilder();
		}
	}
	
	private static void updateModelType() {
		modelType = (model.getName().contains("abstracts") ? ABSTRACTS : FULLTEXTS);
	}
	
	private static void setModelName(String name) {
		model = new File(MODELS_DIR + "\\" + name);
	}
	
	/**
	 * Updates standard paths based on the document type of the model.
	 */
	private static void updatePaths() {
		trainDirPath = (modelType == ABSTRACTS ? ABS_PATH + "\\train30" : FTS_PATH + "\\train30");
		testDirPath = (modelType == ABSTRACTS ? ABS_PATH + "\\test60" : FTS_PATH + "\\test60");
		testTxtDocPath = testDirPath + "\\Artigo32.txt";
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
		System.out.println("\nNome do modelo: " + model.getName());
		System.out.println("Deseja alterar o nome do modelo?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			System.out.print("Novo nome: ");
			if (!input.equals("")) setModelName(input);
			else {
				UI.showInvalidOptionMessage();
				return;
			}
		}
		runModelBuilder();
	}
	
	private static void selectModelOption() {
		System.out.println("\nModelo Selecionado: " +  model.getName());
		model = MauiFileUtils.chooseFileFromList(MODELS_DIR.listFiles());
		updateModelType();
		updatePaths();
	}
	
	private static void testModelOption() throws MauiFilterException {
		System.out.println("\nModelo Atual: " + model.getName());
		System.out.println("Deseja alterar o modelo para o teste?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) selectModelOption();
		else if (!input.equals("")) {
			UI.showInvalidOptionMessage();
			return;
		}
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
				String browsingDir = (modelType == ABSTRACTS ? ABS_PATH : FTS_PATH);
				File testDir = MauiFileUtils.chooseFileFromList(MauiFileUtils.filterFileList(browsingDir, "test"));
				testDirPath = testDir.getPath();
				runTopicExtractor();
				break;
			case "2":
				System.out.print("\nDigite o caminho completo do diretório: ");
				input = SCAN.nextLine();
				if (MauiFileUtils.exists(input)) {
					testDirPath = input;
					runTopicExtractor();
				} else {
					UI.showDirectoryNotFoundMessage(input);
					return;
				}
				break;
			}
		} else if (input.equals("")) runTopicExtractor();
	}
	
	private static void runModelOption() throws IOException, MauiFilterException {
		System.out.println("\nModelo Atual: " + model.getName());
		System.out.println("Deseja alterar o modelo para o teste?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) selectModelOption();
		else if (!input.equals("")) {
			UI.showInvalidOptionMessage();
			return;
		}
		
		System.out.println("\n Arquivo Selecionado: " + testTxtDocPath);
		System.out.println("Deseja alterar o arquivo para o teste?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		if (input.equals("1")) {
			System.out.println("\n1 - Escolher da lista");
			System.out.println("2 - Arquivo customizado");
			System.out.print("Opção: ");
			input = SCAN.nextLine();
			switch (input) {
			case "1":
				String browsingDir = (modelType == ABSTRACTS ? ABS_PATH : FTS_PATH);
				browsingDir += "\\train60"; 
				File document = MauiFileUtils.chooseFileFromDirectory(browsingDir);
				testTxtDocPath = document.getPath();
				break;
			case "2":
				System.out.println("Digite o caminho completo do arquivo de texto: ");
				input = SCAN.nextLine();
				if (MauiFileUtils.exists(input)) {
					testTxtDocPath = input;
				} else {
					UI.showDirectoryNotFoundMessage(input);
					return;
				}
				break;
			}
			MauiCore.runMauiWrapperOnFile(new File(testTxtDocPath), model.getPath(), stemmer);
		}
	}
	
	private static void runOptionsMenuPT() throws Exception {
		
		initConfigs();
		
		String input;
		boolean exit = false;
		while (!exit) {
			UI.displayMainMenu();
			System.out.print("Opção: ");
			input = SCAN.nextLine();
			
			switch (input) {
			case "1":
				trainModelOption();
				break;
			case "2":
				selectModelOption();
				break;
			case "3":
				testModelOption();
				break;
			// RUN ON FILE
			case "4":
				runModelOption();
				break;
				
			// STRUCTURED TEST
			case "5":
				StructuredTest2.runAllTests();
				break;
			
			// ABOUT
			case "6":
				UI.displayCredits();
				System.out.println("Aperte [enter] para continuar ou 0 para sair.");
				input = SCAN.nextLine();
				if (input.equals("")) break;
				else if (input.equals("0")) exit = true;
				break;
				
			//EXIT OPTION
			case "0":
				exit = true;
				break;
			default:
				System.out.println("Opção inválida");
				break;
			}
		}
		SCAN.close();
	}

	public static void main(String[] args) throws Exception {
		if (args == null || args.length == 0) {
			UI.printPTCIMessage("pt");
			runNoArguments();
			return;
		}
		
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
