package org.proof.recorder.loremipsum;

import org.apache.http.conn.HttpHostConnectException;
import org.proof.recorder.xmlrpc.XMLRPCClient;
import org.proof.recorder.xmlrpc.XMLRPCException;
import org.proof.recorder.xmlrpc.XMLRPCFault;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

interface XMLRPCMethodCallback {
	void callFinished(Object result);
}
public class XMLRPCMethod extends Thread {
	private static final String TAG = "XMLRPCMethod_lorem";
	private String method;
	private Object[] params;
	private Handler handler;
	private XMLRPCMethodCallback callBack;
	public XMLRPCClient client;
	Handler mHandler;
	Message msg;
	
	public XMLRPCMethod(String method, XMLRPCMethodCallback xmlrpcMethodCallback, Handler mH) {
		this.method = method;
		this.callBack = xmlrpcMethodCallback;
		mHandler = mH;
		handler = new Handler();
	}



	public void call() {
		call(null, client);
	}

	public void call(Object[] params, XMLRPCClient cli) {
		
		Log.v(TAG, "params pass call for synchron");
		this.params = params;
		this.client = cli;
		start();
		
	}

	@Override
	public void run() {
		try {
			final Object result = client.callEx(method, params);

			handler.post(new Runnable() {
				public void run() {
					callBack.callFinished(result);
				}
			});
		} catch (final XMLRPCFault e) {
			handler.post(new Runnable() {
				public void run() {
					
					Log.d(TAG, "1 error", e);
					msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("ENDOP", 1);
					b.putInt("total", 100);
					b.putString("Hmsg", "Erreur "+e.toString());
					msg.setData(b);
					mHandler.sendMessage(msg);
					
				}
			});
		} catch (final XMLRPCException e) {
			handler.post(new Runnable() {
				public void run() {

					Throwable couse = e.getCause();
					if (couse instanceof HttpHostConnectException) {
						
						Log.d(TAG, "2 error", e);
						msg = mHandler.obtainMessage();
						Bundle b = new Bundle();
						b.putInt("ENDOP", 1);
						b.putInt("total", 100);
						b.putString("Hmsg", "Erreur "+e.toString());
						msg.setData(b);
						Looper.loop();
						mHandler.sendMessage(msg);

					} else {
						
						Log.d(TAG, "3 error", e);
						msg = mHandler.obtainMessage();
						Bundle b = new Bundle();
						b.putInt("ENDOP", 1);
						b.putInt("total", 100);
						b.putString("Hmsg", "Erreur "+e.toString());
						msg.setData(b);
						mHandler.sendMessage(msg);
						

					}
						Log.d(TAG, "4 error", e);
						msg = mHandler.obtainMessage();
						Bundle b = new Bundle();
						b.putInt("ENDOP", 1);
						b.putInt("total", 100);
						b.putString("Hmsg", "Erreur "+e.toString());
						msg.setData(b);
						mHandler.sendMessage(msg);
				}
			});
		}
	}

}
