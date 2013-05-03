package org.proof.recorder.fragment.voice;

import org.proof.recorder.ProofRecorderActivity;
import org.proof.recorder.R;
import org.proof.recorder.utils.AlertDialogHelper;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.StaticIntents;
import org.proof.recorder.utils.TabsPagerAdapter;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TabHost;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class FragmentListVoiceTabs extends SherlockFragmentActivity {

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
		
		bTitled = ProofRecorderActivity.bTitled;
		bUntitled = ProofRecorderActivity.bUntitled;
		
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
		
		if (bTitled && !bUntitled) {
			
			mTabsAdapter.addTab(
					mBar.newTab().setText(
							this.getString(R.string.strTitledVoicesTab)),
					clsTitledVoices, null);
		}
		
		else if (!bTitled && bUntitled) {	
			
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
		
		AlertDialogHelper.hideProgressDialog();
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
