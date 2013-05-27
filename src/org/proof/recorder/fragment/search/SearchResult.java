package org.proof.recorder.fragment.search;

import java.util.ArrayList;
import java.util.List;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.bases.activity.ProofFragmentActivity;
import org.proof.recorder.bases.fragment.ProofListFragmentWithQuickAction;
import org.proof.recorder.database.collections.VoicesList;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.database.models.Voice;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.MenuActions;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.StaticIntents;
import org.proof.recorder.utils.Log.Console;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;

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
		if(SearchListLoader.isMulti) {
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

		private void getItems() {

			String mByDateQuery = null;
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

			innerCollection = null;		

			try {

				cursor = getInternalContext().getContentResolver().query(
						uri, null, null, null, null);

				if(mVoices) {
					VoicesList mList = new VoicesList(cursor);					
					innerCollection = mList.getCollection();
				}

				else if(mCalls) {

					innerCollection = new ArrayList<Object>();

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

						innerCollection.add(mRecord);
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

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);		

			String mLogMsg = "";

			extraData = getActivity().getIntent().getExtras();

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

		@Override
		public void onListItemClick(ListView l, View view, int position, long id) {
			super.onListItemClick(l, view, position, id);

			if (!isMulti) {
				if(mCalls)
					QuickActionDlg.showPhoneOptionsDlg(
							getActivity(), 
							view, 
							listAdapter, 
							(Record) listAdapter.getItem(position)
							);

				else if(mVoices)
					QuickActionDlg.showTitledVoiceOptionsDlg(
							getActivity(), 
							view, 
							(Voice) listAdapter.getItem(position), 
							listAdapter, 
							innerCollection, 
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
				
				for (Object item : innerCollection) {
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
				
				for (Object item : innerCollection) {
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
			
			for(Object item : innerCollection) {
				
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
					((org.proof.recorder.adapter.phone.ObjectsAdapter)listAdapter).remove((Record) item);
					((ArrayList<Object>)innerCollection).remove((Record) item);
				}
				else if(mVoices) {
					((org.proof.recorder.adapter.voice.ObjectsAdapter)listAdapter).remove((Voice) item);
					((ArrayList<Object>)innerCollection).remove((Voice) item);
				}
			}
			
			if(mCalls) {
				((org.proof.recorder.adapter.phone.ObjectsAdapter)listAdapter).notifyDataSetChanged();
			}
			else if(mVoices) {
				((org.proof.recorder.adapter.voice.ObjectsAdapter)listAdapter).notifyDataSetChanged();
			}
		}

		@Override
		protected void preDeleteAllAction() {
			
			int iter = 0;
			for (Object item : innerCollection) {
				if(mCalls) {
					recordIds[iter] = ((Record) item).getmId();
					recordPaths[iter] = ((Record) item).getmFilePath();
				}
				else if(mVoices) {
					recordIds[iter] = ((Voice) item).getId();
					recordPaths[iter] = ((Voice) item).getFilePath();
				}
				else
					break;

				iter++;

				Console.print_debug("Position: " + item);
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
			}
			else
				return;
			
			
		}

		@Override
		protected void ShareAction() {
			MenuActions.sharingOptions(recordPaths);			
		}

		@Override
		protected boolean itemChecked(Object item) {

			if(mCalls)
				return ((Record) item).isChecked();

			else if(mVoices)
				return ((Voice) item).isChecked();

			else
				return false;
		}

		@Override
		protected int innerCollectionSorting(Object first, Object second) {

			if(mCalls)
				return ((Record) first).getmHtime().compareToIgnoreCase(
						((Record) second).getmHtime());

			else if(mVoices)
				return ((Voice) first).getHumanTime().compareToIgnoreCase(
						((Voice) second).getHumanTime());

			else
				return 0;

		}

		@Override
		protected void uncheckItem(Object item) {
			if(mCalls)
				((Record) item).setChecked(false);
			else if(mVoices)
				((Voice) item).setChecked(false);			
			else
				return;
		}

		@Override
		protected void toggleItem(Object item, boolean checked) {
			if(mCalls)
				((Record) item).setChecked(checked);
			else if(mVoices)
				((Voice) item).setChecked(checked);
			else
				return;
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
				listAdapter = new org.proof.recorder.adapter.phone.ObjectsAdapter(
						context, collection, layoutId, multiSelectMode);

			if(mVoices)
				listAdapter = new org.proof.recorder.adapter.voice.ObjectsAdapter(
						context, collection, layoutId, multiSelectMode);
		}

	}

}
