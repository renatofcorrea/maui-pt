package com.entopix.maui.tests;

import java.io.File;
import java.util.List;

import com.entopix.maui.core.MauiCore;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.util.Topic;
import com.entopix.maui.utils.MauiFileUtils;

@SuppressWarnings("unused")
public class TempClass {
	
	public static void main(String[] args) throws Exception {
		String modelPath = MauiFileUtils.getModelsDirPath() + "model_fulltexts_PortugueseStemmer_train30";
		File document = new File(MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts\\test60");
		Stemmer stemmer = new PortugueseStemmer();
	}
}
