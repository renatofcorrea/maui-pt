package com.entopix.maui.utils;

import java.util.List;

import com.entopix.maui.filters.MauiFilter;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.main.MauiModelBuilder;
import com.entopix.maui.main.MauiTopicExtractor;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stemmers.Stemmer;
import com.entopix.maui.stopwords.Stopwords;
import com.entopix.maui.stopwords.StopwordsPortuguese;
import com.entopix.maui.util.DataLoader;
import com.entopix.maui.util.Evaluator;
import com.entopix.maui.util.MauiDocument;
import com.entopix.maui.util.MauiTopics;

import weka.core.Utils;

public class StructuredTest {
	
	static String testDocsPath = Paths.dataPath + "\\docs\\structured_test\\test\\test30";
	
	static String trainDocsPath = Paths.dataPath + "\\docs\\structured_test\\train";
	static String summariesDocPath = trainDocsPath + "\\summaries";
	static String fullTextsDocPath = trainDocsPath + "\\full_texts";
	
	static String modelsPath = Paths.dataPath + "\\models\\structured_test";
	static String summariesModelPath = modelsPath + "\\summaries";
	static String fullTextModelPath = modelsPath + "\\full_texts";
	
	static String vocabPath = Paths.dataPath + "\\vocabulary\\TBCI-SKOS_pt.rdf";
	
	/**
	 * 
	 * @param docType summary or fulltext
	 * @param docCount 10,20 or 30
	 * @throws Exception
	 */
	static void buildModel(String docType, int docCount) throws Exception {
		MauiModelBuilder modelBuilder = new MauiModelBuilder();
		modelBuilder.vocabularyFormat = "skos";
		modelBuilder.vocabularyName = vocabPath;
		modelBuilder.stemmer = new PortugueseStemmer();
		modelBuilder.stopwords = new StopwordsPortuguese();
		modelBuilder.documentLanguage = "pt";
		modelBuilder.documentEncoding = "UTF-8";
		modelBuilder.minNumOccur = 1;
		modelBuilder.maxPhraseLength = 5;
		modelBuilder.minPhraseLength = 1;
		modelBuilder.serialize = true;
		modelBuilder.setPositionsFeatures(false);
		modelBuilder.setKeyphrasenessFeature(false);
		modelBuilder.setThesaurusFeatures(false);
		
		if(docType.equals("summary")) {
			switch(docCount) {
			case 10:
				modelBuilder.inputDirectoryName = summariesDocPath + "\\train10";
				modelBuilder.modelName = summariesModelPath + "\\summary_model10";
				MauiFilter filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
				modelBuilder.saveModel(filter);
				break;
			case 20:
				modelBuilder.inputDirectoryName = summariesDocPath + "\\train20";
				modelBuilder.modelName = summariesModelPath + "\\summary_model20";
				filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
				modelBuilder.saveModel(filter);
				break;
			case 30:
				modelBuilder.inputDirectoryName = summariesDocPath + "\\train30";
				modelBuilder.modelName = summariesModelPath + "\\summary_model30";
				filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
				modelBuilder.saveModel(filter);
				break;
			default:
				throw new Exception("ERROR: Invalid docType in StructuredTest.buildModel");
			}
		} else if(docType.equals("fulltext")) {
			switch(docCount) {
			case 10:
				break;
			case 20:
				break;
			case 30:
				break;
			default:
				break;
			}
		}
	}
	
	static double[] testModel(String modelPath) throws MauiFilterException {
		MauiTopicExtractor topicExtractor = new MauiTopicExtractor();
		
		topicExtractor.inputDirectoryName = testDocsPath;
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
		return Evaluator.evaluateTopics(topics);
	}
	
	public static void runAllTests() throws Exception {
		double[] results = null;
		String[][] resultsString = {
				{"modelo1","precisao1","recall1","f-measure1"},
				{"modelo2","precisao2","recall2","f-measure2"},
				{"modelo3","precisao3","recall3","f-measure3"}};
		
		buildModel("summary", 10);
		buildModel("summary", 20);
		buildModel("summary", 30);
		
		results = testModel(summariesModelPath + "\\summary_model10");
		resultsString[0][0] = "summary_model10";
		for (int i = 0; i < 3; i++)
			resultsString[0][i+1] = Utils.doubleToString(results[i] * 100, 2);
		
		results = testModel(summariesModelPath + "\\summary_model20");
		resultsString[1][0] = "summary_model20";
		for (int i = 0; i < 3; i++)
			resultsString[1][i+1] = Utils.doubleToString(results[i] * 100, 2);
		
		results = testModel(summariesModelPath + "\\summary_model30");
		resultsString[2][0] = "summary_model10";
		for (int i = 0; i < 3; i++)
			resultsString[2][i+1] = Utils.doubleToString(results[i] * 100, 2);
		
		
		System.out.println("--------------------------------TEST RESULTS--------------------------------");
		System.out.print("MODEL\t\t\tPrecision\tRecall\t\tF-Measure \n");
		
		for (int modelNumber = 0; modelNumber < resultsString.length; modelNumber++) {
			for(int i = 0; i < resultsString[modelNumber].length; i++) {
				System.out.print(resultsString[modelNumber][i] + "\t\t");
			}
			System.out.println();
		}
		System.out.println("----------------------------------------------------------------------------");
	}

}
