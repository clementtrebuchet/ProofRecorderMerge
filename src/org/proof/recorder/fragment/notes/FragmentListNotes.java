package org.proof.recorder.fragment.notes;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.proof.recorder.R;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.Log.Console;

import java.util.Date;

@SuppressWarnings("unused")
public class FragmentListNotes extends Fragment {

	private static Bundle mBundle;

	private static final String[] from = new String[] {
			ProofDataBase.COLUMN_TITLE, 
			ProofDataBase.COLUMN_NOTE,
			ProofDataBase.COLUMN_DATE_LAST_MODIF,
			ProofDataBase.COLUMNNOTES_ID 
	};

	private EditText mTitre;
	private EditText mNote;
	private String idNote;
	int i;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mBundle = getActivity().getIntent().getExtras();
		Console.setTagName(this.getClass().getSimpleName());
	}

	@SuppressWarnings("UnusedAssignment")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Console.print_debug("PASS HERE");

		setHasOptionsMenu(true);
		setMenuVisibility(true);

		registerForContextMenu(getView());
		mTitre = (EditText) getView().findViewById(R.id.titreNotes);
		mNote = (EditText) getView().findViewById(R.id.notesContenu);
		TextView mCreation = (TextView) getView().findViewById(R.id.notesCreation);
		String id = (String) mBundle.get("id");
		
		Console.print_debug("<<PHONE's NOTE NUMERO>>" + id);
		
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "note_recordid/"
						+ id);
		
		Console.print_debug(uri.toString());		
		
		Cursor cursor = null;
		
		try {
			cursor = getActivity().getContentResolver().query(uri, from, null, null, null);
			while (cursor.moveToNext()){
				idNote = cursor.getString(cursor.getColumnIndex(ProofDataBase.COLUMNNOTES_ID));
				mTitre.setText(cursor.getString(cursor.getColumnIndex(ProofDataBase.COLUMN_TITLE)));
				mNote.setText(cursor.getString(cursor.getColumnIndex(ProofDataBase.COLUMN_NOTE)));
				//mCreation.setText(cursor.getString(cursor.getColumnIndex(ProofDataBase.COLUMN_DATE_CREATION)));
				
			}
		}
		catch(Exception e) {
			Console.print_exception(e);
		}
		finally {
			if(cursor != null) {
				cursor.close();
			}			
		}		
		
		mTitre.setOnTouchListener(mEditTitleAction);
		mNote.setOnTouchListener(mEditTxtAction);
	}
	
	private final OnTouchListener mEditTitleAction = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			mTitre.setText("");
			return false;
		}
	};	
	
	private final OnTouchListener mEditTxtAction = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			mNote.setText("");
			return false;
		}
	};

	@SuppressWarnings("UnusedAssignment")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fragment_notes_origine, container, false);
		
		return view;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		menu.add(Menu.NONE, R.id.note_add, Menu.NONE, getString(R.string.noteContextualMenuUpdate));
		menu.add(Menu.NONE, R.id.note_sup, Menu.NONE, getString(R.string.noteContextualMenuDelete));

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.note_add) {
			Console.print_debug("Item note_add");
			enregistrerNote();
			return true;
		} else if (item.getItemId() == R.id.note_sup) {
			Console.print_debug("Item note_sup");
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void enregistrerNote() {

		ContentValues valuesNote = new ContentValues();
		final String titre = mTitre.getText().toString();
		final String contenu = mNote.getText().toString();
		
		@SuppressWarnings("deprecation")
		final String date = new Date().toLocaleString();
		valuesNote.put(ProofDataBase.COLUMN_TITLE, titre);
		valuesNote.put(ProofDataBase.COLUMN_NOTE, contenu);
		valuesNote.put(ProofDataBase.COLUMN_DATE_LAST_MODIF, date);
		valuesNote.put(ProofDataBase.COLUMN_ISYNC_NOP, 0);
		
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "note_id/" + idNote);
		
		getActivity().getContentResolver().update(uri, valuesNote, null, null);
		
		Console.print_debug("Note :" + mTitre.getText().toString());
		Console.print_debug("Note :" + mNote.getText().toString());
		Console.print_debug("Note :" + date);
	}
}
