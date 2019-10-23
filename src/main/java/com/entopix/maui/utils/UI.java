package com.entopix.maui.utils;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

public class UI {
	
	public static void displayMainMenu() {
		System.out.println("\n1 - Criar modelo  ");
		System.out.println("2 - Selecionar modelo");
		System.out.println("3 - Testar modelo em diretório  ");
		System.out.println("4 - Executar modelo em arquivo  ");
		System.out.println("5 - Executar teste estruturado  ");
		System.out.println("6 - Sobre");
		System.out.println("0 - Sair  ");
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

	public static void instructUser(String language) {
		if (language.equals("en")) {
			System.out.println("Maui Standalone Runner\njava -jar maui-standalone.jar [train|test|run] options...\nPlease specify train or test or run and then the appropriate parameters.   ");
		} else if (language.equals("pt")) {
			System.out.println("Maui Standalone Runner\njava -jar maui-standalone.jar [train|test|run] opções...\nFavor especificar train ou test ou run e em seguida os parâmetros apropriados.   ");
		}
	}

	public static void printPTCIMessage(String language) {
		instructUser(language);
		if (language.equals("en")) {
			System.out.println("By default, MAUI is running example in pt language and CI documents.   ");
		} else if (language.equals("pt")){
			System.out.println("Por padrão, MAUI está executando exemplo em português e documentos de CI.   ");
		}
	}
	
	public static void showElapsedTime(Instant start, Instant finish) {
		double seconds = (Duration.between(start, finish).toMillis()/1000);
		int minutes = (int) seconds/60;
		int remainingSec = (int) (seconds - (minutes*60));
		System.out.println(minutes + " minutes and " + remainingSec + " seconds.");
	}
	
	public static void showInvalidOptionMessage() {
		System.out.println("ERRO: Opção Inválida.");
	}
	
	public static void showDirectoryNotFoundMessage(String dir) {
		System.out.println("ERRO: O diretório '" + dir + "' não foi encontrado.");
	}
}
