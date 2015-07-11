package org.proof.recorder.bases.activity;

import android.os.AsyncTask;
import android.os.Bundle;

@SuppressWarnings("unused")
public abstract class ProofAsyncPreferenceActivity extends ProofPreferenceActivity {
	
	private AsyncLoader collectionLoader;

	private volatile boolean isLoading;

	private void startAsyncLoader() {
		this.collectionLoader.execute();		
	}
	
	protected void reStartAsyncLoader() {
		this.stopAsyncLoader();
		this.initializeTask();
		this.startAsyncLoader();
	}

	private void stopAsyncLoader() {
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
	
	@SuppressWarnings("EmptyMethod")
	protected abstract void _onPreExecute();
	@SuppressWarnings("EmptyMethod")
	protected abstract void _onProgressUpdate(Integer... progress);
	@SuppressWarnings("EmptyMethod")
	protected abstract void _onPostExecute(Long result);

	protected abstract void _doInBackground(Void... params);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.initializeTask();	
	}
	
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

	@SuppressWarnings("unused")
	private class AsyncLoader extends AsyncTask<Void, Integer, Long> {

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
