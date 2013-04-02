package org.proof.recorder.fragment.dialog;

import org.proof.recorder.R;
import org.proof.recorder.Settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class AboutApps extends SherlockFragmentActivity {
	
	private static Context mContext;
	
	private ListView mListView;
	private Spanned[] mLinks = {			
			Html.fromHtml("<a mailto:contact@frugandfrog.com >contact</a>"),
			Html.fromHtml("<a href='www.frugandfrog.com' >www.frugandfrog.com</a>")
	};
	
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
	 * @param message
	 */
	private static void print(String message) {
		if(Settings.isDebug())
			Log.d(AboutApps.class.getName(), message);
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
		
		mListView = (ListView) findViewById(R.id.some_links_list);
		
		mListView.setAdapter(
				new ArrayAdapter<Spanned>(
						this, 
						android.R.layout.simple_list_item_1, 
						mLinks)
						);
		
		mListView.setItemsCanFocus(true);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
				Spanned link = (Spanned)mListView.getItemAtPosition(position);
				String mLink = cleanString(link.toString());
				String frug = "frugandfrog.com";
				
				print("mLink:" + mLink);
				
				if(mLink.equals("contact")) {
					
					print("mLink clicked!");
					
					String email = mLink + "@" + frug;
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
				if(mLink.equals("www." + frug)) {
					print("mLink clicked!");
					Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://" + mLink));
					startActivity(browserIntent);
				}
			}
		});
	}
}
