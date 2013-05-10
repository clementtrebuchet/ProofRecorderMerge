package org.proof.recorder.fragment.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.bases.activity.ProofFragmentActivity;
import org.proof.recorder.utils.AlertDialogHelper;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class PluginsInformations extends ProofFragmentActivity {

	protected static final String GPLAY = "market://search?q=pname:";
	private static Context mContext;
	private static ListView plugs;

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
		PluginsInformations.mContext = mContext;
	}

	/**
	 * @param message
	 */
	private static void print(String message) {
		if(!Settings.isDebug())
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.plugins_dialog);

		setContext(this);
		AlertDialogHelper.setContext(this);

		print("onCreate");

		// Array of strings storing country names
		String[] titles = new String[] {
				getString(R.string.plug_mp3_title),
				getString(R.string.plug_ogg_title),
				//getString(R.string.plug_ftp_title)
		};



		// Array of strings to store currencies
		String[] texts = new String[]{
				getString(R.string.plug_mp3_txt),
				getString(R.string.plug_ogg_txt),
				//getString(R.string.plug_ftp_txt),
		};

		// Array of integers points to images stored in /res/drawable-ldpi/
		int[] icons = new int[]{
				R.drawable.plug_mp3,
				R.drawable.plug_ogg,
				//R.drawable.plug_ftp
		};

		String[] mLinksIds = new String[] {
				getString(R.string.plug_mp3_link),
				getString(R.string.plug_ogg_link),
				//getString(R.string.plug_ftp_link)
		};

		// Keys used in Hashmap
		String[] from = {
				"icon", 
				"title", 
				"full_desc"
		};

		// Ids of views in listview_layout
		int[] to = {
				R.id.icon, 
				R.id.title, 
				R.id.full_desc
		};



		List<HashMap<String,String>> mPlugs = new ArrayList<HashMap<String,String>>();

		for(int i = 0; i < titles.length; i++){

			HashMap<String, String> hm = new HashMap<String, String>();

			hm.put("icon", Integer.toString(icons[i]) );
			hm.put("title", titles[i]);
			hm.put("full_desc", texts[i]);
			hm.put("gId", mLinksIds[i]);

			mPlugs.add(hm);
		}

		plugs = (ListView) findViewById(R.id.plugins_list);		

		SimpleAdapter adapter = new SimpleAdapter(
				getBaseContext(), mPlugs, R.layout.lv_plugins_layout, from, to);

		plugs.setAdapter(adapter);

		plugs.setItemsCanFocus(true);

		plugs.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {

				@SuppressWarnings("unchecked")
				HashMap<String, String> info = (HashMap<String, String>) plugs.getItemAtPosition(position);

				String mId = cleanString(info.get("gId"));

				Intent marketIntent = new Intent(
						"android.intent.action.VIEW", 
						Uri.parse(GPLAY + mId)
				);
				
				try {
                    startActivity(marketIntent);
                } catch (ActivityNotFoundException ex) {
                	AlertDialogHelper.openSimpleNoMatchDialog();
                }
			}
		});
	}

}
