package com.entopix.maui.tests;

/**
 * Class used for tests during development. Should be removed before release.
 * @author Rahmon Jorge
 *
 */

public class TempClass {
	
	public static void main(String[] args) throws Exception {
		/*
		//Variable init
		String docPath = MauiFileUtils.getDataPath() + "\\docs\\corpusci\\fulltexts\\test30";
		File[] docList = MauiFileUtils.filterFileList(docPath, ".txt");
		String[] docPaths = MauiFileUtils.getFileListPaths(docList);
		String modelPath = "C:\\Users\\PC1\\git\\maui-pt\\data\\models\\model_fulltexts_PortugueseStemmer_train30";
		Stemmer stemmer = new PortugueseStemmer();
		
		//MauiCore setup
		MauiCore.DB_evaluateTopics = true;
		MauiCore.DB_evaluateTopicsSingle = true;
		MauiCore.setNumTopicsToExtract(10);
		MauiCore.setCutOffTopicProbability(0.12);
		MauiCore.setStemmer(stemmer);
		MauiCore.setTestDirPath(docPath);
		MauiCore.setModelPath(modelPath);
		
		List<MauiTopics> mauiTopics = MauiCore.runTopicExtractor();
		
		List<List<String>> extractedTopics = MauiPTUtils.mauiTopicsToString(mauiTopics);
		
		double[] numCorrect = MauiCore.evaluateTopics(docPaths, extractedTopics, 10);
		
		
		//Counts and prints topics marked as true
		List<Topic> topics;
		int[] correctTopicsCount = new int[mauiTopics.size()];
		int i;
		for (i = 0; i < mauiTopics.size(); i++) {
			topics = mauiTopics.get(i).getTopics();
			for (Topic t : topics) {
				if (t.isCorrect()) {
					correctTopicsCount[i]++;
				}
			}
			System.out.println(correctTopicsCount[i] + " marked as 'true' for document " + new File(mauiTopics.get(i).getFilePath()).getName());
			if (correctTopicsCount[i] == numCorrect[i]) {
				System.out.println("TopicExtractor evaluation matches manual evaluation.");
			} else {
				System.out.println("TopicExtractor evaluation does NOT matches manual evaluation.");
			}
		}

		
		*/
		
	}
}
