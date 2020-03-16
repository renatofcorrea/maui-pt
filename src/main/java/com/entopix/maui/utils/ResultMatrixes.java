package com.entopix.maui.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
import com.entopix.maui.util.MauiTopics;

public class ResultMatrixes {
	
	/**
	 * Builds and saves a workbook with two sheets: one for the keywords comparison and other for the model evaluation.
	 * @param mauiTopics
	 * @param testDirPath
	 * @param filepath
	 * @throws IOException
	 * @throws Exception
	 */
	public static void buildAndSaveResultsWorkbook(List<MauiTopics> mauiTopics, String testDirPath, String filepath) throws IOException, Exception {
		Workbook wb = buildWorkbook(mauiTopics, testDirPath);
		saveWorkbook(wb, filepath);
	}
	
	/**
	 * Returns a workbook with two sheets: one for the keywords comparison and 
	 * other for the model evaluation.
	 * @param mauiTopics
	 * @param testDirPath
	 * @return 
	 * @throws Exception
	 * @throws IOException
	 */
	private static Workbook buildWorkbook(List<MauiTopics> mauiTopics, String testDirPath) throws Exception, IOException {
		
		// Gets data
		List<String[]> extractedTopics = MPTUtils.mauiTopicsToString(mauiTopics);
		List<String[]> manualTopics = MauiFileUtils.readKeyFromFolder(testDirPath, ".key");
		List<String[]> matches = MauiCore.allMatches(manualTopics, extractedTopics);
		String[] docNames = MauiFileUtils.getFileListNames(MauiFileUtils.filterFileList(testDirPath, ".key"), true);
		
		// Builds the matrixes
		List<Object[]> matrix1 = buildKeywordsComparisonMatrix(docNames, extractedTopics, manualTopics, mauiTopics);
		List<Object[]> matrix2 = buildModelEvaluationMatrix(docNames, MPTUtils.elementSizes(extractedTopics), MPTUtils.elementSizes(manualTopics), MPTUtils.elementSizes(matches));
		
		// Build workbook and adds matrixes to it
		Workbook wb = new HSSFWorkbook();
		Sheet sheet;
		
		sheet = wb.createSheet("Comparação de Frases-Chave");
		sheet = fillSheet(sheet, matrix1);
		sheet = format(sheet);
		
		sheet = wb.createSheet("Avaliação do Modelo");
		sheet = fillSheet(sheet, matrix2);
		sheet = format(sheet);
		
		return wb;
	}

	/**
	 * Formats the sheet.
	 * @param sheet
	 * @return
	 */
	private static Sheet format(Sheet sheet) {
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
		line.add(MPTUtils.mean(MPTUtils.integerListToDoubleList((extracted))));
		line.add(MPTUtils.mean(MPTUtils.integerListToDoubleList((manual))));
		line.add(MPTUtils.mean(MPTUtils.integerListToDoubleList((matches))));
		line.add(MPTUtils.mean(consistencies));
		line.add(MPTUtils.mean(precisions));
		line.add(MPTUtils.mean(recalls));
		line.add(MPTUtils.mean(fMeasures));
		matrix.add(line.toArray(new Object[0]));
		line.clear();
		
		line.add("Desvio Padrão");
		line.add(MPTUtils.stdDev(MPTUtils.integerListToDoubleList(extracted)));
		line.add(MPTUtils.stdDev(MPTUtils.integerListToDoubleList(manual)));
		line.add(MPTUtils.stdDev(MPTUtils.integerListToDoubleList(matches)));
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
	 * Adds the content from a matrix of objects to a sheet.
	 * The objects must be String, Double, Integer or Boolean instances.
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
	 * Saves a workbook to the file in the specified location.
	 * @param wb
	 */
	public static void saveWorkbook(Workbook wb, String filepath) {
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
