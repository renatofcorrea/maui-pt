package com.entopix.maui.beans;

import com.entopix.maui.filters.MauiFilter;
import com.entopix.maui.main.MauiModelBuilder;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stopwords.StopwordsPortuguese;
import com.entopix.maui.util.DataLoader;

public class MauiModel {
	public MauiModelBuilder modelBuilder;
	private MauiFilter filter;
	
	public MauiModel(String dirPath, String name, Stemmer stemmer, String vocabPath) {
		modelBuilder = new MauiModelBuilder();
		modelBuilder.documentEncoding = "UTF-8";
		modelBuilder.documentLanguage = "pt";
		modelBuilder.inputDirectoryName = dirPath;
		modelBuilder.maxPhraseLength = 5;
		modelBuilder.minNumOccur = 1;
		modelBuilder.minPhraseLength = 1;
		modelBuilder.modelName = name;
		modelBuilder.serialize = true;
		modelBuilder.stemmer = stemmer;
		modelBuilder.stopwords = new StopwordsPortuguese();
		modelBuilder.vocabularyFormat = "skos";
		modelBuilder.vocabularyName = vocabPath;
		modelBuilder.setPositionsFeatures(false);
		modelBuilder.setKeyphrasenessFeature(false);
		modelBuilder.setThesaurusFeatures(false);
	}
	
	public void saveModel() throws Exception {
		filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
		modelBuilder.saveModel(filter);
	}
}
