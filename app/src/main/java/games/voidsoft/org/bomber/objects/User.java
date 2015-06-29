package games.voidsoft.org.bomber.objects;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Petar on 4/19/2015.
 */
public class User {
    //Parameters
    String name;
    String username;
    String password;
    String email;
    String money;
    Place currentPlace;
    public List<Integer> friends;
    public List<Bomb> bombs;

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String avatarURL;

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    Bitmap avatar;
    //{'name':'petar','username':'petar19992','password':'fulcrummig','email':'petar@gmail.com','money':'100','currentPlace':{'longitude':23.1542,'latitude':21.2452},'friends':[],'bombs':[]}

    public Place getCurrentPlace() {
        return currentPlace;
    }

    public void setCurrentPlace(Place currentPlace) {
        this.currentPlace = currentPlace;
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    public void setBombs(List<Bomb> bombs) {
        this.bombs = bombs;
    }

    public void addBomb(Bomb bomb)
    {
        bombs.add(bomb);
    }
    public Bomb getLastBomb()
    {
        return bombs.get(bombs.size()-1);
    }
    public Bomb getBombById(int id)
    {
        for(Bomb s: bombs)
        {
            if(s.bombID==id)
                return s;
        }
        return null;
    }
    public void addFriend(int user)
    {
        friends.add(user);
    }
    public boolean deleteFriend(int user)
    {
        for(int u:friends)
        {
            if(user==u)
            {
                friends.remove(u);
                return true;
            }
        }
        return false;
    }
 /*   public User getFriendByUsername(String username)
    {
        for(User u:friends)
        {
            if(u.username==username)
            {
                return u;
            }
        }
        return null;
    }
*/

    public User(){}
    public User(List<Bomb> bombs, String name, String username, String password, String email, String money, Place currentPlace, List<Integer> friends, Bitmap avatar, String avatarURL) {
        this.bombs = bombs;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.money = money;
        this.currentPlace = currentPlace;
        this.friends = friends;
        this.avatar=avatar;
        this.avatarURL=avatarURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public List<Integer> getFriends() {
        return friends;
    }

    public void setFriends(List<Integer> friends) {
        this.friends = friends;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
