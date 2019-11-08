package com.entopix.maui.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
	private static List<ArrayList<String[]>> abstractsMatrixes, fulltextsMatrixes;
	private static Instant start, finish;
	private static boolean sort, saveToFile;
	private static int sortIndex;
	
	public static String[] header;
	
	/** @return A List where each line contains a model name and its test results. */
	public static List<String[]> runTest(File[] trainFolders, String testDir, Stemmer[] stemmers) throws Exception {
		List<String[]> matrix = new ArrayList<String[]>();
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
				
				matrix.add(result);
			}
		}
		return matrix;
	}
	
	public static void runAllTests() throws Exception {
		
		abstractsMatrixes = new ArrayList<ArrayList<String[]>>();
		fulltextsMatrixes = new ArrayList<ArrayList<String[]>>();
		File[] trainFolders = null;
		Stemmer[] stemmers = MauiCore.getStemmerList();
		
		start = Instant.now();
		
		trainFolders = MauiFileUtils.filterFileList(fullTextsDir.listFiles(), "train");
		fulltextsMatrixes.add((ArrayList<String[]>) runTest(trainFolders, fullTextsDir.getPath() + "\\test30", stemmers));
		fulltextsMatrixes.add((ArrayList<String[]>) runTest(trainFolders, fullTextsDir.getPath() + "\\test60", stemmers));
		
		MauiCore.setMinOccur(1); //Set to abstract models only
		
		trainFolders = MauiFileUtils.filterFileList(abstractsDir.listFiles(), "train");
		abstractsMatrixes.add((ArrayList<String[]>) runTest(trainFolders, abstractsDir.getPath() + "\\test30", stemmers));
		abstractsMatrixes.add((ArrayList<String[]>) runTest(trainFolders, abstractsDir.getPath() + "\\test60", stemmers));
		
		MauiCore.setMinOccur(2);
		
		finish = Instant.now();
		
		if (sort) {
			abstractsMatrixes.set(0, (ArrayList<String[]>) MauiPTUtils.sort(abstractsMatrixes.get(0), sortIndex));
			abstractsMatrixes.set(1, (ArrayList<String[]>) MauiPTUtils.sort(abstractsMatrixes.get(1), sortIndex));
			fulltextsMatrixes.set(0, (ArrayList<String[]>) MauiPTUtils.sort(fulltextsMatrixes.get(0), sortIndex));
			fulltextsMatrixes.set(1, (ArrayList<String[]>) MauiPTUtils.sort(fulltextsMatrixes.get(1), sortIndex));
		}
		
		String results = getResultString();
		System.out.println(results);
		
		if (saveToFile) {
			Date date = new Date();
			String dateString = new SimpleDateFormat("dd-MM-yyyy HHmm").format(date);
			String filePath = testResultsPath + "\\" + dateString + ".txt";
			
			MauiFileUtils.printOnFile(results, filePath); //TODO: generates completely messed up file
		}
	}
	
	public static String getResultString() {
		header = new String[] {"MODEL NAME","AVG KEY","STDEV KEY","AVG PRECISION","STDEV PRECISION","AVG RECALL","STDEV RECALL","F-MEASURE"};
		String h = MauiPTUtils.formatHeader(header);
		
		String s = "--- STRUCTURED TEST RESULTS ---\n";
		s += "\n--- ABSTRACTS ---\n";
		s += "\n>>> Results based on 30 documents:\n";
		s += MauiPTUtils.matrixToString(h, abstractsMatrixes.get(0));
		s += "\n>>> Results based on 60 documents:\n";
		s += MauiPTUtils.matrixToString(h, abstractsMatrixes.get(1));
		s += "\n";
		s += "\n--- FULL TEXTS ---\n";
		s += "\n>>> Results based on 30 documents:\n";
		s += MauiPTUtils.matrixToString(h, fulltextsMatrixes.get(0));
		s += "\n>>> Results based on 60 documents:\n";
		s += MauiPTUtils.matrixToString(h, fulltextsMatrixes.get(1));
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
