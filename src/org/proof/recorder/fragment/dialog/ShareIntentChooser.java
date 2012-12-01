package org.proof.recorder.fragment.dialog;

import java.io.File;
import java.util.List;

import org.proof.recorder.R;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class ShareIntentChooser extends
		FragmentActivity {

	private ImageButton mShareBlueToothIcon, mShareMailIcon, mShareYouTubeIcon, mShareFaceBookIcon, mShareDropBoxIcon, mShareGmailIcon;
	private static Bundle mBundle;
	private static String mAttachFilePath;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_intent_chooser);

		mBundle = getIntent().getExtras();
		mAttachFilePath = mBundle.getString("mAttachFilePath");

		mShareMailIcon = (ImageButton) findViewById(R.id.mShareMailIcon);
		mShareYouTubeIcon = (ImageButton) findViewById(R.id.mShareYouTubeIcon);
		mShareFaceBookIcon = (ImageButton) findViewById(R.id.mShareFaceBookIcon);
		mShareGmailIcon = (ImageButton) findViewById(R.id.mShareGmailIcon);
		mShareDropBoxIcon = (ImageButton) findViewById(R.id.mShareDropBoxIcon);
		mShareBlueToothIcon = (ImageButton) findViewById(R.id.mShareBlueToothIcon);

		mShareMailIcon.setOnClickListener(mShareMailAction);
		mShareGmailIcon.setOnClickListener(mShareGmailAction);
		mShareFaceBookIcon.setOnClickListener(mShareFaceBookAction);
		mShareYouTubeIcon.setOnClickListener(mShareYouTubeAction);
		mShareDropBoxIcon.setOnClickListener(mShareDropBoxAction);
		mShareBlueToothIcon.setOnClickListener(mShareBlueToothAction);
	}

	private final OnClickListener mShareMailAction = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			startMailIntent("mail");
		}
	};
	
	private final OnClickListener mShareBlueToothAction = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			startMailIntent("blue");
		}
	};
	
	private final OnClickListener mShareGmailAction = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			startMailIntent("gmail");
		}
	};
	
	private final OnClickListener mShareDropBoxAction = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			onBackPressed();
		}
	};

	private final OnClickListener mShareYouTubeAction = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			onBackPressed();
		}
	};

	private final OnClickListener mShareFaceBookAction = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			startMailIntent("face");
		}
	};

	private void startMailIntent(String mType) {

		boolean found = false;
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("audio/3gp");

		// gets the list of intents that can be loaded.
		List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(
				share, 0);
		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				if (info.activityInfo.packageName.toLowerCase()
						.contains(mType)
						|| info.activityInfo.name.toLowerCase()
								.contains(mType)) {

					share.putExtra(Intent.EXTRA_SUBJECT,
							getString(R.string.custom_intent_chooser_subject));
					share.putExtra(Intent.EXTRA_TEXT,
							getString(R.string.custom_intent_chooser_text));
					share.putExtra(Intent.EXTRA_STREAM,
							Uri.fromFile(new File(mAttachFilePath))); 
					share.setPackage(info.activityInfo.packageName);
					found = true;
					break;
				}
			}
			if (!found)
				return;

			startActivity(Intent.createChooser(share, "Select"));
		}
	}
}
