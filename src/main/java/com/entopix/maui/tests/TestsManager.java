package com.entopix.maui.tests;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.entopix.maui.core.MauiCore;
import com.entopix.maui.main.TBCI;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.utils.MPTUtils;
import com.entopix.maui.utils.Matrix;
import com.entopix.maui.utils.MauiFileUtils;

public class TestsManager {

	
	/**
	 * Saves a workbook to the file in the specified location.
	 * @param wb
	 */
	public static void saveWorkbook(Workbook wb, String filepath) {
		OutputStream fileOut;
		try {
			fileOut = new FileOutputStream(filepath);
			wb.write(fileOut);
			wb.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves a matrix of objects in a sheet as a .xls file.
	 * @param matrix
	 * @param filepath
	 * @throws IOException
	 */
	public static void saveMatrixAsSheet(Matrix matrix, String filepath) throws IOException {
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet();
		sheet = fillSheet(sheet, matrix);
		saveWorkbook(wb, filepath);
	}
	
	/**
	 * Saves a list of matrixes as sheets in a .xls file.
	 * @param matrixes
	 * @param sheetNames
	 * @param filepath
	 * @throws IOException
	 */
	public static void saveMatrixesAsSheets(Matrix[] matrixes, String[] sheetNames, String filepath) throws IOException {
		Workbook wb = new HSSFWorkbook();
		int matrixCount = matrixes.length;
		
		Sheet sheet;
		int i;
		for (i = 0; i < matrixCount; i++) {
			if (sheetNames[i] != null) {
				sheet = wb.createSheet(sheetNames[i]);
			} else {
				sheet = wb.createSheet();
			}
			sheet = fillSheet(sheet, matrixes[i]);
		}
		
		saveWorkbook(wb, filepath);
	}

	/**
	 * Formats the sheet.
	 * @param sheet
	 * @return
	 */
	public static Sheet format(Sheet sheet) {
		CellStyle style1 = sheet.getWorkbook().createCellStyle();
		Font font = sheet.getWorkbook().createFont();
		Row r;
		int i;
		
		// formats header
		font.setBold(true);
		style1.setFont(font);
		
		r = sheet.getRow(0);
		for (i = 0; i < r.getLastCellNum(); i++) {
			r.getCell(i).setCellStyle(style1);
		}
		
		// formats lines
		CellStyle style2 = sheet.getWorkbook().createCellStyle();
		style2.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		int rownum;
		for (rownum = 0; rownum <= sheet.getLastRowNum(); rownum += 2) {
			r = sheet.getRow(rownum);
			for (i = 0; i < r.getLastCellNum(); i++) {
				r.getCell(i).setCellStyle(style2);
			}
 		}
		
		return sheet;
	}
	
	/**
	 * Adds the content from a matrix of objects to a sheet.
	 * The objects must be String, Double, Integer or Boolean instances.
	 * @param wb
	 * @param sheetname
	 * @param matrix
	 * @return
	 * @throws IOException 
	 */
	public static Sheet fillSheet(Sheet sheet, Matrix m) throws IOException {
		List<Object[]> matrix = m.getData();
		Row r;
		Cell c;
		int i,j;
		Object[] line;
		for (i = 0; i < matrix.size(); i++) {
			line = matrix.get(i);
			r = sheet.createRow(i);
			for (j = 0; j < line.length; j++) {
				c = r.createCell(j);
				if (line[j] instanceof String) {
					c.setCellValue(line[j].toString());
				} else if (line[j] instanceof Integer) {
					c.setCellValue(new Double((Integer) line[j]));
				} else if (line[j] instanceof Double) {
					c.setCellValue((Double) line[j]);
				} else if (line[j] instanceof Boolean) {
					c.setCellValue((Boolean) line[j]);
				}
				sheet.autoSizeColumn(i);
			}
		}
		return sheet;
	}
	
	/**
	 * Builds a matrix that compares the keywords extracted by a model on every document by the document's corresponding manual keywords.
	 * @param keywordsPaths the paths to the manual keywords of every document
	 * @param allDocTopicsExtracted
	 * @return 
	 * @throws Exception if number of manual topics isn't equal to number of documents
	 */
	public static Matrix buildKeywordsComparisonMatrix(String[] docnames, List<String[]> extractedTopics, List<String[]> manualTopics, List<MauiTopics> mauiTopics) throws Exception {
		
		if (docnames.length != manualTopics.size()) {
			throw new Exception("Incoherent number of topics");
		}
		
		log("Building Keywords Comparison Matrix...");
		
		Matrix matrix = new Matrix();
		String[] header = new String[]{"Documento","Termo do MAUI","Termo em Comum","Termo em Comum MAUI","Termos do Maui","Acertos"};
		matrix.addLine(header);
		
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
				
				matrix.addLine(line);
			}
		}
		log("Done!");
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
	public static Matrix buildModelEvaluationMatrix(String[] docnames, int[] extractedCount, int[] manualCount, int[] matchesCount) throws Exception {
		
		log("Building Model Evaluation Matrix...");
		
		Matrix matrix = new Matrix();
		String[] header = new String[]{"Documento","Termos Extraídos", "Termos Manuais","Casamentos Exatos","Consistência","Precisão","Revocação","Medida-F"};
		matrix.addLine(header);
		
		List<Object> line = new ArrayList<>();
		List<Double> precisions = new ArrayList<>();
		List<Double> recalls = new ArrayList<>();
		List<Double> fMeasures = new ArrayList<>();
		List<Double> consistencies = new ArrayList<>();
		int currentDoc, numExtracted, numManual, numMatches;
		double[] measures = new double[4];
		
		for (currentDoc = 0; currentDoc < docnames.length; currentDoc++) {
			numExtracted = extractedCount[currentDoc];
			numManual = manualCount[currentDoc];
			numMatches = matchesCount[currentDoc];
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
			
			matrix.addLine(line.toArray(new Object[0]));
			line.clear();
		}
		
		List<Integer> extracted = MPTUtils.intArrayToIntegerList(extractedCount);
		List<Integer> manual = MPTUtils.intArrayToIntegerList(manualCount);
		List<Integer> matches = MPTUtils.intArrayToIntegerList(matchesCount);
		matrix.addLine(new Object[0]);
		
		line.add("Mínimo");
		line.add(Collections.min(extracted));
		line.add(Collections.min(manual));
		line.add(Collections.min(matches));
		line.add((Collections.min(consistencies)));
		line.add((Collections.min(precisions)));
		line.add((Collections.min(recalls)));
		line.add((Collections.min(fMeasures)));
		matrix.addLine(line.toArray(new Object[0]));
		line.clear();
		
		line.add("Média");
		line.add(MPTUtils.mean(MPTUtils.integerListToDoubleList((extracted))));
		line.add(MPTUtils.mean(MPTUtils.integerListToDoubleList((manual))));
		line.add(MPTUtils.mean(MPTUtils.integerListToDoubleList((matches))));
		line.add(MPTUtils.mean(consistencies));
		line.add(MPTUtils.mean(precisions));
		line.add(MPTUtils.mean(recalls));
		line.add(MPTUtils.mean(fMeasures));
		matrix.addLine(line.toArray(new Object[0]));
		line.clear();
		
		line.add("Desvio Padrão");
		line.add(MPTUtils.stdDev(MPTUtils.integerListToDoubleList(extracted)));
		line.add(MPTUtils.stdDev(MPTUtils.integerListToDoubleList(manual)));
		line.add(MPTUtils.stdDev(MPTUtils.integerListToDoubleList(matches)));
		line.add(MPTUtils.stdDev(consistencies));
		line.add(MPTUtils.stdDev(precisions));
		line.add(MPTUtils.stdDev(recalls));
		line.add(MPTUtils.stdDev(fMeasures));
		matrix.addLine(line.toArray(new Object[0]));
		line.clear();
		
		line.add("Máximo");
		line.add(Collections.max(extracted));
		line.add(Collections.max(manual));
		line.add(Collections.max(matches));
		line.add(Collections.max(consistencies));
		line.add(Collections.max(precisions));
		line.add(Collections.max(recalls));
		line.add(Collections.max(fMeasures));
		matrix.addLine(line.toArray(new Object[0]));
		
		log("Done!");
		return matrix;
	}
	
	/** Sets up and runs the model evaluation. 
	 * @param runDir location of the .txt and .key files. 
	 * @return a matrix with the results.*/
	public static Matrix runModelEvaluation(String runDir, List<MauiTopics> topics, String outPath) throws Exception {
		// Getting data
		List<String[]> manualKeywords = MauiFileUtils.readAllKeyFromDir(runDir, ".key");
		List<String[]> extractedKeywords = MPTUtils.mauiTopicsToListofStringArrays(topics);
		List<String[]> matches = MauiCore.allMatches(manualKeywords, extractedKeywords);
		String[] docnames = MauiFileUtils.getFileNames(MauiFileUtils.filterDir(runDir, ".txt"), true);
		int[] extractedCount = MPTUtils.topicsCount(topics);
		int[] manualCount = MPTUtils.elementSizes(manualKeywords);
		int[] matchesCount = MPTUtils.elementSizes(matches);
		
		Matrix results = TestsManager.buildModelEvaluationMatrix(docnames, extractedCount, manualCount, matchesCount);
		return results;
	}
	
	/** Sets up and runs the keywords comparison. 
	 * @param runDir location of the .txt and .key files.
	 * @return a matrix with the results. */
	public static Matrix runKeywordsComparison(String runDir, List<MauiTopics> mauiTopics, String outPath) throws Exception {
		String[] docnames = MauiFileUtils.getFileNames(MauiFileUtils.filterDir(runDir, ".txt"), true);
		List<String[]> extractedTopics = MPTUtils.mauiTopicsToListofStringArrays(mauiTopics);
		List<String[]> manualTopics = MauiFileUtils.readAllKeyFromDir(runDir, ".key");
		Matrix results = TestsManager.buildKeywordsComparisonMatrix(docnames, extractedTopics, manualTopics, mauiTopics);
		return results;
	}
	
	/**
	 * Sets up and runs general terms comparison.
	 * @param runDir location of the .txt and .key files. 
	 * @param extracted
	 * @return a matrix with the results.
	 * @throws FileNotFoundException
	 */
	public static Matrix runGeneralTermsComparison(String runDir, List<String[]> extracted) throws FileNotFoundException {
		String[] docnames = MauiFileUtils.getFileNames(MauiFileUtils.filterDir(runDir, ".txt"), true);
		List<String[]> manualTopics = MauiFileUtils.readAllKeyFromDir(runDir, ".key");
		Matrix results = TestsManager.buildGeneralTermsComparisonMatrix(docnames, extracted, manualTopics);
		return results;
	}
	
	public static Matrix buildGeneralTermsComparisonMatrix(String[] docnames, List<String[]> extractedTopics, List<String[]> manualTopics) {
		
		log("Building General Terms Comparison Matrix...");
		
		Matrix matrix = new Matrix();
		String[] header = new String[]{"Nome do Documento", "TG mais Frequente MAUI", "TG MAIS Frequente Manual", "Acerto"};
		matrix.addLine(header);
		
		Object[] line;
		ArrayList<Entry<String, Integer>> mauiResults, keywordsResults;
		int currentDoc, linePointer;
		for (currentDoc = 0; currentDoc < docnames.length; currentDoc++) {
			line = new Object[header.length];
			mauiResults = TBCI.getTBCITopConceptsCount(extractedTopics.get(currentDoc));
			keywordsResults = TBCI.getTBCITopConceptsCount(manualTopics.get(currentDoc));
			
			for (linePointer = 0; linePointer < line.length; linePointer++) {
				line[0] = docnames[currentDoc]; //adds docname
				
				line[1] = TBCI.getTBCITerm(Integer.parseInt(mauiResults.get(0).getKey())).getKey(); //adds most frequent maui term
				
				line[2] = TBCI.getTBCITerm(Integer.parseInt(keywordsResults.get(0).getKey())).getKey(); //adds most frequent keywords term
				
				line[3] = (line[1].equals(line[2]) ? 1 : 0);
				
				matrix.addLine(line);
			}
			log((currentDoc + 1) + " out of " + docnames.length + " documents processed...");
		}
		log("Done!");
		return matrix;
	}
	
	public static Matrix buildGeneralTermsComparisonMatrix2(String dirPath, int termsToEvaluate) throws Exception {
		
		log("Building General Terms Comparison Matrix...");
		
		// Gathering data
		System.out.println();
		log("Processing MAUI terms...");
		String[] topTermsMaui = MauiCore.getTopFrequentTermsFromDir(dirPath, ".maui", termsToEvaluate, true);
		System.out.println();
		log("Processing manual terms...");
		String[] topTermsManual = MauiCore.getTopFrequentTermsFromDir(dirPath, ".key", termsToEvaluate, true);
		double matches = MPTUtils.matchesCount(topTermsMaui, topTermsManual, true);
		double percentage = (matches / (double) topTermsManual.length) * 100;
		System.out.println("\nTotal de acertos: " + matches);
		System.out.println("Percentual de acertos: " + percentage + "%"); // TODO: round this
		String[] docnames = MauiFileUtils.getFileNames(MauiFileUtils.filterDir(dirPath, ".txt"), false);
		
		// Building matrix
		Matrix matrix = new Matrix();
		String[] header = new String[]{"Nome do Documento", "TG mais Frequente MAUI", "TG mais Frequente Manual", "Acerto"};
		matrix.addLine(header);
		
		Object[] line = null;
		int currentDoc;
		for (currentDoc = 0; currentDoc < docnames.length; currentDoc++) {
			line = new Object[header.length];
			line[0] = docnames[currentDoc]; //adds doc name
			line[1] = topTermsMaui[currentDoc]; //adds maui top term
			line[2] = topTermsManual[currentDoc]; //adds manual top term
			line[3] = (line[1].equals(line[2]) ? 1 : 0); //checks match
			matrix.addLine(line);
		}
		log("Done!");
		return matrix;
	}
	
	private static void log(String message) {
		System.out.println("[" + TestsManager.class.getSimpleName() + "] " + message);
	}
}
