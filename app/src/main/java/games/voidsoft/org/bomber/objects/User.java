package games.voidsoft.org.bomber.objects;

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
    public List<User> friends;
    public List<Bomb> bombs;
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



    public User(List<Bomb> bombs, String name, String username, String password, String email, String money, Place currentPlace, List<User> friends) {
        this.bombs = bombs;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.money = money;
        this.currentPlace = currentPlace;
        this.friends = friends;
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

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
