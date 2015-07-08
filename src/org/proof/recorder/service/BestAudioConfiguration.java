package org.proof.recorder.service;

import android.os.Bundle;

/**
 * @author clement
 *
 */
public class BestAudioConfiguration {


	public BestAudioConfiguration() {

	}
	
	/**
	 * @param capabilities (Bundle)
	 * @return best capabilities
	 */
	public static Bundle bestCapabilities(Bundle capabilities) {
		Bundle BestConf = new Bundle();
		boolean voiceCall = capabilities.getBoolean("voice_call");
		boolean voiceReco = capabilities.getBoolean("voice_reco");
		boolean voiceCom = capabilities.getBoolean("voice_com");
		
		if (voiceCall) {
			BestConf.putString("key", "voice_call");
			return BestConf;
		} else if (voiceReco) {
			BestConf.putString("key","voice_reco");
			return BestConf;

		} else if (voiceCom) {
			BestConf.putString("key","voice_com");
			return BestConf;

		} else {
			BestConf.putString("key","mic");
			return BestConf;
		}
	}

}
