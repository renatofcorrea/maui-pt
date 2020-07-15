package com.entopix.maui.tests;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.entopix.maui.core.MPTCore;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.stemmers.LuceneBRStemmer;
import com.entopix.maui.stemmers.LuceneRSLPMinimalStemmer;
import com.entopix.maui.stemmers.LuceneRSLPStemmer;
import com.entopix.maui.stemmers.LuceneSavoyStemmer;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stemmers.WekaStemmerOrengo;
import com.entopix.maui.stemmers.WekaStemmerPorter;
import com.entopix.maui.stemmers.WekaStemmerSavoy;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.utils.MPTUtils;
import com.entopix.maui.utils.MauiFileUtils;
import com.entopix.maui.utils.StringTable;

public class StructuredTest {
	
	//Paths
	private static String dataPath = MauiFileUtils.getDataPath();
	private static String modelsPath = MauiFileUtils.getModelsDirPath();
	private static String resultsPath = dataPath + "tests\\";
	
	//Files
	private static File abstractsDir = new File(dataPath + "\\docs\\corpusci\\abstracts");
	private static File fullTextsDir = new File(dataPath + "\\docs\\corpusci\\fulltexts");
	
	
	private static List<StringTable> fulltextsMatrixes, abstractsMatrixes;
	private static Instant start, finish;
	private static String elapsed;
	private static boolean sort;
	private static boolean saveCSVFile = true;
	private static int sortIndex;
	
	private static String[] header = {"MODEL NAME","AVG KEY","STDEV KEY","AVG PRECISION","STDEV PRECISION","AVG RECALL","STDEV RECALL","F-MEASURE"};
	private static String[] tableFormat = {"%-65s","%-20s","%-20s","%-20s","%-20s","%-20s","%-20s","%-20s"};
	
	private static void buildModel(String trainDirPath, String modelPath, Stemmer stemmer) throws Exception {
		MPTCore.setTrainDirPath(trainDirPath);
		MPTCore.setModelPath(modelPath);
		MPTCore.setStemmer(stemmer);
		MPTCore.setupAndBuildModel();
	}
	
	private static List<MauiTopics> runTopicExtractor(String modelPath, String testDirPath, Stemmer stemmer, boolean printTopics) throws MauiFilterException {
		MPTCore.setModelPath(modelPath);
		MPTCore.setTestDirPath(testDirPath);
		MPTCore.setStemmer(stemmer);
		MPTCore.setPrintExtractedTopics(printTopics);
		try {
			return MPTCore.runTopicExtractor();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String[] formatArray(String modelName, double[] arr) {
		String[] s = new String[arr.length + 1];
		s[0] = modelName;
		for (int i = 0; i < arr.length; i++) {
			s[i+1] = String.format("%.2f", arr[i]);
		}
		return s;
	}
	
	/** @return A List where each line contains model name and its test results. */
	public static List<String[]> runTest(File[] trainFolders, String testDir, Stemmer[] stemmers) throws Exception {
		List<String[]> list = new ArrayList<String[]>();
		String[] result;
		String modelName, modelPath;
		List<MauiTopics> topics;
		for (Stemmer stemmer : stemmers) {
			for (File trainDir : trainFolders) {
				modelName = MPTUtils.generateModelName(trainDir.getPath(), stemmer);
				modelPath = modelsPath + "\\" + modelName;
				buildModel(trainDir.getPath(), modelPath, stemmer);
				topics = runTopicExtractor(modelPath, testDir, stemmer, false);
				result = formatArray(modelName, MPTCore.classicEvaluateTopics(topics));
				list.add(result);
			}
		}
		return list;
	}

	
	public static void runAllTests() throws Exception {
		
		abstractsMatrixes = new ArrayList<StringTable>();
		fulltextsMatrixes = new ArrayList<StringTable>();
		File[] trainFolders = null;
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
		
		start = Instant.now();
		
		// FULLTEXTS
		trainFolders = MauiFileUtils.filterFileList(fullTextsDir.listFiles(), "train");
		List<String[]> results = null;
		results = runTest(trainFolders, fullTextsDir.getPath() + "\\test30", stemmers);
		fulltextsMatrixes.add(new StringTable(header, results, tableFormat));
		results = runTest(trainFolders, fullTextsDir.getPath() + "\\test60", stemmers);
		fulltextsMatrixes.add(new StringTable(header, results, tableFormat));
		
		// ABSTRACTS
		MPTCore.setMinOccur(1); //Set to abstract models only
		trainFolders = MauiFileUtils.filterFileList(abstractsDir.listFiles(), "train");
		results = runTest(trainFolders, abstractsDir.getPath() + "\\test30", stemmers);
		abstractsMatrixes.add(new StringTable(header, results, tableFormat));
		results = runTest(trainFolders, abstractsDir.getPath() + "\\test60", stemmers);
		abstractsMatrixes.add(new StringTable(header, results, tableFormat));
		
		MPTCore.setMinOccur(2); //Must be set back to standard
		
		finish = Instant.now();
		elapsed = MPTUtils.elapsedTime(start, finish);
		
		if (sort) {
			for (StringTable t : abstractsMatrixes) t.sort(sortIndex, "double");
			for (StringTable t : fulltextsMatrixes) t.sort(sortIndex, "double");
		}
		
		String allResults = getResultString();
		System.out.println(allResults);
		
		if (saveCSVFile) {
			String date = MPTUtils.getTimeAndDate();
			abstractsMatrixes.get(0).exportAsCSV(resultsPath + "abstracts30_" + date + ".csv");
			abstractsMatrixes.get(0).exportAsCSV(resultsPath + "abstracts60_" + date + ".csv");
			fulltextsMatrixes.get(1).exportAsCSV(resultsPath + "fulltexts30_" + date + ".csv");
			fulltextsMatrixes.get(1).exportAsCSV(resultsPath + "fulltexts60_" + date + ".csv");
		}
	}
	
	public static String getResultString() {
		String s = "--- STRUCTURED TEST RESULTS ---\n";
		s += "\n--- ABSTRACTS ---\n";
		s += "\n>>> Results based on 30 documents:\n";
		s += abstractsMatrixes.get(0).tableToFormattedString();
		s += "\n>>> Results based on 60 documents:\n";
		s += abstractsMatrixes.get(1).tableToFormattedString();
		s += "\n";
		s += "\n--- FULL TEXTS ---\n";
		s += "\n>>> Results based on 30 documents:\n";
		s += fulltextsMatrixes.get(0).tableToFormattedString();
		s += "\n>>> Results based on 60 documents:\n";
		s += fulltextsMatrixes.get(1).tableToFormattedString();
		s += "\nStructured Test Duration: " + elapsed;
		return s;
	}
	
	public static void main(String[] args) {
		sort = true;
		sortIndex = 8;
		saveCSVFile = true;
		try {
			runAllTests();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
