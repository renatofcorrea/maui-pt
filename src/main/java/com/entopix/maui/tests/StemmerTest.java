package com.entopix.maui.tests;

import com.entopix.maui.stemmers.Stemmer;

import ptstemmer.support.PTStemmerUtilities;

public class StemmerTest {
	
	public static boolean matchstemform(Stemmer stemmer, String singular, String plural) {
		try {
			String stemsingular = PTStemmerUtilities.removeDiacritics(stemmer.stem(singular));
			String stemplural = PTStemmerUtilities.removeDiacritics(stemmer.stem(plural));
			return (stemsingular.equalsIgnoreCase(stemplural));
		} catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static int matchstemerrors(Stemmer stemmer, String[] singular, String []plural) {
		int errors = 0;
    	for(int i=0; i < singular.length; i++) {
			if(!matchstemform(stemmer,singular[i],plural[i])) {
				System.out.println("Stem Error: "+singular[i]+" "+plural[i]);
				errors++;
			}
		}
		return errors;
	}
	
	public static int teststemmer(Stemmer stemmer) {
    	String[] singular = {"álcool","aprendiz","biblioteca","bibliotecário","canção","cão","campus","cartel","ciência","cientista","cupom","informação","gestor","natural","natureza","mãe","mão","oficial","país","papel","pesquisa","pesquisador","paizinho","pai","publicação","público","quartil","filho","filhinho","engenharia","engenho","engenheiro","réptil","réptil","social","têxtil","troféu","vilão","vilão"};
    	String[] plural = {"álcoois","aprendizes","bibliotecas","bibliotecários","canções","cães","campi","cartéis","ciências","cientistas","cupons","informações","gestores","naturais","naturezas","mães","mãos","oficiais","países","papéis","pesquisas","pesquisadores","paizinhos","pais","publicações","públicos","quartis","filhos","filhinhos","engenharias","engenhos","engenheiros","répteis","réptis","sociais","têxteis","troféus","vilãos","vilões"};
 
    	return matchstemerrors(stemmer,singular,plural);
    	
    }
}
