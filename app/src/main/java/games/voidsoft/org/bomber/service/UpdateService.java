package games.voidsoft.org.bomber.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Petar on 5/3/2015.
 */
public final class UpdateService extends Service {

    public static final String ACTION_UPDATE = "games.voidsoft.org.bomber.action.UPDATE";
    private final static IntentFilter sIntentFilter;

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mTimeChangedReceiver);
        Toast.makeText(getApplicationContext(), "Ugasio se", Toast.LENGTH_LONG).show();
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

    private final BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_TIMEZONE_CHANGED))
            {
                Toast.makeText(context,"Radi",Toast.LENGTH_LONG).show();
            }
            if(action.equals(Intent.ACTION_TIME_TICK))
            {
                Toast.makeText(context,"Radi",Toast.LENGTH_LONG).show();
                sendMessage();
                //makeText();
            }
            else
            {}
        }
    };

}
