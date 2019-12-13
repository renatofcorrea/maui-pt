package com.entopix.maui.core;

import java.io.Serializable;

import com.entopix.maui.filters.MauiFilter;
import com.entopix.maui.stemmers.Stemmer;

public class ModelWrapper implements Serializable{
	
	private static final long serialVersionUID = 8767651479566767406L;

	private MauiFilter filter;

	/**
	 * Path of the documents folder where the model was trained
	 */
	private String trainedPath;
	
	/**
	 * Stemmer using when building the model
	 */
	private Stemmer stemmerUsed;
	
	/**
	 * Path of the controlled vocabulary used when building the model
	 */
	private String vocabUsedPath;

	public ModelWrapper(MauiFilter filter, String trainingPath, Stemmer stemmerUsed, String pathOfVocabUsed) {
		this.filter = filter;
		this.trainedPath = trainingPath;
		this.stemmerUsed = stemmerUsed;
		this.vocabUsedPath = pathOfVocabUsed;
	}

	public MauiFilter getFilter() {
		return filter;
	}

	public String getTrainingPath() {
		return trainedPath;
	}

	public Stemmer getStemmerUsed() {
		return stemmerUsed;
	}

	public String getVocabUsedPath() {
		return vocabUsedPath;
	}
}
