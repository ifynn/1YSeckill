package com.fynn.oyseckill.app.module.home.util;

import com.fynn.oyseckill.model.entity.Product;

import java.util.Comparator;

/**
 * Created by Fynn on 2016/7/5.
 */
public abstract class ProductComparator implements Comparator<Product> {

    public static ProductComparator getComparator(ComparatorType type) {
        switch (type) {
            case RECENT:
                return new RecentCmprt();

            case PROGRESS:
                return new ProgressCmprt();

            case POPULARITY:
            default:
                return new PopularityCmprt();

            case PERSON_TIMES:
                return new PersonTimesCmprt(false);

            case PERSON_TIMES_SEQUENCE:
                return new PersonTimesCmprt(true);
        }
    }
}
