package com.entopix.maui.utils;

import java.io.File;
import java.io.IOException;

public class Paths {
	public static final String getRootPath() {
		try {
			return new File(".").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}
	public static final String dataPath = getRootPath(); //add "\\data" if maui-pt is the root directory
}
