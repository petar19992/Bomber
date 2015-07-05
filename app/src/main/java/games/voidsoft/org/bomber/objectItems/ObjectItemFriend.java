package games.voidsoft.org.bomber.objectItems;

import android.graphics.Bitmap;

/**
 * Created by Petar on 7/4/2015.
 */
public class ObjectItemFriend {

    public int friendID;
    public String friendUsername;
    public Bitmap friendAvatar;

    public ObjectItemFriend(int friendID, String friendUsername, Bitmap friendAvatar) {
        this.friendID = friendID;
        this.friendUsername = friendUsername;
        this.friendAvatar = friendAvatar;
    }
}
