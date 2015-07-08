package org.proof.recorder.fragment.notes;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TabHost;

import com.actionbarsherlock.app.ActionBar;

import org.proof.recorder.R;
import org.proof.recorder.bases.activity.ProofFragmentActivity;
import org.proof.recorder.fragment.phone.FragmentListRecordDetail;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.TabsPagerAdapter;

public class FragmentNoteTabs extends ProofFragmentActivity {

	//private static final String TAG = "FragmentRecordsNoteTabs";

	private TabHost mTabHost;
	public static String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_listrecord_detail_tab);

		ActionBar mBar = getSupportActionBar();

		mBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mBar.setHomeButtonEnabled(true);

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
		mTabHost.setup();
		Bundle bundle = getIntent().getExtras();
		id = (String) bundle.get("id");
		TabsPagerAdapter mTabsAdapter = new TabsPagerAdapter(this, mViewPager);

		mTabsAdapter.addTab(
				mBar.newTab().setText(this.getString(R.string.noteTabDetail)),
				FragmentListRecordDetail.CustomLoader.class);
		mTabsAdapter.addTab(
				mBar.newTab().setText(this.getString(R.string.noteTabNote)),
				FragmentListNotes.class);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
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