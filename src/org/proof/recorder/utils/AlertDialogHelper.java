package org.proof.recorder.utils;

import org.proof.recorder.R;
import org.proof.recorder.fragment.dialog.NoneRecordsDialog;
import org.proof.recorder.fragment.dialog.VoiceEditDialog;
import org.proof.recorder.utils.Log.Console;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlertDialogHelper {
	
	private static AlertDialog.Builder mDialog = null;
	private static ProgressDialog progressDialog = null;
	
	private static Context mContext = null;
	
	public static void openVoiceEditDialog() {		
		Intent mIntent = new Intent(getContext(), VoiceEditDialog.class);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getContext().startActivity(mIntent);
	}
	
	public static void openVoiceEditDialog(Bundle bundle) {		
		Intent mIntent = new Intent(getContext(), VoiceEditDialog.class);
		if(bundle != null) {
			mIntent.putExtras(bundle);
		}
		getContext().startActivity(mIntent);
	}

	public static void openNoneRecordsDialog() {
		Intent mIntent = new Intent(getContext(), NoneRecordsDialog.class);
		getContext().startActivity(mIntent);
	}
	
	public static void openSimpleNoMatchDialog() {
		
		mDialog = new AlertDialog.Builder(getContext());
		mDialog.setTitle(getContext().getString(R.string.gplay_title));

		mDialog.setMessage(getContext().getString(R.string.gplay_no_match));

		mDialog.setPositiveButton(
				getContext().getString(R.string.ok),
				null);
		

		mDialog.show();
	}
	
	
	public static void closeProgressDialog() {

		if(progressDialog != null) {
			progressDialog.dismiss();
			progressDialog.cancel();
			progressDialog = null;
		}
	}
	
	public static void openProgressDialog(Object stringId) {
		
		//closeProgressDialog();
		
		progressDialog = new ProgressDialog(getContext());
		
		try {
			progressDialog.setMessage(getContext().getText(
					stringId  != null ? (Integer)stringId : R.string.loading));
			
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}
		catch (Exception e) {
			Console.print_exception(e);
		}		
	}

	public static void hideProgressDialog() {
		if(progressDialog != null)
			progressDialog.hide();
	}

	/**
	 * @return the mContext
	 */
	private static Context getContext() {
		return mContext;
	}
	
	public static boolean hasContext() {
		return AlertDialogHelper.mContext != null;
	}

	/**
	 * @param mContext the mContext to set
	 */
	public static void setContext(Context mContext) {
		AlertDialogHelper.mContext = mContext;
	}
}
