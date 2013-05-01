package org.proof.recorder.adapter.voice;

//import java.util.Date;

import org.proof.recorder.R;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.ServiceAudioHelper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class VoiceListAdapter extends SimpleCursorAdapter {

	private static final String TAG = "VoiceListAdpater";

	public VoiceListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flag) {
		super(context, layout, c, from, to, flag);
	}
	
	/**
	 * Separator Logic (sorted by date into the listview)
	 */
	
/*	private static final int ITEM_VIEW_TYPE_VIDEO = 0;
	private static final int ITEM_VIEW_TYPE_SEPARATOR = 1;
	private static final int ITEM_VIEW_TYPE_COUNT = 2;
	
	private Date convertTimeStampToDate(String timeStamp)
	{
		Date date = new Date(Long.parseLong(timeStamp));
		return date;
	}*/
	
	/**
	 * End of Separator Logic
	 */	
	

	@Override
	public void bindView(View view, Context context, Cursor cursor) {			
		
		super.bindView(view, context, cursor);
		
		String recordSize = cursor.getString(cursor
				.getColumnIndex(ProofDataBase.COLUMN_VOICE_TAILLE));
		
		TextView tailleTxt = (TextView) view.findViewById(R.id.sens);
		tailleTxt.setText(ServiceAudioHelper.transByteToKo(recordSize));
		
		TextView mId = (TextView) view.findViewById(R.id.idrecord);
		String id = (String) mId.getText();
	    Log.d(TAG, "R.id.idrecord: " + id);
		mId.setVisibility(View.INVISIBLE);
		
		TextView mTitreVoice = (TextView) view.findViewById(R.id.number);
		String mTitle = PersonnalProofContentProvider.getVoiceNoteById(id);
		mTitreVoice.setText(mTitle);
		
		ImageView imageView = (ImageView) view.findViewById(R.id.list_image);
		Bitmap defaultBite = BitmapFactory.decodeResource(context.getResources(), R.drawable.mic_48);
		imageView.setImageBitmap(defaultBite);	
			    
	}	
}