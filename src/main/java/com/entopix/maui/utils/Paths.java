package com.entopix.maui.utils;

import java.io.File;
import java.io.IOException;
/**
 * Provides paths to avoid path conflicts
 * @author Rahmon Jorge
 *
 */
public class Paths {
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
	
}
