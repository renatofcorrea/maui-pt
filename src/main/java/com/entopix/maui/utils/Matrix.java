package com.entopix.maui.utils;

import java.util.ArrayList;
import java.util.List;

public class Matrix {
	
	private List<Object[]> data;
	
	public Matrix() {
		data = new ArrayList<>();
	}
	
	//TODO: try using (Collection <? extends Object[]> data) signature
	public Matrix(List<Object[]> data) {
		this.data = new ArrayList<>(data);
	}
	
	public void addLine(Object[] line) {
		data.add(line);
	}
	
	public Object[] getLine(int index) {
		return data.get(index);
	}
	
	public List<Object[]> getData() {
		return this.data;
	}
	
	/**
	 * WARNING: Use this method only if you are sure that all content are strings.
	 * @return
	 */
	public List<String[]> getDataAsString() {
		List<String[]> list = new ArrayList<>();
		int i;
		for (i = 0; i < data.size(); i++) {
			if (data.get(i) instanceof String[]) {
				list.add((String[]) data.get(i));
			}
		}
		return list;
	}
	
	/**
	 * Returns the sizes of each array in the matrix.
	 * @return
	 */
	public int[] elementSizes() {
		int[] sizes = new int[data.size()];
		
		int i;
		for (i = 0; i < data.size(); i++) {
			sizes[i] = data.get(i).length;
		}
		
		return sizes;
	}
	
	public void clear() {
		data = new ArrayList<>();
	}
}
