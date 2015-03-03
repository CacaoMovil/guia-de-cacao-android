package kronos.comkronoscodecomandroid.activity.object;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jhon on 3/3/15.
 */
public class GuideObject {

    @SerializedName("name")
    private String mName;
    @SerializedName("file")
    private String mFile;
    @SerializedName("date")
    private String mDate;
    @SerializedName("num_version")
    private String mNumVersion;
    @SerializedName("versions")
    private List<VersionObject> mVersions;

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmFile() {
        return mFile;
    }

    public void setmFile(String mFile) {
        this.mFile = mFile;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmNumVersion() {
        return mNumVersion;
    }

    public void setmNumVersion(String mNumVersion) {
        this.mNumVersion = mNumVersion;
    }

    public List<VersionObject> getmVersions() {
        return mVersions;
    }

    public void setmVersions(List<VersionObject> mVersions) {
        this.mVersions = mVersions;
    }
}
