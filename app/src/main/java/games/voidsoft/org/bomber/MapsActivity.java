package games.voidsoft.org.bomber;

import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.IBinder;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import games.voidsoft.org.bomber.connection.ServerConnection;
import games.voidsoft.org.bomber.costumContextMenu.ContextMenuAdapter;
import games.voidsoft.org.bomber.costumContextMenu.ContextMenuItem;
import games.voidsoft.org.bomber.objects.Bomb;
import games.voidsoft.org.bomber.objects.Place;
import games.voidsoft.org.bomber.objects.Singleton;
import games.voidsoft.org.bomber.objects.User;
import games.voidsoft.org.bomber.service.UpdateService;


import android.text.format.*;


public class MapsActivity extends ActionBarActivity {

    Context context2;
    //Deo vezan za ContextActivity
    List<ContextMenuItem> contextMenuItems;
    Dialog customDialog;

    LayoutInflater inflater;
    View child;
    ListView listView;
    ContextMenuAdapter adapter;

    User user;

    public GoogleMap googleMap;
    public LatLng myLoc;

    ImageView avatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context2=this;
        setContentView(R.layout.activity_maps);
        user= Singleton.getInstance().getUser();

        TextView money=(TextView)findViewById(R.id.header);
        money.setText(user.getMoney());
        avatar=(ImageView)findViewById(R.id.imageview1);
        try
        {
            avatar.setImageBitmap(user.getAvatar());
        }
        catch (Exception ex){}

        googleMap=((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnMyLocationChangeListener(myLocationChangeListener);
        googleMap.clear();

        for(Bomb b:user.bombs)
        {
            if(b.isStatus())
                placeBombAt(new LatLng(b.getPlace().getLatitude(),b.getPlace().getLongitude()),b.getType());
                //googleMap.addMarker(new MarkerOptions().position(new LatLng(b.getPlace().getLatitude(),b.getPlace().getLongitude())).title(b.getType()+" placed at " + b.getTimePlanted()));
        }
        //CameraUpdate update= CameraUpdateFactory.newLatLng(new LatLng(43.337165,21.876526));
        //CameraUpdate update= CameraUpdateFactory.newLatLngZoom(new LatLng(43.337165, 21.876526), 12);
        //googleMap.animateCamera(update);
        //googleMap.addMarker(new MarkerOptions().position(new LatLng(43.337165, 21.876526)).title("Find me here !"));

        //startService(new Intent(UpdateService.ACTION_UPDATE));
        startService(new Intent(getBaseContext(), UpdateService.class));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("TIME_TICK"));
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            Toast.makeText(context,"RADIIIIIIII",Toast.LENGTH_LONG).show();
            makeText();
        }
    };
    public void makeText()
    {
        Toast.makeText(this,"Radiiiiiiiiiiiii",Toast.LENGTH_LONG).show();
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            myLoc=loc;
            //googleMap.addMarker(new MarkerOptions().position(loc).title("My position"));
            if(googleMap != null){
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        //menu.add(1,1,1,user.getAvatar());
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

        }
        else if(id==R.id.settings)
        {

        }
        return super.onOptionsItemSelected(item);
    }

    public void buttonFirends(View view)
    {
        Intent mainIntent = new Intent(MapsActivity.this,FriendsActivity.class);
        MapsActivity.this.startActivity(mainIntent);
        //MapsActivity.this.finish();
    }

    public void placeBombAt(LatLng position,String type)
    {
        if(type.equals("mine"))
        {
            googleMap.addMarker(new MarkerOptions().position(position)
                    .icon(BitmapDescriptorFactory.fromResource(
                            R.drawable.mine_mini))
                            // Specifies the anchor to be at a particular point in the marker image.
                    .anchor(0.5f, 0.5f));
        }
        else if(type.equals("C4"))
        {
            googleMap.addMarker(new MarkerOptions().position(position)
                    .icon(BitmapDescriptorFactory.fromResource(
                            R.drawable.bomb_mini))
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
                R.drawable.mine), "Place Mine"));
        contextMenuItems.add(new ContextMenuItem(getResources().getDrawable(
                R.drawable.bomb), "Place C4"));

        adapter = new ContextMenuAdapter(this,
                contextMenuItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                customDialog.dismiss();

               /* Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
                Canvas canvas1 = new Canvas(bmp);

                // paint defines the text color,
                // stroke width, size
                Paint color = new Paint();
                color.setTextSize(35);
                color.setColor(Color.BLACK);*/
                Bomb bomb=new Bomb();
                bomb.setUser(user);
                bomb.setStatus(true);
                Calendar c = Calendar.getInstance();
                bomb.setTimePlanted(new Date(c.getTimeInMillis()));
                bomb.setPlace(new Place(myLoc.longitude,myLoc.latitude));

                if (position == 0)
                {
                    googleMap.addMarker(new MarkerOptions().position(myLoc)
                            .icon(BitmapDescriptorFactory.fromResource(
                                    R.drawable.mine_mini))
                                    // Specifies the anchor to be at a particular point in the marker image.
                            .anchor(0.5f, 0.5f));
                    bomb.setType("mine");
                    bomb.setPlace(new Place(myLoc.longitude,myLoc.latitude));
                }
                else
                if (position == 1)
                {
                    googleMap.addMarker(new MarkerOptions().position(myLoc)
                            .icon(BitmapDescriptorFactory.fromResource(
                                    R.drawable.bomb_mini))
                                    // Specifies the anchor to be at a particular point in the marker image.
                            .anchor(0.5f, 0.5f));
                    bomb.setType("C4");

                    /*canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                            R.drawable.bomb), 0,0, color);
                    canvas1.drawText("C4!!!", 30, 40, color);
                    googleMap.addMarker(new MarkerOptions().position(myLoc).title("C4")).setIcon(BitmapDescriptorFactory.fromBitmap(bmp));;*/
                }
                user.addBomb(bomb);
                ServerConnection sc=new ServerConnection();
                sc.uploadBomb(bomb);

            }
        });

        customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(child);
        customDialog.show();
    }
    public void buttonTry(View view)
    {}
}
