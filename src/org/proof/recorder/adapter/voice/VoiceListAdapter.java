package org.proof.recorder.adapter.voice;

//import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.proof.recorder.R;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.Log.Console;
import org.proof.recorder.utils.ServiceAudioHelper;

@SuppressWarnings("unused")
public class VoiceListAdapter extends SimpleCursorAdapter {

	public VoiceListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flag) {
		super(context, layout, c, from, to, flag);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {			
		
		super.bindView(view, context, cursor);
		
		String recordSize = cursor.getString(cursor
				.getColumnIndex(ProofDataBase.COLUMN_VOICE_TAILLE));
		
		TextView tailleTxt = (TextView) view.findViewById(R.id.sens);
		tailleTxt.setText(ServiceAudioHelper.transByteToKo(recordSize));
		
		TextView mId = (TextView) view.findViewById(R.id.idrecord);
		String id = (String) mId.getText();		
		mId.setVisibility(View.INVISIBLE);
		
		TextView mTitreVoice = (TextView) view.findViewById(R.id.number);
		
		Uri uri = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "vnote_recordid/"
						+ id);
		
		Cursor dataCursor = null;
		String mTitle = null;
		
		try {
			
			dataCursor = context.getContentResolver().query(uri, null, null, null, null);
			
			while (dataCursor.moveToNext()){
				mTitle = (dataCursor.getString(dataCursor
						.getColumnIndex(
								ProofDataBase.COLUMNVOICE_TITLE)));				
			}
			
			mTitreVoice.setText(mTitle);
		}
		catch(Exception e) {
			Console.print_exception(e);
		}
		finally {
			if(dataCursor != null) {
				dataCursor.close();
			}
		}
		
		ImageView imageView = (ImageView) view.findViewById(R.id.list_image);
		Bitmap defaultBite = BitmapFactory.decodeResource(context.getResources(), R.drawable.mic_48);
		imageView.setImageBitmap(defaultBite);	
			    
	}	
}