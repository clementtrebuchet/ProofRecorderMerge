package org.proof.recorder.fragment.voice;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.Settings.mFormat;
import org.proof.recorder.utils.AlertDialogHelper;
import org.proof.recorder.utils.AudioHandler;
import org.proof.recorder.utils.QuickActionDlg;

import android.content.Context;
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

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class FragmentVoiceMediaRecorder extends SherlockFragmentActivity
{	
	
	//private static final String TAG = "FragmentVoiceMediaRecorder";
	private static Context mContext;
	private static mFormat forma;
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle); 
        mContext = this;
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
	    private AudioHandler mRecorder = null;
	    private static boolean onRecord;
	    
	    private ImageButton btnStartRecorder, btnStopRecorder;
	    private ImageView backG; 
	    private TextView textI;
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
		
		private OnClickListener playCallBack = new OnClickListener() {
	        @Override
			public void onClick(View v) {
	        	if(!onRecord) {
	        		new Thread(new Runnable() {
	    				@Override
						public void run() {
	    					try {
	    						startRecording();
	    						onRecord = true;
	    					} catch (Exception e) {
	    						// TODO Auto-generated catch block
	    						e.printStackTrace();
	    					}
	    				}
	    			}).start();
	        		backG.setImageDrawable(getResources().getDrawable(R.drawable.voicingrec));
	        		textI.setText("Début de l'enregistrement");
	        		
	        	}
	        }
	    }; 
	    
	    private OnClickListener stopCallBack = new OnClickListener() {
	        @Override
			public void onClick(View v) {
	        	if(onRecord) {
	        		backG.setImageDrawable(getResources().getDrawable(R.drawable.voicing));
	        		textI.setText("Fin de l'enregistrement");
	        		stopRecording();
					onRecord = false;
	        		
	        	}
	        }
	    };     

	    private void startRecording() {
	    	String mFormat = Settings.getAudioFormat(mContext);
	    	
			if (mFormat.equals("3GP")) {

				forma = Settings.mFormat.THREE_GP;

			} else if (mFormat.equals("WAV")) {
				forma = Settings.mFormat.WAV;

			} else if (mFormat.equals("MP3")) {
				forma = Settings.mFormat.MP3;

			} else if (mFormat.equals("OGG")) {
				forma = Settings.mFormat.OGG;

			}else {
				forma = Settings.mFormat.THREE_GP;
			}
        	mRecorder = new AudioHandler(
	        		getActivity(), 
	        		forma,
	        		org.proof.recorder.Settings.mType.VOICE_TITLED
	        ); 
	        mRecorder.startRecording();
	    }
	   

	    private void stopRecording() {
	    	AlertDialogHelper.openVoiceEditDialog(getActivity());
	    	mRecorder.stopRecording();
	        mRecorder = null;	        
	    }	    	    

	    @Override
	    public void onPause() {
	        super.onPause();
	        if (mRecorder != null) {
	        	if(forma == Settings.mFormat.THREE_GP )mRecorder.releaseThreeGpRecording();
	            mRecorder = null;
	        }
	    }
	}
}