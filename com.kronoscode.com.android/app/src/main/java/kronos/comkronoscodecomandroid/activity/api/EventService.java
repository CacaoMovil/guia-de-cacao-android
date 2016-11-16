
package kronos.comkronoscodecomandroid.activity.api;


import java.util.List;

import kronos.comkronoscodecomandroid.activity.object.EventObject;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;

/**
 * Created by Jonathan Chavarria
 */
public interface EventService {

    @Headers({"Content-type: application/json"})
    @GET("events/{country}")
    Call<List<EventObject>> getEvents(@Path("country") String country);
}
