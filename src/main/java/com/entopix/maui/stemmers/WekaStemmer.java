package com.entopix.maui.stemmers;

import java.io.Serializable;

import weka.core.stemmers.PTStemmer;

public class WekaStemmer extends Stemmer implements Serializable {

	private static final long serialVersionUID = 3903282839721792340L;

	private static Stemmer instance;
	public static PTStemmer staticStemmer; /*static to serialize*/
	
	public static Stemmer getInstance() {
		if(instance == null) {
			instance = new WekaStemmer();
		}
		return instance;
	}
	
	private WekaStemmer() {
		if(staticStemmer == null) {
			staticStemmer = new PTStemmer();
		}
	}
	
	public static void setOptions(String option) {
		String[] opt = new String[] {"-S",""};
		opt[1] = option;
		
		try {
			if(staticStemmer == null) {
				staticStemmer = new PTStemmer();
			}
			staticStemmer.setOptions(opt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String stem(String str) {
		return staticStemmer.stem(str);
	}

}
