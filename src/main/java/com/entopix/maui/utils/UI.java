package com.entopix.maui.utils;

import java.io.File;

public class UI {
	/**
	 * Displays a list of the folders and text files in a directory.
	 */
	public static void displayDirContent(File[] fileList) {
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isDirectory()) {
				System.out.println(i+1 + " - " + fileList[i].getName());
			} else if(fileList[i].getName().endsWith(".txt")) {
				System.out.println(i+1 + " - " + fileList[i].getName());
			}
		}
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
		} else //if(language.equals("pt")) 
		{
			instructUser(language);
			System.out.println("Por padrão, MAUI está executando exemplo em português e documentos de CI.   ");
		}
	}
}
