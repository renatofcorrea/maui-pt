package com.entopix.maui.tests;

import java.io.File;
import java.util.List;

import com.entopix.maui.filters.MauiFilter;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.main.MauiModelBuilder;
import com.entopix.maui.main.MauiTopicExtractor;
import com.entopix.maui.stemmers.NewPortugueseStemmer;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stopwords.StopwordsPortuguese;
import com.entopix.maui.util.DataLoader;
import com.entopix.maui.util.MauiDocument;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.utils.Paths;

import weka.core.Utils;
/**
 * Performs a structured test comparing models made with abstracts and full texts, made with 10 to 30 documents.
 * @author Rahmon Jorge
 */
public class StructuredTest {
	
	static String dataPath = Paths.getDataPath();
	
	static String abstractsDocsPath = dataPath + "\\docs\\corpusci\\abstracts";
	static String fullTextsDocsPath = dataPath + "\\docs\\corpusci\\full_texts";
	
	static String abstractsTestPath = abstractsDocsPath + "test30";
	static String fullTextsTestPath = fullTextsDocsPath + "test30";
	
	static String abstractsModelsPath = dataPath + "\\models";
	static String fullTextsModelsPath = dataPath + "\\models";
	
	static String vocabPath = dataPath + "\\vocabulary\\TBCI-SKOS_pt.rdf";
	
	static String stemmerName;
	
	/**
	 * 
	 * @param docType abstracts or fulltext
	 * @param docCount 10, 20 or 30
	 * @throws Exception
	 */
	static void buildModel(Stemmer stemmer, String docType, int docCount) throws Exception {
		MauiModelBuilder modelBuilder = new MauiModelBuilder();
		modelBuilder.vocabularyFormat = "skos";
		modelBuilder.vocabularyName = vocabPath;
		modelBuilder.stemmer = stemmer;
		modelBuilder.stopwords = new StopwordsPortuguese();
		modelBuilder.documentLanguage = "pt";
		modelBuilder.documentEncoding = "UTF-8";
		modelBuilder.minNumOccur = 1;
		modelBuilder.maxPhraseLength = 5;
		modelBuilder.minPhraseLength = 1;
		modelBuilder.serialize = false; //true
		modelBuilder.setPositionsFeatures(false);
		modelBuilder.setKeyphrasenessFeature(false);
		modelBuilder.setThesaurusFeatures(false);
		
		if(docType.equals("abstracts")) {
			switch(docCount) {
			case 10:
				modelBuilder.inputDirectoryName = abstractsDocsPath + "\\train10";
				modelBuilder.modelName = abstractsModelsPath + "\\model_abstracts_train10_" + stemmerName;
				MauiFilter filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
				modelBuilder.saveModel(filter);
				break;
			case 20:
				modelBuilder.inputDirectoryName = abstractsDocsPath + "\\train20";
				modelBuilder.modelName = abstractsModelsPath + "\\model_abstracts_train20_" + stemmerName;
				filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
				modelBuilder.saveModel(filter);
				break;
			case 30:
				modelBuilder.inputDirectoryName = abstractsDocsPath + "\\train30";
				modelBuilder.modelName = abstractsModelsPath + "\\model_abstracts_train30_" + stemmerName;
				filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
				modelBuilder.saveModel(filter);
				break;
			default:
				throw new Exception("ERROR: Invalid docCount in StructuredTest.buildModel");
			}
		} else if(docType.equals("full_texts")) {
			switch(docCount) {
			case 10:
				modelBuilder.inputDirectoryName = fullTextsDocsPath + "\\train10";
				modelBuilder.modelName = fullTextsModelsPath + "\\model_fulltexts_train10_" + stemmerName;
				MauiFilter filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
				modelBuilder.saveModel(filter);
				break;
			case 20:
				modelBuilder.inputDirectoryName = fullTextsDocsPath + "\\train20";
				modelBuilder.modelName = fullTextsModelsPath + "\\model_fulltexts_train20_" + stemmerName;
				filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
				modelBuilder.saveModel(filter);
				break;
			case 30:
				modelBuilder.inputDirectoryName = fullTextsDocsPath + "\\train30";
				modelBuilder.modelName = fullTextsModelsPath + "\\model_fulltexts_train30_" + stemmerName;
				filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
				modelBuilder.saveModel(filter);
				break;
			default:
				throw new Exception("ERROR: Invalid docCount in StructuredTest.buildModel");
			}
		} else {
			throw new Exception("ERROR: Invalid docType in StructuredTest.buildModel");
		}
	}
	
	/**
	 * Tests a model precision, recall and f-measure, then return the results.
	 * @param docType abstracts or full_texts
	 * @param modelPath
	 * @throws MauiFilterException
	 */
	static double[] testModel(String docType, String modelPath) throws MauiFilterException {
		MauiTopicExtractor topicExtractor = new MauiTopicExtractor();
		
		if(docType.equals("abstracts"))
			topicExtractor.inputDirectoryName = abstractsTestPath;
		else if(docType.equals("full_texts"))
			topicExtractor.inputDirectoryName = fullTextsTestPath;
		topicExtractor.modelName = modelPath;
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
		topicExtractor.printTopics(topics);
		return TestUtils.evaluateTopics(topics);
	}
	
	public static void printTestResults(int docsCount, String[][] resultsString) {
		System.out.println("TEST RESULTS BASED ON "+ docsCount + " DOCUMENTS");
		System.out.println("Model\t\t\tCorrect Keyphrases\t\tPrecision\t\t\tRecall\t\t\t\tF-Measure");
		System.out.println("     \t\t\tAvg\t\tStdDev\t\tAvg\t\tStdDev\t\tAvg\t\tStdDev \n");
		
		for (int modelNumber = 0; modelNumber < resultsString.length; modelNumber++) {
			for(int i = 0; i < resultsString[modelNumber].length; i++) {
				if(i == 0)
					System.out.print(resultsString[modelNumber][i] + "\t");
				else 
					System.out.print(resultsString[modelNumber][i] + "\t\t");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static String[][] testAllModels(int testDocsCount) throws MauiFilterException {
		String[][] resultsString = {
				{"modelA","correctkeys1","correctkeys2","precision1","precision2","recall1","recall2","f-measure"},
				{"modelB","correctkeys1","correctkeys2","precision1","precision2","recall1","recall2","f-measure"},
				{"modelB","correctkeys1","correctkeys2","precision1","precision2","recall1","recall2","f-measure"},
				{"modelC","correctkeys1","correctkeys2","precision1","precision2","recall1","recall2","f-measure"},
				{"modelD","correctkeys1","correctkeys2","precision1","precision2","recall1","recall2","f-measure"},
				{"modelE","correctkeys1","correctkeys2","precision1","precision2","recall1","recall2","f-measure"},
		};
		
		//Selects test directory
		if(testDocsCount == 30) {
			abstractsTestPath = abstractsDocsPath + "\\test30";
			fullTextsTestPath = fullTextsDocsPath + "\\test30";
		} else if(testDocsCount == 60) {
			abstractsTestPath = abstractsDocsPath + "\\test60";
			fullTextsTestPath = fullTextsDocsPath + "\\test60";
		}
		
		double[] results = null;
		
		results = testModel("abstracts",abstractsModelsPath + "\\model_abstracts_train10_" + stemmerName);
		resultsString[0][0] = "abstracts_model10";
		for (int i = 0; i < 7; i++) {
			if(i > 1) {results[i] *= 100;}
			resultsString[0][i+1] = Utils.doubleToString(results[i], 2);
		}
		
		results = testModel("abstracts",abstractsModelsPath + "\\model_abstracts_train20_" + stemmerName);
		resultsString[1][0] = "abstracts_model20";
		for (int i = 0; i < 7; i++) {
			if(i > 1) {results[i] *= 100;}
			resultsString[1][i+1] = Utils.doubleToString(results[i], 2);
		}
		
		results = testModel("abstracts",abstractsModelsPath + "\\model_abstracts_train30_" + stemmerName);
		resultsString[2][0] = "abstracts_model30";
		for (int i = 0; i < 7; i++) {
			if(i > 1) {results[i] *= 100;}
			resultsString[2][i+1] = Utils.doubleToString(results[i], 2);
		}
		
		results = testModel("full_texts",fullTextsModelsPath + "\\model_fulltexts_train10_" + stemmerName);
		resultsString[3][0] = "full_text_model10";
		for (int i = 0; i < 7; i++) {
			if(i > 1) {results[i] *= 100;}
			resultsString[3][i+1] = Utils.doubleToString(results[i], 2);
		}
		
		results = testModel("full_texts",fullTextsModelsPath + "\\model_fulltexts_train20_" + stemmerName);
		resultsString[4][0] = "full_text_model20";
		for (int i = 0; i < 7; i++) {
			if(i > 1) {results[i] *= 100;}
			resultsString[4][i+1] = Utils.doubleToString(results[i], 2);
		}
		
		results = testModel("full_texts",fullTextsModelsPath + "\\model_fulltexts_train30_" + stemmerName);
		resultsString[5][0] = "full_text_model30";
		for (int i = 0; i < 7; i++) {
			if(i > 1) {results[i] *= 100;}
			resultsString[5][i+1] = Utils.doubleToString(results[i], 2);
		}
		
		return resultsString;
	}
	
	public static void buildAllModels() throws Exception {
		
		Stemmer stemmer = new PortugueseStemmer();
		stemmerName = "ptstemmer";
		buildModel(stemmer,"abstracts",10);
		buildModel(stemmer,"abstracts",20);
		buildModel(stemmer,"abstracts",30);
		buildModel(stemmer,"full_texts",10);
		buildModel(stemmer,"full_texts",20);
		buildModel(stemmer,"full_texts",30);
		
		File vocab = null;
		String serialPath = "C:\\Users\\PC1\\git\\maui-pt\\data\\vocabulary\\TBCI-SKOS_pt.rdf_com.entopix.maui.vocab.VocabularyStore_Original_PortugueseStemmer.serialized";
		
		vocab = new File(serialPath);
		vocab.delete();
		
		String[] stemOptions = {"-S",""};
		
		stemOptions[1] = "Savoy";
		stemmer = new NewPortugueseStemmer(stemOptions);
		stemmerName = "savoy";
		buildModel(stemmer,"abstracts",10);
		buildModel(stemmer,"abstracts",20);
		buildModel(stemmer,"abstracts",30);
		buildModel(stemmer,"full_texts",10);
		buildModel(stemmer,"full_texts",20);
		buildModel(stemmer,"full_texts",30);
		vocab = new File(serialPath);
		vocab.delete();
		
		stemOptions[1] = "Porter";
		stemmer = new NewPortugueseStemmer(stemOptions);
		stemmerName = "porter";
		buildModel(stemmer,"abstracts",10);
		buildModel(stemmer,"abstracts",20);
		buildModel(stemmer,"abstracts",30);
		buildModel(stemmer,"full_texts",10);
		buildModel(stemmer,"full_texts",20);
		buildModel(stemmer,"full_texts",30);
		vocab = new File(serialPath);
		vocab.delete();
		
		stemOptions[1] = "Orengo";
		stemmer = new NewPortugueseStemmer(stemOptions);
		stemmerName = "orengo";
		buildModel(stemmer,"abstracts",10);
		buildModel(stemmer,"abstracts",20);
		buildModel(stemmer,"abstracts",30);
		buildModel(stemmer,"full_texts",10);
		buildModel(stemmer,"full_texts",20);
		buildModel(stemmer,"full_texts",30);
		vocab = new File(serialPath);
		vocab.delete();
	}
	
	public static void runAllTests() throws Exception {
		
		buildAllModels();
		
		stemmerName = "ptstemmer";
		String[][] matrix30docsPtStmr = testAllModels(30);
		String[][] matrix60docsPtStmr = testAllModels(60);
		stemmerName = "savoy";
		String[][] matrix30docsSavoy = testAllModels(30);
		String[][] matrix60docsSavoy = testAllModels(60);
		stemmerName = "porter";
		String[][] matrix30docsPorter = testAllModels(30);
		String[][] matrix60docsPorter = testAllModels(60);
		stemmerName = "orengo";
		String[][] matrix30docsOrengo = testAllModels(30);
		String[][] matrix60docsOrengo = testAllModels(60);
		
		System.out.println("\n-STANDARD STEMMER-\n");
		printTestResults(30, matrix30docsPtStmr);
		printTestResults(60, matrix60docsPtStmr);
		
		System.out.println("\n-SAVOY STEMMER-\n");
		printTestResults(30, matrix30docsSavoy);
		printTestResults(60, matrix60docsSavoy);
		
		System.out.println("\n-PORTER STEMMER-\n");
		printTestResults(30, matrix30docsPorter);
		printTestResults(60, matrix60docsPorter);
		
		System.out.println("\n-ORENGO STEMMER-\n");
		printTestResults(30, matrix30docsOrengo);
		printTestResults(60, matrix60docsOrengo);
	}

}
