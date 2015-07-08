package org.proof.recorder.apis.soundcloud;

import android.os.Environment;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Endpoints;
import com.soundcloud.api.Http;
import com.soundcloud.api.Params;
import com.soundcloud.api.Request;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.proof.recorder.services.SoundCloudBgUploader;
import org.proof.recorder.utils.Log.Console;

import java.io.File;
import java.io.IOException;

/**
 * Upload single/multiple file(s) to SoundCloud.
 */
public final class FileUploader {
	
	
	private static boolean mExternalStorageAvailable = false;
	private static boolean mExternalStorageWriteable = false;
	
	private SoundCloudBgUploader parent = null;
	
	public FileUploader(SoundCloudBgUploader soundCloudBgUploader) {
		parent = soundCloudBgUploader;
	}
	
	/**
	 * @return the mExternalStorageAvailable
	 */
	private static boolean isExternalStorageAvailable() {
		return mExternalStorageAvailable;
	}

	/**
	 * @return the mExternalStorageWriteable
	 */
	private static boolean isExternalStorageWriteable() {
		return mExternalStorageWriteable;
	}
	
	/**
	 * 
	 */
	private void evaluateMedia() {
		
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but all we need
			//  to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
	}
	
	private String getWrapperFileName() {
		
		String fileName = null;
		
		evaluateMedia();
		
		if(isExternalStorageAvailable() && isExternalStorageWriteable()) {
			fileName = Environment.getExternalStorageDirectory().getPath() + 
					   "/ProofRecorder/wrapper.ser";
		}
		
		return fileName;
	}
	
	private CreateWrapper connect() {
		
		String fileName = getWrapperFileName();
		
		if(fileName == null) 
			return null;
		
		CreateWrapper wrapper = new CreateWrapper(fileName);
		
		try {
			wrapper.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			wrapper = null;
		}
		
		return wrapper;
	}
	
	public void upload(String fileName) throws IOException, ClassNotFoundException {
		
		if(fileName == null)
			throw new IOException("You must provide a file name!");
			
		File file = new File(fileName);
		
		if(!file.exists())
			throw new IOException("You must provide a valid file name!");	
		
		CreateWrapper wrap = connect();
		
		if(wrap == null) {
			parent.stopSelf();
			return;
		}
		
		final File wrapperFile = wrap.WRAPPER_SER;
		
		final ApiWrapper wrapper = ApiWrapper.fromFile(wrapperFile);
		
        Console.print_debug("Uploading " + file);
        
        try {
            HttpResponse resp = wrapper.post(
            		Request.to(Endpoints.MY_TRACKS)
                    .add(Params.Track.TITLE, file.getName())
                    .add(Params.Track.TAG_LIST, "demo upload")
                    .withFile(Params.Track.ASSET_DATA, file)
                    // you can add more parameters here, e.g.
                    // .withFile(Params.Track.ARTWORK_DATA, file)) /* to add artwork */

                    // set a progress listener (optional)
                    .setProgressListener(new Request.TransferProgressListener() {
                    	
                        @Override 
                        public void transferred(long amount) {
                        	Console.print_debug("Uploaded bytes: " + amount);
                        }
                        
                    }));

            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
            	Console.print_debug("\n201 Created " + resp.getFirstHeader("Location").getValue());

                // dump the representation of the new track
            	Console.print_debug("\n" + Http.getJSON(resp).toString(4));
            } else {
            	Console.print_exception("Invalid status received: " + resp.getStatusLine());
            }
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            // serialise wrapper state again (token might have been refreshed)
            wrapper.toFile(wrapperFile);
        }
	}
}
