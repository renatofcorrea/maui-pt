package com.entopix.maui.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import weka.core.Utils;

import com.entopix.maui.filters.MauiFilter;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.main.MauiModelBuilder;
import com.entopix.maui.main.MauiTopicExtractor;
import com.entopix.maui.main.MauiWrapper;
import com.entopix.maui.stemmers.PorterStemmer;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stopwords.Stopwords;
import com.entopix.maui.stopwords.StopwordsPortuguese;
import com.entopix.maui.util.DataLoader;
import com.entopix.maui.util.Evaluator;
import com.entopix.maui.util.MauiDocument;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.util.Topic;

/**
 * StandaloneMain - allows us to run the train or test options in the single standalone,
 * as well as run Maui on a single file or a single text string.
 *
 *
 * Passes on all but the first option.
 *
 * @author Richard Vowles - https://google.com/+RichardVowles, Alyona Medelyan - medelyan@gmail.com
 * @author Renato Correa
 * 
 */
public class StandaloneMain {
	
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
	
	public static void main(String[] args) throws Exception {
		
		//instruct user if empty parameters
		if(args == null || args.length == 0){
			System.out.printf("Maui Standalone Runner\njava -jar maui-standalone.jar [train|test|run] options...\nPlease specify train or test or run and then the appropriate parameters.\n");

			System.out.println("By default runing maui example in pt language and CI documents.");
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

		if (command.equals("train")) {
			MauiModelBuilder.main(remainingArgs);
		} else if (command.equals("test")) {
			MauiTopicExtractor.main(remainingArgs);
		} else {
			runMaui(remainingArgs);
		}
	}
	
	/**
	 * @throws Exception
	 */
	//@Test
	public static void runPTCi() throws Exception {
		
		// location of the data from C:\Users\Renato Correa\git\maui
		//String path = "C:/Users/Renato Correa/git/maui/";
		String outpath = "C:/Users/Renato Correa/git/maui-standalone/data/models/";
		String path = "C:/Users/Renato Correa/git/maui-standalone/data/";
		//String trainDir = path + "testdocs/pt/train10ares";//resumo
		String trainDir = path + "docs/pt/train10afullmaui";//full text sisa upper
		//String testDir = path + "docs/pt/test30res";
		//String testDir = path + "docs/pt/test60res";
		//String testDir = path + "docs/pt/train30res";
		String testDir = path + "docs/pt/test60fullmaui";//full
		//10  full maui p40 r20 f27
		//10a full maui p22 r27 f25
		//10a full sisa p17 r13 f15
		
		// name of the file for storing the model
		String modelName = outpath+"pt_model10afull";
		//String modelName = outpath + "pt_model10ares";
		//trn10a 60full p17 r13 f15 (sisa bandin files)
		//trn10a 60res p25 r22 f23
		
		// language specific settings
		Stemmer stemmer = new PortugueseStemmer();
		Stopwords stopwords = new StopwordsPortuguese();
		String language = "pt";
		String encoding = "UTF-8";
		
		// vocabulary to use for term assignment
		//String vocabulary = outpath+"tesauro-brasileiro-de-ciencia-da-informacao.rdf"; //(p21 r23 f22)
		String vocabulary = path+"vocabularies/TBCI-SKOS_pt.rdf";//path + "VOCABULARIES/TBCI-SKOS_pt.rdf";//(p31 r23 f26)
		String format = "skos";
		
		// how many topics per document to extract
		int numTopicsToExtract = 10;
		
		// maui objects
		MauiModelBuilder modelBuilder = new MauiModelBuilder();
		MauiTopicExtractor topicExtractor = new MauiTopicExtractor();
		
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
		
		
		//testando run, está funcionando chamando o construtor completo de MauiWrapper
		//aparentemente problema era carregar vocabulário com parâmetros default
		//na hora de obter saída do modelo em extractTopicsFromText
		String inputString = "Ciência da informação transferência da informação gestão pesquisa.";//options[0];
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
			throw new RuntimeException();
		}

        try {
        	ArrayList<Topic> keywords = mauiWrapper.extractTopicsFromText(inputString, numTopicsToExtract);
            for (Topic keyword : keywords) {
                System.out.println("Keyword: " + keyword.getTitle() + " " + keyword.getProbability());
            }
        } catch (MauiFilterException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

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

}
