package com.entopix.maui.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.entopix.maui.beans.MauiModel;
/**
 * Provides paths to avoid path conflicts
 * @author Rahmon Jorge
 *
 */
public class MauiFileUtils {
	
	private static String vocabPath = getDataPath() + "\\vocabulary\\TBCI-SKOS_pt.rdf";
	private static String modelsDirPath = getDataPath() + "\\models";
	
	public static final String getRootPath() {
		try {
			return new File(".").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	public static final String getDataPath() {
		String rootPath = getRootPath();
		if(rootPath.endsWith("maui-pt")) {
			return rootPath + "\\data";
		} else {
			return rootPath + "\\";
		}
	}
	
	/**
	 * Standard: data/vocabulary/TBCI-SKOS_pt.rdf
	 */
	public static String getVocabPath() {
		return vocabPath;
	}
	
	public void setVocabPath(String path) {
		vocabPath = path;
	}
	
	public static String getModelsDirPath() {
		return modelsDirPath;
	}
	
	public void setModelsDirPath(String path) {
		modelsDirPath = path;
	}
	
	/**
	 * Verifies if a file path exists.
	 * @param path
	 * @return
	 */
	public static boolean exists(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return false;
		} else {
			return true;
		}
	}
	/**
	 * Verifies if a folder is empty.
	 * @param path
	 * @return true if and only if the specified pathname is a directory and is empty.
	 */
	public static boolean isEmpty(String path) {
		File file = new File(path);
		if(file.isDirectory()) {
			if(file.list().length == 0) {
				return true;
			}
		}
		return false;
	}
	
	public static File[] filterFileList(File[] fileArray, String filterMethod) {
		ArrayList<File> newArray = new ArrayList<File>();
		
		for(File f : fileArray) {
			if(f.getName().contains(filterMethod)) {
				newArray.add(f);
			}
		}
		return newArray.toArray(new File[newArray.size()]);
	}
}
