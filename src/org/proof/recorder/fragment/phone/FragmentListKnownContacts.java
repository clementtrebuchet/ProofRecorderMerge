package org.proof.recorder.fragment.phone;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.fragment.contacts.utils.ContactsDataHelper;
import org.proof.recorder.utils.MenuActions;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class FragmentListKnownContacts extends SherlockFragment {
	
	//private final static String TAG = "FragmentListKnownContacts";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}	

	public static class KnownContactsLoader extends ListFragment
	{
		private static final String TAG = "FragmentPhoneCallDossier";
		boolean mDualPane;
		int mCursorPos = -1;
		
		// ArrayList<Contact>() Variables
		
		private static ArrayList<Contact> contacts = null;
		private static ContactAdapter contactAdapter = null;
		private static Runnable viewContacts = null;
		private static boolean uiOn = false;
		
		

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);				
			
			viewContacts = new Runnable() {
				@Override
				public void run() {
					uiOn = true;
					getContacts();
					uiOn = false;
				}
			};
			
			getActivity().runOnUiThread(viewContacts);			
			
			contactAdapter = new ContactAdapter(getActivity(),
					R.layout.fragment_listrecord_dossiers_detail, contacts);
		}

		/**
		 * Contextual Menu for displaying social and all :)
		 */

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return super.onCreateView(inflater, container, savedInstanceState);
		}

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {

			menu.add(Menu.NONE, R.id.cm_records_list_del_file, Menu.NONE,
					getString(R.string.cm_records_list_del_all_file_txt));
			menu.add(Menu.NONE, R.id.cm_records_list_display_details,
					Menu.NONE, getString(R.string.cm_records_list_display_txt));

			super.onCreateContextMenu(menu, v, menuInfo);
		}

		@Override
		public boolean onContextItemSelected(MenuItem item) {

			AdapterContextMenuInfo record = (AdapterContextMenuInfo) item
					.getMenuInfo();
			int recordPosition = record.position;	
			
			ContactAdapter ca = (ContactAdapter) getListAdapter();				
			Contact mContact = ca.getItem(recordPosition);
			
			if(Settings.isDebug())
				Log.v(TAG, "" + recordPosition);
			if (item.getItemId() == R.id.cm_records_list_del_file) {
				MenuActions.deleteContactsFolder(mContact, getActivity(), ca);
				return true;
			} else if (item.getItemId() == R.id.cm_records_list_display_details) {
				MenuActions.displayCallsFolderDetails(mContact.getContractId(), "android_id", getActivity());
				if(Settings.isDebug())
					Log.i("ContextMenu", "Display Item's details");
				return true;
			}
			return super.onContextItemSelected(item);
		}		
		
		private void getContacts() {
			try {
				contacts = ContactsDataHelper.getCallsFoldersOfKnown(getActivity());
				if(uiOn)
					getActivity().runOnUiThread(returnRes);
			} catch (Exception e) {
				
				if(Settings.isDebug())
					Log.e(TAG, "E" + e.getMessage());
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
					view = vi.inflate(R.layout.fragment_listrecord_dossiers_detail, null);
				}
				Contact mContact = items.get(position);
				if (mContact != null) {		
					
					TextView hideId;

					TextView phTxt = (TextView) view.findViewById(R.id.numberDossier);
					TextView nomUtilisateur = (TextView) view.findViewById(R.id.nomDossier);
					hideId = (TextView) view.findViewById(R.id.idrecordDossier);

					ImageView imageView = (ImageView) view
							.findViewById(R.id.list_imageDossier);
					Bitmap defaultBite = BitmapFactory.decodeResource(
							getActivity().getResources(), R.drawable.telphone);
					imageView.setImageBitmap(defaultBite);

					InputStream input = null;

					if (mContact.getLongContractId() != -1) {
						Uri uri = ContentUris.withAppendedId(
								ContactsContract.Contacts.CONTENT_URI,
								mContact.getLongContractId());
						ContentResolver cr = getActivity().getContentResolver();
						input = ContactsContract.Contacts.openContactPhotoInputStream(cr,
								uri);
					}

					if (input == null) {

					} else {
						if (Settings.isDebug())
							Log.v(TAG, "Image is read");

						Bitmap bitmap = BitmapFactory.decodeStream(input);
						imageView.setImageBitmap(bitmap);
					}

					phTxt.setText(mContact.getPhoneNumber());
					nomUtilisateur.setText(mContact.getContactName());
					hideId.setVisibility(View.INVISIBLE);
								
				}
				return view;
			}

		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			if(!contacts.isEmpty())
				Collections.sort(contacts, new Comparator<Contact>() {
			        @Override
			        public int compare(Contact s1, Contact s2) {
			            return s1.getContactName().compareToIgnoreCase(s2.getContactName());
			        }
			    });
			setListAdapter(contactAdapter);
			if(getListView().getCount() > 0)
				registerForContextMenu(getListView());
		}
		
		private Runnable returnRes = new Runnable() {

			@Override
			public void run() {
				
				((ContactAdapter) getListAdapter()).notifyDataSetChanged();
			}
		};

		 @Override
		 public void onListItemClick(ListView l, final View v, int position,
		 long id) {
		
			 super.onListItemClick(l, v, position, id);	
			 
			ContactAdapter ca = (ContactAdapter) getListAdapter();				
			Contact mContact = ca.getItem(position);
			
			MenuActions.displayCallsFolderDetails(mContact.getContractId(), "android_id", getActivity());
		 }

	}

}
