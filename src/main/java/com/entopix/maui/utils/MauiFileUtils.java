package com.entopix.maui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
		if(rootPath.endsWith("maui-pt")) return rootPath + "\\data\\"; // likely to be running on console
		else return rootPath + "\\"; // likely to be running on IDE
	}
	
	public static String getModelsDirPath() {
		return modelsDirPath;
	}
	
	public void setModelsDirPath(String path) {
		modelsDirPath = path;
	}
	
	public static void serializeObject(Object obj, String path) {
		FileOutputStream fout = null;
		ObjectOutputStream oos = null;
		
		try {
			fout = new FileOutputStream(path);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(obj);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static Object deserializeObject(String path) {
		Object object = null;
		FileInputStream fin = null;
		ObjectInputStream ois = null;
		
		try {
			fin = new FileInputStream(path);
			ois = new ObjectInputStream(fin);
			object = ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return object;
	}
	
	/** 
	 * Verifies if a file path exists.
	 */
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
		displayNumberedFileList(dir.listFiles());
	}
	
	public static void displayDirContent(String dirPath) {
		displayNumberedFileList(new File(dirPath).listFiles());
	}
	
	public static void displayNumberedFileList(File[] fileList) {
		for (int i = 0; i < fileList.length; i++) {
			System.out.println(i+1 + " - " + fileList[i].getName());
		}
	}
	
	/**
	 * Returns a array of strings containing the file paths.
	 * @return
	 */
	public static String[] getFileListPaths(File[] files) {
		String[] paths = new String[files.length];
		
		int i;
		for (i = 0; i < files.length; i++) {
			paths[i] = files[i].getPath();
		}
		
		return paths;
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
	
	/**
	 * Prints a numbered file list for the user to choose, then returns the file choice.
	 * @param fileList
	 * @return fileChoice
	 */
	public static File chooseFileFromList(File[] fileList) {
		if (fileList == null) throw new NullPointerException();
		int fileChoice;
		displayNumberedFileList(fileList);
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
	
	/** 
	 * Reads the keywords in a file. Assumes that every word is separated by a newline character. 
	 * @return a list of keywords
	 * */
	public static List<String> readKeyFromFile(String filePath) throws Exception {
		File keys = new File(filePath);
		Scanner scanner = new Scanner(keys);
		String content = scanner.useDelimiter("\\Z").next();
		scanner.close();
		List<String> topics = Arrays.asList(content.split("\n"));
		for (int i = 0; i < topics.size(); i++) {
			topics.set(i, topics.get(i).replace("\r", ""));
		}
		return topics;
	}
	
	/**
	 * Reads keywords from files with specified format in specified directory.
	 * Assumes that every word is separated by a newline character.
	 * @return a list of keywords for every file.
	 */
	public static List<List<String>> readKeyFromFolder(String dir, String format) throws Exception {
		File[] files = filterFileList(dir, format);
		List<List<String>> topics = new ArrayList<>();
		
		for (File f : files) {
			topics.add(readKeyFromFile(f.getPath()));
		}
		
		return topics;
	}
}
