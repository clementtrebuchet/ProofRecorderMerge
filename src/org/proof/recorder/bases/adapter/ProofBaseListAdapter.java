package org.proof.recorder.bases.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.proof.recorder.utils.Log.Console;

import java.util.ArrayList;
import java.util.List;

public abstract class ProofBaseListAdapter extends ArrayAdapter<Object> {
	
	protected abstract void handleView(final int item, View view);
	protected abstract void handleEmptyView(final int item, View view);
	@SuppressWarnings("EmptyMethod")
	protected abstract void handleEventIntent(Intent intent);
	
	protected void sendEvent() {
		
		Console.print_debug("Event sent to: " + broadcastName);
		
		if(broadcastName != null) {
			Intent intent = new Intent(broadcastName);
			handleEventIntent(intent);
			LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);	
		}		  
	}

	private Object objects;
	private int layoutResourceId;
	private String broadcastName;
	
	public final int getLayoutResourceId() {
		return layoutResourceId;
	}

	ViewGroup viewGroup;
	
	/**
	 * @return the objects
	 */
	@SuppressWarnings("unchecked")
	protected ArrayList<Object> getObjects() {
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
			String broadcastName) {
		
		this.objects = objects;		
		this.layoutResourceId = layoutResourceId;
		this.broadcastName = broadcastName;
	}
	
	private void initialize(Object[] objects, int layoutResourceId, String broadcastName) {
		this.initAttributes(objects, layoutResourceId, broadcastName);
	}
	
	private void initialize(List<Object> objects, int layoutResourceId, String broadcastName) {
		this.initAttributes(objects, layoutResourceId, broadcastName);		
	}

	private ProofBaseListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	private ProofBaseListAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	ProofBaseListAdapter(Context context, Object[] objects, int layoutResourceId, String broadcastName) {
		super(context, layoutResourceId, objects);
		this.initialize(objects, layoutResourceId, broadcastName);
	}

	ProofBaseListAdapter(Context context, List<Object> objects, int layoutResourceId, String broadcastName) {
		super(context, layoutResourceId, objects);
		this.initialize(objects, layoutResourceId, broadcastName);
	}

	ProofBaseListAdapter(Context context, int resource,
						 int textViewResourceId, Object[] objects, int layoutResourceId, String broadcastName) {
		super(context, resource, textViewResourceId, objects);
		this.initialize(objects, layoutResourceId, broadcastName);
	}

	ProofBaseListAdapter(Context context, int resource,
						 int textViewResourceId, List<Object> objects, int layoutResourceId, String broadcastName) {
		super(context, resource, textViewResourceId, objects);
		this.initialize(objects, layoutResourceId, broadcastName);
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		
		viewGroup = parent;
		
		LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);			
		view = vi.inflate(this.layoutResourceId, viewGroup);
		
		if(this.objects != null) {
			this.handleView(position, view);
		}
		else {
			this.handleEmptyView(position, view);
		}
		
		return view;
	}
}
