package com.entopix.maui.utils;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.entopix.maui.stemmers.LuceneBRStemmer;
import com.entopix.maui.stemmers.LuceneRSLPMinimalStemmer;
import com.entopix.maui.stemmers.LuceneRSLPStemmer;
import com.entopix.maui.stemmers.LuceneSavoyStemmer;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stemmers.WekaStemmerOrengo;
import com.entopix.maui.stemmers.WekaStemmerPorter;
import com.entopix.maui.stemmers.WekaStemmerSavoy;

/**
 * Provides useful methods to dealing with result matrixes, arrays and models.
 * @author Rahmon Jorge
 */
public class MauiPTUtils {
	
	public static String[] header = {"MODEL NAME","AVG KEY","STDEV KEY","AVG PRECISION","STDEV PRECISION","AVG RECALL","STDEV RECALL","F-MEASURE"};
	
	/**
	 * Takes a model name and its test results, then formats it on a array of strings to be used on a result matrix.
	 * @param modelName
	 * @param arr An array containing the test results.
	 * @return The array in the format [model_name, testresult1, testresult2, ... , testresultN].
	 * Each test result value has been shortened to two decimal digits.
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
	
	/**
	 * Prints a formatted result matrix with a header.
	 * @param matrix
	 */
	public static void printMatrix(List<String[]> matrix) {
		//Builds and prints headers
		for (String word : header) {
			if (word.equals(header[0])) {
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
	
	/**
	 * Generates a model name in the format "model_doctype_stemmerclass_traindir"
	 * @param trainDir
	 * @param stemmer
	 * @return The generated model name.
	 * @throws Exception When the training filepath of the model does not contain 'abstracts' or 'fulltexts' to generate modelname
	 */
	public static String generateModelName(String trainDir, Stemmer stemmer) {
		String name = "model_";
		
		if (trainDir.contains("abstracts")) {
			name += "abstracts_";
		} else if (trainDir.contains("fulltexts")) {
			name += "fulltexts_";
		} else {
			new Exception("The training filepath of the model does not contain 'abstracts' or 'fulltexts' to generate modelname").printStackTrace();
		}
		
		name += stemmer.getClass().getSimpleName().toLowerCase() + "_";
		
		name += new File(trainDir).getName();
		
		return name;
	}
	
	public static List<String[]> sort(List<String[]> matrix, int sortingIndex) throws ParseException {
		
		List<String[]> sortedMatrix = new ArrayList<String[]>();
		
		double highestValue = Double.parseDouble(matrix.get(0)[sortingIndex].replace(",", "."));
		double current;
		int highestValueIndex = 0;
		
		while (!matrix.isEmpty()) {
			for (String[] line : matrix) {
				current  = Double.parseDouble(line[sortingIndex].replace(",", "."));
				if (current > highestValue) {
					highestValue = current;
					highestValueIndex = matrix.indexOf(line);
				}
			}
			sortedMatrix.add(matrix.get(highestValueIndex));
			matrix.remove(highestValueIndex);
			highestValue = 0;
		}
		return sortedMatrix;
	}
}