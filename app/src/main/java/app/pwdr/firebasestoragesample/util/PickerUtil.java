package app.pwdr.firebasestoragesample.util;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import androidx.core.content.ContextCompat;

import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;

import java.util.ArrayList;

import app.pwdr.firebasestoragesample.R;

public class PickerUtil {
    public static void pick(Activity activity, int maxCount, ArrayList<Uri> selectedImages) {
        Context c = activity.getApplicationContext();
        FishBun.with(activity)
                .setImageAdapter(new GlideAdapter())
                .setIsUseDetailView(true)
                .setPickerSpanCount(4)
                .setMaxCount(maxCount)
                .setActionBarColor(
                        ContextCompat.getColor(c, android.R.color.white),
                        ContextCompat.getColor(c, R.color.colorPrimaryDark)
                )
                .setActionBarTitleColor(
                        ContextCompat.getColor(c, android.R.color.black)
                )
                .setSelectedImages(selectedImages)
                .setAlbumSpanCount(1, 2)
                .setButtonInAlbumActivity(false)
                .setCamera(true)
                // .setReachLimitAutomaticClose(true)
                .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(c, R.drawable.ic_arrow_back_black_24dp))
                // .setOkButtonDrawable(ContextCompat.getDrawable(this, R.drawable.ic_custom_ok))
                .setAllViewTitle(c.getString(R.string.fishbun_all_view_title))
                .setActionBarTitle(c.getString(R.string.fishbun_action_bar_title))
                .exceptGif(true)
                .setMenuDoneText(c.getString(R.string.done))
                .setMenuTextColor(ContextCompat.getColor(c, android.R.color.black))
                .textOnImagesSelectionLimitReached(c.getString(R.string.fishbun_limit_reached))
                .textOnNothingSelected(c.getString(R.string.fishbun_nothing_selected))
                .startAlbum();
    }
}