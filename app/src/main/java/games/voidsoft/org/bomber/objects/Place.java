package games.voidsoft.org.bomber.objects;

/**
 * Created by Petar on 4/19/2015.
 */
public class Place {
    double longitude;
    double latitude;

    public Place(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public Place(){}

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
