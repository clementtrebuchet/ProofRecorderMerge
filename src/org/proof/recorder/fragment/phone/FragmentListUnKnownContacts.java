package org.proof.recorder.fragment.phone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import com.actionbarsherlock.view.MenuInflater;

import org.proof.recorder.R;
import org.proof.recorder.adapters.ContactAdapter;
import org.proof.recorder.bases.fragment.ProofFragment;
import org.proof.recorder.bases.fragment.ProofListFragmentWithQuickAction;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.fragment.contacts.utils.ContactsDataHelper;
import org.proof.recorder.utils.Log.Console;
import org.proof.recorder.utils.MenuActions;

import java.util.ArrayList;
import java.util.List;

public class FragmentListUnKnownContacts extends ProofFragment {

	/** Called when the activity is first created. */
	@SuppressWarnings("EmptyMethod")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static class UnKnownContactsLoader extends ProofListFragmentWithQuickAction
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
			
			startAsyncLoader();
		}

		private void getContacts() {
			try {
				objects = (ArrayList<Object>) ContactsDataHelper.getCallsFoldersOfUnKnown(getActivity());
			} catch (Exception e) {
				Console.print_exception(e);
			}
		}

		@Override
		public void onListItemClick(ListView list, final View view, int position, long id) {

			super.onListItemClick(list, view, position, id);	

			if(!multiSelectEnabled) {
				Contact mContact = (Contact) objects.get(position);
				MenuActions.displayCallsFolderDetails(mContact.getPhoneNumber(), getActivity());
			}
			else {
				CheckBox checkbox = (CheckBox) view.findViewById(R.id.cb_select_item);
				checkbox.toggle();
			}			 
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
					objects, R.layout.fragment_listrecord_dossiers_detail, multiSelectEnabled);			
		}

		@Override
		protected void _doInBackground(Void... params) {
			getContacts();
		}

		@Override
		protected int collectionSorter(Object object1, Object object2) {
			return ((Contact) object1).getsPhoneNumber().get_nationalNumber().compareToIgnoreCase(
					((Contact) object2).getsPhoneNumber().get_nationalNumber());
		}

		@Override
		protected void preDeleteAndShareAction() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		protected Intent ShareAction() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void DeleteAction() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void initOnOptionsItemSelected() {
			FragmentListRecordFoldersTabs.removeUnusedTab();			
		}

		@Override
		protected void DoneAction() {
			FragmentListRecordFoldersTabs.readdUnusedTab();			
		}

		@Override
		protected void DeleteAllAction() {
			FragmentListRecordFoldersTabs.removeCurrentTab(getInternalContext());			
		}

		@Override
		protected void uncheckItem(Object item) {
			((Contact) item).setChecked(false);		
		}

		@Override
		protected void toggleItem(Object item, boolean checked) {
			((Contact) item).setChecked(checked);			
		}

		@Override
		protected boolean itemChecked(Object item) {
			return ((Contact) item).isChecked();
		}

		@Override
		protected Object getItemClone(Object item) {
			return ((Contact) item).clone();
		}

		@Override
		protected void initAdapter(Context context, List<Object> collection,
				int layoutId, boolean multiSelectMode) {
			listAdapter = new ContactAdapter(context, collection, layoutId, multiSelectMode, getBroadcastName());						
		}

		@Override
		protected void alertDlgCancelAction(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu,
				MenuInflater inflater) {
		}

	}

}
