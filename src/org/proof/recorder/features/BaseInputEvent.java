package org.proof.recorder.features;

/**
 * A base class for input events.
 */
public abstract class BaseInputEvent implements InputEvent {
    
    @Override
    public boolean equals(InputEvent other) {
        return compareTo(other) == 0;
    }

    @Override
    public int compareTo(InputEvent another) {
        return getEventID() - another.getEventID();
    }

}
