package org.proof.recorder.bases.fragment;

import android.content.Context;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;

import org.proof.recorder.bases.utils.SetStaticContext;

@SuppressWarnings("unused")
public class ProofFragment extends SherlockFragment {
	
	private Context internalContext = null;
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        initialize();
    }

	private void initialize() {
		SetStaticContext.setConsoleTagName(this.getClass().getSimpleName());
		SetStaticContext.setStaticsContext(getActivity(), 1);
		setInternalContext(getActivity());
	}

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
}
