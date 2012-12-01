package org.proof.recorder.fragment.voice.notes;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class FragmentVoiceNotesEditor extends Fragment {
	
	private Bundle b;
	private static final String TAG = "FragmentVoiceNotesEditor";
	Button mButtonEnregistre;
	private String idNote;
	EditText mTitre; 
	EditText mNote; 

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		b = getActivity().getIntent().getExtras();
		idNote = (String) b.get("id");
		
		if(Settings.isDebug())
			Log.e(TAG, idNote);
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//
	

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fragment_note_editor_detail, container, false);
		//mButtonEnregistre = (Button) view.findViewById(R.id.enregistreNoteButton);
		mTitre = (EditText) view.findViewById(R.id.titreNoteDetail);
		mNote = (EditText) view.findViewById(R.id.contenuNote);
		
		Uri uri = Uri.withAppendedPath(PersonnalProofContentProvider.CONTENT_URI,
				"vnote_id/"+idNote);
		Cursor c = getActivity().getContentResolver().query(uri, null, null,null, null);
		while (c.moveToNext()) {
		mTitre.setText(c.getString(c.getColumnIndex(ProofDataBase.COLUMNVOICE_TITLE)));
		mNote.setText(c.getString(c.getColumnIndex(ProofDataBase.COLUMNVOICE_NOTE)));
		
		if(Settings.isDebug())
			Log.v(TAG, "PASS HERE"+c.getString(c.getColumnIndex(ProofDataBase.COLUMNVOICE_TITLE)));
		}
		c.close();
		
		if(Settings.isDebug())
			Log.v(TAG, "PASS HERE");
		//mButtonEnregistre.setOnClickListener(EnregistreNote);
		return view;
	}

	
	
	/*private OnClickListener EnregistreNote = new OnClickListener() {
		public void onClick(View v) {	
			ContentValues valuesNote = new ContentValues();
			valuesNote.put(ProofDataBase.COLUMN_TITLE, mTitre.getText().toString());
			valuesNote.put(ProofDataBase.COLUMN_NOTE, mNote.getText().toString());
			Uri uri = Uri.withAppendedPath(PersonnalProofContentProvider.CONTENT_URI,
					"note_id/"+idNote);
			getActivity().getContentResolver().insert(uri,
					valuesNote);
			Log.v(TAG, "Note :" + mTitre.getText().toString());
			Log.v(TAG, "Note :" + mNote.getText().toString());
			

		}
	};*/



}
