package games.voidsoft.org.bomber.objects;

import java.sql.Time;
import java.util.List;

/**
 * Created by Petar on 4/19/2015.
 */
public class Bomb {
    String type;
    Place place;
    User user;
    boolean status;
    Time timePlanted;
    Time timeDefuse;
    Time timeExplode;
    User defusedBy;
    List<User> killedUsers;
}
