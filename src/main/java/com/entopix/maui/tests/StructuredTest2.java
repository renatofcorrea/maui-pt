package com.entopix.maui.tests;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.entopix.maui.core.MauiCore;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.utils.MauiFileUtils;
import com.entopix.maui.utils.MauiPTUtils;

public class StructuredTest2 {
	
	//Paths
	private static String dataPath = MauiFileUtils.getDataPath();
	private static String modelsPath = MauiFileUtils.getModelsDirPath();
	private static String testResultsPath = MauiFileUtils.getDataPath() + "\\tests";
	
	//Files
	private static File abstractsDir = new File(dataPath + "\\docs\\corpusci\\abstracts");
	private static File fullTextsDir = new File(dataPath + "\\docs\\corpusci\\fulltexts");
	
	//Others
	private static List<Table> fulltextsMatrixes, abstractsMatrixes;
	private static Instant start, finish;
	private static boolean sort, saveToFile;
	private static int sortIndex;
	
	private static String[] header = {"MODEL NAME","AVG KEY","STDEV KEY","AVG PRECISION","STDEV PRECISION","AVG RECALL","STDEV RECALL","F-MEASURE"};
	private static String[] tableFormat = {"%-65s","%-20s","%-20s","%-20s","%-20s","%-20s","%-20s","%-20s"};
	
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
				MauiCore.setupAndBuildModel(trainDir.getPath(), modelPath, stemmer);
				topics = MauiCore.setupAndRunTopicExtractor(modelPath, testDir, stemmer, false);
				result = MauiPTUtils.formatArray(modelName, MauiCore.classicEvaluateTopics(topics));
				list.add(result);
			}
		}
		return list;
	}
	
	public static void runAllTests() throws Exception {
		
		abstractsMatrixes = new ArrayList<Table>();
		fulltextsMatrixes = new ArrayList<Table>();
		File[] trainFolders = null;
		Stemmer[] stemmers = MauiCore.getStemmerList();
		
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
		
		if (sort) {
			for (Table t : abstractsMatrixes) t.sort(sortIndex, "double");
			for (Table t : fulltextsMatrixes) t.sort(sortIndex, "double");
		}
		
		String allResults = getResultString();
		System.out.println(allResults);
		
		if (saveToFile) {
			//creates file
			Date date = new Date();
			String dateString = new SimpleDateFormat("dd-MM-yyyy HHmm").format(date);
			String filePath = testResultsPath + "\\" + dateString + ".txt";
			
			//save file
			MauiFileUtils.printOnFile(allResults, filePath); //TODO: generating completely messed up file
		}
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
		s += "\nStructured Test Duration: " + MauiPTUtils.elapsedTime(start, finish);
		return s;
	}
	
	public static void main(String[] args) {
		sort = true;
		sortIndex = 7;
		saveToFile = true;
		try {
			runAllTests();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
