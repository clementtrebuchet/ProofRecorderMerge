package org.proof.recorder.adapter.contacts;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;


public class ContactListCursorAdapter extends SimpleCursorAdapter {
	
/*	private final static String TAG = "ContactListCursorAdapter";
	private static Cursor mCursor;
	private static Context mContext;*/

	public ContactListCursorAdapter(Context context, int layout, Cursor cursor,
			String[] from, int[] to, int flag) {
		super(context, layout, cursor, from, to, flag);
	}
	
/*	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		mCursor = cursor;
		mContext = context;
		
		super.bindView(view, context, cursor);
		CheckBox box = (CheckBox) view.findViewById(R.id.checkBox);
		box.setText(cursor.getString(cursor
					.getColumnIndex(ProofDataBase.COLUMN_DISPLAY_NAME)));		
	}*/

/*	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		String[] projection = new String[] {
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Contacts.HAS_PHONE_NUMBER,
				ContactsContract.Contacts._ID };

		return context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, projection, 
				ContactsContract.Contacts.HAS_PHONE_NUMBER+"=?", 
				new String[]{"1"}, 
				ContactsContract.Contacts.DISPLAY_NAME
		);
	}*/

}
