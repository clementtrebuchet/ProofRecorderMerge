package org.proof.recorder.features;

public interface InputEventInterceptor {
    
    public void setListener(InputEventListener listener);
    
    public void startIntercepting();
    
    public void stopIntercepting();

}
