package org.proof.recorder.utils;

public interface PlugMiddleware {
	
	void callWhenReady();
	void parametersRecAsynchronously(int message);
	void startRecAsynchronously(int message);
	void stopRecAsynchronously(int message);

	void EncodeRawFileAsynchronously();

}
