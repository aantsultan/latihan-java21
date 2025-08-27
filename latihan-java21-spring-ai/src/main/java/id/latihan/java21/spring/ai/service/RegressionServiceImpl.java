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
public class RegressionServiceImpl implements RegressionService {

    @Override
    public String loadData() {
        return this.calculatePredict("regression/customer_purchases.csv");
    }

    @Override
    public String predictPurchase(SimpleRegression simpleRegression, double income) {
        double predict = simpleRegression.predict(income);
        return String.format("Predict purchase for $%,.2f income: $%,.2f", income, predict);
    }

    @Override
    public String loadDataV2() {
        return this.calculatePredict("regression/customer_purchases_v2.csv");
    }

    @Override
    public String calculatePredict(String filepath) {
        ClassPathResource resource = new ClassPathResource(filepath);
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
            return """
                    === Model Summary ===
                    R-squared: %.4f
                    Intercept: %.2f
                    Slope: %.4f
                    Standard Error: %.4f
                                        
                    === Predictions ===
                    %s
                    %s
                    %s
                    """
                    .formatted(
                            regression.getRSquare(),
                            regression.getIntercept(),
                            regression.getSlope(),
                            regression.getRegressionSumSquares(),
                            this.predictPurchase(regression, 40000),
                            this.predictPurchase(regression, 55000),
                            this.predictPurchase(regression, 80000));
        } catch (IOException e) {
            throw new ApplicationException(e.getMessage());
        }
    }
}
