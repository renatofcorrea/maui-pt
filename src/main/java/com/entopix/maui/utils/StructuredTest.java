package com.entopix.maui.utils;

import java.util.List;

import com.entopix.maui.filters.MauiFilter;
import com.entopix.maui.filters.MauiFilter.MauiFilterException;
import com.entopix.maui.main.MauiModelBuilder;
import com.entopix.maui.main.MauiTopicExtractor;
import com.entopix.maui.stemmers.PortugueseStemmer;
import com.entopix.maui.stopwords.StopwordsPortuguese;
import com.entopix.maui.util.DataLoader;
import com.entopix.maui.util.MauiDocument;
import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.util.Topic;

import weka.core.Utils;

public class StructuredTest {
	
	static String testDocsPath = Paths.getDataPath() + "\\docs\\structured_test\\test\\test30";
	
	static String trainDocsPath = Paths.getDataPath() + "\\docs\\structured_test\\train";
	static String abstractsDocPath = trainDocsPath + "\\abstracts";
	static String fullTextsDocPath = trainDocsPath + "\\full_texts";
	
	static String modelsPath = Paths.getDataPath() + "\\models\\structured_test";
	static String abstractsModelPath = modelsPath + "\\abstracts";
	static String fullTextModelPath = modelsPath + "\\full_texts";
	
	static String vocabPath = Paths.getDataPath() + "\\vocabulary\\TBCI-SKOS_pt.rdf";
	
	/**
	 * 
	 * @param docType abstracts or fulltext
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
		
		if(docType.equals("abstracts")) {
			switch(docCount) {
			case 10:
				modelBuilder.inputDirectoryName = abstractsDocPath + "\\train10";
				modelBuilder.modelName = abstractsModelPath + "\\abstracts_model10";
				MauiFilter filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
				modelBuilder.saveModel(filter);
				break;
			case 20:
				modelBuilder.inputDirectoryName = abstractsDocPath + "\\train20";
				modelBuilder.modelName = abstractsModelPath + "\\abstracts_model20";
				filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
				modelBuilder.saveModel(filter);
				break;
			case 30:
				modelBuilder.inputDirectoryName = abstractsDocPath + "\\train30";
				modelBuilder.modelName = abstractsModelPath + "\\abstracts_model30";
				filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
				modelBuilder.saveModel(filter);
				break;
			default:
				throw new Exception("ERROR: Invalid docCount in StructuredTest.buildModel");
			}
		} else if(docType.equals("full_text")) {
			switch(docCount) {
			case 10:
				modelBuilder.inputDirectoryName = fullTextsDocPath + "\\train10";
				modelBuilder.modelName = fullTextModelPath + "\\full_text_model10";
				MauiFilter filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
				modelBuilder.saveModel(filter);
				break;
			case 20:
				modelBuilder.inputDirectoryName = fullTextsDocPath + "\\train20";
				modelBuilder.modelName = fullTextModelPath + "\\full_text_model20";
				filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
				modelBuilder.saveModel(filter);
				break;
			case 30:
				modelBuilder.inputDirectoryName = fullTextsDocPath + "\\train30";
				modelBuilder.modelName = fullTextModelPath + "\\full_text_model30";
				filter = modelBuilder.buildModel(DataLoader.loadTestDocuments(modelBuilder.inputDirectoryName));
				modelBuilder.saveModel(filter);
				break;
			default:
				throw new Exception("ERROR: Invalid docCount in StructuredTest.buildModel");
			}
		} else {
			throw new Exception("ERROR: Invalid docType in StructuredTest.buildModel");
		}
	}
	
	public static double[] evaluateTopics(List<MauiTopics> allDocumentsTopics) {

		double[] results = null;

		double[] correctStatistics = new double[allDocumentsTopics.size()];
		double[] precisionStatistics = new double[allDocumentsTopics.size()];
		double[] recallStatistics = new double[allDocumentsTopics.size()];

		int i = 0;
		for (MauiTopics documentTopics : allDocumentsTopics) {
			double numExtracted = documentTopics.getTopics().size(), numCorrect = 0;

			for (Topic topic : documentTopics.getTopics()) { //Counts the amount of correct keyphrases found by the model
				if (topic.isCorrect()) {
					numCorrect += 1.0;
				}
			}

			if (numExtracted > 0 && documentTopics.getPossibleCorrect() > 0) {
				//log.debug("-- " + numCorrect + " correct");
				correctStatistics[i] = numCorrect;
				precisionStatistics[i] = numCorrect / numExtracted;				
				recallStatistics[i] = numCorrect / documentTopics.getPossibleCorrect();

			} else {
				correctStatistics[i] = 0.0;
				precisionStatistics[i] = 0.0;	
				recallStatistics[i] = 0.0;
			}
			i++;
		}

		if (correctStatistics.length != 0) {
			//Average number of correct keyphrases per document
			double avg = Utils.mean(correctStatistics);
			double stdDev = Math.sqrt(Utils.variance(correctStatistics));

			//Average precision
			double avgPrecision = Utils.mean(precisionStatistics);
			double stdDevPrecision = Math.sqrt(Utils.variance(precisionStatistics));

			//Average recall
			double avgRecall = Utils.mean(recallStatistics);
			double stdDevRecall = Math.sqrt(Utils.variance(recallStatistics));

			//F-Measure
			double fMeasure = 0.0;
			if (avgPrecision > 0 && avgRecall > 0) {
				fMeasure = 2 * avgRecall * avgPrecision / (avgRecall + avgPrecision);
			}

			results = new double[] {avg, stdDev, avgPrecision, stdDevPrecision, avgRecall, stdDevRecall, fMeasure};
		}
		return results;
	}
	
	
	/**
	 * Tests a model precision, recall and f-measure, then return the results.
	 * @param modelPath
	 * @throws MauiFilterException
	 */
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
		return evaluateTopics(topics);
	}
	
	public static void runAllTests() throws Exception {
		double[] results = null;
		String[][] resultsString = {
				{"modelA","correctkeys1","correctkeys2","precision1","precision2","recall1","recall2","f-measure"},
				{"modelB","correctkeys1","correctkeys2","precision1","precision2","recall1","recall2","f-measure"},
				{"modelB","correctkeys1","correctkeys2","precision1","precision2","recall1","recall2","f-measure"},
				{"modelC","correctkeys1","correctkeys2","precision1","precision2","recall1","recall2","f-measure"},
				{"modelD","correctkeys1","correctkeys2","precision1","precision2","recall1","recall2","f-measure"},
				{"modelE","correctkeys1","correctkeys2","precision1","precision2","recall1","recall2","f-measure"},
		};
		
		buildModel("abstracts", 10);
		buildModel("abstracts", 20);
		buildModel("abstracts", 30);
		buildModel("full_text",10);
		buildModel("full_text",20);
		buildModel("full_text",30);
		
		results = testModel(abstractsModelPath + "\\abstracts_model10");
		resultsString[0][0] = "abstracts_model10";
		for (int i = 0; i < 7; i++) {
			if(i > 1) {results[i] *= 100;}
			resultsString[0][i+1] = Utils.doubleToString(results[i], 2);
		}
		
		results = testModel(abstractsModelPath + "\\abstracts_model20");
		resultsString[1][0] = "abstracts_model20";
		for (int i = 0; i < 7; i++) {
			if(i > 1) {results[i] *= 100;}
			resultsString[1][i+1] = Utils.doubleToString(results[i], 2);
		}
		
		results = testModel(abstractsModelPath + "\\abstracts_model30");
		resultsString[2][0] = "abstracts_model30";
		for (int i = 0; i < 7; i++) {
			if(i > 1) {results[i] *= 100;}
			resultsString[2][i+1] = Utils.doubleToString(results[i], 2);
		}
		
		results = testModel(fullTextModelPath + "\\full_text_model10");
		resultsString[3][0] = "full_text_model10";
		for (int i = 0; i < 7; i++) {
			if(i > 1) {results[i] *= 100;}
			resultsString[3][i+1] = Utils.doubleToString(results[i], 2);
		}
		
		results = testModel(fullTextModelPath + "\\full_text_model20");
		resultsString[4][0] = "full_text_model20";
		for (int i = 0; i < 7; i++) {
			if(i > 1) {results[i] *= 100;}
			resultsString[4][i+1] = Utils.doubleToString(results[i], 2);
		}
		
		results = testModel(fullTextModelPath + "\\full_text_model30");
		resultsString[5][0] = "full_text_model30";
		for (int i = 0; i < 7; i++) {
			if(i > 1) {results[i] *= 100;}
			resultsString[5][i+1] = Utils.doubleToString(results[i], 2);
		}
		
		System.out.println("-TEST RESULTS-");
		System.out.println("Model\t\t\tCorrect Keyphrases\t\tPrecision\t\t\tRecall\t\t\t\tF-Measure");
		System.out.println("     \t\t\tAvg\t\tStdDev\t\tAvg\t\tStdDev\t\tAvg\t\tStdDev \n");
		
		for (int modelNumber = 0; modelNumber < resultsString.length; modelNumber++) {
			for(int i = 0; i < resultsString[modelNumber].length; i++) {
				if(i == 0)
					System.out.print(resultsString[modelNumber][i] + "\t");
				else 
					System.out.print(resultsString[modelNumber][i] + "\t\t");
			}
			System.out.println();
		}
		System.out.println();
	}

}
