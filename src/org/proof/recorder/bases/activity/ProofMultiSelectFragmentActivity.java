package org.proof.recorder.bases.activity;

import org.proof.recorder.R;

import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.StaticIntents;
import org.proof.recorder.utils.TabsPagerAdapter;
import org.proof.recorder.utils.ViewPagerOnSwipeOff;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TabHost;

public abstract class ProofMultiSelectFragmentActivity extends ProofFragmentActivity {

	protected static enum KIND { BASE, VOICE, CALL, CONTACT };

	protected abstract KIND getKind();

	protected static ViewPagerOnSwipeOff mViewPager;	

	protected static ActionBar mBar;
	protected static int savedPosition;
	protected static Tab tabOne, tabSecond;

	protected static boolean hasOne, hasSecond, isNotify;

	protected static Bundle extraData = null;

	protected static Class<?> classOne;
	protected static Class<?> classSecond;

	protected static String _id;

	protected TabHost mTabHost;
	protected TabsPagerAdapter mTabsAdapter;

	/**
	 * @param voiceId the voiceId to set
	 */
	private static void setId(String id) {
		ProofMultiSelectFragmentActivity._id = id;
	}

	protected abstract String idKey();	
	protected abstract int tabOneResourceId();
	protected abstract int tabSecondResourceId();
	protected abstract void setUpTabsClasses();
	protected abstract void setUpTabsCount();
	protected abstract boolean isMulti();
	protected abstract int innerContentView();

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);

		setContentView(innerContentView());

		extraData = getIntent().getExtras();

		mBar = getSupportActionBar();
		mBar.setHomeButtonEnabled(true);
		mBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mViewPager = (ViewPagerOnSwipeOff) findViewById(R.id.pager);
		mTabHost.setup();

		if (savedInstance != null) {
			mTabHost.setCurrentTabByTag(savedInstance.getString("tab"));
		}

		mTabsAdapter = new TabsPagerAdapter(this, mViewPager);

		tabOne = mBar.newTab().setText(getString(tabOneResourceId()));
		tabSecond = mBar.newTab().setText(getString(tabSecondResourceId()));

		this.setUpTabsClasses();

		try {
			setNotify(extraData.getBoolean("isNotify"));			
			setId("" + extraData.getLong(idKey()));

			String mTabTitle;

			if(getKind() == KIND.VOICE) {				
				boolean hasTitle = extraData.getBoolean("hasTitle");

				if(hasTitle) {
					mTabTitle = getString(tabOneResourceId());				
				}
				else {
					mTabTitle = getString(tabSecondResourceId());
				}
			}			
			else if(getKind() == KIND.CALL) {
				String mDirection = extraData.getString("Sense");

				if (mDirection.equalsIgnoreCase("e")) {
					mTabTitle = getString(tabOneResourceId());
				}
				else {
					mTabTitle = getString(tabSecondResourceId());
				}
			}
			else {
				throw new Exception("UNDEFINED KIND!");
			}		

			mTabsAdapter.addTab(mBar.newTab().setText(mTabTitle), classOne, null);
		}
		catch(Exception e) {

			this.setUpTabsCount();

			setNotify(false);			
			setId(null);			

			if (hasOne && !hasSecond) {

				mTabsAdapter.addTab(
						tabOne,
						classOne, null);
			}

			else if (!hasOne && hasSecond) {	

				mTabsAdapter.addTab(
						tabSecond,
						classSecond, null);
			}

			else if (hasOne && hasSecond) {				

				mTabsAdapter.addTab(
						tabOne,
						classOne, null);

				mTabsAdapter.addTab(
						tabSecond,
						classSecond, null);	
			}

			else {
				/**
				 *  @TODO: set Notification and AlertDialog Message 
				 * */
				startActivity(StaticIntents.goHome(this));		
			}		
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(this.isMulti()) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && 
					event.getAction() == KeyEvent.ACTION_UP) {
				// handle your back button code here
				// consumes the back key event - ActionMode is not finished
				return true; 
			}
		}
		return super.dispatchKeyEvent(event);
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

	public static void removeCurrentTab(Context context) {

		if (hasOne && hasSecond && !isNotify) {

			mBar.removeAllTabs();			
			mBar.addTab(savedPosition == 0 ? tabOne : tabSecond, 0);			

			hasOne = savedPosition == 0 ? true : false;
			hasSecond = savedPosition == 1 ? true : false;

			mViewPager.disablePaging(false);
		}
		else {
			context.startActivity(StaticIntents.goHome(context));
		}
	}

	public static void removeUnusedTab() {

		if (hasOne && hasSecond && !isNotify) {

			Tab current = mBar.getSelectedTab();

			if(current.equals(tabOne)) {
				savedPosition = 1;
				mBar.removeTab(tabSecond);
			}
			else {
				savedPosition = 0;
				mBar.removeTab(tabOne);
			}

			mViewPager.disablePaging(true);
		}			
	}

	public static void readdUnusedTab() {
		if (hasOne && hasSecond && !isNotify) {
			mBar.addTab(
					savedPosition == 0 ? tabOne : tabSecond, savedPosition);
			mViewPager.disablePaging(false);
		}		
	}

	/**
	 * @return the isNotify
	 */
	public static boolean isNotify() {
		return isNotify;
	}

	/**
	 * @param isNotify the isNotify to set
	 */
	protected static void setNotify(boolean isNotify) {
		ProofMultiSelectFragmentActivity.isNotify = isNotify;
	}
}
