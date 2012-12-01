package org.proof.recorder.fragment.phone;

import org.proof.recorder.R;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.StaticIntents;
import org.proof.recorder.utils.TabsPagerAdapter;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TabHost;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class FragmentListRecordFoldersTabs extends SherlockFragmentActivity {

	private static final String TAG = "FragmentListRecordFoldersTabs";

	private TabHost mTabHost;
	private ViewPager mViewPager;
	private TabsPagerAdapter mTabsAdapter;

	private static Class<?> clsUnknownContacts;
	private static Class<?> clsknownContacts;
	
	private static boolean bKnown, bUnKnown;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		clsknownContacts = FragmentListKnownContacts.KnownContactsLoader.class;
		clsUnknownContacts = FragmentListUnKnownContacts.UnKnownContactsLoader.class;

		setContentView(R.layout.fragment_inout_records_tabs);

		ActionBar mBar = getSupportActionBar();

		mBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mBar.setHomeButtonEnabled(true);

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mTabHost.setup();
		
		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}

		mTabsAdapter = new TabsPagerAdapter(this, mViewPager);
		
		int known, unknown;		

		known = AndroidContactsHelper.getKnownFolderContactsCount();
		unknown = AndroidContactsHelper.getUnKnownFolderContactsCount();
		
		Log.v(TAG, "TITLED: " + known + " " + "UNTITLED: " + unknown);

		if (known > 0) {
			bKnown = true;
		}
		else
			bKnown = false;

		if (unknown > 0) {
			bUnKnown = true;
		}
		else
			bUnKnown = false;		

		
		
		if (bKnown && !bUnKnown) {
			
			mTabsAdapter.addTab(
					mBar.newTab().setText(
							this.getString(R.string.strKnownContactsTab)),
							clsknownContacts, null);
		}
		
		else if (!bKnown && bUnKnown) {	
			
			mTabsAdapter.addTab(
					mBar.newTab().setText(
							this.getString(R.string.strUnKnownContactsTab)),
							clsUnknownContacts, null);
		}
		
		else if (bKnown && bUnKnown) {		
			

			mTabsAdapter.addTab(
					mBar.newTab().setText(
							this.getString(R.string.strKnownContactsTab)),
							clsknownContacts, null);

			mTabsAdapter.addTab(
					mBar.newTab().setText(
							this.getString(R.string.strUnKnownContactsTab)),
							clsUnknownContacts, null);	

		}
		
		else {
			/**
			 *  @TODO: set Notification and AlertDialog Message 
			 * */
			startActivity(StaticIntents.goHome(this));		
		}

		QuickActionDlg.setmContext(this);
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		return QuickActionDlg.mainUiMenuHandler(menu);
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		return QuickActionDlg.mainActionsMenuHandler(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

}
