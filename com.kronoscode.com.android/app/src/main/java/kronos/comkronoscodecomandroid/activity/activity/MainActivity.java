package kronos.comkronoscodecomandroid.activity.activity;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.kronoscode.cacao.android.app.database.table.GuideTable;
import com.kronoscode.cacao.android.app.database.table.GuideVersionTable;
import com.kronoscode.cacao.android.app.model.Guide;
import com.kronoscode.cacao.android.app.model.GuideVersion;
import com.kronoscode.cacao.android.app.provider.CacaoProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kronos.comkronoscodecomandroid.R;
import kronos.comkronoscodecomandroid.activity.adapter.GuideAdapter;
import kronos.comkronoscodecomandroid.activity.api.ApiClient;
import kronos.comkronoscodecomandroid.activity.object.Content;
import kronos.comkronoscodecomandroid.activity.utils.Decompress;
import kronos.comkronoscodecomandroid.activity.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ExpandableListActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        ExpandableListView.OnChildClickListener {

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private String mFileName;
    private GuideAdapter mAdapter;
    private static final int LOADER_ID = 0;
    private String mValue;
    private static final int REQUEST_ID = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();

        Bundle intent = getIntent().getExtras();

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.title));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (intent != null) {
            mValue = intent.getString("value");
            if (mValue.equals("online")) {
                // Try to update our local database
                getRemoteData();
            } else {
                mFileName = intent.getString("filename");
                new unzipFile().execute(Utils.ZIP_DIR + intent.getString("filename"));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchView searchview = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (mAdapter != null) {
                    mAdapter.getFilter().filter(newText);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (mAdapter != null) {
                    mAdapter.getFilter().filter(query);
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {

            if (Utils.isNetworkAvailable(this)) {
                getRemoteData();
            } else {
                Utils.toastMessage(this, getString(R.string.internet_not_available));
            }
        } else if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_import) {
            Intent  intent = new Intent(this, FilesProviderActivity.class);
            startActivityForResult(intent, REQUEST_ID);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        GuideVersion version = (GuideVersion) mAdapter
                .getChild(groupPosition, childPosition);

        if (Utils.checkIfFolderExist(Utils.UNZIP_DIR + Utils.getNameFromPath(version.getFile()))) {
            goToFolder(Utils.UNZIP_DIR + Utils.getNameFromPath(version.getFile()));
        } else {
            if (Utils.isNetworkAvailable(this)) {
                downloadFile(Utils.DOMAIN + version.getFile());
            } else {
                Utils.toastMessage(this, getString(R.string.internet_not_available));
            }
        }

        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading file..");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }

    /**
     * This function will request data from the server
     */
    public void getRemoteData() {

        if (Utils.isNetworkAvailable(this)) {

            setProgressBarIndeterminateVisibility(Boolean.TRUE);

            ApiClient.getCacaoApiInterface().getGuides(new Callback<List<Guide>>() {
                @Override
                public void success(List<Guide> guideObjects, Response response) {
                    if (response.getStatus() == 200) {
                        postData(guideObjects);
                    } else {
                        postData(null);
                    }
                    setProgressBarIndeterminateVisibility(Boolean.FALSE);
                }

                @Override
                public void failure(RetrofitError error) {
                    setProgressBarIndeterminateVisibility(Boolean.FALSE);
                    Utils.toastMessage(getBaseContext(), error.getMessage());
                    postData(null);
                }
            });
        } else {
            restartLoader();
        }
    }

    /**
     * Data from backend, let's fill the listview!
     *
     * @param guides
     */
    public void postData(List<Guide> guides) {

        // if data  was consumed
        if (guides != null) {
            // Cleaning tables
            Utils.cleanLocalTables(this);

            if (guides.size() > 0) {
                for (Guide guide : guides) {
                    // register guide information

                    if (guide.getmVersions().size() > 0) {
                        ContentValues values = new ContentValues();
                        values.put(GuideTable.NAME, guide.getName());
                        //values.put(GuideTable.ID, counter);

                        getContentResolver().insert(CacaoProvider.GUIDE_CONTENT_URI, values);
                        List<GuideVersion> versions = new ArrayList<>();

                        for (GuideVersion version : guide.getmVersions()) {
                            // register versions information
                            ContentValues versionValues = new ContentValues();
                            versionValues.put(GuideVersionTable.NAME, version.getName());
                            versionValues.put(GuideVersionTable.FILE, version.getFile());
                            versionValues.put(GuideVersionTable.DATE, version.getDate());
                            versionValues.put(GuideVersionTable.NUM_VERSION, version.getNumVersion());

                            getContentResolver().insert(CacaoProvider.GUIDEVERSION_CONTENT_URI, versionValues);
                            versions.add(version);
                        }
                    }
                }
            }
        }

        // read from database
        restartLoader();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(MainActivity.this, CacaoProvider.GUIDE_CONTENT_URI, null, null, null, GuideTable._ID + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            Map<String, List<GuideVersion>> hashMap = new LinkedHashMap<>();

            do {
                Guide guide = new Guide();
                guide.setName(data.getString(data.getColumnIndex(GuideTable.NAME)));
                List<GuideVersion> version;

                if (!guide.getName().equals("")) {
                    version = Utils.getVersionsFromGuide(getBaseContext(), guide.getName());
                    if (version.size() > 0) {
                        guide.setmVersions(version);
                    }

                    hashMap.put(guide.getName(), guide.getmVersions());
                    displayView(hashMap);
                }

            } while (data.moveToNext());
        } else {
            Utils.toastMessage(this, "No information saved");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * This class will download the file
     */
    private class DownloadFileAsync extends AsyncTask<String, String, String> {

        private String mFileDir;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... link) {
            int count;
            try {
                URL url = new URL(link[0]);
                URLConnection connection = url.openConnection();

                connection.connect();
                int lengthOfFile = connection.getContentLength();

                File folder = new File(Utils.ZIP_DIR);

                if (!folder.isDirectory()) {
                    folder.mkdirs();
                }

                String[] separated = link[0].split("descargas/");
                mFileDir = Utils.ZIP_DIR + separated[1];
                mFileName = separated[1];

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(Utils.ZIP_DIR + separated[1]);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                new Runnable() {
                    @Override
                    public void run() {
                        Utils.toastMessage(getBaseContext(), "Something wrong happened");
                    }
                };
            }

            return null;

        }

        protected void onProgressUpdate(String... progress) {
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
            new unzipFile().execute(mFileDir);
        }
    }

    /**
     * This class will unzip the file downloaded before and will update our local database.
     */
    private class unzipFile extends AsyncTask<String, String, String> {
        ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage(getString(R.string.unzip_msg));
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... fileRoot) {
            String zipRoot = fileRoot[0];
            try {
                String[] separated = mFileName.split(".zip");

                Decompress d = new Decompress(zipRoot, Utils.UNZIP_DIR + "/" + separated[0]);
                try {
                    d.unzip();
                } catch (IOException e) {
                    e.printStackTrace();
                    Utils.toastMessage(getBaseContext(), "Something wrong happened trying to unzip this file");
                }
            } catch (Exception e) {
                new Runnable() {
                    @Override
                    public void run() {
                        Utils.toastMessage(getBaseContext(), "Something wrong happened");
                    }
                };
            }
            return null;
        }

        @Override
        protected void onPostExecute(String unused) {
            if (mDialog != null) {
                mDialog.dismiss();

                if (mValue.equals("online")) {
                    Utils.cleanDir(Utils.ZIP_DIR + mFileName);
                    restartLoader();
                } else {
                    try {
                        parseLocalJson();
                    } catch (IOException e) {
                        Utils.toastMessage(getBaseContext(), "No se encontro archivo manifest");
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    /**
     * Go to the downloaded folder
     *
     * @param locaPath
     */
    public void goToFolder(String locaPath) {
        try {

            Intent intent = new Intent(this, GuideActivity.class);
            intent.putExtra("FILE", locaPath + "/guia/index.html");
            startActivity(intent);
            this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } catch (Exception e) {
            Utils.toastMessage(this, "You need to install a file Explorer");
            installFileManager();
        }
    }

    /**
     * This will call the download and unzip asynctask
     *
     * @param path
     */
    public void downloadFile(String path) {
        new DownloadFileAsync().execute(path);
    }

    /**
     * Create view with groups and child
     *
     * @param hashMap
     */
    public void displayView(Map<String, List<GuideVersion>> hashMap) {
        mAdapter = new GuideAdapter(this, hashMap);
        setListAdapter(mAdapter);
    }

    /**
     * if you don't have a file manager, this function will let you install a new one (this a recommendation, this link can be change)
     */
    public void installFileManager() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + Utils.FILE_MANAGER_PLAYSTORE));
        startActivity(intent);
    }

    /**
     * Load data from database
     */
    public void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }


    /**
     * this function will get the guide zip from the sdcard
     */
    private boolean getInfoFromSdCard() {
        //return Utils.checkIfFolderExist(Utils.ZIP_DIR + "guia.zip");
        return true;
    }

    /**
     * @throws IOException
     */
    private void parseLocalJson() throws IOException {

        String[] file = mFileName.split(".zip");

        BufferedReader reader = new BufferedReader(new FileReader(Utils.UNZIP_DIR + file[0] + "/guia/manifest.json"));
        String line, results = "";
        while ((line = reader.readLine()) != null) {
            results += line;
        }
        reader.close();

        JSONObject obj = null;
        try {
            obj = new JSONObject(results);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        Content content;
        content = gson.fromJson(obj.toString(), Content.class);

        int i = 1;

        for (Guide guide : content.getContents()) {

            for (GuideVersion version: guide.getmVersions()) {
                if (Integer.parseInt(content.getGuide_id()) == i) {
                    if (version.getNumVersion().equals(content.getGuide_version())) {

                        String[] separated = version.getFile().split("descargas/");
                        String[] separated2 = separated[1].split(".zip");
                        File from = new File(Utils.UNZIP_DIR + file[0]);
                        File to = new File(Utils.UNZIP_DIR +  separated2[0]);
                        from.renameTo(to);
                    }
                }
            }
            i++;
        }

        if (content.getContents().size() > 0)
            postData(content.getContents());
        else
            Utils.toastMessage(this, "No content");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ID) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a file.
                finish();
                String result = data.getStringExtra("result");
                Intent intent;
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("value", "sdcard");
                intent.putExtra("filename", result);

                startActivity(intent);
            }
        }
    }
}
