package com.fynn.oyseckill.app.module.home.util;

import com.fynn.oyseckill.model.entity.Issue;
import com.fynn.oyseckill.model.entity.Product;

import java.util.Date;

import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by Fynn on 2016/7/6.
 */
public class PopularityCmprt extends ProductComparator {

    @Override
    public int compare(Product lhs, Product rhs) {
        Issue lIssue = lhs.getCurrentIssue();
        Issue rIssue = rhs.getCurrentIssue();

        if (lIssue == null || rIssue == null) {
            return 0;
        }

        long crtDate = new Date().getTime();
        long lDate = BmobDate.getTimeStamp(lhs.getCreatedAt());
        long rDate = BmobDate.getTimeStamp(rhs.getCreatedAt());

        long lDiffValue = crtDate - lDate;
        long rDiffValue = crtDate - rDate;

        double lPt = lIssue.getPersonTimes() == null ? 0 : lIssue.getPersonTimes();
        double rPt = rIssue.getPersonTimes() == null ? 0 : rIssue.getPersonTimes();

        double lPopu = lPt / lDiffValue;
        double rPopu = rPt / rDiffValue;

        if (lPopu > rPopu) {
            return -1;
        } else if (lPopu < rPopu) {
            return 1;
        } else {
            return 0;
        }
    }
}
