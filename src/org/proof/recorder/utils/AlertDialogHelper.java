package org.proof.recorder.utils;

import org.proof.recorder.R;
import org.proof.recorder.fragment.dialog.NoneRecordsDialog;
import org.proof.recorder.fragment.dialog.VoiceEditDialog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlertDialogHelper {

	protected static final String TAG = "AlertDialogHelper";
	
	private static AlertDialog.Builder mDialog = null;
	private static ProgressDialog progressDialog = null;
	
	private static Context mContext = null;
	
	public static void openVoiceEditDialog() {		
		Intent mIntent = new Intent(mContext, VoiceEditDialog.class);
		mContext.startActivity(mIntent);
	}
	
	public static void openVoiceEditDialog(Bundle bundle) {		
		Intent mIntent = new Intent(mContext, VoiceEditDialog.class);
		if(bundle != null) {
			mIntent.putExtras(bundle);
		}
		mContext.startActivity(mIntent);
	}

	public static void openNoneRecordsDialog() {
		Intent mIntent = new Intent(mContext, NoneRecordsDialog.class);
		mContext.startActivity(mIntent);
	}
	
	public static void openSimpleNoMatchDialog() {
		
		mDialog = new AlertDialog.Builder(getContext());
		mDialog.setTitle(mContext.getString(R.string.gplay_title));

		mDialog.setMessage(mContext.getString(R.string.gplay_no_match));

		mDialog.setPositiveButton(
				mContext.getString(R.string.ok),
				null);

		mDialog.show();
	}
	
	
	public static void closeProgressDialog() {

		if(progressDialog != null) {
			progressDialog.dismiss();
			progressDialog.cancel();
		}
	}
	
	public static void openProgressDialog() {
		
		progressDialog = new ProgressDialog(getContext());
		progressDialog.setMessage(getContext().getText(R.string.loading));
		progressDialog.setIndeterminate(true);

		progressDialog.show();
	}

	public static void hideProgressDialog() {
		if(progressDialog != null)
			progressDialog.hide();
	}

	/**
	 * @return the mContext
	 */
	public static Context getContext() {
		return mContext;
	}

	/**
	 * @param mContext the mContext to set
	 */
	public static void setContext(Context mContext) {
		AlertDialogHelper.mContext = mContext;
	}
}
