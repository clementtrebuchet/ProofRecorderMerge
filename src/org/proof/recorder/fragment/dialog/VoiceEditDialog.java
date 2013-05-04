package org.proof.recorder.fragment.dialog;

import java.util.ArrayList;

import org.proof.recorder.R;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.fragment.voice.FragmentListVoiceTabs;
import org.proof.recorder.utils.StaticNotifications;
import org.proof.recorder.utils.Log.Console;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
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
	
	private Button mEdit, mLater;
	private TextView mTitleNote;
	private Bundle mBundle = null;
	private static Context mContext = null;
	
	private String mTitle;
	private long mVoiceId;

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
		
		mBundle = getIntent().getExtras();
		
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
			notifyUser();
		}

	};
	
	private final OnClickListener laterAction = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			saveVoiceTitleNote(false);
			notifyUser();
		}
	};
	
	private void notifyUser() {
		String title, info, text;
		Bundle extraNotification = new Bundle();
		Class<?> destination;
			
		title = getHoldContext().getString(R.string.app_name);
		
		destination = FragmentListVoiceTabs.class;
		
		extraNotification.putBoolean("isNotify", true);
		extraNotification.putLong("voiceId", mVoiceId);
		
		info = "";
		if(mTitle != DEFAULT_TITLE) {
			text = mTitle + " - " + getHoldContext().getString(R.string.notifyEndOfVoice);
			extraNotification.putBoolean("hasTitle", true);
		}
		else {
			text = getHoldContext().getString(R.string.notifyEndOfVoice);
			extraNotification.putBoolean("hasTitle", false);
		}		
		
		StaticNotifications.show(getHoldContext(), destination, extraNotification,
				title, info, text, StaticNotifications.ICONS.DEFAULT, true,
				true, 0);
	}
	
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
				save();				
			}
			
			else {
				mTitleNote.setText(getString(R.string.voice_edit_dlg_empty_title));
			}
		}
		else {
			save();
		}
	}
	
	private void save() {
		
		ArrayList<String> voice = null;
		ArrayList<String> voiceNote = null;		
		
		String noteText = getHoldContext().getString(R.string.default_note_text);
		
		if(mBundle != null) {			
			voice = mBundle.getStringArrayList("voice");
			voiceNote = mBundle.getStringArrayList("voiceNote");			
		}
		
		if(voice != null) {
			ContentValues values = new ContentValues();
			String creationTime = voice.get(4);
			
			Uri uriVoice = Uri.parse(voice.get(0));				
			
			values.put(ProofDataBase.COLUMN_VOICE_FILE, voice.get(1));
			values.put(ProofDataBase.COLUMN_VOICE_TIMESTAMP, voice.get(2));
			values.put(ProofDataBase.COLUMN_VOICE_TAILLE, voice.get(3));				
			values.put(ProofDataBase.COLUMN_VOICE_HTIME, creationTime);
			
			Uri rowId = getHoldContext().getContentResolver().insert(uriVoice, values);
			
			Console.print_exception(rowId.toString());
			
			if(voiceNote != null) {
				ContentValues voiceValues = new ContentValues();
				
				Uri uriVoiceNote = Uri.parse(voiceNote.get(0));
	
					mVoiceId = Long.parseLong(rowId.toString());
					voiceValues.put(ProofDataBase.COLUMNVOICE_ID_COLUMNVOICE_ID, mVoiceId);
					voiceValues.put(ProofDataBase.COLUMNVOICE_TITLE, mTitle);
					voiceValues.put(ProofDataBase.COLUMNVOICE_NOTE, noteText);
					voiceValues.put(ProofDataBase.COLUMNVOICE_DATE_CREATION, creationTime);
					
					getHoldContext().getContentResolver().insert(uriVoiceNote, voiceValues);
								
			}
		}
		
		onBackPressed();
	}
}
