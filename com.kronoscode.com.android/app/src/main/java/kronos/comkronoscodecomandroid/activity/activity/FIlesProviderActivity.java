package kronos.comkronoscodecomandroid.activity.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

import kronos.comkronoscodecomandroid.R;
import kronos.comkronoscodecomandroid.activity.utils.Utils;

public class FilesProviderActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_provider);

        ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.title_files_provider));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ArrayList<String> FilesInFolder = GetFiles(Utils.ZIP_DIR);
        ListView lv = (ListView) findViewById(R.id.filelist);

        if (GetFiles(Utils.ZIP_DIR)!=null) {
            lv.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, FilesInFolder));

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", parent.getAdapter().getItem(position).toString());
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });
        } else {
            Utils.toastMessage(this, "No se encontraron guias comprimidas");
        }

    }

    public ArrayList<String> GetFiles(String DirectoryPath) {
        ArrayList<String> MyFiles = new ArrayList<>();
        File f = new File(DirectoryPath);

        f.mkdirs();
        File[] files = f.listFiles();
        if (files.length == 0)
            return null;
        else {
            for (int i = 0; i < files.length; i++)
                MyFiles.add(files[i].getName());
        }

        return MyFiles;
    }
}
