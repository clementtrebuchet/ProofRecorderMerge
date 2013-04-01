import org.proof.recorder.database.models.Contact;
import org.proof.recorder.fragment.contacts.utils.ContactsDataHelper;
import org.proof.recorder.service.ServiceAudioRecord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class ObservateurTelephone extends PhoneStateListener {
	private static final String TAG = "ObservateurTelephone";
	private Context _context = null;
	private static String _outNumber = null;
	protected ServiceAudioRecord mBoundService;
	protected TelephonyManager _monManagerTel;
	static String sENS_COM;

	// Stopping Service from excluded contacts list
	// getting the SPEAKERON parameter to false by default for this case

	private static boolean isExcluded;

	/**
	 * @return the isExcluded
	 */
	public boolean isExcluded() {
		return isExcluded;
	}

	/**
	 * @param isExcluded
	 *            the isExcluded to set
	 */
	private void setExcluded(boolean isExcluded) {
		ObservateurTelephone.isExcluded = isExcluded;
	}

	public void getContext(Context context) {
		_context = context;
	}

	synchronized public void getManager(TelephonyManager telephony) {
		_monManagerTel = telephony;
	}

	synchronized @Override
	public void onCallStateChanged(int state, String incomingNumber) {

		if (_context == null)
			return;

		if (Settings.isDebug()) {
			Log.v(TAG, incomingNumber);
			Log.v(TAG, "===============L\'ETAT A CHANGER=================");
		}

		switch (state) {
		case TelephonyManager.CALL_STATE_RINGING:
			if (Settings.isDebug())
				Log.d(TAG,
						"=================LE TELEPHONE SONNE================"
								+ incomingNumber);
			String info = "Le téléphone sonne " + incomingNumber;

			if (Settings.isToastNotifications())
				Toast.makeText(_context, info, Toast.LENGTH_SHORT).show();
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:

			boolean excluded = false;
			Contact contact = new Contact();
			
			try {
				contact.setPhoneNumber(_outNumber);
			} catch (Exception e) {
				if (Settings.isDebug())
					Log.e(TAG, 
							"Exception 'ContactsDataHelper.isExcluded(<context>, ': " + _outNumber + "')" +
							"Contact info: " + contact + 
							"Details': " + e);
			}			
			
			setExcluded(excluded);
			
			if (Settings.isDebug()) {
				Log.d(TAG,
						"***********************************************"
						);
				Log.d(TAG,
						"Contact Number: " + _outNumber + " is excluded: " + excluded
						);
				Log.d(TAG,
						"***********************************************"
						);
			}				
			
			if (excluded) {
				try {
					this.finalize();
				} catch (Throwable e) {
					if (Settings.isDebug())
						Log.e(TAG, "Exception ObservateurTelephone()->this.finalize(): " + e);
				}
				return;
			}

			if (Settings.isDebug())
				Log.d(TAG,
						"=================L\'APPEL A ETE PRIS================");

			Intent Is = new Intent(_context, ServiceAudioRecord.class);
			Bundle b = new Bundle();
			
			b.putString("Number", _outNumber);
			b.putString("SENS", sENS_COM);

			if (Settings.isDebug()){
				Log.v(TAG, sENS_COM);
			}				

			Is.addFlags(Intent.FLAG_FROM_BACKGROUND);
			Is.putExtras(b);
			
			_context.startService(Is);
			
			break;
		case TelephonyManager.CALL_STATE_IDLE:

			if (Settings.isDebug())
				Log.d(TAG,
						"=================L\'APPEL Etat IDLE================");

			Intent I = new Intent(_context, ServiceAudioRecord.class);
			_context.stopService(I);
			_monManagerTel.listen(ObservateurTelephone.this,
					PhoneStateListener.LISTEN_NONE);
			break;
		}

	}

	public void feedNumbers(String phonenumber) {
		_outNumber = phonenumber;

	}

	/*
	 * true = ENTRANT false = SORTANT
	 */

}