package org.proof.recorder.fragment.dialog;

import org.proof.recorder.R;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class NoneRecordsDialog extends SherlockFragmentActivity {
	
//	private final static String TAG = "CUSTOM_SEARCH_MODULE";
//	private final static String BR = "\n";
	
	private Button mOk;
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.none_records_info_dialog);
		
		mOk = (Button) findViewById(R.id.btnOk);
		
		mOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

}
