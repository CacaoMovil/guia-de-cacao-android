package kronos.comkronoscodecomandroid.activity.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import kronos.comkronoscodecomandroid.R;

public class WelcomeActivity extends Activity implements View.OnClickListener {

    private static final int REQUEST_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        Button btnList = (Button) findViewById(R.id.btn_go_to_list);
        Button btnAbout = (Button) findViewById(R.id.btn_go_about);
        Button btnSdCard = (Button) findViewById(R.id.btn_go_sdcard);

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

            case R.id.btn_go_sdcard:
                intent = new Intent(this, FilesProviderActivity.class);
                break;
        }

        startActivityForResult(intent, REQUEST_ID);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ID) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a file.
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
