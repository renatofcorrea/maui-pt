package com.entopix.maui.tests;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.entopix.maui.beans.MauiModel;
import com.entopix.maui.stemmers.WekaStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.utils.MauiFileUtils;

@SuppressWarnings("unused")
public class TempClass {
	
	public static void main(String[] args) throws Exception {
		Stemmer stemmer = WekaStemmer.getInstance();
		WekaStemmer.setOptions("Orengo");
		String trainDir = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts\\train30";
		String testDir = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts\\test60";
		
		//MauiModel model = StructuredTest.buildModel(stemmer, trainDir);
		
		//String[] results = StructuredTest.testModel(model.getFile(), testDir);
		
		//System.out.println(Arrays.toString(results));
	}
}
