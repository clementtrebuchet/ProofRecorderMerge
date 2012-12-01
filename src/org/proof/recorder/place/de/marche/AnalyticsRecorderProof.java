package org.proof.recorder.place.de.marche;

import org.proof.recorder.R;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class AnalyticsRecorderProof extends AsyncTask<Void ,Integer, Void>{

	private static final String TAG = "AnalyticsRecorderProof";
	private GoogleAnalyticsTracker tracker;
	Context mContext;
	String mActivity;
	public AnalyticsRecorderProof(Context cont, String activity){
		mContext = cont;
		mActivity = activity;
	}
	public void googleIsMyFriend(){
		Log.e(TAG, "tracker pour google analytics");
		tracker = GoogleAnalyticsTracker.getInstance();
        tracker.startNewSession(mContext.getApplicationContext().getResources().getString(R.string.google_analytics_ua), mContext);
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
