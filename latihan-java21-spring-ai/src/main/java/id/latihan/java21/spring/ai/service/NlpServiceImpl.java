package id.latihan.java21.spring.ai.service;

import au.com.bytecode.opencsv.CSVReader;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import id.latihan.java21.spring.ai.exception.ApplicationException;
import id.latihan.java21.spring.ai.helper.SentimentDistribution;
import org.nd4j.common.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class NlpServiceImpl implements NlpService {
    @Override
    public String loadData() {
        return this.calculate("nlp/product_reviews.csv");
    }

    @Override
    public String calculate(String filepath) {
        Properties properties = new Properties();
        properties.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        properties.setProperty("core.algorithm", "neural");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
        ClassPathResource resource = new ClassPathResource(filepath);

        try (CSVReader reader = new CSVReader(new FileReader(resource.getFile()))) {
            List<String[]> reviews = reader.readAll();
            Map<String, Integer> sentimentDistribution = new LinkedHashMap<>();
            sentimentDistribution.put(SentimentDistribution.VERY_POSITIVE.getName(), 0);
            sentimentDistribution.put(SentimentDistribution.POSITIVE.getName(), 0);
            sentimentDistribution.put(SentimentDistribution.NEUTRAL.getName(), 0);
            sentimentDistribution.put(SentimentDistribution.NEGATIVE.getName(), 0);
            sentimentDistribution.put(SentimentDistribution.VERY_NEGATIVE.getName(), 0);

            StringBuilder sbReview = new StringBuilder();
            for (String[] review : reviews) {
                if (review[0].equals("review_id")) continue; // Skip header

                String sentiment = this.analyzerSentiment(review[1], pipeline);
                sentimentDistribution.put(sentiment, sentimentDistribution.get(sentiment) + 1);
                sbReview.append(String.format("Review %2s: %-60s - %s",
                        review[0], this.shortenText(review[1], 55), sentiment)).append("\n");
            }

            String report = this.generateReport(sentimentDistribution, reviews.size() - 1);
            return """
                    === Review Sentiment Analysis ===
                    %s
                                        
                    %s
                    """
                    .formatted(
                            sbReview.toString(),
                            report
                    );

        } catch (IOException e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public String analyzerSentiment(String text, StanfordCoreNLP pipeline) {
        int mainSentiment = 0;
        int longest = 0;

        Annotation annotation = pipeline.process(text);
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            int sentiment = RNNCoreAnnotations.getPredictedClass(sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
            String partText = sentence.toString();

            if (partText.length() > longest) {
                mainSentiment = sentiment;
                longest = partText.length();
            }
        }
        return switch (mainSentiment) {
            case 0 -> SentimentDistribution.VERY_NEGATIVE.getName();
            case 1 -> SentimentDistribution.NEGATIVE.getName();
            case 3 -> SentimentDistribution.POSITIVE.getName();
            case 4 -> SentimentDistribution.VERY_POSITIVE.getName();
            default -> // 2
                    SentimentDistribution.NEUTRAL.getName();
        };
    }

    @Override
    public String generateBar(double percentage) {
        int bars = (int) (percentage / 5);
        return "[" + new String(new char[bars]).replace("\0", "=") + "]";
    }

    @Override
    public String shortenText(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }

    @Override
    public String generateReport(Map<String, Integer> sentimentCounts, int totalReviews) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : sentimentCounts.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / totalReviews;
            sb.append(String.format("%-12s: %2d reviews (%5.1f%%) %s",
                    entry.getKey(),
                    entry.getValue(),
                    percentage,
                    generateBar(percentage))).append("\n");
        }
        return """
                === Sentiment Analysis Report ===
                Total Reviews Analyzed: %d
                Sentiment Distribution:
                %s
                """
                .formatted(
                        totalReviews,
                        sb.toString());
    }

    @Override
    public String loadDataV2() {
        return this.calculate("nlp/new_products_review.csv");
    }
}
