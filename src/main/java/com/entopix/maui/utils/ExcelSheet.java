package com.entopix.maui.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class ExcelSheet {
	
	private Workbook wb;
	private Sheet sheet;
	
	/**
	 * Returns a ExcelSheet object with no rows or columns on the sheet.
	 */
	public ExcelSheet() {
		wb = new HSSFWorkbook();
		sheet = wb.createSheet();
	}
	
	/**
	 * Adds a row to the sheet on the specified position and returns it.
	 * @param rowNum
	 * @return
	 */
	public Row addRow(int rowNum) {
		return sheet.createRow(rowNum);
	}
	
	/**
	 * Adds a cell to the sheet on the specified position and returns it.
	 * @param rowNum
	 * @param colNum
	 * @return
	 */
	public Cell addCell(int rowNum, int colNum) {
		return sheet.getRow(rowNum).createCell(colNum);
	}
	
	public boolean hasRow(int rowNum) {
		return (sheet.getRow(rowNum) != null ? true : false);
	}
	
	public boolean hasCell(int rowNum, int colNum) {
		if (hasRow(rowNum)) {
			return (sheet.getRow(rowNum).getCell(colNum) != null ? true : false);
		}
		return false;
	}
	
	/**
	 * Sets a cell value, if it exists, and IF the value is a instance of String, Double or Boolean.
	 * @param value
	 * @param row
	 * @param column
	 * @return
	 */
	public Cell setCellValue(Object value, int row, int column) {
		if (!hasCell(row, column)) {
			return null;
		}
		
		Row r = sheet.getRow(row);
		Cell c = r.getCell(column);
		if (value instanceof String) {
			c.setCellValue(value.toString());
		}
		else if (value instanceof Double) {
			c.setCellValue((double)value);
		}
		else if (value instanceof Boolean) {
			c.setCellValue((boolean)value);
		}
		sheet.autoSizeColumn(column);
		return c;
	}
	
	/**
	 * Tries to return the cell value as a string, double or boolean value. Returns null otherwise. 
	 * @param row
	 * @param column
	 * @return
	 */
	public Object getCellValue(int row, int column) {
		Row r = sheet.getRow(row);
		Cell c = r.getCell(column);
		
		try {
			return c.getStringCellValue();
		} catch (Exception e) {}
		
		try {
			return c.getNumericCellValue();
		} catch (Exception e) {}
		
		try {
			return c.getBooleanCellValue();
		} catch (Exception e) {}
		
		return null;
	}
	
	public void saveToFile(String filepath) {
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
}
