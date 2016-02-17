package kronos.comkronoscodecomandroid.activity.utils;

import android.app.Application;
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

import kronos.comkronoscodecomandroid.activity.constants.Constants;


/**
 * Created by jhon on 2/3/15.
 */
@Singleton
public class GuideUtils {

    @Inject
    FolderUtil folderUtil;

    @Inject
    Application application;

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

        Cursor cursor = application.getContentResolver().query(CacaoProvider.GUIDEVERSION_CONTENT_URI, null, GuideVersionTable._ID, null,
                GuideVersionTable.NUM_VERSION + " DESC");

        int position = 0;
        //flag to check that we already have a downloaded guide on the list
        boolean thereIsAGuide = false;

        do {
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    String childName = cursor.getString(cursor.getColumnIndex(GuideVersionTable.NAME));
                    String fileName = cursor.getString(cursor.getColumnIndex(GuideVersionTable.FILE));

                    if (groupName.equals(childName)) {
                        boolean theGuideExists = folderUtil.checkIfFolderExist(Constants.UNZIP_DIR + folderUtil.getNameFromPath(fileName) + "index.html");
                        if (position == 0) {
                            versions.add(new GuideVersion(cursor, false));
                            thereIsAGuide = theGuideExists;
                        } else if (theGuideExists && !thereIsAGuide) {
                            versions.add(new GuideVersion(cursor, false));
                            thereIsAGuide = true;
                        }
                        position = position  + 1;
                    }

                } while (cursor.moveToNext());
            }
        } while (cursor != null && cursor.moveToNext());

        return versions;
    }

}
