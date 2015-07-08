package org.proof.recorder.place.de.marche;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import org.proof.recorder.R;

public class AnalyticsRecorderProof extends AsyncTask<Void ,Integer, Void>{

	private final Context mContext;
	private final String mActivity;

	public AnalyticsRecorderProof(Context cont) {
		mContext = cont;
		mActivity = org.proof.recorder.ProofRecorderActivity.TAG;
	}

	private void googleIsMyFriend() {

		GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(
				mContext.getApplicationContext().getResources().getString(R.string.google_analytics_ua),
				mContext);

		tracker.trackPageView(mActivity);
		tracker.trackEvent(
				mActivity,  // Category
				"OnPage",  // Action
				"See", // Label
				201);       // Value
		tracker.dispatch();
	}
	@Override
	protected Void doInBackground(Void... params) {
		googleIsMyFriend();
		return null;
		
		
	}
	
	

}
