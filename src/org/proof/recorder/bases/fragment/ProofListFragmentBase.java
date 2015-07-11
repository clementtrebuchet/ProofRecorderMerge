package org.proof.recorder.bases.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockListFragment;

import org.proof.recorder.R;
import org.proof.recorder.bases.broadcast.ProofBroadcastReceiver;
import org.proof.recorder.bases.utils.SetStaticContext;

@SuppressWarnings("unused")
public abstract class ProofListFragmentBase extends SherlockListFragment {

	private final ProofBroadcastReceiver listEventSender = new ProofBroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			super.onReceive(context, intent);
			handleOnReceive(context, intent);
		}
	};

	private Context internalContext = null;

	ViewGroup viewGroup = null;
	protected Bundle extraData = null;

	private AlertDialog.Builder alertDlg;

	private ProgressDialog progressDlg;

	private int alertDlgTitle = 0;
	private int alertDlgText = 0;
	private int alertDlgCancelBtn = 0;
	private int alertDlgOkBtn = 0;

	@SuppressWarnings("EmptyMethod")
	protected abstract void alertDlgCancelAction(DialogInterface dialog, int which);
	protected abstract void handleOnReceive(Context context, Intent intent);
	protected abstract void alertDlgOkAction(DialogInterface dialog, int which);

	protected ArrayAdapter<Object> listAdapter = null;

	protected volatile boolean reverseCollection = false;
	private volatile boolean screenLocked = false;

	public volatile static boolean multiSelectEnabled = false;

	/**
	 * @return the internalContext
	 */
	protected Context getInternalContext() {
		return internalContext;
	}

	/**
	 * @param internalContext the internalContext to set
	 */
	private void setInternalContext(Context internalContext) {
		this.internalContext = internalContext;
	}

	private void initializedContext() {
		SetStaticContext.setConsoleTagName(this.getClass().getSimpleName());
		SetStaticContext.setStaticsContext(getActivity(), 1);
		setInternalContext(getActivity());
	}

	private void initialize() {
		initializedContext();		
		setRetainInstance(true);
		setupDlgs();
	}

	protected String getBroadcastName() {
		return "$listEventSender$" + this.getClass().getName();
	}

	private void registerLocalBroadcastReceiver() {
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				listEventSender, new IntentFilter(getBroadcastName()));
	}
	
	private void unregisterLocalBroadcastReceiver() {
		LocalBroadcastManager.getInstance(
				getActivity()).unregisterReceiver(listEventSender);
	}
	
	private void setupDlgs() {
		setupAlertDlg();		
		setupProgressDlg();
	}
	
	@SuppressWarnings("UnusedAssignment")
	private void setupProgressDlg() {
		
		destroyProgress();
		
		progressDlg = new ProgressDialog(getInternalContext());
		
		progressDlg.setCancelable(false);
		progressDlg.setIndeterminate(true);

		String postProgressDlgText = "";
		String preProgressDlgText = "";
		int progressDlgTextId = 0;
		progressDlg.setMessage(preProgressDlgText +
				getInternalContext().getText(
						R.string.loading) +
				postProgressDlgText);
	}
	
	protected void displayProgress() {
		if(!progressDlg.isShowing())
			progressDlg.show();
	}

	protected void refreshProgress() {
		setupProgressDlg();
	}

	private void hideProgress() {
		if(progressDlg.isShowing())
			progressDlg.hide();
	}
	
	private void destroyProgress() {
		if(progressDlg != null) {
			//noinspection ConstantConditions
			if(progressDlg instanceof ProgressDialog) {
				hideProgress();
				progressDlg.cancel();
				progressDlg = null;
			}
		}		
	}
	
	private void setupAlertDlg() {

		if(alertDlgTitle == 0)
			alertDlgTitle = R.string.strUACTitle;

		if(alertDlgText == 0)
			alertDlgText = R.string.strUACDeleteFolder;

		if(alertDlgCancelBtn == 0)
			alertDlgCancelBtn = R.string.strUACCancelBtn;

		if(alertDlgOkBtn == 0)
			alertDlgOkBtn = R.string.strUACConfirmBtn;

		alertDlg = new AlertDialog.Builder(getInternalContext());
		alertDlg.setTitle(getInternalContext().getString(alertDlgTitle));
		alertDlg.setMessage(getInternalContext().getString(alertDlgText));

		//noinspection unused
		alertDlg.setNegativeButton(
				getInternalContext().getString(alertDlgCancelBtn),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDlgCancelAction(dialog, which);
					}
				});

		//noinspection unused
		alertDlg.setPositiveButton(
				getInternalContext().getString(alertDlgOkBtn),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDlgOkAction(dialog, which);
					}
				});	
	}

	void displayAlert() {
		alertDlg.show();
	}
	
	private void destroyAlert() {
		if(alertDlg != null) {
			//noinspection ConstantConditions
			if(alertDlg instanceof AlertDialog.Builder) {
				alertDlg = null;
			}
		}		
	}

	void lockScreenOrientation() {

		if(!screenLocked) {
			switch (getResources().getConfiguration().orientation) {

			case Configuration.ORIENTATION_PORTRAIT:
				if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO){
					getSherlockActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				} else {
					int rotation = getSherlockActivity().getWindowManager().getDefaultDisplay().getRotation();
					if(rotation == android.view.Surface.ROTATION_90|| rotation == android.view.Surface.ROTATION_180){
						getSherlockActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
					} else {
						getSherlockActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					}
				}   
				break;

			case Configuration.ORIENTATION_LANDSCAPE:
				if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO){
					getSherlockActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				} else {
					int rotation = getSherlockActivity().getWindowManager().getDefaultDisplay().getRotation();
					if(rotation == android.view.Surface.ROTATION_0 || rotation == android.view.Surface.ROTATION_90){
						getSherlockActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					} else {
						getSherlockActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
					}
				}
				break;
			}

			screenLocked = true;
		}	
	}

	void unlockScreenOrientation() {
		getSherlockActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		screenLocked = false;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		initialize();
		registerLocalBroadcastReceiver();	
	}

	@Override
	public void onResume() {
		super.onResume();
		initializedContext();
		registerLocalBroadcastReceiver();
	}
	
	@Override
	public void onPause() {		
		unregisterLocalBroadcastReceiver();
		super.onPause();
	}
	
	@Override
	public void onDestroy() {		
		unregisterLocalBroadcastReceiver();
		
		destroyProgress();
		destroyAlert();
		
		super.onDestroy();
	}

	@SuppressWarnings("EmptyMethod")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

}
