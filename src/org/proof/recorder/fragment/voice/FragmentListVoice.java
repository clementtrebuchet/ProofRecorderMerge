package org.proof.recorder.fragment.voice;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.adapter.voice.VoiceListAdapter;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.QuickActionDlg;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class FragmentListVoice extends SherlockFragment {

	//private static final String TAG = "FragmentListVoice";
	
	public static String ID;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static class VoiceListLoader extends ListFragment implements
			LoaderManager.LoaderCallbacks<Cursor> {

		private static final int LIST_LOADE = 0x01;
		private static final String TAG = "FragmentListOfVoice";
		boolean mDualPane;
		int mCursorPos = -1;
		
		private static Bundle extraDatas;

		private String[] from = new String[] {
				ProofDataBase.COLUMNVOICE_ID,
				ProofDataBase.COLUMN_VOICE_HTIME, 
				ProofDataBase.COLUMN_VOICE_TAILLE,
				ProofDataBase.COLUMN_VOICE_TIMESTAMP,
				ProofDataBase.COLUMN_VOICE_FILE
		};

		//private static Cursor c;

		private VoiceListAdapter mAdapter;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			extraDatas = getActivity().getIntent().getExtras();
		}

		/**
		 * Contextual Menu for displaying social and all :)
		 */

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {			
			return super.onCreateView(inflater, container, savedInstanceState);
		}

		/**
		 * End of Contextual Menu
		 */

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			int[] to = { R.id.idrecord, R.id.timehumanreadable, R.id.sens,
					 };

			//setEmptyText("Pas d\'enregistrement");		
			
			
			/*QuickActionDlg.mainDlgClickableMenu(
					getActivity(), 
					getString(R.string.strABVoiceRecords)
			);*/

			mAdapter = new VoiceListAdapter(getActivity(),
					R.layout.listfragmentdroit, null, from, to,
					CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

			setListAdapter(mAdapter);
			setListShown(true);
			setHasOptionsMenu(true);
			setMenuVisibility(true);

			if(Settings.isDebug())
				Log.v(TAG, "PASS HERE");
			// Prepare the loader. Either re-connect with an existing one,
			// or start a new one.
			getLoaderManager().initLoader(LIST_LOADE, null, this);

			registerForContextMenu(getListView());
		}

		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			Uri uri;
			String mQuery;
			CursorLoader cursorLoader;
			
			try {
				
				mQuery = (String) extraDatas.get("search");
				
				if(mQuery == null)
					throw new Exception();
				
				uri = Uri.withAppendedPath(
						PersonnalProofContentProvider.CONTENT_URI, "voices_by_title/" + mQuery);
				cursorLoader = new CursorLoader(getActivity(), uri,
						from, null, null, null);
			}
			catch(Exception e) {
				
				uri = Uri.withAppendedPath(
						PersonnalProofContentProvider.CONTENT_URI, "voices");
				cursorLoader = new CursorLoader(getActivity(), uri,
						from, null, null, null);
			}			
			
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

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {

			super.onListItemClick(l, v, position, id);
			Cursor c = ((VoiceListAdapter) getListAdapter()).getCursor();
			
			if(Settings.isDebug())
				Log.v("MA_LISTE_DE_MERDE_AT :", "" + position);
			
			QuickActionDlg.showTitledVoiceOptionsDlg(getActivity(),v, c, getListAdapter(), getLoaderManager(), this, Settings.mType.VOICE_TITLED);
		}

	}

}