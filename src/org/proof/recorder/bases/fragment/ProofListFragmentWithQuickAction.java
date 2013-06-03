package org.proof.recorder.bases.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.proof.recorder.R;
import org.proof.recorder.bases.adapter.ProofBaseMultiSelectListAdapter;
import org.proof.recorder.utils.Log.Console;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public abstract class ProofListFragmentWithQuickAction extends ProofListFragmentWithAsyncLoader {	

	protected final static int SELECT_ALL = 5, 
			DELETE = 10, 
			SHARE = 15,
			DONE = 20;

	public static boolean isMulti = false;

	protected ActionMode mode = null;	

	protected String[] recordIds = null, recordPaths = null;	

	protected Runnable fillCollectionRunnable = null;
	
	protected MenuItem ItemClicked = null;

	private int selectedCount = 0;
	private boolean checked = false;	

	public final class QuickActionMode implements ActionMode.Callback {

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			
			int itemId = item.getItemId();
			
			if(itemId == SELECT_ALL)
				ItemClicked = item;
			
			return onItemClicked(itemId);
		}			

		@Override
		public void onDestroyActionMode(ActionMode mode)
		{

		}

		@Override
		public boolean onCreateActionMode(ActionMode mode,
				com.actionbarsherlock.view.Menu menu) {				            

			menu.add(0, DELETE, 0, 
					getInternalContext().getString(R.string.qaction_delete))
					.setIcon(R.drawable.icon_delete)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

			menu.add(0, SHARE, 0, 
					getInternalContext().getString(R.string.qaction_share))
					.setIcon(R.drawable.icon_share)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			
			menu.add(0, SELECT_ALL, 0, 
					getInternalContext().getString(R.string.qaction_select_all)
					).setIcon(R.drawable.icon_select_all)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

			return true;
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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		isMulti = false;		
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
					R.layout.listfragmentdroit, 
					isMulti);

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
		isMulti = false;
	}

	private boolean onItemClicked(int itemId) {		
		return handleActionMode(itemId);
	}

	protected abstract void preDeleteAndShareAction();
	protected abstract void DeleteAction();

	protected abstract void initOnOptionsItemSelected();

	protected abstract void preDeleteAllAction();	
	protected abstract void DoneAction();
	protected abstract void DeleteAllAction();	
	protected abstract void ShareAction();	

	protected abstract void uncheckItem(Object item);
	protected abstract void toggleItem(Object item, boolean checked);	
	protected abstract boolean itemChecked(Object item);

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

	protected void evaluateSelectedCount() {
		
		selectedCount = 0;
		for(Object item : objects) {
			if(itemChecked(item)) {
				selectedCount++;
			}
		}		
	}

	private boolean handleActionMode(int itemId) {	
		
		evaluateSelectedCount();

		boolean noSelected = selectedCount == 0;

		if(noSelected && itemId != SELECT_ALL && itemId != DONE) {
			return false;
		}
		else {			

			Console.print_debug("itemId: " + itemId);

			if (itemId == SELECT_ALL && !objects.isEmpty()) {

				selectedCount = objects.size();

				recordIds = new String[selectedCount];
				recordPaths = new String[selectedCount];

				this.preDeleteAllAction();

			} else if (itemId != SELECT_ALL && !noSelected) {				

				recordIds = new String[selectedCount];
				recordPaths = new String[selectedCount];

				this.preDeleteAndShareAction();

			} else {}
		}			

		switch (itemId) {

		case DONE:

			Console.print_debug("DONE: " + itemId);

			isMulti = false;
			selectedCount = 0;
			checked = false;
			
			mode.finish();
			
			reStartAsyncLoader();	
			
			this.DoneAction();

			break;

		case SELECT_ALL:			

			Console.print_debug("SELECT_ALL: " + itemId);

			toggleChecked();

			break;

		case DELETE:

			Console.print_debug("DELETE: " + itemId);

			if(objects.size() == selectedCount) {

				Console.print_debug("DELETE ALL: ");

				mode.finish();

				isMulti = false;

				getSherlockActivity().setRequestedOrientation(
						ActivityInfo.SCREEN_ORIENTATION_SENSOR);

				this.DeleteAllAction();

				return true;
			}

			this.DeleteAction();

			uncheckAll();

			break;

		case SHARE:

			this.ShareAction();

			uncheckAll();

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
		
		if(ItemClicked != null)
			ItemClicked.setIcon(R.drawable.icon_select_all);
	}

	private void toggleChecked() {

		if(!checked) {
			checked = true;
			selectedCount = objects.size();
			ItemClicked.setIcon(R.drawable.icon_all_selected);
		}			
		else {
			checked = false;
			selectedCount = 0;
			ItemClicked.setIcon(R.drawable.icon_select_all);
		}

		for(Object item : objects) {
			toggleItem(item, checked);
		}

		((ProofBaseMultiSelectListAdapter) listAdapter).toggleChecked(checked);
	}	

	protected void displayQuickActionMode() {

		int orientation = getResources().getConfiguration().orientation;

		if(orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			getSherlockActivity().setRequestedOrientation(
					ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}		
		else {
			getSherlockActivity().setRequestedOrientation(
					ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		mode = getSherlockActivity().startActionMode(
				new QuickActionMode());		

		int doneButtonId = Resources.getSystem().getIdentifier("action_mode_close_button", "id", "android");
		View doneButton = getSherlockActivity().findViewById(doneButtonId);
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
	@Override
	public void onPrepareOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com.actionbarsherlock.view.MenuItem)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		
		
		if (item.getItemId() == R.id.cm_records_list_del_file && !isLoading) {
			isMulti = true;
			displayQuickActionMode();
			
			initOnOptionsItemSelected();
			
			((ArrayAdapter<Object>) listAdapter).clear();
			initOnActivityCreated();
		}
		return true;
	}
}
