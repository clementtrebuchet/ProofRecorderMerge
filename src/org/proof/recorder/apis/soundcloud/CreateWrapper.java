package org.proof.recorder.apis.soundcloud;

import java.io.File;
import java.io.IOException;

import org.proof.recorder.utils.Log.Console;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Token;

/**
 * Creates an API wrapper instance, obtains an access token and serialises the wrapper to disk.
 * The serialised wrapper can then be used for subsequent access to resources without reauthenticating
 * @see GetResource
 */
public class CreateWrapper {
	
    public File WRAPPER_SER = null;    
    
    public CreateWrapper(String wrapperFileName) {
    	WRAPPER_SER =  new File(wrapperFileName);
    }

    public void connect() throws IOException {

            final ApiWrapper wrapper = new ApiWrapper(
            		"26d725db0d77a8f8001b675d3a10a622" /* client_id */,
            		"5347feb4863042bbe9c64c57f967312e" /* client_secret */,
                    null    /* redirect URI */,
                    null    /* token */);

            Token token = wrapper.login("namgyal.brisson" /* login */, "Storm1984" /* password */);

            Console.print_debug("got token from server: " + token);

            // in this example the whole wrapper is serialised to disk -
            // in a real application you would just save the tokens and usually have the client_id/client_secret
            // hardcoded in the application, as they rarely change
            wrapper.toFile(WRAPPER_SER);

            Console.print_debug("wrapper serialised to " + WRAPPER_SER);
        
    }
}
