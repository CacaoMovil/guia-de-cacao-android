package kronos.comkronoscodecomandroid.activity;

import android.app.ActionBar;
import android.app.ExpandableListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Window;

import com.kronoscode.cacao.android.app.database.table.GuideTable;
import com.kronoscode.cacao.android.app.provider.CacaoProvider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kronos.comkronoscodecomandroid.R;
import kronos.comkronoscodecomandroid.activity.adapter.GuideAdapter;
import kronos.comkronoscodecomandroid.activity.api.ApiClient;
import kronos.comkronoscodecomandroid.activity.object.GuideObject;
import kronos.comkronoscodecomandroid.activity.object.VersionObject;
import kronos.comkronoscodecomandroid.activity.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ExpandableListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("Home");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getRemoteData();
    }

    /**
     * This function will request data from the server
     */
    public void getRemoteData() {
        setProgressBarIndeterminateVisibility(Boolean.TRUE);

        ApiClient.getCacaoApiInterface().getGuides(new Callback<List<GuideObject>>() {
            @Override
            public void success(List<GuideObject> guideObjects, Response response) {
                if (response.getStatus() == 200) {
                    postData(guideObjects);
                }
                setProgressBarIndeterminateVisibility(Boolean.FALSE);
            }

            @Override
            public void failure(RetrofitError error) {
                setProgressBarIndeterminateVisibility(Boolean.FALSE);
                Utils.toastMessage(getBaseContext(), error.getMessage());
            }
        });
    }

    /**
     * Data from backend, let's fill the listview!
     * @param guides
     */
    public void postData(List<GuideObject> guides) {
        Map<String, List<VersionObject>> hashMap = new LinkedHashMap<>();

        if (guides.size() > 0) {
            for (GuideObject guide : guides) {
                List<VersionObject> versions = new ArrayList<>();

                for (VersionObject version: guide.getmVersions()) {
                    versions.add(version);
                }
                hashMap.put(guide.getmName(), versions);
            }
        }

       GuideAdapter adapter = new GuideAdapter(MainActivity.this, hashMap);
       setListAdapter(adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(MainActivity.this, CacaoProvider.GUIDE_CONTENT_URI, null, null, null, GuideTable.NAME + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
