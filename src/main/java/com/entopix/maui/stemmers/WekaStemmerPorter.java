package com.entopix.maui.stemmers;

import java.io.Serializable;

import weka.core.stemmers.PTStemmer;

public class WekaStemmerPorter extends Stemmer implements Serializable {
	
	private static final long serialVersionUID = -6777881452464337529L;
	
	private static PTStemmer stemmer;
	
	public WekaStemmerPorter() {
		stemmer = new PTStemmer();
		try {
			stemmer.setOptions(new String[] {"-S","Porter"});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String stem(String str) {
		return stemmer.stem(str);
	}
}
