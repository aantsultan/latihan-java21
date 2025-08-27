package id.latihan.java21.spring.ai.service;

import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import id.latihan.java21.spring.ai.model.Prediction;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface ImageRecognitionService {

    String loadData();

    String processImage(File imageFile, Path outputDir);

    /**
     * Extract basic features from the image
     */
    Map<String, Double> extractImageFeatures(BufferedImage originalImage);

    /**
     * Resize an image to specified dimensions
     */
    BufferedImage resizeImage(BufferedImage original, int width, int height);

    /**
     * Calculate a simple edge density metric
     */
    double calculateEdgeDensity(BufferedImage image);

    /**
     * Calculate texture uniformity based on brightness histogram
     */
    double calculateTextureUniformity(int[] histogram, int totalPixels);

    /**
     * Classify the image based on extracted features
     */
    List<Prediction> classifyImage(Map<String, Double> features, String filename);

    /**
     * Save the recognition results to a text file
     */
    String saveResultsToFile(String imageName, List<Prediction> results, Path outputDir);

    /**
     * Save a processed version of the image
     */
    void saveProcessedImage(BufferedImage original, String imageName, Path outputDir);

    String loadDataV2();

    Classifications predict(Image image);

    String calculatePrediction(String imageName);

    String loadDataV3(String fileName);
}
