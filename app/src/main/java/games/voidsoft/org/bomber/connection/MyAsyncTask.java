package games.voidsoft.org.bomber.connection;

import android.os.AsyncTask;
import android.util.Log;
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

import games.voidsoft.org.bomber.objects.Singleton;
import games.voidsoft.org.bomber.objects.User;

/**
 * Created by Petar on 6/22/2015.
 */
public class MyAsyncTask extends AsyncTask<Void, Void, Boolean> {



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



    public MyAsyncTask(String url, List<String> parameters, List<String> value) {
        URL=url;
        Parameters=parameters;
        Value=value;
        O=null;
        Flag=false;
    }
    public MyAsyncTask(String url, List<String> parameters, List<String> value,Object o) {
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
            //finish();

        } else {

        }
    }

    @Override
    protected void onCancelled() {

    }
}


