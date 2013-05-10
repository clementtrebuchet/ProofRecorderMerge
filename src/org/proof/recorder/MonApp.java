package org.proof.recorder;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.proof.recorder.utils.Log.Console;

@ReportsCrashes(formKey = "dEdhREpjQThYVXNVakltRmhFSGktRmc6MQ") 
public class MonApp extends android.app.Application {
	
	@Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
		
		// Try / catch: might fix crashes on some devices.
		try {
			ACRA.init(this);
		}
		catch(Exception e) {
			Console.print_exception(e);
		}
        
        super.onCreate();
    }
}
