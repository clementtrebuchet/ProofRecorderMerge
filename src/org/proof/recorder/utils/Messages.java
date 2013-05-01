package org.proof.recorder.utils;
import android.content.Context;

public class Messages {

	private Messages() {}

	public static int getStringResource(Context context, String name) {
		
        int resId = context.getResources().getIdentifier(name, "string", "org.proof.recorder");
		
        return resId;
	}   
}
