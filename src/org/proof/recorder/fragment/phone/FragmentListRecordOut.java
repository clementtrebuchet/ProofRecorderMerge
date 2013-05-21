package org.proof.recorder.fragment.phone;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.bases.broadcast.ProofBroadcastReceiver;
import org.proof.recorder.bases.fragment.ProofFragment;
import org.proof.recorder.bases.fragment.ProofListFragmentWithQuickAction;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.fragment.contacts.utils.ContactsDataHelper;
import org.proof.recorder.utils.MenuActions;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.StaticIntents;
import org.proof.recorder.utils.Log.Console;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentListRecordOut extends ProofFragment {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static class OutGoingCallsLoader extends ProofListFragmentWithQuickAction
	{
		boolean mDualPane;
		int mCursorPos = -1;
		private static Bundle mBundle;		

		// ArrayList<Contact>() Variables

		private static ArrayList<Record> records = null;
		private static OutGoingCallsAdapter recordsAdapter = null;
		private static Runnable viewRecords = null;

		private void sendEventToFolderList() {
			if(Settings.isDebug())
				Console.print_debug("Broadcast: eventListNeedFolderRefreshReceiver (out)");

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

			setHasOptionsMenu(true);

			LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
					eventListNeedFolderRefreshReceiver, new IntentFilter("eventOutNeedToBeRefreshedReceiver"));

			MenuActions.setmContext(getActivity());			
			mBundle = getActivity().getIntent().getExtras();

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

		/* Options Menu */

		@Override
		public boolean onOptionsItemSelected(
				com.actionbarsherlock.view.MenuItem item) {
			
			boolean result = false;
			if(item.getItemId() == R.id.cm_records_list_del_file) {
				result = super.onOptionsItemSelected(item);

				recordsAdapter.clear();
				initOnActivityCreated();
			}
			return result;
		}

		/* End of Options Menu */

		private void getContacts() {
			try {
				String mIdOrTelephone = mBundle.getString("mIdOrTelephone");
				String mWhere = mBundle.getString("mWhereClause");
				records = ContactsDataHelper.getOutGoingCalls(getActivity(), mWhere, mIdOrTelephone);
			} catch (Exception e) {				
				Console.print_exception(e);
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
			public View getView(final int position, View convertView, ViewGroup parent) {
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
					mId.setVisibility(TextView.INVISIBLE);

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

					if (input != null) {
						Bitmap bitmap = BitmapFactory.decodeStream(input);
						imageView.setImageBitmap(bitmap);
					}

					phTxt.setText(origPhone);
					mHtime.setText(mRecord.getmHtime());

					CheckBox checkbox = (CheckBox) view.findViewById(R.id.cb_select_item);
					ImageView arrow = (ImageView) view.findViewById(R.id.arrow_record_detail);

					if(isMulti) {					
						arrow.setVisibility(ImageView.INVISIBLE);

						checkbox.setVisibility(CheckBox.VISIBLE);
						checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@TargetApi(Build.VERSION_CODES.HONEYCOMB)
							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								if(isChecked) {
									Console.print_debug("Count of checked items: " + position);
									Console.print_debug("Item info: " + records.get(position));

									if(!selectedItems.contains(position)) {
										selectedItems.add(position);
									}									
								}
								else {
									if(selectedItems.contains(position)) {
										selectedItems.remove(position);
									}
								}
							}
						});
					}
					else {
						checkbox.setVisibility(CheckBox.INVISIBLE);
						arrow.setVisibility(ImageView.VISIBLE);
					}								
				}
				return view;
			}

		}		

		@Override
		protected boolean handleActionMode(int itemId) {

			if(!selectedItems.isEmpty()) {

				List<Record> recordsToProcess = new ArrayList<Record>();

				for(int pos : selectedItems) {
					recordsToProcess.add(records.get(pos));
					Console.print_debug("Position: " + pos + " - " + records.get(pos));
				}

				switch (itemId) {

				case DELETE_ALL:
					break;

				case DELETE:
					break;		

				case SHARE:
					break;

				default:
					break;

				}
				return true;
			}			
			return false;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			initOnActivityCreated();			
		}

		@Override
		protected void initOnActivityCreated() {
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

			if(!isMulti) {
				OutGoingCallsAdapter outGoingCallsAdapter = (OutGoingCallsAdapter) getListAdapter();				
				Record mRecord = outGoingCallsAdapter.getItem(position);

				QuickActionDlg.showPhoneOptionsDlg(getActivity(), v, outGoingCallsAdapter, null, mRecord);

				if(recordsAdapter.getCount() == 0)
					sendEventToFolderList();
			}
			else {
				CheckBox checkbox = (CheckBox) view.findViewById(R.id.cb_select_item);
				checkbox.toggle();
			}
		}
	}
}
