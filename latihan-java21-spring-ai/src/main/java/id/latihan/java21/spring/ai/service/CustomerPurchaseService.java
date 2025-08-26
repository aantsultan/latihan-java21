package id.latihan.java21.spring.ai.service;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public interface CustomerPurchaseService {
    void loadData();

    void predictPurchase(SimpleRegression simpleRegression, double income);

    void loadDataV2();
}
