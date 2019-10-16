package com.entopix.maui.tests;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
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
import com.entopix.maui.utils.UI;
import com.entopix.maui.vocab.Vocabulary;
/**
 * Performs a structured test comparing models made with abstracts and full texts, made with 10 to 30 documents.
 * @author Rahmon Jorge
 */
public class StructuredTest {
	
	static String dataPath = MauiFileUtils.getDataPath();
	static String vocabPath = MauiFileUtils.getVocabPath();
	static String abstractsDocsPath = dataPath + "\\docs\\corpusci\\abstracts";
	static String fullTextsDocsPath = dataPath + "\\docs\\corpusci\\fulltexts";
	static String modelsPath = MauiFileUtils.getModelsDirPath();
	
	static Stopwords stopwords = new StopwordsPortuguese();
	static String vocabFormat = "skos";
	static String language = "pt";
	static String encoding = "UTF-8";
	static boolean serialize = true;
	static boolean reorder = false;
	
	private static MauiModel buildModel(Stemmer stemmer, File trainDir) throws Exception {
		ModelDocType modelType = ((trainDir.getAbsolutePath().contains("abstracts") ? ModelDocType.ABSTRACTS : ModelDocType.FULLTEXTS));
		String modelName = "model_" + modelType.getName() + "_" + stemmer.getClass().getSimpleName() + "_" + trainDir.getName();
		
		String modelPath = modelsPath + "\\" + modelName;
		MauiModel model = new MauiModel(trainDir.getAbsolutePath(), modelPath, stemmer, stopwords, vocabPath, "skos", "UTF-8", "pt", modelType);
		model.saveModel();
		return model;
	}
	
	public static List<MauiModel> buildModels(String trainDir) throws Exception {
		
		Stemmer[] stemmerList = {
				new PortugueseStemmer(),
				new LuceneRSLPStemmer(),
				new LuceneBRStemmer(),
				new LuceneSavoyStemmer(),
				new LuceneRSLPMinimalStemmer(),
		};
		
		List<MauiModel> modelList = new ArrayList<MauiModel>();
		File[] dirList = new File(trainDir).listFiles();
		dirList = MauiFileUtils.filterFileList(dirList, "train");
		
		//Builds models on normal stemmers
		for(Stemmer s : stemmerList) {
			for(File f : dirList) {
				modelList.add(buildModel(s, f));
			}
		}
		
		
		//Builds models on WekaStemmers
		Stemmer stemmer = new WekaStemmerOrengo();
		for(File f : dirList) {
			modelList.add(buildModel(stemmer, f));
		}
		
		stemmer = new WekaStemmerPorter();
		for(File f : dirList) {
			modelList.add(buildModel(stemmer, f));
		}
		
		stemmer = new WekaStemmerSavoy();
		for(File f : dirList) {
			modelList.add(buildModel(stemmer, f));
		}
		
		return modelList;
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
	
	/**
	 * Returns a matrix each line containing a model name and its results.
	 * @return 
	 * @throws MauiFilterException 
	 */
	public static List<String[]> testModelsList(List<MauiModel> list, String testDir) throws MauiFilterException {
		List<String[]> matrix = new ArrayList<String[]>();
		
		for(MauiModel m : list) {
			//set wekastemmers options individually
			matrix.add(testModel(m.getFile(), testDir, m.getStemmer(), reorder, serialize));
		}
		return matrix;
	}
	
	public static void printMatrix(List<String[]> matrix) {
		
		matrix = formatMatrix(matrix);
		
		//Prints header
		String[] header = {"MODEL NAME","AVG KEY","STDEV KEY","AVG PRECISION","STDEV PRECISION","AVG RECALL","STDEV RECALL","F-MEASURE"};
		for(String word : header) {
			if(word.equals(header[0])) {
				System.out.format("%-65s",word);
			} else {
				System.out.format("%-20s", word);
			}
		}
		System.out.println();
		
		//Prints matrix
		for(String[] model : matrix) {
			for(String value : model) {
				System.out.print(value);
			}
			System.out.println();
		}
	}
	
	public static List<String[]> formatMatrix(List<String[]> matrix) {
		
		List<String[]> newMatrix = new ArrayList<String[]>();
		
		int i;
		for(String[] line : matrix) {
			for(i = 0; i < line.length; i++) {
				if(i > 0) {
					line[i] = String.format("%-20s", line[i]);
				} else {
					line[i] = String.format("%-65s", line[i]);
				}
			}
			newMatrix.add(line);
		}
		return newMatrix;
	}
	
	/**
	 * Runs the structured test.
	 * @param testProgram 1 for build and test all models and 2 to test only
	 */
	public static void run() throws Exception {
		
		Instant buildStart = Instant.now();
		List<MauiModel> abstractsModels = buildModels(abstractsDocsPath);
		List<MauiModel> fulltextsModels = buildModels(fullTextsDocsPath);
		Instant buildEnd = Instant.now();
		
		Instant testStart = Instant.now();
		List<String[]> abstractsResults30 = testModelsList(abstractsModels, abstractsDocsPath + "\\test30");
		List<String[]> abstractsResults60 = testModelsList(abstractsModels, abstractsDocsPath + "\\test60");
		List<String[]> fulltextsResults30 = testModelsList(fulltextsModels, fullTextsDocsPath + "\\test30");
		List<String[]> fulltextsResults60 = testModelsList(fulltextsModels, fullTextsDocsPath + "\\test60");
		Instant testEnd = Instant.now();
		
		System.out.println("\n- ABSTRACTS -");
		System.out.println("\n---> Test results based on 30 documents:");
		printMatrix(abstractsResults30);
		System.out.println("\n---> Test results based on 60 documents:");
		printMatrix(abstractsResults60);
		System.out.println("\n- FULLTEXTS -");
		System.out.println("\n---> Test results based on 30 documents:");
		printMatrix(fulltextsResults30);
		System.out.println("\n---> Test results based on 60 documents:");
		printMatrix(fulltextsResults60);
		
		System.out.print("\nModel building duration: ");
		UI.showElapsedTime(buildStart, buildEnd);
		System.out.println();
		System.out.print("Model testing duration: ");
		UI.showElapsedTime(testStart, testEnd);
		System.out.print("Total Duration: ");
		UI.showElapsedTime(buildStart, buildEnd);
		
	}


}
