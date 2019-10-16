package com.entopix.maui.stemmers;

import java.io.Serializable;

import weka.core.stemmers.PTStemmer;

public class WekaStemmerSavoy extends Stemmer implements Serializable {
	
	private static final long serialVersionUID = -6777881452464337529L;
	
	private static PTStemmer stemmer;
	
	public WekaStemmerSavoy() {
		stemmer = new PTStemmer();
		try {
			stemmer.setOptions(new String[] {"-S","Savoy"});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String stem(String str) {
		return stemmer.stem(str);
	}
}
