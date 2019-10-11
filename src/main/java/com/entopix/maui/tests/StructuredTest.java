package com.entopix.maui.tests;

import java.io.File;
import java.time.Duration;
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
import com.entopix.maui.stemmers.WekaStemmer;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stopwords.StopwordsPortuguese;
import com.entopix.maui.util.DataLoader;
import com.entopix.maui.util.MauiDocument;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.utils.MauiFileUtils;
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
	static String stemmerName;
	
	static MauiModel buildModel(Stemmer stemmer, String trainDirPath) throws Exception {
		ModelDocType modelType = ((trainDirPath.contains("abstracts") ? ModelDocType.ABSTRACTS : ModelDocType.FULLTEXTS));
		File trainDir = new File(trainDirPath);
		
		//Formats model name
		String modelName = null;
		if(stemmer instanceof WekaStemmer) { //warning: the static stemmer while calling this method has to be properly set to match the one used on model.
			modelName = "model_" + stemmer.getClass().getSimpleName() + "_" + WekaStemmer.staticStemmer.getStemmer().toString() + "_" + modelType.getName() + "_" + trainDir.getName();
		} else {
			modelName = "model_" + stemmer.getClass().getSimpleName() + "_" + modelType.getName() + "_" + trainDir.getName();
		}
		
		//Builds model
		String modelPath = modelsPath + "\\" + modelName;
		MauiModel model = new MauiModel(trainDirPath, modelPath, stemmer, vocabPath, modelType);
		model.saveModel();
		return model;
	}
	
	public static List<MauiModel> buildModels(String trainDir) throws Exception {
		String serialVocabPath = MauiFileUtils.getDataPath() + "\\vocabulary\\TBCI-SKOS_pt.rdf_com.entopix.maui.vocab.VocabularyStore_Original_PortugueseStemmer.serialized";
		File vocab = null;
		
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
				modelList.add(buildModel(s, f.getAbsolutePath()));
			}
			vocab = new File(serialVocabPath);
			vocab.delete();
		}
		
		//Builds models on WekaStemmers
		Stemmer stemmer = WekaStemmer.getInstance();
		WekaStemmer.setOptions("Orengo");
	
		for(File f : dirList) {
			modelList.add(buildModel(stemmer, f.getAbsolutePath()));
		}
		
		vocab = new File(serialVocabPath);
		vocab.delete();
		
		WekaStemmer.setOptions("Porter");
		stemmer = WekaStemmer.getInstance();
		for(File f : dirList) {
			modelList.add(buildModel(stemmer, f.getAbsolutePath()));
		}
		
		vocab = new File(serialVocabPath);
		vocab.delete();
		
		WekaStemmer.setOptions("Savoy");
		stemmer = WekaStemmer.getInstance();
		for(File f : dirList) {
			modelList.add(buildModel(stemmer, f.getAbsolutePath()));
		}
		
		vocab = new File(serialVocabPath);
		vocab.delete();
		
		WekaStemmer.staticStemmer = null;
		
		return modelList;
	}
	
	/**
	 * Tests a model precision, recall and f-measure, then return the results in a array of strings.
	 * @param modelPath
	 * @param testDirPath
	 * @throws MauiFilterException
	 */
	static String[] testModel(File model, String testDirPath) throws MauiFilterException {
		//tests model
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
		topicExtractor.loadModel();
		List<MauiDocument> documents = DataLoader.loadTestDocuments(topicExtractor.inputDirectoryName);
		List<MauiTopics> topics = topicExtractor.extractTopics(documents);
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
	 * Returns a List of arrays each containing a model name and its results.
	 * @return 
	 * @throws MauiFilterException 
	 */
	public static List<String[]> testModelsList(List<MauiModel> list, String testDir) throws MauiFilterException {
		List<String[]> matrix = new ArrayList<String[]>();
		
		for(MauiModel m : list) {
			matrix.add(testModel(m.getFile(), testDir));
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
		
		Instant start = Instant.now();
	
		List<MauiModel> abstractsModels = buildModels(abstractsDocsPath);
		List<MauiModel> fulltextsModels = buildModels(fullTextsDocsPath);

		List<String[]> abstractsResults30 = testModelsList(abstractsModels, abstractsDocsPath + "\\test30");
		List<String[]> abstractsResults60 = testModelsList(abstractsModels, abstractsDocsPath + "\\test60");

		List<String[]> fulltextsResults30 = testModelsList(fulltextsModels, fullTextsDocsPath + "\\test30");
		List<String[]> fulltextsResults60 = testModelsList(fulltextsModels, fullTextsDocsPath + "\\test60");
		
		System.out.println("- ABSTRACTS -");
		System.out.println("---> Test results based on 30 documents:");
		printMatrix(abstractsResults30);
		System.out.println("---> Test results based on 60 documents:");
		printMatrix(abstractsResults60);
		
		System.out.println();
		
		System.out.println("- FULLTEXTS -");
		System.out.println("---> Test results based on 30 documents:");
		printMatrix(fulltextsResults30);
		System.out.println("---> Test results based on 60 documents:");
		printMatrix(fulltextsResults60);
		
		Instant finish = Instant.now();
		
		double seconds = (Duration.between(start, finish).toMillis()/1000);
		int minutes = (int) seconds/60;
		int remainingSec = (int) (seconds - (minutes*60));
		System.out.println("Structured test duration: " + minutes + " minutes and " + remainingSec + " seconds.");
	}

}
