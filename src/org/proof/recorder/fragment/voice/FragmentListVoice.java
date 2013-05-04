package org.proof.recorder.fragment.voice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.adapter.voice.VoiceAdapter;
import org.proof.recorder.database.collections.VoicesList;
import org.proof.recorder.database.models.Voice;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.Log.Console;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class FragmentListVoice extends SherlockFragment {
	
	public static String ID;
	private static boolean isNotify = false;
	private static String voiceId = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
	}

	public static class VoiceListLoader extends ListFragment {
		
		private static ArrayList<Voice> voices = null;
		private static VoiceAdapter voicesAdapter = null;
		private static Runnable viewVoices = null;

		boolean mDualPane;
		int mCursorPos = -1;
		
		private static Bundle extraDatas;
		
		private void getVoices() {
			
			Uri uri;
			String mQuery = null;
			
			if(isNotify) {
				uri = Uri.withAppendedPath(
						PersonnalProofContentProvider.CONTENT_URI, "voice_id/" + voiceId);
			}
			else {
				try {
					
					mQuery = (String) extraDatas.get("search");
					
					if(mQuery != null)
						throw new Exception();
					
					uri = Uri.withAppendedPath(
							PersonnalProofContentProvider.CONTENT_URI, "voices_by_title/" + mQuery);
				}
				catch(Exception e) {
					
					uri = Uri.withAppendedPath(
							PersonnalProofContentProvider.CONTENT_URI, "voices");
				}
			}			
			
			try {			
				
				Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
				
				VoicesList mList = new VoicesList(cursor);
				
				voices = mList.getCollection();
				
			} catch (Exception e) {				
				Console.print_exception(e);
				e.printStackTrace();
			}
		}	

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			Console.setTagName(this.getClass().getSimpleName());
			Voice.setResolver(getActivity().getContentResolver());
			
			isNotify = FragmentListVoiceTabs.isNotify();
			voiceId = FragmentListVoiceTabs.getVoiceId();
			
			extraDatas = getActivity().getIntent().getExtras();
			
			viewVoices = new Runnable() {
				@Override
				public void run() {
					getVoices();
				}
			};
			
			getActivity().runOnUiThread(viewVoices);			
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
			
			try{
				Collections.sort(voices, new Comparator<Voice>() {
			        @Override
			        public int compare(Voice s1, Voice s2) {
			            return s1.getTimestamp().compareToIgnoreCase(s2.getTimestamp());
			        }
			    });
			}
			catch(Exception e) {
				setEmptyText("");
			}
			
			voicesAdapter = new VoiceAdapter(getActivity(),
					R.layout.listfragmentdroit, voices);			

			setListAdapter(voicesAdapter);
			
			if(getListView().getCount() > 0) {
				registerForContextMenu(getListView());
			}			
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {

			super.onListItemClick(l, v, position, id);
			Voice voice = voices.get(position);
			
			QuickActionDlg.showTitledVoiceOptionsDlg(
					getActivity(),
					v, 
					voice, 
					voicesAdapter, 
					voices, 
					this, 
					Settings.mType.VOICE_TITLED
			);
		}
	}
}