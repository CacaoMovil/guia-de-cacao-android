package kronos.comkronoscodecomandroid.activity.utils;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import kronos.comkronoscodecomandroid.activity.constants.Constants;

/**
 * Created by jhon on 16/2/16.
 */
@Singleton
public class FolderUtil {

    @Inject
    public FolderUtil() {

    }

    /**
     * Cleaning all compress files after unzip
     */
    public void cleanDir(String folder) {
        File file = new File(folder);

        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * This function will help us to identify if the file was already downloaded and exist in the phone
     * @param folderName
     * @return
     */
    public boolean checkIfFolderExist(String folderName) {
        File folder = new File(folderName);
        return folder.exists();
    }


    /**
     * This function will give us the name of the file without the complete link
     * @param path
     * @return
     */
    public String getNameFromPath(String path) {
        String[] zipName = path.split(Constants.LINK_SPLIT);
        try{
            String[] fileName = zipName[1].split(".zip");
            return fileName[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "";
        }
    }
}
