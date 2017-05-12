package com.fynn.oyseckill.app.module.home.util;

import com.fynn.oyseckill.model.entity.Issue;
import com.fynn.oyseckill.model.entity.Product;

/**
 * Created by Fynn on 2016/7/6.
 */
public class ProgressCmprt extends ProductComparator {

    @Override
    public int compare(Product lhs, Product rhs) {
        Issue lIssue = lhs.getCurrentIssue();
        Issue rIssue = rhs.getCurrentIssue();
        if (lIssue == null || rIssue == null) {
            return 0;
        }

        Long lPt = lIssue.getPersonTimes() == null ? 0 : lIssue.getPersonTimes();
        Long rPt = rIssue.getPersonTimes() == null ? 0 : rIssue.getPersonTimes();

        Double lPrice = lhs.getPrice();
        Double rPrice = rhs.getPrice();

        Double lPercent = lPt / lPrice;
        Double rPercent = rPt / rPrice;

        return -lPercent.compareTo(rPercent);
    }
}
