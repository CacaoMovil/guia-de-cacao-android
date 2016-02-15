package kronos.comkronoscodecomandroid.activity.object;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jhon on 15/2/16.
 */
public class Setting {

    @SerializedName("title")
    private String title;
    @SerializedName("welcome_title")
    private String welcome_title;
    @SerializedName("logo")
    private String logo;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWelcome_title() {
        return welcome_title;
    }

    public void setWelcome_title(String welcome_title) {
        this.welcome_title = welcome_title;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
