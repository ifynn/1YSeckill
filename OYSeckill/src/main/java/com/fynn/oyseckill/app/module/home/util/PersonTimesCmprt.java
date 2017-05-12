package com.fynn.oyseckill.app.module.home.util;

import com.fynn.oyseckill.model.entity.Product;

/**
 * Created by Fynn on 2016/7/6.
 */
public class PersonTimesCmprt extends ProductComparator {

    private boolean inSequence;

    public PersonTimesCmprt(boolean inSequence) {
        this.inSequence = inSequence;
    }

    @Override
    public int compare(Product lhs, Product rhs) {
        Double lPrice = lhs.getPrice();
        Double rPrice = rhs.getPrice();

        return inSequence ? lPrice.compareTo(rPrice) : -lPrice.compareTo(rPrice);
    }
}
