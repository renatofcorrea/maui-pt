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
	static String trainDir = dataPath + "\\docs\\corpusci\\full_texts\\train30";
	static String testDir = dataPath + "\\docs\\corpusci\\full_texts\\test60";
	static String testDocPath = dataPath + "\\docs\\corpusci\\full_texts\\test30\\Artigo32.txt";
	
	static String modelsDir = dataPath + "\\models";
	static String modelName = "stdmodel";
	static String modelPath = modelsDir + "\\" + modelName;
	
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
		if(guiLanguage.equals("pt")) {
			runOptionsMenuPT();
		} else {
			//TODO runOptionsMenuEN();
		}
	}

	private static void runOptionsMenuPT() throws Exception {
		Scanner scan = new Scanner(System.in);
		String option;
		boolean exit = false;
		while (!exit) {
			System.out.println();
			System.out.println("1 - Construir modelo  ");
			System.out.println("2 - Testar extrator de tópicos em diretório  ");
			System.out.println("3 - Executar extrator de tópicos em arquivo  ");
			System.out.println("4 - Executar teste estruturado  ");
			System.out.println("0 - Sair  ");
			System.out.print("Opção:  ");
			option = scan.nextLine();
			
			switch (option) {
			//TRAIN OPTION
			case "1":
				System.out.println("Digite o caminho do diretório de treinamento ou aperte [enter] para usar o atual.");
				System.out.println("Diretório de treinamento selecionado: " + trainDir);
				option = scan.nextLine();
				switch(option) {
				default:
					if (Paths.exists(option))
						trainDir = option;
					else
						System.out.println("O diretório " + option + " não foi encontrado.");
					break;
				case "":
					break;
				}
				System.out.println("Deseja definir um novo modelo ou sobrescrever o atual?");
				System.out.println("Modelo atual: '" + modelName + "'");
				System.out.println("Digite 1 para alterar o modelo ou aperte enter para continuar.");
				option = scan.nextLine();
				switch (option) {
				case "1":
					System.out.println("Digite o nome do novo modelo: ");
					modelName = scan.nextLine();
					modelPath = modelsDir + "\\" + modelName;
					setupAndBuildModel();
					break;
				case "":
					setupAndBuildModel();
					break;
				default:
					System.out.println("Opção inválida.");
					break;
				}
				break;
				
			//TEST ON DIRECTORY OPTION
			case "2":
				System.out.println("Digite o caminho do diretório de teste ou aperte enter para usar o atual.");
				System.out.println("Diretório de teste selecionado: " + testDir);
				option = scan.nextLine();
				switch (option) {
				default:
					if (Paths.exists(option)) {
						testDir = option;
						setupAndRunTopicExtractor();
					}
					else
						System.out.println("O diretório " + option + " não foi encontrado.");
					break;
				case "":
					setupAndRunTopicExtractor();
					break;
				}
				break;
				
			//RUN ON FILE OPTION
			case "3":
				System.out.println("Digite o caminho do arquivo (.txt) ou aperte enter para usar o atual: ");
				System.out.println("Arquivo selecionado: " + testDocPath);
				option = scan.nextLine();
				switch (option) {
				default:
					if (Paths.exists(option)) {
						testDocPath = option;
						runMauiWrapperOnFile();
					} else
						System.out.println("O diretório " + option + " não foi encontrado.");
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
		System.out.println("Pronto!");
	}
	
	private static void runMauiWrapperOnFile() throws IOException, MauiFilterException {
		File document = new File(testDocPath);
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
			printPTCIMessage("pt");
			runPTCi();
			return;
		}
		
		String command = args[0].toLowerCase(); 
		if ((!command.equals("train") && !command.equals("test") && !command.equals("run"))) {
			instructUser3(Utils.getOption('i', args));
			System.exit(-1);
		}
		
		String[] remainingArgs = new String[args.length - 1];
		System.arraycopy(args, 1, remainingArgs, 0, args.length-1);

		StandaloneMain.runWithArguments(command, remainingArgs);
	}

	private static void instructUser3(String language) {
		if(language.equals("en"))
			System.out.println("Maui Standalone Runner\njava -jar maui-standalone.jar [train|test|run] options...\nPlease specify train or test or run and then the appropriate parameters.   ");
		else //if(language.equals("pt"))
			System.out.println("Maui Standalone Runner\njava -jar maui-standalone.jar [train|test|run] opções...\nFavor especificar train ou test ou run e em seguida os parâmetros apropriados.   ");
	}

	private static void printPTCIMessage(String language) {
		if(language.equals("en")) {
			instructUser3(language);
			System.out.println("By default, MAUI is running example in pt language and CI documents.   ");
		} else //if(language.equals("pt")) 
		{
			instructUser3(language);
			System.out.println("Por padrão, MAUI está executando exemplo em português e documentos de CI.   ");
		}
	}
}
