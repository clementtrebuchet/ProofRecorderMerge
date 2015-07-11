package org.proof.recorder.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import org.proof.recorder.R;
import org.proof.recorder.bases.adapter.ProofBaseMultiSelectListAdapter;
import org.proof.recorder.database.models.Voice;
import org.proof.recorder.utils.ApproxRecordTime;
import org.proof.recorder.utils.Log.Console;

import java.io.File;
import java.util.List;

@SuppressWarnings("unused")
public class VoiceAdapter extends ProofBaseMultiSelectListAdapter {

	

	/**
	 * @param context the context
	 * @param resource the ressource
	 * @param textViewResourceId the view id
	 * @param objects the object
	 * @param layoutResourceId the layout ressource id
	 * @param multiModeEnabled is multi enable ?
	 */
	public VoiceAdapter(Context context, int resource,
			int textViewResourceId, List<Object> objects,
			int layoutResourceId, boolean multiModeEnabled, String broadcastName) {
		super(context, resource, textViewResourceId, objects, layoutResourceId, multiModeEnabled, broadcastName);
	}

	/**
	 * @param context the context
	 * @param resource the ressource
	 * @param textViewResourceId the view id
	 * @param objects the object
	 * @param layoutResourceId the layout ressource id
	 * @param multiModeEnabled is multi enable ?
	 */
	public VoiceAdapter(Context context, int resource,
			int textViewResourceId, Object[] objects, int layoutResourceId,
			boolean multiModeEnabled, String broadcastName) {
		super(context, resource, textViewResourceId, objects, layoutResourceId, multiModeEnabled, broadcastName);
	}

	/**
	 * @param context the context
	 * @param objects the object
	 * @param layoutResourceId the layout ressource id
	 * @param multiModeEnabled is multi enable ?
	 */
	public VoiceAdapter(Context context, List<Object> objects, int layoutResourceId,
			boolean multiModeEnabled, String broadcastName) {
		super(context, objects, layoutResourceId, multiModeEnabled, broadcastName);
	}

	/**
	 * @param context the context
	 * @param objects the object
	 * @param layoutResourceId the layout ressource id
	 * @param multiModeEnabled is multi enable ?
	 */
	public VoiceAdapter(Context context, Object[] objects, int layoutResourceId,
			boolean multiModeEnabled, String broadcastName) {
		super(context, objects, layoutResourceId, multiModeEnabled, broadcastName);
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
			ImageView format = (ImageView) view.findViewById(R.id.format_image);
			
			Bitmap defaultBite = BitmapFactory.decodeResource(
					getContext().getResources(), R.drawable.mic_48);			
			
			id.setVisibility(View.INVISIBLE);
			
			title.setText(voice.getNote().getTitle());
			fileSize.setText(voice.getHumanFileSize());
			humanTime.setText(voice.getHumanTime());			
			icon.setImageBitmap(defaultBite);
			try {
				TextView durationTxt = (TextView) view
						.findViewById(R.id.songtime);
				File g = new File(voice.getFilePath());
				ApproxRecordTime f = new ApproxRecordTime(g);
				durationTxt.setText("" + f.run());
				picturesSongFormat(f.getmFormat(), format);
			} catch (Exception e) {
				Console.print_exception("" + e.getMessage());
			}

			if (this.multiModeEnabled) {
				
				arrow.setVisibility(View.INVISIBLE);
				
				checkbox.setVisibility(View.VISIBLE);
				
				checkbox.setChecked(voice.isChecked());

				//noinspection unused
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
	protected void handleEventIntent(Intent intent) {
		// TODO Auto-generated method stub
		
	}
	private void picturesSongFormat(String r, ImageView v){
		Bitmap defaultBite;
		if (r.equalsIgnoreCase("wav")) {
			defaultBite = BitmapFactory.decodeResource(
					getContext().getResources(), R.drawable.plug_wav);
		} else if (r.equalsIgnoreCase("mp3")) {
			defaultBite = BitmapFactory.decodeResource(
					getContext().getResources(), R.drawable.plug_mp3);
		} else if (r.equalsIgnoreCase("ogg")) {
			defaultBite = BitmapFactory.decodeResource(
					getContext().getResources(), R.drawable.plug_ogg);
		} else if (r.equalsIgnoreCase("3gp")) {
			defaultBite = BitmapFactory.decodeResource(
					getContext().getResources(), R.drawable.plug_3gp);
		} else {
			defaultBite = BitmapFactory.decodeResource(
					getContext().getResources(), R.drawable.navigationrefresh);
		}
		
		v.setImageBitmap(defaultBite);
	}
}
