package org.proof.recorder.fragment.dialog;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.proof.recorder.R;
import org.proof.recorder.bases.activity.ProofFragmentActivity;
import org.proof.recorder.utils.ConnectivityInfo;
import org.proof.recorder.utils.DeviceInfo;
import org.proof.recorder.utils.Log.Console;
import org.proof.recorder.utils.OsInfo;

public class PhoneInformations extends ProofFragmentActivity {

	private static final String TAG = "CapabilitiesDashBoard";
	private static final String BREAK = "\n";

	private static TextView dlgmenu_battery_state;

	private static final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		int scale = -1;
        int level = -1;
        int voltage = -1;
        int temp = -1;
        @Override
        public void onReceive(Context context, Intent intent) {
        	
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            
            Console.print_debug(
            		"level is " + 
		            level + "/" + scale + 
		            ", temp is " + temp + 
		            ", voltage is " + voltage
            ); 
            
            dlgmenu_battery_state.setText(context.getString(R.string.dashbord_dialog_battery_state) + " " + level + " %");
        }
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.dashboard_dialog);
		registerReceiver(mBatInfoReceiver,
				new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		OsInfo mOsInfo = new OsInfo();
		ConnectivityInfo mConnexionState = new ConnectivityInfo(this);
		DeviceInfo mDeviceInfo = new DeviceInfo(mConnexionState, mOsInfo, this);
		
        ContentResolver cr = getContentResolver();
        
        final String mExcluded = mDeviceInfo.getExContactsAndNotCount(cr)[0];
        final String mNotExcluded = mDeviceInfo.getExContactsAndNotCount(cr)[1];     
        final char mNetwork = mDeviceInfo.getNetworkState();
        final String mCalls = mDeviceInfo.getVoicesAndCallsCount()[0];
        final String mVoices = mDeviceInfo.getVoicesAndCallsCount()[1];
        final String mFreeSpace = mDeviceInfo.getFreeSpaceOnDevice();
        final String mSpaceApp = mDeviceInfo.getSpaceConsumedByApp();
        
        String mDeviceMsg = "";
		mDeviceMsg += "Network State: " + mNetwork + BREAK;
		mDeviceMsg += "Calls Count: " + mCalls + BREAK;
		mDeviceMsg += "Voices Count: " + mVoices + BREAK;
		mDeviceMsg += "Free space on SD card: " + mFreeSpace + BREAK;
		mDeviceMsg += "Space used by the app: " + mSpaceApp + BREAK;
		mDeviceMsg += "Excluded Contacts from Recording Service: " + mExcluded + BREAK;
		mDeviceMsg += "Non-Excluded Contacts from Recording Service: " + mNotExcluded;
		
		Log.i(TAG, mDeviceMsg);	
		
		TextView dlgmenu_text1 = (TextView) findViewById(R.id.dlgmenu_text1);
		
		switch (mNetwork) {
		case 'w':
			dlgmenu_text1.setText(getString(R.string.dashbord_dialog_network) + " WIFI");
			break;
			
		case 'g':
			dlgmenu_text1.setText(getString(R.string.dashbord_dialog_network) + " 3G");
			break;
			
		case 'r':
			dlgmenu_text1.setText(getString(R.string.dashbord_dialog_network) + " STANDARD");
			break;

		default:
			break; // should never happen !
		}				
		
		TextView dlgmenu_text2 = (TextView) findViewById(R.id.dlgmenu_text2);
		dlgmenu_text2.setText(getString(R.string.dashbord_dialog_calls) + " " + mCalls);
		
		TextView dlgmenu_text3 = (TextView) findViewById(R.id.dlgmenu_text3);
		dlgmenu_text3.setText(getString(R.string.dashbord_dialog_voices) + " " + mVoices);
		
		TextView dlgmenu_text4 = (TextView) findViewById(R.id.dlgmenu_text4);
		dlgmenu_text4.setText(getString(R.string.dashbord_dialog_excluded_contacts) + " " + mExcluded);
		
		TextView dlgmenu_text5 = (TextView) findViewById(R.id.dlgmenu_text5);
		dlgmenu_text5.setText(getString(R.string.dashbord_dialog_non_excluded_contacts) + " " + mNotExcluded);
		
		dlgmenu_battery_state = (TextView) findViewById(R.id.dlgmenu_text6);
		dlgmenu_battery_state.setText("loading ...");
		
		TextView dlgmenu_text7 = (TextView) findViewById(R.id.dlgmenu_text7);
		dlgmenu_text7.setText(getString(R.string.dashbord_dialog_free_space) + " " + mFreeSpace);
		
		TextView dlgmenu_text8 = (TextView) findViewById(R.id.dlgmenu_text8);
		dlgmenu_text8.setText(getString(R.string.dashbord_dialog_app_space) + " " + mSpaceApp);
        
    }
    
    @Override
    public void onDestroy() {    	
    	unregisterReceiver(mBatInfoReceiver);
    	super.onDestroy();
    }
}
