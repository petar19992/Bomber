package games.voidsoft.org.bomber.objects;

import java.util.ArrayList;

/**
 * Created by Petar on 6/29/2015.
 */
public class Status {
    int mineKillMe;
    int c4KillMe;
    int mineIKill;
    int c4IKill;

    public Status(int mineIDefuse, int mineKillMe, int c4KillMe, int mineIKill, int c4IKill) {
        this.mineIDefuse = mineIDefuse;
        this.mineKillMe = mineKillMe;
        this.c4KillMe = c4KillMe;
        this.mineIKill = mineIKill;
        this.c4IKill = c4IKill;
    }

    public int getMineIDefuse() {

        return mineIDefuse;
    }

    public void setMineIDefuse(int mineIDefuse) {
        this.mineIDefuse = mineIDefuse;
    }

    int mineIDefuse;
    public Status(int mineKillMe, int c4KillMe, int mineIKill, int c4IKill) {
        this.mineKillMe = mineKillMe;
        this.c4KillMe = c4KillMe;
        this.mineIKill = mineIKill;
        this.c4IKill = c4IKill;
    }
    public boolean clearAll()
    {
        mineKillMe=c4KillMe=mineIKill=c4IKill=0;
        return true;
    }
    public int getMineKillMe() {
        return mineKillMe;
    }

    public void setMineKillMe(int mineKillMe) {
        this.mineKillMe = mineKillMe;
    }

    public int getC4KillMe() {
        return c4KillMe;
    }

    public void setC4KillMe(int c4KillMe) {
        this.c4KillMe = c4KillMe;
    }

    public int getMineIKill() {
        return mineIKill;
    }

    public void setMineIKill(int mineIKill) {
        this.mineIKill = mineIKill;
    }

    public int getC4IKill() {
        return c4IKill;
    }

    public void setC4IKill(int c4IKill) {
        this.c4IKill = c4IKill;
    }
}