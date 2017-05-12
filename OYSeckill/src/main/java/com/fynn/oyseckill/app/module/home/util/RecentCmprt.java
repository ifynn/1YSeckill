package com.fynn.oyseckill.app.module.home.util;

import com.fynn.oyseckill.model.entity.Issue;
import com.fynn.oyseckill.model.entity.Product;

import java.util.Date;

import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by Fynn on 2016/7/6.
 */
public class RecentCmprt extends ProductComparator {

    @Override
    public int compare(Product lhs, Product rhs) {
        Issue lIssue = lhs.getCurrentIssue();
        Issue rIssue = rhs.getCurrentIssue();
        if (lIssue == null || rIssue == null) {
            return 0;
        }

        String lbDate = lIssue.getCreatedAt();
        String rbDate = rIssue.getCreatedAt();

        Date lDate = new Date(BmobDate.getTimeStamp(lbDate));
        Date rDate = new Date(BmobDate.getTimeStamp(rbDate));

        if (lDate.before(rDate)) {
            return 1;
        } else if (lDate.after(rDate)) {
            return -1;
        } else {
            return 0;
        }
    }
}
