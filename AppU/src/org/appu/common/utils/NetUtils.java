package org.appu.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.appu.AppU;

public class NetUtils {

	public static boolean isNetworkAvailable() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) AppU.app()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
		if (mNetworkInfo != null) { 
			return mNetworkInfo.isAvailable(); 
		} 
		return false; 
	}

	public static int getNetType() {
		ConnectivityManager connectMgr = (ConnectivityManager) AppU.app()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectMgr.getActiveNetworkInfo();

		int type = -1;
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				type = info.getSubtype();
			} else {
				type = info.getType();
			}
		}

		return type;
	}

	public static String getNetTypeName() {
		ConnectivityManager connectMgr = (ConnectivityManager) AppU.app()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectMgr.getActiveNetworkInfo();

		String type = "null";
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				type = info.getSubtypeName();
			} else {
				type = info.getTypeName();
			}
		}

		return type;
	}
}
