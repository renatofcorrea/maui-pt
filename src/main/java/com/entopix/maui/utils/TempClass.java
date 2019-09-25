package com.entopix.maui.utils;

import com.entopix.maui.stemmers.LuceneBRStemmer;
import com.entopix.maui.stemmers.LucenePTStemmer;
import com.entopix.maui.stemmers.NewPortugueseStemmer;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stopwords.Stopwords;
import com.entopix.maui.stopwords.StopwordsPortuguese;
import com.entopix.maui.tests.StemmerTest;

@SuppressWarnings("unused")
public class TempClass {
	
	private static String dataPath = Paths.getDataPath();
	private static String testFilePath = dataPath + "\\docs\\corpusci\\full_texts\\test60\\Artigo32.txt";
	private static String modelPath = dataPath + "\\models\\StemmerTestModel";
	private static String vocabPath = dataPath + "\\vocabulary\\TBCI-SKOS_pt.rdf";
	private static Stopwords stopwords = new StopwordsPortuguese();
	
	private static String[] stemOptions = {"-S",""};
	private static Stemmer stemmer = new PortugueseStemmer();
	
	public static void main(String[] args) {
		
		System.out.println("-> Standard (Orengo): ");
		System.out.println("Total Errors: " + StemmerTest.teststemmer(stemmer));
		System.out.println();
		
		stemOptions[1] = "Savoy";
		stemmer = new NewPortugueseStemmer(stemOptions);
		System.out.println("-> NewPortugueseStemmer (Savoy): ");
		System.out.println("Total Errors: " + StemmerTest.teststemmer(stemmer));
		System.out.println();
		
		stemOptions[1] = "Porter";
		stemmer = new NewPortugueseStemmer(stemOptions);
		System.out.println("-> NewPortugueseStemmer (Porter): ");
		System.out.println("Total Errors: " + StemmerTest.teststemmer(stemmer));
		System.out.println();
		
		stemOptions[1] = "Orengo";
		stemmer = new NewPortugueseStemmer(stemOptions);
		System.out.println("-> NewPortugueseStemmer (Orengo): ");
		System.out.println("Total Errors: " + StemmerTest.teststemmer(stemmer));
		System.out.println();
		
		stemmer = new LuceneBRStemmer();
		System.out.println("-> Lucene (BRStemmer): ");
		System.out.println("Total Errors: " + StemmerTest.teststemmer(stemmer));
		System.out.println();
		
		stemmer = new LucenePTStemmer();
		System.out.println("-> Lucene (Orengo): ");
		System.out.println("Total Errors: " + StemmerTest.teststemmer(stemmer));
		System.out.println();
		
	}

	private static void stemAll() {
		System.out.println(stemmer.stemString("ciência da informação"));
		System.out.println(stemmer.stemString("cientista da informação"));
		System.out.println(stemmer.stemString("pesquisa pesquisadores"));
		System.out.println(stemmer.stemString("bibliotecários bibliotecas"));
		System.out.println(stemmer.stemString("gestores  gestão"));
		System.out.println(stemmer.stemString("hipertextos hipertexto"));
		System.out.println(stemmer.stemString("público publicações"));
		System.out.println(stemmer.stemString("publicação publicações"));
		System.out.println(stemmer.stemString("realizada realia"));
		System.out.println(stemmer.stemString("realizada realismo"));
		System.out.println(stemmer.stemString("soc socialmente"));
		System.out.println(stemmer.stemString("soc sociais"));
		System.out.println(stemmer.stemString("informação informacional"));
		System.out.println();
	}
	
	private static void stemAll2() {
		System.out.println(stemmer.stemString("ciência"));
		System.out.println(stemmer.stemString("informação"));
		System.out.println(stemmer.stemString("cientista"));
		System.out.println(stemmer.stemString("pesquisa"));
		System.out.println(stemmer.stemString("pesquisadores"));
		System.out.println(stemmer.stemString("bibliotecários"));
		System.out.println(stemmer.stemString("bibliotecas"));
		System.out.println(stemmer.stemString("gestores"));
		System.out.println(stemmer.stemString("gestão"));
		System.out.println(stemmer.stemString("hipertexto"));
		System.out.println(stemmer.stemString("hipertextos"));
		System.out.println(stemmer.stemString("público"));
		System.out.println(stemmer.stemString("publicações"));
		System.out.println(stemmer.stemString("publicação"));
		System.out.println(stemmer.stemString("realizada"));
		System.out.println(stemmer.stemString("realia"));
		System.out.println(stemmer.stemString("realismo"));
		System.out.println(stemmer.stemString("socialmente"));
		System.out.println(stemmer.stemString("soc"));
		System.out.println(stemmer.stemString("sociais"));
		System.out.println(stemmer.stemString("informacional"));
		System.out.println();
	}
	
	private static void stemAll3() {
		System.out.println(stemmer.stemString("álcool"));
		System.out.println(stemmer.stemString("álcoois"));
		System.out.println(stemmer.stemString("aprendiz"));
		System.out.println(stemmer.stemString("aprendizes"));
		System.out.println(stemmer.stemString("biblioteca"));
		System.out.println(stemmer.stemString("bibliotecas"));
		System.out.println(stemmer.stemString("bibliotecário"));
		System.out.println(stemmer.stemString("bibliotecários"));
		System.out.println(stemmer.stemString("canção"));
		System.out.println(stemmer.stemString("canções"));
		System.out.println(stemmer.stemString("cão"));
		System.out.println(stemmer.stemString("cães"));
		System.out.println(stemmer.stemString("campus"));
		System.out.println(stemmer.stemString("campi"));
		System.out.println(stemmer.stemString("cartel"));
		System.out.println(stemmer.stemString("cartéis"));
		System.out.println(stemmer.stemString("ciência"));
		System.out.println(stemmer.stemString("ciências"));
		System.out.println(stemmer.stemString("cientista"));
		System.out.println(stemmer.stemString("cientistas"));
		System.out.println(stemmer.stemString("cupom"));
		System.out.println(stemmer.stemString("cupons"));
		System.out.println(stemmer.stemString("da"));
		System.out.println(stemmer.stemString("das"));
		System.out.println(stemmer.stemString("informação"));
		System.out.println(stemmer.stemString("informações"));
		System.out.println(stemmer.stemString("gestor"));
		System.out.println(stemmer.stemString("gestores"));
	}
}
