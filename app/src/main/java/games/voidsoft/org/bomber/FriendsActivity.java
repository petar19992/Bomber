        package games.voidsoft.org.bomber;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.support.v7.app.ActionBarActivity;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.Window;
        import android.view.View.OnClickListener;
        import android.view.inputmethod.EditorInfo;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

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

        import games.voidsoft.org.bomber.arrayAdapters.ArrayAdapterFriends;
        import games.voidsoft.org.bomber.connection.BluetoothChatService;
        import games.voidsoft.org.bomber.objectItems.ObjectItemFriend;
        import games.voidsoft.org.bomber.objects.Friends;
        import games.voidsoft.org.bomber.objects.Singleton;
        import games.voidsoft.org.bomber.objects.User;
        import games.voidsoft.org.bomber.service.NotificationProperties;


        public class FriendsActivity extends ActionBarActivity {

    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Layout Views
    private TextView mTitle;
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    User user;
    private MyAsyncTaskInMaps mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        setContentView(R.layout.activity_friends);
        user= Singleton.getInstance().getUser();

        ListView lw=(ListView) findViewById(R.id.listView);
        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
                view.setSelected(true);
            }
        });
        try
        {
            List<Friends> friends=Singleton.getInstance().getListOfFriends();
            ObjectItemFriend[] friendsData=new ObjectItemFriend[friends.size()];
            int i=0;
            for(i=0;i<friends.size();i++)
            {
                friendsData[i]=new ObjectItemFriend(friends.get(i).friendID,friends.get(i).getFriendUsername(),friends.get(i).getFriendAvatar());
            }
            ArrayAdapterFriends adapter=new ArrayAdapterFriends(this, R.layout.listviewfriends,friendsData);
            lw.setAdapter(adapter);
        }
        catch (Exception ex)
        {}

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) { setupChat();
            }
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
/*
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);
        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);
        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });
*/
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mChatService.stop();
            //mOutEditText.setText(mOutStringBuffer);
        }
    }

    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
            new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    // If the action is a key-up event on the return key, send the message
                    if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                        String message = view.getText().toString();
                        sendMessage(message);
                    }
                    if(D) Log.i(TAG, "END onEditorAction");
                    return true;
                }
            };

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            /*mTitle.setText("Connected: ");
                            mTitle.append(mConnectedDeviceName);*/
                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                           /* mTitle.setText("connecting...");*/
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            /*mTitle.setText("not connected");*/
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    //String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                    Toast.makeText(getApplicationContext(), readMessage, Toast.LENGTH_LONG).show();
                    String url="http://bomber.voidsoft.in.rs/addFriend.php";
                    List<String> parameters=new ArrayList<String>();
                    List<String> value=new ArrayList<String>();
                    parameters.add("userID1");
                    value.add(String.valueOf(user.getUserID()));
                    parameters.add("userID2");
                    value.add(readMessage);
                    mAuthTask = new MyAsyncTaskInMaps(url, parameters, value);
                    mAuthTask.execute((Void) null);
                    break;
                //Ovaj deo mi je sad mnogo bitan jer mi tako salje neko svoje ime
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mChatService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    //
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Bluetooth was not enabled. Leaving Bluetooth Chat.", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        if(id==R.id.find_friends)
        {
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            return true;
        }
        else if(id==R.id.visible)
        {
            ensureDiscoverable();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    public void buttonRequests(View view)
    {

    }
    public void buttonSend(View view)
    {
        sendMessage(String.valueOf(user.getUserID()));
    }


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
                    URL = url;
                    Parameters = parameters;
                    Value = value;
                    O = null;
                    Flag = false;
                }

                public MyAsyncTaskInMaps(String url, List<String> parameters, List<String> value, Object o) {
                    URL = url;
                    Parameters = parameters;
                    Value = value;
                    O = o;
                    Flag = false;
                }

                //Funkcija koja samo cita URL, bez argumenata za POST i GET metodu
                private String readUrl(String urlString) throws Exception {
                    BufferedReader reader = null;
                    try {
                        java.net.URL url = new java.net.URL(urlString);
                        reader = new BufferedReader(new InputStreamReader(url.openStream()));
                        StringBuffer buffer = new StringBuffer();
                        int read;
                        char[] chars = new char[1024];
                        while ((read = reader.read(chars)) != -1)
                            buffer.append(chars, 0, read);
                        return buffer.toString();
                    } finally {
                        if (reader != null)
                            reader.close();
                    }
                }

                ////POST METODA
                public String POST(String url, List<String> params, List<String> value) {
                    InputStream inputStream = null;
                    String result = "";
                    try {
                        // 1. create HttpClient
                        HttpClient httpclient = new DefaultHttpClient();
                        // 2. make POST request to the given URL
                        HttpPost httpPost = new HttpPost(url);
                        //Deo kada se samo parametri prosledjuju
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                        int i = 0;
                        for (String p : params) {
                            nameValuePairs.add(new BasicNameValuePair(p, value.get(i++)));
                        }
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        HttpResponse httpResponse = httpclient.execute(httpPost);
                        Log.v("Post Status", "Code: " + httpResponse.getStatusLine().getStatusCode());
                        result = String.valueOf(httpResponse.getStatusLine().getStatusCode());

                        inputStream = httpResponse.getEntity().getContent();
                        // 10. convert inputstream to string
                        if (inputStream != null) {
                            result = convertInputStreamToString(inputStream);
                            if (result.equals("true")) {
                                //Ako mi bude vracao boolean ovde cu da stavim return true
                            } else {

                            }
                        } else
                            result = "Did not work!";
                    } catch (Exception e) {
                        Log.d("InputStream", e.getLocalizedMessage());
                    }

                    return result;
                }

                private String convertInputStreamToString(InputStream inputStream) throws IOException {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    String result = "";
                    while ((line = bufferedReader.readLine()) != null)
                        result += line;

                    inputStream.close();
                    return result;

                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    // TODO: attempt authentication against a network service.

                    if (O != null) {
                        try {
                            String json = POST(URL, Parameters, Value);
                            JsonParser parser = new JsonParser();
                            JsonObject obj = parser.parse(json).getAsJsonObject();
                            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                            O = gson.fromJson(obj, O.getClass());
                            return true;
                        } catch (Exception ex) {
                            Log.e("Pribavljanje", "Buffer", ex);
                            return false;
                        }
                    } else {
                        try {

                            String result = POST(URL, Parameters, Value);
                            Result = result;
                            if(Result.equals("true"))
                                return true;
                            else
                                return false;
                        } catch (Exception ex) {
                            Log.e("Pribavljanje", "Buffer", ex);
                            return false;
                        }
                    }

                }

                //Ovde udje nakon izvrsenog zahteva za login
                @Override
                protected void onPostExecute(final Boolean success) {

                    Flag = true;
                    if (success) {
                        Toast.makeText(getApplicationContext(),"postali prijatelji",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"nisu postali prijatelji",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                protected void onCancelled() {

                }
            }
}
