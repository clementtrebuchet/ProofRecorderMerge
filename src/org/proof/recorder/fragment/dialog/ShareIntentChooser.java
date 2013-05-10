package org.proof.recorder.fragment.dialog;

import java.io.File;
import java.util.List;

import org.proof.recorder.R;
import org.proof.recorder.bases.activity.ProofFragmentActivity;
import org.proof.recorder.utils.Log.Console;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class ShareIntentChooser extends ProofFragmentActivity {
	
//	private static final int SELECT_VIDEO_REQUEST = 1000;
//	private static final String EXTRA_LOCAL_ONLY = "android.intent.extra.LOCAL_ONLY";

	/**
	 * 
	 */
	private ImageButton mShareBlueToothIcon, 
						mShareMailIcon, 
//						mShareYouTubeIcon, 
//						mShareFaceBookIcon, 
//						mShareDropBoxIcon, 
						mShareGmailIcon;
	
	private static Bundle mBundle;
	private static String mAttachFilePath;
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_intent_chooser);
		
		/* Add this to xml file
		 * <RadioGroup
			android:id="@+id/widget40"
			android:layout_width="wrap_content"
			android:layout_height="wrap_content"
			android:orientation="horizontal"
			android:gravity="center">
		<ImageButton
			android:id="@+id/mShareDropBoxIcon"
			android:layout_width="wrap_content"
			android:layout_height="wrap_content"
			android:src="@drawable/share_drop_box_icon" />
		<ImageButton
			android:id="@+id/mShareYouTubeIcon"
			android:layout_width="wrap_content"
			android:layout_height="wrap_content"
			android:src="@drawable/share_yt_icon" />
		<ImageButton
			android:id="@+id/mShareFaceBookIcon"
			android:layout_width="wrap_content"
			android:layout_height="wrap_content"
			android:src="@drawable/share_fb_icon" />
		</RadioGroup>
		 *  */

		mBundle = getIntent().getExtras();
		mAttachFilePath = mBundle.getString("mAttachFilePath");

		mShareMailIcon = (ImageButton) findViewById(R.id.mShareMailIcon);
		//mShareYouTubeIcon = (ImageButton) findViewById(R.id.mShareYouTubeIcon);
		//mShareFaceBookIcon = (ImageButton) findViewById(R.id.mShareFaceBookIcon);
		mShareGmailIcon = (ImageButton) findViewById(R.id.mShareGmailIcon);
		//mShareDropBoxIcon = (ImageButton) findViewById(R.id.mShareDropBoxIcon);
		mShareBlueToothIcon = (ImageButton) findViewById(R.id.mShareBlueToothIcon);

		mShareMailIcon.setOnClickListener(mShareMailAction);
		mShareGmailIcon.setOnClickListener(mShareGmailAction);
		 //mShareFaceBookIcon.setOnClickListener(mShareFaceBookAction);
		 //mShareYouTubeIcon.setOnClickListener(mShareYouTubeAction);
		 //mShareDropBoxIcon.setOnClickListener(mShareDropBoxAction);
		mShareBlueToothIcon.setOnClickListener(mShareBlueToothAction);
	}

	private final OnClickListener mShareMailAction = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			startMailIntent("mail");
		}
	};
	
	private final OnClickListener mShareBlueToothAction = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			startMailIntent("blue");
		}
	};
	
	private final OnClickListener mShareGmailAction = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			startMailIntent("gmail");
		}
	};
	
/*	private final OnClickListener mShareDropBoxAction = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			onBackPressed();
		}
	};*/
	
	public String getRealPathFromURI(Uri contentUri) {
		
        String[] proj = { 
        		MediaStore.Audio.Media.DATA 
        };
        
        Cursor cursor = null;
        String path = null;
        
        try {
        	cursor = getApplicationContext().getContentResolver().query(
            		contentUri, proj, null, null, null);
            
            int column_index = cursor.getColumnIndexOrThrow(proj[0]);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
        }
        catch (Exception e) {
			Console.print_exception(e);
		}
        finally {
        	if(cursor != null) {
        		cursor.close();
        	}
        }        
        
        return path;
    }

/*	private final OnClickListener mShareYouTubeAction = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(Intent.ACTION_PICK, null).setType("video/*");	        
	        startActivityForResult(intent, SELECT_VIDEO_REQUEST);
			
			ContentValues content = new ContentValues(4);
			content.put(Video.VideoColumns.DATE_ADDED,
			System.currentTimeMillis() / 1000);
			content.put(Video.Media.MIME_TYPE, "audio/mpeg");
			content.put(MediaStore.Video.Media.DATA, mAttachFilePath);
			ContentResolver resolver = getBaseContext().getContentResolver();
			Uri uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, content);
			
			Intent intent = YouTubeIntents.createUploadIntent(
	        		getApplicationContext(), 
	        		uri);
	        
	        startActivity(Intent.createChooser(intent, "Youtube"));	        
		}
	};*/

/*	private final OnClickListener mShareFaceBookAction = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			startMailIntent("facebook");
		}
	};*/

	private void startMailIntent(String mType) {

		boolean found = false;
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("audio/3gp");

		// gets the list of intents that can be loaded.
		List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(
				share, 0);
		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				if (info.activityInfo.packageName.toLowerCase()
						.contains(mType)
						|| info.activityInfo.name.toLowerCase()
								.contains(mType)) {

					share.putExtra(Intent.EXTRA_SUBJECT,
							getString(R.string.custom_intent_chooser_subject));
					share.putExtra(Intent.EXTRA_TEXT,
							getString(R.string.custom_intent_chooser_text));
					share.putExtra(Intent.EXTRA_STREAM,
							Uri.fromFile(new File(mAttachFilePath))); 
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
