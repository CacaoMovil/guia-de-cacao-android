package kronos.comkronoscodecomandroid.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import kronos.comkronoscodecomandroid.R;
import kronos.comkronoscodecomandroid.activity.api.ApiClient;
import kronos.comkronoscodecomandroid.activity.object.GuideObject;
import kronos.comkronoscodecomandroid.activity.object.VersionObject;
import kronos.comkronoscodecomandroid.activity.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends CacaoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setProgressBarIndeterminateVisibility(Boolean.TRUE);
        getRemoteData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * This function will request data from the server
     */
    public void getRemoteData() {

        ApiClient.getCacaoApiInterface().getGuides(new Callback<List<GuideObject>>() {
            @Override
            public void success(List<GuideObject> guideObjects, Response response) {
                if (response.getStatus() == 200) {
                    postData(guideObjects);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                setProgressBarIndeterminateVisibility(Boolean.FALSE);
                Utils.toastMessage(getBaseContext(), error.getMessage());
            }
        });
    }

    /**
     * Data from backend, let's fill the listview !
     * @param guides
     */
    public void postData(List<GuideObject> guides) {
        if (guides.size() > 0) {
            for (GuideObject guide : guides) {
                for (VersionObject version: guide.getmVersions()) {
                    Utils.toastMessage(this, version.getmName());
                }
            }
        }
    }
}
