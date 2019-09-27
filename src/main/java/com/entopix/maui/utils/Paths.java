package com.entopix.maui.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
/**
 * Provides paths to avoid path conflicts
 * @author Rahmon Jorge
 *
 */
public class Paths {
	
	private static String vocabPath = getDataPath() + "\\vocabulary\\TBCI-SKOS_pt.rdf";
	
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
