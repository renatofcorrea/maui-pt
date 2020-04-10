package com.entopix.maui.tests;

import java.io.File;
import java.util.Scanner;

import com.entopix.maui.utils.MauiFileUtils;

/**
 * Class used for tests during development. Should be removed before release.
 * @author Rahmon Jorge
 *
 */

public class TestClass {
	
	public static void main(String[] args) throws Exception {
		String dir = "C:\\Users\\Silvania\\Desktop\\Rahmon";
		Scanner sc = new Scanner(System.in);
		File file = MauiFileUtils.browseFile(dir, "", sc);
		
		System.out.println(file.getName());
		sc.close();
	}
}
