package org.proof.recorder.fragment.phone;

import java.util.ArrayList;
import java.util.List;

import org.proof.recorder.R;
import org.proof.recorder.adapters.RecordAdapter;
import org.proof.recorder.bases.fragment.ProofFragment;
import org.proof.recorder.bases.fragment.ProofListFragmentWithQuickAction;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.fragment.contacts.utils.ContactsDataHelper;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.MenuActions;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.Log.Console;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

public class FragmentListRecordIn extends ProofFragment {		

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static class InCommingCallsLoader extends ProofListFragmentWithQuickAction
	{

		/*		private void sendEventToFolderList() {
					if(Settings.isDebug())
					  Log.d("sender", "Broadcast: eventListNeedFolderRefreshReceiver (out)");

					  Intent intent = new Intent("eventListToBeRefreshedReceiver");
					  intent.putExtra("message", "Refresh");
					  LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);		  
					}

				private ProofBroadcastReceiver eventListNeedFolderRefreshReceiver = new ProofBroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						super.onReceive(context, intent);
						StaticIntents redirectIntent = StaticIntents.create(getActivity(), FragmentListKnownContacts.class);
						startActivity(redirectIntent);
					}
				};*/

		@SuppressWarnings("unchecked")
		@Override
		public void onListItemClick(ListView l, final View view, int position,
				long id) {

			super.onListItemClick(l, view, position, id);	

			if(!isMulti) {				
				Record mRecord = (Record) objects.get(position);				
				QuickActionDlg.showPhoneOptionsDlg(
						getActivity(), 
						view, 
						(ArrayAdapter<Object>) 
						listAdapter, 
						mRecord);
			}
			else {
				CheckBox checkbox = (CheckBox) view.findViewById(R.id.cb_select_item);
				checkbox.toggle();
			}
		}

		@Override
		protected void initOnOptionsItemSelected() {
			FragmentListRecordTabs.removeUnusedTab();			
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);		

			/*LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
							eventListNeedFolderRefreshReceiver, 
							new IntentFilter("eventOutNeedToBeRefreshedReceiver"));*/

			MenuActions.setInternalContext(getActivity());
			
			startAsyncLoader();

			fillCollectionRunnable = new Runnable() {
				@Override
				public void run() {
					getContacts();
				}
			};
		}

		private void getContacts() {
			Uri uri = null;
			Cursor cursor = null;

			if(objects != null)
				objects.clear();
			else{
				objects = new ArrayList<Object>();
			}

			if (FragmentListRecordTabs.isNotify()) {
				uri = Uri.withAppendedPath(
						PersonnalProofContentProvider.CONTENT_URI, "record_id/"
								+ FragmentListRecordTabs.getSavedId());

				try {
					cursor = getActivity().getContentResolver().query(
							uri, null, null, null, null);

					while (cursor.moveToNext()) {

						String mId = cursor.getString(cursor
								.getColumnIndex(ProofDataBase.COLUMNRECODINGAPP_ID));

						String mAndroidId = cursor.getString(cursor
								.getColumnIndex(ProofDataBase.COLUMN_CONTRACT_ID));

						String mPhone = cursor.getString(cursor
								.getColumnIndex(ProofDataBase.COLUMN_TELEPHONE));

						String mFile = cursor.getString(cursor
								.getColumnIndex(ProofDataBase.COLUMN_FILE));

						String mHtime = cursor.getString(cursor
								.getColumnIndex(ProofDataBase.COLUMN_HTIME));

						String mSense = cursor.getString(cursor
								.getColumnIndex(ProofDataBase.COLUMN_SENS));

						Record mRecord = new Record(
								mId, mFile, mPhone, mSense, mHtime, mAndroidId);

						if(mRecord.isIncomingCall())
							objects.add(mRecord);
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
			}			
			else {
				try {
					String mIdOrTelephone = extraData.getString("mIdOrTelephone");
					objects = (ArrayList<Object>) ContactsDataHelper.getIncommingCalls(
							getActivity(), mIdOrTelephone);
				} catch (Exception e) {
					Console.print_exception(e);
				}
			}			               			
		}

		@Override
		protected void preDeleteAllAction() {

			int iter = 0;
			for (Object record : objects) {
				recordIds[iter] = ((Record) record).getmId();
				recordPaths[iter] = ((Record) record).getmFilePath();

				iter++;

				Console.print_debug("Position: " + record);
			}			
		}

		@Override
		protected void DoneAction() {
			FragmentListRecordTabs.readdUnusedTab();			
		}

		@Override
		protected void DeleteAllAction() {
			MenuActions.deleteCalls(recordIds, recordPaths);
			FragmentListRecordTabs.removeCurrentTab(getInternalContext());			
		}

		@Override
		protected void ShareAction() {
			MenuActions.sharingOptions(recordPaths);			
		}

		@Override
		protected void preDeleteAndShareAction() {
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

		@Override
		protected void DeleteAction() {

			MenuActions.deleteCalls(recordIds, recordPaths);

			ArrayList<Object> toBeProcessed = new ArrayList<Object>();

			for(Object item : objects) {
				Record lcRecord = (Record) item;

				if(lcRecord.isChecked()) {					
					toBeProcessed.add(lcRecord);			
				}					
			}

			for(Object item : toBeProcessed) {
				((RecordAdapter)listAdapter).remove((Record) item);
				((ArrayList<Object>)objects).remove((Record) item);
			}

			((RecordAdapter)listAdapter).notifyDataSetChanged();		
		}

		@Override
		protected boolean itemChecked(Object item) {
			return ((Record) item).isChecked();
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
			initAdapter(getActivity(), objects, R.layout.listfragmentdroit, isMulti);			
		}

		@Override
		protected Long _doInBackground(Void... params) {
			getContacts();
			return null;
		}

		@Override
		protected int collectionSorter(Object object1, Object object2) {
			Record r1 = (Record) object1;
			Record r2 = (Record) object2;
			return (r1.getmTimeStamp().compareToIgnoreCase(r2.getmTimeStamp()));
		}

		@Override
		protected void initAdapter(Context context, List<Object> collection,
				int layoutId, boolean multiSelectMode) {
			listAdapter = null;
			listAdapter = new RecordAdapter(context, collection, layoutId, multiSelectMode);
		}

		@Override
		protected void uncheckItem(Object item) {
			((Record) item).setChecked(false);	
		}

		@Override
		protected void toggleItem(Object item, boolean checked) {
			((Record) item).setChecked(checked);
		}

		@Override
		protected Object getItemClone(Object item) {
			return ((Record) item).clone();
		}
	}
}
