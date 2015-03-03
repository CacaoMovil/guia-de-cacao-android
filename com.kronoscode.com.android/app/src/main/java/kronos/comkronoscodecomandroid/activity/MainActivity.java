package kronos.comkronoscodecomandroid.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import kronos.comkronoscodecomandroid.R;
import kronos.comkronoscodecomandroid.activity.api.ApiClient;
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


        // Testing
        setProgressBarIndeterminateVisibility(Boolean.TRUE);

        ApiClient.getCacaoApiInterface().getGuides(new Callback<VersionObject>() {
            @Override
            public void success(VersionObject result, Response response) {
                if (response.getStatus() == 200) {
                    Utils.toastMessage(getBaseContext(), result.getmDate());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                setProgressBarIndeterminateVisibility(Boolean.FALSE);
                Utils.toastMessage(getBaseContext(), error.getMessage());
            }
        });
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
}
