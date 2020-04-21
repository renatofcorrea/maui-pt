package com.entopix.maui.utils;

import java.io.File;
import java.util.List;

import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.util.Topic;

public class UI {
	
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

	public static void instructUser(String language) {
		if (language.equals("pt")) System.out.println("Maui Standalone Runner\njava -jar maui-standalone.jar [train|test|run] opções...\nFavor especificar train ou test ou run e em seguida os parâmetros apropriados.   ");
		else System.out.println("Maui Standalone Runner\njava -jar maui-standalone.jar [train|test|run] options...\nPlease specify train or test or run and then the appropriate parameters.   ");
	}

	public static void printPTCIMessage(String language) {
		if (language.equals("pt")) System.out.println("Por padrão, MAUI está executando exemplo em português e documentos de CI.   ");
		else System.out.println("By default, MAUI is running example in pt language and CI documents.   ");
	}
	
	public static void showInvalidOptionMessage() {
		System.out.println("\nERRO: Opção Inválida.");
	}
	
	public static void showFileNotFoundMessage(String f) {
		System.out.println("ERRO: O arquivo ou diretório '" + f + "' não foi encontrado.");
	}
	
	public static void displayTopics(List<MauiTopics> topics) {
		for (MauiTopics doc : topics) {
			System.out.println("\nARQUIVO: " + new File(doc.getFilePath()).getName());
			for (Topic topic : doc.getTopics()) {
				System.out.println("Tópico: " + topic.getTitle());
			}
		}
	}
}
