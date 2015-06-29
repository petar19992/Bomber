package games.voidsoft.org.bomber;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import games.voidsoft.org.bomber.objects.Bomb;
import games.voidsoft.org.bomber.objects.Place;
import games.voidsoft.org.bomber.objects.Singleton;
import games.voidsoft.org.bomber.objects.User;

//http://programmerguru.com/android-tutorial/how-to-upload-image-to-php-server/#android-app
public class RegistrationActivity extends Activity {

    Bitmap bmp;
    EditText name;
    EditText username;
    EditText email;
    EditText password;
    private MyAsyncTaskInMaps mAuthTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        name=(EditText)findViewById(R.id.editTextname);
        username=(EditText)findViewById(R.id.editTextusername);
        email=(EditText)findViewById(R.id.editTextemail);
        password=(EditText)findViewById(R.id.editTextpassword);
        bmp=BitmapFactory.decodeResource(this.getResources(), R.drawable.defaultavatar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == CAM_REQUEST)
        {
            if(resultCode==RESULT_OK)
            {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                bmp = thumbnail;
            }
            //imgTakenPhoto.setImageBitmap(thumbnail);
        }
        else
        if(requestCode==1)
        {
            if(resultCode==RESULT_OK)
            {
                Uri selectedImageUri = data.getData();
                String[] projection = { MediaStore.MediaColumns.DATA };
                Cursor cursor = managedQuery(selectedImageUri, projection, null, null,null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);

                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                bmp=bm;
            }
        }

    }
    private static final int CAM_REQUEST = 1313;
    public void buttonCapture(View view)
    {
        Intent cameraintent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraintent, CAM_REQUEST);
    }
    public void buttonOK(View view)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byte_arr = stream.toByteArray();
        // Encode Image to String
        String encodedString = Base64.encodeToString(byte_arr, 0);

        String url="http://bomber.voidsoft.in.rs/insertUser.php";
            List<String> parameters=new ArrayList<String>();
            List<String> value=new ArrayList<String>();
            parameters.add("name");
            value.add(name.getText().toString());
            parameters.add("username");
            value.add(username.getText().toString());
            parameters.add("password");
            value.add(password.getText().toString());
            parameters.add("email");
            value.add(email.getText().toString());
            parameters.add("money");
            value.add("100");
            parameters.add("avatar");
            value.add(encodedString);
            mAuthTask=new MyAsyncTaskInMaps(url,parameters,value);
            mAuthTask.execute((Void) null);
    }
    public void buttonGallery(View view)
    {
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"select from gallery"),1);
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
                java.net.URL url = new java.net.URL(urlString);
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
                    if(result.equals("true"))
                    {
                        return true;
                    }
                    else
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
                User user=new User(new ArrayList<Bomb>(), name.getText().toString(),username.getText().toString(),password.getText().toString(),email.getText().toString(), "100", new Place(),new ArrayList<Integer>(), bmp,null);
                Singleton.getInstance().setUser(user);
                Intent mainIntent = new Intent(RegistrationActivity.this,MapsActivity.class);
                RegistrationActivity.this.startActivity(mainIntent);
                RegistrationActivity.this.finish();

            } else {
                Toast.makeText(getApplicationContext(),"Nesto nije u redu",Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected void onCancelled() {

        }
    }
}