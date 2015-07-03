package games.voidsoft.org.bomber.service;

import android.net.Uri;

/**
 * Created by Petar on 6/30/2015.
 */
public class NotificationProperties {
    int iconID;
    String contentTitle;
    String contentText;
    Uri sound;

    public NotificationProperties(int iconID, String contentTitle, String contentText, Uri sound, Class targetActivity, int notificationID) {
        this.iconID = iconID;
        this.contentTitle = contentTitle;
        this.contentText = contentText;
        this.sound = sound;
        this.targetActivity = targetActivity;
        this.notificationID = notificationID;
    }

    Class targetActivity;

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public Uri getSound() {
        return sound;
    }

    public void setSound(Uri sound) {
        this.sound = sound;
    }

    public Class getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(Class targetActivity) {
        this.targetActivity = targetActivity;
    }

    int notificationID;
}
