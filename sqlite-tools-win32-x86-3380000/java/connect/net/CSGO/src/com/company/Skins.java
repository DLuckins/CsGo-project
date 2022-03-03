package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Skins {
    private String skinsName;
    private String collection;
    private float skinsPrice;
    private float gap;
    private String neededWear;
    private List<Float> nextTierAvgPricesForEachTier;
    private int nextTierOutcomeCount;
    private float neededROI;

    public Skins(String skinsName, String collection, float skinsPrice, float gap, String neededWear ,float priceBS, float priceWW, float priceFT, float priceMW, float priceFN , int nextTierOutcomeCount) {
        this.skinsName = skinsName;
        this.collection = collection;
        this.skinsPrice = skinsPrice;
        this.gap = gap;
        this.nextTierAvgPricesForEachTier = new ArrayList<>(Arrays.asList(priceBS, priceWW, priceFT, priceMW, priceFN));
        this.nextTierOutcomeCount = nextTierOutcomeCount;
        //needed ROI for profit
    }
}
