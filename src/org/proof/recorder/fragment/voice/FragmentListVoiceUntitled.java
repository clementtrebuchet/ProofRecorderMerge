package org.proof.recorder.fragment.voice;

import java.util.ArrayList;
import java.util.List;

import org.proof.recorder.R;

import org.proof.recorder.adapter.voice.ObjectsAdapter;
import org.proof.recorder.bases.fragment.ProofFragment;
import org.proof.recorder.bases.fragment.ProofListFragmentWithQuickAction;
import org.proof.recorder.database.collections.VoicesList;

import org.proof.recorder.database.models.Voice;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.MenuActions;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.Log.Console;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import android.widget.CheckBox;
import android.widget.ListView;

public class FragmentListVoiceUntitled extends ProofFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static class VoiceListLoader extends ProofListFragmentWithQuickAction {
		
		private void getVoices() {
			
			Uri uri;
			String mQuery = null;
			
			try {
				
				mQuery = (String) extraData.get("search");
				
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
				objects = (ArrayList<Object>) mList.getCollection();
				
			} catch (Exception e) {				
				Console.print_exception(e);
			}
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			Voice.setResolver(getActivity().getContentResolver());
			
			startAsyncLoader();
			
			fillCollectionRunnable = new Runnable() {
				@Override
				public void run() {
					getVoices();
				}
			};					
		}

		@Override
		public void onListItemClick(ListView l, View view, int position, long id) {

			super.onListItemClick(l, view, position, id);
			
			if (!isMulti) {
				Voice voice = (Voice) objects.get(position);
				
				QuickActionDlg.showUnTitledVoiceOptionsDlg(
						getActivity(),
						view, 
						voice, 
						listAdapter, 
						objects, 
						org.proof.recorder.Settings.mType.VOICE_UNTITLED
				);
			} else {
				CheckBox checkbox = (CheckBox) view.findViewById(R.id.cb_select_item);
				checkbox.toggle();
			}			
		}

		@Override
		protected void initOnOptionsItemSelected() {
			FragmentListVoiceTabs.removeUnusedTab();			
		}

		@Override
		protected void preDeleteAllAction() {
			
			int iter = 0;
			for (Object voice : objects) {
				recordIds[iter] = ((Voice) voice).getId();
				recordPaths[iter] = ((Voice) voice).getFilePath();
				
				iter++;

				Console.print_debug("Position: " + voice);
			}			
		}

		@Override
		protected void DoneAction() {
			FragmentListVoiceTabs.readdUnusedTab();			
		}

		@Override
		protected void DeleteAllAction() {
			MenuActions.deleteVoices(recordIds, recordPaths);
			FragmentListVoiceTabs.removeCurrentTab(getInternalContext());			
		}

		@Override
		protected void ShareAction() {
			MenuActions.sharingOptions(recordPaths);			
		}

		@Override
		protected void preDeleteAndShareAction() {
			int iter = 0;					
			
			for (Object item : objects) {
				Voice lcVoice = (Voice) item;
				
				if(lcVoice.isChecked()) {
					try {						
						recordIds[iter] = lcVoice.getId();
						recordPaths[iter] = lcVoice.getFilePath();
						
						iter++;
					}
					catch (Exception e) {
						Console.print_exception(e);
					}	
				}							
			}			
		}

		@Override
		protected void DeleteAction() {
			
			MenuActions.deleteVoices(recordIds, recordPaths);
			
			ArrayList<Object> toBeProcessed = new ArrayList<Object>();
			
			for(Object item : objects) {
				Voice lcVoice = (Voice) item;
				
				if(lcVoice.isChecked()) {
					toBeProcessed.add(lcVoice);						
				}				
			}
			
			for(Object item : toBeProcessed) {
				((ObjectsAdapter)listAdapter).remove((Voice) item);
				((ArrayList<Object>)objects).remove((Voice) item);
			}
			
			((ObjectsAdapter)listAdapter).notifyDataSetChanged();			
		}

		@Override
		protected boolean itemChecked(Object item) {
			return ((Voice) item).isChecked();
		}
		
		@Override
		protected void initAdapter(Context context, List<Object> collection,
				int layoutId, boolean multiSelectMode) {
			listAdapter = new ObjectsAdapter(context, collection, layoutId, multiSelectMode);
		}
		
		@Override
		protected void uncheckItem(Object item) {
			((Voice) item).setChecked(false);	
		}

		@Override
		protected void toggleItem(Object item, boolean checked) {
			((Voice) item).setChecked(checked);
		}
		
		@Override
		protected Object getItemClone(Object item) {
			return ((Voice) item).clone();
		}

		@Override
		protected void _onPreExecute() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void _onProgressUpdate(Integer... progress) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void _onPostExecute(Long result) {
			initAdapter(getActivity(), objects,	R.layout.listfragmentdroit, isMulti);
		}

		@Override
		protected Long _doInBackground(Void... params) {
			getVoices();
			return null;
		}

		@Override
		protected int collectionSorter(Object object1, Object object2) {
			return ((Voice) object1).getTimestamp().compareToIgnoreCase(
					((Voice) object2).getTimestamp());
		}
	}
}