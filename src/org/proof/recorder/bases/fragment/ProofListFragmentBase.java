package org.proof.recorder.bases.fragment;

import org.proof.recorder.bases.utils.SetStaticContext;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockListFragment;

public abstract class ProofListFragmentBase extends SherlockListFragment {
	
	private Context internalContext = null;
	
	protected ViewGroup viewGroup = null;
	protected Bundle extraData = null;
	
	protected ArrayAdapter<Object> listAdapter = null;
	
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
	}
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		initialize();
	}

	@Override
	public void onResume() {
		super.onResume();
		initializedContext();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

}
