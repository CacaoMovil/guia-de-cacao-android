package kronos.comkronoscodecomandroid.activity.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SearchView;

import java.io.File;

import kronos.comkronoscodecomandroid.R;
import kronos.comkronoscodecomandroid.activity.adapter.GuideAdapter;
import kronos.comkronoscodecomandroid.activity.utils.Utils;

public class GuideActivity extends Activity {

    private WebView mBrowser;
    private GuideAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.title));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        }

        Bundle intent = getIntent().getExtras();

        if (intent!=null) {
            String path = intent.getString("FILE");

            File file = new File(path);
            if (file.exists()) {
                mBrowser = (WebView) findViewById(R.id.webview);
                WebSettings webSettings = mBrowser.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setBuiltInZoomControls(true);
                webSettings.setDomStorageEnabled(true);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    webSettings.setAllowUniversalAccessFromFileURLs(true);
                    webSettings.setAllowFileAccessFromFileURLs(true);
                }

                mBrowser.setWebViewClient(new WebViewClient());
                mBrowser.setWebChromeClient(new WebChromeClient());

                mBrowser.loadUrl("file://" + path);
            } else {
                Utils.toastMessage(this, "Index file does not exist in this folder");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
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
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent returnIntent = new Intent(getApplicationContext(), MainActivity.class);
                returnIntent.putExtra("query", query);
                setResult(RESULT_OK, returnIntent);
                startActivity(returnIntent);

                return true;
            }
        });

        return true;
    }
}
