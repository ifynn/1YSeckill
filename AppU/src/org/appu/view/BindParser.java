package org.appu.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.appu.view.annotation.BindView;
import org.appu.view.annotation.ContentView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by Fynn on 2016/3/24.
 */
public class BindParser implements ViewBinder {

    @Override
    public void bind(Activity activity) {
        bindContentView(activity);
        bindObject(activity, new ViewFinder(activity));
    }

    @Override
    public void bind(Object object, View view) {
        bindObject(object, new ViewFinder(view));
    }

    @Override
    public void bind(View view) {
        bindObject(view, new ViewFinder(view));
    }

    @Override
    public View bind(Object fragment, LayoutInflater inflater, ViewGroup container) {
        View view = null;
        Class cls = fragment.getClass();
        try {
            ContentView cv = findContentView(cls);
            if (cv != null) {
                int viewId = cv.value();
                if (viewId > 0) {
                    view = inflater.inflate(viewId, container, false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        bindObject(fragment, new ViewFinder(view));

        return view;
    }

    private ContentView findContentView(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        ContentView cv = cls.getAnnotation(ContentView.class);
        if (cv == null) {
            return findContentView(cls.getSuperclass());
        }
        return cv;
    }

    private void bindContentView(Activity activity) {
        Class<?> cls = activity.getClass();
        ContentView cv = findContentView(cls);
        if (cv != null) {
            int viewId = cv.value();
            if (viewId > 0) {
                try {
                    Method setContentView = cls.getMethod("setContentView", int.class);
                    setContentView.invoke(activity, viewId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void bindObject(Object object, ViewFinder viewFinder) {
        if (object == null) {
            return;
        }

        Class cls = object.getClass();
        Field[] fields = cls.getDeclaredFields();
        if (fields == null || fields.length <= 0) {
            return;
        }

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) ||
                    Modifier.isFinal(field.getModifiers()) ||
                    field.getType().isPrimitive() ||
                    field.getType().isArray()) {
                continue;
            }

            boolean present = field.isAnnotationPresent(BindView.class);
            if (present) {
                BindView viewBind = field.getAnnotation(BindView.class);
                if (viewBind != null) {
                    View view = viewFinder.finViewById(viewBind.value());
                    if (view != null) {
                        try {
                            field.setAccessible(true);
                            field.set(object, view);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String name = BindView.class.getSimpleName();
                        throw new RuntimeException("Invalid @" + name + " for "
                                + cls.getSimpleName() + "." + field.getName() + " or setContentView() not called");
                    }
                }
            }
        }
    }
}
