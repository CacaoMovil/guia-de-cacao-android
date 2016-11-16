package kronos.comkronoscodecomandroid.activity.utils;

import android.content.ContentResolver;

import com.kronoscode.cacao.android.app.provider.CacaoProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by jhon on 16/2/16.
 */
@Singleton
public class DatabaseUtil {

    @Inject
    ContentResolver contentResolver;

    @Inject
    public DatabaseUtil() {

    }

    /**
     * This function will clean all the tables
     */
    public void cleanLocalTables() {
        contentResolver.delete(CacaoProvider.GUIDE_CONTENT_URI, null, null);
        contentResolver.delete(CacaoProvider.GUIDEVERSION_CONTENT_URI, null, null);
    }

    /**
     * This function will clean all the tables
     */
    public void cleanEventsTable() {
        contentResolver.delete(CacaoProvider.EVENT_CONTENT_URI, null, null);
    }
}
