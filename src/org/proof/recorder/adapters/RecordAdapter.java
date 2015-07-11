package org.proof.recorder.adapters;

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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import org.proof.recorder.R;
import org.proof.recorder.bases.adapter.ProofBaseMultiSelectListAdapter;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.utils.Log.Console;

import java.io.InputStream;
import java.util.List;

@SuppressWarnings("unused")
public class RecordAdapter extends ProofBaseMultiSelectListAdapter {

	/**
	 * @param context the context
	 * @param resource the ressource
	 * @param textViewResourceId the view id
	 * @param objects object
	 * @param layoutResourceId the layer id
	 * @param multiModeEnabled is multi mode enabled ?
	 */
	public RecordAdapter(Context context, int resource,
			int textViewResourceId, List<Object> objects,
			int layoutResourceId, boolean multiModeEnabled, String broadcastName) {
		super(context, resource, textViewResourceId, objects, layoutResourceId, multiModeEnabled, broadcastName);
	}

	/**
	 * @param context the context
	 * @param resource the ressource
	 * @param textViewResourceId the view id
	 * @param objects object
	 * @param layoutResourceId the layer id
	 * @param multiModeEnabled is multi mode enabled ?
	 */
	public RecordAdapter(Context context, int resource,
			int textViewResourceId, Object[] objects, int layoutResourceId,
			boolean multiModeEnabled, String broadcastName) {
		super(context, resource, textViewResourceId, objects, layoutResourceId, multiModeEnabled, broadcastName);
	}

	/**
	 * @param context the context
	 * @param objects object
	 * @param layoutResourceId the layer id
	 * @param multiModeEnabled is multi mode enabled ?
	 */
	public RecordAdapter(Context context, List<Object> objects, int layoutResourceId,
			boolean multiModeEnabled, String broadcastName) {
		super(context, objects, layoutResourceId, multiModeEnabled, broadcastName);
	}

	/**
	 * @param context the context
	 * @param objects object
	 * @param layoutResourceId the layer id
	 * @param multiModeEnabled is multi mode enabled ?
	 */
	public RecordAdapter(Context context, Object[] objects, int layoutResourceId,
			boolean multiModeEnabled, String broadcastName) {
		super(context, objects, layoutResourceId, multiModeEnabled, broadcastName);
	}

	@Override
	protected void handleView(final int position, View view) {		
		
		final Record record = (Record) getObjects().get(position);
		
		if (record != null) {	
			
			CheckBox checkbox = (CheckBox) view.findViewById(R.id.cb_select_item);
			
			TextView phoneNumber = (TextView) view.findViewById(R.id.number);
			TextView humanTime = (TextView) view.findViewById(R.id.timehumanreadable);
			TextView id = (TextView) view.findViewById(R.id.idrecord);
			TextView songTime = (TextView) view.findViewById(R.id.songtime);
		    
			ImageView photo = (ImageView) view.findViewById(R.id.list_image);
			ImageView arrow = (ImageView) view.findViewById(R.id.arrow_record_detail);
			ImageView format = (ImageView) view.findViewById(R.id.format_image);

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

			id.setVisibility(View.INVISIBLE);			

			if (input != null) {
				Bitmap bitmap = BitmapFactory.decodeStream(input);
				photo.setImageBitmap(bitmap);
			}
			else {
				photo.setImageBitmap(defaultBite);
			}

			phoneNumber.setText(origPhone);
			humanTime.setText(record.getmHtime());
			
			// Duration of record
			songTime.setText(record.getmSongTime());
			
			// Setting format icon
			picturesSongFormat(record, format);
			
			if(this.multiModeEnabled) {					
				arrow.setVisibility(View.INVISIBLE);

				checkbox.setVisibility(View.VISIBLE);
				
				checkbox.setChecked(record.isChecked());

				//noinspection unused
				checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {						
						record.setChecked(isChecked);
						sendEvent();
					}
				});
			}
			else {
				checkbox.setVisibility(View.INVISIBLE);
				arrow.setVisibility(View.VISIBLE);
			}							
		}		
	}

	@Override
	protected void handleEmptyView(final int position, View convertView) {
		Console.print_debug("Empty list!");
	}

	@Override
	protected void handleEventIntent(Intent intent) {
		// TODO Auto-generated method stub		
	}
	
	private void picturesSongFormat(Record record, ImageView image) {
		
		String format = record.getFormat();
		
		Console.print_exception(String.format("Format: %s for record: %s", format, record));
		
		Bitmap icon;
		
		try {
		
			if(format.equalsIgnoreCase("wav")){
				icon = BitmapFactory.decodeResource(
						getContext().getResources(), R.drawable.plug_wav);
			} else if(format.equalsIgnoreCase("mp3")){
				icon = BitmapFactory.decodeResource(
						getContext().getResources(), R.drawable.plug_mp3);
			} else if(format.equalsIgnoreCase("ogg")){
				icon = BitmapFactory.decodeResource(
						getContext().getResources(), R.drawable.plug_ogg);
			} else if(format.equalsIgnoreCase("3gp")){
				icon = BitmapFactory.decodeResource(
						getContext().getResources(), R.drawable.plug_3gp);
			} else {
				icon = BitmapFactory.decodeResource(
						getContext().getResources(), R.drawable.navigationrefresh);
			}
			
			image.setImageBitmap(icon);
		
		}
		catch (Exception e) {
			Console.print_exception(e);
		}
	}
}
