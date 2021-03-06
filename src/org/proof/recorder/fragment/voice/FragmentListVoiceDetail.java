package org.proof.recorder.fragment.voice;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import org.proof.recorder.R;
import org.proof.recorder.adapter.voice.VoiceDetailAdapter;
import org.proof.recorder.bases.activity.ProofFragmentActivity;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.QuickActionDlg;

public class FragmentListVoiceDetail extends ProofFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentManager fm = getSupportFragmentManager();

		// Create the list fragment and add it as our sole content.
		if (fm.findFragmentById(R.id.Detail_FragmentVoice) == null) {
			CustomLoader list = new CustomLoader();
			fm.beginTransaction().add(R.id.Detail_FragmentVoice, list).commit();
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

	@SuppressWarnings("unused")
	public static class CustomLoader extends ListFragment implements
			LoaderManager.LoaderCallbacks<Cursor> {

		private static final int LIST_LOADE = 0x01;

		boolean mDualPane;
		int mCursorPos = -1;

		final String[] from = new String[]{
				ProofDataBase.COLUMNVOICE_ID,
				ProofDataBase.COLUMN_VOICE_FILE, 
				ProofDataBase.COLUMN_VOICE_TAILLE,
				ProofDataBase.COLUMN_VOICE_HTIME 
		};

		private VoiceDetailAdapter mAdapter;

		@SuppressWarnings("EmptyMethod")
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			int[] to = { R.id.mId, R.id.mTitleNote, R.id.mSizeOfFile,
					R.id.mDate };

			mAdapter = new VoiceDetailAdapter(getActivity(),
					R.layout.voice_detail, from, to,
					Integer.parseInt(FragmentListVoice.ID));

			setListAdapter(mAdapter);
			setListShown(true);
			setHasOptionsMenu(true);
			setMenuVisibility(true);

			// Prepare the loader. Either re-connect with an existing one,
			// or start a new one.
			getLoaderManager().initLoader(LIST_LOADE, null, this);
		}

		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {

			/*id = (String) b.get("id"); */
			
			Uri uri = Uri.withAppendedPath(
					PersonnalProofContentProvider.CONTENT_URI, "voice_id/" + FragmentListVoice.ID);
			return new CursorLoader(getActivity(), uri,
					from, null, null, null);
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
