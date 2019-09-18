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
	
	private static Stemmer stemmer = new NewPortugueseStemmer();
	
	public static void main(String[] args) {
		String a = "palavra";
		System.out.println(stemmer.stem(a));
	}
}
