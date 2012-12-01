package org.proof.recorder.fragment.dialog;

import org.proof.recorder.R;
import org.proof.recorder.utils.ConnectivityInfo;
import org.proof.recorder.utils.DeviceInfo;
import org.proof.recorder.utils.OsInfo;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ApplicationInformations extends SherlockFragmentActivity {
	
	private static final String TAG = "FragmentDashBoardPerformances";
	private static final String BREAK = "\n";
	
	private static DeviceInfo mDeviceInfo;
	private static OsInfo mOsInfo;
	private static TextView dlgmenu_battery_state;
	
	private static Context mContext = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_performances_dashboard);
		
		mContext = this;
		
		mOsInfo = new OsInfo();
		ConnectivityInfo mConnexionState = new ConnectivityInfo(this);
		mDeviceInfo = new DeviceInfo(mConnexionState, mOsInfo, this);
		
        ContentResolver cr = getContentResolver();
        
        final String mExcluded = mDeviceInfo.getExContactsAndNotCount(cr)[0];
        final String mNotExcluded = mDeviceInfo.getExContactsAndNotCount(cr)[1];     
        final char mNetwork = mDeviceInfo.getNetworkState();
        final String mCalls = mDeviceInfo.getVoicesAndCallsCount()[0];
        final String mVoices = mDeviceInfo.getVoicesAndCallsCount()[1];
        final String mFreeSpace = mDeviceInfo.getFreeSpaceOnDevice();
        final String mSpaceApp = mDeviceInfo.getSpaceConsumedByApp();
	}
}
