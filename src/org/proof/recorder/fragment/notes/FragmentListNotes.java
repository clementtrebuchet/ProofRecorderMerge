package org.proof.recorder.fragment.notes;

import java.util.Date;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class FragmentListNotes extends Fragment {
/*implements
		LoaderManager.LoaderCallbacks<Cursor> {*/
	static Bundle b;
	static String id;
	private static final String TAG = "FragmentListNotes";
	private static final String[] from = new String[] {
			ProofDataBase.COLUMN_TITLE, ProofDataBase.COLUMN_NOTE,
			ProofDataBase.COLUMN_DATE_LAST_MODIF, ProofDataBase.COLUMNNOTES_ID };
	/*private static final int LIST_LOADE = 0x55;
	private int[] to = { R.id.titreNotes, R.id.notesContenu, R.id.notesCreation };
	private SimpleCursorAdapter mAdaptexr;*/
	EditText mTitre;
	EditText mNote;
	TextView mCreation; 
	String idNote;
	int i ;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		b = getActivity().getIntent().getExtras();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		/*mAdaptexr = new SimpleCursorAdapter(getActivity(),
				R.layout.fragment_notes_origine, null, from, to,
				CursorAdapter.NO_SELECTION);*/
		
		if(Settings.isDebug())
			Log.v(TAG, "PASS HERE");
		/*setListAdapter(mAdaptexr);
		setListShown(true);*/
		setHasOptionsMenu(true);
		setMenuVisibility(true);
		//getLoaderManager().initLoader(LIST_LOADE, null, this);
		registerForContextMenu(getView());
		mTitre = (EditText) getView().findViewById(R.id.titreNotes);
		mNote = (EditText) getView().findViewById(R.id.notesContenu);
		mCreation = (TextView) getView().findViewById(R.id.notesCreation);
		id = (String) b.get("id");
		
		if(Settings.isDebug())
			Log.v(TAG, "<<PHONE's NOTE NUMERO>>" + id);
		
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "note_recordid/"
						+ id);
		
		if(Settings.isDebug())
			Log.v(TAG, uri.toString());
		
		Cursor c = getActivity().getContentResolver().query(uri, from, null, null, null);
		while (c.moveToNext()){
			idNote = c.getString(c.getColumnIndex(ProofDataBase.COLUMNNOTES_ID));
			mTitre.setText(c.getString(c.getColumnIndex(ProofDataBase.COLUMN_TITLE)));
			mNote.setText(c.getString(c.getColumnIndex(ProofDataBase.COLUMN_NOTE)));
			//mCreation.setText(c.getString(c.getColumnIndex(ProofDataBase.COLUMN_DATE_CREATION)));
			
		}
		c.close();
		/*mTitre.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	            i++;
	            mTitre.setText(mTitre.getText().toString());
	            
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
	    }); 
		mNote.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	            i++;
	            mNote.setText(mNote.getText().toString());
	            
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
	    }); */

	}
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
		/*AdapterContextMenuInfo record = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int recordPosition = (int) record.position;
		Log.v(TAG, "" + recordPosition);*/
		switch (item.getItemId()) {
		case R.id.note_add:
			
			if(Settings.isDebug())
				Log.i(TAG, "Item note_add");
			enregistrerNote();
			return true;
		case R.id.note_sup:
			
			if(Settings.isDebug())
				Log.i(TAG, "Item note_sup");
			return true;

		}
		return super.onContextItemSelected(item);
	}

	public void enregistrerNote() {
		/*idNote = ((SimpleCursorAdapter) getListAdapter()).getCursor()
				.getString(3);*/
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
		
		if(Settings.isDebug())
		{
			Log.e(TAG, "Note :" + mTitre.getText().toString());
			Log.e(TAG, "Note :" + mNote.getText().toString());
			Log.e(TAG, "Note :" + date);
		}
		
		
		//getLoaderManager().initLoader(LIST_LOADE, null, this);
	}

	/*@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		id = (String) b.get("id");
		Log.v(TAG, "<<NOTE NUMERO>>" + id);
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "note_recordid/"
						+ id);
		Log.v(TAG, uri.toString());
		CursorLoader cursorLoader = new CursorLoader(getActivity()
				.getApplicationContext(), uri, from, null, null, null);
		return cursorLoader;

	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		mAdaptexr.swapCursor(arg1);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdaptexr.swapCursor(null);

	}
*/
}
/* } */
