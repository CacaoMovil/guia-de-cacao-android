package kronos.comkronoscodecomandroid.activity.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import com.kronoscode.cacao.android.app.database.table.GuideVersionTable;
import com.kronoscode.cacao.android.app.model.GuideVersion;
import com.kronoscode.cacao.android.app.provider.CacaoProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by jhon on 2/3/15.
 */
public class Utils {

    public static final String FILE_MANAGER_PLAYSTORE  = "com.estrongs.android.pop&hl=en";
    public static final String DOMAIN = "http://cacao.kronoscode.com";
    public static final String ZIP_DIR = Environment.getExternalStorageDirectory().getPath() + "/zip_guias/";
    public static final String UNZIP_DIR = Environment.getExternalStorageDirectory().getPath() + "/guias/";
    public static final String LINK_SPLIT = "descargas/";

    /**
     * Display toast message
     * @param context
     * @param msg
     */
    public static void toastMessage(Context context, String msg) {
        final Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 5000);
    }

    /**
     * This function will clean all the tables
     * @param context
     */
    public static void CleanTablesLocalTables(Activity context) {
        context.getContentResolver().delete(CacaoProvider.GUIDE_CONTENT_URI, null, null);
        context.getContentResolver().delete(CacaoProvider.GUIDEVERSION_CONTENT_URI, null, null);
    }

    /**
     * This function will check if an specific zone contains schedule
     * @param context
     * @param guideId
     * @return
     */
    public static List<GuideVersion> getVersionsFromGuide(Context context, String guideId) {
        List<GuideVersion> versions = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(CacaoProvider.GUIDEVERSION_CONTENT_URI, null, GuideVersionTable._ID, null,
                GuideVersionTable._ID + " ASC");
        do {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String groupName = cursor.getString(cursor.getColumnIndex(GuideVersionTable.NAME));
                    if (groupName.equals(guideId)) {
                        versions.add(new GuideVersion(cursor, false));
                    }
                } while (cursor.moveToNext());
            }
        } while (cursor != null ? cursor.moveToNext() : false);

        return versions;
    }

    /**
     * Cleaning all compress files after unzip
     */
    public static void cleanDir(String folder) {
        File dir = new File(folder);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }

    /**
     * This function will help us to identify if the file was already downloaded and exist in the phone
     * @param folderName
     * @return
     */
    public static boolean checkIfFolderExist(String folderName) {
        File folder = new File(folderName);
        return folder.exists();
    }


    /**
     * This function will give us the name of the file without the complete link
     * @param path
     * @return
     */
    public static String getNameFromPath(String path) {
        String[] zipName = path.split(LINK_SPLIT);
        String[] fileName = zipName[1].split(".zip");

        return fileName[0];
    }
}