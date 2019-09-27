package com.entopix.maui.stemmers;

import org.apache.lucene.analysis.pt.PortugueseLightStemmer;

public class LuceneMPTStemmer extends Stemmer {
	
	private static final long serialVersionUID = -6827556939199168366L;
	
	public static PortugueseLightStemmer stemmer; /*static for serializer*/

	public LuceneMPTStemmer() {
		stemmer = new PortugueseLightStemmer();
	}
	
	@Override
	public String stem(String str) {
		char[] s = str.toCharArray();
		int len = s.length;
		int index;
		try {
			index = stemmer.stem(s, len);
		} catch (ArrayIndexOutOfBoundsException e) {
			index = stemmer.stem(s, len-1);
		}
		str = str.substring(0, index);
		return str;
	}
}
