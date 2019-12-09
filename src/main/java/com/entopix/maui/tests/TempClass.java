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

public class TempClass {
	
	public static void main(String[] args) throws Exception {
		
		//Variable init
		String testDirPath = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts\\test60";
		String trainDirPath = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts\\train30";
		String packedModelPath = "C:\\Users\\PC1\\git\\maui-pt\\data\\models\\packed_model";
		Stemmer stemmer = new LuceneBRStemmer();
		
		MauiCore.setTrainDirPath(trainDirPath);
		MauiCore.setStemmer(stemmer);
		MauiCore.setSaveModel(false);
		
		ModelWrapper model = new ModelWrapper(MauiCore.buildModel(), trainDirPath, stemmer);
		
		MauiFileUtils.serializeObject(model, packedModelPath);
		
		ModelWrapper modelFromFile = (ModelWrapper) MauiFileUtils.deserializeObject(packedModelPath);
		
		/*
		ModelWrapper wrapper = (ModelWrapper) MauiFileUtils.deserializeObject(modelPath);
		
		Vocabulary vocab = new Vocabulary();
		vocab.setReorder(false);
		vocab.setSerialize(true);
		vocab.setEncoding("UTF-8");
		vocab.setLanguage("pt");
		vocab.setStemmer(wrapper.getStemmerUsed());
		vocab.setStopwords(new StopwordsPortuguese());
		vocab.setVocabularyName(MauiCore.getVocabPath());
		vocab.initializeVocabulary(MauiCore.getVocabPath(), "skos");
		
		MauiTopicExtractor mte = new MauiTopicExtractor();
		mte.setModel(wrapper.getFilter());
		mte.stemmer = wrapper.getStemmerUsed();
		mte.setVocabulary(vocab);
		mte.extractTopics(DataLoader.loadTestDocuments(testDirPath));
		*/
		
	}
}
