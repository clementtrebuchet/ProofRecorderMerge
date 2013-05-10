package org.proof.recorder.utils;

import java.io.IOException;
import java.util.ArrayList;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.database.models.Voice;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.fragment.dialog.Search;
import org.proof.recorder.fragment.phone.FragmentListRecordIn.InCommingCallsLoader.InCommingCallsAdapter;
import org.proof.recorder.fragment.phone.FragmentListRecordOut.OutGoingCallsLoader.OutGoingCallsAdapter;
import org.proof.recorder.quick.action.ActionItem;
import org.proof.recorder.quick.action.QuickAction;
import org.proof.recorder.utils.Log.Console;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.view.SubMenu;

public class QuickActionDlg {

	private final static String TAG = "QUICK_ACTION_DLG";
	private static final String BR  = "\n";

	private static boolean bTitled, bUntitled, bKnown, bUnknown;

	/**
	 * QUICK_ACTION_DLG IDS
	 */
	private static final int ID_DISPLAY = 1;
	private static final int ID_READ = 2;
	private static final int ID_SHARE = 3;
	private static final int ID_DELETE = 4;

	/**
	 * QUICK_MAIN_DLG
	 */
	private static final int ID_HOME = 5;
	private static final int ID_SETTINGS = 6;
	private static final int ID_CONTACTS_LIST = 7;
	private static final int ID_VOICE_LIST = 8;
	private static final int ID_RECORDS_LIST = 9;
	private static final int ID_VOICE_RECORDER = 10;
	private static final int ID_PLUGINS = 11;
	private static final int ID_SEARCH = 12;

	/**
	 * 
	 */
	private static Context mContext = null;
	private static QuickAction mQuickAction;

	private QuickActionDlg() {

	}

	/**
	 * Set the context used in all methods is to be set at first !
	 * 
	 * @param mContext
	 */
	public static void setmContext(Context mContext) {
		QuickActionDlg.mContext = mContext;
	}
	
	public static boolean hasContext() {
		return QuickActionDlg.mContext != null;
	}

	/**
	 * Calls List Quick Actions Dialog
	 * 
	 * @param c
	 * @param v
	 * @param outAdapter
	 * @param inAdapter
	 * @param mRecord
	 */
	public static void showPhoneOptionsDlg(Context c, View v,
			final OutGoingCallsAdapter outAdapter,
			final InCommingCallsAdapter inAdapter, final Record mRecord) {
		mContext = c;
		MenuActions.setmContext(c);

		setHasSearch(true);

		injectQuickDlgMenu(c);

		mQuickAction
		.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
			@Override
			public void onItemClick(QuickAction source, int pos,
					int actionId) {

				switch (actionId) {

				case ID_DISPLAY:
					MenuActions.displayItemPhoneDetails(mRecord
							.getmId());
					break;

				case ID_READ:
					MenuActions.readPhone(mRecord.getmFilePath());
					break;

				case ID_SEARCH:
					Intent intent = new Intent(mContext, Search.class);
					intent.putExtra("phone", mRecord.getmDataNumber().get_nationalNumber());
					mContext.startActivity(intent);
					break;						

				case ID_SHARE:
					String[] mDatas = new String[] { mRecord
							.getmFilePath() };
					MenuActions.sharingOptions(mDatas);
					break;

				case ID_DELETE:
					MenuActions.deleteItem(mRecord.getmId(),
							Settings.mType.CALL, null,
							outAdapter, inAdapter, mRecord);
					break;
				default:
					if (Settings.isToastNotifications())
						Toast.makeText(
								mContext,
								"Erreur dans "
										+ TAG
										+ " ID inconnu dans onItemClick()",
										Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});

		mQuickAction.show(v);
	}

	/**
	 * Voice List Quick Actions Dialog
	 * 
	 * @param c
	 * @param v
	 * @param voice
	 * @param adapter
	 * @param lm
	 * @param voiceListLoader
	 */
	public static void showTitledVoiceOptionsDlg(
			Context c,
			View v,
			final Voice voice,
			final Object adapter,
			final ArrayList<Voice> collection,
			final org.proof.recorder.fragment.voice.FragmentListVoice.VoiceListLoader voiceListLoader,
			final Settings.mType mType) {
		
		mContext = c;
		MenuActions.setmVoiceAdapter(adapter);
		MenuActions.setmContext(c);

		setHasSearch(false);

		injectQuickDlgMenu(c);
		// Set listener for action item clicked
		mQuickAction
		.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
			@Override
			public void onItemClick(QuickAction source, int pos,
					int actionId) {

				switch (actionId) {

				case ID_DISPLAY:
					MenuActions.displayItemVoiceDetails(voice.getId());
					break;

				case ID_READ:
					MenuActions.readVoice(voice.getFilePath());
					break;

				case ID_SHARE:
					String[] mDatas = new String[] { voice.getFilePath() };
					MenuActions.sharingOptions(mDatas);
					break;

				case ID_DELETE:
					MenuActions.deleteItem(voice.getId(), mType,
							collection, adapter, null, voice);
					try {
						OsHandler.deleteFileFromDisk(voice.getFilePath());
					} catch (IOException e) {
						Console.print_exception(
									"showVoiceOptionsDlg()->ID_DELETE : "
											+ e.getMessage());
					}
					break;

				default:
					if (Settings.isToastNotifications())
						Toast.makeText(
								mContext,
								"Erreur dans "
										+ TAG
										+ " ID inconnu dans onItemClick()",
										Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});

		mQuickAction.show(v);
	}

	public static void showSearchOptionsDlg(
			Context activity, 
			View v, 
			final Cursor c,
			final Settings.mType mType,
			final LoaderManager lm,
			final Object mCustomLoader) {
		mContext = activity;

		setHasSearch(false);

		injectQuickDlgMenu(activity);
		MenuActions.setmContext(activity);

		mQuickAction
		.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
			@Override
			public void onItemClick(QuickAction source, int pos,
					int actionId) {

				switch (actionId) {

				case ID_DISPLAY:

					if (mType == Settings.mType.CALL) {
						MenuActions.displayItemPhoneDetails(c.getString(0));
					} else {
						MenuActions.displayItemVoiceDetails(c);
					}
					break;

				case ID_READ:
					if (mType == Settings.mType.CALL) {
						MenuActions.readPhone(c.getString(4));
					} else {
						MenuActions.readVoice(c.getString(4));
					}
					break;

				case ID_SHARE:
					MenuActions.sharingOptions(new String[] { c.getString(4) });
					break;

				case ID_DELETE:
					MenuActions.deleteSearchItem(c, mType, lm, mCustomLoader);
					break;

				default:
					if (Settings.isToastNotifications())
						Toast.makeText(
								mContext,
								"Erreur dans "
										+ TAG
										+ " ID inconnu dans onItemClick()",
										Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});

		mQuickAction.show(v);
	}

	public static void showUnTitledVoiceOptionsDlg(
			Context activity,
			View v,
			final Voice voice,
			final Object listAdapter,
			final ArrayList<Voice> collection,
			final org.proof.recorder.fragment.voice.FragmentListVoiceUntitled.VoiceListLoader voiceListLoader,
			final Settings.mType mType) {

		mContext = activity;
		MenuActions.setmVoiceAdapter(listAdapter);
		MenuActions.setmContext(activity);

		setHasSearch(false);

		injectQuickDlgMenu(activity);
		// Set listener for action item clicked
		mQuickAction
		.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
			@Override
			public void onItemClick(QuickAction source, int pos,
					int actionId) {

				switch (actionId) {

				case ID_DISPLAY:
					MenuActions.displayItemVoiceDetails(voice.getId());
					break;

				case ID_READ:
					MenuActions.readVoice(voice.getFilePath());
					break;

				case ID_SHARE:
					String[] mDatas = new String[] { voice.getFilePath() };
					MenuActions.sharingOptions(mDatas);
					break;

				case ID_DELETE:
					MenuActions.deleteItem(voice.getId(), mType,
							collection, listAdapter, null, voice);
					try {
						OsHandler.deleteFileFromDisk(voice.getFilePath());
					} catch (IOException e) {
						Console.print_exception(
									"showVoiceOptionsDlg()->ID_DELETE : "
											+ e.getMessage());
					}
					break;

				default:
					if (Settings.isToastNotifications())
						Toast.makeText(
								mContext,
								"Erreur dans "
										+ TAG
										+ " ID inconnu dans onItemClick()",
										Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});

		mQuickAction.show(v);

	}

	private static boolean hasSearch = false;

	/**
	 * Quick Actions Bar facility
	 * 
	 * @param context
	 */
	private static void injectQuickDlgMenu(Context context) {

		ActionItem display = new ActionItem(ID_DISPLAY,
				mContext.getString(R.string.cm_records_list_display_txt),
				mContext.getResources().getDrawable(R.drawable.quick_display));
		ActionItem read = new ActionItem(ID_READ,
				mContext.getString(R.string.cm_records_list_read_wav_txt),
				mContext.getResources().getDrawable(R.drawable.quick_play));
		ActionItem share = new ActionItem(ID_SHARE,
				mContext.getString(R.string.cm_records_list_sharing_opts_txt),
				mContext.getResources().getDrawable(R.drawable.quick_share));
		ActionItem delete = new ActionItem(ID_DELETE,
				mContext.getString(R.string.cm_records_list_del_file_txt),
				mContext.getResources().getDrawable(R.drawable.quick_delete));		

		mQuickAction = new QuickAction(context);

		mQuickAction.addActionItem(display);
		mQuickAction.addActionItem(read);
		mQuickAction.addActionItem(share);
		mQuickAction.addActionItem(delete);

		if(hasSearch()) {
			ActionItem search = new ActionItem(ID_SEARCH,
					mContext.getString(R.string.search_quick_dlg_msg),
					mContext.getResources().getDrawable(R.drawable.search));
			mQuickAction.addActionItem(search);
		}		

		mQuickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
			@Override
			public void onDismiss() {

			}
		});
	}

	/**
	 * SherlockActionBar Wrapper for displaying the main Menu & Search facility
	 * 
	 * @param menu
	 * @return boolean
	 */
	@SuppressLint("InlinedApi")
	public static boolean mainUiMenuHandler(com.actionbarsherlock.view.Menu menu) {

		menu.add(0, ID_SEARCH, 0, mContext.getString(R.string.search_hint))
		.setIcon(R.drawable.ic_action_search)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		SubMenu sub = menu.addSubMenu("MENU");

		sub.add(0, ID_HOME, 0, mContext.getString(R.string.strHome)).setIcon(
				R.drawable.ic_home_menu);

		sub.add(0, ID_PLUGINS, 0,
				mContext.getString(R.string.strPlugins)).setIcon(
						R.drawable.ic_web_design);

		sub.add(0, ID_RECORDS_LIST, 0,
				mContext.getString(R.string.strCallRecords)).setIcon(
						R.drawable.ic_phone_list);

		sub.add(0, ID_VOICE_LIST, 0,
				mContext.getString(R.string.strVoiceRecords)).setIcon(
						R.drawable.ic_voice_list);

		sub.add(0, ID_VOICE_RECORDER, 0,
				mContext.getString(R.string.strVoiceRecorder)).setIcon(
						R.drawable.ic_voice_recorder);

		sub.add(0, ID_CONTACTS_LIST, 0,
				mContext.getString(R.string.strContactManager)).setIcon(
						R.drawable.ic_contacts_list);

		sub.add(0, ID_SETTINGS, 0,
				mContext.getString(R.string.strGlobalSettings)).setIcon(
						R.drawable.ic_settings);

		sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	/**
	 * 
	 * @param item
	 * @return boolean
	 */
	public static boolean mainActionsMenuHandler(
			com.actionbarsherlock.view.MenuItem item) {

		AlertDialogHelper.setContext(mContext);

		if (Settings.isDebug()) {

			String msg = "=== ITEM INFO ===" + BR;
			msg += "STRING: " + item.toString() + BR;
			msg += "ID    : " + item.getItemId() + BR;
			msg += "GROUP : " + item.getGroupId() + BR;

			Console.print_debug(msg);
		}

		int id = item.getItemId();
		switch (id) {

		case android.R.id.home:
			mContext.startActivity(StaticIntents.goHome(mContext));
			break;

		case ID_SEARCH:
			StaticIntents intent = StaticIntents.create(mContext, Search.class);
			mContext.startActivity(intent);
			break;

		case ID_HOME:
			mContext.startActivity(StaticIntents.goHome(mContext));
			break;

		case ID_SETTINGS:
			mContext.startActivity(StaticIntents.goSettings(mContext));
			break;

		case ID_CONTACTS_LIST:
			mContext.startActivity(StaticIntents.goContactsList(mContext));
			break;

		case ID_VOICE_LIST:

			int titled,
			untitled;

			titled = AndroidContactsHelper.getTitledVoiceCount();
			untitled = AndroidContactsHelper.getUnTitledVoiceCount();

			Console.print_debug("TITLED: " + titled + " " + "UNTITLED: " + untitled);

			if (titled > 0) {
				bTitled = true;
			} else
				bTitled = false;

			if (untitled > 0) {
				bUntitled = true;
			} else
				bUntitled = false;

			if (!bTitled && !bUntitled) {

				AlertDialogHelper.openNoneRecordsDialog();
			}

			else {

				if (!Settings.isNotLicensed()) {
					mContext.startActivity(StaticIntents.goVoice(mContext));
				}

			}
			break;

		case ID_RECORDS_LIST:

			int known,
			unknown;

			known = AndroidContactsHelper.getKnownFolderContactsCount();
			unknown = AndroidContactsHelper.getUnKnownFolderContactsCount();

			Console.print_debug("TITLED: " + known + " " + "UNTITLED: " + unknown);

			if (known > 0) {
				bKnown = true;
			} else
				bKnown = false;

			if (unknown > 0) {
				bUnknown = true;
			} else
				bUnknown = false;

			if (!bKnown && !bUnknown) {

				AlertDialogHelper.openNoneRecordsDialog();

			}

			else {

				if (!Settings.isNotLicensed()) {
					mContext.startActivity(StaticIntents.goPhone(mContext));
				}

			}
			break;

		case ID_VOICE_RECORDER:
			mContext.startActivity(StaticIntents.goVoiceRecorder(mContext));
			break;

		case ID_PLUGINS:
			mContext.startActivity(StaticIntents.goPlugins(mContext));
			break;

		default:
			if (Settings.isToastNotifications() && id != 0)
				Toast.makeText(
						mContext,
						"Erreur dans " + TAG + " ID inconnu dans onItemClick()",
						Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	/**
	 * @return the hasSearch
	 */
	public static boolean hasSearch() {
		return hasSearch;
	}

	/**
	 * @param hasSearch the hasSearch to set
	 */
	public static void setHasSearch(boolean hasSearch) {
		QuickActionDlg.hasSearch = hasSearch;
	}
}
