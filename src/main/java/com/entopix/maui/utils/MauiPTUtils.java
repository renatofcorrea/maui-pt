package com.entopix.maui.utils;

import java.io.File;
import java.util.List;

import com.entopix.maui.stemmers.Stemmer;

public class MauiPTUtils {
	
	/**
	 * 
	 * @param modelName
	 * @param arr An array containing the test results.
	 * @return A array in the format [model_name, testresult1, testresult2, ... , testresultN]. Each
	 * test result value has been shortened to two decimal digits.
	 */
	public static String[] formatArray(String modelName, double[] arr) {
		String[] s = new String[arr.length + 1];
		s[0] = String.format("%-65s", modelName);
		int i;
		for (i = 0; i < arr.length; i++) {
			s[i+1] = String.format("%.2f", arr[i]);
			s[i+1] = String.format("%-20s", s[i+1]);
		}
		return s;
	}
	
	public static void printMatrix(List<String[]> matrix) {
		//Builds and prints header
		String[] header = {"MODEL NAME","AVG KEY","STDEV KEY","AVG PRECISION","STDEV PRECISION","AVG RECALL","STDEV RECALL","F-MEASURE"};
		for(String word : header) {
			if(word.equals(header[0])) {
				System.out.format("%-65s",word);
			} else {
				System.out.format("%-20s", word);
			}
		}
		System.out.println();
		
		//Prints matrix values
		for(String[] model : matrix) {
			for(String value : model) {
				System.out.print(value);
			}
			System.out.println();
		}
	}
	
	public static String generateModelName(String trainDir, Stemmer stemmer) throws Exception {
		String name = "model_";
		
		if (trainDir.contains("abstracts")) {
			name += "abstracts_";
		} else if (trainDir.contains("fulltexts")) {
			name += "fulltexts_";
		} else {
			throw new Exception("Model training directory does not contain clues to generate a model name");
		}
		
		name += stemmer.getClass().getSimpleName().toLowerCase() + "_";
		
		name += new File(trainDir).getName();
		
		return name;
	}
}
