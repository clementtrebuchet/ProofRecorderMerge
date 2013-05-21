package org.proof.recorder.utils;

import org.proof.recorder.R;

import android.content.Context;
import android.widget.Toast;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.MenuItem;

public final class QuickActionMode implements ActionMode.Callback {
	
	private static Context mContext = null;

        /**
	 * @return the mContext
	 */
	public static Context getContext() {
		return mContext;
	}

	/**
	 * @param mContext the mContext to set
	 */
	public static void setContext(Context mContext) {
		QuickActionMode.mContext = mContext;
	}
	
	public QuickActionMode(Context context) {
		setContext(context);
	}

		@Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Toast.makeText(getContext(), "Got click: " + item, Toast.LENGTH_SHORT).show();
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        	
        }

		@Override
		public boolean onCreateActionMode(ActionMode mode,
				com.actionbarsherlock.view.Menu menu) {

            menu.add("Save")
                .setIcon(R.drawable.ic_compose_inverse)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            menu.add("Search")
                .setIcon(R.drawable.ic_search_inverse)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            menu.add("Refresh")
                .setIcon(R.drawable.ic_refresh_inverse)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            menu.add("Save")
                .setIcon(R.drawable.ic_compose_inverse)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            menu.add("Search")
                .setIcon(R.drawable.ic_search_inverse)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            menu.add("Refresh")
                .setIcon(R.drawable.ic_refresh_inverse)
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
