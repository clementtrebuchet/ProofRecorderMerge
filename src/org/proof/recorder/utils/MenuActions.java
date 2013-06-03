package org.proof.recorder.utils;

import java.util.List;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.adapter.voice.VoiceListAdapter;

import org.proof.recorder.database.models.Contact;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.database.models.Voice;
import org.proof.recorder.fragment.dialog.ShareIntentChooser;
import org.proof.recorder.fragment.notes.FragmentNoteTabs;
import org.proof.recorder.fragment.phone.FragmentListKnownContacts.KnownContactsLoader.ContactAdapter;
import org.proof.recorder.fragment.phone.FragmentListRecordTabs;

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

import android.widget.ArrayAdapter;
import android.widget.Toast;

public final class MenuActions {

	private static Context mContext;
	private static Object mVoiceAdapter;

	private static AlertDialog.Builder mDialog;

	/*
	 * private methods for contextual menu
	 */

	private MenuActions() {

	}

	private static VoiceListAdapter getVoiceAdapter() {
		return (VoiceListAdapter) mVoiceAdapter;
	}

	public static void setVoiceAdapter(Object adapter) {
		MenuActions.mVoiceAdapter = adapter;
	}

	private static Context getInternalContext() {
		return mContext.getApplicationContext();
	}

	public static void setInternalContext(Context mContext) {
		MenuActions.mContext = mContext;
	}

	private static void _deleteContactsFolder(String mPhone) {
		int mDeletedContacts = PersonnalProofContentProvider
				.deleteContactsFolder(mPhone);
		Console.print_debug(
				"All contacts' folder were Suppressed (CONTACTS COUNT: "
				+ mDeletedContacts + ") !!");
	}

	public static void displayCallsFolderDetails(String mPhone, String mWhere,
			Context mContext) {
		
		Console.print_exception(mPhone);

		try {
			Bundle extraData = new Bundle();
			extraData.putString("mIdOrTelephone", mPhone);
			StaticIntents intent = StaticIntents.create(mContext,
					FragmentListRecordTabs.class, extraData);
			mContext.startActivity(intent);
		} catch (Exception e) {
			Console.print_exception(e);
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
			getInternalContext().getContentResolver().delete(uri, null, null);
		} catch (Exception e) {
			Console.print_exception(e);
		}
	}

	private static void deleteOnDisk(String mFilePath) {

		try {
			OsHandler.deleteFileFromDisk(mFilePath);
		} catch (Exception e) {
			Console.print_exception(e);
		}
	}

	/*public static void deleteSearchItem(
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
	}*/
	
	public static void deleteCalls(
			final String[] recordIds, final String[] recordPaths) {
		
		for(String id : recordIds) {
			_deleteItem(id, Settings.mType.CALL);
		}
		
		for(String file : recordPaths) {
			deleteOnDisk(file);
		}
	}
	
	public static void deleteVoices(
			final String[] recordIds, final String[] recordPaths) {
		
		for(String id : recordIds) {
			_deleteItem(id, Settings.mType.VOICE);
		}
		
		for(String file : recordPaths) {
			deleteOnDisk(file);
		}
	}
	
	
	private static void removeItem(
			final Settings.mType type, 
			final String id, 
			final ArrayAdapter<Object> adpater,
			final List<Object> innerCollection,
			final Object item) {
		
		String itemPath = null;
		
		_deleteItem(id, type);
		
		switch (type) {
		case CALL:
			Record record = (Record) item;
			adpater.remove(record);
			itemPath = record.getmFilePath();
			break;
				
		case VOICE_TITLED:
		case VOICE_UNTITLED:
			Voice voice = (Voice) item;
			adpater.remove(voice);
			innerCollection.remove(voice);
			adpater.notifyDataSetChanged();
			itemPath = voice.getFilePath();
			break;

		default:
			break;
		}	
		
		if(itemPath != null)
			deleteOnDisk(itemPath);
	}

	/**
	 * @param mId
	 * @param mType
	 * @param objects
	 * @param mOutAdapter
	 * @param mInAdapter
	 * @param mItem
	 */

	public static void deleteItem(
			final String mId, 
			final Settings.mType mType,
			final List<Object> innerCollection,
			final ArrayAdapter<Object> adpater,
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
							removeItem(mType, mId, adpater, innerCollection, mItem);
						}
					});

			mDialog.show();
		} else {
			removeItem(mType, mId, adpater, innerCollection, mItem);
		}

	}

	private static void displayItemDetails(StaticIntents intent, Bundle b) {
		try {
			intent.putExtras(b);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getInternalContext().startActivity(intent);
		} catch (Exception e) {
			Console.print_exception(e);
		}
	}

	public static void displayItemVoiceDetails(Cursor c) {
		if (c == null)
			c = getVoiceAdapter().getCursor();
		FragmentListVoice.ID = c.getString(0);
		StaticIntents intent = StaticIntents.create(getInternalContext(),
				FragmentVoiceNoteTabs.class);
		Bundle b = new Bundle();
		b.putString("id", c.getString(0));
		displayItemDetails(intent, b);
	}
	
	public static void displayItemVoiceDetails(String id) {

		FragmentListVoice.ID = id;
		StaticIntents intent = StaticIntents.create(getInternalContext(),
				FragmentVoiceNoteTabs.class);
		Bundle b = new Bundle();
		b.putString("id", id);
		displayItemDetails(intent, b);
	}

	public static void displayItemPhoneDetails(String mId) {
		FragmentNoteTabs.id = mId;
		StaticIntents intent = StaticIntents.create(getInternalContext(),
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
				Toast.makeText(getInternalContext(), wav.toString(),
						Toast.LENGTH_SHORT).show();

			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getInternalContext().startActivity(intent);
		} catch (Exception e) {
			Console.print_exception(e);
		}
	}

	/**
	 * Array of Strings [0] : filePath
	 * 
	 * @param mDatas
	 */
	public static void sharingOptions(String[] mDatas) {
		Intent mIntentShare = new Intent(mContext, ShareIntentChooser.class);
		mIntentShare.putExtra("AttachedFiles", mDatas);
		mContext.startActivity(mIntentShare);    
	}
}
