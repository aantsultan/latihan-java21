package id.latihan.java21.spring.ai.service;

import au.com.bytecode.opencsv.CSVReader;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import id.latihan.java21.spring.ai.exception.ApplicationException;
import id.latihan.java21.spring.ai.helper.SentimentDistribution;
import id.latihan.java21.spring.ai.model.FeedbackEntry;
import lombok.RequiredArgsConstructor;
import org.nd4j.common.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SentimentServiceImpl implements SentimentService {

    private final StanfordCoreNLP stanfordCoreNLP;
    private final ResourceLoader resourceLoader;

    private static final Pattern FEEDBACK_NUMBER_PATTERN = Pattern.compile("Feedback #(\\d+).*");
    private static final Pattern CUSTOMER_PATTERN = Pattern.compile("Customer:\\s*(.+)");
    private static final Pattern DEPARTMENT_PATTERN = Pattern.compile("Department:\\s*(.+)");
    private static final Pattern DATE_PATTERN = Pattern.compile("Date:\\s*(.+)");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("Comment:\\s*(.+)");

    @Override
    public String loadData() {
        return this.calculate("sentiment/product_reviews.csv");
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
        return this.calculate("sentiment/new_products_review.csv");
    }

    @Override
    public String loadDataStore() {
        String inputFile = "sentiment/store_feedback.txt";
        String outputFile = "latihan-java21-spring-ai/output/sentiment/sentiment_feedback_output.txt";

        List<FeedbackEntry> feedbackEntries = this.processFeedbackFile(inputFile);
        StringBuilder sb = getStringBuilder(feedbackEntries);

        this.writeResults(feedbackEntries, outputFile);

        return sb.toString();
    }

    private static StringBuilder getStringBuilder(List<FeedbackEntry> feedbackEntries) {
        StringBuilder sb = new StringBuilder();
        for (FeedbackEntry entry : feedbackEntries) {
            sb.append("""
                    ID: %s
                    Customer: %s
                    Comment: %s
                    Sentiment: %s
                    -------------------------
                    """.formatted(entry.getId(),
                    entry.getCustomer(),
                    entry.getComment(),
                    entry.getSentiment()));
        }
        return sb;
    }

    @Override
    public List<FeedbackEntry> processFeedbackFile(String filePath) {
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            File file = resource.getFile();
            List<FeedbackEntry> feedbackEntries = new ArrayList<>();
            String content = new String(Files.readAllBytes(file.toPath()));

            // Split the content by the "Feedback #" pattern to get individual entries
            String[] rawEntries = content.split("(?=Feedback #)");

            for (int i = 0; i < rawEntries.length; i++) {
                String rawEntry = rawEntries[i].trim();

                if (ObjectUtils.isEmpty(rawEntry) || !rawEntry.startsWith("Feedback #")) {
                    continue;
                }

                FeedbackEntry entry = this.parseEntry(rawEntry);

                if (entry != null) {
                    String sentiment = this.analyzeSentiment(entry.getComment());
                    entry.setSentiment(sentiment);

                    feedbackEntries.add(entry);
                }
            }
            return feedbackEntries;
        } catch (Exception e) {
            throw new ApplicationException("Error: " + e.getMessage());
        }
    }

    @Override
    public FeedbackEntry parseEntry(String entryText) {
        try {
            FeedbackEntry entry = new FeedbackEntry();

            String[] lines = entryText.split("\n");

            for (String line : lines) {
                line = line.trim();

                // Extract Feedback number from first line
                if (line.startsWith("Feedback #")) {
                    Matcher numberMatcher = FEEDBACK_NUMBER_PATTERN.matcher(line);
                    if (numberMatcher.find()) entry.setId(Integer.parseInt(numberMatcher.group(1)));
                }

                // Extract customer info
                else if (line.startsWith("Customer:")) {
                    Matcher matcher = CUSTOMER_PATTERN.matcher(line);
                    if (matcher.find()) entry.setCustomer(matcher.group(1).trim());
                }

                // Extract department
                else if (line.startsWith("Department:")) {
                    Matcher matcher = DEPARTMENT_PATTERN.matcher(line);
                    if (matcher.find()) entry.setDepartment(matcher.group(1).trim());
                }

                // Extract date
                else if (line.startsWith("Date:")) {
                    Matcher matcher = DATE_PATTERN.matcher(line);
                    if (matcher.find()) entry.setDate(matcher.group(1).trim());
                }

                // Extract comment
                else if (line.startsWith("Comment:")) {
                    Matcher matcher = COMMENT_PATTERN.matcher(line);
                    if (matcher.find()) entry.setComment(matcher.group(1).trim());
                }

            }

            // Validate all required fields
            if (entry.getId() > 0 && entry.getComment() != null) {
                return entry;
            } else {
                throw new ApplicationException("""
                        Invalid entry - missing ID or comment
                        ID: %s
                        Comment: %s
                        """.formatted(entry.getId(), entry.getComment()));
            }
        } catch (Exception e) {
            throw new ApplicationException("Error: " + e.getMessage());
        }
    }

    @Override
    public String analyzeSentiment(String comment) {

        CoreDocument document = new CoreDocument(comment);

        stanfordCoreNLP.annotate(document);

        List<CoreSentence> sentences = document.sentences();

        if (sentences.isEmpty()) return "NEUTRAL";

        // Calculate the average sentiment
        Map<String, Integer> sentimentCount = new HashMap<>();
        for (CoreSentence sentence : sentences) {
            String sentiment = sentence.sentiment();
            sentimentCount.put(sentiment, sentimentCount.getOrDefault(sentiment, 0) + 1);
        }

        return sentimentCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("NEUTRAL");
    }

    @Override
    public void writeResults(List<FeedbackEntry> feedbackEntries, String outputFilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            writer.write("# Sentiment Analysis Results \n\n");

            writer.write("## Symmary Statistics \n\n");

            // Count sentiments
            Map<String, Long> sentimentCounts = feedbackEntries.stream()
                    .collect(Collectors.groupingBy(FeedbackEntry::getSentiment, Collectors.counting()));

            writer.write("Total Feedback Entries: " + feedbackEntries.size());
            writer.write("\nSentiment Distribution: ");

            for (Map.Entry<String, Long> entry : sentimentCounts.entrySet()) {
                double percentage = ((double) entry.getValue() / feedbackEntries.size()) * 100;
                writer.write(String.format("- %s: %d (%.1f%%)%n", entry.getKey(), entry.getValue(), percentage));
            }

            writer.write("\n## Department Analysis\n\n");

            // Group by department
            Map<String, List<FeedbackEntry>> byDepartment = feedbackEntries.stream().collect(Collectors.groupingBy(FeedbackEntry::getDepartment));
            for (Map.Entry<String, List<FeedbackEntry>> entry : byDepartment.entrySet()) {
                writer.write(String.format("### %s%n%n", entry.getKey()));

                Map<String, Long> deptSentimentCount = entry.getValue().stream().collect(Collectors.groupingBy(FeedbackEntry::getSentiment, Collectors.counting()));

                for (Map.Entry<String, Long> sentCount : deptSentimentCount.entrySet()) {
                    double percentage = ((double) sentCount.getValue() / entry.getValue().size()) * 100;
                    writer.write((String.format("- %s: %d (%.1f%%)%n", sentCount.getKey(), sentCount.getValue(), percentage)));
                }

                writer.write("\n");
            }

            writer.write("## Detailed Feedback Entries\n\n");

            for (FeedbackEntry entry : feedbackEntries) {
                writer.write("""
                        Feedback #%s
                        Customer: %s
                        Department: %s
                        Date: %s
                        Comment: %s
                        Sentiment: %s
                        """);
            }

        } catch (Exception e) {
            throw new ApplicationException("Error: " + e.getMessage());
        }
    }
}
