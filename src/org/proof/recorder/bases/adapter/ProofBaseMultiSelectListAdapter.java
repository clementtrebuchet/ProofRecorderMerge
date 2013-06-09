package org.proof.recorder.bases.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

public abstract class ProofBaseMultiSelectListAdapter extends ProofBaseListAdapter {
	
	protected boolean multiModeEnabled;

	/**
	 * @return the multiModeEnabled
	 */
	protected boolean isMultiModeEnabled() {
		return multiModeEnabled;
	}

	/**
	 * @param multiModeEnabled the multiModeEnabled to set
	 */
	protected void setMultiModeEnabled(boolean multiModeEnabled) {
		this.multiModeEnabled = multiModeEnabled;
	}

	public ProofBaseMultiSelectListAdapter(Context context, Object[] objects, int layoutResourceId, boolean multiModeEnabled, String broadcastName) {
		super(context, objects, layoutResourceId, broadcastName);
		setMultiModeEnabled(multiModeEnabled);
	}

	public ProofBaseMultiSelectListAdapter(Context context, List<Object> objects, int layoutResourceId, boolean multiModeEnabled, String broadcastName) {
		super(context, objects, layoutResourceId, broadcastName);
		setMultiModeEnabled(multiModeEnabled);
	}

	public ProofBaseMultiSelectListAdapter(Context context, int resource,
			int textViewResourceId, Object[] objects, int layoutResourceId, boolean multiModeEnabled, String broadcastName) {
		super(context, resource, textViewResourceId, objects, layoutResourceId, broadcastName);
		setMultiModeEnabled(multiModeEnabled);
	}

	public ProofBaseMultiSelectListAdapter(Context context, int resource,
			int textViewResourceId, List<Object> objects, int layoutResourceId, boolean multiModeEnabled, String broadcastName) {
		super(context, resource, textViewResourceId, objects, layoutResourceId, broadcastName);
		setMultiModeEnabled(multiModeEnabled);
	}
	
	public void uncheckAll() {
		checkItemsHandler(null, false);
	}
	
	public void toggleChecked(boolean checked) {		
		checkItemsHandler(null, checked);
	}
	
	private void checkItemsHandler(ViewGroup vg, boolean checked) {

		if(vg == null)
			vg = viewGroup;		
		
		for (int i = 0; i < vg.getChildCount(); i++) {
			View v = vg.getChildAt(i);
			if (v instanceof CheckBox) {
				CheckBox checkbox = (CheckBox) v;
				checkbox.setChecked(checked);				
			} else if (v instanceof ViewGroup) {
				checkItemsHandler((ViewGroup) v, checked);
			}
		}
	}
}
