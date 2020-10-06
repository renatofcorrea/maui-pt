package com.entopix.maui.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.entopix.maui.core.MPTCore;
import com.entopix.maui.core.ModelWrapper;
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
	
	private static String dataPath = MauiFileUtils.getDataPath();
	private static String modelsPath = MauiFileUtils.getModelsDirPath() + "\\ST models";
	private static String resultsPath = dataPath + "\\tests\\";
	private static File abstractsDir = new File(dataPath + "\\docs\\corpusci\\abstracts");
	private static File fullTextsDir = new File(dataPath + "\\docs\\corpusci\\fulltexts");
	private static String abstracts30Path = abstractsDir.getPath() + "\\test30";
	private static String abstracts60Path = abstractsDir.getPath() + "\\test60";
	private static String fullTexts30Path = fullTextsDir.getPath() + "\\test30";
	private static String fullTexts60Path = fullTextsDir.getPath() + "\\test60";
	
	private static List<StringTable> fulltextsMatrixes, abstractsMatrixes;
	private static Instant start, finish;
	private static String elapsed;
	private static boolean sort = true;
	private static boolean saveCSVFile = true;
	private static boolean trainModels = false;
	private static int sortIndex = 7;
	private static int fullTextsMinOccur = 2;
	private static int abstractsMinOccur = 1;
	
	private static String[] header = {"MODEL NAME","AVG KEY","STDEV KEY","AVG PRECISION","STDEV PRECISION","AVG RECALL","STDEV RECALL","F-MEASURE"};
	private static String[] tableFormat = {"%-65s","%-20s","%-20s","%-20s","%-20s","%-20s","%-20s","%-20s"}; //spacing
	
	public static void setTestPaths(String abs30, String abs60, String ft30, String ft60) throws FileNotFoundException {
		if (!new File(abs30).exists()) throw new FileNotFoundException("The path " + abs30 + " was not found.");
		else abstracts30Path = abs30;
		if (!new File(abs60).exists()) throw new FileNotFoundException("The path " + abs60 + " was not found.");
		else abstracts60Path = abs60;
		if (!new File(ft30).exists()) throw new FileNotFoundException("The path " + ft30 + " was not found.");
		else fullTexts30Path = ft30;
		if (!new File(ft60).exists()) throw new FileNotFoundException("The path " + ft60 + " was not found.");
		else fullTexts60Path = ft60;
	}
	
	public static void setTestPaths(String[] paths) throws FileNotFoundException {
		setTestPaths(paths[0],paths[1],paths[2],paths[3]);
	}
	
	public static void setModelsDir(String modelsPath) {
		StructuredTest.modelsPath = modelsPath;
	}
	
	public static void setTrainModels(boolean trainModels) {
		StructuredTest.trainModels = trainModels;
	}
	
	public static void setSortingIndex(int sortingIndex) {
		sortIndex = sortingIndex;
	}
	
	public static void setSaveCSVFile(boolean save) {
		saveCSVFile = save;
	}
	
	public static void setFullTextsMinOccur(int fullTextsMinOccur) {
		StructuredTest.fullTextsMinOccur = fullTextsMinOccur;
	}

	public static void setAbstractsMinOccur(int abstractsMinOccur) {
		StructuredTest.abstractsMinOccur = abstractsMinOccur;
	}
	
	private static void buildModel(String trainDirPath, String modelPath, Stemmer stemmer) throws Exception {
		MPTCore.setTrainDirPath(trainDirPath);
		MPTCore.setModelPath(modelPath);
		MPTCore.setStemmer(stemmer);
		MPTCore.setupAndBuildModel();
	}
	
	private static ModelWrapper loadModel(String modelPath) {
		return (ModelWrapper) MauiFileUtils.deserializeObject(modelPath);
	}
	
	private static List<MauiTopics> runTopicExtractor(String modelPath, String testDirPath, Stemmer stemmer, boolean printTopics) throws Exception {
		MPTCore.setModelPath(modelPath);
		MPTCore.loadModel(modelPath);
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
	
	/** @return A matrix where each line contains model name and its test results. 
	 *  @param testDir The directory where the tests will be conduced. 
	 *  */
	public static List<String[]> runTests(File[] trainFolders, String testDir, String modelsDir, Stemmer[] stemmers) throws Exception {
		List<String[]> list = new ArrayList<String[]>();
		String[] result;
		String modelName, modelPath;
		List<MauiTopics> topics;
		
		if (trainModels) {
			for (Stemmer stemmer : stemmers) {
				for (File trainDir : trainFolders) {
					//Build Model
					modelName = MPTUtils.generateModelName(trainDir.getPath(), stemmer);
					modelPath = modelsPath + "\\ST models\\" + modelName;
					buildModel(trainDir.getPath(), modelPath, stemmer);

					//Test Model
					topics = runTopicExtractor(modelPath, testDir, stemmer, false);
					result = formatArray(modelName, MPTCore.classicEvaluateTopics(topics));
					list.add(result);
				}
			}
		} else {
			String[] modelPaths = null;
			if (testDir.contains("fulltexts")) modelPaths = MauiFileUtils.getFileListPaths(MauiFileUtils.filterDir(modelsDir, "fulltexts"));
			else if (testDir.contains("abstracts")) modelPaths = MauiFileUtils.getFileListPaths(MauiFileUtils.filterDir(modelsDir, "abstracts"));
			
			ModelWrapper model = null;
			for (String modPath : modelPaths) {
				model = loadModel(modPath);
				topics = runTopicExtractor(modPath, testDir, model.getStemmerUsed(), false);
				result = formatArray(new File(modPath).getName(), MPTCore.classicEvaluateTopics(topics));
				list.add(result);
			}
		}
		
		return list;
	}
	
	public static void runAllTests() throws Exception {
		
		// INITIALIZE
		abstractsMatrixes = new ArrayList<StringTable>();
		fulltextsMatrixes = new ArrayList<StringTable>();
		File[] trainFolders = null; //folders where the models WERE TRAINED.
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
		MPTCore.setMinOccur(fullTextsMinOccur);
		if (trainModels) trainFolders = MauiFileUtils.filterFileList(fullTextsDir.listFiles(), "train");
		List<String[]> results = null;
		results = runTests(trainFolders, fullTexts30Path, modelsPath, stemmers);
		fulltextsMatrixes.add(new StringTable(header, results, tableFormat));
		results = runTests(trainFolders, fullTexts60Path, modelsPath, stemmers);
		fulltextsMatrixes.add(new StringTable(header, results, tableFormat));
		
		// ABSTRACTS
		MPTCore.setMinOccur(abstractsMinOccur); //Set to abstract models only
		if (trainModels) trainFolders = MauiFileUtils.filterFileList(abstractsDir.listFiles(), "train");
		results = runTests(trainFolders, abstracts30Path, modelsPath, stemmers);
		abstractsMatrixes.add(new StringTable(header, results, tableFormat));
		results = runTests(trainFolders, abstracts60Path, modelsPath, stemmers);
		abstractsMatrixes.add(new StringTable(header, results, tableFormat));
		
		MPTCore.setMinOccur(2); //Must be set back to standard
		
		finish = Instant.now();
		elapsed = MPTUtils.elapsedTime(start, finish);
		
		if (sort) {
			for (StringTable t : abstractsMatrixes) t.sort(sortIndex, "double");
			for (StringTable t : fulltextsMatrixes) t.sort(sortIndex, "double");
		}
		
		System.out.println(getResultString());
		
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
		try {
			runAllTests();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
