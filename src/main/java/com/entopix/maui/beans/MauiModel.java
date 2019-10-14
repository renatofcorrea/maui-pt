package com.entopix.maui.beans;

import java.io.File;

import com.entopix.maui.filters.MauiFilter;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.main.MauiModelBuilder;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stopwords.Stopwords;
import com.entopix.maui.tests.StructuredTest;
import com.entopix.maui.util.DataLoader;
import com.entopix.maui.vocab.Vocabulary;
/**
 * This class is a reference to a maui model file. It is used to identify model properties and helps managing groups of models.
 * @author PC1
 *
 */
public class MauiModel {
	
	private File file;
	private Stemmer stemmer;
	private ModelDocType docType;
	
	private MauiModelBuilder modelBuilder;
	private MauiFilter filter;
	
	public MauiModel(String dirPath, String modelPath, Stemmer stemmer, Stopwords stopwords, String vocabPath, String vocabFormat, String encoding, String language, ModelDocType docType) throws MauiFilterException {
		this.file = new File(modelPath);
		this.stemmer = stemmer;
		this.docType = docType;
		
		modelBuilder = new MauiModelBuilder();
		modelBuilder.documentEncoding = encoding;
		modelBuilder.documentLanguage = language;
		modelBuilder.inputDirectoryName = dirPath;
		modelBuilder.maxPhraseLength = 5;
		modelBuilder.minNumOccur = 2;
		modelBuilder.minPhraseLength = 1;
		modelBuilder.modelName = modelPath;
		modelBuilder.serialize = true;
		modelBuilder.stemmer = stemmer;
		modelBuilder.stopwords = stopwords;
		
		Vocabulary vocab = new Vocabulary();
		vocab.setReorder(false);
		vocab.setSerialize(true);
		vocab.setEncoding(encoding);
		vocab.setLanguage(language);
		vocab.setStemmer(stemmer);
		vocab.setStopwords(stopwords);
		vocab.setVocabularyName(vocabPath);
		vocab.initializeVocabulary(vocabPath, vocabFormat);
		modelBuilder.setVocabulary(vocab);
		
		modelBuilder.setPositionsFeatures(false);
		modelBuilder.setKeyphrasenessFeature(false);
		modelBuilder.setThesaurusFeatures(false);
		
		filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
	}
	
	public File getFile() {
		return file;
	}
	
	/**
	 * @return A instance of the stemmer class used to build this model.
	 */
	public Stemmer getStemmer() {
		return stemmer;
	}
	
	public ModelDocType getDocType() {
		return docType;
	}
	
	public void saveModel() throws Exception {
		filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
		modelBuilder.saveModel(filter);
	}
	
}
