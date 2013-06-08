package org.proof.recorder.fragment.contacts;

import org.proof.recorder.R;
import org.proof.recorder.bases.activity.ProofFragmentActivity;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.TabsPagerAdapter;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TabHost;

import com.actionbarsherlock.app.ActionBar;

public class FragmentListPhoneContactsTabs extends ProofFragmentActivity {
	
	private TabHost mTabHost;
	private ViewPager mViewPager;
	private TabsPagerAdapter mTabsAdapter;

	private static Class<?> clsPhoneContacts;
	private static Class<?> clsExcludedContacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar mBar = getSupportActionBar();

		mBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mBar.setHomeButtonEnabled(true);

		clsPhoneContacts = FragmentListPhoneContacts.PhoneContactsLoader.class;
		clsExcludedContacts = FragmentListExcludedContacts.ExcludedContactsLoader.class;

		setContentView(R.layout.fragment_contacts_tabs);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mTabHost.setup();

		mTabsAdapter = new TabsPagerAdapter(this, mViewPager);

		mTabsAdapter.addTab(
				mBar.newTab().setText(
						this.getString(R.string.strNotExcludedTab)),
				clsPhoneContacts, null);

		mTabsAdapter.addTab(
				mBar.newTab().setText(this.getString(R.string.strExcludedTab)),
				clsExcludedContacts, null);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
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
	
	/**
	 * stop the recreation of the activity on Orientation Change the
	 * MediaRecorder, is therefore not recreated and keep recording on
	 * Orientation Changes
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}