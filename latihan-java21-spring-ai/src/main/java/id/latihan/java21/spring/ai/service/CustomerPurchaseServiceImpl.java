package id.latihan.java21.spring.ai.service;

import au.com.bytecode.opencsv.CSVReader;
import id.latihan.java21.spring.ai.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.nd4j.common.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class CustomerPurchaseServiceImpl implements CustomerPurchaseService {
    @Override
    public void loadData() {
        ClassPathResource resource = new ClassPathResource("customer_purchases.csv");
        try (CSVReader csvReader = new CSVReader(new FileReader(resource.getFile()))) {
            // 1. Load dataset
            List<String[]> data = csvReader.readAll();

            // 2. Prepare the regression
            SimpleRegression regression = new SimpleRegression();
            for (int i = 1; i < data.size(); i++) {
                String[] row = data.get(i);
                double income = Double.parseDouble(row[2]);
                double purchaseAmount = Double.parseDouble(row[3]);
                regression.addData(income, purchaseAmount);
            }

            // 3. Print Model Statistics
            log.info("=== Model Summary ===");
            log.info("R-squared: {}", String.format("%.4f", regression.getRSquare()));
            log.info("Intercept: {}", String.format("%.2f", regression.getIntercept()));
            log.info("Slope: {}", String.format("%.4f", regression.getSlope()));
            log.info("Standard Error: {}", String.format("%.4f", regression.getRegressionSumSquares()));

            // 4. Print
            log.info("=== Predictions ===");
            this.predictPurchase(regression, 40000);
            this.predictPurchase(regression, 55000);
            this.predictPurchase(regression, 80000);

        } catch (IOException e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    public void predictPurchase(SimpleRegression simpleRegression, double income) {
        double predict = simpleRegression.predict(income);
        log.info("Predict purchase for {} income: {}", String.format("$%,.2f", income), String.format("$%,.2f", predict));
    }

    @Override
    public void loadDataV2() {
        ClassPathResource resource = new ClassPathResource("customer_purchases_v2.csv");
        try (CSVReader csvReader = new CSVReader(new FileReader(resource.getFile()))) {
            // 1. Load dataset
            List<String[]> data = csvReader.readAll();

            // 2. Prepare the regression
            SimpleRegression regression = new SimpleRegression();
            for (int i = 1; i < data.size(); i++) {
                String[] row = data.get(i);
                double income = Double.parseDouble(row[2]);
                double purchaseAmount = Double.parseDouble(row[3]);
                regression.addData(income, purchaseAmount);
            }

            // 3. Print Model Statistics
            log.info("=== Model Summary ===");
            log.info("R-squared: {}", String.format("%.4f", regression.getRSquare()));
            log.info("Intercept: {}", String.format("%.2f", regression.getIntercept()));
            log.info("Slope: {}", String.format("%.4f", regression.getSlope()));
            log.info("Standard Error: {}", String.format("%.4f", regression.getRegressionSumSquares()));

            // 4. Print
            log.info("=== Predictions ===");
            this.predictPurchase(regression, 40000);
            this.predictPurchase(regression, 55000);
            this.predictPurchase(regression, 80000);

        } catch (IOException e) {
            throw new ApplicationException(e.getMessage());
        }
    }
}
