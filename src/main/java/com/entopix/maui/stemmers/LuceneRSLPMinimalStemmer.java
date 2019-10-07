package com.entopix.maui.stemmers;

import org.apache.lucene.analysis.pt.PortugueseMinimalStemmer;

public class LuceneRSLPMinimalStemmer extends Stemmer {
	
	private static final long serialVersionUID = -6827556939199168366L;
	
	public static PortugueseMinimalStemmer stemmer; /*static for serializer*/

	public LuceneRSLPMinimalStemmer() {
		stemmer = new PortugueseMinimalStemmer();
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
