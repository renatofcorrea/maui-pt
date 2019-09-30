package com.entopix.maui.tests;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

import weka.core.Utils;
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
	
	/**
	 * Tests a model precision, recall and f-measure, then return the results.
	 * @param docType abstracts or full_texts
	 * @param modelPath
	 * @throws MauiFilterException
	 */
	static String[] testModel(String modelPath, String testDirPath) throws MauiFilterException {
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
		
		double[] results = TestUtils.evaluateTopics(topics);
		
		//Converts results to string
		String modelName = new File(modelPath).getName();
		String[] resultString = new String[8];
		resultString[0] = modelName;
		int i;
		for(i = 1 ; i < resultString.length ; i++) {
			resultString[i] = Utils.doubleToString(results[i-1], 2);
		}
		return resultString;
	}
	/**
	 * Test all models, store the results in a matrix and print it.
	 * @throws MauiFilterException
	 */
	public static void testAllModels() throws MauiFilterException {
		File modelsDir = new File(modelsPath);
		File[] abstractsModelsList = MauiFileUtils.filterFileList(modelsDir.listFiles(), "abstracts");
		File[] fulltextsModelsList = MauiFileUtils.filterFileList(modelsDir.listFiles(), "abstracts");
		String testDir = null;
		
		//ALL ABSTRACTS TESTS
		List<String[]> abstractsResultsMatrix = new ArrayList<String[]>();

		testDir = abstractsDocsPath + "\\test30";
		abstractsResultsMatrix.add(new String[] {"\n---> Results based on 30 documents:\n"});
		for(File model : abstractsModelsList) {
			abstractsResultsMatrix.add(testModel(model.getAbsolutePath(), testDir));
		}
		
		testDir = abstractsDocsPath + "\\test60";
		abstractsResultsMatrix.add(new String[] {"\n---> Results based on 60 documents:\n"});
		for(File model : abstractsModelsList) {
			abstractsResultsMatrix.add(testModel(model.getAbsolutePath(), testDir));
		}
		
		
		//ALL FULLTEXTS TESTS
		List<String[]> fulltextsResultsMatrix = new ArrayList<String[]>();

		testDir = fullTextsDocsPath + "\\test30";
		fulltextsResultsMatrix.add(new String[] {"\n---> Results based on 30 documents:\n"});
		for(File f : fulltextsModelsList) {
			fulltextsResultsMatrix.add(testModel(f.getAbsolutePath(), testDir));
		}
		
		testDir = fullTextsDocsPath + "\\test60";
		fulltextsResultsMatrix.add(new String[] {"\n---> Results based on 60 documents:\n"});
		for(File f : fulltextsModelsList) {
			fulltextsResultsMatrix.add(testModel(f.getAbsolutePath(), testDir));
		}
		
		System.out.println("\n ---ABSTRACTS MODELS---");
		printTestResults(abstractsResultsMatrix);
		System.out.println("\n\n ---FULLTEXTS MODELS---");
		printTestResults(fulltextsResultsMatrix);
		System.out.println("\n");
		
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
	
	public static void printTestResults(List<String[]> matrix) {
		for(String[] model : matrix) {
			for(String value : model) {
				double stringSize = value.length();
				if(stringSize >= 51) System.out.print(value + "\t");
				else if(stringSize >= 41) System.out.print(value + "\t\t");
				else System.out.print(value + "\t\t\t");
				
			}
			System.out.println();
		}
	}
	
	public static void run(int testProgram) throws Exception {
		
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
	}

}
