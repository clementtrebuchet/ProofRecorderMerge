package org.proof.recorder.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;

import android.util.Log;

public class SecCom {

	private static boolean ROOTED = true;
	private static boolean UNROOTED = false;
	public static String TAG = "SecCom";

	public static enum isRooted {

		Device1, Device2, Device3;
		private boolean value;

		public boolean getValueMethod(int method) {
			switch (method) {
			case 1:
				checkRooted1();
				Log.e(TAG, "La device est elle root ? methode 1 : "
						+ this.value);
				break;
			case 2:
				checkRooted2();
				Log.e(TAG, "La device est elle root ? methode 2 : "
						+ this.value);
				break;
			case 3:
				checkRooted3();
				Log.e(TAG, "La device est elle root ? methode 3 : "
						+ this.value);
				break;
			}

			return this.value;

		}

		public void setValue(boolean value) {
			this.value = value;
		}
	}

	private static void checkRooted1() {

		String buildTag = android.os.Build.TAGS;
		Log.e(TAG, "checkRooted1 : " + buildTag);
		if (buildTag != null && buildTag.contains("test-keys")) {
			SecCom.isRooted.Device1.setValue(ROOTED);

		}
		SecCom.isRooted.Device1.setValue(UNROOTED);
	}

	private static void checkRooted2() {

		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec(
					"su -c /system/bin/ls root");
			InputStream stream = process.getInputStream();
			Log.i(TAG, streamToString(stream));
			process.waitFor();
			if (process.exitValue() != 0) {

				SecCom.isRooted.Device2.setValue(UNROOTED);

			}
			SecCom.isRooted.Device2.setValue(ROOTED);
		} catch (Exception e) {
			Log.e(TAG, "checkRooted2 : " + e);
			SecCom.isRooted.Device2.setValue(UNROOTED);
		} finally {
			if (os != null) {
				try {
					os.close();
					process.destroy();
				} catch (Exception e) {

				}
			}
		}

	}

	static String streamToString(InputStream is) {
		try {
			return new java.util.Scanner(is).useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}

	private static void checkRooted3() {
		try {
			File file = new File("/system/app/Superuser.apk");
			
			if (file.exists()) {
				SecCom.isRooted.Device3.setValue(ROOTED);
				Log.e(TAG, "checkRooted3: " + file.exists());
			} else {
				Log.e(TAG, "checkRooted3: " + file.exists());
				SecCom.isRooted.Device3.setValue(UNROOTED);
			}
		} catch (Exception e) {

		}

	}

	public static boolean unionRooted() {
		if (SecCom.isRooted.Device1.getValueMethod(1) == ROOTED
			|| SecCom.isRooted.Device2.getValueMethod(2) == ROOTED
			|| SecCom.isRooted.Device3.getValueMethod(3) == ROOTED) {
			return ROOTED;
		} else {
			return UNROOTED;
		}

	}
}
