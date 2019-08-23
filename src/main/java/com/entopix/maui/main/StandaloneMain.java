package com.entopix.maui.main;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import com.entopix.maui.filters.MauiFilter;
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
	
	/**
	 * 
	 * @param command 
	 * @param args
	 * @throws Exception
	 */
	public static void setOptions(String command, String[] args) throws Exception {
		String rootPath = new File(".").getCanonicalPath();
		String dataPath = rootPath + "\\"; // add "data" folder if maui-pt is the root directory
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
		//Pre-Init
		String rootPath = Paths.getRootPath();
		String dataPath = rootPath + "\\data"; //Add data if maui-pt is the root directory
		String modelOutputPath = dataPath + "\\models";
		String trainDir = dataPath + "\\docs\\train10a";
		String testDir = dataPath + "\\docs\\test60";
		String testDocPath = dataPath + "/docs/test30/Artigo32.txt";
		
		String modelPath = modelOutputPath + "\\pt_model10";
		String vocabPath = dataPath + "\\vocabulary\\TBCI-SKOS_pt.rdf";
		String vocabFormat = "skos";
		
		int numTopicsToExtract = 10;
			
		Stemmer stemmer = new PortugueseStemmer();
		Stopwords stopwords = new StopwordsPortuguese();
		String language = "pt";
		String encoding = "UTF-8";
			
		MauiModelBuilder modelBuilder = new MauiModelBuilder();
		MauiTopicExtractor topicExtractor = new MauiTopicExtractor();
				
		//Run
		boolean exit = false;
		while(!exit) {
			Scanner sc = new Scanner(System.in);
			String option;
			System.out.println("1 - Train (Build model)");
			System.out.println("2 - Test topic extractor on directory");
			System.out.println("3 - Run topic extractor on file");
			System.out.println("4 - Execute structured test");
			System.out.println("0 - Exit");
			System.out.print("Option: ");
			option = sc.nextLine();

			switch(option) {
			//Train (Build Model)
			case "1":
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

				MauiFilter filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(trainDir));
				System.out.println("Model built. Saving the model...");
				modelBuilder.saveModel(filter);
				System.out.println("Done!");
				break;
			//Test topic extractor on directory
			case "2":
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
				break;
			//Run topic extractor on file
			case "3":
				File document = new File(testDocPath);
				String documentText = FileUtils.readFileToString(document, Charset.forName("UTF-8"));
				
				MauiWrapper mauiWrapper = null;
			
				mauiWrapper = new MauiWrapper(modelPath, vocabPath, vocabFormat, stopwords,stemmer, language);
				mauiWrapper.setModelParameters(vocabPath, stemmer, stopwords, language); 

		        ArrayList<Topic> keywords = mauiWrapper.extractTopicsFromText(documentText, numTopicsToExtract);
		        for (Topic keyword : keywords) {
		        	System.out.println("Keyword: " + keyword.getTitle() + " " + keyword.getProbability());
		        }
				break;
			case "4":
				StructuredTest.runAllTests();
				break;
			case "0":
				exit = true;
				sc.close();
				break;
			default:
				System.out.println("Invalid option");
				break;
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		if(args == null || args.length == 0) {
			System.out.printf("Maui Standalone Runner\njava -jar maui-standalone.jar [train|test|run] options...\nPlease specify train or test or run and then the appropriate parameters.\n");
			System.out.println("By default, MAUI is running example in pt language and CI documents.");
			runPTCi();
			return;
		}
		
		String command = args[0].toLowerCase(); 
		if ((!command.equals("train") && !command.equals("test") && !command.equals("run"))) {
			System.out.printf("Maui Standalone Runner\njava -jar maui-standalone.jar [train|test|run] options...\nPlease specify train or test or run and then the appropriate parameters.\n");
			System.exit(-1);
		}
		
		String[] remainingArgs = new String[args.length - 1];
		System.arraycopy(args, 1, remainingArgs, 0, args.length-1);

		StandaloneMain.setOptions(command, remainingArgs);
	}

}
