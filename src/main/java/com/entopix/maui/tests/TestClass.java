package com.entopix.maui.tests;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Class used for tests during development. Should be removed before release.
 * @author Rahmon Jorge
 *
 */

public class TestClass {
	
	public static void main(String[] args) throws Exception {
		
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("new sheet");
		// Create a row and put some cells in it. Rows are 0 based.
		Row row = sheet.createRow(1);
		// Aqua background
		CellStyle style = wb.createCellStyle();
		style.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
		Cell cell = row.createCell(1);
		cell.setCellValue("X");
		cell.setCellStyle(style);
		// Orange "foreground", foreground being the fill foreground not the font color.
		style = wb.createCellStyle();
		style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cell = row.createCell(2);
		cell.setCellValue("X");
		cell.setCellStyle(style);
		// Write the output to a file
		try (OutputStream fileOut = new FileOutputStream("workbook.xls")) {
		    wb.write(fileOut);
		}
		wb.close();
		
	}
}
