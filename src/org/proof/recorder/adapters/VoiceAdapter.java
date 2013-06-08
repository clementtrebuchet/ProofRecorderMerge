package org.proof.recorder.adapters;

import java.util.List;

import org.proof.recorder.R;
import org.proof.recorder.bases.adapter.ProofBaseMultiSelectListAdapter;
import org.proof.recorder.database.models.Voice;
import org.proof.recorder.utils.Log.Console;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class VoiceAdapter extends ProofBaseMultiSelectListAdapter {

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
	public VoiceAdapter(Context context, int resource,
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
	public VoiceAdapter(Context context, int resource,
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
	public VoiceAdapter(Context context, List<Object> objects, int layoutResourceId,
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
	public VoiceAdapter(Context context, Object[] objects, int layoutResourceId,
			boolean multiModeEnabled) {
		super(context, objects, layoutResourceId, multiModeEnabled);
	}

	@Override
	protected void handleView(final int position, View view) {
		
		final Voice voice = (Voice) getObjects().get(position);
		
		if (voice != null) {
			
			CheckBox checkbox = (CheckBox) view.findViewById(R.id.cb_select_item);
			
			TextView fileSize = (TextView) view.findViewById(R.id.sens);
			TextView humanTime = (TextView) view.findViewById(R.id.timehumanreadable);
			TextView title = (TextView) view.findViewById(R.id.number);
			TextView id = (TextView) view.findViewById(R.id.idrecord);
		    
			ImageView icon = (ImageView) view.findViewById(R.id.list_image);
			ImageView arrow = (ImageView) view.findViewById(R.id.arrow_record_detail);
			
			
			Bitmap defaultBite = BitmapFactory.decodeResource(
					getContext().getResources(), R.drawable.mic_48);			
			
			id.setVisibility(View.INVISIBLE);
			
			title.setText(voice.getNote().getTitle());
			fileSize.setText(voice.getHumanFileSize());
			humanTime.setText(voice.getHumanTime());			
			icon.setImageBitmap(defaultBite);

			if (this.multiModeEnabled) {
				
				arrow.setVisibility(View.INVISIBLE);
				
				checkbox.setVisibility(View.VISIBLE);
				
				checkbox.setChecked(voice.isChecked());
				
				checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						voice.setChecked(isChecked);
						sendEvent();
					}
				});
				
			} else {				
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
	protected void handleEvenetIntent(Intent intent) {
		// TODO Auto-generated method stub
		
	}
}
