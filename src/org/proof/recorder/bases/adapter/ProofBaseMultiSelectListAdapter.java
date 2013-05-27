package org.proof.recorder.bases.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

public abstract class ProofBaseMultiSelectListAdapter extends ArrayAdapter<Object> {
	
	protected abstract void handleView(final int item, View view);
	protected abstract void handleEmptyView(final int item, View view);
	
	protected Object objects;		
	protected int layoutResourceId;	
	protected boolean multiModeEnabled;
	
	protected ViewGroup viewGroup;
	
	/**
	 * @return the objects
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Object> getObjects() {
		return (ArrayList<Object>) objects;
	}
	/**
	 * @param objects the objects to set
	 */
	public void setObjects(Object objects) {
		this.objects = objects;
	}
	private void initAttributes(
			Object objects, 
			int layoutResourceId,
			boolean multiModeEnabled) {
		
		this.objects = objects;		
		this.layoutResourceId = layoutResourceId;
		this.multiModeEnabled = multiModeEnabled;
	}
	
	private void initialize(Object[] objects, int layoutResourceId, boolean multiModeEnabled) {
		this.initAttributes(objects, layoutResourceId, multiModeEnabled);
	}
	
	private void initialize(List<Object> objects, int layoutResourceId, boolean multiModeEnabled) {
		this.initAttributes(objects, layoutResourceId, multiModeEnabled);		
	}

	private ProofBaseMultiSelectListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	private ProofBaseMultiSelectListAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public ProofBaseMultiSelectListAdapter(Context context,	Object[] objects, int layoutResourceId, boolean multiModeEnabled) {
		super(context, layoutResourceId, objects);
		this.initialize(objects, layoutResourceId, multiModeEnabled);
	}

	public ProofBaseMultiSelectListAdapter(Context context, List<Object> objects, int layoutResourceId, boolean multiModeEnabled) {
		super(context, layoutResourceId, objects);
		this.initialize(objects, layoutResourceId, multiModeEnabled);
	}

	public ProofBaseMultiSelectListAdapter(Context context, int resource,
			int textViewResourceId, Object[] objects, int layoutResourceId, boolean multiModeEnabled) {
		super(context, resource, textViewResourceId, objects);
		this.initialize(objects, layoutResourceId, multiModeEnabled);
	}

	public ProofBaseMultiSelectListAdapter(Context context, int resource,
			int textViewResourceId, List<Object> objects, int layoutResourceId, boolean multiModeEnabled) {
		super(context, resource, textViewResourceId, objects);
		this.initialize(objects, layoutResourceId, multiModeEnabled);
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		
		viewGroup = parent;
		
		LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);			
		view = vi.inflate(this.layoutResourceId, null);		
		
		if(this.objects != null) {
			this.handleView(position, view);
		}
		else {
			this.handleEmptyView(position, view);
		}
		
		return view;
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
