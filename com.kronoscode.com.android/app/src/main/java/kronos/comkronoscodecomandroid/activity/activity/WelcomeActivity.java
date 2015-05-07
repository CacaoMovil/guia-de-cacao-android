package kronos.comkronoscodecomandroid.activity.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import kronos.comkronoscodecomandroid.R;

public class WelcomeActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        Button btnList = (Button) findViewById(R.id.btn_go_to_list);
        Button btnAbout = (Button) findViewById(R.id.btn_go_about);
        Button btnSdCard = (Button) findViewById(R.id.btn_load_sdcard);

        btnAbout.setOnClickListener(this);
        btnList.setOnClickListener(this);
        btnSdCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.btn_go_to_list:
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("value", "online");
                break;

            case R.id.btn_go_about:
                intent = new Intent(this, aboutActivity.class);
                break;

            case R.id.btn_load_sdcard:
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("value", "sdcard");
                break;
        }

        startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
