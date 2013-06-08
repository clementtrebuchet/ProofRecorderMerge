package org.proof.recorder.adapters;

import java.io.InputStream;
import java.util.List;

import org.proof.recorder.R;
import org.proof.recorder.bases.adapter.ProofBaseMultiSelectListAdapter;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.utils.Log.Console;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ContactAdapter extends ProofBaseMultiSelectListAdapter {

	public ContactAdapter(Context context, List<Object> collection, int layoutResourceId,
			boolean multiModeEnabled) {
		super(context, collection, layoutResourceId, multiModeEnabled);
	}

	@Override
	protected void handleView(final int position, View view) {
		
		final Contact contact = (Contact) getObjects().get(position);		
		
		if (contact != null) {	
			
			Bitmap photo;
			InputStream input = null;
			TextView id, phoneNumber, userName;
			ImageView imageView, recorderLogo;
			CheckBox checkbox;
			
			ContentResolver contentResolver;
			Uri uri;
			
			checkbox = (CheckBox) view.findViewById(R.id.cb_select_item);
			
			id = (TextView) view.findViewById(R.id.idrecordDossier);
			id.setVisibility(View.INVISIBLE);
			
			phoneNumber = (TextView) view.findViewById(R.id.numberDossier);
			userName = (TextView) view.findViewById(R.id.nomDossier);		

			imageView = (ImageView) view
					.findViewById(R.id.list_imageDossier);	
			
			recorderLogo = (ImageView) view.findViewById(R.id.arrow_record_detailDossier);

			if (contact.getLongContractId() != -1) {
				uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
						contact.getLongContractId());
				
				contentResolver = getContext().getContentResolver();
				input = ContactsContract.Contacts.openContactPhotoInputStream(
						contentResolver, uri);
			}			

			if (input != null) {
				photo = BitmapFactory.decodeStream(input);
			}
			else {
				photo = BitmapFactory.decodeResource(
						getContext().getResources(), R.drawable.telphone);
			}
			
			imageView.setImageBitmap(photo);
			userName.setText(contact.getContactName());
			phoneNumber.setText(contact.getPhoneNumber());	
			
			if(this.multiModeEnabled) {					
				recorderLogo.setVisibility(View.INVISIBLE);

				checkbox.setVisibility(View.VISIBLE);
				
				checkbox.setChecked(contact.isChecked());
				
				checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {						
						contact.setChecked(isChecked);
						sendEvent();
					}
				});
			}
			else {
				checkbox.setVisibility(View.INVISIBLE);
				recorderLogo.setVisibility(View.VISIBLE);
			}
		}		
	}

	@Override
	protected void handleEmptyView(int item, View view) {
		Console.print_debug("Empty list!");	
	}

	@Override
	protected void handleEvenetIntent(Intent intent) {
		// TODO Auto-generated method stub
		
	}

}
