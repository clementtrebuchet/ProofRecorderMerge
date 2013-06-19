package org.proof.recorder.bases.activity;

import android.os.AsyncTask;
import android.os.Bundle;

public abstract class ProofAsyncPreferenceActivity extends ProofPreferenceActivity {
	
	private AsyncLoader collectionLoader;
	
	protected volatile boolean isLoading;	
	
	protected void startAsyncLoader() {
		this.collectionLoader.execute();		
	}
	
	protected void reStartAsyncLoader() {
		this.stopAsyncLoader();
		this.initializeTask();
		this.startAsyncLoader();
	}
	
	protected void stopAsyncLoader() {
		if (this.collectionLoader != null) {
			if(this.collectionLoader.getStatus() != AsyncTask.Status.FINISHED &&
					!this.collectionLoader.isCancelled()) {
				this.collectionLoader.cancel(true);
				this.collectionLoader = null;
			}				
        }
	}
	
	private void initializeTask() {
		this.collectionLoader = new AsyncLoader();
	}
	
	protected abstract void _onPreExecute();
	protected abstract void _onProgressUpdate(Integer... progress);
	protected abstract void _onPostExecute(Long result);
	protected abstract Long _doInBackground(Void... params);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.initializeTask();	
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

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			_onProgressUpdate(progress);
		}
		
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        
	        isLoading = true;
	        
	        lockScreenOrientation();	        
	        displayProgress();
	        
	        _onPreExecute();
	    }

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			
			_onPostExecute(result);
			
			unlockScreenOrientation();
			hideProgress();
			
			this.cancel(true);			
			isLoading = false;				
		}

		@Override
		protected Long doInBackground(Void... params) {			
			_doInBackground(params);            
			return null;
		}
	}

}
