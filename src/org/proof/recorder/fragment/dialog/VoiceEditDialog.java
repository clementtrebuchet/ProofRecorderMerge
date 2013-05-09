package org.proof.recorder.fragment.dialog;

import org.proof.recorder.R;
import org.proof.recorder.receivers.AudioRecorderReceiver;
import org.proof.recorder.service.DataPersistanceManager;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class VoiceEditDialog extends SherlockFragmentActivity {
	
	private static String DEFAULT_TITLE;
	private static Context mContext = null;
	private static boolean isSaved = false;
	
	private Button mEdit, mLater;
	private TextView mTitleNote;
	
	private String mTitle;
	
	private DataPersistanceManager dpm = null;

	/**
	 * @return the mContext
	 */
	public static Context getHoldContext() {
		return mContext;
	}

	/**
	 * @param mContext the mContext to set
	 */
	public static void setHoldContext(Context mContext) {
		VoiceEditDialog.mContext = mContext;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voice_edit_dialog);
		
		setHoldContext(this);
		
		DEFAULT_TITLE = getHoldContext().getString(
				R.string.default_note_title);
		
		mTitle = DEFAULT_TITLE;
		
		mTitleNote = (TextView) findViewById(R.id.mEdit);
		
		mEdit = (Button) findViewById(R.id.mBtnEdit);
		mLater = (Button) findViewById(R.id.mBtnLater);
		
		mEdit.setOnClickListener(editVoiceAction);
		mLater.setOnClickListener(laterAction);
		
		mTitleNote.setOnTouchListener(mEditTxtAction);
	}
	
	private final OnTouchListener mEditTxtAction = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			mTitleNote.setText("");
			return false;
		}
	};	
	
	private final OnClickListener editVoiceAction = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			saveVoiceTitleNote(true);
		}

	};
	
	private final OnClickListener laterAction = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			saveVoiceTitleNote(false);			
		}
	};
	
	private void saveVoiceTitleNote(boolean edited) {
		
		if(edited) {
			mTitle = mTitleNote.getText().toString();
			
			if (!mTitle.equals("") && 
					!mTitle.equals(
							getString(
									R.string.voice_edit_dlg_empty_title)) &&
					!mTitle.equals(
							getString(
									R.string.default_note_title))) {			
				notifyAndSave();
			}
			
			else {
				mTitleNote.setText(getString(R.string.voice_edit_dlg_empty_title));
			}
		}
		else {
			notifyAndSave();
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if(!isSaved) {
			notifyAndSave();
		}
		isSaved = false;
	}
	
	private void notifyAndSave() {
		
		dpm = new DataPersistanceManager();		
    	
    	if(!dpm.isProcessing()) {
    		
    		Intent audioService = new Intent(this, AudioRecorderReceiver.class);	    	
	    	
	    	Bundle extras = new Bundle();
	    	
	    	extras.putString("audioTitle", mTitle);
	    	
	    	audioService.setAction("android.intent.action.SAVE_AUDIO_RECORDER");
	    	
	    	audioService.putExtras(extras);
	    	
	    	sendBroadcast(audioService);
	    	isSaved = true;
    	}
    	
		onBackPressed();
	}
}
