package org.proof.recorder.utils;

@SuppressWarnings("unused")
public interface PlugMiddleware {
	
	void callWhenReady();
	void parametersRecAsynchronously(int message);
	void startRecAsynchronously(int message);
	void stopRecAsynchronously(int message);

	void EncodeRawFileAsynchronously();

}
