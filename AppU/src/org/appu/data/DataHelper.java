package org.appu.data;

import org.appu.common.utils.LogU;
import org.appu.security.Base64Helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Fynn on 2016/7/1.
 */
public class DataHelper {

    public static String objectToString(Serializable serializable) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] data = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(serializable);
            oos.flush();

            data = baos.toByteArray();
            return Base64Helper.encodeByte(data);

        } catch (Exception e) {
            LogU.e("objectToString异常", e);

        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                LogU.e(e);
            }
        }
        return "";
    }

    public static Serializable stringToObject(String s) {
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64Helper.decodeByte(s));
        ObjectInputStream ois = null;
        Serializable object = null;
        try {
            ois = new ObjectInputStream(bais);
            object = (Serializable) ois.readObject();
        } catch (Exception e) {
            LogU.e(e);
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (bais != null) {
                    bais.close();
                }
            } catch (IOException e) {
                LogU.e(e);
            }
        }
        return object;
    }
}
