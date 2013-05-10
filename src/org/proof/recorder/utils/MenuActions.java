package org.proof.recorder.utils;

import java.io.IOException;
import java.util.ArrayList;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.adapter.voice.VoiceListAdapter;
import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.database.models.Voice;
import org.proof.recorder.fragment.dialog.ShareIntentChooser;
import org.proof.recorder.fragment.notes.FragmentNoteTabs;
import org.proof.recorder.fragment.phone.FragmentListKnownContacts.KnownContactsLoader.ContactAdapter;
import org.proof.recorder.fragment.phone.FragmentListRecordIn.InCommingCallsLoader.InCommingCallsAdapter;
import org.proof.recorder.fragment.phone.FragmentListRecordOut.OutGoingCallsLoader.OutGoingCallsAdapter;
import org.proof.recorder.fragment.phone.FragmentListRecordTabs;
import org.proof.recorder.fragment.search.SearchResult.SearchListLoader;
import org.proof.recorder.fragment.voice.FragmentListVoice;
import org.proof.recorder.fragment.voice.notes.FragmentVoiceNoteTabs;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.Log.Console;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public final class MenuActions {

	private final static String TAG = "MENU_ACTIONS";

	private static Context mContext;
	private static Object mVoiceAdapter;

	private static AlertDialog.Builder mDialog;

	/*
	 * private methods for contextual menu
	 */

	private MenuActions() {

	}

	private static VoiceListAdapter getmVoiceAdapter() {
		return (VoiceListAdapter) mVoiceAdapter;
	}

	public static void setmVoiceAdapter(Object adapter) {
		MenuActions.mVoiceAdapter = adapter;
	}

	private static Context getmContext() {
		return mContext.getApplicationContext();
	}

	public static void setmContext(Context mContext) {
		MenuActions.mContext = mContext;
	}

	private static void _deleteContactsFolder(String mPhone) {
		int mDeletedContacts = PersonnalProofContentProvider
				.deleteContactsFolder(mPhone);
		if (Settings.isDebug())
			Log.i("ContextMenu",
					"All contacts' folder were Suppressed (CONTACTS COUNT: "
							+ mDeletedContacts + ") !!");
	}

	public static void displayCallsFolderDetails(String mPhone, String mWhere,
			Context mContext) {

		try {
			Bundle b = new Bundle();
			b.putString("mWhereClause", mWhere);
			b.putString("mIdOrTelephone", mPhone);
			StaticIntents intent = StaticIntents.create(mContext,
					FragmentListRecordTabs.class, b);
			mContext.startActivity(intent);
		} catch (Exception e) {
			if (Settings.isDebug())
				Log.e(TAG, e.getMessage());
		}
	}

	public static void deleteContactsFolder(final Contact mContact,
			Context mContext, final ContactAdapter ca) {

		if (Settings.isUACAssisted()) {

			mDialog = new AlertDialog.Builder(mContext);
			mDialog.setTitle(mContext.getString(R.string.strUACTitle));

			mDialog.setMessage(mContext.getString(R.string.strUACDeleteFolder));

			mDialog.setNegativeButton(
					mContext.getString(R.string.strUACCancelBtn),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});

			mDialog.setPositiveButton(
					mContext.getString(R.string.strUACConfirmBtn),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							_deleteContactsFolder(mContact.getPhoneNumber());
							ca.remove(mContact);
						}
					});

			mDialog.show();
		} else {
			_deleteContactsFolder(mContact.getPhoneNumber());
			ca.remove(mContact);
		}

	}

	private static void _deleteItem(String mId, Settings.mType mType) {
		String uriType = "";

		switch (mType) {
		case CALL:
			uriType = "record_id/";
			break;

		case VOICE:
		case VOICE_TITLED:
		case VOICE_UNTITLED:
			uriType = "voice_id/";
			break;

		default:
			break;
		}

		Uri uri = PersonnalProofContentProvider.deleteItem(uriType, mId);
		try {
			getmContext().getContentResolver().delete(uri, null, null);
		} catch (Exception e) {
			if (Settings.isDebug())
				Log.e(TAG, e.getMessage());
		}
	}

	private static void deleteOnDisk(String mFilePath) {

		try {
			OsHandler.deleteFileFromDisk(mFilePath);
		} catch (IOException e) {
			if (Settings.isDebug())
				Log.v(TAG,
						"showVoiceOptionsDlg()->ID_DELETE : " + e.getMessage());
		}
	}

	private static void refreshListOfRecords(
			final OutGoingCallsAdapter outAdapter,
			final InCommingCallsAdapter inAdapter, final Record mRecord) {

		if (null != outAdapter)
			outAdapter.remove(mRecord);
		else if (null != inAdapter)
			inAdapter.remove(mRecord);
		else {
		}
	}

	public static void deleteSearchItem(
			final Cursor mCursor,
			final Settings.mType mType,
			final LoaderManager lm,
			final Object mCustomLoader
		) {

		final String mFilePath = mCursor.getString(4), mId = mCursor
				.getString(0);

		if (Settings.isUACAssisted()) {

			mDialog = new AlertDialog.Builder(mContext);
			mDialog.setTitle(mContext.getString(R.string.strUACTitle));

			mDialog.setMessage(mContext.getString(R.string.strUACDeleteRecord));

			mDialog.setNegativeButton(
					mContext.getString(R.string.strUACCancelBtn),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});

			mDialog.setPositiveButton(
					mContext.getString(R.string.strUACConfirmBtn),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							_deleteItem(mId, mType);
							deleteOnDisk(mFilePath);
							
							lm.restartLoader(
									0x01,
									null, 
									(SearchListLoader) mCustomLoader
							);
						}
					});

			mDialog.show();
		} else {
			_deleteItem(mId, mType);
			deleteOnDisk(mFilePath);
			
			lm.restartLoader(
					0x01,
					null, 
					(SearchListLoader) mCustomLoader
			);
		}		
	}

	/**
	 * @param mId
	 * @param mType
	 * @param collection
	 * @param mOutAdapter
	 * @param mInAdapter
	 * @param mItem
	 */
	@SuppressWarnings("unchecked")
	public static void deleteItem(
			final String mId, 
			final Settings.mType mType,
			final ArrayList<Voice> collection,
			final Object mOutAdapter, 
			final Object mInAdapter,
			final Object mItem) {

		if (Settings.isUACAssisted()) {

			mDialog = new AlertDialog.Builder(mContext);
			mDialog.setTitle(mContext.getString(R.string.strUACTitle));

			mDialog.setMessage(mContext.getString(R.string.strUACDeleteRecord));

			mDialog.setNegativeButton(
					mContext.getString(R.string.strUACCancelBtn),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});

			mDialog.setPositiveButton(
					mContext.getString(R.string.strUACConfirmBtn),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							_deleteItem(mId, mType);
							switch (mType) {
							case CALL:
								deleteOnDisk(((Record) mItem).getmFilePath());
								refreshListOfRecords(
										(OutGoingCallsAdapter) mOutAdapter,
										(InCommingCallsAdapter) mInAdapter,
										(Record) mItem);
								break;

							case VOICE_UNTITLED:
								((ArrayAdapter<Voice>) mOutAdapter).remove((Voice) mItem);
								collection.remove((Voice) mItem);
								((ArrayAdapter<Voice>) mOutAdapter).notifyDataSetChanged();
								break;

							case VOICE_TITLED:
								((ArrayAdapter<Voice>) mOutAdapter).remove((Voice) mItem);
								collection.remove((Voice) mItem);
								((ArrayAdapter<Voice>) mOutAdapter).notifyDataSetChanged();
								break;

							default:
								break;
							}
						}
					});

			mDialog.show();
		} else {
			_deleteItem(mId, mType);

			switch (mType) {
			case CALL:
				deleteOnDisk(((Record) mItem).getmFilePath());
				refreshListOfRecords((OutGoingCallsAdapter) mOutAdapter,
						(InCommingCallsAdapter) mInAdapter, (Record) mItem);
				break;

			case VOICE_UNTITLED:
				((ArrayAdapter<Voice>) mOutAdapter).remove((Voice) mItem);
				collection.remove((Voice) mItem);
				((ArrayAdapter<Voice>) mOutAdapter).notifyDataSetChanged();
				break;

			case VOICE_TITLED:
				((ArrayAdapter<Voice>) mOutAdapter).remove((Voice) mItem);
				collection.remove((Voice) mItem);
				((ArrayAdapter<Voice>) mOutAdapter).notifyDataSetChanged();
				break;

			default:
				break;
			}
		}

	}

	private static void displayItemDetails(StaticIntents intent, Bundle b) {
		try {
			intent.putExtras(b);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getmContext().startActivity(intent);
		} catch (Exception e) {
			Console.print_exception(e);
		}
	}

	public static void displayItemVoiceDetails(Cursor c) {
		if (c == null)
			c = getmVoiceAdapter().getCursor();
		FragmentListVoice.ID = c.getString(0);
		StaticIntents intent = StaticIntents.create(getmContext(),
				FragmentVoiceNoteTabs.class);
		Bundle b = new Bundle();
		b.putString("id", c.getString(0));
		displayItemDetails(intent, b);
	}
	
	public static void displayItemVoiceDetails(String id) {

		FragmentListVoice.ID = id;
		StaticIntents intent = StaticIntents.create(getmContext(),
				FragmentVoiceNoteTabs.class);
		Bundle b = new Bundle();
		b.putString("id", id);
		displayItemDetails(intent, b);
	}

	public static void displayItemPhoneDetails(String mId) {
		FragmentNoteTabs.id = mId;
		StaticIntents intent = StaticIntents.create(getmContext(),
				FragmentNoteTabs.class);
		Bundle b = new Bundle();
		b.putString("id", mId);
		displayItemDetails(intent, b);
	}

	public static void readVoice(String mFilePath) {
		readRecord(mFilePath, "audio/3gp");
	}

	public static void readPhone(String mFilePath) {
		readRecord(mFilePath, "audio/wav");
	}

	private static void readRecord(String mFilePath, String audio) {

		try {

			Uri wav = Uri.parse("file://" + mFilePath);
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(wav, audio);

			if (Settings.isToastNotifications())
				Toast.makeText(getmContext(), wav.toString(),
						Toast.LENGTH_SHORT).show();

			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getmContext().startActivity(intent);
		} catch (Exception e) {

			if (Settings.isDebug())
				Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * Array of Strings [0] : filePath
	 * 
	 * @param mDatas
	 */
	public static void sharingOptions(String[] mDatas) {
		Intent mIntentShare = new Intent(mContext, ShareIntentChooser.class);
		mIntentShare.putExtra("mAttachFilePath", mDatas[0]);
		mContext.startActivity(mIntentShare);    
	}
}
