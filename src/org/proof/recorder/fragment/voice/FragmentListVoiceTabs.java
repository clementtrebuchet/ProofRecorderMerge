package org.proof.recorder.fragment.voice;

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

public class FragmentListVoiceTabs extends SherlockFragmentActivity {

	private static final String TAG = "FragmentListRecordFoldersTabs";

	private TabHost mTabHost;
	private ViewPager mViewPager;
	private TabsPagerAdapter mTabsAdapter;

	private static Class<?> clsUnTitledVoices;
	private static Class<?> clsTitledVoices;
	
	private static boolean bTitled, bUntitled;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		setContentView(R.layout.fragment_inout_records_tabs);
		
		clsTitledVoices = FragmentListVoice.VoiceListLoader.class;
		clsUnTitledVoices = FragmentListVoiceUntitled.VoiceListLoader.class;
		
		ActionBar mBar = getSupportActionBar();
		mBar.setHomeButtonEnabled(true);
		mBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mTabHost.setup();
		
		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}

		mTabsAdapter = new TabsPagerAdapter(this, mViewPager);

		int titled, untitled;		

		titled = AndroidContactsHelper.getTitledVoiceCount();
		untitled = AndroidContactsHelper.getUnTitledVoiceCount();
		
		Log.v(TAG, "TITLED: " + titled + " " + "UNTITLED: " + untitled);

		if (titled > 0) {
			bTitled = true;
		}
		else
			bTitled = false;

		if (untitled > 0) {
			bUntitled = true;
		}
		else
			bUntitled = false;		

		
		
		if (bTitled && !bUntitled) {
			
			mTabsAdapter.addTab(
					mBar.newTab().setText(
							this.getString(R.string.strTitledVoicesTab)),
					clsTitledVoices, null);
		}
		
		else if (!bTitled && bUntitled) {
			
			/*setContentView(R.layout.fragmentdroit);
			FragmentManager fm = getSupportFragmentManager();
			
			ActionBar mBar = getSupportActionBar();
			mBar.setHomeButtonEnabled(true);

			// Create the list fragment and add it as our sole content.
			if (fm.findFragmentById(R.id.Content_FragmentOne) == null) {
				FragmentListVoiceUntitled.VoiceListLoader list = new FragmentListVoiceUntitled.VoiceListLoader();
				fm.beginTransaction().add(R.id.Content_FragmentOne, list).commit();
			}*/		
			
			mTabsAdapter.addTab(
					mBar.newTab().setText(
							this.getString(R.string.strUnTitledVoicesTab)),
					clsUnTitledVoices, null);
		}
		
		else if (bTitled && bUntitled) {		
			

			mTabsAdapter.addTab(
					mBar.newTab().setText(
							this.getString(R.string.strTitledVoicesTab)),
					clsTitledVoices, null);

			mTabsAdapter.addTab(
					mBar.newTab().setText(
							this.getString(R.string.strUnTitledVoicesTab)),
					clsUnTitledVoices, null);

			

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
}
