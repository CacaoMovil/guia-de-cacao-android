package kronos.comkronoscodecomandroid.activity.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by jhon on 2/3/15.
 */
public class Utils {

    /**
     * Display toast message
     * @param context
     * @param msg
     */
    public static void toastMessage(Context context, String msg) {
        final Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 5000);
    }

}
