package org.proof.recorder;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;




@ReportsCrashes(formKey = "dEdhREpjQThYVXNVakltRmhFSGktRmc6MQ") 
public class MonApp extends android.app.Application {
	
	@Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        super.onCreate();
    }
}
