package org.appu.data.Storage;

import android.support.annotation.Nullable;

import org.appu.data.DataSource;
import org.appu.security.Base64Helper;

import java.io.Serializable;

/**
 * Created by Fynn on 2016/7/1.
 */
public final class Storage {

    private static Object object = new Object();
    private static Cookie cookie;
    private static Session session;

    /**
     * Get DataSource by StorageType.
     *
     * @param type StorageType of preference.
     * @return Cookie or Session.
     */
    private static DataSource getDataSource(StorageType type) {
        if (type == StorageType.TYPE_SP_COOKIE) {
            return cookie = Cookie.get();
        } else if (type == StorageType.TYPE_SP_SESSION) {
            return session = Session.get();
        }
        return null;
    }

    /**
     * Set an int value in the preferences editor, to be written back once.
     * Clear app data after upgrading if StorageType is StorageType.TYPE_SP_SESSION.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @param type  StorageType of preference.
     */
    public static void put(String key, Object value, StorageType type) {
        getDataSource(type).add(key, value);
    }

    /**
     * Set an int value in the preferences editor, to be written back once.
     * Do not clear app data after upgrading.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     */
    public static void put(String key, Object value) {
        put(key, value, false);
    }

    /**
     * Set an int value in the preferences editor, to be written back once
     *
     * @param key                    The name of the preference to modify.
     * @param value                  The new value for the preference.
     * @param isClearAfterAppUpgrade If clear app data after upgrading.
     */
    public static void put(String key, Object value, boolean isClearAfterAppUpgrade) {
        if (isClearAfterAppUpgrade) {
            put(key, value, StorageType.TYPE_SP_SESSION);
        } else {
            put(key, value, StorageType.TYPE_SP_COOKIE);
        }
    }

    /**
     * Retrieve a boolean value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a boolean.
     * @throws ClassCastException
     */
    public static boolean getBoolean(String key, boolean defValue) {
        return (boolean) get(key, defValue, StorageType.TYPE_SP_COOKIE);
    }

    /**
     * Retrieve a float value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a float.
     * @throws ClassCastException
     */
    public static float getFloat(String key, float defValue) {
        return (float) get(key, defValue, StorageType.TYPE_SP_COOKIE);
    }

    /**
     * Retrieve a long value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a long.
     * @throws ClassCastException
     */
    public static long getLong(String key, long defValue) {
        return (long) get(key, defValue, StorageType.TYPE_SP_COOKIE);
    }

    /**
     * Retrieve an int value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * an int.
     * @throws ClassCastException
     */
    public static int getInt(String key, int defValue) {
        return (int) get(key, defValue, StorageType.TYPE_SP_COOKIE);
    }

    /**
     * Retrieve a String value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a String.
     * @throws ClassCastException
     */
    public static String getString(String key, @Nullable String defValue) {
        String encodeValue = (String) get(key, defValue, StorageType.TYPE_SP_COOKIE);
        return Base64Helper.decode(encodeValue);
    }

    /**
     * Retrieve a Serializable value from the preferences.
     *
     * @param key          The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return
     */
    public static Serializable getSerializable(String key, @Nullable Serializable defValue) {
        Serializable value = getDataSource(StorageType.TYPE_SP_COOKIE).getSerializable(key, defValue);
        if (value == null) {
            value = defValue;
        }
        return value;
    }

    /**
     * Retrieve a Object value from the preferences, StorageType.TYPE_SP_COOKIE
     * or StorageType.TYPE_SP_SESSION.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @param type     StorageType of preference.
     * @return Returns the preference value if it exists, or defValue.
     */
    public static Object get(String key, Object defValue, StorageType type) {
        Object sp = getDataSource(type).get(key, defValue);
        return sp;
    }

    /**
     * Retrieve a Object value from the preferences, StorageType.TYPE_SP_COOKIE
     * or StorageType.TYPE_SP_SESSION.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     */
    public static Object get(String key, Object defValue) {
        Object spCookie = getDataSource(StorageType.TYPE_SP_COOKIE).get(key, null);
        Object spSession = getDataSource(StorageType.TYPE_SP_SESSION).get(key, null);
        if (spCookie != null) {
            return spCookie;
        } else if (spSession != null) {
            return spSession;
        } else {
            return defValue;
        }
    }

    /**
     * Remove the data named key from Cookie.
     *
     * @param key The name of the preference to remove.
     */
    public static void remove(String key) {
        getDataSource(StorageType.TYPE_SP_COOKIE).remove(key);
    }

    /**
     * Remove the data named key from type.
     *
     * @param key  The name of the preference to remove.
     * @param type StorageType of preference.
     */
    public static void remove(String key, StorageType type) {
        getDataSource(type).remove(key);
    }

    /**
     * Remove all from Session.
     */
    public static void removeSession() {
        getDataSource(StorageType.TYPE_SP_SESSION).removeAll();
    }
}