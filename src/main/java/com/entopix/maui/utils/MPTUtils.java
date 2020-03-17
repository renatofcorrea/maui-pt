package com.entopix.maui.utils;

import java.io.File;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.entopix.maui.core.MauiCore;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.util.Topic;

import weka.core.Utils;

/**
 * Provides useful methods to dealing with models, result matrixes, arrays and strings.
 * @author Rahmon Jorge
 */
//TODO: organize method order and description in this class
public class MPTUtils {
	
	/**
	 * Generates a model name in the format "model_doctype_stemmerclass_traindir".
	 * Throws a Exception when the path of the training directory of the model does not contain 'abstracts' or 'fulltexts' to generate the model name.
	 * @param trainDir
	 * @param stemmer
	 * @return
	 * @throws Exception
	 */
	public static String generateModelName(String trainDir, Stemmer stemmer) {
		String name = "model_";
		
		if (trainDir.contains("abstracts")) {
			name += "abstracts_";
		} else if (trainDir.contains("fulltexts")) {
			name += "fulltexts_";
		} else {
			new Exception("The path of the training directory of the model does not contain 'abstracts' or 'fulltexts' to generate the model name").printStackTrace();
		}
		
		name += stemmer.getClass().getSimpleName() + "_";
		name += new File(trainDir).getName();
		
		return name;
	}
	
	/**
	 * Returns a Stemmer instance based on the name of the model.
	 * The model name has to be in the format model_modeltype_stemmer_traindir.
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static Stemmer getStemmerFromModelName(File model) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		int start = ordinalIndexOf(model.getName(), "_", 1);
		int end = ordinalIndexOf(model.getName(), "_", 2);
		String stemmerName = MauiCore.getStemmersPackage() + model.getName().substring(start + 1, end);
		return (Stemmer) Class.forName(stemmerName).newInstance();
	}
	
	/**
	 * Finds the Nth occurrence of a substring on a string. (n = 0 for fist occurrence)
	 * @param str
	 * @param substr
	 * @param n
	 * @return
	 */
	public static int ordinalIndexOf(String str, String substr, int n) {
	    int pos = -1;
	    do {
	        pos = str.indexOf(substr, pos + 1);
	    } while (n-- > 0 && pos != -1);
	    return pos;
	}
	
	/**
	 * Returns true if, and only if, length of string s != 0 and s != null.
	 * @param s
	 * @return
	 */
	public static boolean isValid(String s) {
		return s != null && !s.isEmpty();
	}
	
	/**
	 * Returns a string representation of elapsed time between two instants.
	 * @param start
	 * @param finish
	 * @return string
	 */
	public static String elapsedTime(Instant start, Instant finish) {
		double seconds = (Duration.between(start, finish).toMillis()/1000);
		int minutes = (int) seconds/60;
		int remainingSec = (int) (seconds - (minutes*60));
		return minutes + " minutes and " + remainingSec + " seconds.";
	}
	
	/**
	 * Converts a list of Topic objects to a array of strings.
	 * @param topics
	 * @return
	 */
	public static String[] topicsToString(List<Topic> topics) {
		String[] strings = new String[topics.size()];
		int i;
		for (i = 0; i < topics.size(); i++) {
			strings[i] = topics.get(i).getTitle();
		}
		return strings;
	}

	/**
	 * Converts a list of MauiTopics to a list of arrays of strings.
	 * @param topicsList
	 * @return
	 */
	public static List<String[]> mauiTopicsToString(List<MauiTopics> topicsList) {
		List<String[]> strList = new ArrayList<>();
		String[] strings;
		
		for (MauiTopics mt : topicsList) {
			strings = topicsToString(mt.getTopics());
			strList.add(strings);
		}
		
		return strList;
	}
	
	/**
	 * Converts a string array of topics from specified document to a MauiTopics object.
	 * @param topics
	 * @param documentPath
	 * @return
	 */
	public static MauiTopics stringArrayToMauiTopics(String[] topics, String documentPath) {
		MauiTopics mt = new MauiTopics(documentPath);
		int i;
		for (i = 0; i < topics.length; i++) {
			mt.addTopic(new Topic(topics[i]));
		}
		
		return mt;
	}
	
	/**
	 * Gets the Nth column in a matrix of doubles.
	 * Throws a exception if there is a row without specified index.
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
	
	/**
	 * Removes the diacritics of a string.
	 * @param s
	 * @return
	 */
	public static String stripAccents(String s) {
	    s = Normalizer.normalize(s, Normalizer.Form.NFD);
	    s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
	    return s;
	}
	
	/**
	 * Removes the file extension of a string (all characters after last index of '.' character).
	 * @param s
	 * @return
	 */
	public static String removeFileExtension(String s) {
		int index = s.lastIndexOf(".");
		if (index > 0) {
			return s.substring(0, index);
		}
		return null;
	}
	
	//TODO: now in Matrix class
	/**
	 * Takes a list of arrays and returns it as a list with their sizes.
	 * @param matrix
	 * @return
	 */
	public static List<Integer> elementSizes(List<String[]> matrix) {
		List<Integer> sizes = new ArrayList<Integer>();
		
		for (String[] line : matrix) {
			sizes.add(line.length);
		}
		
		return sizes;
	}
	
	/**
	 * Returns a string with the current date as dd-MM format.
	 * @return
	 */
	public static String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM");
		return sdf.format(new Date(System.currentTimeMillis()));
	}
	

	/**
	 * Calculates precision, recall, f-measure and consistency and returns them as a array of doubles.
	 * @param extracted
	 * @param manual
	 * @param matches
	 * @param roundValues
	 * @return
	 */
	public static double[] calculateMeasures(int extracted, int manual, int matches, boolean roundValues) {
		double precision, recall, fMeasure, consistency;
		
		precision = (double) matches / extracted;
		recall = (double) matches / manual;
		fMeasure = (double) 2 / ( (1/precision) + (1/recall) );
		consistency = (double) matches / (extracted + manual - matches);
		
		if (roundValues) {
			precision = Utils.roundDouble(precision, 4);
			recall = Utils.roundDouble(recall, 4);
			fMeasure = Utils.roundDouble(fMeasure, 4);
			consistency = Utils.roundDouble(consistency, 4);
		}
		
		return new double[] {precision, recall, fMeasure, consistency};
	}
	
	/**
	 * Converts a list of Integer objects to a list of Double objects.
	 * @param list
	 * @return
	 */
	public static List<Double> integerListToDoubleList(List<Integer> list) {
		List<Double> dList = new ArrayList<>();
		for (Integer i : list) {
			dList.add(i.doubleValue());
		}
		return dList;
	}
	
	public static double mean(List<Double> list) {
		double sum = 0;
		if (!list.isEmpty()) {
			for (Double d : list) {
				sum += d;
			}
			return sum / list.size();
		}
		return sum;
	}
	
	public static double stdDevMaui(List<Double> list) {
		//Variance
		double sum = 0, sumSquared = 0;
		for (Double d : list) {
			sum += d;
			sumSquared += d * d;
		}
		double result = (sumSquared - (sum * sum / list.size()) / (list.size() - 1));
		result = (result < 0 ? 0 : result);
		
		//Standard Deviation
		return Math.sqrt(result);
	}
	
	/**
	 * Calculates the standard deviation.
	 * @param list
	 * @return
	 */
	public static double stdDev(List<Double> list) {
		//Variance
		double mean = mean(list);
		double sum = 0;
		
		for (Double d : list) {
			sum += ((d - mean) * (d - mean));
		}
		
		double result = sum / list.size();
		
		//Standard Deviation
		return Math.sqrt(result);
	}
	
	public static List<Integer> intArrayToIntegerList(int[] arr) {
		List<Integer> list = new ArrayList<Integer>(arr.length);
		for (int i : arr){
		    list.add(i);
		}
		return list;
	}
}
