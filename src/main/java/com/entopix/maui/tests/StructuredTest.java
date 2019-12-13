package com.entopix.maui.tests;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.entopix.maui.core.MauiCore;
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
import com.entopix.maui.utils.MauiFileUtils;
import com.entopix.maui.utils.MauiPTUtils;

public class StructuredTest {
	
	//Paths
	private static String dataPath = MauiFileUtils.getDataPath();
	private static String modelsPath = MauiFileUtils.getModelsDirPath();
	
	//Files
	private static File abstractsDir = new File(dataPath + "\\docs\\corpusci\\abstracts");
	private static File fullTextsDir = new File(dataPath + "\\docs\\corpusci\\fulltexts");
	
	//Others
	private static List<Table> fulltextsMatrixes, abstractsMatrixes;
	private static Instant start, finish;
	private static String elapsed;
	private static boolean sort;
	private static int sortIndex;
	
	private static String[] header = {"MODEL NAME","AVG KEY","STDEV KEY","AVG PRECISION","STDEV PRECISION","AVG RECALL","STDEV RECALL","F-MEASURE"};
	private static String[] tableFormat = {"%-65s","%-20s","%-20s","%-20s","%-20s","%-20s","%-20s","%-20s"};
	
	private static void buildModel(String trainDirPath, String modelPath, Stemmer stemmer) throws Exception {
		MauiCore.setTrainDirPath(trainDirPath);
		MauiCore.setModelPath(modelPath);
		MauiCore.setStemmer(stemmer);
		MauiCore.buildModel();
	}
	
	private static List<MauiTopics> runTopicExtractor(String modelPath, String testDirPath, Stemmer stemmer, boolean printTopics) throws MauiFilterException {
		MauiCore.setModelPath(modelPath);
		MauiCore.setTestDirPath(testDirPath);
		MauiCore.setStemmer(stemmer);
		MauiCore.setPrintExtractedTopics(printTopics);
		try {
			return MauiCore.runTopicExtractor();
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
				modelName = MauiPTUtils.generateModelName(trainDir.getPath(), stemmer);
				modelPath = modelsPath + "\\" + modelName;
				buildModel(trainDir.getPath(), modelPath, stemmer);
				topics = runTopicExtractor(modelPath, testDir, stemmer, false);
				result = formatArray(modelName, MauiCore.classicEvaluateTopics(topics));
				list.add(result);
			}
		}
		return list;
	}
	
	public static void runAllTests() throws Exception {
		
		abstractsMatrixes = new ArrayList<Table>();
		fulltextsMatrixes = new ArrayList<Table>();
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
		
		trainFolders = MauiFileUtils.filterFileList(fullTextsDir.listFiles(), "train");
		fulltextsMatrixes.add(new Table(header, runTest(trainFolders, fullTextsDir.getPath() + "\\test30", stemmers), tableFormat));
		fulltextsMatrixes.add(new Table(header, runTest(trainFolders, fullTextsDir.getPath() + "\\test60", stemmers), tableFormat));
		
		MauiCore.setMinOccur(1); //Set to abstract models only
		
		trainFolders = MauiFileUtils.filterFileList(abstractsDir.listFiles(), "train");
		abstractsMatrixes.add(new Table(header, runTest(trainFolders, abstractsDir.getPath() + "\\test30", stemmers), tableFormat));
		abstractsMatrixes.add(new Table(header, runTest(trainFolders, abstractsDir.getPath() + "\\test60", stemmers), tableFormat));
		
		MauiCore.setMinOccur(2);
		
		finish = Instant.now();
		elapsed = MauiPTUtils.elapsedTime(start, finish);
		
		if (sort) {
			for (Table t : abstractsMatrixes) t.sort(sortIndex, "double");
			for (Table t : fulltextsMatrixes) t.sort(sortIndex, "double");
		}
		
		String allResults = getResultString();
		System.out.println(allResults);
	}
	
	public static String getResultString() {
		String s = "--- STRUCTURED TEST RESULTS ---\n";
		s += "\n--- ABSTRACTS ---\n";
		s += "\n>>> Results based on 30 documents:\n";
		s += abstractsMatrixes.get(0).tableToString();
		s += "\n>>> Results based on 60 documents:\n";
		s += abstractsMatrixes.get(1).tableToString();
		s += "\n";
		s += "\n--- FULL TEXTS ---\n";
		s += "\n>>> Results based on 30 documents:\n";
		s += fulltextsMatrixes.get(0).tableToString();
		s += "\n>>> Results based on 60 documents:\n";
		s += fulltextsMatrixes.get(1).tableToString();
		s += "\nStructured Test Duration: " + elapsed;
		return s;
	}
	
	public static void main(String[] args) {
		sort = true;
		sortIndex = 7;
		try {
			runAllTests();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
