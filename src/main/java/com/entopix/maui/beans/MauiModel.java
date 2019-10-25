package com.entopix.maui.beans;

import com.entopix.maui.stemmers.Stemmer;
/**
 * This class is a reference to a maui model file. It is used to wrap the model properties. Useful when managing groups of models.
 * @author PC1
 *
 */
public class MauiModel {
	
	private String name;
	private String path;
	private Stemmer stemmer;
	private int docType;
	private String trainDirPath;
	
	public MauiModel(String name, String path, Stemmer stemmer, int docType, String trainDirPath) {
		this.name = name;
		this.path = path;
		this.stemmer = stemmer;
		this.docType = docType;
		this.trainDirPath = trainDirPath;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public Stemmer getStemmer() {
		return stemmer;
	}

	public int getDocType() {
		return docType;
	}

	public String getTrainDirPath() {
		return trainDirPath;
	}
	
	
	
}
