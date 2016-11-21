package kronos.comkronoscodecomandroid.activity.activity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.kronoscode.cacao.android.app.database.table.EventTable;
import com.kronoscode.cacao.android.app.model.Event;
import com.kronoscode.cacao.android.app.provider.CacaoProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import kronos.comkronoscodecomandroid.R;
import kronos.comkronoscodecomandroid.activity.App;
import kronos.comkronoscodecomandroid.activity.api.EventService;
import kronos.comkronoscodecomandroid.activity.api.SettingsService;
import kronos.comkronoscodecomandroid.activity.object.EventObject;
import kronos.comkronoscodecomandroid.activity.object.Setting;
import kronos.comkronoscodecomandroid.activity.prefs.PersistentStore;
import kronos.comkronoscodecomandroid.activity.utils.DatabaseUtil;
import kronos.comkronoscodecomandroid.activity.utils.GuideUtils;
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

    @Inject
    EventService eventService;

    @Inject
    GuideUtils guideUtils;

    @Inject
    DatabaseUtil database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInjectComponent(this).inject(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Android 6.0 - Request Storage permission
        requestWriteExternalStoragePermission();
        //insertEvent("23");
        getRemoteEvents();


    }

    private int getDayOfCurrentDate() {
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.DAY_OF_MONTH);
    }

    private int getDayOfEvent(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

        Date convertedDate = new Date();

        try {
            convertedDate = sdf.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(convertedDate);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * This function will request data from the server
     */
    public void getRemoteEvents() {
        Call<List<EventObject>> call = eventService.getEvents(Locale.getDefault().getCountry());

        call.enqueue(new Callback<List<EventObject>>() {
            @Override
            public void onResponse(Response<List<EventObject>> response, Retrofit retrofit) {
                if (response.body() != null) {
                    database.cleanEventsTable();
                    for (EventObject eventObject : response.body()) {
                        insertEvent(eventObject);
                        //openEvent(eventObject);
                    }
                }

                sendNotifications(Event.listFromCursor(guideUtils.getEvents()));
            }
            @Override
            public void onFailure(Throwable t) {
                sendNotifications(Event.listFromCursor(guideUtils.getEvents()));
            }
        });
    }

    private void sendNotifications(List<Event> events) {
        if (events.size() > 0) {
            for (Event event : events) {
                if ((getDayOfCurrentDate() - getDayOfEvent(event.getStartDate()) == 1)) {
                    openEvent(event);
                }
            }
        }
    }

    private void insertEvent(EventObject eventObject) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        ContentValues values = new ContentValues();
        values.put(EventTable.EVENTID, eventObject.getName());
        values.put(EventTable.DESCRIPTION, eventObject.getDescription());
        values.put(EventTable.STARTDATE, eventObject.getStart());
        values.put(EventTable.ENDDATE, eventObject.getEnd());

        ContentProviderOperation branchesOperation = ContentProviderOperation.newInsert(CacaoProvider.EVENT_CONTENT_URI).withValues(values).build();
        operations.add(branchesOperation);

        try {
            getContentResolver().applyBatch(CacaoProvider.AUTHORITY, operations);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    private void openEvent(Event event) {
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("title", event.getEventId());
        intent.putExtra("startDate", event.getStartDate());
        intent.putExtra("description", event.getDescription());
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification n = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(event.getEventId())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();

        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }

    private void requestWriteExternalStoragePermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permisos de escritura requeridos")
                    //.setMessage("You need to enable permissions")
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
