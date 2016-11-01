package kronos.comkronoscodecomandroid.activity.utils;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.kronoscode.cacao.android.app.database.table.GuideVersionTable;
import com.kronoscode.cacao.android.app.model.GuideVersion;
import com.kronoscode.cacao.android.app.provider.CacaoProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import kronos.comkronoscodecomandroid.R;
import kronos.comkronoscodecomandroid.activity.constants.Constants;
import kronos.comkronoscodecomandroid.activity.prefs.PersistentStore;


/**
 * Created by jhon on 2/3/15.
 */
@Singleton
public class GuideUtils {

    @Inject
    FolderUtil folderUtil;

    @Inject
    ContentResolver contentResolver;

    @Inject
    PersistentStore persistentStore;

    @Inject
    public GuideUtils() {
        /// nothing
    }

    /**
     * @param groupName
     * @return
     */
    public List<GuideVersion> getVersionsFromGuide(String groupName) {
        List<GuideVersion> versions = new ArrayList<>();

        Cursor cursor = contentResolver.query(CacaoProvider.GUIDEVERSION_CONTENT_URI, null, GuideVersionTable._ID, null, GuideVersionTable.NUM_VERSION + " DESC");

        int position = 0;
        do {
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    String childName = cursor.getString(cursor.getColumnIndex(GuideVersionTable.NAME));
                    if (groupName.equals(childName)) {
                        versions.add(new GuideVersion(cursor, false));
                        position = position  + 1;
                    }

                } while (cursor.moveToNext());
            }
        } while (cursor != null && cursor.moveToNext());

        return versions;
    }

    /**
     * check if there is an update
     * @param groupName
     * @param version
     * @return
     */
    public Boolean isAnUpdate(String groupName , int version) {
        int lastDownload = (version - 1);
        Cursor cursor = contentResolver.query(CacaoProvider.GUIDEVERSION_CONTENT_URI, null, GuideVersionTable.NAME + "=? AND " + GuideVersionTable.NUM_VERSION + "=?", new String[]{String.valueOf(groupName), String.valueOf(lastDownload)}, GuideVersionTable.NUM_VERSION + " DESC");

        do {
            if (cursor != null && cursor.moveToFirst()) {
                String file = cursor.getString(cursor.getColumnIndex(GuideVersionTable.FILE));
                if (folderUtil.checkIfFolderExist(persistentStore.getFolderName() +  folderUtil.getNameFromPath(file))) {
                    return true;
                }
            }
        } while (cursor != null && cursor.moveToNext());

        return false;
    }

    public String returnCurrentAvailableGuide(String groupName , int version) {
        int lastDownload = (version - 1);
        Cursor cursor = contentResolver.query(CacaoProvider.GUIDEVERSION_CONTENT_URI, null, GuideVersionTable.NAME + "=? AND " + GuideVersionTable.NUM_VERSION + "=?", new String[]{String.valueOf(groupName), String.valueOf(lastDownload)}, GuideVersionTable.NUM_VERSION + " DESC");

        do {
            if (cursor != null && cursor.moveToFirst()) {
                return folderUtil.getNameFromPath(cursor.getString(cursor.getColumnIndex(GuideVersionTable.FILE)));
            }
        } while (cursor != null && cursor.moveToNext());

        return null;
    }

}
