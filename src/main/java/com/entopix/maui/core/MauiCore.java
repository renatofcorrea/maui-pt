package com.entopix.maui.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.util.Topic;
import com.entopix.maui.utils.MauiFileUtils;
import com.entopix.maui.vocab.Vocabulary;

import weka.core.Utils;

public class MauiCore {
	
	//Standard Paths
	public static String modelsPath = MauiFileUtils.getModelsDirPath();
	public static String vocabPath = MauiFileUtils.getVocabPath();
	
	//Standard Objects
	public static Stopwords stopwords = new StopwordsPortuguese();
	
	//Additional Objects
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
	
	//Standard TopicExtractor configs
	public static int numTopicsToExtract = 10;
	public static double cutOffTopicProbability = 0.12;
	public static boolean topicExtractorSerialize = true;
	
	//Standard Vocabulary configs
	public static String vocabFormat = "skos";
	public static boolean vocabSerialize = true;
	public static boolean vocabReorder = false;
	
	//Standard General configs
	public static String encoding = "UTF-8";
	public static String language = "pt";
	
	public static Stemmer[] getStemmerList() {
		return stemmerList;
	}
	
	public static Vocabulary setupVocab(String vocabPath, Stemmer stemmer, Stopwords stopwords) {
		return setupVocab(vocabPath, vocabFormat, encoding, language, stemmer, stopwords, false, true);
	}
	
	public static Vocabulary setupVocab(String vocabPath, String vocabFormat, String encoding, String language, Stemmer stemmer, Stopwords stopwords, boolean reorder, boolean serialize) {
		Vocabulary vocab = new Vocabulary();
		vocab.setReorder(reorder);
		vocab.setSerialize(serialize);
		vocab.setEncoding(encoding);
		vocab.setLanguage(language);
		vocab.setStemmer(stemmer);
		vocab.setStopwords(stopwords);
		vocab.setVocabularyName(vocabPath);
		vocab.initializeVocabulary(vocabPath, vocabFormat);
		return vocab;
	}
	
	public static void setupAndBuildModel(String modelPath, String trainDir, Stemmer stemmer) throws Exception {
		setupAndBuildModel(trainDir, modelPath, vocabFormat, vocabPath, stemmer, stopwords, language, encoding);
	}
	
	public static void setupAndBuildModel(String trainDir, String modelPath, String vocabFormat, String vocabPath, Stemmer stemmer, Stopwords stopwords, String language, String encoding) throws Exception {
		MauiModelBuilder modelBuilder = new MauiModelBuilder();
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

		Vocabulary vocab = setupVocab(vocabPath, stemmer, stopwords);
		modelBuilder.setVocabulary(vocab);
		modelBuilder.setPositionsFeatures(false);
		modelBuilder.setKeyphrasenessFeature(false);
		modelBuilder.setThesaurusFeatures(false);
		
		MauiFilter filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(trainDir));
		modelBuilder.saveModel(filter);
	}
	
	public static List<MauiTopics> setupAndRunTopicExtractor(String modelPath, String runDir, Stemmer stemmer, boolean printTopics) throws MauiFilterException {
		return setupAndRunTopicExtractor(runDir, modelPath, vocabPath, vocabFormat, stemmer, stopwords, language, encoding, cutOffTopicProbability, topicExtractorSerialize, printTopics);
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
		topicExtractor.cutOffTopicProbability = 0.12;
		topicExtractor.serialize = true;
		
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
	
	public static void runMauiWrapperOnFile(File document, String modelPath, Stemmer stemmer) throws IOException, MauiFilterException {
		String documentText = FileUtils.readFileToString(document, Charset.forName("UTF-8"));
		Vocabulary vocab = setupVocab(vocabPath, stemmer, stopwords);
		
		MauiWrapper mauiWrapper = null;
		mauiWrapper = new MauiWrapper(vocab, DataLoader.loadModel(modelPath));
		mauiWrapper.setModelParameters(vocabPath, stemmer, stopwords, language);

		ArrayList<Topic> keywords = mauiWrapper.extractTopicsFromText(documentText, numTopicsToExtract);
		for  (Topic keyword : keywords) {
			System.out.println("Palavra-chave: " + keyword.getTitle() + " " + keyword.getProbability());
		}
		//Not writing .maui file because keywords are stored in List<Topic> instead of List<MauiTopics>
	}
	
	/**
	 * @return The test results in a array of size 7 in the format: [avgKey, stdDevKey, avgPrecision (%), stdDevPrecision (%), avgRecall (%), stdDevRecall (%), fMeasure]
	 */
	public static double[] evaluateTopics(List<MauiTopics> allDocumentsTopics) {
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
}
