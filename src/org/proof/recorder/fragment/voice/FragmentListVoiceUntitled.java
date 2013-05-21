package org.proof.recorder.fragment.voice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.adapter.voice.VoiceAdapter;
import org.proof.recorder.bases.fragment.ProofFragment;
import org.proof.recorder.bases.fragment.ProofListFragmentWithQuickAction;
import org.proof.recorder.database.collections.VoicesList;
import org.proof.recorder.database.models.Voice;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.Log.Console;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FragmentListVoiceUntitled extends ProofFragment {

	//private static final String TAG = "FragmentListVoice";
	
	public static String ID;
	/** Called when the activity is first created. */	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static class VoiceListLoader extends ProofListFragmentWithQuickAction {

		private static ArrayList<Voice> voices = null;
		private static VoiceAdapter voicesAdapter = null;
		private static Runnable viewVoices = null;
		
		boolean mDualPane;
		int mCursorPos = -1;
		
		private static Bundle extraDatas;
		
		private void getVoices() {
			
			Uri uri;
			String mQuery = null;
			
			try {
				
				mQuery = (String) extraDatas.get("search");
				
				if(mQuery != null)
					throw new Exception();
				
				uri = Uri.withAppendedPath(
						PersonnalProofContentProvider.CONTENT_URI, "voices_by_title/" + mQuery);
			}
			catch(Exception e) {
				
				uri = Uri.withAppendedPath(
						PersonnalProofContentProvider.CONTENT_URI, "voices_by_untitled");
			}
			
			try {			
				
				Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
				
				VoicesList mList = new VoicesList(cursor);
				
				voices = mList.getCollection();
				
			} catch (Exception e) {				
				Console.print_exception(e);
			}
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			Console.setTagName(this.getClass().getSimpleName());
			Voice.setResolver(getActivity().getContentResolver());
			
			extraDatas = getActivity().getIntent().getExtras();
			
			viewVoices = new Runnable() {
				@Override
				public void run() {
					getVoices();
				}
			};					
		}

		/**
		 * Contextual Menu for displaying social and all :)
		 */

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {			
			return super.onCreateView(inflater, container, savedInstanceState);
		}		

		/**
		 * End of Contextual Menu
		 */

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			getActivity().runOnUiThread(viewVoices);
			
			try{
				Collections.sort(voices, new Comparator<Voice>() {
			        @Override
			        public int compare(Voice s1, Voice s2) {
			            return s1.getTimestamp().compareToIgnoreCase(s2.getTimestamp());
			        }
			    });
				
				voicesAdapter = new VoiceAdapter(getActivity(),
						R.layout.listfragmentdroit, voices);
				
				setListAdapter(voicesAdapter);
			}
			catch(Exception e) {
				setEmptyText("");
			}
			
			if(getListView().getCount() > 0) {
				registerForContextMenu(getListView());
			}			
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {

			super.onListItemClick(l, v, position, id);
			
			Voice voice = voices.get(position);
			
			QuickActionDlg.showUnTitledVoiceOptionsDlg(
					getActivity(),
					v, 
					voice, 
					voicesAdapter, 
					voices, 
					this, 
					Settings.mType.VOICE_UNTITLED
			);
		}

		@Override
		protected boolean handleActionMode(int itemId) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		protected void initOnActivityCreated() {
			// TODO Auto-generated method stub
			
		}

	}

}