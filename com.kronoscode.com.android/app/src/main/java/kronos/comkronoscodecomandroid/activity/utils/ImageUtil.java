package kronos.comkronoscodecomandroid.activity.utils;

/**
 * Created by jhon on 15/2/16.
 */

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;
import javax.inject.Singleton;

import kronos.comkronoscodecomandroid.R;

/**
 * Created by jhon on 9/1/16.
 */
@Singleton
public class ImageUtil {

    @Inject
    Application application;

    @Inject
    public ImageUtil() {

    }

    /**
     * validate if images not empty
     * @param imageView
     * @param url
     */
    public void loadImage(ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            Picasso.with(application.getBaseContext())
                    .load(url)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher);
        }
    }
}