package com.fynn.oyseckill.app.module.home.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fynn on 2016/8/8.
 */
public class Manager {
    private static List<String> objIds = new ArrayList<>();

    static {
        objIds.add("84ced49f3f");
        objIds.add("4f2d50ca24");
    }

    public static boolean contains(String objId) {
        return objIds.contains(objId);
    }
}
