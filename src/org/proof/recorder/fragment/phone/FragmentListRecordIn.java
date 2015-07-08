package org.proof.recorder.fragment.phone;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import org.proof.recorder.R;
import org.proof.recorder.adapters.RecordAdapter;
import org.proof.recorder.bases.fragment.ProofFragment;
import org.proof.recorder.bases.fragment.ProofListFragmentWithQuickAction;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.fragment.contacts.utils.ContactsDataHelper;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.ApproxRecordTime;
import org.proof.recorder.utils.Log.Console;
import org.proof.recorder.utils.MenuActions;
import org.proof.recorder.utils.QuickActionDlg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FragmentListRecordIn extends ProofFragment {		

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static class InCommingCallsLoader extends ProofListFragmentWithQuickAction
	{		
		
		@SuppressWarnings("unchecked")
		@Override
		public void onListItemClick(ListView l, final View view, int position,
				long id) {

			super.onListItemClick(l, view, position, id);	

			if(!multiSelectEnabled) {				
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

			MenuActions.setInternalContext(getActivity());
			
			reverseCollection = true;
			
			startAsyncLoader();

			fillCollectionRunnable = new Runnable() {
				@Override
				public void run() {
					getContacts();
				}
			};
		}

		private void getContacts() {
			Uri uri;
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
						
						try {

							File g = new File(mFile);
							ApproxRecordTime f = new ApproxRecordTime(g);
							String stime = f.run();
							mRecord.setmSongTime(stime);
							Console.print_debug(stime);

						} catch (Exception e) {

							Console.print_exception(e);
						}
						
						
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
		protected void DoneAction() {
			FragmentListRecordTabs.readdUnusedTab();			
		}

		@Override
		protected void DeleteAllAction() {
			MenuActions.deleteCalls(recordIds, recordPaths);
			FragmentListRecordTabs.removeCurrentTab(getInternalContext());			
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
				((RecordAdapter)listAdapter).remove(item);
				objects.remove(item);
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
			initAdapter(getActivity(), objects, R.layout.listfragmentdroit, multiSelectEnabled);			
		}

		@Override
		protected void _doInBackground(Void... params) {
			getContacts();
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
			listAdapter = new RecordAdapter(context, collection, layoutId, multiSelectMode, getBroadcastName());
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

		@Override
		protected void alertDlgCancelAction(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub			
		}
	}
}
