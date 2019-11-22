package com.entopix.maui.utils;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.util.Topic;

/**
 * Provides useful methods to dealing with result matrixes, arrays and models.
 * @author Rahmon Jorge
 */
public class MauiPTUtils {
	
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
			new Exception("The training filepath of the model does not contain 'abstracts' or 'fulltexts' to generate modelname").printStackTrace();
		}
		
		name += stemmer.getClass().getSimpleName() + "_";
		
		name += new File(trainDir).getName();
		
		return name;
	}
	
	/** 
	 * Finds the Nth occurrence of a substring on a string.
	 */
	public static int ordinalIndexOf(String str, String substr, int n) {
	    int pos = -1;
	    do {
	        pos = str.indexOf(substr, pos + 1);
	    } while (n-- > 0 && pos != -1);
	    return pos;
	}
	
	/**
	 * Calculates the elapsed time between two instants.
	 * @param start
	 * @param finish
	 * @return string representation of elapsed time.
	 */
	public static String elapsedTime(Instant start, Instant finish) {
		double seconds = (Duration.between(start, finish).toMillis()/1000);
		int minutes = (int) seconds/60;
		int remainingSec = (int) (seconds - (minutes*60));
		return minutes + " minutes and " + remainingSec + " seconds.";
	}
	
	/**
	 * Converts a list of topics to a list of strings.
	 * @param topics
	 * @return
	 */
	public static List<String> topicsToString(List<Topic> topics) {
		List<String> strings = new ArrayList<String>();
		for (Topic t : topics) {
			strings.add(t.getTitle());
		}
		return strings;
	}
	
	/**
	 * Converts a list of maui topics into a list of string lists.
	 * @param topics
	 */
	public static List<List<String>> mauiTopicsToString(List<MauiTopics> topicsList) {
		
		List<List<String>> stringsList = new ArrayList<>();
		List<String> strings;
		
		for (MauiTopics mauiTopics : topicsList) {
			strings = topicsToString(mauiTopics.getTopics());
			stringsList.add(strings);
		}
		
		return stringsList;
	}

	/**
	 * Gets the Nth column of a matrix of doubles.
	 * @param
	 * @return
	 * @throws ArrayIndexOutOfBoundsException if there is a row without specified index.
	 */
	public static double[] getColumn(double[][] matrix, int index) {
		int rowCount = matrix.length;
		double[] column = new double[rowCount];
		
		int i;
		for (i = 0; i < rowCount; i++) {
			if (index >= matrix[i].length) throw new ArrayIndexOutOfBoundsException();
			column[i] = matrix[i][index];
		}
		
		return column;
	}
}
