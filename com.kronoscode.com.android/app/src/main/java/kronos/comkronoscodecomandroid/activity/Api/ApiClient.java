package kronos.comkronoscodecomandroid.activity.api;

import com.kronoscode.cacao.android.app.model.Guide;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;

/**
 * Created by jhon on 2/03/15.
 * This class is the main class for API connection, we will define all the interfaces here that communicate with the backend
 */
public final class ApiClient {
    // API URL
    public static final String API_URL = "http://cacaomovil.com/api/v1";

    private static cacaoApiInterface sCacaoApiInterface;

    public static cacaoApiInterface getCacaoApiInterface() {
        if (sCacaoApiInterface == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(API_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();

            sCacaoApiInterface = restAdapter.create(cacaoApiInterface.class);
        }

        return sCacaoApiInterface;
    }

    /**
     * This interface will handle all the events with the API
     */
    public interface cacaoApiInterface {

        // Get guides
        @GET("/guides/?format=json")
        void getGuides (Callback<List<Guide>> callBack);

    }
}
