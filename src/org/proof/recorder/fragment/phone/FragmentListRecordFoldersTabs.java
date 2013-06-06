package org.proof.recorder.fragment.phone;

import org.proof.recorder.ProofRecorderActivity;
import org.proof.recorder.R;
import org.proof.recorder.bases.activity.ProofMultiSelectFragmentActivity;

import android.os.Bundle;

public class FragmentListRecordFoldersTabs extends ProofMultiSelectFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected KIND getKind() {
		return KIND.CONTACT;
	}

	@Override
	protected String idKey() {
		return _id;
	}

	@Override
	protected int tabOneResourceId() {
		return R.string.strKnownContactsTab;
	}

	@Override
	protected int tabSecondResourceId() {
		return R.string.strUnKnownContactsTab;
	}

	@Override
	protected void setUpTabsClasses() {
		classOne = FragmentListKnownContacts.KnownContactsLoader.class;
		classSecond = FragmentListUnKnownContacts.UnKnownContactsLoader.class;		
	}

	@Override
	protected void setUpTabsCount() {		
		hasOne = ProofRecorderActivity.bKnown;
		hasSecond = ProofRecorderActivity.bUnknown;
	}

	@Override
	protected boolean isMulti() {
		return FragmentListKnownContacts.KnownContactsLoader.isMulti |
				FragmentListUnKnownContacts.UnKnownContactsLoader.isMulti;
	}

	@Override
	protected int innerContentView() {
		return R.layout.fragment_inout_records_tabs;
	}

}
