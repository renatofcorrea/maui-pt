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
				MauiCore.setupAndBuildModel(modelPath, trainDir.getPath(), stemmer);
				topics = MauiCore.setupAndRunTopicExtractor(modelPath, testDir, stemmer, false);
				result = MauiPTUtils.formatArray(modelName, MauiCore.classicEvaluateTopics(topics));
				
				matrix.add(result);
			}
		}
		return matrix;
	}
	
	public static void runAllTests(boolean save, boolean sort) throws Exception {

		Instant start = Instant.now();
		
		File[] trainFolders = null;
		List<ArrayList<String[]>> abstractsMatrixes = new ArrayList<ArrayList<String[]>>();
		List<ArrayList<String[]>> fulltextsMatrixes = new ArrayList<ArrayList<String[]>>();
		Stemmer[] stemmers = MauiCore.getStemmerList();
		
		trainFolders = MauiFileUtils.filterFileList(abstractsDir.listFiles(), "train");
		abstractsMatrixes.add((ArrayList<String[]>) runTest(trainFolders, abstractsDir.getPath() + "//test30", stemmers));
		abstractsMatrixes.add((ArrayList<String[]>) runTest(trainFolders, abstractsDir.getPath() + "//test60", stemmers));
		
		trainFolders = MauiFileUtils.filterFileList(fullTextsDir.listFiles(), "train");
		fulltextsMatrixes.add((ArrayList<String[]>) runTest(trainFolders, fullTextsDir.getPath() + "//test30", stemmers));
		fulltextsMatrixes.add((ArrayList<String[]>) runTest(trainFolders, fullTextsDir.getPath() + "//test60", stemmers));
		
		Instant finish = Instant.now();
		
		int sortingIndex = 7;
		if (sort) {
			abstractsMatrixes.set(0, (ArrayList<String[]>) MauiPTUtils.sort(abstractsMatrixes.get(0), sortingIndex));
			abstractsMatrixes.set(1, (ArrayList<String[]>) MauiPTUtils.sort(abstractsMatrixes.get(1), sortingIndex));
			fulltextsMatrixes.set(0, (ArrayList<String[]>) MauiPTUtils.sort(fulltextsMatrixes.get(0), sortingIndex));
			fulltextsMatrixes.set(1, (ArrayList<String[]>) MauiPTUtils.sort(fulltextsMatrixes.get(1), sortingIndex));
		}
		
		System.out.println("\n--- STRUCTURED TEST RESULTS ---");
		if (sort) System.out.println("Models are sorted by: " + MauiPTUtils.header[sortingIndex]);
		System.out.println("\n- ABSTRACTS -");
		System.out.println("\n---> Test results based on 30 documents: ");
		MauiPTUtils.printMatrix(abstractsMatrixes.get(0));
		System.out.println("\n---> Test results based on 60 documents: ");
		MauiPTUtils.printMatrix(abstractsMatrixes.get(1));
		System.out.println("\n\n- FULLTEXTS -");
		System.out.println("\n---> Test results based on 30 documents: ");
		MauiPTUtils.printMatrix(fulltextsMatrixes.get(0));
		System.out.println("\n---> Test results based on 60 documents: ");
		MauiPTUtils.printMatrix(fulltextsMatrixes.get(1));
		
		String elapsed = MauiPTUtils.elapsedTime(start, finish);
		
		System.out.print("Structured Test Duration: " + elapsed);
		
		if (save) {
			saveToFile(abstractsMatrixes, fulltextsMatrixes, elapsed, sortingIndex);
		}
		
	}
	
	private static void printOnFile(List<String[]> matrix, PrintWriter writer) {
		//Builds and prints headers
		String[] header = {"MODEL NAME","AVG KEY","STDEV KEY","AVG PRECISION","STDEV PRECISION","AVG RECALL","STDEV RECALL","F-MEASURE"};
		for (String word : header) {
			if (word.equals(header[0])) {
				writer.printf("%-65s",word);
			} else {
				writer.printf("%-20s", word);
			}
		}
		writer.println();
		
		//Prints matrix values
		for(String[] model : matrix) {
			for(String value : model) {
				writer.print(value);
			}
			writer.println();
		}
	}
	
	private static void saveToFile(List<ArrayList<String[]>> abstracts, List<ArrayList<String[]>> fulltexts, String elapsedTime, int sortingIndex) throws IOException {
		//creating file
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HHmm");
		String dateString = formatter.format(date);
		File file = new File(testResultsPath + "\\" + dateString + ".txt");
		
		//writing to file
		PrintWriter pw = new PrintWriter(new FileWriter(file));
		pw.println("\n--- STRUCTURED TEST RESULTS ---\n");
		pw.println("Models are sorted by: " + MauiPTUtils.header[sortingIndex]);
		pw.println("\n- ABSTRACTS -");
		pw.println("\n---> Test results based on 30 documents: ");
		printOnFile(abstracts.get(0), pw);
		pw.println("\n---> Test results based on 60 documents: ");
		printOnFile(abstracts.get(1), pw);
		pw.println("\n\n- FULLTEXTS -");
		pw.println("\n---> Test results based on 30 documents: ");
		printOnFile(fulltexts.get(0), pw);
		pw.println("\n---> Test results based on 60 documents: ");
		printOnFile(fulltexts.get(1), pw);
		
		pw.print("Structured Test Duration: " + elapsedTime);
		pw.close();
	}
	
	public static void main(String[] args) {
		boolean saveResults = true;
		boolean sort = true;
		try {
			runAllTests(saveResults, sort);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
