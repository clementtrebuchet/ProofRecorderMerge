package org.proof.recorder.utils;

import org.proof.recorder.fragment.dialog.NoneRecordsDialog;
import org.proof.recorder.fragment.dialog.VoiceEditDialog;

import android.content.Context;
import android.content.Intent;

public class AlertDialogHelper {

	protected static final String TAG = "AlertDialogHelper";
	
	public static void openVoiceEditDialog(Context _mContext) {		
		Intent mIntent = new Intent(_mContext, VoiceEditDialog.class);
		_mContext.startActivity(mIntent);
	}

	public static void openNoneRecordsDialog(Context _context) {
		Intent mIntent = new Intent(_context, NoneRecordsDialog.class);
		_context.startActivity(mIntent);
	}
}
