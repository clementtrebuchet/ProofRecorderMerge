package org.proof.recorder;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.proof.recorder.utils.Log.Console;


public class MonApp extends android.app.Application {

	@Override
    public void onCreate() {
		super.onCreate();
        /*
         * The following line triggers the initialization of ACRA 
         * Try / catch: might fix crashes on some devices.
		 */
		try {
			
		ACRA.init(this);
			ACRAConfiguration mACRA = ACRA.getNewDefaultConfig(this);
		mACRA.setFormKey("dEdhREpjQThYVXNVakltRmhFSGktRmc6MQ");
		ACRA.setConfig(mACRA);
		
			
		}
		catch(Exception e) {
			Console.print_exception(e);
		}        
    }
}
