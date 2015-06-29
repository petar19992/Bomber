package games.voidsoft.org.bomber.objects;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Petar on 4/19/2015.
 */
public class Bomb {
    int bombID;
    String type;
    Place place;
    int user;
    boolean status;
    Date timePlanted;
    Date timeDefuse;
    Date timeExplode;
    int defusedBy;
    List<Integer> killedUsers;

    public Bomb()
    {
        this.killedUsers= new ArrayList<Integer>();
    }
    public Bomb(int bombID, String type, Place place, int user, boolean status, Date timePlanted, Date timeDefuse, Date timeExplode, int defusedBy, List<Integer> killedUsersID) {
        this.bombID = bombID;
        this.type = type;
        this.place = place;
        this.user = user;
        this.status = status;
        this.timePlanted = timePlanted;
        this.timeDefuse = timeDefuse;
        this.timeExplode = timeExplode;
        this.defusedBy = defusedBy;
        this.killedUsers = killedUsersID;
    }

    public int getBombID() {
        return bombID;
    }

    public void setBombID(int bombID) {
        this.bombID = bombID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getTimePlanted() {
        return timePlanted;
    }

    public void setTimePlanted(Date timePlanted) {
        this.timePlanted = timePlanted;
    }

    public Date getTimeDefuse() {
        return timeDefuse;
    }

    public void setTimeDefuse(Date timeDefuse) {
        this.timeDefuse = timeDefuse;
    }

    public Date getTimeExplode() {
        return timeExplode;
    }

    public void setTimeExplode(Date timeExplode) {
        this.timeExplode = timeExplode;
    }

    public int getDefusedBy() {
        return defusedBy;
    }

    public void setDefusedBy(int defusedBy) {
        this.defusedBy = defusedBy;
    }

    public List<Integer> getKilledUsers() {
        return killedUsers;
    }

    public void setKilledUsers(List<Integer> killedUsers) {
        this.killedUsers = killedUsers;
    }
}
