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

public class FragmentListRecordTabs extends SherlockFragmentActivity {

	private static final String TAG = "FragmentListRecordTabs";

	private TabHost mTabHost;
	private ViewPager mViewPager;
	private TabsPagerAdapter mTabsAdapter;

	private static Class<?> clsInCommingRecords;
	private static Class<?> clsOutGoingRecords;
	
	private static boolean bIn, bOut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		 Bundle extraData = getIntent().getExtras();
		 String mIdOrTelephone = (String) extraData.get("mIdOrTelephone");
		 String mWhereClause = (String) extraData.get("mWhereClause");

		clsInCommingRecords = FragmentListRecordIn.InCommingCallsLoader.class;
		clsOutGoingRecords = FragmentListRecordOut.OutGoingCallsLoader.class;

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
		
		int in, out;		

		in = AndroidContactsHelper.getInRecordsCount(mIdOrTelephone, mWhereClause);
		out = AndroidContactsHelper.getOutRecordsCount(mIdOrTelephone, mWhereClause);
		
		Log.v(TAG, "TITLED: " + in + " " + "UNTITLED: " + out);

		if (in > 0) {
			bIn = true;
		}
		else
			bIn = false;

		if (out > 0) {
			bOut = true;
		}
		else
			bOut = false;		

		
		
		if (bIn && !bOut) {
			
			mTabsAdapter.addTab(
					mBar.newTab().setText(
							this.getString(R.string.strIncommingTab)),
							clsInCommingRecords, null);
		}
		
		else if (!bIn && bOut) {	
			
			mTabsAdapter.addTab(
					mBar.newTab().setText(
							this.getString(R.string.strOutcommingTab)),
							clsOutGoingRecords, null);
		}
		
		else if (bIn && bOut) {		
			

			mTabsAdapter.addTab(
					mBar.newTab().setText(
							this.getString(R.string.strIncommingTab)),
							clsInCommingRecords, null);

			mTabsAdapter.addTab(
					mBar.newTab().setText(
							this.getString(R.string.strOutcommingTab)),
							clsOutGoingRecords, null);	

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

	/*public static class TabsAdapter extends FragmentPagerAdapter implements
			ActionBar.TabListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}

		public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mActionBar = activity.getSupportActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(),
					info.args);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			mActionBar.setSelectedNavigationItem(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}*/
}
