package com.entopix.maui.tests;

import java.io.File;
import java.util.Arrays;

import com.entopix.maui.beans.MauiModel;
import com.entopix.maui.beans.ModelDocType;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.utils.MauiFileUtils;

public class TempClass {
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		String trainDirPath = StructuredTest2.abstractsDir.getPath() + "\\train30";
		String testDirPath = StructuredTest2.abstractsDir.getPath() + "\\test30";
		File trainDir = new File(trainDirPath);
		File testDir = new File(testDirPath);
		
		Stemmer stemmer = new PortugueseStemmer();
		boolean reorder = false;
		boolean serialize = true;
		
		String modelName = "model_abstracts_train30";
		MauiModel model = new MauiModel(modelName, MauiFileUtils.getModelsDirPath() + "\\" + modelName, stemmer, ModelDocType.ABSTRACTS, trainDirPath);
		
		String[] results = StructuredTest2.testModel(model, testDirPath, reorder, serialize);
	
		System.out.println(Arrays.toString(results));
	}
}
