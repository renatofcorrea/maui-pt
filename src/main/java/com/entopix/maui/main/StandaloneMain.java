package com.entopix.maui.main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import com.entopix.maui.beans.ModelDocType;
import com.entopix.maui.core.MauiCore;
import com.entopix.maui.filters.MauiFilter;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.stemmers.LuceneBRStemmer;
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
import com.entopix.maui.vocab.Vocabulary;

import weka.core.Utils;

/**
 * StandaloneMain - allows us to run the train or test options in the single standalone,
 * as well as run Maui on a single file or a single text string.
 *
 *
 * Passes on all but the first option.
 *
 * @author Richard Vowles - https://google.com/+RichardVowles, Alyona Medelyan - medelyan@gmail.com
 * @author Renato Correa
 * @author Rahmon Jorge
 */
public class StandaloneMain {
	
	//Initialization & configuration
	public static Scanner scan = new Scanner(System.in);
	static Stopwords stopwords = new StopwordsPortuguese();
	static Stemmer stemmer = new LuceneBRStemmer();
	static ModelDocType modelType = ModelDocType.FULLTEXTS;
	static String guiLanguage = "pt";
	static String language = "pt";
	static String encoding = "UTF-8";
	static String vocabFormat = "skos";
	static int numTopicsToExtract = 10;
	static boolean serialize = true;
	static boolean reorder = false;
	
	//Paths & Names
	static String dataPath = MauiFileUtils.getDataPath();
	static String abstractsPath = dataPath + "\\docs\\corpusci\\abstracts";
	static String fullTextsPath = dataPath + "\\docs\\corpusci\\fulltexts";
	static String trainDirPath = fullTextsPath + "\\train30";
	static String testDir = fullTextsPath + "\\test60";
	static String testFilePath = dataPath + "\\docs\\corpusci\\fulltexts\\test60\\Artigo32.txt";
	static String vocabPath = dataPath + "\\vocabulary\\TBCI-SKOS_pt.rdf";
	static String modelsDir = MauiFileUtils.getModelsDirPath();
	static String modelName = MauiPTUtils.generateModelName(trainDirPath, stemmer);
	static String modelPath = modelsDir + "\\" + modelName;
	
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

	private static void runOptionsMenuPT() throws Exception {
		String input;
		boolean exit = false;
		while (!exit) {
			UI.displayMainMenu();
			System.out.print("Opção: ");
			input = scan.nextLine();
			
			switch (input) {
			// TRAIN MODEL
			case "1":
				System.out.println();
				System.out.println("Escolha o tipo de modelo: ");
				System.out.println("1 - Resumos");
				System.out.println("2 - Textos Completos");
				System.out.print("Opção: ");
				input = scan.nextLine();
				switch(input) {
				case "1":
					modelType = ModelDocType.ABSTRACTS;
					break;
				case "2":
					modelType = ModelDocType.FULLTEXTS;
					break;
				default:
					UI.showInvalidOptionMessage();
					continue;
				}
				trainDirPath = (modelType.equals(ModelDocType.ABSTRACTS) ? abstractsPath + "\\train30" : trainDirPath);
				System.out.println("\nDiretório de Treinamento: " + trainDirPath);
				System.out.println("Deseja alterar o diretório de treinamento?");
				System.out.println("1 - Sim");
				System.out.println("[Enter] - Não");
				System.out.print("Opção: ");
				input = scan.nextLine();
				if (input.equals("1")) {
					System.out.println("\n1 - Escolher da lista");
					System.out.println("2 - Diretório customizado");
					System.out.print("Opção: ");
					System.out.println();
					input = scan.nextLine();
					switch (input) {
					case "1":
						String browsingDir = (modelType.equals(ModelDocType.ABSTRACTS) ? abstractsPath : fullTextsPath);
						File[] trainDirList = MauiFileUtils.filterFileList(new File(browsingDir).listFiles(), "train");
						File trainDirChosen = MauiFileUtils.chooseFileFromList(trainDirList);
						trainDirPath = trainDirChosen.getPath();
						break;
					case "2":
						System.out.println("\nDigite o caminho completo do diretório: ");
						System.out.print("Opção: ");
						input = scan.nextLine();
						if (MauiFileUtils.exists(input)) trainDirPath = input;
						else UI.showDirectoryNotFoundMessage(input);
						break;
					default:
						UI.showInvalidOptionMessage();
						continue;
					}
				}
				
				modelName = MauiPTUtils.generateModelName(trainDirPath, stemmer);
				System.out.println("\nNome do modelo: " + modelName);
				System.out.println("Deseja alterar o nome do modelo?");
				System.out.println("1 - Sim");
				System.out.println("[Enter] - Não");
				System.out.print("Opção: ");
				input = scan.nextLine(); //scan being ignored
				if (input.equals("1")) {
					System.out.print("Novo nome: ");
					if (!input.equals("")) {
						modelName = input;
					} else {
						UI.showInvalidOptionMessage();
						continue;
					}
				}
				modelPath = modelsDir + "\\" + modelName;
				MauiCore.setupAndBuildModel(trainDirPath, modelPath, vocabFormat, vocabPath, stemmer, stopwords, language, encoding);
				break; //end of train option
				
			// DISPLAY MODELS
			case "2":
				System.out.println();
				chooseFileFromList("model");
				System.out.println("\nModelo " + modelName + " selecionado.");
				break;
			
			// TEST ON DIRECTORY
			case "3":
				System.out.println("\nEscolha o modelo a ser utilizado ou aperte [enter] para usar o atual:");
				System.out.println("Atual: " + modelName + "\n");
				chooseFileFromList("model");
				
				System.out.println("\nEscolha o  diretório de teste ou aperte [enter] para usar o atual.");
				testDir = (modelType.equals(ModelDocType.ABSTRACTS) ? abstractsPath + "\\test60" : fullTextsPath + "\\test60");
				System.out.println("Atual: " + testDir);
				System.out.println("1 - Escolher da lista");
				System.out.println("2 - Diretório customizado");
				System.out.print("Opção: ");
				input = scan.nextLine();
				switch (input) {
				case "1":
					chooseFileFromList("test");
					MauiCore.setupAndRunTopicExtractor(testDir, modelPath, vocabPath, vocabFormat, stemmer, stopwords, language, encoding, 0.12, true, true);
					break;
				case "2":
					System.out.println("Digite o caminho completo do diretório: ");
					input = scan.nextLine();
					if (MauiFileUtils.exists(input)) {
						testDir = input;
						MauiCore.setupAndRunTopicExtractor(testDir, modelPath, vocabPath, vocabFormat, stemmer, stopwords, language, encoding, 0.12, true, true);
					} else {
						UI.showDirectoryNotFoundMessage(input);
						continue;
					}
					break;
				case "":
					MauiCore.setupAndRunTopicExtractor(testDir, modelPath, vocabPath, vocabFormat, stemmer, stopwords, language, encoding, 0.12, true, true);
					break;
				}
				break;
				
			// RUN ON FILE
			case "4":
				System.out.println("\nEscolha o modelo a ser utilizado ou aperte [enter] para usar o atual: ");
				System.out.println("Atual: " + modelName + "\n");
				chooseFileFromList("model");
				
				System.out.println("\nEscolha o arquivo (.txt) ou aperte [enter] para usar o atual: ");
				testFilePath = (modelType.equals(ModelDocType.ABSTRACTS) ? abstractsPath + "\\test60\\Artigo32.txt" : fullTextsPath + "\\test60\\Artigo32.txt");
				System.out.println("Arquivo selecionado: " + testFilePath);
				System.out.println("1 - Escolher da lista");
				System.out.println("2 - Arquivo customizado");
				System.out.print("Opção: ");
				input = scan.nextLine();
				switch (input) {
				case "1":
					chooseFileFromList("run");
					MauiCore.runMauiWrapperOnFile(new File(testFilePath), modelPath, stemmer);
					//runMauiWrapperOnFile();
					break;
				case "2":
					System.out.println("Digite o caminho completo do arquivo de texto: ");
					input = scan.nextLine();
					if (MauiFileUtils.exists(input)) {
						testFilePath = input;
						//runMauiWrapperOnFile();
					} else {
						UI.showDirectoryNotFoundMessage(input);
						continue;
					}
					break;
				case "":
					//runMauiWrapperOnFile();
					break;
				}
				break;
				
			// STRUCTURED TEST
			case "5":
				StructuredTest2.runAllTests();
				break;
			
			// ABOUT
			case "6":
				UI.displayCredits();
				System.out.println("Aperte [enter] para continuar ou 0 para sair.");
				input = scan.nextLine();
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
		scan.close();
	}

	public static void chooseFileFromList(String dirType) {
		String input, fileChoice;
		File dir = null;
		File[] fileList = null;
		
		//Set model browsing directory
		if (dirType.equals("model")) {
			dir = new File(modelsDir);
		} else {
			if (modelType.equals(ModelDocType.ABSTRACTS)) {
				dir = new File(abstractsPath);
			} else if (modelType.equals(ModelDocType.FULLTEXTS)) {
				dir = new File(fullTextsPath);
			} else {
				System.out.println("ERRO: Tipo de Modelo Inválido.");
				return;
			}
		}
		
		
		fileList = dir.listFiles();
		
		if (dirType.equals("train") || dirType.equals("test") || dirType.equals("model")) {
			fileList = filterFileList(fileList, dirType);
		} else {
			System.out.println("ERRO: Tipo do diretório inválido");
		}
		
		System.out.println("Diretório atual: " + dir.getAbsolutePath());
		MauiFileUtils.displayFileList(fileList);
		if (dirType.equals("model")) System.out.print("Digite o número correspondente ao modelo ou aperte [enter para usar o atual]: ");
		else System.out.print("Opção: ");
		input = scan.nextLine();
		
		if (input.equals("") && dirType.equals("model")) return; //User selected standard model
		
		fileChoice = fileList[Integer.parseInt(input)-1].getAbsolutePath(); //OBS: Files are displayed starting with 1, but array starts with 0
		
		switch(dirType) {
		case "train":
			trainDirPath = fileChoice;
			break;
		case "test":
			testDir = fileChoice;
			break;
		case "model":
			modelPath = fileChoice;
			modelName = new File(modelPath).getName();
			if (modelName.contains("abstracts")) modelType = ModelDocType.ABSTRACTS;
			else if (modelName.contains("fulltexts")) modelType = ModelDocType.FULLTEXTS;
			break;
		case "run":
			File txtFileFolder = new File(fileChoice);
			fileList = txtFileFolder.listFiles();
			fileList = filterFileList(fileList, ".txt");
			
			System.out.println("Diretório atual: " + txtFileFolder.getAbsolutePath());
			MauiFileUtils.displayFileList(fileList);
			System.out.print("Opção: ");
			input = scan.nextLine();
			
			testFilePath = fileList[Integer.parseInt(input)-1].getAbsolutePath(); //WARNING: Files are displayed starting with 1, but array starts with 0
			break;
		}
	}

	private static File[] filterFileList(File[] array, String filterMethod) {
		ArrayList<File> newArray = new ArrayList<File>();
		
		switch(filterMethod) {
		case ".txt":
			for (File f : array) {
				if (f.getName().endsWith(".txt"))
					newArray.add(f);
			}
			break;
		case "train":
			for (File f : array) {
				if (f.getName().startsWith("train"))
					newArray.add(f);
			}
			break;
		case "test":
			for (File f : array) {
				if (f.getName().startsWith("test"))
					newArray.add(f);
			}
			break;
		case "model":
			for (File f : array) {
				if (!f.isDirectory())
					newArray.add(f);
			}
		}
		
		return newArray.toArray(new File[newArray.size()]);
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
