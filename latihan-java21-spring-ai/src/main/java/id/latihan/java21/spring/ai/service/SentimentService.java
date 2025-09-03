package id.latihan.java21.spring.ai.service;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import id.latihan.java21.spring.ai.model.FeedbackEntry;

import java.util.List;
import java.util.Map;

public interface SentimentService {

    String loadData();

    String calculate(String filepath);

    String analyzerSentiment(String text, StanfordCoreNLP pipeline);

    String generateBar(double percentage);

    String shortenText(String text, int maxLength);

    String generateReport(Map<String, Integer> sentimentCounts, int totalReviews);

    String loadDataV2();

    String loadDataStore();

    List<FeedbackEntry> processFeedbackFile(String filePath);

    FeedbackEntry parseEntry(String entryText);

    String analyzeSentiment(String comment);

    void writeResults(List<FeedbackEntry> feedbackEntries, String outputFilePath);
}
