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
import com.entopix.maui.stemmers.LuceneLPTStemmer;
import com.entopix.maui.stemmers.LuceneMPTStemmer;
import com.entopix.maui.stemmers.LucenePTStemmer;
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
	static String vocabPath = Paths.getVocabPath();
	
	static String abstractsDocsPath = dataPath + "\\docs\\corpusci\\abstracts";
	static String fullTextsDocsPath = dataPath + "\\docs\\corpusci\\full_texts";
	
	static String modelsPath = dataPath + "\\models";
	
	static String stemmerName;
	
	static void buildModel(Stemmer stemmer, String trainDirPath) throws Exception {
		ModelDocType modelType = ((trainDirPath.contains("abstracts") ? ModelDocType.ABSTRACTS : ModelDocType.FULLTEXTS));
		
		String modelName = null;
		
		if(stemmer instanceof NewPortugueseStemmer) {
			NewPortugueseStemmer newptstemmer = (NewPortugueseStemmer) stemmer;
			modelName = "model_NewPortugueseStemmer_" + newptstemmer.type + "_" + modelType.getName() + "_" + new File(trainDirPath).getName();
		} else {
			modelName = "model_" + stemmer.getClass().getSimpleName() + "_" + modelType.getName() + "_" + new File(trainDirPath).getName();
		}
		
		String modelPath = modelsPath + "\\" + modelName;
		MauiModel model = new MauiModel(trainDirPath, modelPath, stemmer, vocabPath, modelType);
		model.saveModel();
	}
	
	/**
	 * Tests a model precision, recall and f-measure, then return the results.
	 * @param docType abstracts or full_texts
	 * @param modelPath
	 * @throws MauiFilterException
	 */
	static String[] testModel(String modelPath, String testDirPath) throws MauiFilterException {
		//Test model
		MauiTopicExtractor topicExtractor = new MauiTopicExtractor();
		topicExtractor.inputDirectoryName = testDirPath;
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
		
		double[] results = TestUtils.evaluateTopics(topics);
		
		//Converts results to string
		String modelName = new File(modelPath).getName();
		String[] resultString = new String[8];
		resultString[0] = modelName;
		int i;
		for(i = 0 ; i < resultString.length ; i++) {
			resultString[i+1] = Utils.doubleToString(results[i], 2);
		}
		return resultString;
	}
	
	public static void testAllModels() throws MauiFilterException {
		List<String[]> resultsMatrix = new ArrayList<String[]>();
		File modelsDir = new File(modelsPath);
		File[] abstractsModelsList = Paths.filterFileList(modelsDir.listFiles(), "abstracts");
		File[] fulltextsModelsList = Paths.filterFileList(modelsDir.listFiles(), "abstracts");
		String testDir = null;
		
		//ALL ABSTRACTS TESTS

		testDir = abstractsDocsPath + "//test30";
		for(File f : abstractsModelsList) {
			resultsMatrix.add(testModel(f.getAbsolutePath(), testDir));
		}
		
		testDir = abstractsDocsPath + "//test60";
		for(File f : abstractsModelsList) {
			resultsMatrix.add(testModel(f.getAbsolutePath(), testDir));
		}
		
		List<String[]> abstractsResultsMatrix = resultsMatrix;
		resultsMatrix = new ArrayList<String[]>();
		
		//ALL ABSTRACTS TESTS

		testDir = fullTextsDocsPath + "//test30";
		for(File f : fulltextsModelsList) {
			resultsMatrix.add(testModel(f.getAbsolutePath(), testDir));
		}
		
		testDir = fullTextsDocsPath + "//test60";
		for(File f : fulltextsModelsList) {
			resultsMatrix.add(testModel(f.getAbsolutePath(), testDir));
		}
		
		List<String[]> fulltextsResultsMatrix = resultsMatrix;
		
	}
	
	public static void buildAllModels() throws Exception {
		
		String serialVocabPath = "C:\\Users\\PC1\\git\\maui-pt\\data\\vocabulary\\TBCI-SKOS_pt.rdf_com.entopix.maui.vocab.VocabularyStore_Original_PortugueseStemmer.serialized";
		File vocab = null;
		
		Stemmer[] stemmerList = {
				new PortugueseStemmer(),
				new NewPortugueseStemmer(new String[] {"-S","orengo"}),
				new NewPortugueseStemmer(new String[] {"-S","savoy"}),
				new NewPortugueseStemmer(new String[] {"-S","porter"}),
				new LucenePTStemmer(),
				new LuceneBRStemmer(),
				new LuceneLPTStemmer(),
				new LuceneMPTStemmer(),
		};
		
		File[] dirList = new File(abstractsDocsPath).listFiles();
		dirList = Paths.filterFileList(dirList, "train");
		
		for(Stemmer s : stemmerList) {
			for(File f : dirList) {
				buildModel(s, f.getAbsolutePath());
			}
			vocab = new File(serialVocabPath);
			vocab.delete();
		}
		
		dirList = new File(fullTextsDocsPath).listFiles();
		dirList = Paths.filterFileList(dirList, "train");
		
		for(Stemmer s : stemmerList) {
			for(File f : dirList) {
				buildModel(s, f.getAbsolutePath());
			}
			vocab = new File(serialVocabPath);
			vocab.delete();
		}
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
	
	public static void runAllTests() throws Exception {
		Instant start = Instant.now();
		
		buildAllModels();
		
		/*
		stemmerName = "std_orengo";
		String[][] matrix30docsPtStmr = testAllModels(30);
		String[][] matrix60docsPtStmr = testAllModels(60);
		stemmerName = "newpt_savoy";
		String[][] matrix30docsSavoy = testAllModels(30);
		String[][] matrix60docsSavoy = testAllModels(60);
		stemmerName = "newpt_porter";
		String[][] matrix30docsPorter = testAllModels(30);
		String[][] matrix60docsPorter = testAllModels(60);
		stemmerName = "newpt_orengo";
		String[][] matrix30docsOrengo = testAllModels(30);
		String[][] matrix60docsOrengo = testAllModels(60);
		stemmerName = "lucene_br";
		String[][] matrix30docsLuceneBR = testAllModels(30);
		String[][] matrix60docsLuceneBR = testAllModels(60);
		stemmerName = "lucene_orengo";
		String[][] matrix30docsLuceneOrengo = testAllModels(30);
		String[][] matrix60docsLuceneOrengo = testAllModels(60);
		
		
		System.out.println("\n---STANDARD ORENGO STEMMER---");
		printTestResults(30, matrix30docsPtStmr);
		printTestResults(60, matrix60docsPtStmr);
		
		System.out.println("\n---NEWPT SAVOY STEMMER---");
		printTestResults(30, matrix30docsSavoy);
		printTestResults(60, matrix60docsSavoy);
		
		System.out.println("\n---NEWPT PORTER STEMMER---");
		printTestResults(30, matrix30docsPorter);
		printTestResults(60, matrix60docsPorter);
		
		System.out.println("\n---NEWPT ORENGO STEMMER---");
		printTestResults(30, matrix30docsOrengo);
		printTestResults(60, matrix60docsOrengo);
		
		System.out.println("\n---LUCENE BR STEMMER---");
		printTestResults(30, matrix30docsLuceneBR);
		printTestResults(60, matrix60docsLuceneBR);
		
		System.out.println("\n---LUCENE ORENGO STEMMER---");
		printTestResults(30, matrix30docsLuceneOrengo);
		printTestResults(60, matrix60docsLuceneOrengo);
		*/
		
		Instant finish = Instant.now();
		double timeElapsed = Duration.between(start, finish).toMillis()/1000;
		System.out.println("Structured test duration: " + timeElapsed + " seconds.");
	}

}
