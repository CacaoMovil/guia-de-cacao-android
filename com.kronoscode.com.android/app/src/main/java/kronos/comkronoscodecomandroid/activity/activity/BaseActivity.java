package kronos.comkronoscodecomandroid.activity.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInjectComponent(this).inject(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
               // nothing yet
            }
        }, nextUpdate);

    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
}
