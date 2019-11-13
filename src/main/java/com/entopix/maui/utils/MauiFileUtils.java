package com.entopix.maui.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.entopix.maui.main.StandaloneMain;
/**
 * Provides paths to avoid path conflicts and methods for managing and processing files.
 * @author Rahmon Jorge
 */
public class MauiFileUtils {
	
	private static String modelsDirPath = getDataPath() + "\\models";
	
	public static final String getRootPath() {
		try {
			return new File(".").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	public static final String getDataPath() {
		String rootPath = getRootPath();
		if(rootPath.endsWith("maui-pt")) return rootPath + "\\data";
		else return rootPath + "\\";
	}
	
	public static String getModelsDirPath() {
		return modelsDirPath;
	}
	
	public void setModelsDirPath(String path) {
		modelsDirPath = path;
	}
	
	/** Verifies if a file path exists. */
	public static boolean exists(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Verifies if a folder is empty.
	 * @param dir
	 * @return true if and only if the specified file is a directory and is empty.
	 */
	public static boolean isEmpty(File dir) {
		if (dir.isDirectory()) {
			if (dir.listFiles().length == 0) return true;
		}
		return false;
	}
	
	/**
	 * Verifies if a folder is empty.
	 * @param path
	 * @return true if and only if the specified pathname is a directory and is empty.
	 */
	public static boolean isEmpty(String dirPath) {
		File file = new File(dirPath);
		if (file.isDirectory()) {
			if (file.list().length == 0) return true;
		}
		return false;
	}
	
	public static void displayDirContent(File dir) {
		displayFileList(dir.listFiles());
	}
	
	public static void displayDirContent(String dirPath) {
		displayFileList(new File(dirPath).listFiles());
	}
	
	public static void displayFileList(File[] fileList) {
		for (int i = 0; i < fileList.length; i++) {
			System.out.println(i+1 + " - " + fileList[i].getName());
		}
	}
	
	public static File[] filterFileList(String dirPath, String filterMethod) {
		File[] fileArray = new File(dirPath).listFiles();
		ArrayList<File> newArray = new ArrayList<File>();
		
		for (File f : fileArray) {
			if (f.getName().contains(filterMethod)) newArray.add(f);
		}
		return newArray.toArray(new File[newArray.size()]);
	}
	
	public static File[] filterFileList(File[] fileArray, String filterMethod) {
		ArrayList<File> newArray = new ArrayList<File>();
		
		for (File f : fileArray) {
			if (f.getName().contains(filterMethod)) newArray.add(f);
		}
		return newArray.toArray(new File[newArray.size()]);
	}
	
	public static File chooseFileFromDirectory(String dirPath) {
		return chooseFileFromList(new File(dirPath).listFiles());
	}
	
	public static File chooseFileFromList(File[] fileList) {
		if (fileList == null) throw new NullPointerException();
		int fileChoice;
		displayFileList(fileList);
		System.out.print("Opção: ");
		fileChoice = StandaloneMain.SCAN.nextInt();
		StandaloneMain.SCAN.nextLine();
		return fileList[fileChoice - 1];
	}
	
	public static void printOnFile(String s, String filePath) throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(filePath));
		pw.printf(s);
		pw.close();
	}
	
	public static List<String> readKeyFromFile(String keysPath) throws FileNotFoundException {
		File keys = new File(keysPath);
		Scanner scanner = new Scanner(keys);
		String content = scanner.useDelimiter("\\Z").next();
		scanner.close();
		List<String> topics = Arrays.asList(content.split("\n"));
		for (int i = 0; i < topics.size(); i++) {
			topics.set(i, topics.get(i).replace("\r", ""));
		}
		return topics;
	}
}
