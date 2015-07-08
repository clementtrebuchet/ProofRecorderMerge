package org.proof.recorder.fragment.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.bases.activity.ProofFragmentActivity;
import org.proof.recorder.database.models.Record;
import org.proof.recorder.utils.Log.Console;
import org.proof.recorder.utils.OsInfo;

import java.io.File;
import java.util.Locale;

public class AboutApps extends ProofFragmentActivity {
	
	private ListView mListView;

	private String fullAppName;
	
	/**
	 * @param str
	 * @return
	 */
	private static String cleanString(String str) {
		return str.replace(" ", "").trim().toLowerCase(Locale.getDefault());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_apps_dialog);

		TextView mAboutApp = (TextView) findViewById(R.id.txt_app);
		mAboutApp.setText(Html.fromHtml((String) getText(R.string.about_app)));

		String[] mLinks = new String[3];
					
		mLinks[0] = (String) getText(R.string.contact_uri);
		mLinks[1] = (String) getText(R.string.site_uri);
		
		fullAppName = "";
		
		final String versionName = Settings.getpInfo().versionName;
		if(versionName != null) {
			
			fullAppName = getText(R.string.app_name) + "(" + versionName + ")";
			mLinks[2] = fullAppName;
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
					String subject = getInternalContext().getString(R.string.contact_subject);
					String body = getInternalContext().getString(R.string.contact_body);					
					
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
				
				if(mLink.equals(cleanString(fullAppName))) {
					Record.setResolver(getContentResolver());					
					
					String directionCall, fileName;
					boolean created;
					
					for(int i = 0; i < 20; i++) {
						
						if(i%2 == 0) {
							directionCall = "E";
							fileName = OsInfo.newFileName("mp3");
						}
						else {
							directionCall = "S";
							fileName = OsInfo.newFileName("ogg");
						}					
						
						try {
							File file = new File(fileName);
							created = file.createNewFile();
						}catch (Exception e) {
							Console.print_exception(e);
							created = false;
						}
						
						if(created) {		
							
							int size;							
							
							if(i%2 == 0)
								size = 5 * 2 * 24 * i;
							else 
								size = 5 * 2 * 12 * i;

							Record rec = new Record("" + size, fileName, directionCall);
							rec.save();
							
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}						
					}					
				}
			}
		});
	}
}