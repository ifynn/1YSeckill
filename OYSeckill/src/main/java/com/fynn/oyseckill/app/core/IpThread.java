package com.fynn.oyseckill.app.core;

import android.text.TextUtils;

import com.fynn.oyseckill.model.IpAddress;
import com.fynn.oyseckill.util.constants.SpKey;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.appu.data.Storage.Storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Fynn on 2016/8/12.
 */
public class IpThread extends Thread {

    private static final String URL = "http://pv.sohu.com/cityjson?ie=utf-8";
    private OnResultListener listener;

    public static synchronized String requestIp(String httpUrl) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
            }
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    public void run() {
        String ipRst = requestIp(URL);
        try {
            String ipJson = ipRst.substring(ipRst.indexOf("{"), ipRst.indexOf("}") + 1);
            Gson gs = new GsonBuilder()
                    .setLenient()
                    .disableHtmlEscaping()
                    .create();
            IpAddress ipa = gs.fromJson(ipJson, IpAddress.class);
            String ip = ipa.getCip();

            if (listener != null) {
                listener.onResult(ip);
            }

            if (!TextUtils.isEmpty(ip)) {
                Storage.put(SpKey.IP, ip);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(OnResultListener listener) {
        this.listener = listener;
        super.start();
    }

    interface OnResultListener {
        void onResult(String ip);
    }
}
