package org.proof.recorder.fragment.phone;

import java.util.ArrayList;
import java.util.List;

import org.proof.recorder.R;
import org.proof.recorder.adapter.phone.ObjectsAdapter;
import org.proof.recorder.bases.fragment.ProofFragment;
import org.proof.recorder.bases.fragment.ProofListFragmentWithQuickAction;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.fragment.contacts.utils.ContactsDataHelper;
import org.proof.recorder.utils.MenuActions;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.Log.Console;

import android.content.Context;
import android.os.Bundle;

import android.view.View;

import android.widget.CheckBox;
import android.widget.ListView;

public class FragmentListRecordOut extends ProofFragment {		

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static class OutGoingCallsLoader extends ProofListFragmentWithQuickAction
	{
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			fillCollectionRunnable = new Runnable() {
				@Override
				public void run() {
					getContacts();
				}
			};					
		}

		/* Options Menu */

		/*@Override
		public boolean onOptionsItemSelected(
				com.actionbarsherlock.view.MenuItem item) {
			
			boolean result = false;
			if(item.getItemId() == R.id.cm_records_list_del_file) {
				result = super.onOptionsItemSelected(item);

				listAdapter.clear();
				initOnActivityCreated();
			}
			return result;
		}*/

		/* End of Options Menu */

		private void getContacts() {
			try {
				String mIdOrTelephone = extraData.getString("mIdOrTelephone");
				String mWhere = extraData.getString("mWhereClause");
				innerCollection = ContactsDataHelper.getOutGoingCalls(getActivity(), mWhere, mIdOrTelephone);
			} catch (Exception e) {				
				Console.print_exception(e);
			}
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			initOnActivityCreated();			
		}

		@Override
		public void onListItemClick(ListView l, final View view, int position,
				long id) {

			super.onListItemClick(l, view, position, id);	

			if(!isMulti) {				
				QuickActionDlg.showPhoneOptionsDlg(
						getActivity(), 
						view, 
						listAdapter,
						(Record) listAdapter.getItem(position)
				);
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
		protected void preDeleteAllAction() {
			
			int iter = 0;
			for (Object record : innerCollection) {
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

		@Override
		protected void DeleteAction() {
			
			MenuActions.deleteCalls(recordIds, recordPaths);
			
			ArrayList<Object> toBeProcessed = new ArrayList<Object>();
			
			for(Object item : innerCollection) {
				Record lcRecord = (Record) item;
				
				if(lcRecord.isChecked()) {					
					toBeProcessed.add(lcRecord);			
				}					
			}
			
			for(Object item : toBeProcessed) {
				((ObjectsAdapter)listAdapter).remove((Record) item);
				((ArrayList<Object>)innerCollection).remove((Record) item);
			}
			
			((ObjectsAdapter)listAdapter).notifyDataSetChanged();		
		}

		@Override
		protected boolean itemChecked(Object item) {
			return ((Record) item).isChecked();
		}

		@Override
		protected int innerCollectionSorting(Object first, Object second) {
			return ((Record) first).getmHtime().compareToIgnoreCase(
					((Record) second).getmHtime());
		}

		@Override
		protected void initAdapter(Context context, List<Object> collection,
				int layoutId, boolean multiSelectMode) {
			listAdapter = null;
			listAdapter = new ObjectsAdapter(context, collection, layoutId, multiSelectMode);
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
