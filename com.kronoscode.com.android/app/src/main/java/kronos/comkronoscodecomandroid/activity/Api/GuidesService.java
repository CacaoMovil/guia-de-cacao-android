package kronos.comkronoscodecomandroid.activity.api;


import com.kronoscode.cacao.android.app.model.Guide;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Headers;

/**
 * Created by Jonathan Chavarria
 */
public interface GuidesService {

    @Headers({"Content-type: application/json"})
    @GET("guides")
    Call<List<Guide>> getGuides();
}
