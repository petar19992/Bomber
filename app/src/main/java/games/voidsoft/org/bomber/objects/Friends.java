package games.voidsoft.org.bomber.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import games.voidsoft.org.bomber.R;

/**
 * Created by Petar on 7/4/2015.
 */
public class Friends {
    public int friendID;
    public String friendUsername;
    public String firendAvatarURL;
    public Bitmap friendAvatar;//= drawableToBitmap(R.drawable.defaultavatar);

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public Friends(int friendID, String friendUsername, String firendAvatarURL, Bitmap friendAvatar) {
        this.friendID = friendID;
        this.friendUsername = friendUsername;
        this.firendAvatarURL = firendAvatarURL;
        this.friendAvatar = friendAvatar;
    }

    public Friends(int friendID, String friendUsername, String firendAvatarURL) {

        this.friendID = friendID;
        this.friendUsername = friendUsername;
        this.firendAvatarURL = firendAvatarURL;
    }

    public int getFriendID() {

        return friendID;
    }

    public void setFriendID(int friendID) {
        this.friendID = friendID;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public String getFirendAvatarURL() {
        return firendAvatarURL;
    }

    public void setFirendAvatarURL(String firendAvatarURL) {
        this.firendAvatarURL = firendAvatarURL;
    }

    public Bitmap getFriendAvatar() {
        return friendAvatar;
    }

    public void setFriendAvatar(Bitmap friendAvatar) {
        this.friendAvatar = friendAvatar;
    }
}
