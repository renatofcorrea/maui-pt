package com.entopix.maui.tests;

import java.util.List;

import com.entopix.maui.util.MauiTopics;
import com.entopix.maui.util.Topic;

import weka.core.Utils;

public class TestUtils {

	/**
	 * Returns a array of doubles of size 7 containing the test results.
	 * @param allDocumentsTopics
	 * @return
	 */
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
	
			//Average precision (%)
			double avgPrecision = Utils.mean(precisionStatistics) * 100;
			double stdDevPrecision = Math.sqrt(Utils.variance(precisionStatistics)) * 100;
	
			//Average recall (%)
			double avgRecall = Utils.mean(recallStatistics) * 100;
			double stdDevRecall = Math.sqrt(Utils.variance(recallStatistics)) * 100;
	
			//F-Measure
			double fMeasure = 0.0;
			if (avgPrecision > 0 && avgRecall > 0) {
				fMeasure = 2 * avgRecall * avgPrecision / (avgRecall + avgPrecision);
			}
	
			results = new double[] {avg, stdDev, avgPrecision, stdDevPrecision, avgRecall, stdDevRecall, fMeasure};
		}
		return results;
	}

}
