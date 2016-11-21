package kronos.comkronoscodecomandroid.activity.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.api.GoogleApiClient;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import kronos.comkronoscodecomandroid.R;
import kronos.comkronoscodecomandroid.activity.App;
import kronos.comkronoscodecomandroid.activity.adapter.GuideAdapter;
import kronos.comkronoscodecomandroid.activity.api.GuidesService;
import kronos.comkronoscodecomandroid.activity.api.SettingsService;
import kronos.comkronoscodecomandroid.activity.constants.Constants;
import kronos.comkronoscodecomandroid.activity.event.ToastEvent;
import kronos.comkronoscodecomandroid.activity.object.Content;
import kronos.comkronoscodecomandroid.activity.prefs.PersistentStore;
import kronos.comkronoscodecomandroid.activity.utils.DatabaseUtil;
import kronos.comkronoscodecomandroid.activity.utils.Decompress;
import kronos.comkronoscodecomandroid.activity.utils.FolderUtil;
import kronos.comkronoscodecomandroid.activity.utils.GuideUtils;
import kronos.comkronoscodecomandroid.activity.utils.NetworkUtil;
import pocketknife.BindExtra;
import pocketknife.NotRequired;
import pocketknife.PocketKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by jhon on 2/03/2015.
 */
public class MainActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>, ExpandableListView.OnChildClickListener {

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    public int MY_PERMISSION_THING = 0;
    private static final int REQUEST_ID = 1;
    private static final int LOADER_ID = 0;
    private ProgressDialog progressDialog;
    private String filename;
    private GuideAdapter adapter;
    private SearchView searchView;
    private DownloadFileAsync task;

    @Bind(R.id.empty)
    TextView empty;

    @Bind(R.id.list)
    ExpandableListView guidesList;

    @Bind(R.id.empty_container)
    ViewGroup emptyContainer;

    @Bind(R.id.list_container)
    ViewGroup listContainer;

    @NotRequired
    @BindExtra(Constants.SEARCH_VALUE)
    String searchValue;

    @NotRequired
    @BindExtra(Constants.QUERY)
    String query;

    @NotRequired
    @BindExtra(Constants.FILENAME)
    String file;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    GuidesService guidesService;

    @Inject
    EventBus eventBus;

    @Inject
    FolderUtil folderUtil;

    @Inject
    DatabaseUtil databaseUtil;

    @Inject
    GuideUtils guideUtils;

    @Inject
    NetworkUtil networkUtil;

    @Inject
    SettingsService settingsService2;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.getInjectComponent(this).inject(this);
        ButterKnife.bind(this);
        PocketKnife.bindExtras(this);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        setTitle(getString(R.string.guide_list));

        listViewAction();

        if (searchValue != null) {
            if (searchValue.equals(Constants.SOURCE_ONLINE)) {
                // Try to update our local database
                getRemoteData();
            } else if (searchValue.equals(Constants.SOURCE_SDCARD)) {
                filename = file;
                new unzipFile().execute(file);
            }
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapter != null) {
                    adapter.getFilter().filter(query);
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            if (networkUtil.isNetworkAvailable()) {
                getRemoteData();
            } else {
                eventBus.post(new ToastEvent(getString(R.string.internet_not_available)));
            }
        } else if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_import) {
            Intent intent = new Intent(this, FilesProviderActivity.class);
            startActivityForResult(intent, REQUEST_ID);
        } else if (id == R.id.action_update_api) {
            changeApiPopUp();
        } else if (id == R.id.action_update_folder) {
            changeFolderName();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        // nothing
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                progressDialog = new ProgressDialog(MainActivity.this) {
                    @Override
                    public void onBackPressed() {
                        cancelDownloadDialog();
                    }
                };
                progressDialog.setProgress(0);
                progressDialog.setMessage(getString(R.string.downloading_text));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
                return progressDialog;
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
        if (!networkUtil.isNetworkAvailable()) {
            restartLoader();
            showProgress(false);
            return;
        }

        showProgress(true);

        Call<List<Guide>> call = guidesService.getGuides();

        call.enqueue(new Callback<List<Guide>>() {

            @Override
            public void onResponse(Response<List<Guide>> response, Retrofit retrofit) {
                if (response.body() != null) {
                    postData(response.body());
                } else {
                    postData(null);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                eventBus.post(new ToastEvent(t.getMessage()));
                showProgress(false);
            }
        });

        restartLoader();
    }

    /**
     * Data from backend, let's fill the listview!
     *
     * @param guides
     */
    public void postData(List<Guide> guides) {
        showProgress(false);
        // if data  was consumed
        if (guides != null) {
            // Cleaning tables
            databaseUtil.cleanLocalTables();

            if (guides.size() > 0) {
                for (Guide guide : guides) {
                    // register guide information

                    if (guide.getmVersions().size() > 0) {
                        ContentValues values = new ContentValues();
                        values.put(GuideTable.NAME, guide.getName());
                        values.put(GuideTable.TAGS, guide.getTags());
                        //values.put(GuideTable.ID, counter);

                        getContentResolver().insert(CacaoProvider.GUIDE_CONTENT_URI, values);

                        for (GuideVersion version : guide.getmVersions()) {
                            // register versions information
                            ContentValues versionValues = new ContentValues();
                            versionValues.put(GuideVersionTable.NAME, version.getName());
                            versionValues.put(GuideVersionTable.FILE, version.getFile());
                            versionValues.put(GuideVersionTable.DATE, version.getDate());
                            versionValues.put(GuideVersionTable.NUM_VERSION, version.getNumVersion());
                            versionValues.put(GuideVersionTable.TAGS, version.getTags());

                            getContentResolver().insert(CacaoProvider.GUIDEVERSION_CONTENT_URI, versionValues);
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
                    version = guideUtils.getVersionsFromGuide(guide.getName());
                    if (version.size() > 0) {
                        guide.setmVersions(version);
                    }

                    hashMap.put(guide.getName(), guide.getmVersions());
                }

            } while (data.moveToNext());

            displayView(hashMap);

        } else {
            eventBus.post(new ToastEvent(getString(R.string.not_information_saved)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    /**
     * This class will download the file
     */

    private class DownloadFileAsync extends AsyncTask<String, String, String> {
        private String fileDir;
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
            progressDialog.setProgress(0);
            progressDialog.dismiss();
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

                //CHeck for permissions on android M
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {


                        } else {

                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSION_THING);

                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    }
                }

                int lengthOfFile = connection.getContentLength();

                File folder = new File(Constants.ZIP_DIR);

                if (!folder.isDirectory()) {
                    folder.mkdirs();
                }

                String[] separated = link[0].split("descargas/");
                fileDir = Constants.ZIP_DIR + separated[1];
                filename = separated[1];

                input = new BufferedInputStream(url.openStream());
                output = new FileOutputStream(Constants.ZIP_DIR + separated[1]);

                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {

                    if (isCancelled()) {
                        publishProgress();
                        break;
                    }

                    total += count;
                    if (lengthOfFile > 0)
                        publishProgress("" + (int) ((total * 100) / lengthOfFile));

                    output.write(data, 0, count);
                }

            } catch (Exception e) {
                Log.d("CACAO DEBUG", e.getMessage());
                failed = true;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            progressDialog.hide();
            if (!failed) {
                new unzipFile().execute(fileDir);
            } else {
                progressDialog.setProgress(0);
                progressDialog.dismiss();
                folderUtil.cleanDir(fileDir);
                downloadGuideDialog(finalLink, getString(R.string.error_download_retry), getString(R.string.yes));
            }
        }
    }

    /**
     * This class will unzip the file downloaded before and will update our local database.
     */
    private class unzipFile extends AsyncTask<String, String, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage(getString(R.string.unzip_msg));
            dialog.show();
        }

        @Override
        protected String doInBackground(String... fileRoot) {
            String zipRoot = fileRoot[0];
            try {
                Decompress d = new Decompress(zipRoot, persistentStore.getFolderName());
                try {
                    d.unzip();
                } catch (IOException e) {
                    e.printStackTrace();
                    eventBus.post(new ToastEvent(getString(R.string.error_on_unzip)));
                }
            } catch (Exception e) {
                new Runnable() {
                    @Override
                    public void run() {
                        eventBus.post(new ToastEvent(getString(R.string.error)));
                    }
                };
            }
            return null;
        }

        @Override
        protected void onPostExecute(String unused) {
            if (dialog != null) {
                dialog.dismiss();

                if (searchValue.equals(Constants.SOURCE_ONLINE)) {
                    folderUtil.cleanDir(Constants.ZIP_DIR + filename);
                    goToFolder(persistentStore.getFolderName() + filename.split(".zip")[0], "");
                    restartLoader();
                } else {
                    try {
                        parseLocalJson();
                    } catch (IOException e) {
                        eventBus.post(new ToastEvent(getString(R.string.not_manifest_found)));
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
    public void goToFolder(String localPath, String name) {
        try {
            Intent intent = new Intent(this, GuideActivity.class);
            intent.putExtra("name", name);
            String oldIndexPath = localPath + "/guia/index.html";
            if (folderUtil.checkIfFolderExist(oldIndexPath)) {
                intent.putExtra(Constants.FILE, localPath + "/guia/index.html");
            } else {
                intent.putExtra(Constants.FILE, localPath + "/index.html");
            }
            startActivity(intent);
            this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } catch (Exception e) {
            eventBus.post(new ToastEvent(getString(R.string.file_explorer_file)));
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
        adapter = new GuideAdapter(this, hashMap, guidesList, empty);
        guidesList.setAdapter(adapter);
        if (query != null) {
            if (!query.equals("")) {
                searchView.setQuery(query, true);
                query = "";
            }
        }
    }

    /**
     * if you don't have a file manager, this function will let you install a new one (this a recommendation, this link can be change)
     */
    public void installFileManager() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + Constants.FILE_MANAGER_PLAYSTORE));
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
        String[] file = filename.split(".zip");
        if (file[0].contains("/")) {
            String[] paths = file[0].split("/");
            fileName = paths[paths.length - 1];
        } else {
            fileName = file[0];
        }

        BufferedReader reader = new BufferedReader(new FileReader(persistentStore.getFolderName() + fileName + "/manifest.json"));
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
        Content content = null;

        if (obj != null) {
            content = gson.fromJson(obj.toString(), Content.class);
        }

        int i = 1;

        if (content != null) {
            for (Guide guide : content.getContents()) {
                for (GuideVersion version : guide.getmVersions()) {
                    if (Integer.parseInt(content.getGuide_id()) == i) {
                        if (version.getNumVersion().equals(content.getGuide_version())) {

                            String[] separated = version.getFile().split("descargas/");
                            String[] separated2 = separated[1].split(".zip");
                            File from = new File(persistentStore.getFolderName() + file[0]);
                            File to = new File(persistentStore.getFolderName() + separated2[0]);
                            from.renameTo(to);
                        }
                    }
                }
                i++;
            }
        }

        if (content != null) {
            if (content.getContents().size() > 0)
                postData(content.getContents());
            else {
                eventBus.post(new ToastEvent(getString(R.string.not_content)));
            }
        }
    }

    private void showProgress(boolean visible) {
        listContainer.setVisibility(visible ? View.GONE : View.VISIBLE);
        emptyContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ID) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a file.
                finish();
                String result = data.getStringExtra(Constants.FILE_RESULT);
                Intent intent;
                intent = new Intent(this, MainActivity.class);
                intent.putExtra(Constants.SEARCH_VALUE, Constants.SOURCE_SDCARD);
                intent.putExtra(Constants.FILENAME, result);
                startActivity(intent);
            }
        }
    }

    /**
     * Le'ts ask if we really want to cancel the current download.
     */
    private void cancelDownloadDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.cancel_download_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        progressDialog.cancel();
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

    private void downloadGuideDialog(final String url, String message, String positiveMsg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton(positiveMsg, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        downloadFile(url);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void newGuideUpdateDialog(final String url, final String guideName, final String version) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.new_guide_version_msg))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.start_download), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        downloadFile(url);
                    }
                })
                .setNegativeButton(R.string.open_current_guide, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        String currentAvailableFile = guideUtils.returnCurrentAvailableGuide(guideName, Integer.parseInt(version));
                        goToFolder(persistentStore.getFolderName() + currentAvailableFile, "");
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void listViewAction() {
        guidesList.setOnChildClickListener(this);
        guidesList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We don't need to collapse , let's just take the first row
                GuideVersion version = (GuideVersion) adapter.getChild(groupPosition, 0);

                // Order : if guide exists, if there is an update, download guide
                if (folderUtil.checkIfFolderExist(persistentStore.getFolderName() + folderUtil.getNameFromPath(version.getFile()))) {
                    goToFolder(persistentStore.getFolderName() + folderUtil.getNameFromPath(version.getFile()), version.getName());
                } else if (guideUtils.isAnUpdate(version.getName(), Integer.parseInt(version.getNumVersion()))) {
                    newGuideUpdateDialog(version.getFile(), version.getName(), version.getNumVersion());
                } else {
                    if (networkUtil.isNetworkAvailable()) {
                        downloadGuideDialog(version.getFile(), getString(R.string.download_guide_msg), getString(R.string.start_download));
                    } else {
                        eventBus.post(new ToastEvent(getString(R.string.internet_not_available)));
                    }
                }
                return true;
            }
        });
    }

    private void changeApiPopUp() {
        final android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.title));
        alert.setMessage(getString(R.string.current_api));

        final EditText input = new EditText(this);
        input.setText(persistentStore.get(PersistentStore.API_URL, Constants.DEFAULT_API_URL));
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                persistentStore.set(PersistentStore.API_URL, input.getText().toString());
                finish();
                startActivity(getIntent());
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }


    private void changeFolderName() {
        final android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.title));
        alert.setMessage(getString(R.string.current_api));

        final EditText input = new EditText(this);
        input.setText(persistentStore.get(PersistentStore.FOLDER_NAME, Constants.UNZIP_DEFAULT_DIR));
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                persistentStore.set(PersistentStore.FOLDER_NAME, input.getText().toString());
                finish();
                startActivity(getIntent());
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
