package org.proof.recorder.features;


public class SpyEvt extends BaseInputEvent {
    public static final int ID = 1;
    public static final CharSequence NAME = "Double Tap";
    public static final CharSequence DESCRIPTION =
        "Occures when rapidly tapping the device twice";

    /* (non-Javadoc)
     * @see com.android.kino.logic.InputEvent#getEventID()
     */
    @Override
    public int getEventID() {
        return ID;
    }

    /* (non-Javadoc)
     * @see com.android.kino.logic.InputEvent#getEventName()
     */
    @Override
    public CharSequence getEventName() {
        return NAME;
    }

    /* (non-Javadoc)
     * @see com.android.kino.logic.InputEvent#getEventDescription()
     */
    @Override
    public CharSequence getEventDescription() {
        return DESCRIPTION;
    }


}
