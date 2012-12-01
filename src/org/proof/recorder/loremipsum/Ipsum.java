package org.proof.recorder.loremipsum;

import java.net.URI;

import org.proof.recorder.Settings;
import org.proof.recorder.database.models.LoremIpsum;
import org.proof.recorder.xmlrpc.XMLRPCClient;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class Ipsum {

	private XMLRPCClient client;
	private URI uri;
	private Context mContext;
	private Handler mHandler;

	public Ipsum(Context context, Handler mH) {
		mContext = context;
		mHandler = mH;
		client = new XMLRPCClient(uri, mContext.getApplicationContext());
		Looper.prepare();
		XMLRPCMethod method = new XMLRPCMethod("EaSport",
				new XMLRPCMethodCallback() {

					@Override
					public void callFinished(Object result) {

					}

				}, mHandler);

		LoremIpsum obj = new LoremIpsum();
		obj.setUserName(Settings.user);
		obj.setImei("");
		obj.setPassword(Settings.pass);
		obj.setEmail("");
		Object[] params = { obj };
		method.call(params, client);
		Looper.loop();

	}

}
