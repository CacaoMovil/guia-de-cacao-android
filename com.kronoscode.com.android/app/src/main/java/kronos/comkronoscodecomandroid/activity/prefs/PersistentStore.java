package kronos.comkronoscodecomandroid.activity.prefs;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * Created by jhon on 15/02/16.
 */
@Singleton
public class PersistentStore {

    @Inject
    SharedPreferences preferences;

    private static final String synchronizer = "synchronizer";
    public static final String TITLE_CACACO = "title_cacao";
    public static final String WELCOME_CACAO = "welcome_cacao";
    public static final String LOGO_CACAO = "logo_cacao";
    public static final String LAST_UPDATE = "last_update";
    public static final String API_URL = "api";

    @Inject
    public PersistentStore() {
        //Purposefully left blank
    }

    private Editor getSharedPreferencesEditor() {
        return preferences.edit();
    }

    public void set(String key, ArrayList<String> value) {
        JSONArray json = new JSONArray(value);

        synchronized (synchronizer) {
            getSharedPreferencesEditor().putString(key, json.toString()).commit();
        }
    }

    public ArrayList<String> get(String key, ArrayList<String> defaultValue) {
        String jsonString = preferences.getString(key, "");
        try {
            JSONArray json = new JSONArray(jsonString);

            ArrayList<String> result = new ArrayList<String>();
            for (int i = 0; i < json.length(); i++) {
                result.add(json.getString(i));
            }

            return result;
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    /*
     * Basic Get/Set Methods
     */
    public void set(String key, String value) {
        synchronized (synchronizer) {
            getSharedPreferencesEditor().putString(key, value).commit();
        }
    }

    public String get(String key, String defaultValue) {
        SharedPreferences prefs = preferences;
        return prefs.getString(key, defaultValue);
    }

    public void set(String key, int value) {
        synchronized (synchronizer) {
            getSharedPreferencesEditor().putInt(key, value).commit();
        }
    }

    public int get(String key, int defaultValue) {
        SharedPreferences prefs = preferences;
        return prefs.getInt(key, defaultValue);
    }

    public void set(String key, boolean value) {
        synchronized (synchronizer) {
            getSharedPreferencesEditor().putBoolean(key, value).commit();
        }
    }

    public boolean get(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    public void set(String key, float value) {
        synchronized (synchronizer) {
            getSharedPreferencesEditor().putFloat(key, value).commit();
        }
    }

    public float get(String key, float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }

    public void set(String key, long value) {
        synchronized (synchronizer) {
            getSharedPreferencesEditor().putLong(key, value).commit();
        }
    }

    public long get(String key, long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    public void set(String key, Date value) {
        synchronized (synchronizer) {
            getSharedPreferencesEditor().putLong(key, value.getTime()).commit();
        }
    }

    public Date get(String key, Date defaultValue) {
        return new Date(preferences.getLong(key, defaultValue.getTime()));
    }

    public void unset(String key){
        synchronized (synchronizer) {
            getSharedPreferencesEditor().remove(key).commit();
        }
    }
}