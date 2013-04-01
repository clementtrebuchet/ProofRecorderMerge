package org.proof.recorder.fragment.search;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.adapter.phone.SearchListAdapter;
import org.proof.recorder.adapter.voice.VoiceListAdapter;
import org.proof.recorder.database.support.ProofDataBase;
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
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class SearchResult extends SherlockFragmentActivity {

	private static final String SEP_QUERY = ";", BR = "\n";

	private static boolean mByDate, mPreciseDate, mPeriodDate, mVoices, mCalls;
	private static String mQuery, mPrecise, mStartingDate, mEndingDate;
	private static Bundle extraDatas;
	
	/**
	 * @param message
	 */
	private static void print(String message) {
		if(Settings.isDebug())
			Log.d(SearchResult.class.getName(), message);
	}

	@Override
	public void onCreate(Bundle mIcicle) {
		super.onCreate(mIcicle);
		setContentView(R.layout.fragment_contacts_list);

		FragmentManager fm = getSupportFragmentManager();

		ActionBar mBar = getSupportActionBar();
		mBar.setHomeButtonEnabled(true);

		if (fm.findFragmentById(R.id.contacts_list_fragment) == null) {
			SearchListLoader list = new SearchListLoader();
			fm.beginTransaction().add(R.id.contacts_list_fragment, list)
					.commit();
		}		
	}

	public static class SearchListLoader extends ListFragment implements
			LoaderManager.LoaderCallbacks<Cursor> {

		private static final int LIST_LOADE = 0x01;

		boolean mDualPane;
		int mCursorPos = -1;

		private String[] mVoicesFrom = new String[] {
				ProofDataBase.COLUMNVOICE_ID, ProofDataBase.COLUMN_VOICE_HTIME,
				ProofDataBase.COLUMN_VOICE_TAILLE,
				ProofDataBase.COLUMN_VOICE_TIMESTAMP,
				ProofDataBase.COLUMN_VOICE_FILE };

		private String[] mCallsFrom = new String[] {
				ProofDataBase.COLUMNRECODINGAPP_ID, ProofDataBase.COLUMN_HTIME,
				ProofDataBase.COLUMN_TAILLE, ProofDataBase.COLUMN_TIMESTAMP,
				ProofDataBase.COLUMN_FILE, ProofDataBase.COLUMN_TELEPHONE };

		private Object mAdapter;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			String mLogMsg = "SEARCH LOG MESSAGE => " + BR;

			extraDatas = getActivity().getIntent().getExtras();

			mByDate = extraDatas.getBoolean("mByDate");
			mCalls = extraDatas.getBoolean("mCalls");
			mVoices = extraDatas.getBoolean("mVoices");

			mLogMsg += "mByDate: " + mByDate + BR;
			mLogMsg += "mCalls: " + mCalls + BR;
			mLogMsg += "mVoices: " + mVoices + BR;

			if (mByDate) {
				mPreciseDate = extraDatas.getBoolean("mPreciseDate");
				mPeriodDate = extraDatas.getBoolean("mPeriodDate");

				mLogMsg += "mPreciseDate: " + mPreciseDate + BR;
				mLogMsg += "mPeriodDate: " + mPeriodDate + BR;

				if (mPreciseDate) {
					mPrecise = extraDatas.getString("preciseDate");
					mLogMsg += "mPrecise: " + mPrecise + BR;
				} else if (mPeriodDate) {
					mStartingDate = extraDatas.getString("startingDate");
					mEndingDate = extraDatas.getString("endingDate");

					mLogMsg += "mStartingDate: " + mStartingDate + BR;
					mLogMsg += "mEndingDate: " + mEndingDate + BR;
				} else {
				} // should never happened
			} else {
				mPreciseDate = false;
				mPeriodDate = false;
				mPrecise = "";
				mStartingDate = "";
				mEndingDate = "";
			}

			mQuery = extraDatas.getString("mQuery");

			mLogMsg += "mQuery: " + mQuery;

			print(mLogMsg);
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

			if (mCalls) {

				int[] to = { R.id.idrecord, R.id.timehumanreadable, R.id.sens, };

				mAdapter = new SearchListAdapter(getActivity(),
						R.layout.listfragmentdroit, null, mCallsFrom, to,
						CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
				
			} else if (mVoices) {

				int[] to = { R.id.idrecord, R.id.timehumanreadable, R.id.sens, };

				mAdapter = new VoiceListAdapter(getActivity(),
						R.layout.listfragmentdroit, null, mVoicesFrom, to,
						CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			}

			else {
			}

			setListAdapter((ListAdapter) mAdapter);
			setListShown(true);
			setHasOptionsMenu(true);
			setMenuVisibility(true);

			if (Settings.isDebug())
				print("PASS HERE");
			
			getLoaderManager().initLoader(LIST_LOADE, null, this);

			registerForContextMenu(getListView());

			if (((android.support.v4.widget.CursorAdapter) mAdapter).getCount() == 0)
				setEmptyText(getString(R.string.search_none_records_found));
		}

		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			String[] mFrom = null;
			String mByDateQuery = null;
			Uri uri = null;
			CursorLoader cursorLoader;

			if (mCalls) {

				mFrom = mCallsFrom;

				if (mByDate) {
					mByDateQuery = mQuery;
					if (mPreciseDate) {
						mByDateQuery += SEP_QUERY + mPrecise;
						uri = Uri.withAppendedPath(
								PersonnalProofContentProvider.CONTENT_URI,
								"search_calls_by_date/" + mByDateQuery);
					} else if (mPeriodDate) {
						mByDateQuery += SEP_QUERY + mStartingDate + SEP_QUERY + mEndingDate;
						uri = Uri.withAppendedPath(
								PersonnalProofContentProvider.CONTENT_URI,
								"search_calls_by_date/" + mByDateQuery);
					}

					else {
					} // Should never happened
				} else {
					uri = Uri.withAppendedPath(
							PersonnalProofContentProvider.CONTENT_URI,
							"search_calls/" + mQuery);
				}
			}

			else if (mVoices) {

				mFrom = mVoicesFrom;

				if (mByDate) {
					mByDateQuery = mQuery;
					if (mPreciseDate) {
						mByDateQuery += SEP_QUERY + mPrecise;
						uri = Uri.withAppendedPath(
								PersonnalProofContentProvider.CONTENT_URI,
								"search_voices_by_date/" + mByDateQuery);
					} else if (mPeriodDate) {
						mByDateQuery += SEP_QUERY + mStartingDate + SEP_QUERY + mEndingDate;
						uri = Uri.withAppendedPath(
								PersonnalProofContentProvider.CONTENT_URI,
								"search_voices_by_date/" + mByDateQuery);
					}

					else {
					} // Should never happened
				} else {
					uri = Uri.withAppendedPath(
							PersonnalProofContentProvider.CONTENT_URI,
							"search_voices/" + mQuery);
				}
			}

			else {
			} // Should never happened

			cursorLoader = new CursorLoader(getActivity(), uri, mFrom, null,
					null, null);

			return cursorLoader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
			((SimpleCursorAdapter) mAdapter).swapCursor(arg1);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			((SimpleCursorAdapter) mAdapter).swapCursor(null);
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			super.onListItemClick(l, v, position, id);
			
			Cursor c = ((android.support.v4.widget.CursorAdapter) getListAdapter()).getCursor();
			
			if(mCalls)
				QuickActionDlg.showSearchOptionsDlg(getActivity(), v, c, Settings.mType.CALL, getLoaderManager(), this);
			else if(mVoices)
				QuickActionDlg.showSearchOptionsDlg(getActivity(), v, c, Settings.mType.VOICE, getLoaderManager(), this);
			else {}
		}

	}

}
