package org.appu.common.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;

import org.appu.AppU;

import java.util.regex.Pattern;

public class TextUtils {

    public static final String REGEX_CONTAINS_BLANK = "\\s+";  //字符包含空格
    public static final String REGEX_ONLY_LETTER = "^[a-zA-Z]+$";  //字符是纯英文字母
    public static final String REGEX_ONLY_SPECIAL_CHAR = "^[^0-9A-Za-z]+$";  //非数字和字母（即全是特殊字符和中文）
    public static final String REGEX_CONTAINS_NUMERIC = "\\d+"; //字符包含数字
    public static final String REGEX_CLN = "^[\u4e00-\u9fa5\\w]{4,20}$";  //中文、大小写英文、数字

    /**
     * 字符串是否为空
     *
     * @param text
     * @return
     */
    public static boolean isEmpty(CharSequence text) {
        if (text == null || text.length() <= 0) {
            return true;
        }
        return false;
    }

    public static boolean isContainBlank(String text) {
        if (text == null || text.length() <= 0) {
            return true;

        } else {
            for (char c : text.toCharArray()) {
                if (!Character.isSpaceChar(c)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean isContainBlank(CharSequence chars) {
        boolean isContainBlank = Pattern.compile(REGEX_CONTAINS_BLANK).matcher(chars).find();
        return isContainBlank;
    }

    public static boolean isMobile(String str) {
        String regex = "^[1][3,4,5,7,8][0-9]{9}$";
        return Pattern.compile(regex).matcher(str).matches();
    }

    public static boolean isDigitsOnly(CharSequence str) {
        return android.text.TextUtils.isDigitsOnly(str);
    }

    public static boolean isLetterOnly(String chars) {
        boolean isOnlyLetter = Pattern.matches(REGEX_ONLY_LETTER, chars);
        return isOnlyLetter;
    }

    public static boolean isSpecialCharsOnly(String chars) {
        boolean isOnlySpecialChar = Pattern.matches(REGEX_ONLY_SPECIAL_CHAR, chars);
        return isOnlySpecialChar;
    }

    public static boolean isContainNumeric(String chars) {
        boolean isContainNumeric = Pattern.compile(REGEX_CONTAINS_NUMERIC).matcher(chars).find();
        return isContainNumeric;
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static boolean isContainChinese(String chars) {
        char[] ch = chars.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isChineseLetterNumeric(String chars) {
        boolean isChineseLetterNumeric = Pattern.matches(REGEX_CLN, chars);
        return isChineseLetterNumeric;
    }

    /**
     * 复制到剪切板
     * @param text
     */
    public static void copyText(CharSequence text) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager)
                    AppU.app().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(text);

        } else {
            ClipboardManager clip = (ClipboardManager) AppU.app()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData cd = ClipData.newPlainText("wechat", "oyseckill");
            clip.setPrimaryClip(cd);
        }
    }
}
