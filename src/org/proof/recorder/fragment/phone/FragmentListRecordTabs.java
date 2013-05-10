package org.proof.recorder.fragment.phone;

import org.proof.recorder.R;
import org.proof.recorder.bases.activity.ProofFragmentActivity;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.StaticIntents;
import org.proof.recorder.utils.TabsPagerAdapter;
import org.proof.recorder.utils.Log.Console;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TabHost;

import com.actionbarsherlock.app.ActionBar;

public class FragmentListRecordTabs extends ProofFragmentActivity {

	private TabHost mTabHost;
	private ViewPager mViewPager;
	private TabsPagerAdapter mTabsAdapter;

	private static Class<?> clsInCommingRecords;
	private static Class<?> clsOutGoingRecords;
	
	private static boolean bIn, bOut, isNotify;
	
	private static long mRecordId;

	/**
	 * @return the isNotify
	 */
	public static boolean isNotify() {
		return isNotify;
	}

	/**
	 * @param isNotify the isNotify to set
	 */
	public static void setNotify(boolean isNotify) {
		FragmentListRecordTabs.isNotify = isNotify;
	}
	
	/**
	 * @return the mRecordId
	 */
	public static long getRecordId() {
		return mRecordId;
	}

	/**
	 * @param mRecordId the mRecordId to set
	 */
	private static void setRecordId(long mRecordId) {
		FragmentListRecordTabs.mRecordId = mRecordId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_inout_records_tabs);
		
		Console.setTagName(this.getClass().getSimpleName());
		
		Bundle extraData = getIntent().getExtras();
		
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
		
		try {
			
			
			
			setRecordId((long) extraData.getLong("RecordId"));
			String mDirection = (String) extraData.getString("Sense");
			setNotify((boolean) extraData.getBoolean("isNotify"));
			
			Class<?> tab = FragmentListRecordIn.InCommingCallsLoader.class;
			
			String tabTitle;
			if (mDirection.equalsIgnoreCase("e")) {
				tabTitle = getString(R.string.strIncommingTab);
			}
			else {
				tabTitle = getString(R.string.strOutcommingTab);
			}
			
			mTabsAdapter.addTab(mBar.newTab().setText(tabTitle), tab, null);
			
			Console.print_exception("C'est le bad");
		}
		catch(Exception e) {
			
			Console.print_debug("C'est Bon");
			
			setNotify(false);
			
			String mIdOrTelephone = (String) extraData.get("mIdOrTelephone");
			String mWhereClause = (String) extraData.get("mWhereClause");

			clsInCommingRecords = FragmentListRecordIn.InCommingCallsLoader.class;
			clsOutGoingRecords = FragmentListRecordOut.OutGoingCallsLoader.class;			
			
			int in, out;		

			in = AndroidContactsHelper.getInRecordsCount(mIdOrTelephone, mWhereClause);
			out = AndroidContactsHelper.getOutRecordsCount(mIdOrTelephone, mWhereClause);
			
			Console.print_debug("TITLED: " + in + " " + "UNTITLED: " + out);

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
}
