package com.entopix.maui.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.main.MauiWrapper;
import com.entopix.maui.stemmers.NewPortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stopwords.Stopwords;
import com.entopix.maui.stopwords.StopwordsPortuguese;
import com.entopix.maui.util.Topic;

import weka.core.stemmers.PTStemmer;

@SuppressWarnings("unused")
public class TempClass {
	
	private static String dataPath = Paths.getDataPath();
	private static String testFilePath = dataPath + "\\docs\\corpusci\\full_texts\\test60\\Artigo32.txt";
	private static String modelPath = dataPath + "\\models\\StemmerTestModel";
	private static String vocabPath = dataPath + "\\vocabulary\\TBCI-SKOS_pt.rdf";
	private static Stopwords stopwords = new StopwordsPortuguese();
	
	private static String[] stemOptions = {"-S","Savoy"};
	private static Stemmer stemmer = new NewPortugueseStemmer(stemOptions);
	
	public static void main(String[] args) {
		
		System.out.println("-> Savoy: ");
		stemAll2();
		
		stemOptions[1] = "Porter";
		stemmer = new NewPortugueseStemmer(stemOptions);
		System.out.println("-> Porter: ");
		stemAll2();
		
		stemOptions[1] = "Orengo";
		stemmer = new NewPortugueseStemmer(stemOptions);
		System.out.println("-> Orengo: ");
		stemAll2();
		
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
}
