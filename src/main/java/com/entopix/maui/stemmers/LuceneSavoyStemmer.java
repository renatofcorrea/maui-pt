package com.entopix.maui.stemmers;

import org.apache.lucene.analysis.pt.PortugueseLightStemmer;

public class LuceneSavoyStemmer extends Stemmer {

	private static final long serialVersionUID = -435397420345476573L;
	
	public static PortugueseLightStemmer stemmer; /*static for serializer*/

	public LuceneSavoyStemmer() {
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
