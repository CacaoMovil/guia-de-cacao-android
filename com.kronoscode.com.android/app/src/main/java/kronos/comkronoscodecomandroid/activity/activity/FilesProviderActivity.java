package kronos.comkronoscodecomandroid.activity.activity;

/**
 * Created by jhon on 20/5/15.
 */

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kronos.comkronoscodecomandroid.R;

/**
 * This class will handle the custom explorer to get the zip file
 * @author jhon chavarria
 *
 */
public class FilesProviderActivity extends ListActivity {

    private List<String> item = null;
    private List<String> path = null;
    private static final String mDir = "/sdcard";
    private TextView mPath;
    private File mFile;

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

        mPath = (TextView) findViewById(R.id.path);
        getDir(mDir);
    }

    /**
     * This function will display all the files inside a specific folder
     * @param dirPath
     */
    private void getDir(String dirPath) {
        mPath.setText("Location: " + dirPath);

        item = new ArrayList<>();
        path = new ArrayList<>();

        File f = new File(dirPath);
        File[] files = f.listFiles();

        if (!dirPath.equals(mDir)) {

            item.add(mDir);
            path.add(mDir);

            item.add("../");
            path.add(f.getParent());

        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            path.add(file.getPath());
            if (file.isDirectory())
                item.add(file.getName() + "/");
            else
                item.add(file.getName());
        }

        ArrayAdapter<String> fileList = new ArrayAdapter<>(this,
                R.layout.explorer_row, item);
        setListAdapter(fileList);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        mFile = new File(path.get(position));

        if (mFile.isDirectory()) {
            if (mFile.canRead())
                getDir(path.get(position));
            else {
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.eye_icon)
                        .setTitle(
                                "[" + mFile.getName()
                                        + "] folder can't be read!")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                }).show();
            }
        } else {

            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.eye_icon)
                    .setTitle("Seleccionar")
                    .setMessage("Desea Seleccionar este archivo:  " + mFile.getName() + " ?")
                    .setPositiveButton("Si",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("result", mFile.getAbsolutePath());
                                    setResult(RESULT_OK, returnIntent);
                                    finish();
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).show();
        }
    }
}
