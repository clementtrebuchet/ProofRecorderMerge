package org.proof.recorder.preferences;

import org.proof.recorder.R;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class SettingsTabs extends TabActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.tabs_settings);

	    //Resources res = getResources();
	    TabHost tabHost = getTabHost();
	    TabHost.TabSpec spec;
	    Intent intent;

	    intent = new Intent(this, Preference.class);
	    spec = tabHost.newTabSpec("global_service").setIndicator(prepareTabView(this, getString(R.string.strSettingsGlobalServices),
	                      R.drawable.settings_calls))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent(this, DeviceCapabilities.class);
	    spec = tabHost.newTabSpec("device_capabilities").setIndicator(prepareTabView(this, getString(R.string.strSettingsDeviceCapabilities),
	                      R.drawable.settings_audio))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent(this, FormulaPreferences.class);
	    spec = tabHost.newTabSpec("options").setIndicator(prepareTabView(this, getString(R.string.strSettingsOptions),
	                      R.drawable.settings_infos))
	                  .setContent(intent);	    
	    tabHost.addTab(spec);
	    
	    /*intent = new Intent(this, ServerAccount.class);
	    spec = tabHost.newTabSpec("Secure").setIndicator(prepareTabView(this, "Autentification",
	                      R.drawable.settings_auth))
	                  .setContent(intent);	    
	    tabHost.addTab(spec);*/

	    tabHost.setCurrentTab(0);
	}
	
	
	public static View prepareTabView(Context context, String text, int mRid) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.tab_indicator, null);
        TextView tv = (TextView) view.findViewById(R.id.tabIndicatorTextView);
        ImageView img = (ImageView) view.findViewById(R.id.fakeNativeTabImageView);
        img.setBackgroundResource(mRid);
        tv.setText(text);

        return view;
    }
}
