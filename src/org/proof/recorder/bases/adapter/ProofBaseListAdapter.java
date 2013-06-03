package org.proof.recorder.bases.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public abstract class ProofBaseListAdapter extends ArrayAdapter<Object> {
	
	protected abstract void handleView(final int item, View view);
	protected abstract void handleEmptyView(final int item, View view);
	
	protected Object objects;		
	protected int layoutResourceId;
	
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
			int layoutResourceId) {
		
		this.objects = objects;		
		this.layoutResourceId = layoutResourceId;
	}
	
	private void initialize(Object[] objects, int layoutResourceId) {
		this.initAttributes(objects, layoutResourceId);
	}
	
	private void initialize(List<Object> objects, int layoutResourceId) {
		this.initAttributes(objects, layoutResourceId);		
	}

	private ProofBaseListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	private ProofBaseListAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public ProofBaseListAdapter(Context context,	Object[] objects, int layoutResourceId) {
		super(context, layoutResourceId, objects);
		this.initialize(objects, layoutResourceId);
	}

	public ProofBaseListAdapter(Context context, List<Object> objects, int layoutResourceId) {
		super(context, layoutResourceId, objects);
		this.initialize(objects, layoutResourceId);
	}

	public ProofBaseListAdapter(Context context, int resource,
			int textViewResourceId, Object[] objects, int layoutResourceId) {
		super(context, resource, textViewResourceId, objects);
		this.initialize(objects, layoutResourceId);
	}

	public ProofBaseListAdapter(Context context, int resource,
			int textViewResourceId, List<Object> objects, int layoutResourceId) {
		super(context, resource, textViewResourceId, objects);
		this.initialize(objects, layoutResourceId);
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
}
