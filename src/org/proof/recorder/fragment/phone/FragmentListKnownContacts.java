package org.proof.recorder.fragment.phone;

import java.io.InputStream;
import java.util.ArrayList;

import org.proof.recorder.R;
import org.proof.recorder.bases.fragment.ProofFragment;
import org.proof.recorder.bases.fragment.ProofListFragmentWithAsyncLoader;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.fragment.contacts.utils.ContactsDataHelper;
import org.proof.recorder.utils.MenuActions;
import org.proof.recorder.utils.Log.Console;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentListKnownContacts extends ProofFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}	

	public static class KnownContactsLoader extends ProofListFragmentWithAsyncLoader
	{		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);				
			startAsyncLoader();
		}		
		
		private void getContacts() {
			try {
				objects = (ArrayList<Object>) ContactsDataHelper.getCallsFoldersOfKnown(getActivity());
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
					view = vi.inflate(R.layout.fragment_listrecord_dossiers_detail, null);
				}
				Contact mContact = (Contact) items.get(position);
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
						Console.print_debug("Image is read");

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
		 public void onListItemClick(ListView l, final View v, int position,
		 long id) {
		
			 super.onListItemClick(l, v, position, id);	
			
			Contact mContact = (Contact) objects.get(position);
			
			MenuActions.displayCallsFolderDetails(mContact.getPhoneNumber(), "phone", getActivity());
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
					R.layout.fragment_listrecord_dossiers_detail, objects);			
		}

		@Override
		protected Long _doInBackground(Void... params) {
			getContacts();
			return null;
		}

		@Override
		protected int collectionSorter(Object object1, Object object2) {
			Contact c1 = (Contact) object1;
			Contact c2 = (Contact) object2;
			return c1.getContactName().compareToIgnoreCase(c2.getContactName());
		}

	}

}
