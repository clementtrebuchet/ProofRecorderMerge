package org.proof.recorder.bases.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.proof.recorder.R;
import org.proof.recorder.utils.Log.Console;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListAdapter;

public abstract class ProofListFragmentWithAsyncLoader extends ProofListFragmentBase {
	
	private AsyncLoader collectionLoader;
	
	protected volatile boolean isLoading;
	
	
	protected void startAsyncLoader() {
		collectionLoader.execute();		
	}
	
	protected void reStartAsyncLoader() {
		stopAsyncLoader();
		initializeTask();
		startAsyncLoader();
	}
	
	protected void stopAsyncLoader() {
		if (collectionLoader != null) {
			if(collectionLoader.getStatus() != AsyncTask.Status.FINISHED &&
					!collectionLoader.isCancelled()) {
				collectionLoader.cancel(true);
				collectionLoader = null;
			}				
        }
	}
	
	private void initializeTask() {
		collectionLoader = new AsyncLoader();
	}
	
	protected volatile Object listAdapter = null;
	protected volatile ArrayList<Object> objects = null;
	
	protected abstract void _onPreExecute();
	protected abstract void _onProgressUpdate(Integer... progress);
	protected abstract void _onPostExecute(Long result);
	protected abstract Long _doInBackground(Void... params);
	
	protected abstract int collectionSorter(Object object1, Object object2);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		objects = new ArrayList<Object>();		
		extraData = getActivity().getIntent().getExtras();
		initializeTask();	
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopAsyncLoader();
	}
	
	protected class AsyncLoader extends AsyncTask<Void, Integer, Long> {
		
		private volatile boolean isEmpty = false;

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			_onProgressUpdate(progress);
		}
		
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        
	        isLoading = true;
	        
	        if(!multiSelectEnabled)
	        	lockScreenOrientation();
	        
	        //displayProgress();
	        
	        _onPreExecute();
	    }

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			
			_onPostExecute(result);
			setListAdapter((ListAdapter) listAdapter);
			
			if(!isEmpty) {								
				registerForContextMenu(getListView());
			}		    						
			else {
				setEmptyText(getActivity().getString(R.string.search_none_records_found));
			}				
			
			if(!multiSelectEnabled)
				unlockScreenOrientation();

			this.cancel(true);
			
			//hideProgress();
			
			isLoading = false;
		}

		@Override
		protected Long doInBackground(Void... params) {
			
			_doInBackground(params);      
            
            isEmpty = objects.size() == 0;
            
            if(!isEmpty) {
            	try {
    				Collections.sort(objects, new Comparator<Object>() {
    			        @Override
    			        public int compare(Object object1, Object object2) {
    			            return collectionSorter(object1, object2);
    			        }
    			    });			    				
    			}
    			catch(Exception e) {
    				Console.print_exception(e);
    			}		                
            }
            
			return null;
		}
	}	
}
