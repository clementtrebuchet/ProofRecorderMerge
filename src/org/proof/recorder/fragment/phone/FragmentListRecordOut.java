package org.proof.recorder.fragment.phone;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import org.proof.recorder.R;
import org.proof.recorder.adapters.RecordAdapter;
import org.proof.recorder.bases.activity.ProofMultiSelectFragmentActivity;
import org.proof.recorder.bases.fragment.ProofFragment;
import org.proof.recorder.bases.fragment.ProofListFragmentWithQuickAction;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.fragment.contacts.utils.ContactsDataHelper;
import org.proof.recorder.utils.MenuActions;
import org.proof.recorder.utils.QuickActionDlg;

import java.util.ArrayList;
import java.util.List;

public class FragmentListRecordOut extends ProofFragment {

    /**
     * Called when the activity is first created.
     */
    @SuppressWarnings("EmptyMethod")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static class OutGoingCallsLoader extends ProofListFragmentWithQuickAction {
        @SuppressWarnings("unchecked")
        @Override
        public void onListItemClick(ListView l, final View view, int position,
                                    long id) {

            super.onListItemClick(l, view, position, id);

            if (!multiSelectEnabled) {
                Record mRecord = (Record) objects.get(position);
                QuickActionDlg.showPhoneOptionsDlg(
                        getActivity(),
                        view,
                        (ArrayAdapter<Object>)
                                listAdapter,
                        mRecord);
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
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            MenuActions.setInternalContext(getActivity());

            reverseCollection = true;

            fillCollectionRunnable = new Runnable() {
                @Override
                public void run() {
                    getContacts();
                }
            };

            startAsyncLoader();
        }

        private void getContacts() {
            try {
                String mIdOrTelephone = extraData.getString("mIdOrTelephone");
                objects = (ArrayList<Object>) ContactsDataHelper.getOutGoingCalls(getActivity(), mIdOrTelephone);
            } catch (Exception e) {
                Log.d(this.getClass().getName(), e.getMessage());

            }
        }

        @Override
        protected void DoneAction() {
            ProofMultiSelectFragmentActivity.readdUnusedTab();
        }

        @Override
        protected void DeleteAllAction() {
            MenuActions.deleteCalls(recordIds, recordPaths);
            ProofMultiSelectFragmentActivity.removeCurrentTab(getInternalContext());
        }

        @Override
        protected void preDeleteAndShareAction() {
            int iter = 0;

            for (Object item : objects) {
                Record lcRecord = (Record) item;

                if (lcRecord.isChecked()) {
                    try {
                        recordIds[iter] = lcRecord.getmId();
                        recordPaths[iter] = lcRecord.getmFilePath();

                        iter++;
                    } catch (Exception e) {
                        Log.d(this.getClass().getName(), e.getMessage());
                    }
                }
            }
        }

        @Override
        protected void DeleteAction() {

            MenuActions.deleteCalls(recordIds, recordPaths);

            ArrayList<Object> toBeProcessed = new ArrayList<Object>();

            for (Object item : objects) {
                Record lcRecord = (Record) item;

                if (lcRecord.isChecked()) {
                    toBeProcessed.add(lcRecord);
                }
            }

            for (Object item : toBeProcessed) {
                ((RecordAdapter) listAdapter).remove(item);
                objects.remove(item);
            }

            ((RecordAdapter) listAdapter).notifyDataSetChanged();
        }

        @Override
        protected boolean itemChecked(Object item) {
            return ((Record) item).isChecked();
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
        protected void _doInBackground(Void... params) {
            getContacts();
        }

        @Override
        protected int collectionSorter(Object object1, Object object2) {
            Record r1 = (Record) object1;
            Record r2 = (Record) object2;
            return (r1.getmTimeStamp().compareToIgnoreCase(r2.getmTimeStamp()));
        }

        @Override
        protected void initAdapter(Context context, List<Object> collection,
                                   int layoutId, boolean multiSelectMode) {
            listAdapter = null;
            listAdapter = new RecordAdapter(context, collection, layoutId, multiSelectMode, getBroadcastName());
        }

        @Override
        protected void uncheckItem(Object item) {
            ((Record) item).setChecked(false);
        }

        @Override
        protected void toggleItem(Object item, boolean checked) {
            ((Record) item).setChecked(checked);
        }

        @Override
        protected Object getItemClone(Object item) {
            return ((Record) item).clone();
        }

        @Override
        protected void alertDlgCancelAction(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub

        }
    }
}
