package id.latihan.java21.spring.ai.service;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import id.latihan.java21.spring.ai.exception.ApplicationException;
import id.latihan.java21.spring.ai.helper.DocumentType;
import id.latihan.java21.spring.ai.model.Prediction;
import org.nd4j.common.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ImageRecognitionServiceImpl implements ImageRecognitionService {

    private static final int RESIZE_WIDTH = 100;
    private static final int RESIZE_HEIGHT = 100;

    @Override
    public String loadData() {
        String folderPath = "images";
        ClassPathResource resource = new ClassPathResource(folderPath);
        StringBuilder sb = new StringBuilder();
        try {
            // 1. Create necessary dir
            File imageFolder = resource.getFile();
            Path output = Paths.get("latihan-java21-spring-ai/output");
            if (!Files.exists(output)) {
                Files.createDirectories(output);
            }

            // 2. Process image
            File[] imageFiles = imageFolder.listFiles();
            if (imageFiles != null && imageFiles.length > 0) {
                for (File imageFile : imageFiles) {
                    if (imageFile.isFile() && (
                            imageFile.getName().endsWith(DocumentType.ImageType.JPEG.extension())
                                    || imageFile.getName().endsWith(DocumentType.ImageType.JPG.extension())
                                    || imageFile.getName().endsWith(DocumentType.ImageType.PNG.extension())
                    )) {
                        sb.append(this.processImage(imageFile, output)).append("\n");
                    }
                }
            } else {
                throw new ApplicationException("""
                        No images found in directory: %s
                        Please add some product images to the 'images' folder.
                        """
                        .formatted(folderPath));
            }

            return """
                    Starting Product Recognition Lab
                    %s
                    Product recognition completed successfully!
                    """.formatted(sb.toString());
        } catch (IOException e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public String processImage(File imageFile, Path outputDir) {
        String fileName = imageFile.getName();
        StringBuilder outSb = new StringBuilder();

        outSb.append(String.format("Analyzing image : %s", fileName)).append("\n");
        try {
            // 1. Load image
            BufferedImage originalImage = ImageIO.read(imageFile);
            if (originalImage == null)
                throw new ApplicationException(String.format("Failed to load image: %s", fileName));

            // 2. Extract Image feature
            Map<String, Double> features = this.extractImageFeatures(originalImage);

            // Step 3: Classify the image based on features
            List<Prediction> predictions = this.classifyImage(features, imageFile.getName());

            // Step 4: Print the results
            outSb.append(String.format("Top 5 predictions for : %s", fileName)).append("\n");
            for (Prediction p : predictions) {
                String message = String.format("%-30s: %.2f%%", p.getLabel(), p.getProbability() * 100);
                outSb.append(message).append("\n");
            }
            outSb.append("-----------------------------------------").append("\n");

            // Step 5: Save the results to a file
            outSb.append(this.saveResultsToFile(fileName, predictions, outputDir)).append("\n");

            // Optional: Save a processed version of the image
            this.saveProcessedImage(originalImage, fileName, outputDir);

            return outSb.toString();
        } catch (IOException e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public Map<String, Double> extractImageFeatures(BufferedImage originalImage) {
        Map<String, Double> features = new HashMap<>();

        // Resize image for consistent analysis
        BufferedImage resized = this.resizeImage(originalImage, RESIZE_WIDTH, RESIZE_HEIGHT);

        // Calculate average color components
        double avgRed = 0;
        double avgGreen = 0;
        double avgBlue = 0;

        // Calculate brightness histogram
        int[] brightnessHistogram = new int[256];

        // Sample pixels from the image
        for (int y = 0; y < resized.getHeight(); y++) {
            for (int x = 0; x < resized.getWidth(); x++) {
                Color color = new Color(resized.getRGB(x, y));
                avgRed += color.getRed();
                avgGreen += color.getGreen();
                avgBlue += color.getBlue();

                // Calculate brightness (simple average of RGB)
                int brightness = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                brightnessHistogram[brightness]++;
            }
        }

        int totalPixels = resized.getWidth() * resized.getHeight();
        avgRed /= totalPixels;
        avgGreen /= totalPixels;
        avgBlue /= totalPixels;

        features.put("avgRed", avgRed / 255.0);
        features.put("avgGreen", avgGreen / 255.0);
        features.put("avgBlue", avgBlue / 255.0);

        // Calculate color ratios
        double redGreenRatio = avgRed / (avgGreen + 1);
        double blueGreenRatio = avgBlue / (avgGreen + 1);
        double redBlueRatio = avgRed / (avgBlue + 1);

        features.put("redGreenRatio", redGreenRatio);
        features.put("blueGreenRatio", blueGreenRatio);
        features.put("redBlueRatio", redBlueRatio);

        // Calculate brightness stats
        double avgBrightness = (avgRed + avgGreen + avgBlue) / 3 / 255.0;
        features.put("avgBrightness", avgBrightness);

        // Analyze edge density (simplified)
        double edgeDensity = this.calculateEdgeDensity(resized);
        features.put("edgeDensity", edgeDensity);

        // Calculate texture uniformity (simplified)
        double textureUniformity = this.calculateTextureUniformity(brightnessHistogram, totalPixels);
        features.put("textureUniformity", textureUniformity);

        return features;
    }

    @Override
    public BufferedImage resizeImage(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, original.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();
        return resized;
    }

    @Override
    public double calculateEdgeDensity(BufferedImage image) {
        int edgeCount = 0;
        int totalPixels = (image.getWidth() - 1) * (image.getHeight() - 1);

        for (int y = 0; y < image.getHeight() - 1; y++) {
            for (int x = 0; x < image.getWidth() - 1; x++) {
                Color pixel = new Color(image.getRGB(x, y));
                Color pixelRight = new Color(image.getRGB(x + 1, y));
                Color pixelBelow = new Color(image.getRGB(x, y + 1));

                int diffX = Math.abs(pixel.getRed() - pixelRight.getRed()) +
                        Math.abs(pixel.getGreen() - pixelRight.getGreen()) +
                        Math.abs(pixel.getBlue() - pixelRight.getBlue());

                int diffY = Math.abs(pixel.getRed() - pixelBelow.getRed()) +
                        Math.abs(pixel.getGreen() - pixelBelow.getGreen()) +
                        Math.abs(pixel.getBlue() - pixelBelow.getBlue());

                // If the difference is significant, count as an edge
                if (diffX > 100 || diffY > 100) {
                    edgeCount++;
                }
            }
        }

        return (double) edgeCount / totalPixels;
    }

    @Override
    public double calculateTextureUniformity(int[] histogram, int totalPixels) {
        double uniformity = 0;
        for (int j : histogram) {
            double p = (double) j / totalPixels;
            uniformity += p * p;
        }
        return uniformity;
    }

    @Override
    public List<Prediction> classifyImage(Map<String, Double> features, String filename) {
        List<Prediction> predictions = new ArrayList<>();

        // Using filename for simulation, since this is just a demonstration
        filename = filename.toLowerCase();

        // Initialize with low probabilities
        predictions.add(new Prediction("laptop", 0.01));
        predictions.add(new Prediction("water bottle", 0.01));
        predictions.add(new Prediction("coffee mug", 0.01));
        predictions.add(new Prediction("book", 0.01));
        predictions.add(new Prediction("smartphone", 0.01));

        // Feature-based classification (simplified rules)
        double avgRed = features.get("avgRed");
        double avgGreen = features.get("avgGreen");
        double avgBlue = features.get("avgBlue");
        double edgeDensity = features.get("edgeDensity");
        double textureUniformity = features.get("textureUniformity");

        // Example classification rules (simplified)
        // These are just for demonstration purposes, not accurate classification

        // Dark colors with high edge density might be electronic devices
        if (avgRed < 0.5 && avgGreen < 0.5 && avgBlue < 0.5 && edgeDensity > 0.1) {
            predictions.get(0).setProbability(0.6); // laptop
            predictions.get(4).setProbability(0.3); // smartphone
        }

        // Blue tones might suggest water bottles
        if (avgBlue > avgRed && avgBlue > avgGreen) {
            predictions.get(1).setProbability(0.7); // water bottle
        }

        // High uniformity might suggest solid objects like mugs
        if (textureUniformity > 0.1 && avgRed > 0.3) {
            predictions.get(2).setProbability(0.65); // coffee mug
        }

        // Medium brightness with texture might be books
        if (avgRed > 0.3 && avgRed < 0.7 && textureUniformity < 0.1) {
            predictions.get(3).setProbability(0.55); // book
        }

        // Override with filename-based simulated results for this demo
        if (filename.contains("laptop")) {
            predictions.get(0).setProbability(0.92); // laptop
            predictions.get(4).setProbability(0.05); // smartphone
        } else if (filename.contains("bottle") || filename.contains("water")) {
            predictions.get(1).setProbability(0.89); // water bottle
        } else if (filename.contains("mug") || filename.contains("coffee")) {
            predictions.get(2).setProbability(0.94); // coffee mug
        } else if (filename.contains("book")) {
            predictions.get(3).setProbability(0.91); // book
        } else if (filename.contains("phone") || filename.contains("smartphone")) {
            predictions.get(4).setProbability(0.95); // smartphone
            predictions.get(0).setProbability(0.03); // laptop
        }

        // Sort by probability
        predictions.sort((a, b) -> Double.compare(b.getProbability(), a.getProbability()));

        return predictions;
    }

    @Override
    public String saveResultsToFile(String imageName, List<Prediction> results, Path outputDir) {
        // Create a file named after the image
        String filename = imageName.substring(0, imageName.lastIndexOf('.')) + "_results.txt";
        Path outputFile = outputDir.resolve(filename);

        // Write the results to the file
        try (FileWriter writer = new FileWriter(outputFile.toFile())) {
            StringBuilder sb = new StringBuilder();
            for (Prediction p : results) {
                sb.append(String.format("%-30s: %.2f%%",
                        p.getLabel(),
                        p.getProbability() * 100)).append("\n");
            }
            writer.write("""
                    Recognition results for: %s
                    -----------------------------------------
                    %s
                                        
                    Image Analysis:
                    This is a simplified image analysis demo that shows how
                    basic image processing can extract features like color,
                    texture, and edges. In a real AI system, these features
                    would be inputs to a machine learning model trained on
                    thousands of product images.
                    """
                    .formatted(
                            imageName,
                            sb.toString()));
        } catch (IOException e) {
            throw new ApplicationException(e.getMessage());
        }

        return String.format("Results saved to file: %s", outputFile);
    }

    @Override
    public void saveProcessedImage(BufferedImage original, String imageName, Path outputDir) {
        try {
            // Create a copy of the image to draw on
            BufferedImage processed = new BufferedImage(
                    original.getWidth(),
                    original.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g = processed.createGraphics();
            g.drawImage(original, 0, 0, null);

            // Add a simple border to show processing
            g.setColor(Color.GREEN);
            g.setStroke(new BasicStroke(5));
            g.drawRect(0, 0, processed.getWidth() - 1, processed.getHeight() - 1);

            // Add text showing it's been analyzed
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Analyzed", 20, 40);

            g.dispose();

            // Save the processed image
            String outputFilename = imageName.substring(0, imageName.lastIndexOf('.')) + "_processed.jpg";
            Path outputFile = outputDir.resolve(outputFilename);
            ImageIO.write(processed, "jpg", outputFile.toFile());

        } catch (IOException e) {
            throw new ApplicationException("Error saving processed image: " + e.getMessage());
        }
    }

    @Override
    public String loadDataV2() {
        return this.calculatePrediction("pill_bottle.png");
    }

    @Override
    public Classifications predict(Image image) {
        // Define translator (preprocessing)
        Translator<Image, Classifications> translator = ImageClassificationTranslator.builder()
                .addTransform(new Resize(224, 224))
                .addTransform(new ToTensor())
                .optApplySoftmax(true)
                .build();

        // Update criteria to use explicit model form TorchHub
        Criteria<Image, Classifications> criteria = Criteria.builder()
                .setTypes(Image.class, Classifications.class)
                .optTranslator(translator)
                .optArtifactId("resnet") // Use explicit model name
                .optEngine("PyTorch") // Specify engine
                .build();

        try (ZooModel<Image, Classifications> model = criteria.loadModel();
             Predictor<Image, Classifications> predictor = model.newPredictor()) {
            return predictor.predict(image);
        } catch (IOException | ModelNotFoundException | MalformedModelException | TranslateException e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public String calculatePrediction(String imageName) {
        ClassPathResource resource = new ClassPathResource(String.format("images/%s", imageName));
        try (InputStream inputStream = resource.getInputStream()) {

            // 1. Load Image
            Image image = ImageFactory.getInstance().fromInputStream(inputStream);

            // 2. Run Prediction
            Classifications predicts = this.predict(image);

            // 3. Print Result
            StringBuilder sb = new StringBuilder();
            sb.append("Top 5 Predictions : ").append("\n");
            List<Classifications.Classification> classifications = predicts.topK(5);
            for (Classifications.Classification data : classifications) {
                sb.append(String.format("%-30s : %.2f%%", data.getClassName(), data.getProbability() * 100)).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public String loadDataV3(String fileName) {
        return this.calculatePrediction(fileName);
    }
}
