package org.proof.recorder.adapters;

import java.io.InputStream;
import java.util.List;

import org.proof.recorder.R;
import org.proof.recorder.bases.adapter.ProofBaseMultiSelectListAdapter;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.utils.Log.Console;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
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

public class RecordAdapter extends ProofBaseMultiSelectListAdapter {

	/**
	 * @param context
	 * @param resource
	 * @param textViewResourceId
	 * @param objects
	 * @param selectedObjects
	 * @param reflectClassName
	 * @param layoutResourceId
	 * @param multiModeEnabled
	 */
	public RecordAdapter(Context context, int resource,
			int textViewResourceId, List<Object> objects,
			int layoutResourceId, boolean multiModeEnabled) {
		super(context, resource, textViewResourceId, objects, layoutResourceId, multiModeEnabled);
	}

	/**
	 * @param context
	 * @param resource
	 * @param textViewResourceId
	 * @param objects
	 * @param selectedObjects
	 * @param reflectClassName
	 * @param layoutResourceId
	 * @param multiModeEnabled
	 */
	public RecordAdapter(Context context, int resource,
			int textViewResourceId, Object[] objects, int layoutResourceId,
			boolean multiModeEnabled) {
		super(context, resource, textViewResourceId, objects, layoutResourceId, multiModeEnabled);
	}

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 * @param selectedObjects
	 * @param reflectClassName
	 * @param layoutResourceId
	 * @param multiModeEnabled
	 */
	public RecordAdapter(Context context, List<Object> objects, int layoutResourceId,
			boolean multiModeEnabled) {
		super(context, objects, layoutResourceId, multiModeEnabled);
	}

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 * @param selectedObjects
	 * @param reflectClassName
	 * @param layoutResourceId
	 * @param multiModeEnabled
	 */
	public RecordAdapter(Context context, Object[] objects, int layoutResourceId,
			boolean multiModeEnabled) {
		super(context, objects, layoutResourceId, multiModeEnabled);
	}

	@Override
	protected void handleView(final int position, View view) {		
		
		final Record record = (Record) getObjects().get(position);
		
		if (record != null) {	
			
			CheckBox checkbox = (CheckBox) view.findViewById(R.id.cb_select_item);
			
			TextView phoneNumber = (TextView) view.findViewById(R.id.number);
			TextView humanTime = (TextView) view.findViewById(R.id.timehumanreadable);
			TextView id = (TextView) view.findViewById(R.id.idrecord);
		    
			ImageView photo = (ImageView) view.findViewById(R.id.list_image);
			ImageView arrow = (ImageView) view.findViewById(R.id.arrow_record_detail);

			String origPhone = record.getmPhone();

			Contact mContact = AndroidContactsHelper.getContactInfosByNumber(
					getContext(), origPhone);
			
			Bitmap defaultBite = BitmapFactory.decodeResource(
					getContext().getResources(), R.drawable.telphone);
			
			InputStream input = null;

			if (mContact.getLongContractId() != -1) {
				Uri uri = ContentUris.withAppendedId(
						ContactsContract.Contacts.CONTENT_URI,
						mContact.getLongContractId());
				ContentResolver cr = getContext().getContentResolver();
				input = ContactsContract.Contacts.openContactPhotoInputStream(cr,
						uri);
			}

			id.setVisibility(TextView.INVISIBLE);			

			if (input != null) {
				Bitmap bitmap = BitmapFactory.decodeStream(input);
				photo.setImageBitmap(bitmap);
			}
			else {
				photo.setImageBitmap(defaultBite);
			}

			phoneNumber.setText(origPhone);
			humanTime.setText(record.getmHtime());

			if(this.multiModeEnabled) {					
				arrow.setVisibility(ImageView.INVISIBLE);

				checkbox.setVisibility(CheckBox.VISIBLE);
				
				checkbox.setChecked(record.isChecked());
				
				checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {						
						record.setChecked(isChecked);
					}
				});
			}
			else {
				checkbox.setVisibility(CheckBox.INVISIBLE);
				arrow.setVisibility(ImageView.VISIBLE);
			}							
		}		
	}

	@Override
	protected void handleEmptyView(final int position, View convertView) {
		Console.print_debug("Empty list!");
	}
}
