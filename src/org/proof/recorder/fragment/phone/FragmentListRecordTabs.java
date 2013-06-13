package org.proof.recorder.fragment.phone;

import org.proof.recorder.R;
import org.proof.recorder.bases.activity.ProofMultiSelectFragmentActivity;
import org.proof.recorder.database.support.AndroidContactsHelper;
import org.proof.recorder.fragment.phone.FragmentListRecordIn.InCommingCallsLoader;
import org.proof.recorder.fragment.phone.FragmentListRecordOut.OutGoingCallsLoader;
import org.proof.recorder.utils.Log.Console;

import android.os.Bundle;

public class FragmentListRecordTabs extends ProofMultiSelectFragmentActivity {

	/**
	 * @return the mRecordId
	 */
	public static long getSavedId() {
		return Long.parseLong(_id);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}

	@Override
	protected KIND getKind() {
		return KIND.CALL;
	}

	@Override
	protected String idKey() {
		return "RecordId";
	}

	@Override
	protected int tabOneResourceId() {
		return R.string.strIncommingTab;
	}

	@Override
	protected int tabSecondResourceId() {
		return R.string.strOutcommingTab;
	}

	@Override
	protected void setUpTabsClasses() {
		classOne = FragmentListRecordIn.InCommingCallsLoader.class;
		classSecond = FragmentListRecordOut.OutGoingCallsLoader.class;		
	}

	@Override
	protected void setUpTabsCount() {
		
		String mIdOrTelephone = (String) extraData.get("mIdOrTelephone");		

		int in, out;		

		in = AndroidContactsHelper.getInRecordsCount(mIdOrTelephone);
		out = AndroidContactsHelper.getOutRecordsCount(mIdOrTelephone);

		Console.print_debug("TITLED: " + in + " " + "UNTITLED: " + out);

		if (in > 0) {
			hasOne = true;
		}
		else
			hasOne = false;

		if (out > 0) {
			hasSecond = true;
		}
		else
			hasSecond = false;
	}

	@Override
	protected boolean isMulti() {
		return OutGoingCallsLoader.multiSelectEnabled |
			   InCommingCallsLoader.multiSelectEnabled;
	}

	@Override
	protected int innerContentView() {
		return R.layout.fragment_inout_records_tabs;
	}
}
