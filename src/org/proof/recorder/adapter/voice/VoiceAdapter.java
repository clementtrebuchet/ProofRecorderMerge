package org.proof.recorder.adapter.voice;

import java.util.ArrayList;

import org.proof.recorder.R;
import org.proof.recorder.database.models.Voice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VoiceAdapter extends ArrayAdapter<Voice> {

	private ArrayList<Voice> items;
	private static Context context;

	public VoiceAdapter(Context context, int textViewResourceId,
			ArrayList<Voice> items) {
		super(context, textViewResourceId, items);
		VoiceAdapter.context = context;
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = convertView;
		if (view == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			
			view = vi.inflate(R.layout.listfragmentdroit, null);
		}
		
		if(items != null) {
			Voice voice = items.get(position);
			if (voice != null) {					
				
				TextView tvTitle = (TextView) view.findViewById(R.id.number);
				TextView tvId = (TextView) view.findViewById(R.id.idrecord);
				
				TextView tvFileSize = (TextView) view.findViewById(R.id.sens);
				TextView tvHumanTime = (TextView) view.findViewById(R.id.timehumanreadable);
				
				tvId.setVisibility(View.INVISIBLE);
				
				tvTitle.setText(voice.getNote().getTitle());
				tvFileSize.setText(voice.getHumanFileSize());
				tvHumanTime.setText(voice.getHumanTime());
				
				ImageView imageView = (ImageView) view.findViewById(R.id.list_image);
				Bitmap defaultBite = BitmapFactory.decodeResource(context.getResources(), R.drawable.mic_48);
				imageView.setImageBitmap(defaultBite);	
			}
		}
		
		return view;
	}
}
