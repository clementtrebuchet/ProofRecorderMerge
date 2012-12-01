package org.proof.recorder.fragment.contacts;

import org.proof.recorder.R;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.TabsPagerAdapter;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TabHost;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class FragmentListPhoneContactsTabs extends SherlockFragmentActivity {

	//private static final String TAG = "FragmentListPhoneContactsTabs";
	private TabHost mTabHost;
	private ViewPager mViewPager;
	private TabsPagerAdapter mTabsAdapter;

	private static Class<?> clsPhoneContacts;
	private static Class<?> clsExcludedContacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

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
	
	/**
	 * stop the recreation of the activity on Orientation Change the
	 * MediaRecorder, is therefore not recreated and keep recording on
	 * Orientation Changes
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	/*public void getContacts() {
		try {		
			
			contacts = ContactsDataHelper.getPhoneList(mContext);				
			((ContactAdapter) getListAdapter()).notifyDataSetChanged();				
			
		} catch (Exception e) {
			
			if(Settings.isDebug())
				Log.e(TAG, "E" + e.getMessage());
		}
	}
	
	private static ContactAdapter contactAdapter = null;
	private static ArrayList<Contact> contacts = null;
	private static Context mContext;
	
	private class LoadContactsList extends AsyncTask<Void, Integer, Long> {

		 
		 @Override
		    protected void onProgressUpdate(Integer... values){
		        super.onProgressUpdate(values);
		        // Mise à jour de la ProgressBar
		        //mProgressBar.setProgress(values[0]);
		    }
		 

		 @Override
	     protected void onPostExecute(Long result) {		 
	    		 contactAdapter = new ContactAdapter(mContext,
							R.layout.custom_contacts_list, contacts);	
				setListAdapter(contactAdapter);	    			         
	     }

		@Override
		protected Long doInBackground(Void... params) {
			getContacts();
			return null;
		}
	 }*/
}