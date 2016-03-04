package kronos.comkronoscodecomandroid.activity.api;

import dagger.Module;
import dagger.Provides;
import kronos.comkronoscodecomandroid.activity.constants.Constants;
import kronos.comkronoscodecomandroid.activity.prefs.PersistentStore;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

@Module
public class ApiModule {

    @Provides
    public GuidesService provideGuidesService(PersistentStore persistentStore) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(persistentStore.get(PersistentStore.API_URL, Constants.DEFAULT_API_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GuidesService.class);
    }

    @Provides
    public SettingsService settingsService(PersistentStore persistentStore) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(persistentStore.get(PersistentStore.API_URL, Constants.DEFAULT_API_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(SettingsService.class);
    }
}
