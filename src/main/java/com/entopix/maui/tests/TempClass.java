package com.entopix.maui.tests;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.entopix.maui.core.MauiCore;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.utils.MauiFileUtils;
import com.entopix.maui.utils.MauiPTUtils;

public class TempClass {
	
	public static void main(String[] args) throws Exception {
		
		String docPath = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts\\test30";
		File[] docList = MauiFileUtils.filterFileList(docPath, ".txt");
		String[] docPaths = MauiFileUtils.getFileListPaths(docList);
		String modelPath = "C:\\Users\\PC1\\git\\maui-pt\\data\\models\\model_fulltexts_PortugueseStemmer_train30";
		Stemmer stemmer = new PortugueseStemmer();
		
		MauiCore.debug = false;
		MauiCore.setNumTopicsToExtract(20);
		MauiCore.setStemmer(stemmer);
		MauiCore.setTestDirPath(docPath);
		MauiCore.setModelPath(modelPath);
		List<MauiTopics> mauiTopicsList = MauiCore.runTopicExtractor(); //TODO: for some reason, the model extracted only 9 keywords from "Artigo59.txt" file.
		List<List<String>> extractedTopics = MauiPTUtils.mauiTopicsToString(mauiTopicsList);
		
		double[] results = MauiCore.evaluateTopics(docPaths, extractedTopics, 10);
		
		System.out.println(Arrays.toString(results));
	}
}
