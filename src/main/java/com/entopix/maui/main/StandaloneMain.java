package com.entopix.maui.main;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.entopix.maui.core.MPTCore;
import com.entopix.maui.core.ModelWrapper;
import com.entopix.maui.exceptions.EmptyModelsDirException;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.stemmers.LuceneBRStemmer;
import com.entopix.maui.stemmers.LuceneRSLPMinimalStemmer;
import com.entopix.maui.stemmers.LuceneRSLPStemmer;
import com.entopix.maui.stemmers.LuceneSavoyStemmer;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stemmers.WekaStemmerOrengo;
import com.entopix.maui.stemmers.WekaStemmerPorter;
import com.entopix.maui.stemmers.WekaStemmerSavoy;
import com.entopix.maui.stopwords.Stopwords;
import com.entopix.maui.tests.StructuredTest;
import com.entopix.maui.tests.TestsManager;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.util.Topic;
import com.entopix.maui.utils.MPTUtils;
import com.entopix.maui.utils.Matrix;
import com.entopix.maui.utils.MauiFileUtils;
import com.entopix.maui.utils.UI;

import weka.core.Utils;

//TODO: every method in this class must be procedural, and all computation must be done outside this class
//TODO: avoid using static variables.

/**
 * StandaloneMain - allows us to run the train or test options in the single standalone,
 * as well as run Maui on a single file or a single text string.
 * 
 * Passes on all but the first option.
 * 
 * Warning: models with names that does not contain "abstracts" or "fulltexts" in its names WILL
 * break some parts of the program when using the interactive menu.
 *
 * @author Richard Vowles - https://google.com/+RichardVowles, Alyona Medelyan - medelyan@gmail.com
 * @author Renato Correa
 * @author Rahmon Jorge
 */
public class StandaloneMain {
	
	public static final Scanner SCAN = new Scanner(System.in);
	
	private static String input;
	
	/** The models directory. Can be set using MauiFileUtils.setModelsDirPath */
	private static final File MODELS_DIR = new File(MauiFileUtils.getModelsDirPath());
	
	/** Indicates that the current model was trained under abstracts or fulltexts documents. */
	private static final int ABSTRACTS = 0, FULLTEXTS = 1;
	
	/** Variable that holds the enums above.*/
	private static int modelType = -1;
	
	private static ModelWrapper model = null;
	
	private static File testDoc = null;
	
	private static Stemmer stemmer = new LuceneRSLPStemmer();//PortugueseStemmer();
	
	private static Stemmer[] stemmerList = {//ranking test fulltext 60,30 (abstract 60,30)
			new LuceneRSLPStemmer(),//#1 (#2,#6) //RSLP == Orengo Stemmer ***
			new LuceneBRStemmer(),//#3 //BrazilianStemmer ***
			new LuceneRSLPMinimalStemmer(),//#5,#7 (#6,#5) //RSLP-S ***
			new LuceneSavoyStemmer(),//#7,#5 //UniNE - Light Stemmer for Portuguese 
			new WekaStemmerSavoy(),//#2 (#1,#1) *****
			new WekaStemmerOrengo(),//#4,#6
			new WekaStemmerPorter(),//#6,#4
			new PortugueseStemmer()//#8 (#3,#2) #Added to KEA *
	};
	
	/** Path to the abstracts documents folder. */
	private static final String ABS_PATH = MauiFileUtils.getDataPath() + "/docs/corpusci/abstracts";
	
	/** Path to the full texts documents folder. */
	private static final String FTS_PATH = MauiFileUtils.getDataPath() + "/docs/corpusci/fulltexts";
	
	private static final String TESTS_PATH = MauiFileUtils.getDataPath() + "/tests";
	
	private static String trainDirPath = null, runDirPath = null;
	
	private static String modelPath, modelName;
	
	/**
	 * @param command "train", "test" or "run" <br>
	 * @param args valid arguments: <br>
	 * -l documents directory <br>
	 * -m model path <br>
	 * -v vocabulary path <br>
	 * -f vocabulary format <br>
	 * -i language <br>
	 * -e encoding <br>
	 * -z serialize vocabulary? <br>
	 * -n number of topics to extract <br>
	 * -s stopwords class <br>
	 * -t stemmer class <br>
	 * @throws Exception
	 */
	public static void runWithArguments(String[] mainArgs) throws Exception {
		
		String command = mainArgs[0].toLowerCase(); 
		if ((!command.equals("train") && !command.equals("test") && !command.equals("run"))) {
			UI.instructUser(Utils.getOption('i', mainArgs));
			System.exit(-1);
		}
		String[] args = new String[mainArgs.length - 1]; // remaining args
		System.arraycopy(mainArgs, 1, args, 0, mainArgs.length-1);
		
		String dataPath = MauiFileUtils.getDataPath();
		String documentsPath = dataPath + Utils.getOption('l', args);
		String modelPath = dataPath + Utils.getOption('m', args);
		String vocabPath = dataPath + Utils.getOption('v', args);
		
		String vocabFormat = MPTCore.getVocabFormat();
		String input = Utils.getOption('f', args);
		if (MPTUtils.isValid(input)) vocabFormat = input;
		
		String language = MPTCore.getLanguage();
		input = Utils.getOption('i', args);
		if (MPTUtils.isValid(input)) language = input;
		
		String encoding = MPTCore.getEncoding();
		input = Utils.getOption('e', args);
		if (MPTUtils.isValid(input)) encoding = input;
		
		boolean serialize = MPTCore.isVocabSerialize();
		input = Utils.getOption('z', args);
		if (MPTUtils.isValid(Utils.getOption('z', args))) serialize = Boolean.parseBoolean(input);
		
		int numTopicsToExtract = MPTCore.getNumTopicsToExtract();
		input = Utils.getOption('n', args);
		if (MPTUtils.isValid(input)) numTopicsToExtract = Integer.parseInt(input);
		
		Stopwords stopwords = MPTCore.getStopwords();
		input = Utils.getOption('s', args);
		if (MPTUtils.isValid(input)) {
			stopwords = (Stopwords) Class.forName(MPTCore.getStopwordsPackage() + Utils.getOption('s', args)).getDeclaredConstructor().newInstance();
		}
		
		Stemmer stemmer = MPTCore.getStemmer();
		input = Utils.getOption('t', args);
		if (MPTUtils.isValid(input)) {
			stemmer = (Stemmer) Class.forName(MPTCore.getStemmersPackage() + Utils.getOption('t', args)).getDeclaredConstructor().newInstance();
		}
		
		MPTCore.setStemmer(stemmer);
		MPTCore.setStopwords(stopwords);
		MPTCore.setLanguage(language);
		MPTCore.setEncoding(encoding);
		MPTCore.setVocabPath(vocabPath);
		
		if (MPTUtils.isValid(vocabPath)) {
			MPTCore.setVocabFormat(vocabFormat);
		}
		
		if (command.equals("train")) {
			MPTCore.setModelPath(modelPath);
			MPTCore.setTrainDirPath(documentsPath);
			MPTCore.setupAndBuildModel();
		}
		
		if (MauiFileUtils.exists(modelPath)) {
			MPTCore.setModelPath(modelPath);
			MPTCore.setModel((ModelWrapper) MauiFileUtils.deserializeObject(modelPath));
		} else {
			throw new Exception("Model " + modelPath + " was not found");
		}
		
		if (command.equals("test")) {
			MPTCore.setTestDirPath(documentsPath);
			MPTCore.setVocabSerialize(serialize);
			MPTCore.runTopicExtractor();
			
		} else if (command.equals("run")) {
			MPTCore.setTestDocFile(new File(documentsPath));
			MPTCore.setNumTopicsToExtract(numTopicsToExtract);
			MPTCore.runMauiWrapperOnFile();
		}
	}
	
	public static void main(String[] mainArgs) throws Exception {
		
		if (mainArgs != null && mainArgs.length > 0) {
			StandaloneMain.runWithArguments(mainArgs);
			System.exit(0);
		}
		
		UI.instructUser("pt");
		UI.printPTCIMessage("pt");
		
		boolean exit = false;
		while (!exit) {
			System.out.println("\n --- MAUI-PT --- \n");
			System.out.println("1 - Treinar modelo  ");
			System.out.println("2 - Excluir modelo  ");
			System.out.println("3 - Executar modelo");
			System.out.println("4 - Avaliar radicalizadores via teste estruturado");
			System.out.println("5 - Avaliar indexação  ");
			System.out.println("6 - Avaliar termos gerais ");
			System.out.println("7 - Sobre");
			System.out.println("0 - Sair  ");
			System.out.print("-> ");
			if (SCAN.hasNext()) input = SCAN.nextLine();
			switch (input) {
			case "1": trainModel(); break;
			case "2": deleteModels(); break;
			case "3": runModel(); break;
			case "4": StructuredTest.runAllTests(); break;
			case "5": evaluateIndexing(); break;
			case "6": evaluateGeneralTerms(); break;
			case "7": optionShowCredits(); break;
			case "0": exit = true; break;
			default: UI.showInvalidOptionMessage();	
			}
		}
	}
	
	/** Option 1 at the main menu. */
	private static void trainModel() throws Exception { //TODO: factor this
		System.out.println("\nEscolha o tipo de modelo: ");
		System.out.println("1 - Resumos");
		System.out.println("2 - Textos Completos");
		System.out.print("-> ");
		input = SCAN.nextLine();
		switch (input) {
			case "1": modelType = ABSTRACTS; break;
			case "2": modelType = FULLTEXTS; break;
			default: UI.showInvalidOptionMessage(); return;
		}
		updatePaths();
		System.out.println("\nDiretório Atual: " + trainDirPath);
		System.out.println("Deseja alterar o diretório de treinamento?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("-> ");
		input = SCAN.nextLine();
		switch (input) {
		case "1":
			System.out.println("\n1 - Escolher da lista");
			System.out.println("2 - Escolher do arquivo");
			System.out.print("-> ");
			input = SCAN.nextLine();
			switch (input) {
			case "1": // sets training directory from list
				trainDirPath = browseTrainDirFromList().getPath();
				break;
			case "2":
				System.out.println("\nDigite o caminho completo do diretório: ");
				System.out.print("-> ");
				input = SCAN.nextLine();
				if (MauiFileUtils.exists(input)) trainDirPath = input;
				else UI.showFileNotFoundMessage(input);
				break;
			default:
				UI.showInvalidOptionMessage();
				return;
			}
			break;
		default:
			//intentionally empty
		}
		System.out.println("\nRadicalizador: " + stemmer.getClass().getSimpleName());
		System.out.println("Deseja alterar o radicalizador?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("-> ");
		input = SCAN.nextLine();
		switch (input) {
		case "1": //loads stemmer list
			stemmer = browseStemmerFromList();
			System.out.println("Radicalizador " + stemmer.getClass().getSimpleName() + " selecionado.");
			break;
		default:
			//intentionally empty
		}
		modelName = MPTUtils.generateModelName(trainDirPath, stemmer);
		System.out.println("\nNome gerado: " + modelName);
		System.out.println("Deseja alterar o nome do modelo?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("-> ");
		input = SCAN.nextLine();
		switch (input) {
		case "1":
			System.out.print("Novo nome: ");
			input = SCAN.nextLine();
			if (!input.equals("")) {
				modelName = input;
				modelPath = MODELS_DIR + "/" + modelName;
			}
			else {
				UI.showInvalidOptionMessage();
				return;
			}
			break;
		default:
			modelPath = MODELS_DIR + "/" + modelName;
		}
		setupAndRunModelBuilder();
		model = MPTCore.getModel();
	}
	
	/** Option 2 at the main menu. */
	private static void deleteModels() throws IOException {
		if (MauiFileUtils.isEmpty(MODELS_DIR)) {
			System.out.println("\nO diretório de modelos está vazio.");
			return;
		}
		System.out.println("1 - Excluir um modelo");
		System.out.println("2 - Excluir todos os modelos");
		System.out.print("-> ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			System.out.println();
			File model = MauiFileUtils.chooseFileFromFileArray(MODELS_DIR.getPath(), MODELS_DIR.listFiles(), SCAN);
			String name = model.getName();
			model.delete();
			System.out.println("\nModelo " + name + " excluído.");
		} else if (input.equals("2")){
			System.out.println("\nIsto apagará todos os modelos. Deseja continuar? ");
			System.out.println("1 - Sim");
			System.out.println("2 - Não");
			System.out.print("-> ");
			input = SCAN.nextLine();
			if (input.equals("1"))  {
				FileUtils.cleanDirectory(MODELS_DIR);
				System.out.println("Todos os modelos excluídos com sucesso.");
			}
			model = null;
			modelPath = null;
			modelName = null;
		} else UI.showInvalidOptionMessage();
	}
	
	/** Option 3 at the main menu. */
	private static void runModel() throws Exception {
		System.out.println("1 - Executar modelo em diretório");
		System.out.println("2 - Executar modelo em arquivo");
		System.out.println("0 - Voltar");
		System.out.print("-> ");
		input = SCAN.nextLine();
		switch (input) {
		case "1": runOnDirectory(); break;
		case "2": runOnFile(); break;
		case "0": return;
		default: UI.showInvalidOptionMessage();
		}
	}
	
	/** Option 3.1 at the run model menu. 
	 * Sets the run directory path and runs the topic extractor.*/
	private static void runOnDirectory() throws Exception {
		try {
			selectModel();
		} catch (EmptyModelsDirException e) {
			return;
		}
		runDirPath = chooseRunDirectory().getPath();
		UI.displayTopics(setupAndRunTopicExtractor());
		System.out.println("Tópicos salvos em " + runDirPath + ".");
	}
	
	/** Option 3.2 at the run model menu. */
	private static List<Topic> runOnFile() throws IOException, MauiFilterException, InstantiationException, IllegalAccessException, ClassNotFoundException, EmptyModelsDirException {
		try {
			selectModel();
		} catch (EmptyModelsDirException e) {
			return null;
		}
		System.out.println("1 - Escolher arquivo da lista");
		System.out.println("2 - Escolher arquivo personalizado");
		System.out.print("-> ");
		input = SCAN.nextLine();
		switch (input) {
		case "1":
			String browsingDir = (modelType == ABSTRACTS ? ABS_PATH : FTS_PATH) + "/test60";
			File document = MauiFileUtils.chooseFileFromFileArray(browsingDir, MauiFileUtils.filterFileList(browsingDir, ".txt", true), SCAN);
			testDoc = new File(document.getPath());
			break;
		case "2":
			System.out.println("Digite o caminho completo do arquivo de texto: ");
			System.out.print("-> ");
			input = SCAN.nextLine();
			if (MauiFileUtils.exists(input)) {
				testDoc = new File(input);
			} else {
				UI.showFileNotFoundMessage(input);
				return null;
			}
			break;
		default:
			UI.showInvalidOptionMessage();
		}
		return setupAndRunMauiWrapper();
	}
	
	/** Option 5 at the main menu. */
	private static void evaluateIndexing() throws Exception {
		System.out.println("1 - Avaliar indexação do maui");
		System.out.println("2 - Avaliar indexação via relatórios de termos");
		System.out.println("0 - Voltar");
		System.out.print("-> ");
		input = SCAN.nextLine();
		switch (input) {
		case "1": evaluateMauiIndexing(); break;
		case "2": evaluateCustomIndexing(); break;
		case "0": return;
		}
	}
	
	/** Option 5.1 at the evaluate indexing menu. */
	private static void evaluateMauiIndexing() throws Exception {
		try {
			selectModel();
		} catch (EmptyModelsDirException e) {
			return;
		}
		String dir = FTS_PATH + "/test30";
		System.out.println("Diretório padrão: " + dir);
		System.out.println("1 - Usar diretório padrão");
		System.out.println("2 - Alterar diretório");
		System.out.print("-> ");
		input = SCAN.nextLine();
		switch (input) {
		case "1": runDirPath = dir; break;
		case "2": runDirPath = MauiFileUtils.chooseFileFromDirectory(FTS_PATH, SCAN).getPath(); break;
		default: UI.showInvalidOptionMessage();
		}
		List<MauiTopics> topics = setupAndRunTopicExtractor();
		runMauiIndexingEvaluation(runDirPath, topics);
	}
	
	/** Option 5.2 at the evaluate indexing menu. */
	private static void evaluateCustomIndexing() throws Exception {
		System.out.println("1 - Avaliar por arquivo");
		System.out.println("2 - Avaliar por diretório");
		System.out.print("-> ");
		input = SCAN.nextLine();
		switch (input) {
		case "1": customEvaluationOnFile(); break;
		case "2": customEvaluationOnDir(); break;
		default: UI.showInvalidOptionMessage();
		}
	}
	
	/** Option 5.2.1 at the custom evaluation indexing menu. */
	private static void customEvaluationOnFile() throws Exception {
		System.out.println("Insira o caminho do arquivo contendo as palavras chave de saída do sistema de indexação (.maui, .rake, etc.).");
		System.out.print("Caminho: ");
		input = SCAN.nextLine();
		if (!MauiFileUtils.exists(input)) {
			UI.showFileNotFoundMessage(input);
			return;
		}
		String extractedTopicsPath = input;
		
		System.out.println("Insira o caminho do arquivo contendo as palavras chave manuais correspondentes ao documento (.key).");
		System.out.print("Caminho: ");
		input = SCAN.nextLine();
		if (!MauiFileUtils.exists(input)) {
			UI.showFileNotFoundMessage(input);
			return;
		}
		String originalTopicsPath = input;
		String filename = MPTUtils.removeFileExtension(new File(input).getName());
		
		String[] extractedTopicsList = MauiFileUtils.readKeyFromFile(extractedTopicsPath);
		String[] manualTopicsList = MauiFileUtils.readKeyFromFile(originalTopicsPath);
		
		MPTCore.evaluateTopics(filename, manualTopicsList, extractedTopicsList , extractedTopicsList.length, true);
	}
	
	/** Option 5.2.2 at the custom evaluation indexing menu. */
	private static void customEvaluationOnDir() throws IOException, Exception {
		System.out.println("Insira o formato do arquivo contendo os tópicos extraídos (.maui, .rake, etc)");
		System.out.print("Formato: ");
		input = SCAN.nextLine();
		String format = input;
		System.out.println("Insira o caminho do diretório contendo os arquivos de tópicos extraídos e os tópicos originais (.key)");
		System.out.print("Caminho: ");
		input = SCAN.nextLine();
		if (!validPath(input)) return;
		String allTopicsPath = input;
		
		List<MauiTopics> topics = MPTUtils.stringMatrixToMauiTopics(MauiFileUtils.readAllKeyFromDir(allTopicsPath, format), allTopicsPath,format);
		runMauiIndexingEvaluation(allTopicsPath, topics); //NOTE: Assumes that the .key files and .format files are in the same folder with corresponding names
	}
	
	/** Option 6 at the main menu. */
	private static void evaluateGeneralTerms() throws Exception {
		System.out.println("1 - Avaliar diretório");
		System.out.println("2 - Avaliar arquivo");
		System.out.print("-> ");
		input = SCAN.nextLine();
		switch (input) {
		case "1": evaluateGeneralTermsOnDir(); break;
		case "2": evaluateGeneralTermsOnFile(); break;
		default: UI.showInvalidOptionMessage();
		}
	}
	
	/** Option 6.1 at the general terms evaluation menu. */
	private static void evaluateGeneralTermsOnDir() throws Exception {
		File dir = MauiFileUtils.browseFile(FTS_PATH, "", SCAN);
		File[] allFiles = ArrayUtils.addAll(MauiFileUtils.filterDir(dir, ".maui"), MauiFileUtils.filterDir(dir, ".key"));
		String[] allFilesPaths = MauiFileUtils.getFileListPaths(allFiles);
		int count = chooseTermsCount(allFilesPaths);
		runGeneralTermsEvaluation(dir.getPath(), count);
	}
	
	/**
	 * Option 6.2 at the general terms evaluation menu.
	 * Displays a directory of .maui files to choose from, then gets the general terms on specified file.
	 * Next, executes the same process on the equivalent .key file and compares their top frequent term.
	 */
	private static void evaluateGeneralTermsOnFile() throws Exception {
		String dir = FTS_PATH + "/test60";
		String mauiKeyPath = MauiFileUtils.browseFile(dir, ".maui", SCAN).getPath();
		String manualKeyPath = MPTUtils.removeFileExtension(mauiKeyPath) + ".key";
		String[] mauiTerms, manualTerms = null;
		try {
			mauiTerms = MauiFileUtils.readKeyFromFile(mauiKeyPath);
			manualTerms = MauiFileUtils.readKeyFromFile(manualKeyPath);
		} catch (FileNotFoundException e) {
			System.out.println(e.toString());
			return;
		}
		int count = chooseTermsCount(new String[] {mauiKeyPath, manualKeyPath});
		System.out.println("\nObtendo termos gerais para o arquivo " + mauiKeyPath + "...");
		String topTermMaui = MPTCore.getTopFrequentTerm(mauiTerms, count, true);
		System.out.println("\nObtendo termos gerais para o arquivo " + manualKeyPath + "...");
		String topTermManual = MPTCore.getTopFrequentTerm(manualTerms, count, true);
		
		System.out.println("\nTermo mais frequente MAUI: " + topTermMaui);
		System.out.println("Termo manual mais frequente: " + topTermManual); 
		
		int res = (topTermMaui.equalsIgnoreCase(topTermManual) ? 1 : 0);
		System.out.print("\nTermos iguais?: " + res + "\n");
	}
	
	private static int chooseTermsCount(String[] files) throws FileNotFoundException {
		System.out.print("Quantidade de termos a serem avaliados: ");
		input = SCAN.nextLine();
		int count = Integer.parseInt(input);
		
		for (int i = 0; i < files.length; i++) {
			int maxTermCount = MauiFileUtils.readKeyFromFile(files[i]).length; 
			if (count < 0) count = 1;
			if (count > maxTermCount) {
				count = maxTermCount;
			}
		}
		
		
		System.out.println("Quantidade selecionada: " + count);
		return count;
	}
	
	/** Returns a dir choice by the user.
	 * By standard, shows only directories that contain "test" on its names. */
	private static File chooseRunDirectory() throws FileNotFoundException {
		System.out.println("1 - Lista de diretórios");
		System.out.println("2 - Diretório personalizado");
		System.out.print("-> ");
		input = SCAN.nextLine();
		switch (input) {
		case "1": return browseFileOnDirList("test");
		case "2": return MauiFileUtils.getCustomFile(SCAN);
		default: UI.showInvalidOptionMessage();
		}
		return null;
	}
	
	/** Browse a file on a directory list according to the current model type. 
	 * @param filter the directory list will only have dirs that contain the filter string on its names. */
 	private static File browseFileOnDirList(String filter) {
		String browsingDir = (modelType == ABSTRACTS ? ABS_PATH : FTS_PATH);
		try {
			return MauiFileUtils.chooseFileFromFileArray(browsingDir, MauiFileUtils.filterDir(browsingDir, filter), SCAN);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
 	/** Generates and saves the model evaluation sheet and the keywords comparison sheet. */
 	private static void runMauiIndexingEvaluation(String runDirPath, List<MauiTopics> topics) throws Exception {
 		Matrix m1 = TestsManager.runModelEvaluation(runDirPath, topics, TESTS_PATH);
		Matrix m2 = TestsManager.runKeywordsComparison(runDirPath, topics, TESTS_PATH);
		String outpath = TESTS_PATH + "/" + "maui_indexing_evaluation" + " " + MPTUtils.getTimeAndDate() + ".xls";
		TestsManager.saveMatrixesAsSheets(new Matrix[] {m1,m2}, new String[] {"Avaliação do Modelo", "Comparação de Frases-Chave"}, outpath);
		showTestResults(outpath);
 	}
 	
 	/** Generates and saves the general terms evaluation sheet. */
 	private static void runGeneralTermsEvaluation(String dirpath, int termsToEvaluate) throws Exception {
 		Matrix m = TestsManager.buildGeneralTermsComparisonMatrix2(dirpath, termsToEvaluate);
		String outpath = TESTS_PATH + "/" + "general_terms_evaluation" + " " + MPTUtils.getTimeAndDate() + ".xls";
		TestsManager.saveMatrixAsSheet(m, outpath);
		showTestResults(outpath);
 	}
 	
 	/** Checks if provided path is valid, and shows error message if it's not.
 	 * @throws FileNotFoundException */
 	private static boolean validPath(String path) throws FileNotFoundException {
		if (!MauiFileUtils.exists(path)) {
			UI.showFileNotFoundMessage(path);
			return false;
		}
 		return true;
 	}
 	
 	/** Shows a file out path and display options to open it or its directory. */
  	private static void showTestResults(String outpath) throws IOException {
		String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Executa o código apenas se for Windows
            //System.out.println("Executando código na plataforma compatível...");
            // Coloque aqui o trecho de código que deseja executar
			System.out.println("Resultados dos testes salvos em " + TESTS_PATH);
			System.out.println("1 - Abrir diretório de testes");
			System.out.println("2 - Abrir arquivo");
			System.out.println("3 - Voltar ao menu principal");
			System.out.print("-> ");
			input = SCAN.nextLine();
			switch (input) {
				case "1": Runtime.getRuntime().exec("explorer.exe " + TESTS_PATH); break;
				case "2": Desktop.getDesktop().open(new File(outpath));
				case "3": break;
				default: UI.showInvalidOptionMessage();
			}
        } else {
            System.out.println("Plataforma incompatível para abertura de arquivos. Verifique os arquivos em:"+ TESTS_PATH);
        }
 		
 	}
 	
	private static void setupAndRunModelBuilder() throws Exception {
		MPTCore.setTrainDirPath(trainDirPath);
		MPTCore.setModelPath(modelPath);
		MPTCore.setStemmer(stemmer);
		MPTCore.setupAndBuildModel();
	}
	
	private static List<MauiTopics> setupAndRunTopicExtractor() throws Exception {
		MPTCore.setModelPath(modelPath);
		MPTCore.setTestDirPath(runDirPath);
		MPTCore.setStemmer(stemmer);
		MPTCore.setModel(model);
		return MPTCore.runTopicExtractor();
	}
	
	private static List<Topic> setupAndRunMauiWrapper() throws IOException, MauiFilterException {
		MPTCore.setTestDocFile(testDoc);
		MPTCore.setModelPath(modelPath);
		MPTCore.setStemmer(stemmer);
		MPTCore.setModel(model);
		return MPTCore.runMauiWrapperOnFile();
	}
	
	/**
	 * Updates the model name, type and stemmer.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private static void updateModelSetup() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		modelName = new File(modelPath).getName();
		modelType = (model.getTrainingPath().contains("abstracts") ? ABSTRACTS : FULLTEXTS); //Updates model type
		stemmer = model.getStemmerUsed(); //Updates model stemmer
	}
	
	/** 
	 * Updates standard paths based on the document type of the model.
	 */
	private static void updatePaths() {
		trainDirPath = (modelType == ABSTRACTS ? ABS_PATH + "/train30" : FTS_PATH + "/train30");
		runDirPath = (modelType == ABSTRACTS ? ABS_PATH + "/test60" : FTS_PATH + "/test60");
		testDoc = new File(runDirPath + "/Artigo32.txt");
	}
	
	private static void selectModel() throws InstantiationException, IllegalAccessException, ClassNotFoundException, EmptyModelsDirException {
		System.out.println("1 - Escolher modelo da lista");
		System.out.println("2 - Escolher modelo personalizado");
		System.out.print("-> ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			if (MauiFileUtils.isEmpty(MODELS_DIR)) throw new EmptyModelsDirException("O diretório de modelos está vazio.");
			chooseModelFromList();
		}
		else if (input.equals("2")) {
			System.out.println("Digite o caminho completo do modelo a ser carregado: ");
			System.out.print("Caminho: ");
			input = SCAN.nextLine();
			if (MPTUtils.isValid(input)) {
				modelPath = input;
				model = (ModelWrapper) MauiFileUtils.deserializeObject(modelPath);
				updateModelSetup();
				updatePaths();
				System.out.println("Modelo " + modelName + " carregado.");
			}
		}
	}
	
	/**
	 * Shows a list of available models for the user to choose from, then sets the model to be used on the rest of the program.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws EmptyModelsDirException
	 */
	private static void chooseModelFromList() throws InstantiationException, IllegalAccessException, ClassNotFoundException, EmptyModelsDirException {
		if (MauiFileUtils.isEmpty(MODELS_DIR)) {
			throw new EmptyModelsDirException("O diretório de modelos está vazio.");
		}
		modelPath = MauiFileUtils.chooseFileFromFileArray(MODELS_DIR.getPath(), MODELS_DIR.listFiles(), SCAN).getPath();
		model = (ModelWrapper) MauiFileUtils.deserializeObject(modelPath);
		updateModelSetup();
		updatePaths();
		System.out.println("\nModelo " + modelName + " carregado.\n");
	}
	
	/**
	 * Shows the training directory matching the type of model for the user to browse a training directory.
	 * @return the chosen directory.
	 */
	private static File browseTrainDirFromList() {
		System.out.println();
		String browsingDir = (modelType == ABSTRACTS ? ABS_PATH : FTS_PATH);
		try {
			return MauiFileUtils.chooseFileFromFileArray(browsingDir, MauiFileUtils.filterFileList(browsingDir, "train", true), SCAN);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Shows a list of available stemmers for the user to choose from.
	 * @return the chosen stemmer.
	 */
	private static Stemmer browseStemmerFromList() {
		System.out.println();
		for (int i = 0; i < stemmerList.length; i++) {
			System.out.println(i+1 + " - " + stemmerList[i].getClass().getSimpleName());
		}
		System.out.print("-> ");
		input = SCAN.nextLine();
		return stemmerList[Integer.parseInt(input) - 1];
	}

	private static void optionShowCredits() {
		UI.displayCredits();
		System.out.println("[Enter] - Voltar ao menu principal");
		SCAN.nextLine();
	}
}
