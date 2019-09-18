package com.entopix.maui.stemmers;

import java.io.Serializable;

import weka.core.stemmers.PTStemmer;

public class NewPortugueseStemmer extends Stemmer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3903282839721792340L;

	private PTStemmer stemmer;
	
	public NewPortugueseStemmer() {
		stemmer = new PTStemmer();
		String[] options = {"-S","SAVOY"};
		try {
			stemmer.setOptions(options);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public String stem(String str) {
		return stemmer.stem(str);
	}

}
