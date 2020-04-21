package com.entopix.maui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
	
	/**
	 * Gets the path to the "data" folder in the project's directory.
	 * @return
	 */
	public static final String getDataPath() {
		String rootPath = getRootPath();
		if(rootPath.endsWith("maui-pt")) return rootPath + "\\data"; // likely to be running on console
		else return rootPath; // likely to be running on IDE or other platform
	}
	
	public static String getModelsDirPath() {
		return modelsDirPath;
	}
	
	public void setModelsDirPath(String path) {
		modelsDirPath = path;
	}
	
	/**
	 * Serializes a object in the specified path
	 * @param obj
	 * @param path
	 */
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
	
	/**
	 * Deserializes the object in the specified path
	 * @param path
	 * @return
	 */
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
	
	/** Verifies if a file path exists.
	 * @throws FileNotFoundException */
	public static boolean exists(String path) throws FileNotFoundException {
		if (path == null) throw new NullPointerException("Path argument is null.");
		File file = new File(path);
		return file.exists();
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
	
	public static void displayNumberedFileList(File[] fileList) {
		for (int i = 0; i < fileList.length; i++) {
			System.out.println(i+1 + " - " + fileList[i].getName());
		}
	}
	
	/**
	 * Takes a array of files and returns its paths.
	 */
	public static String[] getFileListPaths(File[] files) {
		String[] paths = new String[files.length];
		
		int i;
		for (i = 0; i < files.length; i++) {
			paths[i] = files[i].getPath();
		}
		
		return paths;
	}
	
	/**
	 * Gets the names of the files in a file list.
	 * @param files
	 * @param removeFileExtension
	 * @return
	 */
	public static String[] getFileNames(File[] files, boolean removeFileExtension) {
		String[] names = new String[files.length];
		
		int i;
		for (i = 0; i < files.length; i++) {
			names[i] = files[i].getName();
			if (removeFileExtension) {
				names[i] = MPTUtils.removeFileExtension(names[i]);
			}
		}
		
		return names;
	}
	
	/**
	 * Gets the names of the files in a directory on the specified path.
	 * @param dir
	 * @return
	 */
	public static String[] getFileNames(String dir, boolean removeFileExtension) {
		return getFileNames(new File(dir).listFiles(), removeFileExtension);
	}
	
	/**
	 * Filter the files inside a directory using a filter method.
	 * If "filterDirs" is false, it will always add directories to the filtered list, regardless of their names.
	 * @param dirPath
	 * @param filterMethod
	 * @param filterDirs
	 * @return the files whose names include the string in filterMethod.
	 */
	public static File[] filterFileList(String dirPath, String filterMethod, boolean filterDirs) { //TODO: SPLIT THE "FILTERDIRS" OPTION INTO TWO METHODS FOR SAFETY
		File[] fileArray = new File(dirPath).listFiles();
		ArrayList<File> newArray = new ArrayList<File>();
		
		if (filterDirs) {
			for (File f : fileArray) {
				if (f.getName().contains(filterMethod)) newArray.add(f);
			}
		} else {
			for (int file = 0; file < fileArray.length; file++) {
				if (fileArray[file].isDirectory()) newArray.add(fileArray[file]);
				else {
					if (fileArray[file].getName().contains(filterMethod)) newArray.add(fileArray[file]);
				}
			}
		}
		
		return newArray.toArray(new File[newArray.size()]);
	}
	
	/**
	 * Filter a file array using a filter method.
	 * @param fileArray
	 * @param filterMethod
	 * @return the files whose names include the string in filterMethod.
	 */
	public static File[] filterFileList(File[] fileArray, String filterMethod) {
		ArrayList<File> newArray = new ArrayList<File>();
		
		for (File f : fileArray) {
			if (f.getName().contains(filterMethod)) newArray.add(f);
		}
		return newArray.toArray(new File[newArray.size()]);
	}
	
	/**
	 * Filters the files in the specified directory.
	 * @return the files whose names include the string in filterMethod.
	 */
	public static File[] filterDir(File dir, String filterMethod) {
		return filterFileList(dir.listFiles(), filterMethod);
	}
	
	/**
	 * Filters the files in the specified directory.
	 * @return the files whose names include the string in filterMethod.
	 */
	public static File[] filterDir(String dirPath, String filterMethod) {
		return filterFileList(new File(dirPath).listFiles(), filterMethod);
	}
	
	/**
	 * Prints a numbered file list for the user to choose, then returns the file choice.
	 */
	public static File chooseFileFromDirectory(String dirPath, Scanner scanner) {
		return chooseFileFromFileArray(dirPath, new File(dirPath).listFiles(), scanner);
	}
	
	/**
	 * Prints a numbered file list for the user to choose, then returns the file choice.
	 * This method is useful when handling filtered file lists.
	 */
	public static File chooseFileFromFileArray(String sourceDir, File[] fileList, Scanner scanner) {
		if (fileList == null) throw new NullPointerException();
		System.out.println("Diretório atual: " + sourceDir);
		int fileChoice;
		displayNumberedFileList(fileList);
		System.out.print("-> ");
		fileChoice = scanner.nextInt();
		scanner.nextLine();
		try {
			return fileList[fileChoice - 1];
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("\nOpção Inválida.\n");
			return chooseFileFromFileArray(sourceDir, fileList, scanner);
		}
	}
	
	/** Gets a file from the user input. 
	 * @throws FileNotFoundException */
	public static File getCustomFile(Scanner scan) throws FileNotFoundException {
		System.out.print("Insira o caminho completo do diretório: ");
		String dir = scan.nextLine();
		if (MauiFileUtils.exists(dir)) return new File(dir);
		else throw new FileNotFoundException(dir);
	}
	
	/**
	 * Browses a file of the specified format in the specified folder.
	 * If no format is specified, then it may also return a directory. //TODO: add "select folder" param instead of absence of format
	 * @param dir
	 * @param format the string that will filter the files in the folder.
	 * @param scan
	 * @return
	 */
	public static File browseFile(String dir, String format, Scanner scan) { 
		File[] files = filterFileList(dir, format, false);
		File file = chooseFileFromFileArray(dir, files, scan);
		if (file.isDirectory() && !format.equals("")) {
			return browseFile(file.getPath(), format, scan);
		} else {
			return file;
		}
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
	public static String[] readKeyFromFile(String filePath) throws FileNotFoundException {
		if (exists(filePath)) {
			File keys = new File(filePath);
			Scanner scanner = new Scanner(keys);
			String content = scanner.useDelimiter("\\Z").next();
			scanner.close();
			String[] topics = content.split("\n");
			for (int i = 0; i < topics.length; i++) {
				topics[i] = topics[i].replace("\r", "");
			}
			return topics;
		} else {
			throw new FileNotFoundException("O arquivo " + filePath + " não foi encontrado.");
		}
	}
	
	/**
	 * Reads keywords from files with specified format in specified directory.
	 * Assumes that every word is separated by a newline character.
	 * @return a list of keywords for every file.
	 */
	public static List<String[]> readAllKeyFromDir(String dir, String format) throws FileNotFoundException {
		if (exists(dir)) {
			File[] files = filterFileList(dir, format, true);
			List<String[]> topics = new ArrayList<>();
			
			int i;
			for (i = 0; i < files.length; i++) {
				topics.add(readKeyFromFile(files[i].getPath()));
			}
			
			return topics;
		} else {
			throw new FileNotFoundException("O Arquivo " + dir + " com o formato " + format + " não foi encontrado.");
		}
	}
	
	/**
	 * Saves a matrix as a .csv file. The filepath does not need an extension.
	 * @param matrix
	 * @param filepath
	 * @throws IOException
	 */
	public static void saveMatrixAsCSV(List<String[]> matrix, String filepath) throws IOException {
		StringTable st = new StringTable(matrix);
		st.exportAsCSV(filepath + ".csv");
	}
}
