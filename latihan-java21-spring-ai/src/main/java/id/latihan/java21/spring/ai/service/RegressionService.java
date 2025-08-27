package id.latihan.java21.spring.ai.service;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public interface RegressionService {

    String loadData();

    String predictPurchase(SimpleRegression simpleRegression, double income);

    String loadDataV2();

    String calculatePredict(String filepath);
}
