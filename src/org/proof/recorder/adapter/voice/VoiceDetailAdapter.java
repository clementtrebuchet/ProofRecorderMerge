package org.proof.recorder.adapter.voice;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.utils.ServiceAudioHelper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class VoiceDetailAdapter extends SimpleCursorAdapter {

	private static final String TAG = "VoiceDetailAdpater";
	
	private static Context mcontext;
	private ImageView B;
	private String f;

	public VoiceDetailAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flag, int recId) {
		super(context, layout, c, from, to, flag);
		mcontext = context;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		/*
		 * Setup user photo 1 setup defaultBitmap 2 if photo, override
		 * defaultBitmap
		 */		
		
		super.bindView(view, context, cursor);
		
		String recordSize = cursor.getString(cursor
				.getColumnIndex(ProofDataBase.COLUMN_VOICE_TAILLE));
		
		TextView tailleTxt = (TextView) view.findViewById(R.id.mSizeOfFile);
		tailleTxt.setText(ServiceAudioHelper.transByteToKo(recordSize));	
		
		f = cursor.getString(cursor
				.getColumnIndex(ProofDataBase.COLUMN_VOICE_FILE));
		
		if(Settings.isDebug())
			Log.v(TAG, f.toString());
		
		Uri wav = Uri.parse("content://" + f);
		B = (ImageView) view.findViewById(R.id.mPlayVoiceImage);
		B.setOnClickListener(ButtonOnClickListener);
		
		if(Settings.isDebug())
			Log.v(TAG, wav.toString());
		
	}
	
	
	public  OnClickListener ButtonOnClickListener = new OnClickListener() {
        public void onClick(View v) {
        	Log.v(TAG, "button click");
        	Uri wav = Uri.parse("file://" + f);
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(wav, "audio/3gp");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mcontext.startActivity(intent); 
            
        }
    }; 
}