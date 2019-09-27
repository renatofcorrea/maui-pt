package com.entopix.maui.stemmers;

import java.io.Serializable;

import weka.core.stemmers.PTStemmer;

public class NewPortugueseStemmer extends Stemmer implements Serializable {

	private static final long serialVersionUID = 3903282839721792340L;

	public static PTStemmer stemmer; /*static for serializer*/
	public String type;
	
	public NewPortugueseStemmer(String[] options) {
		
		type = options[1];
		
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
