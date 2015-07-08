package org.proof.recorder.fragment.dialog;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.proof.recorder.R;
import org.proof.recorder.bases.activity.ProofFragmentActivity;

public class NoneRecordsDialog extends ProofFragmentActivity {
	
//	private final static String TAG = "CUSTOM_SEARCH_MODULE";
//	private final static String BR = "\n";

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.none_records_info_dialog);

		Button mOk = (Button) findViewById(R.id.btnOk);
		
		mOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

}
