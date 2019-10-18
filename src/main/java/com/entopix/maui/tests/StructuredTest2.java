package com.entopix.maui.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.entopix.maui.beans.MauiModel;
import com.entopix.maui.beans.ModelDocType;
import com.entopix.maui.filters.MauiFilter;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.main.MauiModelBuilder;
import com.entopix.maui.main.MauiTopicExtractor;
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
import com.entopix.maui.utils.MauiFileUtils;
import com.entopix.maui.vocab.Vocabulary;

public class StructuredTest2 {

	//Paths
	static String dataPath = MauiFileUtils.getDataPath();
	static String modelsPath = MauiFileUtils.getModelsDirPath();
	static String vocabPath = MauiFileUtils.getVocabPath();
	
	//Files
	static File modelsDir = new File(modelsPath);
	static File abstractsDir = new File(dataPath + "\\docs\\corpusci\\abstracts");
	static File fullTextsDir = new File(dataPath + "\\docs\\corpusci\\fulltexts");
	
	//Initialization
	static Stopwords stopwords = new StopwordsPortuguese();
	static MauiModelBuilder modelBuilder = new MauiModelBuilder();
	static MauiTopicExtractor topicExtractor = new MauiTopicExtractor();
	static MauiFilter filter = new MauiFilter();
	static Vocabulary vocab = new Vocabulary();
	
	//Configuration
	static String encoding = "UTF-8";
	static String language = "pt";
	static String vocabFormat = "skos";
	static boolean serialize = true;
	static boolean reorder = false;
	
	static MauiModel buildModel(Stemmer stemmer, File trainDir) throws Exception {
		//makes model name
		ModelDocType modelType = ((trainDir.getAbsolutePath().contains("abstracts") ? ModelDocType.ABSTRACTS : ModelDocType.FULLTEXTS));
		String modelName = "model_" + modelType.getName() + "_" + stemmer.getClass().getSimpleName() + "_" + trainDir.getName();
		
		//builds model
		String modelPath = modelsPath + "\\" + modelName;
		modelBuilder.inputDirectoryName = trainDir.getPath();
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

		vocab = new Vocabulary();
		vocab.setReorder(false);
		vocab.setSerialize(true);
		vocab.setEncoding(encoding);
		vocab.setLanguage(language);
		vocab.setStemmer(stemmer);
		vocab.setStopwords(stopwords);
		vocab.setVocabularyName(vocabPath);
		vocab.initializeVocabulary(vocabPath, vocabFormat);
		modelBuilder.setVocabulary(vocab);
		
		modelBuilder.setPositionsFeatures(false);
		modelBuilder.setKeyphrasenessFeature(false);
		modelBuilder.setThesaurusFeatures(false);
		
		filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(trainDir.getPath()));
		modelBuilder.saveModel(filter);
		return new MauiModel(modelName, modelPath, stemmer, modelType, trainDir.getPath());
	}
	
	/**
	 * @return the test results in an array with the format: 
	 * [model name, avg key, stdDev key, avgPrecision, stdDevPrecision, avgRecall, stdDevRecall, fMeasure]
	 */
	static String[] testModel(MauiModel model, String testDir, boolean reorder, boolean serialize) throws MauiFilterException {
		
		topicExtractor.inputDirectoryName = testDir;
		topicExtractor.modelName = model.getPath();
		topicExtractor.vocabularyName = vocabPath;
		topicExtractor.vocabularyFormat = vocabFormat;
		topicExtractor.stemmer = model.getStemmer();
		topicExtractor.stopwords = stopwords;
		topicExtractor.documentLanguage = language;
		topicExtractor.documentEncoding = encoding;
		topicExtractor.cutOffTopicProbability = 0.12;
		topicExtractor.serialize = true;
		
		vocab = new Vocabulary();
		vocab.setReorder(reorder);
		vocab.setSerialize(serialize);
		vocab.setEncoding(encoding);
		vocab.setLanguage(language);
		vocab.setStemmer(model.getStemmer());
		vocab.setStopwords(stopwords);
		vocab.setVocabularyName(vocabPath);
		vocab.initializeVocabulary(vocabPath, vocabFormat);
		
		topicExtractor.setVocabulary(vocab);
		topicExtractor.loadModel();
		
		List<MauiDocument> documents = DataLoader.loadTestDocuments(testDir);
		List<MauiTopics> topics = topicExtractor.extractTopics(documents);
		topicExtractor.printTopics(topics);
		double[] results = TestUtils.evaluateTopics(topics);
		
		//converts results to string and adds the model name to it
		String[] array = new String[8];
		array[0] = model.getName();
		int i;
		for(i = 0; i < results.length; i++) {
			array[i+1] = String.format("%.2f", results[i]);
		}
		return array;
	}
	
	public static void run() throws Exception {
		
		Stemmer[] stemmers = {
				new PortugueseStemmer(),
				new LuceneRSLPStemmer(),
				new LuceneBRStemmer(),
				new LuceneSavoyStemmer(),
				new LuceneRSLPMinimalStemmer(),
				new WekaStemmerOrengo(),
				new WekaStemmerPorter(),
				new WekaStemmerSavoy(),
		};
		
		List<String[]> abstractsResults30 = new ArrayList<>();
		File[] trainFolders = MauiFileUtils.filterFileList(abstractsDir.listFiles(), "train");
		String testDir = abstractsDir + "\\test30";
		MauiModel model = null;
		
		String[] result;
		for(Stemmer s : stemmers) {
			for(File f : trainFolders) {
				model = buildModel(s, f); //MODEL NOT BEING BUILT PROPERLY!!!
				result = testModel(model, testDir, reorder, serialize);
				abstractsResults30.add(result);
			}
		}
		
		System.out.println("- ABSTRACTS -");
		System.out.println("---> Test results based on 30 documents: ");
		StructuredTest.printMatrix(abstractsResults30);
	}
	
	public static void main(String[] args) {
		try {
			run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
