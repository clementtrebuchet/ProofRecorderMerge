package org.proof.recorder.fragment.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.fragment.contacts.utils.ContactsDataHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

public class FragmentListPhoneContacts extends Fragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static class PhoneContactsLoader extends ListFragment {
		
		private final static String TAG = "FragmentListPhoneContacts";
		private static ArrayList<Contact> contacts = null;		
		private static ContactAdapter contactAdapter = null;
		private static LoadContactsList async;
		private SparseBooleanArray selectedContacts = new SparseBooleanArray();
		
		private void sendEventToExcludedList(Contact c) {
			if(Settings.isDebug())
			  Log.d("sender", "Broadcasting message");
			
			  Intent intent = new Intent("eventOnContactAdded");
			  // You can also include some extra data.
			  intent.putExtra("message", "Contact: " + c + " added to excluded List!");
			  intent.putExtra("contactObj", c);
			  LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
			}
		
		private BroadcastReceiver eventListExcludedReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// Get extra data included in the Intent
				String message = intent.getStringExtra("message");
				
				if(Settings.isDebug())
					Log.d(TAG + " Receiver", "Got message: " + message);
				
				try
				{
					Contact contactObj = (Contact) intent.getSerializableExtra("contactObj");
					ContactAdapter c = (ContactAdapter) getListAdapter();
					c.add(contactObj);
					Collections.sort(contacts, new Comparator<Contact>() {
				        @Override
				        public int compare(Contact s1, Contact s2) {
				            return s1.getContactName().compareToIgnoreCase(s2.getContactName());
				        }
				    });
					c.notifyDataSetChanged();
				}
				catch (Exception e) {
					
					if(Settings.isDebug())
						Log.e(TAG + " Receiver", "Got Error: " + e.getMessage());
				}
			}
		};
		
		@Override
		public void onDestroy() {
			LocalBroadcastManager.getInstance(getActivity())
					.unregisterReceiver(eventListExcludedReceiver);
			
			if(!async.getStatus().equals("FINISHED"))
				async.cancel(true);
			
			super.onDestroy();
		}

		public void getContacts() {
			try {		
				
				contacts = ContactsDataHelper.getPhoneList(getActivity());				
								
				
			} catch (Exception e) {
				
				if(Settings.isDebug())
					Log.e(TAG, "E" + e.getMessage());
			}
		}
		
		private class LoadContactsList extends AsyncTask<Void, Integer, Long> {

			@Override
		    protected void onPreExecute() {
		        super.onPreExecute();
		    }

			 
			 @Override
			    protected void onProgressUpdate(Integer... values){
			        super.onProgressUpdate(values);
			    }
			 

			 @Override
		     protected void onPostExecute(Long result) {
				 contactAdapter = new ContactAdapter(getActivity(),
							R.layout.custom_contacts_list, contacts);	
					setListAdapter(contactAdapter);
				((ContactAdapter) getListAdapter()).notifyDataSetChanged();
		     }

			@Override
			protected Long doInBackground(Void... params) {
				getContacts();					
				return null;
			}
		 }

		public class ContactAdapter extends ArrayAdapter<Contact> {

			private ArrayList<Contact> items;

			public ContactAdapter(Context context, int textViewResourceId,
					ArrayList<Contact> items) {
				super(context, textViewResourceId, items);
				this.items = items;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = convertView;
				if (view == null) {
					LayoutInflater vi = (LayoutInflater) getActivity()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = vi.inflate(R.layout.custom_contacts_list, null);
				}
				Contact contact = items.get(position);
				if (contact != null) {
					CheckBox nameCheckBox = (CheckBox) view
							.findViewById(R.id.checkBox);

					nameCheckBox.setChecked(selectedContacts.get(position));

					if (nameCheckBox != null) {
						nameCheckBox.setText(contact.getContactName());
					}

					nameCheckBox.setOnClickListener(new OnItemClickListener(
							position, nameCheckBox.getText(), nameCheckBox));
				}

				return view;
			}

		}

		private class OnItemClickListener implements OnClickListener {
			private int position;
			// private CharSequence text;
			@SuppressWarnings("unused")
			private CheckBox checkBox;

			OnItemClickListener(int position, CharSequence text,
					CheckBox checkBox) {
				this.position = position;
				// this.text = text;
				this.setCheckBox(checkBox);
			}

			@Override
			public void onClick(View arg0) {

				selectedContacts.append(position, true);
				
				Contact.setResolver(
						getActivity().getApplicationContext().getContentResolver());
				
				ContactAdapter ca = (ContactAdapter) getListAdapter();				
				Contact c = ca.getItem(position);				

				try {
					
					c.save();					
					sendEventToExcludedList(c);
					
					ca.remove(c);
					selectedContacts.delete(position);
					ca.notifyDataSetChanged();

				} catch (IllegalArgumentException e) {
					
					if(Settings.isDebug())
						Log.e(TAG, c + "->DETAILS: " + e.getMessage());
				}
			}

			public void setCheckBox(CheckBox checkBox) {
				this.checkBox = checkBox;
			}
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);	
			LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
					eventListExcludedReceiver, new IntentFilter("eventOnContactExcludedAdded"));				
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return super.onCreateView(inflater, container, savedInstanceState);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);	
			async = new LoadContactsList();
			async.execute();
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);				
		}
	}
}