package org.appu.common.utils;

import java.text.NumberFormat;

/**
 * Created by Fynn on 2015/11/11.
 */
public class NumberUtils {

    public static String toDecimal(Object obj, int decimalCount) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMaximumFractionDigits(decimalCount);
        String decimalStr = nf.format(obj);
        return decimalStr;
    }
}
