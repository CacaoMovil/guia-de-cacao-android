package kronos.comkronoscodecomandroid.activity.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import kronos.comkronoscodecomandroid.R;
import kronos.comkronoscodecomandroid.activity.App;
import kronos.comkronoscodecomandroid.activity.constants.Constants;
import kronos.comkronoscodecomandroid.activity.event.ToastEvent;
import pocketknife.BindExtra;
import pocketknife.PocketKnife;

public class GuideActivity extends BaseActivity {

    @Bind(R.id.webview)
    WebView browser;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @BindExtra(Constants.FILE)
    String filePath;

    @Inject
    EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        PocketKnife.bindExtras(this);
        App.getInjectComponent(this).inject(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            setTitle(getIntent().getStringExtra("name"));
        }

        if (getIntent().getExtras() != null) {

            File file = new File(filePath);
            if (file.exists()) {
                WebSettings webSettings = browser.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setBuiltInZoomControls(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.setSupportZoom(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    webSettings.setAllowUniversalAccessFromFileURLs(true);
                    webSettings.setAllowFileAccessFromFileURLs(true);
                }

                browser.setWebViewClient(new WebViewClient());
                browser.setWebChromeClient(new WebChromeClient());
                browser.getSettings().setBuiltInZoomControls(true);
                browser.getSettings().setDisplayZoomControls(false);

                browser.loadUrl("file://" + filePath);
            } else {
                eventBus.post(new ToastEvent(getString(R.string.no_index_found)));
            }
        }

        if (18 < Build.VERSION.SDK_INT ){
            //18 = JellyBean MR2, KITKAT=19
            browser.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
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
        menu.findItem(R.id.action_search).setVisible(false);
        //searchview.setVisibility(View.GONE);
        //menu.se

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent returnIntent = new Intent(getApplicationContext(), MainActivity.class);
                returnIntent.putExtra(Constants.QUERY, query);
                setResult(RESULT_OK, returnIntent);
                startActivity(returnIntent);
                return true;
            }
        });

        return true;
    }
}
