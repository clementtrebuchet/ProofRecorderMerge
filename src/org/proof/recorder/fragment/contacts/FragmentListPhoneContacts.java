package org.proof.recorder.fragment.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.proof.recorder.R;
import org.proof.recorder.bases.broadcast.ProofBroadcastReceiver;
import org.proof.recorder.bases.fragment.ProofListFragmentWithAsyncLoader;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.fragment.contacts.utils.ContactsDataHelper;
import org.proof.recorder.utils.Log.Console;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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

	public static class PhoneContactsLoader extends ProofListFragmentWithAsyncLoader {

		private SparseBooleanArray selectedContacts = new SparseBooleanArray();
		
		private void sendEventToExcludedList(Contact c) {
			Console.print_debug("Broadcasting message");
			
			  Intent intent = new Intent("eventOnContactAdded");
			  // You can also include some extra data.
			  intent.putExtra("message", "Contact: " + c + " added to excluded List!");
			  intent.putExtra("contactObj", c);
			  LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
			}
		
		private ProofBroadcastReceiver eventListExcludedReceiver = new ProofBroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// Get extra data included in the Intent
				String message = intent.getStringExtra("message");
				
				Console.print_debug("Got message: " + message);
				
				try
				{
					Contact contactObj = (Contact) intent.getSerializableExtra("contactObj");
					ContactAdapter c = (ContactAdapter) getListAdapter();
					c.add(contactObj);
					Collections.sort(objects, new Comparator<Object>() {
				        @Override
				        public int compare(Object s1, Object s2) {
				            return ((Contact) s1).getContactName().compareToIgnoreCase(((Contact) s2).getContactName());
				        }
				    });
					c.notifyDataSetChanged();
				}
				catch (Exception e) {					
					Console.print_exception(e);
				}
			}
		};

		public void getContacts() {
			try {				
				objects = ContactsDataHelper.getPhoneList(getActivity());				
			} catch (Exception e) {
				Console.print_exception(e);
			}
		}

		public class ContactAdapter extends ArrayAdapter<Object> {

			private ArrayList<Object> items;

			public ContactAdapter(Context context, int textViewResourceId,
					ArrayList<Object> items) {
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
				Contact contact = (Contact) items.get(position);
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
				
				ContactAdapter contactAdapter = (ContactAdapter) getListAdapter();				
				Contact contact = (Contact) objects.get(position);				

				try {
					
					contact.save();					
					sendEventToExcludedList(contact);
					
					contactAdapter.remove(contact);
					objects.remove(contact);
					selectedContacts.delete(position);
					contactAdapter.notifyDataSetChanged();

				} catch (IllegalArgumentException e) {					
					Console.print_exception(e);	
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
			
			startAsyncLoader();
		}
		
		@Override
		public void onDestroy() {
			super.onDestroy();
			LocalBroadcastManager.getInstance(getActivity())
					.unregisterReceiver(eventListExcludedReceiver);		
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
			listAdapter = new ContactAdapter(getActivity(),
					R.layout.custom_contacts_list, objects);			
		}

		@Override
		protected Long _doInBackground(Void... params) {
			getContacts();
			return null;
		}

		@Override
		protected int collectionSorter(Object object1, Object object2) {
			// TODO Auto-generated method stub
			return ((Contact) object1).getContactName().compareToIgnoreCase(
					((Contact) object2).getContactName());
		}
	}
}