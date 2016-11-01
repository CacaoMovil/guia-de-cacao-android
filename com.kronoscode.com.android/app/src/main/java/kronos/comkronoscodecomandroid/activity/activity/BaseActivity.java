package kronos.comkronoscodecomandroid.activity.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.kronoscode.cacao.android.app.model.Guide;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import kronos.comkronoscodecomandroid.R;
import kronos.comkronoscodecomandroid.activity.App;
import kronos.comkronoscodecomandroid.activity.api.SettingsService;
import kronos.comkronoscodecomandroid.activity.event.UpdateSettingsEvent;
import kronos.comkronoscodecomandroid.activity.object.Setting;
import kronos.comkronoscodecomandroid.activity.prefs.PersistentStore;
import kronos.comkronoscodecomandroid.activity.utils.NetworkUtil;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by jhon on 2/03/2015.
 */
public class BaseActivity extends AppCompatActivity {

    private static final long WAIT_CHECK_TIME = 50 * 6000; // 5 Minutes
    private Timer timer;

    @Inject
    PersistentStore persistentStore;

    @Inject
    SettingsService settings;

    @Inject
    NetworkUtil networkUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInjectComponent(this).inject(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Android 6.0 - Request Storage permission

        requestWriteExternalStoragePermission();
    }

    private void requestWriteExternalStoragePermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Inform and request")
                    .setMessage("You need to enable permissions, bla bla bla")
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
              onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onPause() {
        stopTimer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        startTimer();
        super.onResume();
    }

    private void startTimer() {

        stopTimer();

        long lastUpdate = persistentStore.get(PersistentStore.LAST_UPDATE, Long.valueOf(0));

        final long currentTimestamp = Calendar.getInstance().getTimeInMillis();

        long countDown = currentTimestamp - lastUpdate;

        long nextUpdate = WAIT_CHECK_TIME - countDown;

        Log.d("Next Update..", String.valueOf(nextUpdate));

        if (nextUpdate < 0) {
            nextUpdate = 0;
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getSettings();
            }
        }, nextUpdate);

    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    public void getSettings() {

        if (!networkUtil.isNetworkAvailable()) {
            return;
        }

        Call<Setting> call = settings.getSettings();

        call.enqueue(new Callback<Setting>() {

            @Override
            public void onResponse(Response<Setting> response, Retrofit retrofit) {
                if (response.body() != null) {
                    persistentStore.set(PersistentStore.TITLE_CACACO, response.body().getTitle());
                    persistentStore.set(PersistentStore.WELCOME_CACAO, response.body().getWelcome_title());
                    persistentStore.set(PersistentStore.LOGO_CACAO, response.body().getLogo());
                    // Register last update
                    Date currentDate = new Date(System.currentTimeMillis());
                    persistentStore.set(PersistentStore.LAST_UPDATE, currentDate.getTime());
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }
}
