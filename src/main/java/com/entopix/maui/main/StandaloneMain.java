package com.entopix.maui.main;

import java.io.File;
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
	
	/*
	public static void runMaui(String[] options) throws Exception {
		
		String inputString = options[0];
		// checking if it's a file or an input string
		File testFile = new File(inputString);
		if (testFile.exists()) {
			inputString = FileUtils.readFileToString(testFile);
		}
		
		String modelName = Utils.getOption('m', options);
		if (modelName.length() == 0) {
			throw new Exception("Name of model required argument.");
		}

		String vocabularyName = Utils.getOption('v', options);
		if (vocabularyName.length() == 0) {
			throw new Exception("Use \"none\" or supply the name of vocabulary .");
		}

		
		String vocabularyFormat = Utils.getOption('f', options);
		if (vocabularyFormat.length() > 0 && 
				(!vocabularyFormat.equals("skos") && (!vocabularyFormat.equals("text")))) {
				throw new Exception(
						"Vocabulary format should be either \"skos\" or \"text\".");
		}
		
		
		int topicsPerDocument = 10;
		String numPhrases = Utils.getOption('n', options);
		if (numPhrases.length() > 0) {
			topicsPerDocument = Integer.parseInt(numPhrases);
		}
		
		//stay
		MauiWrapper mauiWrapper = null;
		try {
			// Use default stemmer, stopwords and language
			// MauiWrapper also can be initalized with a pre-loaded vocabulary
			// and a pre-loaded MauiFilter (model) objects
			mauiWrapper = new MauiWrapper(modelName, vocabularyName, "skos");
			
			// the last three items should match what was used in the wrapper constructor
			// i.e. null if the defaults were used
			mauiWrapper.setModelParameters(vocabularyName, new PorterStemmer(), null, null); 
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

        try {
        	ArrayList<Topic> keywords = mauiWrapper.extractTopicsFromText(inputString, topicsPerDocument);
            for (Topic keyword : keywords) {
                System.out.println("Keyword: " + keyword.getTitle() + " " + keyword.getProbability());
            }
        } catch (MauiFilterException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	*/
	@SuppressWarnings("unused")
	public static void setOptions(String command, String[] args) throws Exception {
		switch(command) {
		case "train":
			MauiModelBuilder modelBuilder = new MauiModelBuilder();
			modelBuilder.inputDirectoryName = Utils.getOption('l', args);
			modelBuilder.modelName = Utils.getOption('m', args);
			modelBuilder.vocabularyName = Utils.getOption('v', args);
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
			
			modelBuilder.setBasicFeatures(true);
			modelBuilder.setLengthFeature(true);
			modelBuilder.setFrequencyFeatures(true);
			modelBuilder.setPositionsFeatures(false);
			modelBuilder.setKeyphrasenessFeature(false);
			modelBuilder.setThesaurusFeatures(false);
			modelBuilder.setWikipediaFeatures(false);
			
			MauiFilter filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
			modelBuilder.saveModel(filter);
			break;
		case "test":
			MauiTopicExtractor topicExtractor = new MauiTopicExtractor();
			topicExtractor.inputDirectoryName = Utils.getOption('l', args);
			topicExtractor.modelName = Utils.getOption('m', args);
			topicExtractor.vocabularyName = Utils.getOption('v', args);
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
			String rootPath = new File(".").getCanonicalPath();
			
			String documentPath = Utils.getOption('l', args).replace("\"", "\\");
			String fullDocumentPath = rootPath + "\\" + documentPath;
			File document = new File(fullDocumentPath);
			String documentText = FileUtils.readFileToString(document, Charset.forName("UTF-8"));
			
			String modelPath = Utils.getOption('m', args).replace("\"", "\\");
			String fullModelPath = rootPath + "\\" + modelPath;
			
			String vocabularyPath = Utils.getOption('v', args).replace("\"", "\\");
			String fullVocabPath = rootPath + "\\" + vocabularyPath;
			
			String vocabularyFormat = Utils.getOption('f', args);
			int topicsPerDocument = 10;
			String documentLanguage = Utils.getOption('i', args);
			Stopwords stopwords = null;
			Stemmer stemmer = null;
			if(documentLanguage.equals("pt")) {
				stopwords = new StopwordsPortuguese();
				stemmer = new PortugueseStemmer();
			} else {
				stopwords = (Stopwords) Class.forName(Utils.getOption('s', args)).newInstance();
				stemmer = (Stemmer) Class.forName(Utils.getOption('t', args)).newInstance();
			}
			
			MauiWrapper mauiWrapper = null;
			mauiWrapper = new MauiWrapper(fullModelPath, fullVocabPath, vocabularyFormat, stopwords,stemmer, documentLanguage);
			mauiWrapper.setModelParameters(fullVocabPath, stemmer, stopwords, documentLanguage); 

	        ArrayList<Topic> keywords = mauiWrapper.extractTopicsFromText(documentText, topicsPerDocument);
	        for (Topic keyword : keywords) {
	        	System.out.println("Keyword: " + keyword.getTitle() + " " + keyword.getProbability());
	        }
			break;
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		
		//instruct user if empty parameters
		if(args == null || args.length == 0){
			System.out.printf("Maui Standalone Runner\njava -jar maui-standalone.jar [train|test|run] options...\nPlease specify train or test or run and then the appropriate parameters.\n");

			System.out.println("By default, MAUI is running example in pt language and CI documents.");
			runPTCi();
			return;
			//System.exit(-1);
		}
		
		String command = args[0].toLowerCase(); 
		
		//instruct user if wrong the first parameter value
		if ((!command.equals("train") && !command.equals("test") && !command.equals("run"))) {
			System.out.printf("Maui Standalone Runner\njava -jar maui-standalone.jar [train|test|run] options...\nPlease specify train or test or run and then the appropriate parameters.\n");
			System.exit(-1);
		}

		String[] remainingArgs = new String[args.length - 1];

		System.arraycopy(args, 1, remainingArgs, 0, args.length-1);

		StandaloneMain.setOptions(command,remainingArgs);
	}
	
	/**
	 * @throws Exception
	 */
	//@Test
	public static void runPTCi() throws Exception {
		//PATHS
			// location of the data from C:\Users\Renato Correa\git\maui
			//String path = "C:/Users/Renato Correa/git/maui/";
			String outpath = "C:/Users/PC1/git/maui-standalone/data/models/";
			String path = "C:/Users/PC1/git/maui-standalone/data/";
			//String trainDir = path + "testdocs/pt/train10a";//resumo
			String trainDir = path + "docs/train10a";//full text sisa upper
			//String testDir = path + "testdocs/pt/test30";
			//String testDir = path + "testdocs/pt/test60";
			//String testDir = path + "testdocs/pt/train30";
			String testDir = path + "docs/test60";//full
			//10  full maui p40 r20 f27
			//10a full maui p22 r27 f25
			//10a full sisa p17 r13 f15
			
			// name of the file for storing the model
			String modelName = outpath + "pt_model10";//path + "testdocs/pt/pt_model10";
			//trn10a 60full p17 r13 f15 (sisa bandin files)
			//trn10a 60res p25 r22 f23
			
			// vocabulary to use for term assignment
			//String vocabulary = outpath+"tesauro-brasileiro-de-ciencia-da-informacao.rdf"; //(p21 r23 f22)
			String vocabulary = path+"vocabulary/TBCI-SKOS_pt.rdf";//path + "VOCABULARIES/TBCI-SKOS_pt.rdf";//(p31 r23 f26)
			String format = "skos";
		
		//PRE-INIT
			// how many topics per document to extract
			int numTopicsToExtract = 10;
			
			// language specific settings
			Stemmer stemmer = new PortugueseStemmer();
			Stopwords stopwords = new StopwordsPortuguese();
			String language = "pt";
			String encoding = "UTF-8";
			
			// maui objects
			MauiModelBuilder modelBuilder = new MauiModelBuilder();
			MauiTopicExtractor topicExtractor = new MauiTopicExtractor();
				
		//TEST
			boolean exit = false;
			while(!exit) {
				Scanner sc = new Scanner(System.in);
				int option;
				System.out.println("0 - Exit");
				System.out.println("1 - Load model");
				System.out.println("2 - Run topic extractor on directory");
				System.out.println("3 - Run topic extractor on file");
				System.out.println("4 - Radicalizer test");
				System.out.print("Option: ");
				option = sc.nextInt();
				
				//LOAD MODEL
				if(option == 1) {
					// Settings for the model builder
					modelBuilder.inputDirectoryName = trainDir;
					modelBuilder.modelName = modelName;
					modelBuilder.vocabularyFormat = format;
					modelBuilder.vocabularyName = vocabulary;
					modelBuilder.stemmer = stemmer;
					modelBuilder.stopwords = stopwords;
					modelBuilder.documentLanguage = language;
					modelBuilder.documentEncoding = encoding;
					modelBuilder.minNumOccur=1;//full 2 piora
					modelBuilder.maxPhraseLength=5;//full 5
					modelBuilder.minPhraseLength=1;
					modelBuilder.serialize = true;
					
					// Which features to use?
					modelBuilder.setBasicFeatures(true);//tfidf and first occurrence position
					modelBuilder.setLengthFeature(true);//length false (r19 p24) true (r23 p31)
					modelBuilder.setFrequencyFeatures(true);//tf and idf false (r22 p31) true (r23 p31)
					modelBuilder.setPositionsFeatures(false);//last occurrence position and spread of occurrence false (r23 p31) true (r23 p31)
					modelBuilder.setKeyphrasenessFeature(false);//domain keyphraseness false (r23 p31) true (r22 p29)
					modelBuilder.setThesaurusFeatures(false);//node degree and generality (always 0) false (r23 p31) true (r23 p31)
					modelBuilder.setWikipediaFeatures(false);//wiki-keyphraseness, wiki_inlinks, wiki_generality
					
					// Run model builder
					//Comentando aqui para não construir modelo mas somente carregar!
					MauiFilter filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(trainDir));
					modelBuilder.saveModel(filter);
				}
				
				//RUN TOPIC EXTRACTOR on directory
				else if(option == 2) {
					// Settings for the topic extractor
					topicExtractor.inputDirectoryName = testDir;
					topicExtractor.modelName = modelName;
					topicExtractor.vocabularyName = vocabulary;
					topicExtractor.vocabularyFormat = format;
					topicExtractor.stemmer = stemmer;
					topicExtractor.stopwords = stopwords;
					topicExtractor.documentLanguage = language;
					topicExtractor.documentEncoding = encoding;
					//topicExtractor.topicsPerDocument = numTopicsToExtract; 
					topicExtractor.cutOffTopicProbability = 0.12; //0.15//0.12;//0.10//0.05//0.04-0.0
					topicExtractor.serialize = true;
					
					// Run topic extractor
					topicExtractor.loadModel();
						// Extracting Keyphrases from all files in the input directory
						List<MauiDocument> documents = DataLoader.loadTestDocuments(testDir);
						List<MauiTopics> topics = topicExtractor.extractTopics(documents);
						topicExtractor.printTopics(topics);
						Evaluator.evaluateTopics(topics);
					//Run topic extractor on file
					}
				// run topic extractor on file
				else if(option == 3) {
					//testando run, está funcionando chamando o construtor completo de MauiWrapper
					//aparentemente problema era carregar vocabulário com parâmetros default
					//na hora de obter saída do modelo em extractTopicsFromText
					String inputString = "C:/Users/PC1/git/maui-standalone/data/docs/test30/Artigo32.txt";//options[0];
					// checking if it's a file or an input string
					File testFile = new File(inputString);
					if (testFile.exists()) {
						inputString = FileUtils.readFileToString(testFile);
					}
					MauiWrapper mauiWrapper = null;
					try {
						// dont use default stemmer, stopwords and language
						// MauiWrapper also can be initalized with a pre-loaded vocabulary
						// and a pre-loaded MauiFilter (model) objects
						mauiWrapper = new MauiWrapper(modelName, vocabulary, "skos", stopwords,stemmer,language);
						
						// the last three items should match what was used in the wrapper constructor
						// i.e. null if the defaults were used
						mauiWrapper.setModelParameters(vocabulary,stemmer, stopwords, language); 
						
					} catch (Exception e) {
						e.printStackTrace();
						sc.close();
						throw new RuntimeException();
					}
	
			        try {
			        	ArrayList<Topic> keywords = mauiWrapper.extractTopicsFromText(inputString, numTopicsToExtract);
			            for (Topic keyword : keywords) {
			                System.out.println("Keyword: " + keyword.getTitle() + " " + keyword.getProbability());
			            }
			        } catch (MauiFilterException e) {
						e.printStackTrace();
						sc.close();
						throw new RuntimeException();
					}
				}
				else if(option == 4) {
					//testa radicalizador
					System.out.println(stemmer.stemString("ciência da informação"));
					System.out.println(stemmer.stemString("cientista da informação"));
					System.out.println(stemmer.stemString("pesquisa pesquisadores"));
					System.out.println(stemmer.stemString("bibliotecários bibliotecas"));
					System.out.println(stemmer.stemString("gestores  gestão"));
					System.out.println(stemmer.stemString("hipertextos hipertexto"));
					System.out.println(stemmer.stemString("público publicações"));
					System.out.println(stemmer.stemString("publicação publicações"));
					System.out.println(stemmer.stemString("realizada realia"));
					System.out.println(stemmer.stemString("realizada realismo"));
					System.out.println(stemmer.stemString("soc socialmente"));
					System.out.println(stemmer.stemString("soc sociais"));
					System.out.println(stemmer.stemString("informação informacional"));
				}
				else if(option == 0) {
					exit = true;
					sc.close();
				}
				else {
					System.out.println("Invalid option");
				}
			}
	}

}
