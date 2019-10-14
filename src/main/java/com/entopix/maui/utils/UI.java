package com.entopix.maui.utils;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

public class UI {
	public static void displayFileList(File[] fileList) {
		for (int i = 0; i < fileList.length; i++)
			System.out.println(i+1 + " - " + fileList[i].getName());
	}
	
	public static void displayDirContent(String dirPath) {
		File dir = new File(dirPath);
		File[] fileList = dir.listFiles();
		displayFileList(fileList);
	}

	public static void instructUser(String language) {
		if(language.equals("en"))
			System.out.println("Maui Standalone Runner\njava -jar maui-standalone.jar [train|test|run] options...\nPlease specify train or test or run and then the appropriate parameters.   ");
		else //if(language.equals("pt"))
			System.out.println("Maui Standalone Runner\njava -jar maui-standalone.jar [train|test|run] opções...\nFavor especificar train ou test ou run e em seguida os parâmetros apropriados.   ");
	}

	public static void printPTCIMessage(String language) {
		if(language.equals("en")) {
			instructUser(language);
			System.out.println("By default, MAUI is running example in pt language and CI documents.   ");
		} else { //if(language.equals("pt"))
			instructUser(language);
			System.out.println("Por padrão, MAUI está executando exemplo em português e documentos de CI.   ");
		}
	}
	
	public static void displayCredits() {
		System.out.println();
		System.out.println("----------------------CRÉDITOS----------------------");
		System.out.println("MAUI-PT - Maui adaptado para o português");
		System.out.println("Desenvolvido por Renato Corrêa e Rahmon Jorge,"); 
		System.out.println("como parte do projeto PIBITI/UFPE/CNPq em 2019.");
		System.out.println("MAUI original criado por Alyona Medelyan");
		System.out.println("-------------------------//-------------------------");
		System.out.println();
	}
	
	public static void showElapsedTime(Instant start, Instant finish) {
		double seconds = (Duration.between(start, finish).toMillis()/1000);
		int minutes = (int) seconds/60;
		int remainingSec = (int) (seconds - (minutes*60));
		System.out.println(minutes + " minutes and " + remainingSec + " seconds.");
	}
}
