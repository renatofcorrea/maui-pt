package com.entopix.maui.tests;

import com.entopix.maui.core.MauiCore;
import com.entopix.maui.core.ModelWrapper;
import com.entopix.maui.main.MauiTopicExtractor;
import com.entopix.maui.stemmers.LuceneBRStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stopwords.StopwordsPortuguese;
import com.entopix.maui.util.DataLoader;
import com.entopix.maui.utils.MauiFileUtils;
import com.entopix.maui.vocab.Vocabulary;

/**
 * Class used for tests during development. Should be removed before release.
 * @author Rahmon Jorge
 *
 */

public class Testing {
	
	public static void main(String[] args) throws Exception {
		
		//Variable init
		String testDirPath = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts\\test60";
		String trainDirPath = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts\\train30";
		String packedModelPath = "C:\\Users\\PC1\\git\\maui-pt\\data\\models\\packed_model";
		Stemmer stemmer = new LuceneBRStemmer();
		
		//MauiCore setup
		MauiCore.setTrainDirPath(trainDirPath);
		MauiCore.setTestDirPath(testDirPath);
		MauiCore.setStemmer(stemmer);
		MauiCore.saveModel= true;
		MauiCore.DB_evaluateTopics = true;
		MauiCore.setPrintExtractedTopics(true);
		MauiCore.setModelPath(packedModelPath);
		
		//Test area
		MauiCore.runTopicExtractor();
	}
}
