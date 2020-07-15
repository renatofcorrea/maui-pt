package com.entopix.maui.tests;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import com.entopix.maui.core.MPTCore;
import com.entopix.maui.util.Topic;
import com.entopix.maui.utils.MauiFileUtils;

public class TestClass {
	
	public static void main(String[] args) throws Exception {
		//Variable setup
		String filepath = MauiFileUtils.getDataPath() + "/docs/corpusci/fulltexts/test60/Artigo01.txt";
		String stopwordsPath = MauiFileUtils.getDataPath() + "/res/sn_stoplist.txt";
		
		File file = new File(filepath);
		String text = FileUtils.readFileToString(file);
		
		//Model setup
		//ModelWrapper model = new ModelWrapper(MauiCore.buildModel(), MauiCore.getTrainDirPath(), MauiCore.getStemmer(), MauiCore.getVocabPath());
		
		//ArrayList<Topic> out = MPTCore.setupAndRunIndexerModel(MPTCore.getTrainDirPath(), MPTCore.getVocabPath(), text);
	}
}
