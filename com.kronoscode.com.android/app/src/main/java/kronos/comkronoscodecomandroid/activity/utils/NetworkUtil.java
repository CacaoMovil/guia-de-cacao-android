package kronos.comkronoscodecomandroid.activity.utils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.greenrobot.event.EventBus;
import kronos.comkronoscodecomandroid.activity.api.SettingsService;
import kronos.comkronoscodecomandroid.activity.event.UpdateSettingsEvent;
import kronos.comkronoscodecomandroid.activity.object.Setting;
import kronos.comkronoscodecomandroid.activity.prefs.PersistentStore;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by jhon on 16/2/16.
 */
@Singleton
public class NetworkUtil {

    @Inject
    Application application;

    @Inject
    public NetworkUtil() {

    }

    /**
     * Check internet connection
     * @return
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
