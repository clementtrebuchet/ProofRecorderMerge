package org.proof.recorder.fragment.voice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.proof.recorder.R;
import org.proof.recorder.database.models.Voice;
import org.proof.recorder.utils.Log.Console;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class FragmentListVoice extends SherlockFragment {
	
	public static String ID;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static class VoiceListLoader extends ListFragment {
		
		private static ArrayList<Voice> voices = null;
		private static VoiceAdapter voicesAdapter = null;
		private static Runnable viewVoices = null;
		private static boolean uiOn = false;
		boolean mDualPane;
		int mCursorPos = -1;
		
		private static Bundle extraDatas;
		
		private Runnable returnRes = new Runnable() {

			@Override
			public void run() {
				
				((VoiceAdapter) getListAdapter()).notifyDataSetChanged();
			}
		};
		
		private void getVoices() {
			try {
				voices = null;
				if(uiOn)
					getActivity().runOnUiThread(returnRes);
			} catch (Exception e) {				
				Console.print_exception(e);
			}
		}

		public class VoiceAdapter extends ArrayAdapter<Voice> {

			private ArrayList<Voice> items;

			public VoiceAdapter(Context context, int textViewResourceId,
					ArrayList<Voice> items) {
				super(context, textViewResourceId, items);
				this.items = items;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = convertView;
				if (view == null) {
					LayoutInflater vi = (LayoutInflater) getActivity()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = vi.inflate(R.layout.listfragmentdroit, null);
				}
				Voice voice = items.get(position);
				if (voice != null) {					
								
				}
				return view;
			}
		}		

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			extraDatas = getActivity().getIntent().getExtras();
			
			viewVoices = new Runnable() {
				@Override
				public void run() {
					uiOn = true;
					getVoices();
					uiOn = false;
				}
			};
			
			getActivity().runOnUiThread(viewVoices);			
			
			voicesAdapter = new VoiceAdapter(getActivity(),
					R.layout.listfragmentdroit, voices);
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
			
			if(!voices.isEmpty())
				Collections.sort(voices, new Comparator<Voice>() {
			        @Override
			        public int compare(Voice s1, Voice s2) {
			            return s1.getId().compareToIgnoreCase(s2.getId());
			        }
			    });
			setListAdapter(voicesAdapter);
			if(getListView().getCount() > 0)
				registerForContextMenu(getListView());
		}

/*		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			Uri uri;
			String mQuery;
			CursorLoader cursorLoader;
			
			try {
				
				mQuery = (String) extraDatas.get("search");
				
				if(mQuery == null)
					throw new Exception();
				
				uri = Uri.withAppendedPath(
						PersonnalProofContentProvider.CONTENT_URI, "voices_by_title/" + mQuery);
				cursorLoader = new CursorLoader(getActivity(), uri,
						from, null, null, null);
			}
			catch(Exception e) {
				
				uri = Uri.withAppendedPath(
						PersonnalProofContentProvider.CONTENT_URI, "voices");
				cursorLoader = new CursorLoader(getActivity(), uri,
						from, null, null, null);
			}			
			
			return cursorLoader;
		}*/

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {

			super.onListItemClick(l, v, position, id);
			/*Cursor c = ((VoiceListAdapter) getListAdapter()).getCursor();
			
			QuickActionDlg.showTitledVoiceOptionsDlg(
					getActivity(),
					v, 
					c, 
					getListAdapter(), 
					getLoaderManager(), 
					this, 
					Settings.mType.VOICE_TITLED
			);*/
		}

	}

}