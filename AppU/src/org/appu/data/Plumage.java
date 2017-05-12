package org.appu.data;

import android.content.SharedPreferences;

import org.appu.security.Base64Helper;

import java.io.Serializable;

/**
 * Created by Fynn on 2016/7/1.
 */
public abstract class Plumage implements DataSource {

    @Override
    public void add(String key, Object value) {
        SharedPreferences sp = getSharedPreferences();
        String encodeKey = Base64Helper.encode(key);
        if (value == null) {
            sp.edit().remove(encodeKey).commit();
        }

        if (value instanceof String) {
            String encodeValue = Base64Helper.encode((String) value);
            sp.edit().putString(encodeKey, encodeValue).commit();

        } else if (value instanceof Boolean) {
            sp.edit().putBoolean(encodeKey, (Boolean) value).commit();

        } else if (value instanceof Float) {
            sp.edit().putFloat(encodeKey, (Float) value).commit();

        } else if (value instanceof Integer) {
            sp.edit().putInt(encodeKey, (Integer) value).commit();

        } else if (value instanceof Long) {
            sp.edit().putLong(encodeKey, (Long) value).commit();

        } else if (value instanceof Serializable) {
            String data = DataHelper.objectToString((Serializable) value);
            sp.edit().putString(encodeKey, data).commit();
        }

    }

    @Override
    public void remove(String key) {
        String encodeKey = Base64Helper.encode(key);
        getSharedPreferences().edit().remove(encodeKey).commit();
    }

    @Override
    public void removeAll() {
        getSharedPreferences().edit().clear().commit();
    }

    @Override
    public Object get(String key, Object defValue) {
        String encodeKey = Base64Helper.encode(key);
        Object obj = getSharedPreferences().getAll().get(encodeKey);
        if (obj == null) {
            obj = defValue;
            if (defValue instanceof String) {
                obj = Base64Helper.encode((String) defValue);
            }
        }
        return obj;
    }

    @Override
    public Serializable getSerializable(String key, Serializable defValue) {
        Object obj = getSharedPreferences().getAll().get(key);
        if (obj == null || !(obj instanceof String)) {
            return null;
        }
        return DataHelper.stringToObject((String) obj);
    }

    public abstract SharedPreferences getSharedPreferences();
}
