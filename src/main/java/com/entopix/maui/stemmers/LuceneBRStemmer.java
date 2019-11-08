package com.entopix.maui.stemmers;

import org.apache.lucene.analysis.br.BrazilianStemmer;

public class LuceneBRStemmer extends Stemmer {
	
	private static final long serialVersionUID = 2658970678094941051L;
	
	public static BRStemmer stemmer; /*static for serializer*/
	
	public LuceneBRStemmer() {
		stemmer = new BRStemmer();
	}
	
	private class BRStemmer extends BrazilianStemmer {
		@Override
		public String stem(String str) {
			String stm = super.stem(str);
			if (stm == null) {
				return "";
			} else {
				return stm;
			}
		}
	}

	@Override
	public String stem(String str) {
		return stemmer.stem(str);
	}
}
