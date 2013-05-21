package org.proof.recorder.bases.fragment;

import java.util.ArrayList;
import java.util.List;

import org.proof.recorder.R;
import org.proof.recorder.bases.utils.SetStaticContext;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public abstract class ProofListFragmentWithQuickAction extends SherlockListFragment {
	
	public static boolean isMulti = false;
	
	protected final static int DELETE_ALL = R.string.qaction_delete_all, 
			DELETE = R.string.qaction_delete, 
			SHARE = R.string.qaction_share,
			DONE = R.string.qaction_done;

	protected static List<Integer> selectedItems;
	protected static ActionMode mode = null;
	
	private Context internalContext = null;	

	public final class QuickActionMode implements ActionMode.Callback {

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {            
			return onItemClicked(item.getItemId());
		}			

		@Override
		public void onDestroyActionMode(ActionMode mode)
		{

		}

		@Override
		public boolean onCreateActionMode(ActionMode mode,
				com.actionbarsherlock.view.Menu menu) {

			menu.add(DELETE_ALL)
			.setIcon(R.drawable.ic_compose_inverse)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);	            

			menu.add(DELETE)
			.setIcon(R.drawable.ic_refresh_inverse)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

			menu.add(SHARE)
			.setIcon(R.drawable.ic_search_inverse)
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
	public void onResume() {
		super.onResume();
		initialize();
	}

	protected void initialize() {
		SetStaticContext.setConsoleTagName(this.getClass().getSimpleName());
		SetStaticContext.setStaticsContext(getActivity(), 1);
		setInternalContext(getActivity());

		selectedItems = new ArrayList<Integer>();
		isMulti = false;
	}
	
	private boolean onItemClicked(int itemId) {
		isMulti = false;
		boolean result = handleActionMode(itemId);
		initOnActivityCreated();
		mode.finish();
		
		getSherlockActivity().setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		
		return result;
	}

	protected abstract boolean handleActionMode(int itemId);
	protected abstract void initOnActivityCreated();

	/**
	 * @return the internalContext
	 */
	protected Context getInternalContext() {
		return internalContext;
	}

	/**
	 * @param internalContext the internalContext to set
	 */
	private void setInternalContext(Context internalContext) {
		this.internalContext = internalContext;
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
				onItemClicked(DONE);
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
	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		isMulti = true;
		displayQuickActionMode();
		return true;
	}
}
