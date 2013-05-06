package org.proof.recorder.fragment.dialog;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.utils.Log.Console;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class AboutApps extends SherlockFragmentActivity {
	
	private static Context mContext;
	
	private ListView mListView;
	private TextView mAboutApp;
	
	private String[] mLinks;
	
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
		AboutApps.mContext = mContext;
	}
	
	/**
	 * @param str
	 * @return
	 */
	private static String cleanString(String str) {
		return str.replace(" ", "").trim().toLowerCase();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_apps_dialog);
		
		setContext(this);
		
		mAboutApp = (TextView) findViewById(R.id.txt_app);
		mAboutApp.setText(Html.fromHtml((String) getText(R.string.about_app)));		
		
		mLinks = new String[]{			
				(String) getText(R.string.contact_uri),
				(String) getText(R.string.site_uri)
		};
		
		String versionName = Settings.getpInfo().versionName;
		if(versionName != null) {
			mLinks[2] = (String) getText(R.string.app_name) + "(" + versionName + ")";
		}	
		
		mListView = (ListView) findViewById(R.id.some_links_list);
		
		mListView.setAdapter(
				new ArrayAdapter<String>(
						this, 
						android.R.layout.simple_list_item_1, 
						mLinks)
						);
		
		mListView.setItemsCanFocus(true);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
				String link = (String)mListView.getItemAtPosition(position);
				String mLink = cleanString(link);
				String phoneandvoice = "frugandfrog.com";
				String webMatch = "phone&voicerecorder";
				
				Console.print_debug("mLink:" + mLink);
				
				if(mLink.equals("contact")) {
					
					Console.print_debug("mLink clicked!");
					
					String email = mLink + "@" + phoneandvoice;
					String subject = getContext().getString(R.string.contact_subject);
					String body = getContext().getString(R.string.contact_body);					
					
					Intent intent = new Intent(Intent.ACTION_VIEW);
					
					Uri data = Uri.parse(
							"mailto:" + email + 
							"?subject=" + subject + 
							"&body=" + body
					);
					
					intent.setData(data);
					startActivity(intent);
				}
				
				if(mLink.equals(webMatch)) {
					
					Console.print_debug("mLink clicked!");
					
					Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://www." + phoneandvoice));
					startActivity(browserIntent);
				}
			}
		});
	}
}
