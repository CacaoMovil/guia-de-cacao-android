package kronos.comkronoscodecomandroid.activity;

import android.app.ActivityManager;
import android.app.Application;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jhon on 12/02/16.
 */
@Module
public class AndroidModule {
    @Provides
    SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    NotificationManager provideNotificationManager(Application application) {
        return (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides
    DisplayMetrics provideDisplayMetrics(Application application) {
        return application.getResources().getDisplayMetrics();
    }

    @Provides
    AudioManager provideAudioManager(Application application) {
        return (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);
    }

    @Provides
    WifiManager.WifiLock provideWifiLock(Application application) {
        return ((WifiManager) application.getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "mcLock");
    }

    @Provides
    PackageManager providePackageManager(Application application) {
        return application.getPackageManager();
    }

    @Provides
    DownloadManager provideDownloadManager(Application application) {
        return (DownloadManager) application.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Provides
    ActivityManager provideActivityManager(Application application) {
        return (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
    }
}
