package games.voidsoft.org.bomber.connection;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import games.voidsoft.org.bomber.objects.*;

/**
 * Created by Petar on 4/27/2015.
 */
public class ServerConnection {

    static String url="http://192.168.1.6:9000/test";
    private HttpAsyncTask mAuthTask = null;

    public void uploadBomb(Bomb bomb)
    {
        Gson gson=new Gson();
        String json="";
        /*try {
            json = gson.toJson(bomb);
        }
        catch (Exception ex)
        {
            Log.e("","",ex);
        }*/
        mAuthTask= new HttpAsyncTask("http://192.168.1.6:9000/traffic/login.php"/*,json*/);
        mAuthTask.execute();

    }
    public static String POST(String url/*, String json*/){
        InputStream inputStream = null;
        String result = "";
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);



//Deo kada se samo parametri prosledjuju
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("username", "dsa"));
            nameValuePairs.add(new BasicNameValuePair("password", "dsa"));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpclient.execute(httpPost);
            Log.v("Post Status","Code: "+httpResponse.getStatusLine().getStatusCode());
            result=String.valueOf(httpResponse.getStatusLine().getStatusCode());


//Deo kada se prosledjuje ceo JSON
           /* String json = "";
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("username", "dsa");
            jsonObject.accumulate("password", "dsa");
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();




            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);
            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);
            // 6. set httpPost Entity
            httpPost.setEntity(se);
            // 7. Set some headers to inform server about the type of the content*/
            /*httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");*/
            // 8. Execute POST request to the given URL
            /*
            HttpResponse httpResponse = httpclient.execute(httpPost);
            // 9. receive response as inputStream
            */
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

        // 11. return result
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<Void, Void, String> {

        String URL;
        HttpAsyncTask(String url)
        {
            this.URL=url;
        }
        @Override
        protected String doInBackground(Void... urls) {
            return POST(this.URL);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(this.getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
            String res=result;
            res+=".";
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}





/*public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }*/
