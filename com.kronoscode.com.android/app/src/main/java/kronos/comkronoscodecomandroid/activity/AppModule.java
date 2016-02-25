package kronos.comkronoscodecomandroid.activity;

import android.app.Application;
import android.content.ContentResolver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import kronos.comkronoscodecomandroid.activity.api.ApiModule;

/**
 * Created by jhon on 12/02/16.
 */
@Module(
        includes = {
                AndroidModule.class,
                ApiModule.class
        }
)
public class AppModule {
    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return app;
    }

    @Provides
    @Singleton
    EventBus provideBus() {
        return new EventBus();
    }

    @Provides
    @Singleton
    ContentResolver provideContentResolver() {
        return app.getContentResolver();
    }
}
