package org.proof.recorder.bases.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.actionbarsherlock.widget.ShareActionProvider.OnShareTargetSelectedListener;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.bases.adapter.ProofBaseListAdapter;
import org.proof.recorder.bases.adapter.ProofBaseMultiSelectListAdapter;
import org.proof.recorder.services.SoundCloudBgUploader;
import org.proof.recorder.utils.Log.Console;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class ProofListFragmentWithQuickAction extends ProofListFragmentWithAsyncLoader {

	private final static int SELECT_ALL = 5;
	private final static int DELETE = 10;
	private final static int SHARE = 15;
	private final static int SHARE_SC = 20;
	private final static int DONE = 25;

	private ActionMode mode = null;
	
	protected String[] recordIds = null, 
					   recordPaths = null;

	protected Runnable fillCollectionRunnable = null;

	private MenuItem selectItem = null;
	private MenuItem deleteItem = null;
	private MenuItem shareSoundCItem = null;
	private MenuItem shareItem = null;

	private ShareActionProvider actionProvider = null;

	private int selectedCount = 0;
	private boolean checked = false;

	private QuickActionMode quickActionMode;	

	public final class QuickActionMode implements ActionMode.Callback {

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			int itemId = item.getItemId();
			return onItemClicked(itemId);
		}			

		@Override
		public void onDestroyActionMode(ActionMode mode)
		{

		}		

		@Override
		public boolean onCreateActionMode(ActionMode mode,
				com.actionbarsherlock.view.Menu menu) {	

			getSherlockActivity().getSupportMenuInflater().inflate(R.menu.share_action_provider, menu);

			// Set file with share history to the provider and set the share intent.
			shareItem = menu.findItem(R.id.menu_item_share_action_provider_action_bar);
			
			shareSoundCItem = menu.add(0, SHARE_SC, 0, 
					getInternalContext().getString(R.string.qaction_share_sc));
			
			deleteItem = menu.add(0, DELETE, 0, 
					getInternalContext().getString(R.string.qaction_delete));			

			selectItem = menu.add(0, SELECT_ALL, 0, 
					getInternalContext().getString(R.string.qaction_select_all));
			
			selectItem.setIcon(R.drawable.icon_select_all)
					  .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			
			deleteItem.setIcon(R.drawable.icon_delete_disabled)					  
			          .setEnabled(false)
			          .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			
			shareSoundCItem.setIcon(R.drawable.icon_soundcloud)
						   .setEnabled(false)
						   .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			
			shareItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			
			actionProvider = (ShareActionProvider) shareItem.getActionProvider();
			actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
			// Note that you can set/change the intent any time,
			// say when the user has selected an image.				
			enableItems(false);
			
			actionProvider.setOnShareTargetSelectedListener(new OnShareTargetSelectedListener() {
				
				@Override
				public boolean onShareTargetSelected(ShareActionProvider source,
						Intent intent) {
					uncheckAll();
					getSherlockActivity().startActivity(intent);					
					return true;
				}
			});		

			return true;
		}
		
		public void enableItems(boolean enable) {
			
			if(!enable) {
				boolean fallback = false;
				if(actionProvider != null) {				
					try {
						// Workaround on apparently android known bug
						// to set ShareActionProvider in disable mode state.
						// Note that might raise Exception on some devices!
						actionProvider.setShareIntent(null);
						
						Console.print_debug("Share Intent set to null!");
						
					}catch (Exception e) {
						fallback = true;
					}
				}
				if(fallback && shareItem != null) {
					// Fall-back for not compatible workaround devices
					// Playing with visibility :)
					//noinspection ConstantConditions
					shareItem.setVisible(enable);
				}
			}
			else {
				if(!shareItem.isVisible())
					//noinspection ConstantConditions
					shareItem.setVisible(enable);
			}
			
			if(deleteItem != null) {
				deleteItem.setEnabled(enable);

				deleteItem.setIcon(
						enable ? R.drawable.icon_delete : R.drawable.icon_delete_disabled);
			}
			
			if(shareSoundCItem != null) {
				shareSoundCItem.setEnabled(enable);

				shareSoundCItem.setIcon(
						enable ? R.drawable.icon_soundcloud : R.drawable.icon_soundcloud_disabled);
			}
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode,
				com.actionbarsherlock.view.Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		initialize();
	}

	@SuppressWarnings("EmptyMethod")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		multiSelectEnabled = false;		
		viewGroup = null;
		extraData = null;
		listAdapter = null;
	}

	protected void initOnActivityCreated() {

		getActivity().runOnUiThread(fillCollectionRunnable);

		try {
			Collections.sort(objects, new Comparator<Object>() {
				@Override
				public int compare(Object first, Object second) {
					return collectionSorter(first, second);
				}
			});

			initAdapter(
					getActivity(), 
					objects, 
					((ProofBaseListAdapter) listAdapter).getLayoutResourceId(),
					multiSelectEnabled);

			setListAdapter((ListAdapter) listAdapter);
		} catch (Exception e) {
			setEmptyText(getString(R.string.none_records_dlg_msg));
		}

		if(objects.size() == 0)
			setEmptyText(getString(R.string.none_records_dlg_msg));

		if (getListView().getCount() > 0) {
			registerForContextMenu(getListView());
		}
	}

	private void initialize() {
		setHasOptionsMenu(true);
		multiSelectEnabled = false;
	}

	private boolean onItemClicked(int itemId) {		
		return handleActionMode(itemId);
	}

	protected abstract void preDeleteAndShareAction();
	protected abstract void DeleteAction();

	protected abstract void initOnOptionsItemSelected();

	protected abstract void DoneAction();
	protected abstract void DeleteAllAction();	
	
	protected abstract void uncheckItem(Object item);
	protected abstract void toggleItem(Object item, boolean checked);	
	protected abstract boolean itemChecked(Object item);

	protected Intent ShareAction() {

		Intent share = null;

		if(recordPaths.length > 0) {

			share = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
			share.setType("audio/*");

			share.putExtra(Intent.EXTRA_SUBJECT,
					getString(R.string.custom_intent_chooser_subject));
			share.putExtra(Intent.EXTRA_TEXT,
					getString(R.string.custom_intent_chooser_text));

			ArrayList<Uri> uris = new ArrayList<Uri>();
			for(String attachmentPath : recordPaths) {
				Uri attachment = Uri.fromFile(new File(attachmentPath));
				uris.add(attachment);						
			}

			share.putParcelableArrayListExtra(Intent.EXTRA_STREAM,	uris);
		}		 

		return share;
	}

	protected List<Object> cloneCollection(List<Object> list) {
		List<Object> clone = new ArrayList<Object>(list.size());
		for(Object item: list) clone.add(getItemClone(item));
		return clone;
	}

	protected abstract Object getItemClone(Object item);

	protected abstract void initAdapter(
			Context context, 
			List<Object> collection, 
			int layoutId,
			boolean multiSelectMode);

	private void evaluateSelectedCount() {

		selectedCount = 0;
		for(Object item : objects) {
			if(itemChecked(item)) {
				selectedCount++;
			}
		}		
	}

	private boolean allSelected() {
		return objects.size() == selectedCount;
	}

	private boolean emptySelection() {
		return selectedCount == 0;
	}

	@Override
	protected void alertDlgOkAction(DialogInterface dialog, int which) {

		if(allSelected()) {

			Console.print_debug("DELETE ALL: ");

			mode.finish();

			multiSelectEnabled = false;

			unlockScreenOrientation();

			this.DeleteAllAction();
		}
		else {
			this.DeleteAction();
		}	
	}
	
	@Override
	protected void handleOnReceive(Context context, Intent intent) {
		Console.print_debug("Received notify");
		handleActionMode(SHARE);
	}

	private boolean handleActionMode(int itemId) {

		Console.print_debug("itemId: " + itemId);

		Intent share;

		switch (itemId) {

		case DONE:

			Console.print_debug("DONE: " + itemId);

			multiSelectEnabled = false;
			selectedCount = 0;
			checked = false;

			setListShown(false);

			reStartAsyncLoader();	
			//initOnActivityCreated();
			
			mode.finish();
			this.DoneAction();

			break;

		case SELECT_ALL:			

			Console.print_debug("SELECT_ALL: " + itemId);			

			toggleChecked();

			break;

		case DELETE:

			Console.print_debug("DELETE: " + itemId);

			evaluateSelectedCount();

			if(emptySelection()) {
				return false;
			}

			recordIds = new String[selectedCount];
			recordPaths = new String[selectedCount];

			this.preDeleteAndShareAction();

			if(!emptySelection()) {
				if(Settings.isUACAssisted())
					displayAlert();
				else
					alertDlgOkAction(null, 0);			
			}				

			break;
		
		case SHARE:
		case SHARE_SC:

			evaluateSelectedCount();

			recordIds = new String[selectedCount];
			recordPaths = new String[selectedCount];

			this.preDeleteAndShareAction();
			
			if(itemId == SHARE_SC) {			

				if(selectedCount > 0) {
					
					Intent service = new Intent(getActivity(), SoundCloudBgUploader.class);
					
					service.putExtra("selectedCount", selectedCount);
					service.putExtra("recordPaths", recordPaths);
					
					getActivity().startService(service);
				}
			}
			else {
				share = this.ShareAction();
	
				if(share != null) {				
					if(quickActionMode != null)
						quickActionMode.enableItems(true);
					
					actionProvider.setShareIntent(share);
					
					Console.print_debug("Share Intent set!");
				}
				else {
					if(quickActionMode != null)
						quickActionMode.enableItems(false);
					
					Console.print_debug("Share Intent null!");
				}
			}

			break;

		default:
			Console.print_debug("DEFAULT: " + itemId);

			return false;
		}
		return true;
	}

	private void uncheckAll() {		

		for(Object item : objects) {
			uncheckItem(item);
		}

		((ProofBaseMultiSelectListAdapter) listAdapter).uncheckAll();

		selectedCount = 0;
		checked = false;

		selectItem.setIcon(R.drawable.icon_select_all);
	}

	private void toggleChecked() {

		if(!checked) {
			checked = true;
			selectedCount = objects.size();
			selectItem.setIcon(R.drawable.icon_all_selected);
		}			
		else {
			checked = false;
			selectedCount = 0;
			selectItem.setIcon(R.drawable.icon_select_all);
		}

		for(Object item : objects) {
			toggleItem(item, checked);
		}

		((ProofBaseMultiSelectListAdapter) listAdapter).toggleChecked(checked);
	}

	private void displayQuickActionMode() {

		lockScreenOrientation();
		
		quickActionMode = new QuickActionMode();
		mode = getSherlockActivity().startActionMode(quickActionMode);		

		int doneButtonId = Resources.getSystem().getIdentifier("action_mode_close_button", "id", "android");
		View doneButton = getSherlockActivity().findViewById(doneButtonId);

		if(doneButton == null | doneButtonId == 0) {
			doneButtonId = R.id.abs__action_mode_close_button;
			doneButton = getSherlockActivity().findViewById(doneButtonId);
		}

		doneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleActionMode(DONE);
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com.actionbarsherlock.view.Menu, 
	 * com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu,
			MenuInflater inflater) {

		menu.add(Menu.NONE, R.id.cm_records_list_del_file, Menu.NONE,
				getString(R.string.multi_select_mode));

		super.onCreateOptionsMenu(menu, inflater);
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onPrepareOptionsMenu(com.actionbarsherlock.view.Menu)
	 */
	@SuppressWarnings("EmptyMethod")
	@Override
	public void onPrepareOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com.actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {


		if (item.getItemId() == R.id.cm_records_list_del_file && !isLoading) {

			multiSelectEnabled = true;	
			
			setListShown(false);

			reStartAsyncLoader();
			//initOnActivityCreated();			

			displayQuickActionMode();
			
			initOnOptionsItemSelected();
		}
		return true;
	}
}
