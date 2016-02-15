package kronos.comkronoscodecomandroid.activity.object;

import com.google.gson.annotations.SerializedName;
import com.kronoscode.cacao.android.app.model.Guide;

import java.util.List;

/**
 * Created by jhon on 5/5/15.
 */
public class Content {

    @SerializedName("manifest_version")
    private String manifest_version;
    @SerializedName("guide_id")
    private String guide_id;
    @SerializedName("guide_version")
    private String guide_version;
    @SerializedName("generation_date")
    private String generation_date;

    private List<Guide> contents;

    public List<Guide> getContents() {
        return contents;
    }

    public void setContents(List<Guide> contents) {
        this.contents = contents;
    }

    public String getManifest_version() {
        return manifest_version;
    }

    public void setManifest_version(String manifest_version) {
        this.manifest_version = manifest_version;
    }

    public String getGuide_id() {
        return guide_id;
    }

    public void setGuide_id(String guide_id) {
        this.guide_id = guide_id;
    }

    public String getGuide_version() {
        return guide_version;
    }

    public void setGuide_version(String guide_version) {
        this.guide_version = guide_version;
    }

    public String getGeneration_date() {
        return generation_date;
    }

    public void setGeneration_date(String generation_date) {
        this.generation_date = generation_date;
    }
}
