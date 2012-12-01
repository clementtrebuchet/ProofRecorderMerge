package org.proof.recorder.utils;

import org.proof.recorder.R;

import android.content.Context;

public class DateUtils {
	
	private static Context mContext;

	private DateUtils() {}
	
	private static String getMonthFromInt(int m) {
		String month;
		switch (m) {
		case 1:
			month = mContext.getString(R.string.jan);
			break;
		case 2:
			month = mContext.getString(R.string.feb);
			break;
		case 3:
			month = mContext.getString(R.string.mar);
			break;
		case 4:
			month = mContext.getString(R.string.apr);
			break;
		case 5:
			month = mContext.getString(R.string.may);
			break;
		case 6:
			month = mContext.getString(R.string.jun);
			break;
		case 7:
			month = mContext.getString(R.string.jul);
			break;
		case 8:
			month = mContext.getString(R.string.aug);
			break;
		case 9:
			month = mContext.getString(R.string.sep);
			break;
		case 10:
			month = mContext.getString(R.string.oct);
			break;
		case 11:
			month = mContext.getString(R.string.nov);
			break;
		case 12:
			month = mContext.getString(R.string.dec);
			break;
		default:
			month = "" + m;
			break;
		}
		
		return month;
	}
	
	
	public static String reOrderDate(Context _context, String unOrdered) {
		mContext = _context;
		String ordered = "";
		String[] parts = unOrdered.split("-");
		ordered = parts[2] + " " + getMonthFromInt(Integer.parseInt(parts[1])) + " " + parts[0];
		return ordered;
	}
	
}
