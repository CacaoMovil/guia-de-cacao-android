package kronos.comkronoscodecomandroid.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import de.greenrobot.event.Subscribe;
import kronos.comkronoscodecomandroid.activity.event.ToastEvent;

/**
 * Created by jhon on 5/01/16.
 */
public class App extends Application {

    //@Inject
    //EventBus bus;

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    private AppComponent appComponent;

    public static final String DEFAULT_TAG_PREFIX = "com.cacao.android";
    public static final int MAX_TAG_LENGTH = 23; // if over: IllegalArgumentException: Log tag "xxx" exceeds limit of 23 characters

    public App() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //bus.register(this);

        // analytics
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-64984400-2"); // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);

    }

    public static String createTag(String name) {
        String fullName = DEFAULT_TAG_PREFIX + name;
        return fullName.length() > MAX_TAG_LENGTH ? fullName.substring(0, MAX_TAG_LENGTH) : fullName;
    }

    public static String createTag(Class clazz) {
        return createTag(clazz.getSimpleName());
    }


    private AppComponent getComponent() {
        return appComponent;
    }

    public static AppComponent getInjectComponent(Activity activity) {
        return getApplication(activity).getComponent();
    }

    public static AppComponent getInjectComponent(Fragment fragment) {
        return getApplication(fragment).getComponent();
    }

    /**
     * support for leanback fragments
     */
    public static AppComponent getInjectComponent(android.app.Fragment fragment) {
        return getApplication(fragment).getComponent();
    }

    public static AppComponent getInjectComponent(Context context) {
        return getApplication(context).getComponent();
    }


    /**
     * support for leanback fragments
     */
    public static App getApplication(android.app.Fragment fragment) {
        return (App) fragment.getActivity().getApplication();
    }

    public static App getApplication(Fragment fragment) {
        return (App) fragment.getActivity().getApplication();
    }

    public static App getApplication(Activity activity) {
        return (App) activity.getApplication();
    }

    public static App getApplication(Context context) {
        return (App) context.getApplicationContext();
    }

    @Subscribe
    public void onToastEvent(ToastEvent event) {
        Toast.makeText(this, event.getMessage(), event.isLengthShort() ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }
}
