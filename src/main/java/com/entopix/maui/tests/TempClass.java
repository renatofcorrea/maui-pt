package com.entopix.maui.tests;

import com.entopix.maui.utils.MauiFileUtils;

public class TempClass {
	
	public static void main(String[] args) throws Exception {
		/*
		Stemmer stemmer = new NewPortugueseStemmer(new String[] {"-S","orengo"});
		String trainDirPath = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts\\train30";
		
		MauiModel model = StructuredTest.buildModel(stemmer, trainDirPath);
		*/
		
		String modelPath = "C:\\Users\\PC1\\git\\maui-pt\\data\\models\\model_NewPortugueseStemmer_orengo_fulltexts_train30";
		
		String testDirPath = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts\\test60";
		double[] results = StructuredTest.testModel(modelPath, testDirPath);
		
		for(int i = 0;i < results.length;i++) {
			System.out.format("%.2f", results[i]);
			System.out.println();
		}
	}
}
