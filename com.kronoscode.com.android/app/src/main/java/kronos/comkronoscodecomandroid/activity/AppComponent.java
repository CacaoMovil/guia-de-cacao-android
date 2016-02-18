package kronos.comkronoscodecomandroid.activity;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import kronos.comkronoscodecomandroid.activity.activity.BaseActivity;
import kronos.comkronoscodecomandroid.activity.activity.GuideActivity;
import kronos.comkronoscodecomandroid.activity.activity.MainActivity;
import kronos.comkronoscodecomandroid.activity.activity.WelcomeActivity;
import kronos.comkronoscodecomandroid.activity.adapter.GuideAdapter;
import kronos.comkronoscodecomandroid.activity.api.ApiModule;

/**
 * Created by jhon on 12/02/16.
 */
@Singleton
@Component(
        modules = {
            AppModule.class,
                ApiModule.class,
        }
)
public interface AppComponent {
    // Exported for child-components.
    Application application();
    void inject(App application);

    // Activities
    void inject(MainActivity target);

    void inject(BaseActivity target);

    void inject(GuideActivity target);

    void inject(WelcomeActivity  target);

    // Adapters
    void inject(GuideAdapter target);

}

