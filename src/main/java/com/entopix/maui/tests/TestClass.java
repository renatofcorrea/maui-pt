package com.entopix.maui.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.entopix.maui.core.MPTCore;
import com.entopix.maui.stemmers.LuceneBRStemmer;
import com.entopix.maui.stemmers.LuceneRSLPMinimalStemmer;
import com.entopix.maui.stemmers.LuceneRSLPStemmer;
import com.entopix.maui.stemmers.LuceneSavoyStemmer;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stemmers.WekaStemmerOrengo;
import com.entopix.maui.stemmers.WekaStemmerPorter;
import com.entopix.maui.stemmers.WekaStemmerSavoy;
import com.entopix.maui.util.Topic;
import com.entopix.maui.utils.MauiFileUtils;
import com.entopix.maui.utils.StringTable;

public class TestClass {
	
	public static void main(String[] args) throws Exception {
		//Variable setup
		String filepath = MauiFileUtils.getDataPath() + "/docs/corpusci/abstracts/test30";
		String stopwordsPath = MauiFileUtils.getDataPath() + "/res/sn_stoplist.txt";
		//String modelPath = MauiFileUtils.getDataPath() + "/models/ST models/model_abstracts_WekaStemmerSavoy_train20";
		String testDirPath = "C:\\Users\\Silvania\\Desktop\\Rahmon\\PIBITI\\projeto\\maui-pt\\data\\docs\\corpusci\\abstracts\\sns30";
		File abstractsDir = new File(MauiFileUtils.getDataPath() + "/docs/corpusci/abstracts");
		////////////////
		
		
		File[] trainFolders = MauiFileUtils.filterFileList(abstractsDir.listFiles(), "train");
		String modelsDir = MauiFileUtils.getDataPath() + "/models/ST models";
		Stemmer[] stemmers = {
				new PortugueseStemmer(),
				new LuceneRSLPStemmer(),
				new LuceneBRStemmer(),
				new LuceneSavoyStemmer(),
				new LuceneRSLPMinimalStemmer(),
				new WekaStemmerOrengo(),
				new WekaStemmerPorter(),
				new WekaStemmerSavoy(),
		};
		
		List<String[]> results = StructuredTest.runTests(trainFolders, testDirPath, modelsDir, stemmers);
		String[] header = {"MODEL NAME","AVG KEY","STDEV KEY","AVG PRECISION","STDEV PRECISION","AVG RECALL","STDEV RECALL","F-MEASURE"};
		String[] tableFormat = {"%-65s","%-20s","%-20s","%-20s","%-20s","%-20s","%-20s","%-20s"}; //spacing
		StringTable table = new StringTable(header, results, tableFormat);
		table.sort(7, "double");
		System.out.println(table.tableToFormattedString());
	}
}
