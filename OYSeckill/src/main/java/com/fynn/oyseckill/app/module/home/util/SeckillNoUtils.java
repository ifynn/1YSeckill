package com.fynn.oyseckill.app.module.home.util;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by Fynn on 2016/8/9.
 */
public class SeckillNoUtils {

    public static final int BASE_NUM = 1000000;

    public static HashSet<Long> genDiffRandom(List<Long> nos, int maxValue, int count) {
        HashSet<Long> all = new HashSet<Long>();
        all.addAll(nos);
        int rc = 0;
        int rest = maxValue - nos.size();
        int c = count <= rest ? count : rest;
        HashSet<Long> rst = new HashSet<Long>();
        Random random = new Random();

        while (rc < c) {
            int r = random.nextInt(maxValue) + 1;
            long l = r + BASE_NUM;
            if (!all.contains(l)) {
                all.add(l);
                rst.add(l);
                rc++;
            }
        }
        return rst;
    }
}
