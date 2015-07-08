package org.proof.recorder.fragment.search;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.bases.activity.ProofFragmentActivity;
import org.proof.recorder.bases.fragment.ProofListFragmentWithQuickAction;
import org.proof.recorder.database.collections.VoicesList;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.database.models.Voice;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.Log.Console;
import org.proof.recorder.utils.MenuActions;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.StaticIntents;

import java.util.ArrayList;
import java.util.List;

public class SearchResult extends ProofFragmentActivity {

	private static final String SEP_QUERY = ";", BR = "\n";

	private static boolean mByDate, 
	mPreciseDate, 
	mPeriodDate, 
	mVoices, 
	mCalls;

	private static String mQuery, 
	mPrecise, 
	mStartingDate, 
	mEndingDate;
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(ProofListFragmentWithQuickAction.multiSelectEnabled) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && 
					event.getAction() == KeyEvent.ACTION_UP) {
				// handle your back button code here
				// consumes the back key event - ActionMode is not finished
				return true; 
			}
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	protected void onCreate(Bundle mIcicle) {
		super.onCreate(mIcicle);
		setContentView(R.layout.fragment_contacts_list);

		FragmentManager fm = getSupportFragmentManager();

		ActionBar mBar = getSupportActionBar();

		mBar.setHomeButtonEnabled(true);

		if (fm.findFragmentById(R.id.contacts_list_fragment) == null) {
			SearchListLoader list = new SearchListLoader();
			fm.beginTransaction().add(R.id.contacts_list_fragment, list).commit();
		}		
	}

	public static class SearchListLoader extends ProofListFragmentWithQuickAction {

		@SuppressWarnings("StatementWithEmptyBody")
		private void getItems() {

			String mByDateQuery;
			Uri uri = null;

			if (mCalls) {

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

					else {} // Should never happened
				} else {
					uri = Uri.withAppendedPath(
							PersonnalProofContentProvider.CONTENT_URI,
							"search_calls/" + mQuery);
				}
			}
			else if (mVoices) {

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

					else {} // Should never happened
				} else {
					uri = Uri.withAppendedPath(
							PersonnalProofContentProvider.CONTENT_URI,
							"search_voices/" + mQuery);
				}
			}
			else {} // Should never happened

			Cursor cursor = null;

			objects = null;		

			try {

				assert uri != null;
				cursor = getInternalContext().getContentResolver().query(
						uri, null, null, null, null);

				if(mVoices) {
					VoicesList mList = new VoicesList(cursor);					
					objects = (ArrayList<Object>) mList.getCollection();
				}

				else if(mCalls) {

					objects = new ArrayList<Object>();

					while (cursor.moveToNext()) {

						String mId = cursor
								.getString(cursor
										.getColumnIndex(ProofDataBase.COLUMNRECODINGAPP_ID));
						
						String mAndroidId = cursor
								.getString(cursor
										.getColumnIndex(ProofDataBase.COLUMN_CONTRACT_ID));

						String mPhone = cursor
								.getString(cursor
										.getColumnIndex(ProofDataBase.COLUMN_TELEPHONE));

						String mFile = cursor.getString(cursor
								.getColumnIndex(ProofDataBase.COLUMN_FILE));

						String mHtime = cursor.getString(cursor
								.getColumnIndex(ProofDataBase.COLUMN_HTIME));

						String mSense = cursor.getString(cursor
								.getColumnIndex(ProofDataBase.COLUMN_SENS));

						Record mRecord = new Record(mId, mFile, mPhone, mSense,
								mHtime, mAndroidId);

						objects.add(mRecord);
					}
				}

			} catch (Exception e) {
				Console.print_exception(e);
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}			
		}

		@SuppressWarnings("StatementWithEmptyBody")
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);		

			String mLogMsg = "";

			mCalls = extraData.getBoolean("mCalls");
			mVoices = extraData.getBoolean("mVoices");

			mByDate = extraData.getBoolean("mByDate");

			if(Settings.isDebug()) {
				mLogMsg += "SEARCH LOG MESSAGE => " + BR;
				mLogMsg += "mByDate: " + mByDate + BR;
				mLogMsg += "mCalls: " + mCalls + BR;
				mLogMsg += "mVoices: " + mVoices + BR;
			}

			if (mByDate) {
				mPreciseDate = extraData.getBoolean("mPreciseDate");
				mPeriodDate = extraData.getBoolean("mPeriodDate");

				if(Settings.isDebug()) {
					mLogMsg += "mPreciseDate: " + mPreciseDate + BR;
					mLogMsg += "mPeriodDate: " + mPeriodDate + BR;
				}

				if (mPreciseDate) {
					mPrecise = extraData.getString("preciseDate");

					if(Settings.isDebug())
						mLogMsg += "mPrecise: " + mPrecise + BR;

				} else if (mPeriodDate) {
					mStartingDate = extraData.getString("startingDate");
					mEndingDate = extraData.getString("endingDate");

					if(Settings.isDebug()) {
						mLogMsg += "mStartingDate: " + mStartingDate + BR;
						mLogMsg += "mEndingDate: " + mEndingDate + BR;
					}

				} else {} // should never happened

			} else {
				mPreciseDate = false;
				mPeriodDate = false;
				mPrecise = "";
				mStartingDate = "";
				mEndingDate = "";
			}

			mQuery = extraData.getString("mQuery");

			if(Settings.isDebug())
				mLogMsg += "mQuery: " + mQuery;

			Console.print_debug(mLogMsg);

			fillCollectionRunnable = new Runnable() {
				@Override
				public void run() {
					getItems();
				}
			};
			
			startAsyncLoader();
		}

		/**
		 * End of Contextual Menu
		 */

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setListShown(true);
			setMenuVisibility(true);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onListItemClick(ListView l, View view, int position, long id) {
			super.onListItemClick(l, view, position, id);

			if (!multiSelectEnabled) {
				if(mCalls)
					QuickActionDlg.showPhoneOptionsDlg(
							getActivity(), 
							view, 
							(ArrayAdapter<Object>) listAdapter, 
							(Record) ((ArrayAdapter<Object>) listAdapter).getItem(position)
							);

				else if(mVoices)
					QuickActionDlg.showTitledVoiceOptionsDlg(
							getActivity(), 
							view, 
							(Voice) ((ArrayAdapter<Object>) listAdapter).getItem(position), 
							listAdapter, 
							objects,
							Settings.mType.VOICE);

			} else {
				CheckBox checkbox = (CheckBox) view
						.findViewById(R.id.cb_select_item);
				checkbox.toggle();
			}
		}
		
		@Override
		protected void initOnOptionsItemSelected() {
			// TODO Auto-generated method stub
		}
		
		@Override
		protected void DoneAction() {
			// TODO Auto-generated method stub
		}

		@Override
		protected void preDeleteAndShareAction() {
			if(mCalls) {
				int iter = 0;					
				
				for (Object item : objects) {
					Record lcRecord = (Record) item;
					
					if(lcRecord.isChecked()) {
						try {						
							recordIds[iter] = lcRecord.getmId();
							recordPaths[iter] = lcRecord.getmFilePath();
							
							iter++;
						}
						catch (Exception e) {
							Console.print_exception(e);
						}	
					}							
				}
			}
			else if(mVoices) {
				int iter = 0;					
				
				for (Object item : objects) {
					Voice lcVoice = (Voice) item;
					
					if(lcVoice.isChecked()) {
						try {						
							recordIds[iter] = lcVoice.getId();
							recordPaths[iter] = lcVoice.getFilePath();
							
							iter++;
						}
						catch (Exception e) {
							Console.print_exception(e);
						}	
					}							
				}	
			}
		}

		@Override
		protected void DeleteAction() {
			
			if(mCalls) {
				MenuActions.deleteCalls(recordIds, recordPaths);
			}
			else if(mVoices) {
				MenuActions.deleteVoices(recordIds, recordPaths);
			}
			
			ArrayList<Object> toBeProcessed = new ArrayList<Object>();
			
			for(Object item : objects) {
				
				if(mCalls) {
					Record lcItem = (Record) item;
					if(lcItem.isChecked()) {					
						toBeProcessed.add(lcItem);			
					}
				}
				else if(mVoices) {
					Voice lcItem = (Voice) item;
					if(lcItem.isChecked()) {					
						toBeProcessed.add(lcItem);			
					}
				}									
			}
			
			for(Object item : toBeProcessed) {
				if(mCalls) {
					((org.proof.recorder.adapters.RecordAdapter)listAdapter).remove(item);
					objects.remove(item);
				}
				else if(mVoices) {
					((org.proof.recorder.adapters.VoiceAdapter)listAdapter).remove(item);
					objects.remove(item);
				}
			}
			
			if(mCalls) {
				((org.proof.recorder.adapters.RecordAdapter)listAdapter).notifyDataSetChanged();
			}
			else if(mVoices) {
				((org.proof.recorder.adapters.VoiceAdapter)listAdapter).notifyDataSetChanged();
			}
		}

		@Override
		protected void DeleteAllAction() {
			if(mCalls) {
				MenuActions.deleteCalls(recordIds, recordPaths);
				getActivity().startActivity(StaticIntents.goPhone(getInternalContext()));
			}
			else if(mVoices) {
				MenuActions.deleteVoices(recordIds, recordPaths);
				getActivity().startActivity(StaticIntents.goVoice(getInternalContext()));
			} else {
			}
			
		}

		@Override
		protected boolean itemChecked(Object item) {

			if (mCalls)
				return ((Record) item).isChecked();

			else
				return mVoices && ((Voice) item).isChecked();
		}

		@Override
		protected void uncheckItem(Object item) {
			if(mCalls)
				((Record) item).setChecked(false);
			else if(mVoices)
				((Voice) item).setChecked(false);
			else {
			}
		}

		@Override
		protected void toggleItem(Object item, boolean checked) {
			if(mCalls)
				((Record) item).setChecked(checked);
			else if(mVoices)
				((Voice) item).setChecked(checked);
			else {
			}
		}

		@Override
		protected Object getItemClone(Object item) {
			if(mCalls)
				return ((Record) item).clone();
			else if(mVoices)
				return ((Voice) item).clone();
			else
				return item;
		}

		@Override
		protected void initAdapter(Context context, List<Object> collection,
				int layoutId, boolean multiSelectMode) {

			listAdapter = null;

			if(mCalls)
				listAdapter = new org.proof.recorder.adapters.RecordAdapter(
						context, collection, layoutId, multiSelectMode, getBroadcastName());

			if(mVoices)
				listAdapter = new org.proof.recorder.adapters.VoiceAdapter(
						context, collection, layoutId, multiSelectMode, getBroadcastName());
		}

		@Override
		protected void _onPreExecute() {
			// TODO Auto-generated method stub			
		}

		@Override
		protected void _onProgressUpdate(Integer... progress) {
			// TODO Auto-generated method stub			
		}

		@Override
		protected void _onPostExecute(Long result) {
			initAdapter(getActivity(), 
					objects, 
					R.layout.listfragmentdroit, 
					multiSelectEnabled);
		}

		@Override
		protected void _doInBackground(Void... params) {
			getItems();
		}

		@Override
		protected int collectionSorter(Object object1, Object object2) {
			if(mCalls)
				return ((Record) object1).getmHtime().compareToIgnoreCase(
						((Record) object2).getmHtime());

			else if(mVoices)
				return ((Voice) object1).getHumanTime().compareToIgnoreCase(
						((Voice) object2).getHumanTime());

			else
				return 0;
		}

		@Override
		protected void alertDlgCancelAction(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			
		}
	}
}
