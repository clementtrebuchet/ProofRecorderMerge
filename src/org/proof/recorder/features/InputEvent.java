package org.proof.recorder.features;

/**
 * An interface to represent different types of input.
 */
public interface InputEvent extends Comparable<InputEvent> {
    /**
     * A unique event ID.
     */
    public int getEventID();
    
    /**
     * The name of the event.
     * To be used in the settings UI.
     */
    public CharSequence getEventName();
    
    /**
     * Description to use in a tooltip, for example.
     */
    public CharSequence getEventDescription();
    
    public boolean equals(InputEvent other);
}
