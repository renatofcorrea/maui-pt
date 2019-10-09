package com.entopix.maui.beans;

import java.io.File;

import com.entopix.maui.filters.MauiFilter;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.main.MauiModelBuilder;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stopwords.StopwordsPortuguese;
import com.entopix.maui.util.DataLoader;

public class MauiModel {
	
	private MauiModelBuilder modelBuilder;
	private MauiFilter filter;
	
	private File file;
	private ModelDocType docType;
	
	public MauiModel(String dirPath, String modelPath, Stemmer stemmer, String vocabPath, ModelDocType docType) throws MauiFilterException {
		this.file = new File(modelPath);
		this.docType = docType;
		
		modelBuilder = new MauiModelBuilder();
		modelBuilder.documentEncoding = "UTF-8";
		modelBuilder.documentLanguage = "pt";
		modelBuilder.inputDirectoryName = dirPath;
		modelBuilder.maxPhraseLength = 5;
		modelBuilder.minNumOccur = 1;
		modelBuilder.minPhraseLength = 1;
		modelBuilder.modelName = modelPath;
		modelBuilder.serialize = true;
		modelBuilder.stemmer = stemmer;
		modelBuilder.stopwords = new StopwordsPortuguese();
		modelBuilder.vocabularyFormat = "skos";
		modelBuilder.vocabularyName = vocabPath;
		modelBuilder.setPositionsFeatures(false);
		modelBuilder.setKeyphrasenessFeature(false);
		modelBuilder.setThesaurusFeatures(false);
		filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
	}
	
	public File getFile() {
		return file;
	}
	
	public ModelDocType ModelDocType() {
		return docType;
	}
	
	public void saveModel() throws Exception {
		filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
		modelBuilder.saveModel(filter);
	}
	
}
