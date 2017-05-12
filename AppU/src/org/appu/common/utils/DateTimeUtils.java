package org.appu.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Fynn on 2015/11/11.
 */
public class DateTimeUtils {

    public static Date parseDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date parseDate = null;
        try {
            parseDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parseDate;
    }

    public static Date parseDate(String date) {
        return parseDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String formatDate(long milliseconds, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(milliseconds));
    }

    public static String formatDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static Date addDate(Date date, int field, int value) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(field, value);
        return c.getTime();
    }

    public static int getNowMillis() {
        Calendar c = Calendar.getInstance();
        int mill = c.get(Calendar.MILLISECOND);
        return mill;
    }

    /**
     * 将日期毫秒数的时、分、秒、毫秒转化为数字
     *
     * @param millis
     * @return
     */
    public static long formatDateToLong(long millis) {
        String fd = formatDate(millis, "HHmmssSSS");
        long longD = 0;
        try {
            longD = Long.valueOf(fd);
        } catch (Exception e) {}

        return longD;
    }
}
