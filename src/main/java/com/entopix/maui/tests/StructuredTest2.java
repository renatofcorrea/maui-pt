package com.entopix.maui.tests;

import java.io.File;
import java.util.List;

import com.entopix.maui.beans.MauiModel;
import com.entopix.maui.beans.ModelDocType;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
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
import com.entopix.maui.util.MauiDocument;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.utils.MauiFileUtils;
import com.entopix.maui.vocab.Vocabulary;

public class StructuredTest2 {

	static String dataPath = MauiFileUtils.getDataPath();
	static String modelsPath = MauiFileUtils.getModelsDirPath();
	static File modelsDir = new File(modelsPath);
	static String abstractsPath = dataPath + "\\docs\\corpusci\\abstracts";
	static File abstractsDir = new File(abstractsPath);
	static String fullTextsPath = dataPath + "\\docs\\corpusci\\fulltexts";
	static File fullTextsDir = new File(fullTextsPath);
	static String vocabPath = MauiFileUtils.getVocabPath();
	
	static Stopwords stopwords = new StopwordsPortuguese();
	static String encoding = "UTF-8";
	static String language = "pt";
	static String vocabFormat = "skos";
	static boolean serialize = true;
	static boolean reorder = false;
	
	private static void buildModel(Stemmer stemmer, File trainDir) throws Exception {
		ModelDocType modelType = ((trainDir.getAbsolutePath().contains("abstracts") ? ModelDocType.ABSTRACTS : ModelDocType.FULLTEXTS));
		String modelName = "model_" + modelType.getName() + "_" + stemmer.getClass().getSimpleName() + "_" + trainDir.getName();
		
		String modelPath = modelsPath + "\\" + modelName;
		MauiModel model = new MauiModel(trainDir.getAbsolutePath(), modelPath, stemmer, stopwords, vocabPath, "skos", "UTF-8", "pt", modelType);
		model.saveModel();
	}
	
	static String[] testModel(File model, String testDirPath, Stemmer stemmer, boolean reorder, boolean serialize) throws MauiFilterException {
		
		//setup topic extractor
		MauiTopicExtractor topicExtractor = new MauiTopicExtractor();
		topicExtractor.inputDirectoryName = testDirPath;
		topicExtractor.modelName = model.getPath();
		topicExtractor.vocabularyName = vocabPath;
		topicExtractor.vocabularyFormat = "skos";
		topicExtractor.documentLanguage = "pt";
		topicExtractor.cutOffTopicProbability = 0.12;
		topicExtractor.serialize = true;
		topicExtractor.stopwords = new StopwordsPortuguese();
		topicExtractor.stemmer = new PortugueseStemmer();
		
		//setup vocabulary
		Vocabulary vocab = new Vocabulary();
		vocab.setReorder(reorder);
		vocab.setSerialize(serialize);
		vocab.setEncoding(encoding);
		vocab.setLanguage(language);
		vocab.setStemmer(stemmer);
		vocab.setStopwords(stopwords);
		vocab.setVocabularyName(vocabPath);
		vocab.initializeVocabulary(vocabPath, vocabFormat);
		
		topicExtractor.setVocabulary(vocab);
		topicExtractor.loadModel();
		
		//get topics and evaluate
		List<MauiDocument> documents = DataLoader.loadTestDocuments(topicExtractor.inputDirectoryName);
		List<MauiTopics> topics = topicExtractor.extractTopics(documents); //something wrong with topic extractor
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
		
		File[] trainFolders = MauiFileUtils.filterFileList(abstractsDir.listFiles(), "train");
		for(Stemmer s : stemmers) {
			for(File dir : trainFolders) {
				buildModel(s, dir);
				
			}
		}
	}
}
