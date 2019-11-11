package com.entopix.maui.tests;

import java.io.File;
import java.util.List;

import com.entopix.maui.core.MauiCore;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.utils.MauiFileUtils;

public class TempClass {
	
	public static void main(String[] args) throws Exception {
		
		String docsPath = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts\\test30";
		String modelPath = "C:\\Users\\PC1\\git\\maui-pt\\data\\models\\model_abstracts_PortugueseStemmer_train30";
		List<MauiTopics> extractedTopicsList = MauiCore.setupAndRunTopicExtractor(modelPath, docsPath, null, true);
		
		MauiCore.newEvaluateTopics(docsPath, extractedTopicsList);
	}
}
