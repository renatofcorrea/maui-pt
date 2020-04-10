package com.entopix.maui.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import com.entopix.maui.core.MauiCore;
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
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.util.Topic;
import com.entopix.maui.utils.MPTUtils;
import com.entopix.maui.utils.Matrix;
import com.entopix.maui.utils.MauiFileUtils;
import com.entopix.maui.utils.ResultMatrixes;
import com.entopix.maui.utils.UI;

import weka.core.Utils;

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
	
	private static Stemmer stemmer = new PortugueseStemmer();
	
	private static Stemmer[] stemmerList = {
			new PortugueseStemmer(),
			new LuceneRSLPStemmer(),
			new LuceneBRStemmer(),
			new LuceneSavoyStemmer(),
			new LuceneRSLPMinimalStemmer(),
			new WekaStemmerOrengo(),
			new WekaStemmerPorter(),
			new WekaStemmerSavoy(),
	};
	
	/** Path to the abstracts documents folder. */
	private static final String ABS_PATH = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\abstracts";
	
	/** Path to the full texts documents folder. */
	private static final String FTS_PATH = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts";
	
	private static String trainDirPath = null, testDirPath = null;
	
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
	public static void runWithArguments(String command, String[] args) throws Exception {
		String dataPath = MauiFileUtils.getDataPath();
		String documentsPath = dataPath + Utils.getOption('l', args);
		String modelPath = dataPath + Utils.getOption('m', args);
		String vocabPath = dataPath + Utils.getOption('v', args);
		
		String vocabFormat = MauiCore.getVocabFormat();
		String input = Utils.getOption('f', args);
		if (MPTUtils.isValid(input)) vocabFormat = input;
		
		String language = MauiCore.getLanguage();
		input = Utils.getOption('i', args);
		if (MPTUtils.isValid(input)) language = input;
		
		String encoding = MauiCore.getEncoding();
		input = Utils.getOption('e', args);
		if (MPTUtils.isValid(input)) encoding = input;
		
		boolean serialize = MauiCore.isVocabSerialize();
		input = Utils.getOption('z', args);
		if (MPTUtils.isValid(Utils.getOption('z', args))) serialize = Boolean.parseBoolean(input);
		
		int numTopicsToExtract = MauiCore.getNumTopicsToExtract();
		input = Utils.getOption('n', args);
		if (MPTUtils.isValid(input)) numTopicsToExtract = Integer.parseInt(input);
		
		Stopwords stopwords = MauiCore.getStopwords();
		input = Utils.getOption('s', args);
		if (MPTUtils.isValid(input)) {
			stopwords = (Stopwords) Class.forName(MauiCore.getStopwordsPackage() + Utils.getOption('s', args)).newInstance();
		}
		
		Stemmer stemmer = MauiCore.getStemmer();
		input = Utils.getOption('t', args);
		if (MPTUtils.isValid(input)) {
			stemmer = (Stemmer) Class.forName(MauiCore.getStemmersPackage() + Utils.getOption('t', args)).newInstance();
		}
		
		MauiCore.setStemmer(stemmer);
		MauiCore.setStopwords(stopwords);
		MauiCore.setLanguage(language);
		MauiCore.setEncoding(encoding);
		MauiCore.setVocabPath(vocabPath);
		
		if (MPTUtils.isValid(vocabPath)) {
			MauiCore.setVocabFormat(vocabFormat);
		}
		
		if (command.equals("train")) {
			MauiCore.setModelPath(modelPath);
			MauiCore.setTrainDirPath(documentsPath);
			MauiCore.buildModel();
		}
		
		if (MauiFileUtils.exists(modelPath)) {
			MauiCore.setModelPath(modelPath);
			MauiCore.setModel((ModelWrapper) MauiFileUtils.deserializeObject(modelPath));
		} else {
			throw new Exception("Model " + modelPath + " was not found");
		}
		
		if (command.equals("test")) {
			MauiCore.setTestDirPath(documentsPath);
			MauiCore.setVocabSerialize(serialize);
			MauiCore.runTopicExtractor();
			
		} else if (command.equals("run")) {
			MauiCore.setTestDocFile(new File(documentsPath));
			MauiCore.setNumTopicsToExtract(numTopicsToExtract);
			MauiCore.runMauiWrapperOnFile();
		}
	}
	
	private static void runModelBuilder() throws Exception {
		MauiCore.setTrainDirPath(trainDirPath);
		MauiCore.setModelPath(modelPath);
		MauiCore.setStemmer(stemmer);
		MauiCore.buildModel();
	}
	
	private static List<MauiTopics> runTopicExtractor() throws Exception {
		MauiCore.setModelPath(modelPath);
		MauiCore.setTestDirPath(testDirPath);
		MauiCore.setStemmer(stemmer);
		MauiCore.setModel(model);
		return MauiCore.runTopicExtractor();
	}
	
	private static List<Topic> runMauiWrapper() throws IOException, MauiFilterException {
		MauiCore.setTestDocFile(testDoc);
		MauiCore.setModelPath(modelPath);
		MauiCore.setStemmer(stemmer);
		MauiCore.setModel(model);
		return MauiCore.runMauiWrapperOnFile();
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
		trainDirPath = (modelType == ABSTRACTS ? ABS_PATH + "\\train30" : FTS_PATH + "\\train30");
		testDirPath = (modelType == ABSTRACTS ? ABS_PATH + "\\test60" : FTS_PATH + "\\test60");
		testDoc = new File(testDirPath + "\\Artigo32.txt");
	}
	
	private static void selectModel() throws InstantiationException, IllegalAccessException, ClassNotFoundException, EmptyModelsDirException {
		System.out.println("\nEscolha o modelo:");
		System.out.println("\n1 - Escolher da lista");
		System.out.println("2 - Escolher do arquivo");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			if (MauiFileUtils.isEmpty(MODELS_DIR)) {
				System.out.println("\nO diretório de modelos está vazio.");
				throw new EmptyModelsDirException();
			}
			chooseModelFromList();
		}
		else if (input.equals("2")) {
			System.out.println("\nDigite o caminho completo do modelo a ser carregado: ");
			System.out.print("Caminho: ");
			input = SCAN.nextLine();
			if (MPTUtils.isValid(input)) {
				modelPath = input;
				model = (ModelWrapper) MauiFileUtils.deserializeObject(modelPath);
				updateModelSetup();
				updatePaths();
				System.out.println("\nModelo " + modelName + " carregado.");
			}
		}
	}
	
	private static void chooseModelFromList() throws InstantiationException, IllegalAccessException, ClassNotFoundException, EmptyModelsDirException {
		if (MauiFileUtils.isEmpty(MODELS_DIR)) {
			System.out.println("\nO diretório de modelos está vazio.");
			throw new EmptyModelsDirException();
		}
		System.out.println();
		modelPath = MauiFileUtils.chooseFileFromArray(MODELS_DIR.listFiles(), SCAN).getPath();
		model = (ModelWrapper) MauiFileUtils.deserializeObject(modelPath);
		updateModelSetup();
		updatePaths();
		System.out.println("\nModelo " + modelName + " carregado.");
	}
	
	private static void trainModel() throws Exception {
		System.out.println("\nEscolha o tipo de modelo: ");
		System.out.println("1 - Resumos");
		System.out.println("2 - Textos Completos");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) modelType = ABSTRACTS;
		else if (input.equals("2")) modelType = FULLTEXTS;
		else {
			UI.showInvalidOptionMessage();
			return;
		}
		updatePaths();
		System.out.println("\nDiretório de Treinamento: " + trainDirPath);
		System.out.println("Deseja alterar o diretório de treinamento?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			System.out.println("\n1 - Escolher da lista");
			System.out.println("2 - Escolher do arquivo");
			System.out.print("Opção: ");
			input = SCAN.nextLine();
			switch (input) {
			case "1":
				System.out.println();
				String browsingDir = (modelType == ABSTRACTS ? ABS_PATH : FTS_PATH);
				File trainDir = MauiFileUtils.chooseFileFromArray(MauiFileUtils.filterFileList(browsingDir, "train", false), SCAN);
				trainDirPath = trainDir.getPath();
				break;
			case "2":
				System.out.println("\nDigite o caminho completo do diretório: ");
				System.out.print("Opção: ");
				input = SCAN.nextLine();
				if (MauiFileUtils.exists(input)) trainDirPath = input;
				else UI.showFileNotFoundMessage(input);
				break;
			default:
				UI.showInvalidOptionMessage();
				return;
			}
		}
		System.out.println("\nRadicalizador: " + stemmer.getClass().getSimpleName());
		System.out.println("Deseja alterar o radicalizador?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			System.out.println();
			for (int i = 0; i < stemmerList.length; i++) {
				System.out.println(i+1 + " - " + stemmerList[i].getClass().getSimpleName());
			}
			System.out.print("Opção: ");
			input = SCAN.nextLine();
			stemmer = stemmerList[Integer.parseInt(input) - 1];
			System.out.println("Radicalizador " + stemmer.getClass().getSimpleName() + " selecionado.");
		}
		
		modelName = MPTUtils.generateModelName(trainDirPath, stemmer);
		System.out.println("\nNome gerado: " + modelName);
		System.out.println("Deseja alterar o nome do modelo?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			System.out.print("Novo nome: ");
			input = SCAN.nextLine();
			if (!input.equals("")) {
				modelName = input;
				modelPath = MODELS_DIR + "\\" + modelName;
			}
			else {
				UI.showInvalidOptionMessage();
				return;
			}
		} else modelPath = MODELS_DIR + "\\" + modelName;
		
		runModelBuilder();
		model = MauiCore.getModel();
	}
	
	/**
	 * Runs MAUI-PT on a directory of documents and generates a sheet with test results.
	 * @param saveResults
	 * @return 
	 * @throws Exception
	 */
	private static List<MauiTopics> runOnDirectory(boolean saveResults) throws Exception {
		
		try {
			selectModel();
		} catch (EmptyModelsDirException e) {
			return null;
		}
		
		//Choose directory
		File testDir = null;
		System.out.println("\nEscolha o diretório de teste:");
		System.out.println("\n1 - Escolher da lista");
		System.out.println("2 - Escolher do arquivo");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		switch (input) {
		case "1":
			System.out.println();
			String browsingDir = (modelType == ABSTRACTS ? ABS_PATH : FTS_PATH);
			testDir = MauiFileUtils.chooseFileFromArray(MauiFileUtils.filterFileList(browsingDir, "test", false), SCAN);
			testDirPath = testDir.getPath();
			break;
		case "2":
			System.out.print("\nDigite o caminho completo do diretório: ");
			input = SCAN.nextLine();
			if (MauiFileUtils.exists(input)) {
				testDirPath = input;
			} else {
				UI.showFileNotFoundMessage(input);
				return null;
			}
			break;
		}
		
		List<MauiTopics> topics = runTopicExtractor();
		if (saveResults) {
			String resultsPath = MauiFileUtils.getDataPath() + "\\tests\\" + modelName + "_" + "test_results_" + MPTUtils.getDate();
			ResultMatrixes.buildAndSaveDetailedResults(topics, testDirPath, modelName + "_" + resultsPath);
			System.out.println("\nPlanilha " + new File(resultsPath).getName() + " gerada com sucesso.");
		}
		return topics;
	}
	
	/**
	 * Runs MAUI-PT on a .txt file.
	 * @return the topics extracted.
	 * @throws IOException
	 * @throws MauiFilterException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws EmptyModelsDirException
	 */
	private static List<Topic> runOnFile() throws IOException, MauiFilterException, InstantiationException, IllegalAccessException, ClassNotFoundException, EmptyModelsDirException {
		
		try {
			selectModel();
		} catch (EmptyModelsDirException e) {
			return null;
		}
		
		System.out.println("\nArquivo Selecionado: " + testDoc.getName());
		System.out.println("Deseja alterar o documento?");
		System.out.println("1 - Sim");
		System.out.println("[Enter] - Não");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		switch (input) {
		case "1":
			System.out.println("\n1 - Escolher da lista");
			System.out.println("2 - Escolher do arquivo");
			System.out.print("Opção: ");
			input = SCAN.nextLine();
			switch (input) {
			case "1":
				String browsingDir = (modelType == ABSTRACTS ? ABS_PATH : FTS_PATH) + "\\test60";
				File document = MauiFileUtils.chooseFileFromArray(MauiFileUtils.filterFileList(browsingDir, ".txt", false), SCAN);
				testDoc = new File(document.getPath());
				break;
			case "2":
				System.out.println("Digite o caminho completo do arquivo de texto: ");
				input = SCAN.nextLine();
				if (MauiFileUtils.exists(input)) {
					testDoc = new File(input);
				} else {
					UI.showFileNotFoundMessage(input);
					return null;
				}
				break;
			default: UI.showInvalidOptionMessage();
			}
			break;
		default: UI.showInvalidOptionMessage();
		}
		
		return runMauiWrapper();
	}

	private static void deleteModels() throws IOException {
		if (MauiFileUtils.isEmpty(MODELS_DIR)) {
			System.out.println("\nO diretório de modelos está vazio.");
			return;
		}
		System.out.println("\n1 - Excluir um modelo");
		System.out.println("2 - Excluir todos os modelos");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			System.out.println();
			File model = MauiFileUtils.chooseFileFromArray(MODELS_DIR.listFiles(), SCAN);
			String name = model.getName();
			model.delete();
			System.out.println("\nModelo " + name + " excluído.");
		} else if (input.equals("2")){
			System.out.println("\nIsto apagará todos os modelos. Deseja continuar? ");
			System.out.println("1 - Sim");
			System.out.println("2 - Não");
			System.out.print("Opção: ");
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
	
	private static void optionEvaluateIndexing() throws Exception {
		System.out.println("\nDeseja avaliar a indexação por documento ou diretório?");
		System.out.println("1 - Avaliação por documento");
		System.out.println("2 - Avaliação por diretório");
		System.out.print("Opção: ");
		input = SCAN.nextLine();
		if (input.equals("1")) {
			System.out.println("Insira o caminho do arquivo contendo as palavras chave de saída do sistema de indexação (.maui, .rake, etc.).");
			System.out.print("Caminho: ");
			input = SCAN.nextLine();
			if (!MauiFileUtils.exists(input)) {
				UI.showFileNotFoundMessage(input);
				return;
			}
			String extractedTopicsPath = input;
			
			System.out.println("Insira o caminho do arquivo contendo as palavras chave manuais do documento (.key).");
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
			
			MauiCore.evaluateTopics(filename, manualTopicsList, extractedTopicsList , extractedTopicsList.length, true);
		}
		else if (input.equals("2")) {
			System.out.println("\nInsira o caminho do diretório contendo as palavras chave de saída do sistema de indexação.");
			System.out.print("Caminho: ");
			input = SCAN.nextLine();
			if (!MauiFileUtils.exists(input)) {
				UI.showFileNotFoundMessage(input);
				return;
			}
			
			String extractedTopicsPath = input;
			
			System.out.println("Insira o formato do arquivo (sem o ponto)");
			System.out.print("Formato: ");
			input = SCAN.nextLine();
			
			String format = "." + input;
			
			System.out.println("\nInsira o caminho do diretório contendo as palavras chave manuais do documento (.key).");
			System.out.print("Caminho: ");
			input = SCAN.nextLine();
			if (!MauiFileUtils.exists(input)) {
				UI.showFileNotFoundMessage(input);
				return;
			}
			
			// builds sheets and export as .xls
			String manualTopicsPath = input;
			String filename = "indexing_evaluation_results_" + MPTUtils.getDate();
			String filepath = MauiFileUtils.getDataPath() + "\\tests\\" + filename;
			
			saveResults(manualTopicsPath, extractedTopicsPath, format, filepath);
			
			System.out.println("\nPlanilha " + filename + " gerada com sucesso.");
		}
	}
	
	private static void saveResults(String manualTopicsPath, String extractedTopicsPath, String format, String filepath) throws Exception, IOException {
		String[] docnames = MauiFileUtils.getFileNames(MauiFileUtils.filterFileList(new File(manualTopicsPath).listFiles(), ".key"), true);
		Matrix manualTopics = new Matrix(new ArrayList<Object[]>(MauiFileUtils.readKeyFromFolder(manualTopicsPath, ".key")));
		Matrix extractedTopics = new Matrix(new ArrayList<Object[]>(MauiFileUtils.readKeyFromFolder(extractedTopicsPath, format)));
		Matrix matches = new Matrix(new ArrayList<Object[]>(MauiCore.allMatches(manualTopics.getDataAsStringList(), extractedTopics.getDataAsStringList())));
		
		Matrix matrix = ResultMatrixes.buildModelEvaluationMatrix(docnames, extractedTopics.elementSizes(), manualTopics.elementSizes(), matches.elementSizes());
		ResultMatrixes.saveMatrixToFile(matrix, filepath);
	}

	/**
	 * Displays a directory of .maui files for the user to choose from, then gets the general terms on specified file. Then, executes the same
	 * process on the equivalent .key file and compares their top frequent term.
	 * @throws Exception
	 */
	private static void evaluateGeneralTermsOnFile() throws Exception {
		String dir = FTS_PATH + "\\test60";
		String mauiKeyPath = MauiFileUtils.browseFile(dir, ".maui", SCAN).getPath();
		String manualKeyPath = MPTUtils.removeFileExtension(mauiKeyPath) + ".key";
		String[] mauiTerms = MauiFileUtils.readKeyFromFile(mauiKeyPath);
		String[] manualTerms = MauiFileUtils.readKeyFromFile(manualKeyPath);
		
		System.out.println("\nObtendo termos gerais para o arquivo " + mauiKeyPath + "...");
		String topTermMaui = MauiCore.getTopFrequentTerm(mauiTerms, true);
		System.out.println("\nObtendo termos gerais para o arquivo " + manualKeyPath + "...");
		String topTermManual = MauiCore.getTopFrequentTerm(manualTerms, true);
		
		System.out.println("\nTermo mais frequente MAUI: " + topTermMaui);
		System.out.println("Termo manual mais frequente: " + topTermManual); 
		
		int res = (topTermMaui.equalsIgnoreCase(topTermManual) ? 1 : 0);
		System.out.print("\nTermos iguais?: " + res + "\n");
	}
	
	private static void evaluateGeneralTermsOnDir() throws Exception {
		File dir = MauiFileUtils.browseFile(FTS_PATH, "", SCAN);
		List<String[]> mauiKeys = MauiFileUtils.readKeyFromFolder(dir.getPath(), ".maui");
		List<String[]> manualKeys = MauiFileUtils.readKeyFromFolder(dir.getPath(), ".key");
		
		System.out.println("\nObtendo termos mais frequentes das palavras-chave do maui...");
		String[] topTermsMaui = MauiCore.getTopFrequentTermsList(mauiKeys, true);
		System.out.println("\nObtendo termos mais frequentes das palavras-chave manuais...");
		String[] topTermsManual = MauiCore.getTopFrequentTermsList(manualKeys, true);
		
		int matches = MPTUtils.matchesCount(topTermsMaui, topTermsManual);
		double percentage = (matches / topTermsManual.length) * 100;
		
		System.out.println("\nTotal de acertos: " + matches);
		System.out.println("Percentual de acertos: " + percentage + "%");
		
	}

	private static void optionShowCredits() {
		UI.displayCredits();
		System.out.println("Aperte [enter] para continuar...");
		input = SCAN.nextLine();
	}

	public static void main(String[] args) throws Exception { //TODO: change method structure to match option 7
		
		/* RUNNING WITH NO ARGUMENTS */
		
		if (args == null || args.length == 0) {
			UI.instructUser("pt");
			UI.printPTCIMessage("pt");
			
			while (true) {
				System.out.println("\n --- MAUI-PT ---");
				System.out.println("\n1 - Treinar modelo  ");
				System.out.println("2 - Excluir modelo  ");
				System.out.println("3 - Executar modelo em diretório  ");
				System.out.println("4 - Executar modelo em arquivo  ");
				System.out.println("5 - Executar teste estruturado  ");
				System.out.println("6 - Avaliar indexação  ");
				System.out.println("7 - Avaliar termos gerais ");
				System.out.println("8 - Sobre");
				System.out.println("0 - Sair  ");
				System.out.print("Opção: ");
				if (SCAN.hasNext()) {
					input = SCAN.nextLine();
				}
				switch (input) {
				case "1":
					trainModel();
					break;
				case "2":
					deleteModels();
					break;
				case "3":
					runOnDirectory(true);
					break;
				case "4":
					runOnFile();
					break;
				case "5":
					StructuredTest.runAllTests();
					break;
				case "6":
					optionEvaluateIndexing();
					break;
				case "7":
					System.out.println("\nDeseja avaliar termos gerais por documento ou diretório?");
					System.out.println("1 - Avaliar por documento");
					System.out.println("2 - Avaliar por diretório");
					System.out.print("Opção: ");
					input = SCAN.nextLine();
					switch (input) {
					case "1":
						evaluateGeneralTermsOnFile();
						break;
					case "2":
						evaluateGeneralTermsOnDir();
						break;
					default:
						
					}
					break;
				case "8":
					optionShowCredits();
					break;
				case "0":
					break;
				default:
					UI.showInvalidOptionMessage();	
				}
			}
		}
		
		/* RUNNING WITH ARGUMENTS */
		
		else {
			String command = args[0].toLowerCase(); 
			if ((!command.equals("train") && !command.equals("test") && !command.equals("run"))) {
				UI.instructUser(Utils.getOption('i', args));
				System.exit(-1);
			}
			String[] remainingArgs = new String[args.length - 1];
			System.arraycopy(args, 1, remainingArgs, 0, args.length-1);
			StandaloneMain.runWithArguments(command, remainingArgs);
		}
	}
}
