package org.proof.recorder.fragment.contacts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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

import org.proof.recorder.R;
import org.proof.recorder.bases.broadcast.ProofBroadcastReceiver;
import org.proof.recorder.bases.fragment.ProofListFragmentWithAsyncLoader;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.fragment.contacts.utils.ContactsDataHelper;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.Log.Console;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FragmentListExcludedContacts extends Fragment {

	@SuppressWarnings("EmptyMethod")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static class ExcludedContactsLoader extends ProofListFragmentWithAsyncLoader {

		private final SparseBooleanArray selectedContacts = new SparseBooleanArray();

		private void sendEventToPhoneList(Contact c) {

			Console.print_debug("Broadcasting message");

			Intent intent = new Intent("eventOnContactExcludedAdded");
			// You can also include some extra data.
			intent.putExtra("message", "Contact: " + c
					+ " added to phone List!");
			intent.putExtra("contactObj", c);
			LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(
					intent);
		}

		private final ProofBroadcastReceiver eventListPhoneReceiver = new ProofBroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				
				super.onReceive(context, intent);
				
				// Get extra data included in the Intent
				String message = intent.getStringExtra("message");

				Console.print_debug("Got message: " + message);

				try {
					Contact contactObj = (Contact) intent
							.getSerializableExtra("contactObj");
					ContactAdapter listAdapter = (ContactAdapter) getListAdapter();
					listAdapter.add(contactObj);
					Collections.sort(objects, new Comparator<Object>() {
						@Override
						public int compare(Object s1, Object s2) {
							return ((Contact) s1).getContactName().compareToIgnoreCase(
									((Contact) s2).getContactName());
						}
					});
					listAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					Console.print_exception(e);
				}
			}
		};

		private void getContacts() {
			try {
				objects = ContactsDataHelper.getExcludedList(getActivity());
			} catch (Exception e) {
				Console.print_exception(e);
				objects = new ArrayList<Object>();
			}
		}

		public class ContactAdapter extends ArrayAdapter<Object> {

			private final ArrayList<Object> items;

			public ContactAdapter(Context context,
								  ArrayList<Object> items) {
				super(context, R.layout.custom_contacts_list, items);
				this.items = items;
			}

			@SuppressWarnings("EmptyMethod")
			@Override
			public void notifyDataSetChanged() {
				super.notifyDataSetChanged();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = convertView;
				if (view == null) {
					LayoutInflater vi = (LayoutInflater) getActivity()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = vi.inflate(R.layout.custom_contacts_list, parent);
				}
				Contact contact = (Contact) items.get(position);
				if (contact != null) {
					CheckBox nameCheckBox = (CheckBox) view
							.findViewById(R.id.checkBox);

					nameCheckBox.setChecked(selectedContacts.get(position));

					nameCheckBox.setText(contact.getContactName());

					nameCheckBox.setOnClickListener(new OnItemClickListener(
							position, nameCheckBox.getText(), nameCheckBox));
				}

				return view;
			}

		}

		private class OnItemClickListener implements OnClickListener {
			private final int position;
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

				ContactAdapter contactAdapter = (ContactAdapter) getListAdapter();
				Contact contact = (Contact) objects.get(position);

				Uri uri = Uri.withAppendedPath(
						PersonnalProofContentProvider.CONTENT_URI,
						"excluded_contact_id/" + contact.getId());
				try {
					getActivity().getApplicationContext().getContentResolver()
							.delete(uri, null, null);

					sendEventToPhoneList(contact);

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
					eventListPhoneReceiver,
					new IntentFilter("eventOnContactAdded"));
			
			startAsyncLoader();
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			LocalBroadcastManager.getInstance(getActivity())
					.unregisterReceiver(eventListPhoneReceiver);		
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
					objects);
		}

		@Override
		protected void _doInBackground(Void... params) {
			getContacts();
		}

		@Override
		protected int collectionSorter(Object object1, Object object2) {
			// TODO Auto-generated method stub
			return ((Contact) object1).getContactName().compareToIgnoreCase(
					((Contact) object2).getContactName());
		}

		@Override
		protected void alertDlgCancelAction(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void alertDlgOkAction(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void handleOnReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
		}
	}
}
