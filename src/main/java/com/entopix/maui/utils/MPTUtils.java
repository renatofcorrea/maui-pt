package com.entopix.maui.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

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
	 * Returns a Stemmer instance based on the name of the model.
	 * The model name has to be in the format modelname_modeltype_stemmername_traindirname .
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
	 */
	public static int ordinalIndexOf(String str, String substr, int n) {
	    int pos = -1;
	    do {
	        pos = str.indexOf(substr, pos + 1);
	    } while (n-- > 0 && pos != -1);
	    return pos;
	}
	
	/**
	 * Returns true if, and only if, length of s != 0 and s != null.
	 */
	public static boolean isValid(String s) {
		return s != null && !s.isEmpty();
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
	
	public static String[] topicsToString(List<Topic> topics) {
		String[] strings = new String[topics.size()];
		int i;
		for (i = 0; i < topics.size(); i++) {
			strings[i] = topics.get(i).getTitle();
		}
		return strings;
	}

	public static List<String[]> mauiTopicsToString(List<MauiTopics> topicsList) {
		List<String[]> strList = new ArrayList<>();
		String[] strings;
		
		for(MauiTopics mt : topicsList) {
			strings = topicsToString(mt.getTopics());
			strList.add(strings);
		}
		
		return strList;
	}
	
	/**
	 * Gets the Nth column in a matrix of doubles.
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
	 * Removes the diacritics of a string
	 * @param s
	 * @return
	 */
	public static String stripAccents(String s) {
	    s = Normalizer.normalize(s, Normalizer.Form.NFD);
	    s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
	    return s;
	}
	
	public static String removeSuffix(String s) {
		int index = s.lastIndexOf(".");
		if (index > 0) {
			return s.substring(0, index);
		}
		return null;
	}
	
	/**
	 * Takes a list of arrays and returns a list with their sizes.
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
	
	public static String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM");
		return sdf.format(new Date(System.currentTimeMillis()));
	}
	

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
	
	public static List<Double> intToDouble(List<Integer> list) {
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
	
	/**
	 * Adds the content from a matrix of objects to a sheet.
	 * The objects must be String, Double or Boolean instances.
	 * @param wb
	 * @param sheetname
	 * @param matrix
	 * @return
	 * @throws IOException 
	 */
	public static Sheet fillSheet(Sheet sheet, List<Object[]> matrix) throws IOException {
		Row r;
		Cell c;
		int i,j;
		Object[] array;
		for (i = 0; i < matrix.size(); i++) {
			array = matrix.get(i);
			r = sheet.createRow(i);
			for (j = 0; j < array.length; j++) {
				c = r.createCell(j);
				if (array[j] instanceof String) {
					c.setCellValue(array[j].toString());
				} else if (array[j] instanceof Double) {
					c.setCellValue((Double) array[j]);
				} else if (array[j] instanceof Boolean) {
					c.setCellValue((Boolean) array[j]);
				}
			}
		}
		return sheet;
	}

	/**
	 * Converts the values in every column of the string matrix to the specified format.
	 * Every string in the formatMethod must explicitly give the format in which every 
	 * column will be converted into. Accepted words are: "string", "double" and "boolean", 
	 * case insensitive. If an element is empty or null, it stays as a string.
	 * @param matrix
	 * @param formatMethod
	 * @return
	 */
	public static List<Object[]> convertMatrixColumns(List<String[]> matrix, String[] columnTypes, boolean ignoreFirstLine) {
		List<Object[]> newMatrix = new ArrayList<>();
		String[] strRow = null;
		Object[] objRow = null;
		int row, col, startRow = 0;
		if (ignoreFirstLine) {
			startRow = 1;
			newMatrix.add(0, matrix.get(0));
		}
		
		for (row = startRow; row < matrix.size(); row++) {
			strRow = matrix.get(row);
			objRow = new Object[strRow.length];
			for (col = 0; col < strRow.length; col++) {
				if (columnTypes[col].equalsIgnoreCase("double")) {
					objRow[col] = Double.valueOf(strRow[col].replace(",", "."));
				} else if (columnTypes[col].equalsIgnoreCase("boolean")) {
					objRow[col] = Boolean.valueOf(strRow[col]);
				} else {
					objRow[col] = strRow[col];
				}
			}
			newMatrix.add(row, objRow);
		}
		
		return newMatrix;
	}
	
	/**
	 * Builds a matrix that compares the keywords extracted by a model on every document by the document's corresponding manual keywords.
	 * @param keywordsPaths the paths to the manual keywords of every document
	 * @param allDocTopicsExtracted
	 * @return 
	 * @throws Exception if number of topic lists isn't equal to number of documents
	 */
	public static List<Object[]> buildKeywordsComparisonMatrix(String[] docnames, List<String[]> extractedTopics, List<String[]> manualTopics, List<MauiTopics> mauiTopics) throws Exception {
		
		if (docnames.length != manualTopics.size()) {
			throw new Exception("Incoherent number of topics");
		}
		
		List<Object[]> matrix = new ArrayList<>();
		String[] header = new String[]{"Documento","Termo do MAUI","Termo em Comum","Termo em Comum MAUI","Termos do Maui","Acertos"};
		matrix.add(header);
		
		Object[] line;
		String[] matches, manual, extracted;
		int linePointer;
		int currentDoc, topic, isMatch;
		int isMauiMatch;
		String keyword;
		for (currentDoc = 0; currentDoc < docnames.length; currentDoc++) {
			
			manual = manualTopics.get(currentDoc); // gets the manual topics for the current document
			extracted = extractedTopics.get(currentDoc); // gets the extracted topics for the current document
			matches = MauiCore.matches(manual, extracted);
			
			for (topic = 0; topic < extractedTopics.get(currentDoc).length; topic++) {
				line = new Object[header.length];
				linePointer = 0;
				
				line[linePointer++] = docnames[currentDoc]; // add document name
				
				keyword = extractedTopics.get(currentDoc)[topic];
				
				line[linePointer++] = keyword; // add keyword name
				
				isMatch = (Arrays.asList(matches).contains(keyword) ? 1 : 0);
				line[linePointer++] = isMatch;
				
				isMauiMatch = (mauiTopics.get(currentDoc).getTopics().get(topic).isCorrect() ? 1 : 0);
				line[linePointer++] = isMauiMatch;
				
				line[linePointer++] = extractedTopics.get(currentDoc).length; // add extracted keywords count
				
				line[linePointer++] = matches.length;
				
				matrix.add(line);
			}
		}
		return matrix;
	}
	
	/**
	 * Evaluate the keywords provided and returns a matrix with the results for the model.
	 * @param docnames
	 * @param extracted
	 * @param manual
	 * @param matches
	 * @return
	 * @throws Exception
	 */
	public static List<Object[]> buildModelEvaluationMatrix(String[] docnames, List<Integer> extracted, List<Integer> manual, List<Integer> matches) throws Exception {
		List<Object[]> matrix = new ArrayList<>();
		List<Object> line = new ArrayList<>();
		List<Double> precisions = new ArrayList<>();
		List<Double> recalls = new ArrayList<>();
		List<Double> fMeasures = new ArrayList<>();
		List<Double> consistencies = new ArrayList<>();
		int currentDoc, numExtracted, numManual, numMatches, docCount = docnames.length;
		double[] measures = new double[4];
		String[] header = new String[]{"Documento","Termos Extraídos", "Termos Manuais","Casamentos Exatos","Consistência","Precisão","Revocação","Medida-F"};
		matrix.add(header);
		
		for (currentDoc = 0; currentDoc < docCount; currentDoc++) {
			numExtracted = extracted.get(currentDoc);
			numManual = manual.get(currentDoc);
			numMatches = matches.get(currentDoc);
			measures = MPTUtils.calculateMeasures(numExtracted, numManual, numMatches, true);
			
			line.add(docnames[currentDoc]);
			line.add(numExtracted);
			line.add(numManual);
			line.add(numMatches);
			line.add(measures[3]); //consistency
			line.add(measures[0]); //precision
			line.add(measures[1]); //recall
			line.add(measures[2]); //f-measure
			
			precisions.add(measures[0]);
			recalls.add(measures[1]);
			fMeasures.add(measures[2]);
			consistencies.add(measures[3]);
			measures = new double[4];
			
			matrix.add(line.toArray(new Object[0]));
			line.clear();
		}
		
		matrix.add(new Object[0]);
		
		line.add("Mínimo");
		line.add(Collections.min(extracted));
		line.add(Collections.min(manual));
		line.add(Collections.min(matches));
		line.add((Collections.min(consistencies)));
		line.add((Collections.min(precisions)));
		line.add((Collections.min(recalls)));
		line.add((Collections.min(fMeasures)));
		matrix.add(line.toArray(new Object[0]));
		line.clear();
		
		line.add("Média");
		line.add(MPTUtils.mean(MPTUtils.intToDouble((extracted))));
		line.add(MPTUtils.mean(MPTUtils.intToDouble((manual))));
		line.add(MPTUtils.mean(MPTUtils.intToDouble((matches))));
		line.add(MPTUtils.mean(consistencies));
		line.add(MPTUtils.mean(precisions));
		line.add(MPTUtils.mean(recalls));
		line.add(MPTUtils.mean(fMeasures));
		matrix.add(line.toArray(new Object[0]));
		line.clear();
		
		line.add("Desvio Padrão");
		line.add(MPTUtils.stdDev(MPTUtils.intToDouble(extracted)));
		line.add(MPTUtils.stdDev(MPTUtils.intToDouble(manual)));
		line.add(MPTUtils.stdDev(MPTUtils.intToDouble(matches)));
		line.add(MPTUtils.stdDev(consistencies));
		line.add(MPTUtils.stdDev(precisions));
		line.add(MPTUtils.stdDev(recalls));
		line.add(MPTUtils.stdDev(fMeasures));
		matrix.add(line.toArray(new Object[0]));
		line.clear();
		
		line.add("Máximo");
		line.add(Collections.max(extracted));
		line.add(Collections.max(manual));
		line.add(Collections.max(matches));
		line.add(Collections.max(consistencies));
		line.add(Collections.max(precisions));
		line.add(Collections.max(recalls));
		line.add(Collections.max(fMeasures));
		matrix.add(line.toArray(new Object[0]));
		
		return matrix;
	}
	
	/**
	 * Builds a model evaluation matrix and a keywords comparison matrix, and saves them.
	 * saves all to file.
	 * @param mauiTopics
	 * @param testDirPath
	 * @throws Exception
	 * @throws IOException
	 */
	public static void buildAndSaveResultMatrixes(List<MauiTopics> mauiTopics, String testDirPath) throws Exception, IOException {
		
		// Gets the documents names, extracted and manual topics and matches between them
		List<String[]> extractedTopics = MPTUtils.mauiTopicsToString(mauiTopics);
		List<String[]> manualTopics = MauiFileUtils.readKeyFromFolder(testDirPath, ".key");
		List<String[]> matches = MauiCore.allMatches(manualTopics, extractedTopics);
		String[] documentsNames = MauiFileUtils.getFileListNames(MauiFileUtils.filterFileList(testDirPath, ".key"), true);
		
		// Builds the matrixes
		List<Object[]> matrix1 = MPTUtils.buildKeywordsComparisonMatrix(documentsNames, extractedTopics, manualTopics, mauiTopics);
		List<Object[]> matrix2 = MPTUtils.buildModelEvaluationMatrix(documentsNames, elementSizes(extractedTopics), elementSizes(manualTopics), elementSizes(matches));
		
		Workbook wb = new HSSFWorkbook();
		Sheet sheet;
		
		// Adds matrixes to workbook
		sheet = wb.createSheet("Comparação de Frases-Chave");
		sheet = MPTUtils.fillSheet(sheet, matrix1);
		sheet = wb.createSheet("Avaliação do Modelo");
		sheet = MPTUtils.fillSheet(sheet, matrix2);
		
		// Saves workbook
		String filepath = MauiFileUtils.getDataPath() + "//tests" + "//test_" +MPTUtils.getDate();
		OutputStream fileOut;
		try {
			fileOut = new FileOutputStream(filepath + ".xls");
			wb.write(fileOut);
			wb.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
