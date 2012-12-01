package org.proof.recorder.fragment.notes;

import org.proof.recorder.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;


public class FragmentNotes extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.fragment_note_editor);
		FragmentManager fm = getSupportFragmentManager();

		// Create the list fragment and add it as our sole content.
		if (fm.findFragmentById(R.id.fragment_note_editor_frame) == null) {
			FragmentNotesEditor notes = new FragmentNotesEditor();
			fm.beginTransaction().add(R.id.fragment_note_editor_frame, notes).commit();
		}

	}

	
}
