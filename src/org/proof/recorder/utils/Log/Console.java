package org.proof.recorder.utils.Log;

import android.util.Log;

public class Console {
	
	public static String tagName = null;

	/**
	 * @return the tagName
	 */
	public static String getTagName() {
		return tagName;
	}

	/**
	 * @param tagName the tagName to set
	 */
	public static void setTagName(String tagName) {
		Console.tagName = tagName;
	}
	
	private static void initPrinting() {
		if(getTagName() == null) {
			setTagName(Console.class.getSimpleName());
		}
	}
	
	private static void print(char type, Object message) {
		initPrinting();
		switch (type) {
			case 'e':
				Log.e(getTagName(), "" + message);
				break;
				
			case 'v':
				Log.v(getTagName(), "" + message);
				break;
				
			case 'd':
				Log.d(getTagName(), "" + message);
				break;
	
			default:
				Log.d(getTagName(), "" + message);
				break;
		}
	}
	
	public static void print(Object message) {
		print('v', message);
	}
	
	public static void print_debug(Object message) {
		print('d', message);
	}
	
	public static void print_exception(Object message) {
		print('e', message);
	}
}