package com.entopix.maui.tests;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.entopix.maui.core.MauiCore;
import com.entopix.maui.filters.MauiFilter;
import com.entopix.maui.main.MauiModelBuilder;
import com.entopix.maui.main.MauiTopicExtractor;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stopwords.Stopwords;
import com.entopix.maui.stopwords.StopwordsPortuguese;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.utils.MauiFileUtils;
import com.entopix.maui.utils.MauiPTUtils;
import com.entopix.maui.utils.UI;
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
	
	/** @return A List where each line contains a model name and its test results. */
	private static List<String[]> runTest(File[] trainFolders, String testDir, Stemmer[] stemmers) throws Exception {
		List<String[]> matrix = new ArrayList<String[]>();
		String[] result;
		String modelName, modelPath;
		List<MauiTopics> topics;
		for (Stemmer stemmer : stemmers) {
			for (File trainDir : trainFolders) {
				modelName = MauiPTUtils.generateModelName(trainDir.getPath(), stemmer);
				modelPath = modelsPath + "\\" + modelName;
				MauiCore.setupAndBuildModel(modelPath, trainDir.getPath(), stemmer);
				topics = MauiCore.setupAndRunTopicExtractor(modelPath, testDir, stemmer, false);
				result = MauiPTUtils.formatArray(modelName, MauiCore.classicEvaluateTopics(topics));
				
				matrix.add(result);
			}
		}
		return matrix;
	}
	
	public static void runAllTests() throws Exception {
		
		Stemmer[] stemmers = MauiCore.getStemmerList();
		
		List<ArrayList<String[]>> abstractsMatrixes = new ArrayList<ArrayList<String[]>>();
		List<ArrayList<String[]>> fulltextsMatrixes = new ArrayList<ArrayList<String[]>>();
		File[] trainFolders = null;

		Instant start = Instant.now();
		
		trainFolders = MauiFileUtils.filterFileList(abstractsDir.listFiles(), "train");
		abstractsMatrixes.add((ArrayList<String[]>) runTest(trainFolders, abstractsDir.getPath() + "//test30", stemmers));
		abstractsMatrixes.add((ArrayList<String[]>) runTest(trainFolders, abstractsDir.getPath() + "//test60", stemmers));
		
		trainFolders = MauiFileUtils.filterFileList(fullTextsDir.listFiles(), "train");
		fulltextsMatrixes.add((ArrayList<String[]>) runTest(trainFolders, fullTextsDir.getPath() + "//test30", stemmers));
		fulltextsMatrixes.add((ArrayList<String[]>) runTest(trainFolders, fullTextsDir.getPath() + "//test60", stemmers));
		
		Instant finish = Instant.now();
		
		int sortingIndex = 7;
		
		System.out.println("\n--- STRUCTURED TEST RESULTS ---");
		System.out.println("Models are sorted by: " + MauiPTUtils.header[sortingIndex]);
		System.out.println("\n- ABSTRACTS -");
		System.out.println("\n---> Test results based on 30 documents: ");
		MauiPTUtils.printMatrix(MauiPTUtils.sort(abstractsMatrixes.get(0), sortingIndex));
		System.out.println("\n---> Test results based on 60 documents: ");
		MauiPTUtils.printMatrix(MauiPTUtils.sort(abstractsMatrixes.get(1), sortingIndex));
		System.out.println("\n\n- FULLTEXTS -");
		System.out.println("\n---> Test results based on 30 documents: ");
		MauiPTUtils.printMatrix(MauiPTUtils.sort(fulltextsMatrixes.get(0), sortingIndex));
		System.out.println("\n---> Test results based on 60 documents: ");
		MauiPTUtils.printMatrix(MauiPTUtils.sort(fulltextsMatrixes.get(1), sortingIndex));
		
		System.out.print("Structured Test Duration: ");
		UI.showElapsedTime(start, finish);
	}
	
	public static void main(String[] args) {
		try {
			runAllTests();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
