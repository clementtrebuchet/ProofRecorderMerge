package org.proof.recorder.adapter.phone;

import java.io.File;
import java.io.InputStream;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.utils.ApproxRecordTime;
import org.proof.recorder.utils.ApproxRecordTime.MSong;
import org.proof.recorder.utils.ServiceAudioHelper;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class RecorderDetailAdapter extends SimpleCursorAdapter {

	private static final String TAG = "RecorderDetailAdpater";
	private ImageView B;
	private String f;
	private static TextView durationTxt;
	private static Context mcontext;

	public RecorderDetailAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flag) {
		super(context, layout, c, from, to, flag);
		mcontext = context;

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		String initialPhone = cursor.getString(cursor
				.getColumnIndex(ProofDataBase.COLUMN_TELEPHONE));

		Contact mContact = AndroidContactsHelper.getContactInfosByNumber(
				context, initialPhone);

		ImageView imageView = (ImageView) view
				.findViewById(R.id.mPhoneIv);

		InputStream input = null;

		if (mContact.getLongContractId() != -1) {
			Uri uri = ContentUris.withAppendedId(
					ContactsContract.Contacts.CONTENT_URI,
					mContact.getLongContractId());
			ContentResolver cr = context.getContentResolver();
			input = ContactsContract.Contacts.openContactPhotoInputStream(cr,
					uri);
		}
		if (input == null) {

		} else {
			if (Settings.isDebug())
				Log.v(TAG, "Image is read");

			Bitmap bitmap = BitmapFactory.decodeStream(input);
			imageView.setImageBitmap(bitmap);
		}

		super.bindView(view, context, cursor);

		TextView userTxt = (TextView) view.findViewById(R.id.mPhoneContact);

		if (mContact.getContactName() == "")
			mContact.setContactName("Contact Inconnu");

		userTxt.setText(mContact.getContactName());
		TextView phTxt = (TextView) view.findViewById(R.id.mPhone);
		
		phTxt.setText(cursor.getString(cursor
				.getColumnIndex(ProofDataBase.COLUMN_TELEPHONE)));
		Linkify.addLinks(phTxt, Linkify.PHONE_NUMBERS);
		String recordSize = cursor.getString(cursor
				.getColumnIndex(ProofDataBase.COLUMN_TAILLE));
		
		TextView tailleTxt = (TextView) view.findViewById(R.id.mSizeOfFile);
		tailleTxt.setText(ServiceAudioHelper.transByteToKo(recordSize));
		
		ImageView sensAppel = (ImageView) view.findViewById(R.id.mSenseIv);
		String s = cursor.getString(cursor
				.getColumnIndex(ProofDataBase.COLUMN_SENS));
		
		if (s.contains("S")) {
			Bitmap appelSortant = BitmapFactory.decodeResource(
					context.getResources(), R.drawable.outgoing_call);
			sensAppel.setImageBitmap(appelSortant);
		} else if (s.contains("E")) {

			Bitmap appelEntrant = BitmapFactory.decodeResource(
					context.getResources(), R.drawable.incoming_call);
			sensAppel.setImageBitmap(appelEntrant);

		}
		TextView Sense = (TextView) view.findViewById(R.id.mSenseTv);
		Sense.setText(this.makeHumanReadableSens(cursor));

		f = cursor.getString(cursor.getColumnIndex(ProofDataBase.COLUMN_FILE));

		if (Settings.isDebug())
			Log.v(TAG, f.toString());

		Uri wav = Uri.parse("content://" + f);
		B = (ImageView) view.findViewById(R.id.mPlayFile);
		B.setOnClickListener(ButtonOnClickListener);

		if (Settings.isDebug())
			Log.v(TAG, wav.toString());
		
		try{
			durationTxt = (TextView) view.findViewById(R.id.mDurationOfFile);
			File g = new File(f);
			ApproxRecordTime f = new ApproxRecordTime(g, true);
			durationTxt.setText("Duration : "+f.run()+" mn/s");
			
			
			
		} catch (Exception e){
			Log.v(TAG,""+e.getMessage());
		}


	}

	public OnClickListener ButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.v(TAG, "button click");
			Uri wav = Uri.parse("file://" + f);
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(wav, "audio/wav");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mcontext.startActivity(intent);

		}
	};

	private String makeHumanReadableSens(Cursor cursor) {
		String HumanReadableSens = cursor.getString(cursor
				.getColumnIndex(ProofDataBase.COLUMN_SENS));

		if (Settings.isDebug())
			Log.v(TAG, "HumanReadableSens-> " + HumanReadableSens);

		if (HumanReadableSens.toLowerCase().contains("s")) {
			return "Appel Sortant";
		} else {
			return "Appel Entrant";
		}
	}
}