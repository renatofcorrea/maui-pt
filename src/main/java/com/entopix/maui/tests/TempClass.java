package com.entopix.maui.tests;

import java.util.ArrayList;
import java.util.List;

import com.entopix.maui.utils.MauiPTUtils;

public class TempClass {
	
	public static void main(String[] args) throws Exception {
		
		List<ArrayList<String[]>> abstractsMatrixes = new ArrayList<ArrayList<String[]>>();
		List<ArrayList<String[]>> fulltextsMatrixes = new ArrayList<ArrayList<String[]>>();
		
		List<String[]> absMatrix = new ArrayList<String[]>();
		absMatrix.add(MauiPTUtils.formatArray("model_abstracts_generic_test_00", new double[] {0,0,0,0,0,0,0}));
		absMatrix.add(MauiPTUtils.formatArray("model_abstracts_generic_test_01", new double[] {0,0,0,0,0,0,0}));
		List<String[]> ftxtMatrix = new ArrayList<String[]>();
		
		abstractsMatrixes.add((ArrayList<String[]>) absMatrix);
		fulltextsMatrixes.add((ArrayList<String[]>) ftxtMatrix);
		
		String[] header = {"MODEL NAME","AVG KEY","STDEV KEY","AVG PRECISION","STDEV PRECISION","AVG RECALL","STDEV RECALL","F-MEASURE"};
		String h = MauiPTUtils.formatHeader(header);
		
		String s = "--- STRUCTURED TEST RESULTS ---";
		s += "\n--- ABSTRACTS ---\n";
		s += "\n>>> Results based on 30 documents:\n";
		s += MauiPTUtils.matrixToString(h, abstractsMatrixes.get(0));
		s += "\n>>> Results based on 60 documents:";
		s += MauiPTUtils.matrixToString(h, abstractsMatrixes.get(1));
		s += "\n";
		s += "\n--- FULL TEXTS ---";
		s += "\n>>> Results based on 30 documents:\n";
		s += MauiPTUtils.matrixToString(h, fulltextsMatrixes.get(0));
		s += "\n>>> Results based on 60 documents:";
		s += MauiPTUtils.matrixToString(h, fulltextsMatrixes.get(1));
		
		System.out.println(s);
	}
}
