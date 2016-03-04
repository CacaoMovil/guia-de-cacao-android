package kronos.comkronoscodecomandroid.activity.constants;

import android.os.Environment;

/**
 * Created by jhon on 13/2/16.
 */
public class Constants {
    public static final String SEARCH_VALUE = "value";
    public static final String QUERY = "query";
    public static final String FILENAME = "filename";
    public static final String FILE = "file";

    public static final String SOURCE_ONLINE = "online";
    public static final String SOURCE_SDCARD = "sdcard";

    public static final String DEFAULT_TITLE_CACAO = "Guias de cacaco";
    public static final String DEFAULT_WELCOME_CACAO = "Aprendiendo cacao";
    public static final String LOGO_DEFAULT = "";

    public static final String FILE_RESULT = "result";

    public static final String DEFAULT_API_URL = "http://cacao.kronoscode.com/api/v1/";

    public static final String FILE_MANAGER_PLAYSTORE  = "com.estrongs.android.pop&hl=en";
    public static final String ZIP_DIR = Environment.getExternalStorageDirectory().getPath() + "/zip_guias/";
    public static final String UNZIP_DIR = Environment.getExternalStorageDirectory().getPath() + "/guias/";
    public static final String LINK_SPLIT = "descargas/";

}
