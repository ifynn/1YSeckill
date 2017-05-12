package org.appu.data;

import java.io.Serializable;

/**
 * Created by Fynn on 2016/7/1.
 */
public interface DataSource {

    void add(String key, Object value);

    void remove(String key);

    void removeAll();

    Object get(String key, Object defValue);

    Serializable getSerializable(String key, Serializable defValue);
}
