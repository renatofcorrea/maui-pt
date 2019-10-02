package com.entopix.maui.tests;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.entopix.maui.beans.MauiModel;
import com.entopix.maui.beans.ModelDocType;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.main.MauiTopicExtractor;
import com.entopix.maui.stemmers.LuceneBRStemmer;
import com.entopix.maui.stemmers.LuceneLPTStemmer;
import com.entopix.maui.stemmers.LuceneMPTStemmer;
import com.entopix.maui.stemmers.LucenePTStemmer;
import com.entopix.maui.stemmers.NewPortugueseStemmer;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stopwords.StopwordsPortuguese;
import com.entopix.maui.util.DataLoader;
import com.entopix.maui.util.MauiDocument;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.utils.MauiFileUtils;
/**
 * Performs a structured test comparing models made with abstracts and full texts, made with 10 to 30 documents.
 * @author Rahmon Jorge
 */
public class StructuredTest {
	
	static String dataPath = MauiFileUtils.getDataPath();
	static String vocabPath = MauiFileUtils.getVocabPath();
	static String abstractsDocsPath = dataPath + "\\docs\\corpusci\\abstracts";
	static String fullTextsDocsPath = dataPath + "\\docs\\corpusci\\full_texts";
	static String modelsPath = dataPath + "\\models";
	static String stemmerName;
	
	static boolean deleteModelsAfterTest = false;
	
	static void buildModel(Stemmer stemmer, String trainDirPath) throws Exception {
		ModelDocType modelType = ((trainDirPath.contains("abstracts") ? ModelDocType.ABSTRACTS : ModelDocType.FULLTEXTS));
		
		String modelName = null;
		
		if(stemmer instanceof NewPortugueseStemmer) {
			NewPortugueseStemmer newptstemmer = (NewPortugueseStemmer) stemmer;
			modelName = "model_NewPortugueseStemmer_" + newptstemmer.type + "_" + modelType.getName() + "_" + new File(trainDirPath).getName();
		} else {
			modelName = "model_" + stemmer.getClass().getSimpleName() + "_" + modelType.getName() + "_" + new File(trainDirPath).getName();
		}
		
		String modelPath = modelsPath + "\\" + modelName;
		MauiModel model = new MauiModel(trainDirPath, modelPath, stemmer, vocabPath, modelType);
		model.saveModel();
	}
	
	public static void buildAllModels() throws Exception {
		
		String serialVocabPath = "C:\\Users\\PC1\\git\\maui-pt\\data\\vocabulary\\TBCI-SKOS_pt.rdf_com.entopix.maui.vocab.VocabularyStore_Original_PortugueseStemmer.serialized";
		File vocab = null;
		
		Stemmer[] stemmerList = {
				new PortugueseStemmer(),
				new NewPortugueseStemmer(new String[] {"-S","orengo"}),
				new NewPortugueseStemmer(new String[] {"-S","savoy"}),
				new NewPortugueseStemmer(new String[] {"-S","porter"}),
				new LucenePTStemmer(),
				new LuceneBRStemmer(),
				new LuceneLPTStemmer(),
				new LuceneMPTStemmer(),
		};
		
		File[] dirList = new File(abstractsDocsPath).listFiles();
		dirList = MauiFileUtils.filterFileList(dirList, "train");
		
		for(Stemmer s : stemmerList) {
			for(File f : dirList) {
				buildModel(s, f.getAbsolutePath());
			}
			vocab = new File(serialVocabPath);
			vocab.delete();
		}
		
		dirList = new File(fullTextsDocsPath).listFiles();
		dirList = MauiFileUtils.filterFileList(dirList, "train");
		
		for(Stemmer s : stemmerList) {
			for(File f : dirList) {
				buildModel(s, f.getAbsolutePath());
			}
			vocab = new File(serialVocabPath);
			vocab.delete();
		}
	}
	
	/**
	 * Tests a model precision, recall and f-measure, then return the results in a array of doubles.
	 * @param docType abstracts or full_texts
	 * @param modelPath
	 * @throws MauiFilterException
	 */
	static double[] testModel(String modelPath, String testDirPath) throws MauiFilterException {
		//Test model
		MauiTopicExtractor topicExtractor = new MauiTopicExtractor();
		topicExtractor.inputDirectoryName = testDirPath;
		topicExtractor.modelName = modelPath;
		topicExtractor.vocabularyName = vocabPath;
		topicExtractor.vocabularyFormat = "skos";
		topicExtractor.documentLanguage = "pt";
		topicExtractor.cutOffTopicProbability = 0.12;
		topicExtractor.serialize = true;
		topicExtractor.stopwords = new StopwordsPortuguese();
		topicExtractor.stemmer = new PortugueseStemmer();
		topicExtractor.loadModel();
		List<MauiDocument> documents = DataLoader.loadTestDocuments(topicExtractor.inputDirectoryName);
		List<MauiTopics> topics = topicExtractor.extractTopics(documents);
		topicExtractor.printTopics(topics);
		
		return TestUtils.evaluateTopics(topics);
	}
	
	/**
	 * Tests a list of models, then return a matrix of objects, where each line contains the model name and its results.
	 * @return 
	 * @throws MauiFilterException 
	 */
	static List<Object[]> testModelList(File[] modelsList, String testDirPath) throws MauiFilterException {
		
		List<Object[]> matrix = new ArrayList<Object[]>();
		
		double[] modelResDouble;
		Object[] modelRes = new Object[8];
		int i;
		for(File model : modelsList) {
			modelResDouble = testModel(model.getAbsolutePath(), testDirPath);
			modelRes[0] = model.getName();
			for(i = 1; i < 8; i++) {
				modelRes[i] = modelResDouble[i-1];
			}
			matrix.add(modelRes);
		}
		return matrix;
	}
	
	public static void testAllModels() throws MauiFilterException {
		File modelsDir = new File(modelsPath);
		File[] abstractsModelsList = MauiFileUtils.filterFileList(modelsDir.listFiles(), "abstracts");
		File[] fulltextsModelsList = MauiFileUtils.filterFileList(modelsDir.listFiles(), "abstracts");
		String testDir = null;

		//Tests all models
		testDir = abstractsDocsPath + "\\test30";
		ArrayList<Object[]> abstractsResults30 = (ArrayList<Object[]>) testModelList(abstractsModelsList, testDir);
		
		testDir = abstractsDocsPath + "\\test60";
		ArrayList<Object[]> abstractsResults60 = (ArrayList<Object[]>) testModelList(abstractsModelsList, testDir);

		testDir = fullTextsDocsPath + "\\test30";
		ArrayList<Object[]> fulltextsResults30 = (ArrayList<Object[]>) testModelList(fulltextsModelsList, testDir);
		
		testDir = fullTextsDocsPath + "\\test60";
		ArrayList<Object[]> fulltextsResults60 = (ArrayList<Object[]>) testModelList(fulltextsModelsList, testDir);
		
		
		System.out.println("ABSTRACTS");
		System.out.println("---> Test results based on 30 documents:");
		printMatrix(formatMatrix(abstractsResults30));
		System.out.println("---> Test results based on 60 documents:");
		printMatrix(formatMatrix(abstractsResults60));
		
		System.out.println("FULLTEXTS");
		System.out.println("---> Test results based on 30 documents:");
		printMatrix(formatMatrix(fulltextsResults30));
		System.out.println("---> Test results based on 60 documents:");
		printMatrix(formatMatrix(fulltextsResults60));
	}
	
	public static void printMatrix(List<String[]> matrix) {
		for(String[] model : matrix) {
			for(String value : model) {
				System.out.println(value);
			}
			System.out.println();
		}
	}
	
	public static List<String[]> formatMatrix(ArrayList<Object[]> matrix) {
		
		List<String[]> formattedMatrix = new ArrayList<String[]>();
		
		//Builds header
		String[] header = {"MODEL NAME","AVG KEY","STDEV KEY","AVG PRECISION","STDEV PRECISION","AVG RECALL","STDEV RECALL","F-MEASURE",""};
		for(String word : header) {
			if(word.equals(header[0])) {
				word = String.format("%-60s",word);
			} else {
				word = String.format("%-20s", word);
			}
		}
		header[8] = "\n";
		formattedMatrix.add(header);
		
		//Formats results columns
		String[] formattedModel = null;
		for(Object[] model : matrix) {
			for(Object value : model) {
				if(value instanceof String) {
					value = String.format("%-60s", value);
				} else if(value instanceof Double) {
					value = value.toString();
					value = String.format("%-20s", value);
				}
			}
			formattedModel = (String[]) model; //erro aqi
			formattedMatrix.add(formattedModel);
		}
		return formattedMatrix;
	}
	
	public static void run(int testProgram) throws Exception {
		
		deleteModelsAfterTest = true; //remove after testing
		
		Instant start = Instant.now();
		if(testProgram == 1) {
			buildAllModels();
			testAllModels();
		} else {
			testAllModels();
		}
		
		Instant finish = Instant.now();
		double seconds = (Duration.between(start, finish).toMillis()/1000);
		int minutes = (int) seconds/60;
		int remainingSec = (int) (seconds - (minutes*60));
		System.out.println("Structured test duration: " + minutes + " minutes and " + remainingSec + " seconds.");
		
		if(deleteModelsAfterTest) {
			FileUtils.cleanDirectory(new File(modelsPath));
		}
	}

}
