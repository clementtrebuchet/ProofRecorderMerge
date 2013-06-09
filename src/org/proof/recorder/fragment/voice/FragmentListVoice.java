package org.proof.recorder.fragment.voice;

import java.util.ArrayList;
import java.util.List;

import org.proof.recorder.R;
import org.proof.recorder.adapters.VoiceAdapter;
import org.proof.recorder.bases.activity.ProofMultiSelectFragmentActivity;
import org.proof.recorder.bases.fragment.ProofFragment;
import org.proof.recorder.bases.fragment.ProofListFragmentWithQuickAction;

import org.proof.recorder.database.collections.VoicesList;

import org.proof.recorder.database.models.Voice;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.utils.MenuActions;
import org.proof.recorder.utils.QuickActionDlg;
import org.proof.recorder.utils.Log.Console;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import android.widget.ListView;

public class FragmentListVoice extends ProofFragment {
	
	public static String ID;
	private static boolean isNotify = false;
	private static String voiceId = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
	}

	public static class VoiceListLoader extends ProofListFragmentWithQuickAction {
		
		private void getVoices() {
			
			Uri uri;
			String mQuery = null;
			
			if(isNotify) {
				uri = Uri.withAppendedPath(
						PersonnalProofContentProvider.CONTENT_URI, "voice_id/" + voiceId);
			}
			else {
				try {
					
					mQuery = (String) extraData.get("search");
					
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
				
				objects = (ArrayList<Object>) mList.getCollection();
				
			} catch (Exception e) {				
				Console.print_exception(e);
				e.printStackTrace();
			}
		}	

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			Voice.setResolver(getActivity().getContentResolver());
			
			isNotify = ProofMultiSelectFragmentActivity.isNotify();
			voiceId = FragmentListVoiceTabs.getSavedId();
			
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
			
			if (!multiSelectEnabled) {
				Voice voice = (Voice) objects.get(position);
				
				QuickActionDlg.showTitledVoiceOptionsDlg(
						getActivity(),
						view, 
						voice, 
						listAdapter, 
						objects, 
						org.proof.recorder.Settings.mType.VOICE_TITLED
				);
			} else {
				CheckBox checkbox = (CheckBox) view.findViewById(R.id.cb_select_item);
				checkbox.toggle();
			}
		}

		@Override
		protected void initOnOptionsItemSelected() {
			ProofMultiSelectFragmentActivity.removeUnusedTab();			
		}

		@Override
		protected void DoneAction() {
			ProofMultiSelectFragmentActivity.readdUnusedTab();			
		}

		@Override
		protected void DeleteAllAction() {
			MenuActions.deleteVoices(recordIds, recordPaths);
			ProofMultiSelectFragmentActivity.removeCurrentTab(getInternalContext());			
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
				((VoiceAdapter)listAdapter).remove(item);
				objects.remove(item);
			}
			
			((VoiceAdapter)listAdapter).notifyDataSetChanged();			
		}

		@Override
		protected boolean itemChecked(Object item) {
			return ((Voice) item).isChecked();
		}
		
		@Override
		protected void initAdapter(Context context, List<Object> collection,
				int layoutId, boolean multiSelectMode) {
			listAdapter = new VoiceAdapter(context, collection, layoutId, multiSelectMode, getBroadcastName());
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
			initAdapter(getActivity(), objects, R.layout.listfragmentdroit, multiSelectEnabled);
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

		@Override
		protected void alertDlgCancelAction(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			
		}
	}
}