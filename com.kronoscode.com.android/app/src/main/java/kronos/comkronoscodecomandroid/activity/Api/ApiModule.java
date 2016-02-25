package kronos.comkronoscodecomandroid.activity.api;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import kronos.comkronoscodecomandroid.BuildConfig;
import kronos.comkronoscodecomandroid.activity.constants.Constants;
import kronos.comkronoscodecomandroid.activity.prefs.PersistentStore;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

@Module
public class ApiModule {

    @Inject
    PersistentStore persistentStore;

    @Provides
    public GuidesService provideGuidesService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(persistentStore.get(PersistentStore.API_URL, Constants.DEFAULT_API_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GuidesService.class);
    }

    @Provides
    public SettingsService settingsService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(persistentStore.get(PersistentStore.API_URL, Constants.DEFAULT_API_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(SettingsService.class);
    }
}
