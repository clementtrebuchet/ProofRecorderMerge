package org.proof.recorder.fragment.phone;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.bases.broadcast.ProofBroadcastReceiver;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.fragment.contacts.utils.ContactsDataHelper;
import org.proof.recorder.utils.MenuActions;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.StaticIntents;

import com.actionbarsherlock.app.SherlockFragment;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
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

public class FragmentListRecordOut extends SherlockFragment {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static class OutGoingCallsLoader extends ListFragment
	{
		private static final String TAG = "FragmentPhoneCallDossier";
		boolean mDualPane;
		int mCursorPos = -1;
		private static Bundle b;
		
		// ArrayList<Contact>() Variables
		
		private static ArrayList<Record> records = null;
		private static OutGoingCallsAdapter recordsAdapter = null;
		private static Runnable viewRecords = null;
		
		private void sendEventToFolderList() {
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
		};
		
		@Override
		public void onDestroy() {
			LocalBroadcastManager.getInstance(getActivity())
					.unregisterReceiver(eventListNeedFolderRefreshReceiver);
			super.onDestroy();
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);			
			LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
					eventListNeedFolderRefreshReceiver, new IntentFilter("eventOutNeedToBeRefreshedReceiver"));
			
			MenuActions.setmContext(getActivity());			
			b = getActivity().getIntent().getExtras();
			
			setRetainInstance(true);
			viewRecords = new Runnable() {
				@Override
				public void run() {
					getContacts();
				}
			};					
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
					getString(R.string.cm_records_list_del_file_txt));
			menu.add(Menu.NONE, R.id.cm_records_list_read_wav, Menu.NONE,
					getString(R.string.cm_records_list_read_wav_txt));
			menu.add(Menu.NONE, R.id.cm_records_list_display_details,
					Menu.NONE, getString(R.string.cm_records_list_details_txt));
			menu.add(Menu.NONE, R.id.cm_records_list_display_sharing_opts,
					Menu.NONE, getString(R.string.cm_records_list_sharing_opts_txt));

			super.onCreateContextMenu(menu, v, menuInfo);
		}

		@Override
		public boolean onContextItemSelected(MenuItem item) {

			AdapterContextMenuInfo record = (AdapterContextMenuInfo) item
					.getMenuInfo();
			int recordPosition = record.position;				
			Record mRecord = recordsAdapter.getItem(recordPosition);		
			
			if(Settings.isDebug())
				Log.v(TAG, "" + recordPosition);

			if (item.getItemId() == R.id.cm_records_list_del_file) {
				if(Settings.isDebug())
					Log.i("ContextMenu", "Suppressed Item");
				MenuActions.deleteItem(
						mRecord.getmId(),
						Settings.mType.CALL, 
						null,
						recordsAdapter, 
						null, 
						mRecord
				);
				return true;
			} else if (item.getItemId() == R.id.cm_records_list_read_wav) {
				MenuActions.readPhone(mRecord.getmFilePath());
				if(Settings.isDebug())
					Log.i("ContextMenu", "Read Item");
				return true;
			} else if (item.getItemId() == R.id.cm_records_list_display_details) {
				if(Settings.isDebug())
					Log.i("ContextMenu", "Display Item's details");
				MenuActions.displayItemPhoneDetails(mRecord.getmId());
				return true;
			} else if (item.getItemId() == R.id.cm_records_list_display_sharing_opts) {
				if(Settings.isDebug())
					Log.i("ContextMenu", "Sharing Options Item");
				String[] mDatas = new String[] {
						mRecord.getmFilePath()
				};
				MenuActions.sharingOptions(mDatas);
				return true;
			}
			return super.onContextItemSelected(item);
		}	
		
		private void getContacts() {
			try {
				String mIdOrTelephone = b.getString("mIdOrTelephone");
				String mWhere = b.getString("mWhereClause");
				records = ContactsDataHelper.getOutGoingCalls(getActivity(), mWhere, mIdOrTelephone);
			} catch (Exception e) {
				
				if(Settings.isDebug())
					Log.e(TAG, "E" + e.getMessage());
			}
		}

		public class OutGoingCallsAdapter extends ArrayAdapter<Record> {

			private ArrayList<Record> items;

			public OutGoingCallsAdapter(Context context, int textViewResourceId,
					ArrayList<Record> items) {
				super(context, textViewResourceId, items);
				this.items = items;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = convertView;
				if (view == null) {
					LayoutInflater vi = (LayoutInflater) getActivity()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = vi.inflate(R.layout.listfragmentdroit, null);
				}
				Record mRecord = items.get(position);
				if (mRecord != null) {
					
					String origPhone = mRecord.getmPhone();
					
					Contact mContact = AndroidContactsHelper.getContactInfosByNumber(
							getActivity(), origPhone);

					TextView phTxt = (TextView) view.findViewById(R.id.number);
					
					TextView mHtime = (TextView) view.findViewById(R.id.timehumanreadable);

					TextView mId = (TextView) view.findViewById(R.id.idrecord);
					mId.setVisibility(View.INVISIBLE);

					ImageView imageView = (ImageView) view.findViewById(R.id.list_image);
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

					phTxt.setText(origPhone);
					mHtime.setText(mRecord.getmHtime());
								
				}
				return view;
			}

		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			getActivity().runOnUiThread(viewRecords);
			
			try {
				Collections.sort(records, new Comparator<Record>() {
			        @Override
			        public int compare(Record s1, Record s2) {
			            return s1.getmHtime().compareToIgnoreCase(s2.getmHtime());
			        }
			    });
				
				recordsAdapter = new OutGoingCallsAdapter(getActivity(),
						R.layout.listfragmentdroit, records);
				
				setListAdapter(recordsAdapter);
			}
			catch(Exception e) {
				setEmptyText("Aucun Enregistrements d'appels");
			}		
			
			if(getListView().getCount() > 0) {
				registerForContextMenu(getListView());
			}
		}

		 @Override
		 public void onListItemClick(ListView l, final View v, int position,
		 long id) {
		
			 super.onListItemClick(l, v, position, id);	
			 
			OutGoingCallsAdapter outGoingCallsAdapter = (OutGoingCallsAdapter) getListAdapter();				
			Record mRecord = outGoingCallsAdapter.getItem(position);
			
			QuickActionDlg.showPhoneOptionsDlg(getActivity(), v, outGoingCallsAdapter, null, mRecord);
			
			if(recordsAdapter.getCount() == 0)
				sendEventToFolderList();
			 
			 if(Settings.isDebug())
				 Log.v("MA_LISTE_DE_MERDE_AT :", "" + position + "(telephone : " + mRecord.getmPhone() +")");		

		 }

	}

}
