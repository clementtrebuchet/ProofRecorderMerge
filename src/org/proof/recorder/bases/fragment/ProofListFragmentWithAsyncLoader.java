package org.proof.recorder.bases.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListAdapter;

import org.proof.recorder.R;
import org.proof.recorder.utils.Log.Console;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public abstract class ProofListFragmentWithAsyncLoader extends ProofListFragmentBase {
	
	private AsyncLoader collectionLoader;

	volatile boolean isLoading;
	
	protected void startAsyncLoader() {
		collectionLoader.execute();		
	}

	void reStartAsyncLoader() {
		stopAsyncLoader();
		initializeTask();
		startAsyncLoader();
	}

	private void stopAsyncLoader() {
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
	
	@SuppressWarnings("EmptyMethod")
	protected abstract void _onPreExecute();
	@SuppressWarnings("EmptyMethod")
	protected abstract void _onProgressUpdate(Integer... progress);
	protected abstract void _onPostExecute(Long result);

	protected abstract void _doInBackground(Void... params);
	
	protected abstract int collectionSorter(Object object1, Object object2);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		objects = new ArrayList<Object>();		
		extraData = getActivity().getIntent().getExtras();
		initializeTask();	
	}
	
	@SuppressWarnings("EmptyMethod")
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@SuppressWarnings("EmptyMethod")
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopAsyncLoader();
	}

	class AsyncLoader extends AsyncTask<Void, Integer, Long> {
		
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
			
			if(!multiSelectEnabled) {
				unlockScreenOrientation();				
			}
			
			try {
				setListShown(true);
			} catch (Exception ignored) {
			}

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
            		
            		Comparator<Object> comp = new Comparator<Object>() {
    			        @Override
    			        public int compare(Object object1, Object object2) {
    			            return collectionSorter(object1, object2);
    			        }
    			    };
    			    
    			    if(reverseCollection)    				
    			    	comp = Collections.reverseOrder(comp);
    			    
    			    Collections.sort(objects, comp);
    			}
    			catch(Exception e) {
    				Console.print_exception(e);
    			}		                
            }
            
			return null;
		}
	}	
}
