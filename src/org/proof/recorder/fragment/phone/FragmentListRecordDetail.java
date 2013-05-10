package org.proof.recorder.fragment.phone;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.adapter.phone.RecorderDetailAdapter;
import org.proof.recorder.bases.activity.ProofFragmentActivity;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.fragment.notes.FragmentListNotes;
import org.proof.recorder.fragment.notes.FragmentNoteTabs;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.QuickActionDlg;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.CursorAdapter;

public class FragmentListRecordDetail extends ProofFragmentActivity {
	
	//private static final String TAG = "FragmentListRecordDetail";
	/** Called when the activity is first created. */
	static Bundle b;
	private static String id;
	static String Id_record;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		b = getIntent().getExtras();
		// setContentView(R.layout.fragmentdroitdet);
		FragmentManager fm = getSupportFragmentManager();

		// Create the list fragment and add it as our sole content.
		if (fm.findFragmentById(R.id.Detail_FragmentOne) == null) {
			CustomLoader list = new CustomLoader();
			fm.beginTransaction().add(R.id.Detail_FragmentOne, list).commit();
		}
		if (fm.findFragmentById(R.id.notes_origine) == null) {
			FragmentListNotes notes = new FragmentListNotes();
			fm.beginTransaction().add(R.id.notes_origine, notes).commit();
		}

		QuickActionDlg.setmContext(this);
		getSupportActionBar().setHomeButtonEnabled(true);
	}
	
    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {		
		return QuickActionDlg.mainUiMenuHandler(menu);
    }
	
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
	    return QuickActionDlg.mainActionsMenuHandler(item);
	}

	public static class CustomLoader extends ListFragment implements
			LoaderManager.LoaderCallbacks<Cursor> {

		private static final int LIST_LOADE = 0x01;
		private static final String TAG = "KnownContactsLoader->FragmentPhoneCallDetail";
		boolean mDualPane;
		int mCursorPos = -1;
		String[] from = new String[] { ProofDataBase.COLUMNRECODINGAPP_ID,
				ProofDataBase.COLUMN_TELEPHONE, ProofDataBase.COLUMN_SENS,
				ProofDataBase.COLUMN_FILE, ProofDataBase.COLUMN_TAILLE,
				ProofDataBase.COLUMN_HTIME };

		private RecorderDetailAdapter mAdapter;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}
		
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			
			int[] to = { R.id.mId, R.id.mPhone, R.id.mSenseTv, R.id.mFilePath, R.id.mSizeOfFile,
					R.id.mDateOfCall };

			mAdapter = new RecorderDetailAdapter(getActivity(),
					R.layout.record_detail, null, from, to,
					CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

			setListAdapter(mAdapter);
			setListShown(true);
			setHasOptionsMenu(true);
			setMenuVisibility(true);

			id = FragmentNoteTabs.id;

			if (Settings.isDebug()) {
				Log.v(TAG, "PASS HERE");
				Log.v(TAG, id);
			}
			// Prepare the loader. Either re-connect with an existing one,
			// or start a new one.
			getLoaderManager().initLoader(LIST_LOADE, null, this);
		}		 

		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			id = FragmentNoteTabs.id;
			// id = (String) b.get("id");
			if (Settings.isDebug())
				Log.v(TAG, id);

			Uri uri = Uri.withAppendedPath(
					PersonnalProofContentProvider.CONTENT_URI, "record_id/"
							+ id);
			CursorLoader cursorLoader = new CursorLoader(getActivity(), uri,
					from, null, null, null);
			return cursorLoader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
			mAdapter.swapCursor(arg1);
			
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			mAdapter.swapCursor(null);

		}

	}
}
