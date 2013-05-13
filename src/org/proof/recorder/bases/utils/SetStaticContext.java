package org.proof.recorder.bases.utils;

import org.proof.recorder.Settings;
import org.proof.recorder.utils.AlertDialogHelper;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.Log.Console;

import android.content.Context;

public class SetStaticContext {
	
	private SetStaticContext() {
		
	}
	
	public static void setConsoleTagName(String mTag) {
		Console.setTagName(mTag);
	}
	
	private static void setUiStaticsContext(Context context) {
		AlertDialogHelper.setContext(context);
		QuickActionDlg.setmContext(context);
	}
	
	public static void setStaticsContext(Context context, int type) {
		Settings.setSettingscontext(context);
		switch (type) {
		case 0:			
			break;

		default:
			setUiStaticsContext(context);
			break;
		}		
	}
}
