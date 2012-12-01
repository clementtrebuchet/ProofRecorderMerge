package org.proof.recorder.syncron.fragment;

import static org.proof.recorder.utils.GCMUtils.DISPLAY_MESSAGE_ACTION;
import static org.proof.recorder.utils.GCMUtils.SENDER_ID;
import static org.proof.recorder.utils.GCMUtils.SERVER_URL;

import org.proof.recorder.R;
import org.proof.recorder.service.ServerUtilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;

public class GMCActivity {
	private static final String TAG = "GMCActivity";
	TextView mDisplay;
    AsyncTask<Void, Void, Void> mRegisterTask;
    Context mCont; 
   
    public  GMCActivity(Context mContext) {
    	mCont = mContext;
        checkNotNull(SERVER_URL, "SERVER_URL");
        checkNotNull(SENDER_ID, "SENDER_ID");
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(mCont);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(mCont);
       
       
        mCont.registerReceiver(mHandleMessageReceiver,
                new IntentFilter(DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(mCont);
        if (regId.equals("")) {
            // Automatically registers application on startup.
        	GCMRegistrar.register(mCont, SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(mContext)) {
                // Skips registration.
                Log.v(TAG,mCont.getString(R.string.already_registered) + "\n");
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = mCont;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered =
                                ServerUtilities.register(context, regId);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }
    }

    
    public void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        mCont.unregisterReceiver(mHandleMessageReceiver);
        GCMRegistrar.onDestroy(mCont);
        
    }

    private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(
            		mCont.getString(R.string.error_config, name));
        }
    }

    public static final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString("firstData");
            Log.e(TAG,newMessage + "Message recu\n");
        }

		
    };


}
