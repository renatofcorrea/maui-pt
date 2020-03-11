package com.entopix.maui.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Contains a grid of String objects with a header. Can be sorted and formatted to ease visualization of data.
 * @author Rahmon Jorge
 */
public class StringTable {
	
	private String[] header;
	
	private List<String[]> content;
	
	/** Holds the format strings of each column in the table. Used to define column length. */
	private String[] columnFormat;
	
	private int columnCount;
	
	/**
	 * Returns a StringTable with null header and null column format. <b>Assumes that every row has same length.</b>
	 * @param content
	 */
	public StringTable(List<String[]> content) {
		this.header = null;
		this.content = content;
		this.columnFormat = null;
		this.columnCount = content.get(0).length;
	}
	
	public StringTable(String[] header, List<String[]> content) throws Exception {
		this(header, content, null);
	}
	
	public StringTable(String[] header, List<String[]> content, String[] columnFormat) {
		this.header = header;
		this.content = content;
		this.columnFormat = columnFormat;
		if (columnFormat != null) {
			this.columnCount = columnFormat.length;
		}
	}
	
	public void setHeader(String[] header) {
		this.header = header;
	}
	
	public void setTableContent(List<String[]> content) {
		this.content = content;
	}
	
	public void setColumnFormat(String[] columnFormat) {
		this.columnFormat = columnFormat;
	}
	
	public void addLine(String[] line) {
		content.add(line);
	}
	
	/**
	 *  Sorts the table content in the specified index according to a sorting method. </br>
	 *  @param sortingMethod   Using "double" will assume that all the values
	 *  in the specified index can be parsed to Double variables to be sorted.
	 */
	public void sort(int sortingIndex, String sortingMethod) {
		if (sortingMethod.equalsIgnoreCase("double")) {
			List<String[]> sorted = new ArrayList<String[]>();
			List<String[]> table = new ArrayList<String[]>(content);
			double current = 0;
			double highest = 0;
			int indexOfHighest = 0;
			
			while (!table.isEmpty()) {
				for (String[] line : table) {
					current  = Double.parseDouble(line[sortingIndex].replace(",", "."));
					if (current > highest) {
						highest = current;
						indexOfHighest = table.indexOf(line);
					}
				}
				sorted.add(table.get(indexOfHighest));
				table.remove(indexOfHighest);
				highest = 0;
			}
			content = sorted;
		}
	}
	
	public String tableToFormattedString() {
		String s = "";
		
		//formats header
		if (header != null) {
			for (int c = 0; c < columnCount; c++) {
				s += String.format(columnFormat[c], header[c]);
			}
			s += "\n";
		}
		//formats content
		for (String[] line : content) {
			for (int c = 0; c < columnCount; c++) {
				s += String.format(columnFormat[c], line[c]);
			}
			s += "\n";
		}
		return s;
	}
	
	/**
	 * Exports the table as a .csv file.
	 * @throws IOException 
	 */
	public void exportAsCSV(String filePath) throws IOException {
		String s = "";
		
		if (header != null) {
			for (String word : header) {
				s += word + ";";
			}
			s += "\n";
		}
		
		for (String[] row : this.content) {
			for (String word : row) {
				s += word + ";";
			}
			s += "\n";
		}
		
		MauiFileUtils.printOnFile(s, filePath);
	}
	
	/**
	 * Exports the table as a formatted .xls file.
	 * @param filePath
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void exportAsXLS(String filepath) throws FileNotFoundException, IOException {
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet();
		int rowNum = 0;
		int column = 0;
		Row row;
		Cell cell;
		CellStyle style;
		Font font;
		
		// Creates and formats header cells
		if (header != null) {
			row = sheet.createRow(rowNum);
			style = wb.createCellStyle();
			font = wb.createFont();
			font.setFontName("Arial");
			font.setBold(true);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setFont(font);
			for (String word : header) {
				cell = row.createCell(column);
				cell.setCellValue(word);
				cell.setCellStyle(style);
				sheet.autoSizeColumn(column);
				column++;
			}
			
			rowNum++;
			column = 0;
		}
		
		// Adds and formats content cells
		row = sheet.createRow(rowNum);
		style = wb.createCellStyle();
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		for (String[] line : content) {
			row = sheet.createRow(rowNum);
			for (String word : line) {
				if (rowNum == content.size()) { // if last row, add bottom border
					style = wb.createCellStyle();
					style.setBorderLeft(BorderStyle.THIN);
					style.setBorderRight(BorderStyle.THIN);
					style.setBorderBottom(BorderStyle.THIN);
				}
				cell = row.createCell(column);
				cell.setCellValue(word);
				cell.setCellStyle(style);
				sheet.autoSizeColumn(column);
				column++;
			}
			
			column = 0;
			rowNum++;
		}
		
		try (OutputStream fileOut = new FileOutputStream(filepath)) {
		    wb.write(fileOut);
		}
		
		wb.close();
	}
}
