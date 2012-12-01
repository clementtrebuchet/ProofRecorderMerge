package org.proof.recorder.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectivityInfo {

	private static final String TAG = "ConnectivityInfo";
	private static Context mContext;

	public static boolean WIFI;
	public static boolean TROISG;
	public static boolean NONETWORK;
	
	public ConnectivityInfo() {

	}

	public ConnectivityInfo(Context cont) {
		mContext = cont;
	}

	public static void informationConnectivity(Context cont) {
		mContext = cont;
		final ConnectivityManager connMgr = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		final android.net.NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		final android.net.NetworkInfo mobile = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (wifi.isAvailable()) {
			final NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
			if (activeNetwork != null
					&& activeNetwork.getState() == NetworkInfo.State.CONNECTED) {
				// notify user you are online
				WIFI = true;
				TROISG = false;
				NONETWORK = false;
				Log.e(TAG, "CONNECTIVITE WIFI");
			} else {
				WIFI = false;
				TROISG = false;
				NONETWORK = true;
				Log.e(TAG, "PAS DE CONNECTIVITE!!!");
			}
		} else if (mobile.isAvailable()) {
			
			final NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
			if (activeNetwork != null
					&& activeNetwork.getState() == NetworkInfo.State.CONNECTED) {
				// notify user you are online
				WIFI = false;
				TROISG = true;
				NONETWORK = false;
				Log.e(TAG, "CONNECTIVITE TROISG");
			} else {
				WIFI = false;
				TROISG = false;
				NONETWORK = true;
				Log.e(TAG, "PAS DE CONNECTIVITE!!!");
			}
		} else {
			WIFI = false;
			TROISG = false;
			NONETWORK = true;
			Log.e(TAG, "<<<<PAS DE RESEAU POSSIBLE PAS DE DEVICE PAS DE CONNECTIVITE>>>>");
		}

	}

	public char getNetworkState(Context mContext) {
		informationConnectivity(mContext);
		if(WIFI) return 'w';
		else if(TROISG) return 'g';
		else return 'r';
	}
}
