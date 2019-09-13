package com.entopix.maui.main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import com.entopix.maui.filters.MauiFilter;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stopwords.Stopwords;
import com.entopix.maui.stopwords.StopwordsPortuguese;
import com.entopix.maui.util.DataLoader;
import com.entopix.maui.util.Evaluator;
import com.entopix.maui.util.MauiDocument;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.util.Topic;
import com.entopix.maui.utils.Paths;
import com.entopix.maui.utils.StructuredTest;
import com.entopix.maui.utils.UI;

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
	
	static String guiLanguage = "pt";
	static MauiModelBuilder modelBuilder = new MauiModelBuilder();
	static MauiTopicExtractor topicExtractor = new MauiTopicExtractor();
	static MauiFilter filter;
	static Stopwords stopwords = new StopwordsPortuguese();
	static Stemmer stemmer = new PortugueseStemmer();
	
	static String dataPath = Paths.getDataPath();
	static String abstractsPath = dataPath + "\\docs\\corpusci\\abstracts";
	static String fullTextsPath = dataPath + "\\docs\\corpusci\\full_texts";
	static String trainDir = dataPath + "\\docs\\corpusci\\full_texts\\train30";
	static String testDir = dataPath + "\\docs\\corpusci\\full_texts\\test60";
	static String testFilePath = dataPath + "\\docs\\corpusci\\full_texts\\test60\\Artigo32.txt";
	
	static String modelsDir = dataPath + "\\models";
	static String modelName = "model_fulltexts_train30";
	static String modelPath = modelsDir + "\\" + modelName;
	static String modelType = "fulltexts"; //abstracts or fulltexts
	
	static String vocabPath = dataPath + "\\vocabulary\\TBCI-SKOS_pt.rdf";
	static String vocabFormat = "skos";
	
	static int numTopicsToExtract = 10;
	static String language = "pt";
	static String encoding = "UTF-8";
	
	/**
	 * @param command 
	 * @param args
	 * @throws Exception
	 */
	public static void runWithArguments(String command, String[] args) throws Exception {
		String dataPath = Paths.getDataPath();
		switch(command) {
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
			modelBuilder.serialize = true;
			if(modelBuilder.documentLanguage.equals("pt")) {
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
			if(topicExtractor.documentLanguage.equals("pt")) {
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
			if(documentLanguage.equals("pt")) { //forces class initialization manually (ignores -s and -t arguments)
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
	
	/**
	 * @throws Exception
	 */
	public static void runPTCi() throws Exception {
		if(!Paths.exists(modelPath))
			setupAndBuildModel();
		
		if(guiLanguage.equals("pt"))
			runOptionsMenuPT();
	}

	private static void runOptionsMenuPT() throws Exception {
		Scanner scan = new Scanner(System.in);
		String input;
		boolean exit = false;
		while (!exit) {
			System.out.println();
			System.out.println("1 - Construir modelo  ");
			System.out.println("2 - Testar extrator de tópicos em diretório  ");
			System.out.println("3 - Executar extrator de tópicos em arquivo  ");
			System.out.println("4 - Executar teste estruturado  ");
			System.out.println("0 - Sair  ");
			System.out.print("Opção: ");
			input = scan.nextLine();
			
			switch (input) {
			//TRAIN OPTION
			case "1":
				System.out.println();
				System.out.println("Escolha o tipo de modelo: ");
				System.out.println("1 - Resumos");
				System.out.println("2 - Textos Completos");
				System.out.print("Opção: ");
				input = scan.nextLine();
				switch(input) {
				case "1":
					modelType = "abstracts";
					break;
				case "2":
					modelType = "fulltexts";
					break;
				default:
					System.out.println("ERRO: Opção Inválida.");
					continue;
				}
				System.out.println();
				System.out.println("Escolha o diretório de treinamento:");
				System.out.println("Diretório de treinamento selecionado: " + trainDir);
				System.out.println("1 - Escolher da lista");
				System.out.println("2 - Diretório customizado");
				System.out.print("Opção: ");
				input = scan.nextLine();
				switch(input) {
				case "1":
					chooseFileFromList(scan,"train");
					break;
				case "2":
					System.out.println();
					System.out.println("Digite o caminho completo do diretório: ");
					System.out.println("Opção: ");
					input = scan.nextLine();
					if(Paths.exists(input))
						trainDir = input;
					else
						System.out.println("O diretório '" + input + "' não foi encontrado.");
					break;
				default:
					System.out.println("ERRO: Opção inválida.");
					continue;
				}
				
				System.out.println();
				System.out.println("Digite o nome do novo modelo ou aperte [enter] para usar o nome padrão:");
				modelName = "model_" + modelType + "_" + new File(trainDir).getName();
				System.out.println("Nome padrão: " + modelName);
				System.out.print("Nome do novo modelo: ");
				input = scan.nextLine();
				if(input.equals("")) {
					modelPath = modelsDir + "\\" + modelName;
					setupAndBuildModel();
				} else {
					modelName = input;
					modelPath = modelsDir + "\\" + modelName;
					setupAndBuildModel();
				}
				break;
				
			//TEST ON DIRECTORY OPTION
			case "2":
				System.out.println();
				System.out.println("Escolha o modelo a ser utilizado ou aperte [enter] para usar o atual: ");
				System.out.println("Modelo Atual: " + modelName);
				chooseFileFromList(scan, "model");
				
				System.out.println();
				System.out.println("Escolha o  diretório de teste ou aperte [enter] para usar o atual.");
				testDir = (modelType.equals("abstracts") ? abstractsPath + "\\test60" : fullTextsPath + "\\test60");
				System.out.println("Diretório de teste selecionado: " + testDir);
				System.out.println("1 - Escolher da lista");
				System.out.println("2 - Diretório customizado");
				System.out.print("Opção: ");
				input = scan.nextLine();
				switch (input) {
				case "1":
					if(modelType.equals("")) {
						if(modelPath.contains("abstracts"))
							modelType = "abstracts";
						else if(modelPath.contains("full_texts"))
							modelType = "fulltexts";
						else {
							System.out.println("ERRO: Caminho do modelo inválido.");
							continue;
						}
					}
					chooseFileFromList(scan, "test");
					setupAndRunTopicExtractor();
					break;
				case "2":
					System.out.println("Digite o caminho completo do diretório: ");
					input = scan.nextLine();
					if (Paths.exists(input)) {
						testDir = input;
						setupAndRunTopicExtractor();
					} else {
						System.out.println("ERRO: O diretório " + input + " não foi encontrado.");
						continue;
					}
					break;
				case "":
					setupAndRunTopicExtractor();
					break;
				}
				break;
				
			//RUN ON FILE OPTION
			case "3":
				System.out.println();
				System.out.println("Escolha o modelo a ser utilizado ou aperte [enter] para usar o atual: ");
				System.out.println("Modelo Atual: " + modelName);
				chooseFileFromList(scan, "model");
				
				System.out.println();
				System.out.println("Escolha o arquivo (.txt) ou aperte [enter] para usar o atual: ");
				testFilePath = (modelType.equals("abstracts") ? abstractsPath + "\\test60\\Artigo32.txt" : fullTextsPath + "\\test60\\Artigo32.txt");
				System.out.println("Arquivo selecionado: " + testFilePath);
				System.out.println("1 - Escolher da lista");
				System.out.println("2 - Arquivo customizado");
				System.out.print("Opção: ");
				input = scan.nextLine();
				switch (input) {
				case "1":
					chooseFileFromList(scan, "run");
					runMauiWrapperOnFile();
					break;
				case "2":
					System.out.println("Digite o caminho completo do arquivo de texto: ");
					input = scan.nextLine();
					if (Paths.exists(input)) {
						testFilePath = input;
						runMauiWrapperOnFile();
					} else {
						System.out.println("ERRO: O diretório " + input + " não foi encontrado.");
						continue;
					}
					break;
				case "":
					runMauiWrapperOnFile();
					break;
				}
				break;
				
			//STRUCTURED TEST OPTION
			case "4":
				StructuredTest.runAllTests();
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

	/**
	 * Procedure to choose a file from a list interactively.
	 * @param scan
	 */
	public static void chooseFileFromList(Scanner scan, String dirType) {
		String input;
		File dir = null;
		File[] fileList = null;
		String fileChoice;
		
		if(dirType.equals("model")) {
			dir = new File(modelsDir);
		} else {
			if(modelType.equals("abstracts")) {
				dir = new File(abstractsPath);
			} else if(modelType.equals("fulltexts")) {
				dir = new File(fullTextsPath);
			} else {
				System.out.println("ERRO: Tipo de Modelo Inválido.");
				return;
			}
		}
		
		
		fileList = dir.listFiles();
		
		if(dirType.equals("train") || dirType.equals("test") || dirType.equals("model"))
			fileList = filterFileList(fileList, dirType);
		
		System.out.println("Diretório atual: " + dir.getAbsolutePath());
		UI.displayDirContent(fileList);
		System.out.print("Opção: ");
		input = scan.nextLine();
		
		if(input.equals("") && dirType.equals("model")) //User selected standard model
			return;
		
		fileChoice = fileList[Integer.parseInt(input)-1].getAbsolutePath(); //WARNING: Files are displayed starting with 1, but array starts with 0
		
		switch(dirType) {
		case "train":
			trainDir = fileChoice;
			break;
		case "test":
			testDir = fileChoice;
			break;
		case "model":
			modelPath = fileChoice;
			modelName = new File(modelPath).getName();
			if(modelName.contains("asbtracts"))
				modelType = "abstracts";
			else if(modelName.contains("fulltexts"))
				modelType = "fulltexts";
			else
				//TODO Custom names don't work, standard modelType assumed
			break;
		case "run":
			File txtFileFolder = new File(fileChoice);
			fileList = txtFileFolder.listFiles();
			fileList = filterFileList(fileList, ".txt");
			
			System.out.println("Diretório atual: " + txtFileFolder.getAbsolutePath());
			UI.displayDirContent(fileList);
			System.out.print("Opção: ");
			input = scan.nextLine();
			
			testFilePath = fileList[Integer.parseInt(input)-1].getAbsolutePath(); //WARNING: Files are displayed starting with 1, but array starts with 0
			break;
		}
	}

	static File[] filterFileList(File[] array, String filterMethod) {
		ArrayList<File> newArray = new ArrayList<File>();
		
		switch(filterMethod) {
		case ".txt":
			for(File f : array) {
				if(f.getName().endsWith(".txt"))
					newArray.add(f);
			}
			break;
		case "train":
			for(File f : array) {
				if(f.getName().startsWith("train"))
					newArray.add(f);
			}
			break;
		case "test":
			for(File f : array) {
				if(f.getName().startsWith("test"))
					newArray.add(f);
			}
			break;
		case "model":
			for(File f : array) {
				if(!f.isDirectory())
					newArray.add(f);
			}
		}
		
		return newArray.toArray(new File[newArray.size()]);
	}
	
	static void setupAndBuildModel() throws Exception {
		modelBuilder.inputDirectoryName = trainDir;
		modelBuilder.modelName = modelPath;
		modelBuilder.vocabularyFormat = vocabFormat;
		modelBuilder.vocabularyName = vocabPath;
		modelBuilder.stemmer = stemmer;
		modelBuilder.stopwords = stopwords;
		modelBuilder.documentLanguage = language;
		modelBuilder.documentEncoding = encoding;
		modelBuilder.minNumOccur = 1;
		modelBuilder.maxPhraseLength = 5;
		modelBuilder.minPhraseLength = 1;
		modelBuilder.serialize = true;

		modelBuilder.setPositionsFeatures(false);
		modelBuilder.setKeyphrasenessFeature(false);
		modelBuilder.setThesaurusFeatures(false);

		filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(trainDir));
		System.out.println("Modelo '" + modelName + "' construído. Salvando modelo...");
		modelBuilder.saveModel(filter);
		System.out.println("Modelo salvo em " + modelPath);
	}
	
	private static void runMauiWrapperOnFile() throws IOException, MauiFilterException {
		File document = new File(testFilePath);
		String documentText = FileUtils.readFileToString(document, Charset.forName("UTF-8"));

		MauiWrapper mauiWrapper = null;

		mauiWrapper = new MauiWrapper(modelPath, vocabPath, vocabFormat, stopwords, stemmer, language);
		mauiWrapper.setModelParameters(vocabPath, stemmer, stopwords, language);

		ArrayList<Topic> keywords = mauiWrapper.extractTopicsFromText(documentText, numTopicsToExtract);
		for (Topic keyword : keywords) {
			System.out.println("Palavra-chave: " + keyword.getTitle() + " " + keyword.getProbability());
		}
		//TODO Not writing .maui file because keywords were stored in List<Topic> instead of List<MauiTopics>
	}

	private static void setupAndRunTopicExtractor() throws MauiFilterException {
		topicExtractor.inputDirectoryName = testDir;
		topicExtractor.modelName = modelPath;
		topicExtractor.vocabularyName = vocabPath;
		topicExtractor.vocabularyFormat = vocabFormat;
		topicExtractor.stemmer = stemmer;
		topicExtractor.stopwords = stopwords;
		topicExtractor.documentLanguage = language;
		topicExtractor.documentEncoding = encoding;
		topicExtractor.cutOffTopicProbability = 0.12;
		topicExtractor.serialize = true;
		topicExtractor.loadModel();
		List<MauiDocument> documents = DataLoader.loadTestDocuments(testDir);
		List<MauiTopics> topics = topicExtractor.extractTopics(documents);
		topicExtractor.printTopics(topics);
		Evaluator.evaluateTopics(topics);
	}
	
	public static void main(String[] args) throws Exception {
		if(args == null || args.length == 0) {
			UI.printPTCIMessage("pt");
			runPTCi();
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
