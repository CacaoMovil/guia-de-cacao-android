package kronos.comkronoscodecomandroid.activity.utils;

/**
 * Created by jhon on 6/3/15.
 */

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Singleton;

/**
 * This class will descompress the zip information
 * @author jhon chavarria
 *
 */
public class Decompress {

    private String zipFile;
    private String location;

    public Decompress(String zipFile, String location) {
        this.zipFile = zipFile;
        this.location = location;
    }

    /**
     * Unzip a zip file.  Will overwrite existing files.
     *

     * @throws IOException
     */
    public void unzip() throws IOException {
        int size;
        int BUFFER_SIZE = 8192;
        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            if (!location.endsWith("/")) {
                location += "/";
            }

            File f = new File(location);
            if(!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));

            try {
                ZipEntry ze;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();
                    File unzipFile = new File(path);

                    if (ze.isDirectory()) {
                        if(!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        // check for and create parent directories if they don't exist
                        File parentDir = unzipFile.getParentFile();
                        if ( null != parentDir ) {
                            if ( !parentDir.isDirectory() ) {
                                parentDir.mkdirs();
                            }
                        }
                        // unzip the file
                        FileOutputStream out = new FileOutputStream(unzipFile, false);
                        BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                        try {
                            while ( (size = zin.read(buffer, 0, BUFFER_SIZE)) != -1 ) {
                                fout.write(buffer, 0, size);
                            }

                            zin.closeEntry();
                        }
                        finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                }
            }
            finally {
                zin.close();
            }
        }
        catch (Exception e) {
            Log.e("CACAO DEBUG", "Unzip exception", e);
        }
    }
}