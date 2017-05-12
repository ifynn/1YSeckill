package org.appu.common.utils;

import android.util.Log;

import org.appu.AppU;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Fynn on 2016/3/28.
 */
public final class LogU {
    /**
     * Use LogU.v.
     */
    private static final int VERBOSE = 2;

    /**
     * Use LogU.d.
     */
    private static final int DEBUG = 3;

    /**
     * Use LogU.i.
     */
    private static final int INFO = 4;

    /**
     * Use LogU.w.
     */
    private static final int WARN = 5;

    /**
     * Use LogU.e.
     */
    private static final int ERROR = 6;

    /**
     * Use LogU.mark.
     */
    private static final int MARK = 7;

    public static void v(Object... object) {
        print(VERBOSE, object);
    }

    public static void d(Object... object) {
        print(DEBUG, object);
    }

    public static void w(Object... object) {
        print(WARN, object);
    }

    public static void i(Object... object) {
        print(INFO, object);
    }

    public static void e(Object... object) {
        print(ERROR, object);
    }

    public static void mark(Object... object) {
        print(MARK, object);
    }

    private static String generateTag() {
        StackTraceElement stElement = new Throwable().getStackTrace()[3];
        String tag = "%s.%s(Line:%d)";
        String className = stElement.getClassName();
        className = className.substring(className.lastIndexOf(".") + 1);
        tag = String.format(tag, className, stElement.getMethodName(), stElement.getLineNumber());
        return tag;
    }

    private static void print(int priority, Object... msg) {
        if (!AppU.isDebug()) {
            return;
        }

        StringBuilder builder = new StringBuilder();

        if (msg == null) {
            builder.append("null");
        } else {
            for (Object o : msg) {
                if (o instanceof Throwable) {
                    builder.append(Log.getStackTraceString((Throwable) o)).append("\n");
                } else {
                    if (o instanceof Collection) {
                        Iterator i = ((Collection) o).iterator();
                        while (i.hasNext()) {
                            Object obj = i.next();
                            builder.append(obj).append("\n");
                        }

                    } else {
                        builder.append(o).append("\n");
                    }
                }
            }
        }

        switch (priority) {
            case VERBOSE:
                Log.v(generateTag(), builder.toString());
                break;

            case DEBUG:
                Log.d(generateTag(), builder.toString());
                break;

            case INFO:
                Log.i(generateTag(), builder.toString());
                break;

            case WARN:
                Log.w(generateTag(), builder.toString());
                break;

            case ERROR:
                Log.e(generateTag(), builder.toString());
                break;

            case MARK:
                LogU.e(generateTag(), builder.append("↑↑↑测试代码，上线前需要删除！"));
                break;

            default:
                break;
        }
    }
}
