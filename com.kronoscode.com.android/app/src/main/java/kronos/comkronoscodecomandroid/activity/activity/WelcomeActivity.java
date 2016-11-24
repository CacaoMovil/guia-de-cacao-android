package kronos.comkronoscodecomandroid.activity.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import kronos.comkronoscodecomandroid.R;
import kronos.comkronoscodecomandroid.activity.App;
import kronos.comkronoscodecomandroid.activity.api.SettingsService;
import kronos.comkronoscodecomandroid.activity.constants.Constants;
import kronos.comkronoscodecomandroid.activity.event.UpdateSettingsEvent;
import kronos.comkronoscodecomandroid.activity.object.Setting;
import kronos.comkronoscodecomandroid.activity.prefs.PersistentStore;
import kronos.comkronoscodecomandroid.activity.utils.ImageUtil;
import kronos.comkronoscodecomandroid.activity.utils.NetworkUtil;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by jhon on 2/03/2015.
 */
public class WelcomeActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.welcome_title)
    TextView welcomeTitle;

    @Bind(R.id.logo)
    ImageView logoImageView;

    @Inject
    PersistentStore persistentStore;

    @Inject
    ImageUtil imageUtil;

    @Inject
    SettingsService settingsService;

    @Inject
    EventBus bus;

    @Inject
    NetworkUtil networkUtil;

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        App.getInjectComponent(this).inject(this);
        ButterKnife.bind(this);


        getSettings();
    }

    @OnClick(R.id.btn_go_to_list)
    public void setGotToList() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.SEARCH_VALUE, Constants.SOURCE_ONLINE);
        startActivity(intent);
        finish();
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

    @Subscribe
    public void onUpdateSettingsEvent(UpdateSettingsEvent event) {
        fillCacaoInfo();
    }

    public void getSettings() {

        if (!networkUtil.isNetworkAvailable()) {
            //bus.post(new UpdateSettingsEvent());
            fillCacaoInfo();
            return;
        }

        Call<Setting> call = settingsService.getSettings();

        call.enqueue(new Callback<Setting>() {

            @Override
            public void onResponse(Response<Setting> response, Retrofit retrofit) {
                if (response.body() != null) {
                    persistentStore.set(PersistentStore.TITLE_CACACO, response.body().getTitle());
                    persistentStore.set(PersistentStore.WELCOME_CACAO, response.body().getWelcome_title());
                    persistentStore.set(PersistentStore.LOGO_CACAO, response.body().getLogo());
                    // Register last update
                    Date currentDate = new Date(System.currentTimeMillis());
                    persistentStore.set(PersistentStore.LAST_UPDATE, currentDate.getTime());

                    bus.post(new UpdateSettingsEvent());
                }

            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        //Intent intent = new Intent(this, WelcomeActivity.class);
        //startActivity(intent);
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finishAffinity();
    }
}
