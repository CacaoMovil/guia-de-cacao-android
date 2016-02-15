package kronos.comkronoscodecomandroid.activity.event;

import android.support.annotation.StringRes;

/**
 * Created by jhon on 14/02/16.
 */
public class ToastEvent {

    @StringRes
    private int stringId = 0;
    private CharSequence message = null;
    private boolean lengthShort = true;

    public ToastEvent(@StringRes int stringId) {
        this.stringId = stringId;
    }

    public ToastEvent(@StringRes int stringId, boolean lengthShort) {
        this.stringId = stringId;
        this.lengthShort = lengthShort;
    }

    public ToastEvent(CharSequence message) {
        this.message = message;
    }

    public ToastEvent(CharSequence message, boolean lengthShort) {
        this.message = message;
        this.lengthShort = lengthShort;
    }

    public int getStringId() {
        return stringId;
    }

    public CharSequence getMessage() {
        return message;
    }

    public boolean isLengthShort() {
        return lengthShort;
    }
}
