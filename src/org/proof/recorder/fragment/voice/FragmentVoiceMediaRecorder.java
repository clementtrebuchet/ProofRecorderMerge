package org.proof.recorder.fragment.voice;

import org.proof.recorder.R;
import org.proof.recorder.bases.activity.ProofFragmentActivity;
import org.proof.recorder.receivers.AudioRecorderReceiver;
import org.proof.recorder.service.DataPersistanceManager;
import org.proof.recorder.utils.AlertDialogHelper;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.Log.Console;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class FragmentVoiceMediaRecorder extends ProofFragmentActivity {	

	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.fragment_voice_recorder);
        FragmentManager fm = getSupportFragmentManager();

		if (fm.findFragmentById(R.id.fragLayout_voice_recorder) == null) {
			Fragment list = new VoiceRecorderFragment();
			fm.beginTransaction().add(R.id.fragLayout_voice_recorder, list).commit();
		}
		
		QuickActionDlg.setmContext(this);		
		getSupportActionBar().setHomeButtonEnabled(true);
    }
	
    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {		
		return QuickActionDlg.mainUiMenuHandler(menu);
    }
	
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
	    return QuickActionDlg.mainActionsMenuHandler(item);
	}
	
	public static class VoiceRecorderFragment extends Fragment {
	    private static boolean onRecord;
	    
	    private ImageButton btnStartRecorder, btnStopRecorder;
	    private ImageView backG; 
	    private TextView textI;
	    
	    private DataPersistanceManager dpm = null;
	    
	    private boolean phoneRecording;
	    /**
	     * stop the recreation of the activity on Orientation Change
	     * the MediaRecorder, is therefore not recreated and keep recording on Orientation Changes
	     */
	    @Override 
	    public void onConfigurationChanged(Configuration newConfig) { 
	        super.onConfigurationChanged(newConfig);
	    }
	    
	    /**
	     * The Constructor must be public and empty to prevent the activity from crash while being recreated.
	     */
	    public VoiceRecorderFragment() {
	    	
	    }
	    
	    @Override
	    public void onCreate(Bundle icicle) {
	        super.onCreate(icicle); 
	        AlertDialogHelper.setContext(getActivity());
	        
	        setRetainInstance(true); // TODO: check efficiency of it!!!
	    }

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			textI = (TextView) getView().findViewById(R.id.pinfo);
			backG = (ImageView) getView().findViewById(R.id.background);
			
			btnStartRecorder = (ImageButton) getView().findViewById(R.id.play);
			btnStopRecorder = (ImageButton) getView().findViewById(R.id.stop);
			
			btnStartRecorder.setOnClickListener(playCallBack);
			btnStopRecorder.setOnClickListener(stopCallBack);
			
			dpm = new DataPersistanceManager();
			
			if(dpm.isProcessing()) {
				setOnRecordScene();
			}
			else {
				setNoRecordScene();
			}
			
		}		

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = super.onCreateView(inflater, container, savedInstanceState);
			view = inflater.inflate(R.layout.fragment_voice_recorder_in, container, false);
			return view;
		}

		public void setRecordButton(int image) {		
			Drawable replacer = getResources().getDrawable(image);
			btnStartRecorder.setImageDrawable(replacer);
			btnStartRecorder.invalidate();
		}
		
		private void setOnRecordScene() {			
			onRecord = true;
    		backG.setImageDrawable(getResources().getDrawable(R.drawable.voicingrec));
    		textI.setText(getString(R.string.start_recording));
		}
		
		private void setNoRecordScene() {
			onRecord = false;
			backG.setImageDrawable(getResources().getDrawable(R.drawable.voicing));
    		textI.setText(getString(R.string.stop_recording));		
		}
		
		private OnClickListener playCallBack = new OnClickListener() {
	        @Override
			public void onClick(View v) {
	        	if(!onRecord) {
	        		dpm = new DataPersistanceManager();
	    	    	
	    	    	// If on a call, the user try to start an Audio Record
	    	    	// It won't start it avoiding bugs!
	    	    	
	    	    	phoneRecording = Boolean.parseBoolean(
	    	    			dpm.retrieveCachedRows("PhoneServiceRunning"));
	    	    	
	    	    	if(!phoneRecording) {
	    	    		startRecording();
	    	    		setOnRecordScene();
	    	    	}
	    	    	else {
	    	    		String message = getString(R.string.action_not_allowed) + "\n";
 	    			   	message += getString(R.string.record_on_recording);
 	    			   
 	    			   	Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
	    	    	}
	        	}
	        }
	    }; 
	    
	    private OnClickListener stopCallBack = new OnClickListener() {
	        @Override
			public void onClick(View v) {
	        	if(onRecord) {
	        		
	        		dpm = new DataPersistanceManager();
	    	    	
	    	    	// If on a call, the user try to stop an Audio Record
	    	    	// It won't stop it avoiding bugs!
	    	    	
	    	    	phoneRecording = Boolean.parseBoolean(
	    	    			dpm.retrieveCachedRows("PhoneServiceRunning"));
	    	    	
	    	    	if(!phoneRecording) {
	    	    		stopRecording();
		        		setNoRecordScene();
	    	    	}        		       		
	        	}
	        }
	    };		     

	    private void startRecording() {
	    	
	    	if(!dpm.isProcessing()) {
	    		Intent audioService = new Intent(getActivity(), AudioRecorderReceiver.class);
		    	audioService.setAction("android.intent.action.START_AUDIO_RECORDER");		    	
		    	getActivity().sendBroadcast(audioService);
	    	}	    	
	    }
	   

	    private void stopRecording() {
	    	
	    	dpm = new DataPersistanceManager();
	    	
	    	if(dpm.isProcessing()) {
	    		Intent audioService = new Intent(getActivity(), AudioRecorderReceiver.class);
		    	audioService.setAction("android.intent.action.STOP_AUDIO_RECORDER");		    	
		    	getActivity().sendBroadcast(audioService);
	    	}      
	    }	    	    

	    @Override
	    public void onPause() {
	        super.onPause();	        
	        Console.print_debug("onPause");
	    }
	}
}