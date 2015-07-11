package org.proof.recorder.adapter.phone;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.database.support.ProofDataBase;

import java.io.InputStream;

@SuppressWarnings("unused")
class RecorderListDossiersAdapter extends SimpleCursorAdapter implements
		SectionIndexer {

	private static final String TAG = "RecListDossiersAda";

	public RecorderListDossiersAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flag) {
		super(context, layout, c, from, to, flag);
	}

	/**
	 * Separator Logic (sorted by date into the listview)
	 */

	/*
	 * private static final int ITEM_VIEW_TYPE_VIDEO = 0; private static final
	 * int ITEM_VIEW_TYPE_SEPARATOR = 1; private static final int
	 * ITEM_VIEW_TYPE_COUNT = 2;
	 *
	 * private Date convertTimeStampToDate(String timeStamp) { Date date = new
	 * Date(Long.parseLong(timeStamp)); return date; }
	 */

	@Override
	public int getPositionForSection(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSectionForPosition(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * End of Separator Logic
	 */

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		// mCursor = cursor;
		String initialPhone = cursor.getString(cursor
				.getColumnIndex(ProofDataBase.COLUMN_TELEPHONE));

		Contact mContact = AndroidContactsHelper.getContactInfosByNumber(
				context, initialPhone);

		TextView phTxt = (TextView) view.findViewById(R.id.numberDossier);
		TextView nomUtilisateur = (TextView) view.findViewById(R.id.nomDossier);
		TextView hideId = (TextView) view.findViewById(R.id.idrecordDossier);

		ImageView imageView = (ImageView) view
				.findViewById(R.id.list_imageDossier);
		Bitmap defaultBite = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.telphone);
		imageView.setImageBitmap(defaultBite);

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
				Log.v(TAG, "image read");

			Bitmap bitmap = BitmapFactory.decodeStream(input);
			imageView.setImageBitmap(bitmap);
		}
		/*
		 * OnClickListener DetailsItemListener = new OnClickListener() { public
		 * void onClick(View v) { try { Intent intent = new
		 * Intent(v.getContext(), FragmentNoteTabs.class); Bundle b = new
		 * Bundle(); b.putString("id", mCursor.getString(0));
		 * intent.putExtras(b); intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * v.getContext().startActivity(intent); Log.e(TAG, "TAPPED ON ARROW");
		 * } catch (Exception e) { Log.e(TAG, e.getMessage()); } } };
		 *
		 * ImageView img =
		 * (ImageView)view.findViewById(R.id.arrow_record_detail);
		 */

		super.bindView(view, context, cursor);

		phTxt.setText(initialPhone);
		nomUtilisateur.setText(mContact.getContactName());
		hideId.setVisibility(View.INVISIBLE);
		/* Linkify.addLinks(phTxt, Linkify.PHONE_NUMBERS); */
		// img.setOnClickListener(DetailsItemListener);

	}
}