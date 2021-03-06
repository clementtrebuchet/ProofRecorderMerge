package org.proof.recorder.fragment.voice;

import org.proof.recorder.ProofRecorderActivity;
import org.proof.recorder.R;
import org.proof.recorder.bases.activity.ProofMultiSelectFragmentActivity;
import org.proof.recorder.bases.fragment.ProofListFragmentWithQuickAction;

import android.os.Bundle;

public class FragmentListVoiceTabs extends ProofMultiSelectFragmentActivity {

	/**
	 * @return the voiceId
	 */
	public static String getSavedId() {
		return _id;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected KIND getKind() {
		return KIND.VOICE;
	}

	@Override
	protected String idKey() {
		return "voiceId";
	}

	@Override
	protected int tabOneResourceId() {
		return R.string.strTitledVoicesTab;
	}

	@Override
	protected int tabSecondResourceId() {
		return R.string.strUnTitledVoicesTab;
	}

	@Override
	protected void setUpTabsClasses() {		
		classOne = FragmentListVoice.VoiceListLoader.class;
		classSecond = FragmentListVoiceUntitled.VoiceListLoader.class;		
	}

	@Override
	protected void setUpTabsCount() {
		hasOne = ProofRecorderActivity.bTitled;
		hasSecond = ProofRecorderActivity.bUntitled;		
	}

	@Override
	protected boolean isMulti() {
		return ProofListFragmentWithQuickAction.multiSelectEnabled |
			   ProofListFragmentWithQuickAction.multiSelectEnabled;
	}

	@Override
	protected int innerContentView() {
		return R.layout.fragment_inout_records_tabs;
	}
}
