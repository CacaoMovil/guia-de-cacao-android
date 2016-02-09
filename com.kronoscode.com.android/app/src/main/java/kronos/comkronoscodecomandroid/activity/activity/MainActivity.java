package kronos.comkronoscodecomandroid.activity.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.TextView;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import kronos.comkronoscodecomandroid.R;
import kronos.comkronoscodecomandroid.activity.api.ApiClient;
import kronos.comkronoscodecomandroid.activity.adapter.GuideAdapter;
import kronos.comkronoscodecomandroid.activity.object.Content;
import kronos.comkronoscodecomandroid.activity.utils.Decompress;
import kronos.comkronoscodecomandroid.activity.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ExpandableListActivity implements LoaderManager.LoaderCallbacks<Cursor>, ExpandableListView.OnChildClickListener {

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private static final int REQUEST_ID = 1;
    private static final int LOADER_ID = 0;

    @Bind(R.id.empty)
    TextView mEmpty;

    private ProgressDialog mProgressDialog;
    private String mFileName;
    private GuideAdapter mAdapter;
    private String mValue;
    private int mCurrentGroup = -1;
    private SearchView mSearchView;
    private String mSearchQuery = "";
    private DownloadFileAsync task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final ExpandableListView listView = this.getExpandableListView();
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (parent.isGroupExpanded(groupPosition)) {
                    parent.collapseGroup(groupPosition);
                } else {
                    //Chicle para obtener la ultima visible
                    int lastVis = parent.getLastVisiblePosition();
                    long packedPosition = parent.getExpandableListPosition(lastVis);
                    int realLastVis = ExpandableListView.getPackedPositionGroup(packedPosition);
                    parent.expandGroup(groupPosition, true);
                    if (groupPosition == realLastVis)
                        parent.setSelection(groupPosition);
                }
                return true;
            }
        });

        ActionBar actionBar = getActionBar();

        Bundle intent = getIntent().getExtras();

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.lista_guias));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (intent != null) {
            mValue = intent.getString("value", "");
            final String query = intent.getString("query", "");
            if (mValue.equals("online")) {
                // Try to update our local database
                getRemoteData();
            } else if (!query.equals("")) {
                mSearchQuery = query;
                getRemoteData();
            } else {
                mFileName = intent.getString("filename");
                new unzipFile().execute(intent.getString("filename"));
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
            Intent intent = new Intent(this, FilesProviderActivity.class);
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
                mCurrentGroup = groupPosition;
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
                mProgressDialog = new ProgressDialog(MainActivity.this) {
                    @Override
                    public void onBackPressed() {
                        cancelDownload();
                    }
                };
                mProgressDialog.setProgress(0);
                mProgressDialog.setMessage(getString(R.string.downloading_text));
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        removeDialog(DIALOG_DOWNLOAD_PROGRESS);
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
                        values.put(GuideTable.TAGS, guide.getTags());
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
                            versionValues.put(GuideVersionTable.TAGS, version.getTags());

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
                }

            } while (data.moveToNext());

            displayView(hashMap);

        } else {
            Utils.toastMessage(this, getString(R.string.not_information_saved));
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
        private boolean failed = false;
        private String finalLink;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mProgressDialog.setProgress(0);
            mProgressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... link) {
            int count;
            InputStream input;
            OutputStream output;
            HttpURLConnection connection = null;
            try {
                finalLink = link[0];
                URL url = new URL(link[0]);
                System.setProperty("http.keepAlive", "false");
                connection = (HttpURLConnection) url.openConnection();
                connection.setUseCaches(false);
                connection.setConnectTimeout(5000); //set timeout to 5 seconds
                connection.setDoInput(true);

                int lengthOfFile = connection.getContentLength();

                File folder = new File(Utils.ZIP_DIR);

                if (!folder.isDirectory()) {
                    folder.mkdirs();
                }

                String[] separated = link[0].split("descargas/");
                mFileDir = Utils.ZIP_DIR + separated[1];
                mFileName = separated[1];

                input = new BufferedInputStream(url.openStream());
                output = new FileOutputStream(Utils.ZIP_DIR + separated[1]);

                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {

                    if(isCancelled()) {
                        publishProgress();
                        break;
                    }

                    total += count;
                    if (lengthOfFile > 0)
                        publishProgress("" + (int) ((total * 100) / lengthOfFile));

                    output.write(data, 0, count);
                }

            } catch (Exception e) {
                Log.d("CACAODEBUG", e.getMessage());
                failed = true;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            //dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
            mProgressDialog.hide();
            if (!failed) {
                new unzipFile().execute(mFileDir);
            } else {
                mProgressDialog.setProgress(0);
                mProgressDialog.dismiss();
                Utils.cleanDir(mFileDir);
                reconnectConnection(finalLink);
            }
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
                Decompress d = new Decompress(zipRoot, Utils.UNZIP_DIR);
                try {
                    d.unzip();
                } catch (IOException e) {
                    e.printStackTrace();
                    Utils.toastMessage(getBaseContext(), getString(R.string.error_unziping));
                }
            } catch (Exception e) {
                new Runnable() {
                    @Override
                    public void run() {
                        Utils.toastMessage(getBaseContext(), getString(R.string.error));
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
                        Utils.toastMessage(getBaseContext(), getString(R.string.not_manifest_found));
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    /**
     * Go to the downloaded folder
     *
     * @param localPath
     */
    public void goToFolder(String localPath) {
        try {
            Intent intent = new Intent(this, GuideActivity.class);
            String oldIndexPath = localPath + "/guia/index.html";
            if (Utils.checkIfFolderExist(oldIndexPath)) {
                intent.putExtra("FILE", localPath + "/guia/index.html");
            } else {
                intent.putExtra("FILE", localPath + "/index.html");
            }
            startActivity(intent);
            this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } catch (Exception e) {
            Utils.toastMessage(this, getString(R.string.file_explorer_file));
            installFileManager();
        }
    }

    /**
     * This will call the download and unzip asynctask
     *
     * @param path
     */
    public void downloadFile(String path) {
        task = new DownloadFileAsync();
        task.execute(path);
    }

    /**
     * Create view with groups and child
     *
     * @param hashMap
     */
    public void displayView(Map<String, List<GuideVersion>> hashMap) {
        mAdapter = new GuideAdapter(this, hashMap, getExpandableListView(), mEmpty);
        setListAdapter(mAdapter);
        if (mCurrentGroup != -1) {
            ExpandableListView listView = this.getExpandableListView();
            listView.expandGroup(mCurrentGroup);
            listView.setSelectionFromTop(mCurrentGroup, 1);
            mCurrentGroup = -1;
        }

        if (!mSearchQuery.equals("")) {
            mSearchView.setQuery(mSearchQuery, true);
            mSearchQuery = "";
        }
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
     * @throws IOException
     */
    private void parseLocalJson() throws IOException {

        String fileName;
        String[] file = mFileName.split(".zip");
        if (file[0].contains("/")) {
            String[] paths = file[0].split("/");
            fileName = paths[paths.length - 1];
        } else {
            fileName = file[0];
        }

        BufferedReader reader = new BufferedReader(new FileReader(Utils.UNZIP_DIR + fileName + "/manifest.json"));
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

            for (GuideVersion version : guide.getmVersions()) {
                if (Integer.parseInt(content.getGuide_id()) == i) {
                    if (version.getNumVersion().equals(content.getGuide_version())) {

                        String[] separated = version.getFile().split("descargas/");
                        String[] separated2 = separated[1].split(".zip");
                        File from = new File(Utils.UNZIP_DIR + file[0]);
                        File to = new File(Utils.UNZIP_DIR + separated2[0]);
                        from.renameTo(to);
                    }
                }
            }
            i++;
        }

        if (content.getContents().size() > 0)
            postData(content.getContents());
        else
            Utils.toastMessage(this, getString(R.string.not_content));
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

    /**
     * Le'ts ask if we really want to cancel the current download.
     */
    private void cancelDownload() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.cancel_download_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        mProgressDialog.cancel();
                        task.cancel(true);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void reconnectConnection(final String url) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.error_download_retry)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        downloadFile(url);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
