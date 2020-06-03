package com.entopix.maui.tests;

import java.util.Scanner;

import com.entopix.maui.core.MauiCore;
import com.entopix.maui.core.ModelWrapper;

public class TestClass {
	
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.println("Escreva o texto a ter palavras chave extraidas e removidas e arrancadas de seu contexo: ");
		String text = sc.nextLine();
		text = text.replaceAll("[ \n\t\r]{2,}"," ");
		
		ModelWrapper model = new ModelWrapper(MauiCore.buildModel(), MauiCore.getTrainDirPath(), MauiCore.getStemmer(), MauiCore.getVocabPath());
		MauiCore.setModel(model);
		MauiCore.runMauiWrapperOnString(text);
	}
}
