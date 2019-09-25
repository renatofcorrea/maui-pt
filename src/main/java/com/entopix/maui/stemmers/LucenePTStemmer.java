package com.entopix.maui.stemmers;

import org.apache.lucene.analysis.pt.PortugueseStemmer;

public class LucenePTStemmer extends Stemmer {
	
	private static final long serialVersionUID = 4577282386684332746L;
	
	public static PortugueseStemmer stemmer; /*static for serializer*/

	public LucenePTStemmer() {
		stemmer = new PortugueseStemmer();
	}
	
	@Override
	public String stem(String str) {
		char[] s = str.toCharArray();
		int len = s.length;
		int index;
		try {
			index = stemmer.stem(s, len);
		} catch (Exception e) {
			index = stemmer.stem(s, len-1);
		}
		str = str.substring(0, index);
		return str;
	}
	
}
