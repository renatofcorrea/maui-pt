package com.entopix.maui.tests;

import java.util.List;

import com.entopix.maui.core.MauiCore;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.util.Topic;
import com.entopix.maui.utils.MauiFileUtils;

public class TempClass {
	
	public static void main(String[] args) throws Exception {
		
		String filename = "Artigo32.txt";
		String docPath = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts\\test30\\" + filename;
		String modelPath = "C:\\Users\\PC1\\git\\maui-pt\\data\\models\\model_fulltexts_PortugueseStemmer_train30";
		Stemmer stemmer = new PortugueseStemmer();
		
		
		MauiCore.setNumTopicsToExtract(20);
		List<Topic> extractedTopicsList = MauiCore.runMauiWrapperOnFile();
		MauiCore.newEvaluateTopics(docPath, extractedTopicsList, 15);
	}
}
