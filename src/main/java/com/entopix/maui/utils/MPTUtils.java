package com.entopix.maui.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.entopix.maui.core.MPTCore;
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
		String stemmerName = MPTCore.getStemmersPackage() + model.getName().substring(start + 1, end);
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
	 * Converts a list of Topic objects into a array of strings.
	 * @param topics
	 * @return
	 */
	public static String[] topicsToStringArray(List<Topic> topics) {
		String[] strings = new String[topics.size()];
		int i;
		for (i = 0; i < topics.size(); i++) {
			strings[i] = topics.get(i).getTitle();
		}
		return strings;
	}
	
	/**
	 * Converts a list of MauiTopics objects into a list of string arrays.
	 * @param topicsList
	 * @return
	 */
	public static List<String[]> mauiTopicsToListofStringArrays(List<MauiTopics> topicsList) {
		List<String[]> strings = new ArrayList<>();
		List<Topic> topics;
		int doc;
		for (doc = 0; doc < topicsList.size(); doc++) {
			topics = topicsList.get(doc).getTopics();
			strings.add(topicsToStringArray(topics));
		}
		return strings;
	}

	/**
	 * Converts a list of MauiTopics to a list of arrays of strings.
	 * @param topicsList
	 * @return
	 */
	public static List<String[]> mauiTopicsToStringMatrix(List<MauiTopics> topicsList) {
		List<String[]> strList = new ArrayList<>();
		String[] strings;
		
		for (MauiTopics mt : topicsList) {
			strings = topicsToStringArray(mt.getTopics());
			strList.add(strings);
		}
		
		return strList;
	}
	
	/**
	 * Converts a string array of topics from specified document to a MauiTopics object.
	 * @param topics
	 * @param sourceDirPath the source dir.
	 * @return
	 */
	public static MauiTopics stringArrayToMauiTopics(String[] topics, String sourceDirPath) {
		MauiTopics mt = new MauiTopics(sourceDirPath);
		int i;
		for (i = 0; i < topics.length; i++) {
			mt.addTopic(new Topic(topics[i]));
		}
		return mt;
	}
	
	/**
	 * Converts a list of string topics into a list of maui topics.
	 * @param topicsList
	 * @param sourceDirPath the source dir.
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static List<MauiTopics> stringMatrixToMauiTopics(List<String[]> topicsList, String sourceDirPath,String format) throws FileNotFoundException {
		List<MauiTopics> mt = new ArrayList<>();
		//File[] files = MauiFileUtils.filterFileList(sourceDirPath, format, false);
		File[] files = MauiFileUtils.filterDir(sourceDirPath, format);
		int i = 0;
		for (String[] doc : topicsList) {
			mt.add(stringArrayToMauiTopics(doc,files[i].getPath()));
			i++;
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
	
	/** Returns the sizes of each array in the list. */
	public static int[] elementSizes(List<String[]> data) {
		int[] sizes = new int[data.size()];
		
		int i;
		for (i = 0; i < data.size(); i++) {
			sizes[i] = data.get(i).length;
		}
		
		return sizes;
	}
	
	/** Counts the amount of topics extracted in every document.
	 * Same as elementSizes, but for maui topics. */
	public static int[] topicsCount(List<MauiTopics> topics) {
		int docCount = topics.size();
		int[] count = new int[docCount];
		
		for (int doc = 0; doc < docCount; doc++) {
			count[doc] = topics.get(doc).getTopics().size();
		}
		return count;
	}
	
	/**
	 * Returns a string with the current date as dd-MM format.
	 * @return
	 */
	public static String getTimeAndDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kkmm");//Add year information
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
	
	/**
	 * Counts the matches on the elements that are in the same index.
	 * @param arr1
	 * @param arr2
	 * @return
	 * @throws Exception
	 */
	public static int matchesCount(String[] arr1, String[] arr2, boolean ignoreCase) throws Exception {
		if (arr1.length != arr2.length) throw new Exception("Array lengths must be equal.");
		int count = 0;
		for (int i = 0; i < arr1.length; i++) {
			if (ignoreCase) {
				if (arr1[i].equalsIgnoreCase(arr2[i])) count++;
			} else {
				if (arr1[i].equals(arr2[i])) count++;
			}
		}
		return count;
	}
	
	/**
	 * Filters a string array using a filter method.
	 * @param list
	 * @param filterMethod
	 * @return the strings whose names include the string in filterMethod.
	 */
	public static String[] filterStringList(String[] list, String filterMethod) {
		List<String> newlist = new ArrayList<>();
		int i;
		for (i = 0; i < list.length; i++) {
			if (list[i].contains(filterMethod)) {
				newlist.add(list[i]);
			}
		}
		return newlist.toArray(new String[newlist.size()]);
	}
	
	public static List<String> flattenString(List<List<String>> strList) {
		List<String> rtr = new ArrayList<>();
		for (List<String> str : strList) rtr.add(String.join(", ", str));
		return rtr;
	}
}
