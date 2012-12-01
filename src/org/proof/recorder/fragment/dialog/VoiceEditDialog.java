package org.proof.recorder.fragment.dialog;

import org.proof.recorder.R;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;

import android.content.ContentValues;
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
	
	private Button mEdit, mLater;
	private TextView mTitleNote;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voice_edit_dialog);
		
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
			saveVoiceTitleNote();			
		}

	};
	
	private final OnClickListener laterAction = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			onBackPressed();
		}
	};
	
	private void saveVoiceTitleNote() {
		
		String mTitle = mTitleNote.getText().toString();
		
		if (!mTitle.equals("") && 
				!mTitle.equals(getString(R.string.voice_edit_dlg_empty_title)) &&
				!mTitle.equals(getString(R.string.voice_edit_dialog_default_title))) {

			Uri uriNotes = Uri
					.withAppendedPath(
							PersonnalProofContentProvider.CONTENT_URI,
							"vnotes");
			String Nomdelabase = ProofDataBase.TABLE_VOICES;
			ContentValues valuesNote = new ContentValues();
			int lastId = PersonnalProofContentProvider
					.lastInsertId(Nomdelabase);

			valuesNote.put(ProofDataBase.COLUMNVOICE_TITLE,
					mTitle);

			getContentResolver().update(uriNotes, valuesNote,
					" recId=?", new String[] { lastId + "" });
			
			onBackPressed();
		}
		
		else {
			mTitleNote.setText(getString(R.string.voice_edit_dlg_empty_title));
		}
	}
	
}
