package kronos.comkronoscodecomandroid.activity.api;


import kronos.comkronoscodecomandroid.activity.object.Setting;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Headers;

/**
 * Created by Jonathan Chavarria
 */
public interface SettingsService {

    @Headers({
            "Content-type: application/json"
    })
    @GET("settings")
    Call<Setting> getSettings();
}
