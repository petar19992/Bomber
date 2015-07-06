package games.voidsoft.org.bomber.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import games.voidsoft.org.bomber.MapsActivity;
import games.voidsoft.org.bomber.R;
import games.voidsoft.org.bomber.objects.Singleton;
import games.voidsoft.org.bomber.objects.Status;
import games.voidsoft.org.bomber.objects.User;

/**
 * Created by Petar on 5/3/2015.
 */
public final class UpdateService extends Service implements LocationListener{

    public static SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String LatValue="LatKey";
    public static final String LonValue="LonKey";
    public static final String UsernameValue="UsernameKey";
    public static final String UserIDValue="UserIDKey";
    public static final String PasswordValue="PasswordKey";


    public static final String ACTION_UPDATE = "games.voidsoft.org.bomber.action.UPDATE";
    private final static IntentFilter sIntentFilter;
    User user;
    Status status;
    private MyAsyncTaskInMaps mAuthTask = null;


    private static NotificationManager mNotificationManager;
    private static int notificationID = 100;
    private static int numMessages = 0;

    public GoogleMap googleMap;
    public LocationManager locationManager;

    static {
        sIntentFilter = new IntentFilter();
        sIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        sIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        sIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(mTimeChangedReceiver, sIntentFilter);
        //Toast.makeText(this, "Radi", Toast.LENGTH_LONG).show();
        user= Singleton.getInstance().getUser();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_APPEND);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mTimeChangedReceiver);
        //Toast.makeText(getApplicationContext(), "Ugasio se", Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if(intent!=null){ //Ovo e uradjeno za svaki slucaj, da bi programer video zasto mu i gde puca program
            if (ACTION_UPDATE.equals(intent.getAction()))
            {
            }
        }
        else
            Log.e("MOJA GRESKA", "Intent je null");
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        sendMessage();
        return super.onStartCommand(intent, flags, startId);
    }

    // Send an Intent with an action named "custom-event-name". The Intent
    // sent should
    // be received by the ReceiverActivity.
    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("TIME_TICK");
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    /*public void makeText()
    {
        Toast.makeText(this,"Radi",Toast.LENGTH_LONG).show();
    }*/

    public final BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_TIMEZONE_CHANGED))
            {
                Toast.makeText(context,"Radi",Toast.LENGTH_LONG).show();
            }
            if(action.equals(Intent.ACTION_TIME_TICK))
            {
                 locationManager= (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, UpdateService.this);
                }else{
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, UpdateService.this);
                }



                String url="http://bomber.voidsoft.in.rs/updateUserLoc.php";
                List<String> parameters=new ArrayList<String>();
                List<String> value=new ArrayList<String>();
                if(sharedpreferences.contains(LatValue)) {
                    parameters.add("username");
                    value.add(sharedpreferences.getString(UsernameValue,""));
                    parameters.add("lat");
                    value.add(sharedpreferences.getString(LatValue,""));
                    parameters.add("lon");
                    value.add(sharedpreferences.getString(LonValue,""));
                    mAuthTask = new MyAsyncTaskInMaps(url, parameters, value);
                    mAuthTask.execute((Void) null);
                }
            }
            else
            {}
        }
    };

    protected void displayNotification() {
        Log.i("Start", "notification");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.mojabomba)
                        .setContentTitle("Ouch")
                        .setContentText("CT win !")
                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.explosion));
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MapsActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MapsActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(notificationID, mBuilder.build());
    }
    protected void cancelNotification() {
        Log.i("Cancel", "notification");
        mNotificationManager.cancel(notificationID);
    }

    protected void displayNotification(NotificationProperties notificationProperties) {
        Log.i("Start", "notification");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(notificationProperties.getIconID())
                        .setContentTitle(notificationProperties.getContentTitle())
                        .setContentText(notificationProperties.getContentText())
                        .setSound(notificationProperties.getSound());
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, notificationProperties.getTargetActivity());

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(notificationProperties.getTargetActivity());
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(notificationProperties.getNotificationID(), mBuilder.build());
    }
    protected void cancelAllNotification() {
        Log.i("Cancel", "notification");
        mNotificationManager.cancelAll();
    }

    /**Funkcija koja u shared Preferences stavlja neku vrednost u odredjeni kljuc*/
    public void shared(String key, String value)
    {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    @Override
    public void onLocationChanged(Location location) {
        shared(LatValue,String.valueOf(location.getLatitude()));
        shared(LonValue,String.valueOf(location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    ///**asinhroni deo**////
    public class MyAsyncTaskInMaps extends AsyncTask<Void, Void, Boolean> {



        private final String URL;
        private final List<String> Parameters;
        private final List<String> Value;
        public Object O;
        public String Result;
        public boolean Flag;

        public String getResult() {
            return Result;
        }
        public Object getO() {
            return O;
        }



        public MyAsyncTaskInMaps(String url, List<String> parameters, List<String> value) {
            URL=url;
            Parameters=parameters;
            Value=value;
            O=null;
            Flag=false;
        }
        public MyAsyncTaskInMaps(String url, List<String> parameters, List<String> value,Object o) {
            URL=url;
            Parameters=parameters;
            Value=value;
            O=o;
            Flag=false;
        }

        //Funkcija koja samo cita URL, bez argumenata za POST i GET metodu
        private String readUrl(String urlString) throws Exception {
            BufferedReader reader = null;
            try {
                java.net.URL url = new URL(urlString);
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuffer buffer = new StringBuffer();
                int read;
                char[] chars = new char[1024];
                while ((read = reader.read(chars)) != -1)
                    buffer.append(chars, 0, read);
                return buffer.toString();
            }
            finally {
                if (reader != null)
                    reader.close();
            }
        }

        ////POST METODA
        public  String POST(String url,List<String> params,List<String> value){
            InputStream inputStream = null;
            String result = "";
            try {
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                //Deo kada se samo parametri prosledjuju
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                int i=0;
                for(String p:params)
                {
                    nameValuePairs.add(new BasicNameValuePair(p, value.get(i++)));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpclient.execute(httpPost);
                Log.v("Post Status", "Code: " + httpResponse.getStatusLine().getStatusCode());
                result=String.valueOf(httpResponse.getStatusLine().getStatusCode());

                inputStream = httpResponse.getEntity().getContent();
                // 10. convert inputstream to string
                if(inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                    if(result.equals("true"))
                    {
                        //Ako mi bude vracao boolean ovde cu da stavim return true
                    }
                    else
                    {

                    }
                }
                else
                    result = "Did not work!";
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }

            return result;
        }

        private  String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            if(O!=null) {
                try {
                    String json = POST(URL, Parameters, Value);
                    JsonParser parser = new JsonParser();
                    JsonObject obj = parser.parse(json).getAsJsonObject();
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    O = gson.fromJson(obj, O.getClass());
                    return true;
                }
                catch (Exception ex)
                {
                    Log.e("Pribavljanje","Buffer",ex);
                    return false;
                }
            }
            else
            {
                try {

                    String result = POST(URL, Parameters, Value);
                    Result=result;
                    JsonParser parser = new JsonParser();
                    JsonObject obj = parser.parse(result).getAsJsonObject();
                    Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    status=gson.fromJson(obj,games.voidsoft.org.bomber.objects.Status.class);


                    List<String> parameters=new ArrayList<String>();
                    List<String> value=new ArrayList<String>();
                    parameters.add("username");
                    value.add(sharedpreferences.getString(UsernameValue,""));
                    parameters.add("password");
                    value.add(sharedpreferences.getString(PasswordValue,""));
                    String json=POST("http://bomber.voidsoft.in.rs/login.php",parameters,value);
                    parser = new JsonParser();
                    obj = parser.parse(json).getAsJsonObject();
                    gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    //Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                    User user2;
                    //user = gson.fromJson(json, User.class);
                    user2 =gson.fromJson(obj,User.class);
                    Singleton.getInstance().setUser(user2);


                    return true;
                }
                catch (Exception ex)
                {
                    Log.e("Pribavljanje","Buffer",ex);
                    return false;
                }
            }

        }

        //Ovde udje nakon izvrsenog zahteva za login
        @Override
        protected void onPostExecute(final Boolean success) {

            Flag=true;
            if (success) {
                if(status.getC4KillMe()!=0)
                    displayNotification(new NotificationProperties(R.drawable.eksplozija,"OUCH",String.valueOf(status.getC4KillMe())+" C4 bombs kills YOU",Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.explosion),MapsActivity.class,100));
                if(status.getMineKillMe()!=0)
                    displayNotification(new NotificationProperties(R.drawable.eksplozija,"OUCH",String.valueOf(status.getMineKillMe())+" MINES kills YOU",Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.explosion),MapsActivity.class,200));
                if(status.getC4IKill()!=0)
                    displayNotification(new NotificationProperties(R.drawable.winflag,"YEAH !",String.valueOf(status.getC4IKill())+" peoples were killed by YOUR C4 bombs",Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win),MapsActivity.class,300));
                if(status.getMineIKill()!=0)
                    displayNotification(new NotificationProperties(R.drawable.winflag,"YEAH !",String.valueOf(status.getMineIKill())+" peoples were killed by YOUR MINES",Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win),MapsActivity.class,400));
                status.clearAll();
            } else {

            }
        }

        @Override
        protected void onCancelled() {

        }

    }
}
