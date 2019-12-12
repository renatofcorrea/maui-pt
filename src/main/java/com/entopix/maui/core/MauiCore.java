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
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stopwords.Stopwords;
import com.entopix.maui.stopwords.StopwordsPortuguese;
import com.entopix.maui.util.DataLoader;
import com.entopix.maui.util.Evaluator;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.util.Topic;
import com.entopix.maui.utils.MauiFileUtils;
import com.entopix.maui.utils.MauiPTUtils;
import com.entopix.maui.utils.UI;
import com.entopix.maui.vocab.Vocabulary;

import weka.core.Utils;

public class MauiCore {
	
	//Core Objects
	private static Stopwords stopwords = new StopwordsPortuguese();
	private static MauiModelBuilder modelBuilder = new MauiModelBuilder();
	private static MauiTopicExtractor topicExtractor = new MauiTopicExtractor();
	private static Vocabulary vocab = new Vocabulary();
	private static ModelWrapper model;
	
	//Core parameters
	private static File testDocFile;
	private static String testDirPath;
	private static String trainDirPath;
	private static Stemmer stemmer = new PortugueseStemmer();
	private static String modelPath;
	private static String vocabPath = MauiFileUtils.getDataPath() + "\\vocabulary\\TBCI-SKOS_pt.rdf";
	
	//Standard ModelBuilder configs
	private static int minOccur = 2;
	private static int maxPhraseLength = 5;
	private static int minPhraseLength = 1;
	public static boolean saveModel = true;
	
	//Standard TopicExtractor configs
	private static int numTopicsToExtract = 10;
	private static double cutOffTopicProbability = 0.12;
	private static boolean printExtractedTopics = true;
	
	//Standard Vocabulary configs
	private static String vocabFormat = "skos";
	private static boolean vocabSerialize = true;
	private static boolean vocabReorder = false;
	
	//Standard General configs
	private static String encoding = "UTF-8";
	private static String language = "pt";
	
	//Debug
	public static boolean DB_evaluateTopicsSingle = false;
	public static boolean DB_evaluateTopics = false;

	//Other
	private static String stemmersPackage = "com.entopix.maui.stemmers.";
	private static String stopwordsPackage = "com.entopix.maui.stopwords.";
	
	public static Stopwords getStopwords() {
		return stopwords;
	}

	public static MauiModelBuilder getModelBuilder() {
		return modelBuilder;
	}

	public static MauiTopicExtractor getTopicExtractor() {
		return topicExtractor;
	}

	public static File getTestDocFile() {
		return testDocFile;
	}

	public static String getTestDirPath() {
		return testDirPath;
	}

	public static String getTrainDirPath() {
		return trainDirPath;
	}

	public static Stemmer getStemmer() {
		return stemmer;
	}

	public static String getModelPath() {
		return modelPath;
	}

	public static String getVocabPath() {
		return vocabPath;
	}

	public static int getMinOccur() {
		return minOccur;
	}

	public static int getMaxPhraseLength() {
		return maxPhraseLength;
	}

	public static int getMinPhraseLength() {
		return minPhraseLength;
	}

	public static int getNumTopicsToExtract() {
		return numTopicsToExtract;
	}

	public static double getCutOffTopicProbability() {
		return cutOffTopicProbability;
	}

	public static boolean isPrintExtractedTopics() {
		return printExtractedTopics;
	}

	public static String getVocabFormat() {
		return vocabFormat;
	}

	public static boolean isVocabSerialize() {
		return vocabSerialize;
	}

	public static boolean isVocabReorder() {
		return vocabReorder;
	}

	public static String getEncoding() {
		return encoding;
	}

	public static String getLanguage() {
		return language;
	}

	public static String getStemmersPackage() {
		return stemmersPackage;
	}

	public static String getStopwordsPackage() {
		return stopwordsPackage;
	}
	
	public static ModelWrapper getModel() {
		return model;
	}

	public static void setStopwords(Stopwords stopwords) {
		MauiCore.stopwords = stopwords;
	}

	public static void setModelBuilder(MauiModelBuilder modelBuilder) {
		MauiCore.modelBuilder = modelBuilder;
	}

	public static void setTopicExtractor(MauiTopicExtractor topicExtractor) {
		MauiCore.topicExtractor = topicExtractor;
	}

	public static void setTestDocFile(File testDocFile) {
		MauiCore.testDocFile = testDocFile;
	}

	public static void setTestDirPath(String testDirPath) {
		MauiCore.testDirPath = testDirPath;
	}

	public static void setTrainDirPath(String trainDirPath) {
		MauiCore.trainDirPath = trainDirPath;
	}

	public static void setStemmer(Stemmer stemmer) {
		MauiCore.stemmer = stemmer;
	}

	public static void setModelPath(String modelPath) {
		MauiCore.modelPath = modelPath;
	}

	public static void setVocabPath(String vocabPath) {
		MauiCore.vocabPath = vocabPath;
	}

	public static void setMinOccur(int minOccur) {
		MauiCore.minOccur = minOccur;
	}

	public static void setMaxPhraseLength(int maxPhraseLength) {
		MauiCore.maxPhraseLength = maxPhraseLength;
	}

	public static void setMinPhraseLength(int minPhraseLength) {
		MauiCore.minPhraseLength = minPhraseLength;
	}

	public static void setNumTopicsToExtract(int numTopicsToExtract) {
		MauiCore.numTopicsToExtract = numTopicsToExtract;
	}

	public static void setCutOffTopicProbability(double cutOffTopicProbability) {
		MauiCore.cutOffTopicProbability = cutOffTopicProbability;
	}

	public static void setPrintExtractedTopics(boolean printExtractedTopics) {
		MauiCore.printExtractedTopics = printExtractedTopics;
	}

	public static void setVocabFormat(String vocabFormat) {
		MauiCore.vocabFormat = vocabFormat;
	}

	public static void setVocabSerialize(boolean vocabSerialize) {
		MauiCore.vocabSerialize = vocabSerialize;
	}

	public static void setVocabReorder(boolean vocabReorder) {
		MauiCore.vocabReorder = vocabReorder;
	}

	public static void setEncoding(String encoding) {
		MauiCore.encoding = encoding;
	}

	public static void setLanguage(String language) {
		MauiCore.language = language;
	}
	
	public static void setStemmersPackage(String stemmersPackage) {
		MauiCore.stemmersPackage = stemmersPackage;
	}

	public static void setStopwordsPackage(String stopwordsPackage) {
		MauiCore.stopwordsPackage = stopwordsPackage;
	}
	
	public static void setModel(ModelWrapper modelWrapper) {
		model = modelWrapper;
	}
	
	public static void loadModel(String modelPath) {
		model = (ModelWrapper) MauiFileUtils.deserializeObject(modelPath);
	}
	
	public static void setupVocab(String vocabPath, Stemmer stemmer, Stopwords stopwords) {
		if (stemmer == null) throw new NullPointerException("Stemmer is not set");

		vocab.setReorder(vocabReorder);
		vocab.setSerialize(vocabSerialize);
		vocab.setEncoding(encoding);
		vocab.setLanguage(language);
		vocab.setStemmer(stemmer);
		vocab.setStopwords(stopwords);
		vocab.setVocabularyName(vocabPath);
		vocab.initializeVocabulary(vocabPath, vocabFormat);
	}
	
	public static MauiFilter buildModel() throws Exception {
		if (trainDirPath == null) throw new NullPointerException("Train directory for the ModelBuilder is not set");
		
		modelBuilder.inputDirectoryName = trainDirPath;
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
		modelBuilder.serialize = vocabSerialize;

		setupVocab(vocabPath, stemmer, stopwords);
		modelBuilder.setVocabulary(vocab);
		modelBuilder.setBasicFeatures(true);
		modelBuilder.setFrequencyFeatures(true);
		modelBuilder.setLengthFeature(true);
		modelBuilder.setPositionsFeatures(false);
		modelBuilder.setKeyphrasenessFeature(false);
		modelBuilder.setThesaurusFeatures(false);
		modelBuilder.setWikipediaFeatures(false);
		
		MauiFilter filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(trainDirPath));
		
		if (saveModel) {
			if (modelPath == null) throw new NullPointerException("Model path for the modelBuilder is not set");
			model = new ModelWrapper(filter, trainDirPath, stemmer, vocabPath);
			MauiFileUtils.serializeObject(model, modelPath);
			UI.showModelBuilt(new File(modelPath).getName());
		} else {
			System.out.println("[MauiCore] Save model is disabled, therefore, the MauiFilter was not serialized.");
		}
		
		
		return filter;
	}
	
	public static List<MauiTopics> runTopicExtractor() throws Exception {
		if (testDirPath == null) throw new NullPointerException("Test directory path for the topic extractor not set.");
		if (modelPath == null) throw new NullPointerException("The model path was not set.");
		if (model == null) throw new NullPointerException("No model loaded.");
		
		topicExtractor.stemmer = stemmer;
		topicExtractor.stopwords = stopwords;
		topicExtractor.serialize = vocabSerialize;
		
		topicExtractor.setOptions(new String[] {
				"-l", testDirPath,
				"-m", modelPath,
				"-v", vocabPath,
				"-f", vocabFormat,
				"-e", encoding,
				"-i", language,
				"-n", String.valueOf(numTopicsToExtract),
				"-c", String.valueOf(cutOffTopicProbability),
		});
		
		setupVocab(vocabPath, stemmer, stopwords);
		topicExtractor.setVocabulary(vocab);
		topicExtractor.setModel(model.getFilter());
		
		List<MauiTopics> topics = null;
		try {
			topics = topicExtractor.extractTopics(DataLoader.loadTestDocuments(testDirPath));
		} catch (NullPointerException e) {
			model.getFilter().setVocabulary(vocab);
			topics = topicExtractor.extractTopics(DataLoader.loadTestDocuments(testDirPath));
		}
		
		if (printExtractedTopics) {
			topicExtractor.printTopics(topics);
			Evaluator.evaluateTopics(topics);
		}
		
		return topics;
	}
	
	public static List<Topic> runMauiWrapperOnFile() throws IOException, MauiFilterException {
		if (testDocFile == null) throw new NullPointerException("Test document file for the MauiWrapper is not set");
		else if (modelPath == null) throw new NullPointerException("Model path for the MauiWrapper is not set");
		
		MauiWrapper mauiWrapper = null;
		setupVocab(vocabPath, stemmer, stopwords);
		String documentText = FileUtils.readFileToString(testDocFile, Charset.forName(encoding));
		
		try {
			mauiWrapper = new MauiWrapper(vocab, model.getFilter()); //TODO: use full constructor
		} catch (Exception e) {
			model.getFilter().setVocabulary(vocab);
			mauiWrapper = new MauiWrapper(vocab, model.getFilter());
		}
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
	
	/** Evaluates the topics on a single document. 
	 * @return precision and recall */
	public static double[] evaluateTopicsSingle(String documentPath, List<String> extracted, int numTopicsToEvaluate) throws Exception {
		
		List<String> manual = MauiFileUtils.readKeyFromFile(documentPath.replace(".txt", ".key")); //original manual keywords from .key file
		
		if (numTopicsToEvaluate > extracted.size()) {
			numTopicsToEvaluate = extracted.size();
		}
		
		List<String> evaluate = extracted.subList(0, numTopicsToEvaluate);
		List<String> matches = new ArrayList<>();
		
		//saves matches
		for (String topic : evaluate) {
			if (manual.contains(topic)) {
				matches.add(topic);
			}
		}
		
		int numCorrect = matches.size();
		int numExtracted = extracted.size();
		int numManual = manual.size();
		int numEvaluated = evaluate.size();
		double precision = (double) numCorrect / numExtracted;
		double recall = (double) numCorrect / numManual;
		
		if (DB_evaluateTopicsSingle) {
			System.out.println("File: " + new File(documentPath).getName());
			System.out.println(numExtracted + " topics extracted");
			System.out.println(numEvaluated + " topics evaluated " + "\n");
			System.out.println("MANUAL (" + numManual + "):");
			System.out.println(manual.toString());
			System.out.println("EVALUATED (" + numTopicsToEvaluate + "): ");
			System.out.println(evaluate.toString());
			System.out.println("MATCHES (" + numCorrect + "):");
			System.out.println(matches.toString());
			
			System.out.println("Precision: " + precision * 100 + "%");
			System.out.println("Recall: " + recall * 100 + "%");
			System.out.println();
		}
		
		return new double[] {numCorrect, precision, recall};
	}
	
	/**
	 * Evaluates the topics on a list of documents. 
	 * @param docPaths
	 * @param allDocTopicsExtracted list of topics extracted in every document
	 * @param numTopicsToEvaluate
	 * @return a size 7 array with the test results.
	 * @throws Exception 
	 */
	public static double[] evaluateTopics(String[] docPaths, List<List<String>> allDocTopicsExtracted, int numTopicsToEvaluate) throws Exception {
		
		int docCount = docPaths.length;
		if (docCount != allDocTopicsExtracted.size()) throw new Exception("Length of extracted topics list is not equal to the number of documents");
		
		int i;
		double[][] docResults = new double[docCount][];
		for (i = 0; i < docCount; i++) {
			docResults[i] = evaluateTopicsSingle(docPaths[i], allDocTopicsExtracted.get(i), numTopicsToEvaluate);
		}
		
		double[] allCorrects = MauiPTUtils.getColumn(docResults, 0);
		double avgCorrect = Utils.mean(allCorrects);
		double stdevCorrect = Math.sqrt(Utils.variance(allCorrects));
		
		double[] allPrecisions = MauiPTUtils.getColumn(docResults, 1);
		double avgPrecision = Utils.mean(allPrecisions);
		double stdevPrecision = Math.sqrt(Utils.variance(allPrecisions));
		
		double[] allRecalls = MauiPTUtils.getColumn(docResults, 2);
		double avgRecall = Utils.mean(allRecalls);
		double stdevRecall = Math.sqrt(Utils.variance(allRecalls));
		
		//F-Measure
		double fMeasure = 0.0;
		if (avgPrecision > 0 && avgRecall > 0) {
			fMeasure = 2 * avgRecall * avgPrecision / (avgRecall + avgPrecision);
		}
		
		double[] results = new double[] {avgCorrect, stdevCorrect, avgPrecision, stdevPrecision, avgRecall, stdevRecall, fMeasure};
		
		if (DB_evaluateTopics) {
			System.out.println("AVG KEY: " + results[0]);
			System.out.println("STDEV KEY: " + results[1]);
			System.out.println("AVG PRECISION: " + results[2] * 100);
			System.out.println("STDEV PRECISION: " + results[3] * 100);
			System.out.println("AVG RECALL: " + results[4] * 100);
			System.out.println("STDEV RECALL: " + results[5] * 100);
			System.out.println("F-MEASURE: " + results[6] * 100);
		}
		
		return results;
	}
}
