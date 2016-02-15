package kronos.comkronoscodecomandroid.activity.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kronos.comkronoscodecomandroid.R;
import kronos.comkronoscodecomandroid.activity.App;
import kronos.comkronoscodecomandroid.activity.constants.Constants;
import kronos.comkronoscodecomandroid.activity.prefs.PersistentStore;
import kronos.comkronoscodecomandroid.activity.utils.ImageUtil;

/**
 * Created by jhon on 2/03/2015.
 */
public class WelcomeActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.welcome_title)
    TextView welcomeTitle;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.logo)
    ImageView logoImageView;

    @Inject
    PersistentStore persistentStore;

    @Inject
    ImageUtil imageUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        App.getInjectComponent(this).inject(this);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        fillCacaoInfo();
    }

    @OnClick(R.id.btn_go_to_list)
    public void setGotToList() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.SEARCH_VALUE, Constants.SOURCE_ONLINE);
        startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    @OnClick(R.id.btn_go_about)
    public void setGotToAbout() {
        Intent intent = new Intent(this, aboutActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @OnClick(R.id.btn_go_tutorial)
    public void setGotToTutorial() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.youtube_video)));
        startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void fillCacaoInfo() {
        title.setText(persistentStore.get(PersistentStore.TITLE_CACACO, Constants.DEFAULT_TITLE_CACAO));
        welcomeTitle.setText(persistentStore.get(PersistentStore.WELCOME_CACAO, Constants.DEFAULT_WELCOME_CACAO));

        String logo = persistentStore.get(PersistentStore.LOGO_CACAO, Constants.LOGO_DEFAULT);

        imageUtil.loadImage(logoImageView, logo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_update_api) {
            changeApiPopUp();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeApiPopUp() {
       final  AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.title));
        alert.setMessage(getString(R.string.current_api));

        final EditText input = new EditText(this);
        input.setText(persistentStore.get(PersistentStore.API_URL, Constants.DEFAULT_API_URL));
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                persistentStore.set(PersistentStore.API_URL, input.getText().toString());
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }
}
