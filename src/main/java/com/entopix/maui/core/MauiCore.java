package com.entopix.maui.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.entopix.maui.filters.MauiFilter;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.main.MauiModelBuilder;
import com.entopix.maui.main.MauiTopicExtractor;
import com.entopix.maui.main.MauiWrapper;
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
import com.entopix.maui.stopwords.StopwordsPortuguese;
import com.entopix.maui.util.DataLoader;
import com.entopix.maui.util.Evaluator;
import com.entopix.maui.util.MauiDocument;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.util.Topic;
import com.entopix.maui.utils.MauiFileUtils;
import com.entopix.maui.utils.UI;
import com.entopix.maui.vocab.Vocabulary;

import weka.core.Utils;

public class MauiCore {
	
	//Standard Paths
	private static final String VOCAB_PATH = MauiFileUtils.getVocabPath();
	
	//Standard Objects
	private static Stopwords stopwords = new StopwordsPortuguese();
	private static MauiModelBuilder modelBuilder = new MauiModelBuilder();
	
	//Stemmer Objects
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
	
	//Standard ModelBuilder configs
	private static int minOccur = 2;
	private static int maxPhraseLength = 5;
	private static int minPhraseLength = 1;
	private static boolean modelBuilderSerialize = true;
	
	//Standard TopicExtractor configs
	private static int numTopicsToExtract = 10;
	private static double cutOffTopicProbability = 0.12;
	private static boolean topicExtractorSerialize = true;
	
	//Standard Vocabulary configs
	private static String vocabFormat = "skos";
	private static boolean vocabSerialize = true;
	private static boolean vocabReorder = false;
	
	//Standard General configs
	private static String encoding = "UTF-8";
	private static String language = "pt";
	
	
	public static Stemmer[] getStemmerList() {
		return stemmerList;
	}
	
	public static void setMinOccur(int min) {
		minOccur = min;
	}
	
	public static double getCutOffTopicProbability() {
		return cutOffTopicProbability;
	}
	
	public static Vocabulary setupVocab(String vocabPath, Stemmer stemmer, Stopwords stopwords) {
		Vocabulary vocab = new Vocabulary();
		vocab.setReorder(vocabReorder);
		vocab.setSerialize(vocabSerialize);
		vocab.setEncoding(encoding);
		vocab.setLanguage(language);
		vocab.setStemmer(stemmer);
		vocab.setStopwords(stopwords);
		vocab.setVocabularyName(vocabPath);
		vocab.initializeVocabulary(vocabPath, vocabFormat);
		return vocab;
	}
	
	public static MauiFilter setupAndBuildModel(String trainDir, String modelPath, Stemmer stemmer) throws Exception {
		return setupAndBuildModel(trainDir, modelPath, vocabFormat, VOCAB_PATH, stemmer, stopwords, language);
	}
	
	public static MauiFilter setupAndBuildModel(String trainDir, String modelPath, String vocabFormat, String vocabPath, Stemmer stemmer, Stopwords stopwords, String language) throws Exception {
		modelBuilder.inputDirectoryName = trainDir;
		modelBuilder.modelName = modelPath;
		modelBuilder.vocabularyFormat = vocabFormat;
		modelBuilder.vocabularyName = vocabPath;
		modelBuilder.stemmer = stemmer;
		modelBuilder.stopwords = stopwords;
		modelBuilder.documentLanguage = language;
		modelBuilder.documentEncoding = encoding;
		modelBuilder.minNumOccur = minOccur;
		modelBuilder.maxPhraseLength = maxPhraseLength;
		modelBuilder.minPhraseLength = minPhraseLength;
		modelBuilder.serialize = modelBuilderSerialize;

		Vocabulary vocab = setupVocab(vocabPath, stemmer, stopwords);
		modelBuilder.setVocabulary(vocab);
		modelBuilder.setBasicFeatures(true);
		modelBuilder.setFrequencyFeatures(false); //std: true
		modelBuilder.setLengthFeature(true);
		modelBuilder.setPositionsFeatures(false);
		modelBuilder.setKeyphrasenessFeature(false);
		modelBuilder.setThesaurusFeatures(false);
		modelBuilder.setWikipediaFeatures(false);
		
		MauiFilter filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(trainDir));
		modelBuilder.saveModel(filter);
		
		UI.showModelBuilt(new File(modelPath).getName());
		
		return filter;
	}
	
	public static List<MauiTopics> setupAndRunTopicExtractor(String modelPath, String runDir, Stemmer stemmer, boolean printTopics) throws MauiFilterException {
		return setupAndRunTopicExtractor(runDir, modelPath, VOCAB_PATH, vocabFormat, stemmer, stopwords, language, encoding, cutOffTopicProbability, topicExtractorSerialize, printTopics);
	}
	
	public static List<MauiTopics> setupAndRunTopicExtractor(String runDir, String modelPath, String vocabPath, String vocabFormat, Stemmer stemmer, Stopwords stopwords, String language, String encoding, double cutOffTopicProbability, boolean serialize, boolean printTopics) throws MauiFilterException {
		MauiTopicExtractor topicExtractor = new MauiTopicExtractor();
		topicExtractor.inputDirectoryName = runDir;
		topicExtractor.modelName = modelPath;
		topicExtractor.vocabularyName = vocabPath;
		topicExtractor.vocabularyFormat = vocabFormat;
		topicExtractor.stemmer = stemmer;
		topicExtractor.stopwords = stopwords;
		topicExtractor.documentLanguage = language;
		topicExtractor.documentEncoding = encoding;
		topicExtractor.cutOffTopicProbability = cutOffTopicProbability;
		topicExtractor.serialize = serialize;
		
		Vocabulary vocab = setupVocab(vocabPath, stemmer, stopwords);
		topicExtractor.setVocabulary(vocab);
		topicExtractor.loadModel();
		
		List<MauiTopics> topics = topicExtractor.extractTopics(DataLoader.loadTestDocuments(runDir));
		if (printTopics) {
			topicExtractor.printTopics(topics);
			Evaluator.evaluateTopics(topics);
		}
		
		return topics;
	}
	
	public static List<Topic> runMauiWrapperOnFile(File document, String modelPath, Stemmer stemmer) throws IOException, MauiFilterException {
		return runMauiWrapperOnFile(modelPath, document, encoding, stemmer, stopwords, VOCAB_PATH, language, numTopicsToExtract);
	}
	
	public static List<Topic> runMauiWrapperOnFile(String modelPath, File document, String encoding, Stemmer stemmer, Stopwords stopwords, String vocabPath, String language, int numTopicsToExtract) throws IOException, MauiFilterException {
		MauiWrapper mauiWrapper = null;
		Vocabulary vocab = setupVocab(vocabPath, stemmer, stopwords);
		String documentText = FileUtils.readFileToString(document, Charset.forName("UTF-8"));
		
		mauiWrapper = new MauiWrapper(vocab, DataLoader.loadModel(modelPath));
		mauiWrapper.setModelParameters(vocabPath, stemmer, stopwords, language);

		ArrayList<Topic> keywords = mauiWrapper.extractTopicsFromText(documentText, numTopicsToExtract);
		for  (Topic keyword : keywords) {
			System.out.println("Palavra-chave: " + keyword.getTitle() + " " + keyword.getProbability());
		}
		//Not writing .maui file because keywords are stored in List<Topic> instead of List<MauiTopics>
		return keywords;
	}
	
	/**
	 * @return The test results in a array in the format: [avgKey, stdDevKey, avgPrecision (%), stdDevPrecision (%), avgRecall (%), stdDevRecall (%), fMeasure]
	 */
	public static double[] classicEvaluateTopics(List<MauiTopics> allDocumentsTopics) {
		double[] results = null;
		double[] correctStatistics = new double[allDocumentsTopics.size()];
		double[] precisionStatistics = new double[allDocumentsTopics.size()];
		double[] recallStatistics = new double[allDocumentsTopics.size()];
		int i = 0;
		for (MauiTopics documentTopics : allDocumentsTopics) {
			double numExtracted = documentTopics.getTopics().size(), numCorrect = 0;
	
			for (Topic topic : documentTopics.getTopics()) { //Counts the amount of correct keyphrases found by the model
				if (topic.isCorrect()) {
					numCorrect += 1.0;
				}
			}
	
			if (numExtracted > 0 && documentTopics.getPossibleCorrect() > 0) {
				//log.debug("-- " + numCorrect + " correct");
				correctStatistics[i] = numCorrect;
				precisionStatistics[i] = numCorrect / numExtracted;				
				recallStatistics[i] = numCorrect / documentTopics.getPossibleCorrect();
	
			} else {
				correctStatistics[i] = 0.0;
				precisionStatistics[i] = 0.0;	
				recallStatistics[i] = 0.0;
			}
			i++;
		}
	
		if (correctStatistics.length != 0) {
			//Average number of correct keyphrases per document
			double avg = Utils.mean(correctStatistics);
			double stdDev = Math.sqrt(Utils.variance(correctStatistics));
	
			//Average precision (%)
			double avgPrecision = Utils.mean(precisionStatistics) * 100;
			double stdDevPrecision = Math.sqrt(Utils.variance(precisionStatistics)) * 100;
	
			//Average recall (%)
			double avgRecall = Utils.mean(recallStatistics) * 100;
			double stdDevRecall = Math.sqrt(Utils.variance(recallStatistics)) * 100;
	
			//F-Measure
			double fMeasure = 0.0;
			if (avgPrecision > 0 && avgRecall > 0) {
				fMeasure = 2 * avgRecall * avgPrecision / (avgRecall + avgPrecision);
			}
			results = new double[] {avg, stdDev, avgPrecision, stdDevPrecision, avgRecall, stdDevRecall, fMeasure};
		}
		return results;
	}
	
	public static void newEvaluateTopics(String docsPath, List<MauiTopics> extractedTopicsList) throws MauiFilterException {
		//loads manual topics
		List<MauiDocument> docs = DataLoader.loadTestDocuments(docsPath);
		int docIndex = 0;
		MauiDocument document = docs.get(docIndex);
		List<String> manualTopics = Arrays.asList(document.getTopicsString().split("\n"));
		
		//converts maui topics to string list
		List<String> extractedTopics = new ArrayList<String>();
		for (Topic t : extractedTopicsList.get(docIndex).getTopics()) {
			extractedTopics.add(t.getTitle());
		}
		
		int correct = 0;
		
		for (String topic : extractedTopics) {
			if (manualTopics.contains(topic)) {
				correct++;
			}
		}
		
		System.out.println("Correct topics count: " + correct);
	}
}
