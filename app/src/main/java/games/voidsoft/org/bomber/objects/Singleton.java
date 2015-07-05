package games.voidsoft.org.bomber.objects;

import com.google.android.gms.maps.GoogleMap;

import java.util.List;

/**
 * Created by Petar on 4/24/2015.
 */
public class Singleton {
    public User getUser() {
        return user;
    }

    public List<Friends> getListOfFriends() {
        return listOfFriends;
    }

    public void setListOfFriends(List<Friends> listOfFriends) {
        this.listOfFriends = listOfFriends;
    }

    List<Friends> listOfFriends;

    public List<Friends> getTop3() {
        return top3;
    }

    public void setTop3(List<Friends> top3) {
        this.top3 = top3;
    }

    List<Friends> top3;

    public void setUser(User user) {
        this.user = user;
    }

    User user;

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public GoogleMap googleMap;
    private static Singleton ourInstance;

    public static Singleton getInstance() {
        if(ourInstance==null)
            ourInstance=new Singleton();
        return ourInstance;
    }

    private Singleton() {
    }
}
