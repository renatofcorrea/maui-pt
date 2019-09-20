package com.entopix.maui.stemmers;

import java.io.Serializable;

import weka.core.stemmers.PTStemmer;

public class NewPortugueseStemmer extends Stemmer implements Serializable {

	private static final long serialVersionUID = 3903282839721792340L;

	private static PTStemmer stemmer;
	
	public NewPortugueseStemmer(String[] options) {
		stemmer = new PTStemmer();
		try {
			stemmer.setOptions(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String stem(String str) {
		return stemmer.stem(str);
	}

}
