package com.entopix.maui.tests;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a grid of String objects with a header. Can be sorted and formatted for better visualization of data.
 * @author Rahmon Jorge
 */
public class Table {
	
	private String[] header;
	
	private List<String[]> content;
	
	/** Holds the format strings of each column in the table. Used to define column length. */
	private String[] columnFormat;
	
	private int columnCount;
	
	public Table(String[] header, List<String[]> content, String[] columnFormat) throws Exception {
		columnCount = columnFormat.length;
		if (header.length != columnCount || columnFormat.length != columnCount) {
			throw new Exception("Header or column format of table must have the same size");
		}
		//TODO: for to check size of content lines
		
		this.header = header;
		this.content = content;
		this.columnFormat = columnFormat;
	}
	
	public void setHeader(String[] header) {
		this.header = header;
	}
	
	public void setTableContent(List<String[]> content) {
		this.content = content;
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
	
	public String tableToString() {
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
}
