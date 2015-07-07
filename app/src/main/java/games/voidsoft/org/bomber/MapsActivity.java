package games.voidsoft.org.bomber;

import android.app.Dialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import games.voidsoft.org.bomber.connection.MyAsyncTask;
import games.voidsoft.org.bomber.connection.ServerConnection;
import games.voidsoft.org.bomber.costumContextMenu.ContextMenuAdapter;
import games.voidsoft.org.bomber.costumContextMenu.ContextMenuItem;
import games.voidsoft.org.bomber.objects.Bomb;
import games.voidsoft.org.bomber.objects.Friends;
import games.voidsoft.org.bomber.objects.Place;
import games.voidsoft.org.bomber.objects.Singleton;
import games.voidsoft.org.bomber.objects.Status;
import games.voidsoft.org.bomber.objects.User;
import games.voidsoft.org.bomber.service.NotificationProperties;
import games.voidsoft.org.bomber.service.UpdateService;


import android.text.format.*;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


public class MapsActivity extends ActionBarActivity {

    public static SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String LatValue="LatKey";
    public static final String LonValue="LonKey";
    public static final String UsernameValue="UsernameKey";
    public static final String UserIDValue="UserIDKey";


    //Deo vezan za ContextActivity
    List<ContextMenuItem> contextMenuItems;
    Dialog customDialog;

    LayoutInflater inflater;
    View child;
    ListView listView;
    ContextMenuAdapter adapter;
    Intent intentForService;
    Intent intentForMinutService;

    User user;
    Status status;

    public GoogleMap googleMap;
    public LatLng myLoc;

    //ImageView avatar;

    private static NotificationManager mNotificationManager;
    ProgressDialog dialog;

    private IntentFilter sIntentFilter;

    //Za asinhroni task
    private MyAsyncTaskInMaps mAuthTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(this,"nadam se da ovo radi",Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_maps);
        user= Singleton.getInstance().getUser();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_APPEND);
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            cancelAllNotification();
        }
        catch (Exception e)
        {}
        /*avatar=(ImageView)findViewById(R.id.imageview1);
        try
        {
            avatar.setImageBitmap(user.getAvatar());
        }
        catch (Exception ex){}*/

        googleMap=((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnMyLocationChangeListener(myLocationChangeListener);
        googleMap.clear();
        Singleton.getInstance().setGoogleMap(googleMap);


        boolean isMine;// Cija je bomba, true je moja, false je od prijatelja
        isMine=true;
        for(Bomb b:user.bombs)
        {
            if(b.isStatus())
                placeBombAt(new LatLng(b.getPlace().getLatitude(),b.getPlace().getLongitude()),b.getType(),isMine );
                //googleMap.addMarker(new MarkerOptions().position(new LatLng(b.getPlace().getLatitude(),b.getPlace().getLongitude())).title(b.getType()+" placed at " + b.getTimePlanted()));
        }
        isMine=false;
        for(Bomb b:user.friendsBombs)
        {
            if(b.isStatus())
                placeBombAt(new LatLng(b.getPlace().getLatitude(),b.getPlace().getLongitude()),b.getType(),isMine);
            //googleMap.addMarker(new MarkerOptions().position(new LatLng(b.getPlace().getLatitude(),b.getPlace().getLongitude())).title(b.getType()+" placed at " + b.getTimePlanted()));
        }
        //CameraUpdate update= CameraUpdateFactory.newLatLng(new LatLng(43.337165,21.876526));
        //CameraUpdate update= CameraUpdateFactory.newLatLngZoom(new LatLng(43.337165, 21.876526), 12);
        //googleMap.animateCamera(update);
        //googleMap.addMarker(new MarkerOptions().position(new LatLng(43.337165, 21.876526)).title("Find me here !"));

        //startService(new Intent(UpdateService.ACTION_UPDATE));
        intentForService=new Intent(this, UpdateService.class);
        startService(intentForService);
        sIntentFilter = new IntentFilter();
        sIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, sIntentFilter);
        getApplicationContext().registerReceiver(mMessageReceiver, sIntentFilter);
        //Intent intent = new Intent("TIME_TICK");
        //LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }
    //Funkcija u koju se ulazi na svaki minut
   public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            user=Singleton.getInstance().getUser();
            googleMap.clear();
            boolean isMine;// Cija je bomba, true je moja, false je od prijatelja
            isMine=true;
            for(Bomb b:user.bombs)
            {
                if(b.isStatus())
                    placeBombAt(new LatLng(b.getPlace().getLatitude(),b.getPlace().getLongitude()),b.getType(),isMine );
                //googleMap.addMarker(new MarkerOptions().position(new LatLng(b.getPlace().getLatitude(),b.getPlace().getLongitude())).title(b.getType()+" placed at " + b.getTimePlanted()));
            }
            isMine=false;
            for(Bomb b:user.friendsBombs)
            {
                if(b.isStatus())
                    placeBombAt(new LatLng(b.getPlace().getLatitude(),b.getPlace().getLongitude()),b.getType(),isMine);
                //googleMap.addMarker(new MarkerOptions().position(new LatLng(b.getPlace().getLatitude(),b.getPlace().getLongitude())).title(b.getType()+" placed at " + b.getTimePlanted()));
            }
            try {
                MenuItem rec2 =(MenuItem) findViewById(R.id.money);
                rec2.setTitle(user.getMoney());
            }catch (Exception ee)
            {}

        }
    };
    //Funkcija u koju ulazi kada se promeni lokacija (obicno je 4 sekunde)
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            myLoc=loc;
            //googleMap.addMarker(new MarkerOptions().position(loc).title("My position"));
            if(googleMap != null){
                //googleMap.animateCamera(CameraUpdateFactory.newLatLng(loc));
            }
            shared(LatValue,String.valueOf(location.getLatitude()));
            shared(LonValue,String.valueOf(location.getLongitude()));
            user.setCurrentPlace(new Place(myLoc.longitude,myLoc.latitude));
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        MenuItem rec = menu.findItem(R.id.avatar);
        rec.setIcon(new BitmapDrawable(getResources(), user.getAvatar()));
        MenuItem rec2 = menu.findItem(R.id.money);
        rec2.setTitle(user.getMoney());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //     return true;
        //}
        if(id==R.id.highscore)
        {
            String url="http://bomber.voidsoft.in.rs/RangList.php";
            List<String> parameters=new ArrayList<String>();
            List<String> value=new ArrayList<String>();
            mAuthTask = new MyAsyncTaskInMaps(url, parameters, value,4);
            mAuthTask.execute((Void) null);
            dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
            //http://bomber.voidsoft.in.rs/RangList.php
        }
        else if(id==R.id.logOut)
        {
            sharedpreferences.edit().clear().commit();
            stopService(intentForService);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void buttonFirends(View view)
    {
        String url="http://bomber.voidsoft.in.rs/getFriends.php";
        List<String> parameters=new ArrayList<String>();
        List<String> value=new ArrayList<String>();
        parameters.add("userID");
        value.add(String.valueOf(user.getUserID()));
        mAuthTask = new MyAsyncTaskInMaps(url, parameters, value,0);
        mAuthTask.execute((Void) null);
        dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
    }

    public void placeBombAt(LatLng position,String type, boolean isMine)
    {
        if(type.equals("MINE"))
        {
            if(isMine)
            googleMap.addMarker(new MarkerOptions().position(position)
                    .icon(BitmapDescriptorFactory.fromResource(
                            R.drawable.mojamina))
                    .anchor(0.5f, 0.5f));
            else
                googleMap.addMarker(new MarkerOptions().position(position)
                        .icon(BitmapDescriptorFactory.fromResource(
                                R.drawable.minanadrugara))
                        .anchor(0.5f, 0.5f));
        }
        else if(type.equals("C4"))
        {
            if(isMine)
            googleMap.addMarker(new MarkerOptions().position(position)
                    .icon(BitmapDescriptorFactory.fromResource(
                            R.drawable.mojabomba))
                            // Specifies the anchor to be at a particular point in the marker image.
                    .anchor(0.5f, 0.5f));
            else
                googleMap.addMarker(new MarkerOptions().position(position)
                        .icon(BitmapDescriptorFactory.fromResource(
                                R.drawable.bombanadrugara))
                                // Specifies the anchor to be at a particular point in the marker image.
                        .anchor(0.5f, 0.5f));
        }
    }
    public void buttonPlaceBomb(View view)
    {
        //Intent i=new Intent(MapsActivity.this, ContextActivity.class);
        //startActivity(i);
        inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        child = inflater.inflate(R.layout.listview_context_menu, null);
        listView = (ListView) child.findViewById(R.id.listView_context_menu);

        contextMenuItems = new ArrayList<ContextMenuItem>();
        contextMenuItems.add(new ContextMenuItem(getResources().getDrawable(
                R.drawable.mojaminabig), "Place Mine"));
        contextMenuItems.add(new ContextMenuItem(getResources().getDrawable(
                R.drawable.mojabombabig), "Place C4"));

        adapter = new ContextMenuAdapter(this,
                contextMenuItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                customDialog.dismiss();

                Bomb bomb=new Bomb();
                //bomb.setUser(user);
                bomb.setStatus(true);
                Calendar c = Calendar.getInstance();
                bomb.setTimePlanted(new Date(c.getTimeInMillis()));
                bomb.setPlace(new Place(myLoc.longitude,myLoc.latitude));

                if (position == 0)
                {
                    googleMap.addMarker(new MarkerOptions().position(myLoc)
                            .icon(BitmapDescriptorFactory.fromResource(
                                    R.drawable.mojamina))
                                    // Specifies the anchor to be at a particular point in the marker image.
                            .anchor(0.5f, 0.5f));
                    bomb.setType("MINE");
                    bomb.setPlace(new Place(myLoc.longitude,myLoc.latitude));

                    String url="http://bomber.voidsoft.in.rs/placeBomb.php";
                    List<String> parameters=new ArrayList<String>();
                    List<String> value=new ArrayList<String>();
                    parameters.add("userID");
                    value.add(String.valueOf(user.getUserID()));
                    parameters.add("lat");
                    value.add(sharedpreferences.getString(LatValue,""));
                    parameters.add("lon");
                    value.add(sharedpreferences.getString(LonValue,""));
                    parameters.add("type");
                    value.add("MINE");
                    parameters.add("timeToExplode");
                    value.add("30"); //Ovo posle stavi opciono da bude
                    mAuthTask = new MyAsyncTaskInMaps(url, parameters, value,1);
                    mAuthTask.execute((Void) null);

                }
                else
                if (position == 1)
                {
                    googleMap.addMarker(new MarkerOptions().position(myLoc)
                            .icon(BitmapDescriptorFactory.fromResource(
                                    R.drawable.mojabomba))
                                    // Specifies the anchor to be at a particular point in the marker image.
                            .anchor(0.5f, 0.5f));
                    bomb.setType("C4");
                    String url="http://bomber.voidsoft.in.rs/placeBomb.php";
                    List<String> parameters=new ArrayList<String>();
                    List<String> value=new ArrayList<String>();
                    parameters.add("userID");
                    value.add(String.valueOf(user.getUserID()));
                    parameters.add("lat");
                    value.add(sharedpreferences.getString(LatValue,""));
                    parameters.add("lon");
                    value.add(sharedpreferences.getString(LonValue,""));
                    parameters.add("type");
                    value.add("C4");
                    parameters.add("timeToExplode");
                    value.add("30"); //Ovo posle stavi opciono da bude
                    mAuthTask = new MyAsyncTaskInMaps(url, parameters, value,1);
                    mAuthTask.execute((Void) null);
                }
                user.addBomb(bomb);

            }
        });

        customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(child);
        customDialog.show();
    }
    public void buttonTry(View view)
    {
        String url="http://bomber.voidsoft.in.rs/tryToDefuse.php";
        List<String> parameters=new ArrayList<String>();
        List<String> value=new ArrayList<String>();
        parameters.add("username");
        value.add(String.valueOf(user.getUsername()));
        parameters.add("lat");
        value.add(sharedpreferences.getString(LatValue,""));
        parameters.add("lon");
        value.add(sharedpreferences.getString(LonValue,""));
        mAuthTask = new MyAsyncTaskInMaps(url, parameters, value,2);
        mAuthTask.execute((Void) null);
    }

    /**Funkcija koja u shared Preferences stavlja neku vrednost u odredjeni kljuc*/
    public void shared(String key, String value)
    {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
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

    ///**asinhroni deo**////
    public class MyAsyncTaskInMaps extends AsyncTask<Void, Void, Boolean> {



        private final String URL;
        private final List<String> Parameters;
        private final List<String> Value;
        public Object O;
        public String Result;
        public boolean Flag;
        public int flag; //0 Ako uzimam prijatelje, 1 ako dodajem bombu, 2 ako defuzujem

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
        public MyAsyncTaskInMaps(String url, List<String> parameters, List<String> value, int flag) {
            URL=url;
            Parameters=parameters;
            Value=value;
            O=null;
            Flag=false;
            this.flag=flag;
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
                URL url = new URL(urlString);
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
                    if(flag==1) {
                        if (result.equals("true"))
                            return true;
                    }
                    else
                        if(flag==0)
                        {
                            /*JsonParser parser = new JsonParser();
                            JsonObject obj = parser.parse(result).getAsJsonObject();
                            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                            Type listType = new TypeToken<ArrayList<Friends>>() {
                            }.getType();
                            List<Friends> yourClassList = new Gson().fromJson(obj, listType);
                            Singleton.getInstance().setListOfFriends(yourClassList);*/
                            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                            JsonParser parser = new JsonParser();
                            JsonArray jArray = parser.parse(result).getAsJsonArray();

                            ArrayList<Friends> lcs = new ArrayList<Friends>();

                            for(JsonElement obj : jArray )
                            {
                                Friends cse = gson.fromJson( obj , Friends.class);
                                lcs.add(cse);
                            }
                            Singleton.getInstance().setListOfFriends(lcs);
                            try {
                                for(Friends f: lcs)
                                {
                                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(f.getFirendAvatarURL()).getContent());
                                    f.setFriendAvatar(bitmap);
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                        else
                            if(flag==2)
                            {
                                JsonParser parser = new JsonParser();
                                JsonObject obj = parser.parse(result).getAsJsonObject();
                                Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                status=gson.fromJson(obj,games.voidsoft.org.bomber.objects.Status.class);
                                return true;
                            }
                        else
                            if(flag==4)
                            {
                                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                JsonParser parser = new JsonParser();
                                JsonArray jArray = parser.parse(result).getAsJsonArray();

                                ArrayList<Friends> lcs = new ArrayList<Friends>();

                                for(JsonElement obj : jArray )
                                {
                                    Friends cse = gson.fromJson( obj , Friends.class);
                                    lcs.add(cse);
                                }
                                Singleton.getInstance().setTop3(lcs);
                                try {
                                    for(Friends f: lcs)
                                    {
                                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(f.getFirendAvatarURL()).getContent());
                                        f.setFriendAvatar(bitmap);
                                    }
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return true;
                            }
                    return false;
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

                    if(flag==0)
                    {
                        dialog.dismiss();
                        Intent mainIntent = new Intent(MapsActivity.this,FriendsActivity.class);
                        MapsActivity.this.startActivity(mainIntent);
                        //MapsActivity.this.finish();
                    }
                    else if(flag==1)
                        Toast.makeText(getApplicationContext(),"Bomb has been planted ! ",Toast.LENGTH_LONG).show();
                    else if(flag==2)
                    {
                        if(status.getC4KillMe()!=0)
                            displayNotification(new NotificationProperties(R.drawable.eksplozija,"OUCH",String.valueOf(status.getC4KillMe())+" C4 bombs kills YOU", Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.explosion),MapsActivity.class,100));
                        if(status.getMineKillMe()!=0)
                            displayNotification(new NotificationProperties(R.drawable.eksplozija,"OUCH",String.valueOf(status.getMineKillMe())+" MINES kills YOU",Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.explosion),MapsActivity.class,200));
                        if(status.getC4IKill()!=0)
                            displayNotification(new NotificationProperties(R.drawable.winflag,"YEAH !",String.valueOf(status.getC4IKill())+" peoples were killed by YOUR C4 bombs",Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win),MapsActivity.class,300));
                        if(status.getMineIKill()!=0)
                            displayNotification(new NotificationProperties(R.drawable.winflag,"YEAH !",String.valueOf(status.getMineIKill())+" peoples were killed by YOUR MINES",Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win),MapsActivity.class,400));
                        if(status.getMineIDefuse()!=0)
                            displayNotification(new NotificationProperties(R.drawable.winflag,"YEAH !",String.valueOf(status.getMineIDefuse())+" MINES defised by YOU ",Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win),MapsActivity.class,500));

                        status.clearAll();
                    }
                    else
                        if(flag==4)
                        {
                            dialog.dismiss();
                            Intent mainIntent = new Intent(MapsActivity.this,ActivityRangList.class);
                            MapsActivity.this.startActivity(mainIntent);
                        }
                    }

        }

        @Override
        protected void onCancelled() {

        }
    }
}
