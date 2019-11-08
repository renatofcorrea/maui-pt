package com.entopix.maui.utils;

import java.io.File;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.entopix.maui.stemmers.Stemmer;

/**
 * Provides useful methods to dealing with result matrixes, arrays and models.
 * @author Rahmon Jorge
 */
public class MauiPTUtils {
	
	/**
	 * Takes a model name and its test results, then formats it on a array of strings.
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
	
	public static String formatHeader(String[] header) {
		String h = "";
		h += String.format("%-65s", header[0]);
		int i;
		for (i = 1; i < header.length; i++) {
			h += String.format("%-20s", header[i]);
		}
		return h;
	}
	
	public static String matrixToString(String header, List<String[]> matrix) {
		String s = header + "\n";
		for(String[] model : matrix) {
			for(String value : model) {
				s += value;
			}
			s += "\n";
		}
		return s;
	}
	
	/**
	 * Generates a model name in the format "model_doctype_stemmerclass_traindir"
	 * @param trainDir
	 * @param stemmer
	 * @return The generated model name.
	 * @throws Exception When the training filepath of the model does not contain 'abstracts' or 'fulltexts' to generate the model name
	 */
	public static String generateModelName(String trainDir, Stemmer stemmer) {
		String name = "model_";
		
		if (trainDir.contains("abstracts")) {
			name += "abstracts_";
		} else if (trainDir.contains("fulltexts")) {
			name += "fulltexts_";
		} else {
			new Exception("The training filepath of the model does not contain 'abstracts' or 'fulltexts' to generate modelname").printStackTrace(); //TODO
		}
		
		name += stemmer.getClass().getSimpleName() + "_";
		
		name += new File(trainDir).getName();
		
		return name;
	}
	
	/** Sorts a List of String arrays according to the value in the specified index.
	 * The value is defined by the parseDouble method.*/
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
	
	/** Finds the Nth occurrence of a substring on a string. */
	public static int ordinalIndexOf(String str, String substr, int n) {
	    int pos = -1;
	    do {
	        pos = str.indexOf(substr, pos + 1);
	    } while (n-- > 0 && pos != -1);
	    return pos;
	}
	
	public static String elapsedTime(Instant start, Instant finish) {
		double seconds = (Duration.between(start, finish).toMillis()/1000);
		int minutes = (int) seconds/60;
		int remainingSec = (int) (seconds - (minutes*60));
		return minutes + " minutes and " + remainingSec + " seconds.";
	}
}
