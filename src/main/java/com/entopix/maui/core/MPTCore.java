package com.entopix.maui.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import com.entopix.maui.filters.MauiFilter;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.main.MauiModelBuilder;
import com.entopix.maui.main.MauiTopicExtractor;
import com.entopix.maui.main.MauiWrapper;
import com.entopix.maui.main.TBCI;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stopwords.Stopwords;
import com.entopix.maui.stopwords.StopwordsPortuguese;
import com.entopix.maui.util.DataLoader;
import com.entopix.maui.util.Evaluator;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.util.Topic;
import com.entopix.maui.utils.MPTUtils;
import com.entopix.maui.utils.MauiFileUtils;
import com.entopix.maui.vocab.Vocabulary;
import weka.core.Utils;

public class MPTCore {
	
	//Core Objects
	private static Stopwords stopwords = new StopwordsPortuguese();
	private static MauiModelBuilder modelBuilder = new MauiModelBuilder();
	private static MauiTopicExtractor topicExtractor = new MauiTopicExtractor();
	private static Vocabulary vocab = new Vocabulary();
	private static ModelWrapper model;
	
	//Core parameters
	private static File testDocFile;
	private static String testDirPath;
	private static String trainDirPath = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts\\train30";
	private static Stemmer stemmer = new PortugueseStemmer();
	private static String modelPath = MauiFileUtils.getDataPath() + "\\models\\standard_model";
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
		MPTCore.stopwords = stopwords;
	}

	public static void setModelBuilder(MauiModelBuilder modelBuilder) {
		MPTCore.modelBuilder = modelBuilder;
	}

	public static void setTopicExtractor(MauiTopicExtractor topicExtractor) {
		MPTCore.topicExtractor = topicExtractor;
	}

	public static void setTestDocFile(File testDocFile) {
		MPTCore.testDocFile = testDocFile;
	}

	public static void setTestDirPath(String testDirPath) {
		MPTCore.testDirPath = testDirPath;
	}

	public static void setTrainDirPath(String trainDirPath) {
		MPTCore.trainDirPath = trainDirPath;
	}

	public static void setStemmer(Stemmer stemmer) {
		MPTCore.stemmer = stemmer;
	}

	public static void setModelPath(String modelPath) {
		MPTCore.modelPath = modelPath;
	}

	public static void setVocabPath(String vocabPath) {
		MPTCore.vocabPath = vocabPath;
	}

	public static void setMinOccur(int minOccur) {
		MPTCore.minOccur = minOccur;
	}

	public static void setMaxPhraseLength(int maxPhraseLength) {
		MPTCore.maxPhraseLength = maxPhraseLength;
	}

	public static void setMinPhraseLength(int minPhraseLength) {
		MPTCore.minPhraseLength = minPhraseLength;
	}

	public static void setNumTopicsToExtract(int numTopicsToExtract) {
		MPTCore.numTopicsToExtract = numTopicsToExtract;
	}

	public static void setCutOffTopicProbability(double cutOffTopicProbability) {
		MPTCore.cutOffTopicProbability = cutOffTopicProbability;
	}

	public static void setPrintExtractedTopics(boolean printExtractedTopics) {
		MPTCore.printExtractedTopics = printExtractedTopics;
	}

	public static void setVocabFormat(String vocabFormat) {
		MPTCore.vocabFormat = vocabFormat;
	}

	public static void setVocabSerialize(boolean vocabSerialize) {
		MPTCore.vocabSerialize = vocabSerialize;
	}

	public static void setVocabReorder(boolean vocabReorder) {
		MPTCore.vocabReorder = vocabReorder;
	}

	public static void setEncoding(String encoding) {
		MPTCore.encoding = encoding;
	}

	public static void setLanguage(String language) {
		MPTCore.language = language;
	}
	
	public static void setStemmersPackage(String stemmersPackage) {
		MPTCore.stemmersPackage = stemmersPackage;
	}

	public static void setStopwordsPackage(String stopwordsPackage) {
		MPTCore.stopwordsPackage = stopwordsPackage;
	}
	
	public static void setModel(ModelWrapper modelWrapper) {
		model = modelWrapper;
	}
	
	public static void loadModel(String modelPath) throws Exception {
		Object obj = MauiFileUtils.deserializeObject(modelPath);
		ModelWrapper model = null;
		if (obj instanceof ModelWrapper) model = (ModelWrapper) obj;
		else throw new Exception("Invalid model class");
		setModel(model);
	}
	
	/**
	 * Extracts topics from text. The model must be set beforehand using loadModel or setModel.
	 * @return a array of topics
	 */
	public static String[] extractTopicsFromString(String text) throws Exception {
		return MPTUtils.topicsToStringArray(runMauiWrapperOnString(text));
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
	
	public static MauiFilter setupAndBuildModel() throws Exception {
		
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
			System.out.println("[MauiCore] Modelo " + new File(modelPath).getName() + " construído com sucesso.");
		} else {
			System.out.println("[MauiCore] Save model is disabled, therefore, the MauiFilter was not serialized.");
		}
		
		return filter;
	}
	
	public static List<MauiTopics> runTopicExtractor() throws Exception {
		if (testDirPath == null) throw new NullPointerException("Test directory path for the topic extractor not set.");
		if (modelPath == null) throw new NullPointerException("The model path was not set.");
		if (model == null && modelPath != null) loadModel(modelPath);
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
		
		setupVocab(MauiFileUtils.getDataPath()+getRelativePathtoDataDir(model.getVocabUsedPath()), stemmer, stopwords); // NOTE: The usedvocabpath might be in another machine, so this might throw a runtimexception
		//Remove models may resolve
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
	
	public static String getRelativePathtoDataDir(String fullpath) {
		int index1 = fullpath.indexOf("\\data\\");
		int index2 = fullpath.indexOf("/data/");
		String relativepath = null;
		if (index1 >=0) {
			relativepath = fullpath.substring(index1+5);
		}
		else if (index2 >=0) {
			relativepath = fullpath.substring(index2+5);
		}
		return relativepath;
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
		
		//TODO: Not writing .maui file because keywords are stored in List<Topic> instead of List<MauiTopics>
		return keywords;
	}
	
	public static ArrayList<Topic> runMauiWrapperOnString(String text) throws MauiFilterException {
		if (model == null) throw new NullPointerException("MauiCore's model was not set.");
		MauiWrapper mw = null;
		setupVocab(vocabPath, stemmer, stopwords);
		
		try {
			mw = new MauiWrapper(vocab, model.getFilter()); //TODO: use full constructor
		} catch (Exception e) {
			model.getFilter().setVocabulary(vocab);
			mw = new MauiWrapper(vocab, model.getFilter());
		}
		mw.setModelParameters(vocabPath, stemmer, stopwords, language);

		ArrayList<Topic> keywords = mw.extractTopicsFromText(text, numTopicsToExtract);
		for  (Topic keyword : keywords) {
			System.out.println("Palavra-chave: " + keyword.getTitle() + " " + keyword.getProbability());
		}
		return keywords;
	}
	
	public static List<String> mauiMatches(List<MauiTopics> allDocumentsTopics) {
		List<String> matches = new ArrayList<String>();
		for (MauiTopics documentTopics : allDocumentsTopics) {
			for (Topic topic : documentTopics.getTopics()) {
				if (topic.isCorrect()) {
					matches.add(topic.getTitle());
				}
			}
		}
		return matches;
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
	
	//TODO: this can be considered a search algorithm, so it can be optimized
	/**
	 * Compares matches between extracted and manual keywords, and returns the matches.
	 * @param numTopicsToEvaluate 
	 * @param extracted 
	 * @param keysPath 
	 * @return The topics matched.
	 * @throws Exception 
	 */
	public static String[] matches(String[] manual, String[] extracted) throws Exception {
		
		List<String> matches = new ArrayList<>();
		
		int e, m;
		for (e = 0; e < extracted.length; e++) {
			for (m = 0; m < manual.length; m++) {
				if (extracted[e].equals(manual[m])) {
					matches.add(extracted[e]);
				} else if (extracted[e].equalsIgnoreCase(manual[m])) {
					System.out.println("Match by ignore case (" + extracted[e] + ", " + manual[m] + ")");
					matches.add(extracted[e]);
				} else if (MPTUtils.stripAccents(extracted[e]).equalsIgnoreCase(MPTUtils.stripAccents(manual[m]))) {
					System.out.println("Match by strip accents and ignore case (" + extracted[e] + ", " + manual[m] + ")");
					matches.add(extracted[e]);
				}
			}
		}
		
		return matches.toArray(new String[matches.size()]);
	}
	
	/**
	 * Compares matches between lists of extracted keywords and lists of manual keywords for every document,
	 * and returns a list of the matches.
	 * @param manual
	 * @param extracted
	 * @return
	 * @throws Exception 
	 */
	public static List<String[]> allMatches(List<String[]> manual, List<String[]> extracted) throws Exception {
		
		if (manual.size() != extracted.size()) {
			throw new Exception("The amount of documents between the lists of manual and extracted keywords is different.");
		}
		
		List<String[]> matches = new ArrayList<>();
		int docCount = manual.size();
		for (int i = 0; i < docCount; i++) {
			matches.add(matches(manual.get(i), extracted.get(i)));
		}
		
		return matches;
	}
	
	public static double[] evaluateTopics(String[] manualKeywords, String[] extractedKeywords) throws Exception {
		return evaluateTopics(null, manualKeywords, extractedKeywords, manualKeywords.length, false);
	}
	
	/**
	 * Compares the extracted topics with the manual topics of a single document. Returns number of correct topics, precision, recall and f-measure.
	 * @param keysPath The path to the file containing the manual, original topics
	 * @param extracted The extracted topics
	 * @param numTopicsToEvaluate The number of extracted topics to be evaluated
	 * @return number of correct topics, precision, recall and f-measure.
	 * @throws Exception
	 */
	public static double[] evaluateTopics(String filename, String[] manual, String[] extracted, int numTopicsToEvaluate, boolean printResults) throws Exception {
		
		if (numTopicsToEvaluate > extracted.length) {
			numTopicsToEvaluate = extracted.length;
		}
		
		String[] evaluate = Arrays.copyOfRange(extracted, 0, numTopicsToEvaluate);
		
		String[] matches = matches(evaluate, manual);
		
		int numCorrect = matches.length;
		int numExtracted = extracted.length;
		int numManual = manual.length;
		int numEvaluated = evaluate.length;
		double precision = (double) numCorrect / numExtracted;
		double recall = (double) numCorrect / numManual;
		
		double fMeasure = 0.0;
		if (precision > 0 && recall > 0) {
			fMeasure = 2 * recall * precision / (recall + precision);
		}
		
		if (printResults || DB_evaluateTopicsSingle) {
			if (filename != null && filename.length() > 0) System.out.println("\nFile: " + filename);
			System.out.println(numExtracted + " topics extracted");
			System.out.println(numEvaluated + " topics evaluated " + "\n");
			System.out.println("MANUAL (" + numManual + "):");
			System.out.println(Arrays.toString(manual));
			System.out.println("EVALUATED (" + numTopicsToEvaluate + "): ");
			System.out.println(Arrays.toString(evaluate));
			System.out.println("MATCHES (" + numCorrect + "):");
			System.out.println(Arrays.toString(matches));
			
			System.out.println("Precision: " + precision * 100 + "%");
			System.out.println("Recall: " + recall * 100 + "%");
			System.out.println("F-Measure: " + fMeasure * 100 + "%");
		}
		
		return new double[] {numCorrect, precision, recall, fMeasure};
	}
	
	/**
	 * Compares the extracted topics with the manual topics of a list of documents. Returns mean of correct topics, precision, recall and f-measure.
	 * @param keysPaths
	 * @param allExtractedTopics list of topics extracted in every document
	 * @param numTopicsToEvaluate
	 * @return a size 7 array with the test results.
	 * @throws Exception 
	 */
	public static double[] evaluateMeanOfTopicsList(String[] filenames, List<String[]> allManualTopics, List<String[]> allExtractedTopics, int numTopicsToEvaluate, boolean printResults) throws Exception {
		
		int docCount = allManualTopics.size();
		if (docCount != allExtractedTopics.size()) throw new Exception("Length of extracted topics list is not equal to the number of documents");
		
		int i;
		double[][] docResults = new double[docCount][];
		for (i = 0; i < docCount; i++) {
			docResults[i] = evaluateTopics(filenames[i], allManualTopics.get(i), allExtractedTopics.get(i), numTopicsToEvaluate, false);
		}
		
		double[] allCorrects = MPTUtils.getColumn(docResults, 0);
		double avgCorrect = Utils.mean(allCorrects);
		double stdevCorrect = Math.sqrt(Utils.variance(allCorrects));
		
		double[] allPrecisions = MPTUtils.getColumn(docResults, 1);
		double avgPrecision = Utils.mean(allPrecisions);
		double stdevPrecision = Math.sqrt(Utils.variance(allPrecisions));
		
		double[] allRecalls = MPTUtils.getColumn(docResults, 2);
		double avgRecall = Utils.mean(allRecalls);
		double stdevRecall = Math.sqrt(Utils.variance(allRecalls));
		
		//F-Measure
		double fMeasure = 0.0;
		if (avgPrecision > 0 && avgRecall > 0) {
			fMeasure = 2 * avgRecall * avgPrecision / (avgRecall + avgPrecision);
		}
		
		double[] results = new double[] {avgCorrect, stdevCorrect, avgPrecision, stdevPrecision, avgRecall, stdevRecall, fMeasure};
		
		if (printResults || DB_evaluateTopics) {
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

	
	/**
	 * Compares the extracted topics with the manual topics of a list of documents. Returns number of correct topics, precision, recall and f-measure for each document.
	 * @param filenames
	 * @param allManualTopics
	 * @param allExtractedTopics
	 * @param numTopicsToEvaluate
	 * @param printResults
	 * @return
	 * @throws Exception
	 */
	public static List<double[]> evaluateTopicsList(String[] filenames, List<String[]> allManualTopics, List<String[]> allExtractedTopics, int numTopicsToEvaluate) throws Exception {
		int docCount = allManualTopics.size();
		if (docCount != allExtractedTopics.size()) throw new Exception("Length of extracted topics list is not equal to the number of documents");
		
		List<double[]> results = new ArrayList<>();
		int i;
		for (i = 0; i < docCount; i++) {
			results.add(evaluateTopics(filenames[i], allManualTopics.get(i), allExtractedTopics.get(i), numTopicsToEvaluate, false));
		}
		
		return results;
	}
	
	/**
	 * Shows top frequent concepts and returns the most frequent.
	 * @param terms
	 * @return the top frequent term.
	 */
	public static String getTopFrequentTerm(String[] terms, int termsCount, boolean fullResults) {
		String[] termsToEvaluate = Arrays.copyOf(terms, termsCount); //TODO: maybe problem here
		
		ArrayList<Entry<String, Integer>> result = TBCI.getTBCITopConceptsCount(termsToEvaluate);
		
		String tfcID = result.get(0).getKey(); // Top Frequent Concept ID
		String tfcName = TBCI.getTBCITerm(Integer.parseInt(tfcID)).getKey(); // TOP FREQUENT CONCEPT NAME
		
		if (fullResults) {
			Integer tfcFreq = result.get(0).getValue(); // Top Frequent Concept Frequency
			
			System.out.println("Top Frequent Concept ID: " + tfcID);
			System.out.println("Top Frequent Concept Frequency: " + tfcFreq);
			System.out.println("Top Frequent Concept Name: " + tfcName);  
			
			System.out.println("Top Frequent Concepts");
		    
		    Iterator<Entry<String, Integer>> iterator = result.iterator();
		   
		    System.out.println("Código\tValor\tTermo");
		 
		    while (iterator.hasNext()){
		      Entry<String, Integer> entry = iterator.next();
		      System.out.println(entry.getKey() + "\t"+entry.getValue() + "\t"+ TBCI.getTBCITerm(Integer.parseInt(entry.getKey())).getKey());
		    }
		}
	    
	    return tfcName;
	}
	
	/** Returns the top frequent term from every document of specified format in dirpath. 
	 * @throws FileNotFoundException */
	public static String[] getTopFrequentTermsFromDir(String dirPath, String format, int termsToEvaluate, boolean debug) throws FileNotFoundException {
		List<String[]> keywords = null;
		try {
			keywords = MauiFileUtils.readAllKeyFromDir(dirPath, format);
		} catch (FileNotFoundException e) {
			System.out.println(e.toString());
			return null;
		}
		
		String[] docnames = MauiFileUtils.getFileNames(MauiFileUtils.filterFileList(dirPath, format, true), false);
		int termsCount = keywords.size();
		String[] list = new String[termsCount];
		int doc;
		for (doc = 0; doc < termsCount; doc++) {
			list[doc] = getTopFrequentTerm(keywords.get(doc), termsToEvaluate, false);
			if (debug) {
				System.out.println("[MauiCore] Arquivo: " + docnames[doc]);
				System.out.println("[MauiCore] Termo Geral: " + list[doc]);
				System.out.println("[MauiCore] Documentos avaliados: " + (doc + 1) + " de " + termsCount);
			}
		}
		
		return list;
	}
}