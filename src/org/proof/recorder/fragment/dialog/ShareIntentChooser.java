package org.proof.recorder.fragment.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.proof.recorder.R;
import org.proof.recorder.bases.activity.ProofFragmentActivity;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ShareIntentChooser extends ProofFragmentActivity {
	
	private ListView shareIntents;
	private SimpleAdapter sharesAdapter;
	
	private static Bundle mBundle;
	private static String[] mAttachedFiles;
	private ArrayList<HashMap<String, String>> shareActions;
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_intent_chooser);

		mBundle = getIntent().getExtras();
		
		mAttachedFiles = mBundle.getStringArray("AttachedFiles");
		
		shareActions = new ArrayList<HashMap<String, String>>();
		
		HashMap<String, String> map;
 
        map = new HashMap<String, String>();
        map.put("key", "mail");
        map.put("title", getString(R.string.share_mail_title));
        map.put("description", getString(R.string.share_mail_desc));
        map.put("img", String.valueOf(R.drawable.share_mail_icon));
        
        shareActions.add(map);
 
        map = new HashMap<String, String>();
        map.put("key", "gmail");
        map.put("title", getString(R.string.share_gmail_title));
        map.put("description", getString(R.string.share_gmail_desc));
        map.put("img", String.valueOf(R.drawable.share_gmail_icon));
        
        shareActions.add(map);
 
        map = new HashMap<String, String>();
        map.put("key", "blue");
        map.put("title", getString(R.string.share_bluetooth_title));
        map.put("description", getString(R.string.share_bluetooth_desc));
        map.put("img", String.valueOf(R.drawable.share_bluetooth));
        
        shareActions.add(map);
		
		shareIntents = (ListView) findViewById(R.id.share_choices_list);
		
		sharesAdapter = new SimpleAdapter(getInternalContext(), 
										  shareActions, 
										  R.layout.list_item_image_title_desc, 
										  new String[] { "key", "img", "title", "description" }, 
										  new int[] { R.id.key, R.id.img, R.id.title, R.id.description }
		);
		
		shareIntents.setAdapter(sharesAdapter);
		
		
		shareIntents.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {
				HashMap<String, String> map = (HashMap<String, String>) shareIntents.getItemAtPosition(position);
				startMailIntent(map.get("key"));
			}
		});
	}

	private void startMailIntent(String mType) {

		boolean found = false;
		Intent share = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
		share.setType("audio/*");

		// gets the list of intents that can be loaded.
		List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(
				share, 0);
		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				if (info.activityInfo.packageName.toLowerCase(Locale.getDefault())
						.contains(mType)
						|| info.activityInfo.name.toLowerCase(Locale.getDefault())
								.contains(mType)) {

					share.putExtra(Intent.EXTRA_SUBJECT,
							getString(R.string.custom_intent_chooser_subject));
					share.putExtra(Intent.EXTRA_TEXT,
							getString(R.string.custom_intent_chooser_text));
					
					ArrayList<Uri> uris = new ArrayList<Uri>();
					for(String attachmentPath : mAttachedFiles) {
						Uri attachment = Uri.fromFile(new File(attachmentPath));
						uris.add(attachment);						
					}
					
					share.putParcelableArrayListExtra(Intent.EXTRA_STREAM,	uris); 
					
					share.setPackage(info.activityInfo.packageName);
					found = true;
					break;
				}
			}
			if (!found) {
				
				return;
			}
				

			startActivity(Intent.createChooser(share, "Select"));
		}
	}
}
