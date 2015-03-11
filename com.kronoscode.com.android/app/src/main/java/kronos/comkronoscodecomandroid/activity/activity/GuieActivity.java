package kronos.comkronoscodecomandroid.activity.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.File;

import kronos.comkronoscodecomandroid.R;
import kronos.comkronoscodecomandroid.activity.utils.Utils;

public class GuieActivity extends Activity {

    private WebView mBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guie);

        ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.title));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle intent = getIntent().getExtras();

        if (intent!=null) {
            String path = intent.getString("FILE");

            File file = new File(path);
            if (file.exists()) {
                mBrowser = (WebView) findViewById(R.id.webview);
                WebSettings webSettings = mBrowser.getSettings();
                webSettings.setJavaScriptEnabled(true);
                mBrowser.loadUrl("file://" + path);
            } else {
                Utils.toastMessage(this, "Index file does not exist in this folder");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_guie, menu);
        return true;
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
}
