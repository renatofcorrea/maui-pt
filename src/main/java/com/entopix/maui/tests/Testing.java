package com.entopix.maui.tests;

import java.util.ArrayList;
import java.util.List;

import com.entopix.maui.core.MauiCore;

/**
 * Class used for tests during development. Should be removed before release.
 * @author Rahmon Jorge
 *
 */

public class Testing {
	
	public static void main(String[] args) throws Exception {
		
		String s = "ciência da informação";
		
		String keysPath = "C:\\Users\\PC1\\git\\maui-pt\\data\\docs\\corpusci\\fulltexts\\test60\\Artigo06.key";
		
		List<String> extracted = new ArrayList<>();
		
		extracted.add("INterdisciplinaridade");
		extracted.add("Ciencia da informacao");
		
		MauiCore.evaluateTopicsSingle(keysPath, extracted, 10, true);
		
	}
}
