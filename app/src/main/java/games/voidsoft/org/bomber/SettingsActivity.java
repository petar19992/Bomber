package games.voidsoft.org.bomber;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import games.voidsoft.org.bomber.objects.Singleton;
import games.voidsoft.org.bomber.objects.User;


public class SettingsActivity extends ActionBarActivity {


    public static SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String LatValue="LatKey";
    public static final String LonValue="LonKey";
    public static final String UsernameValue="UsernameKey";
    public static final String UserIDValue="UserIDKey";

    User user;

    EditText name;
    EditText username;
    EditText currentPassword;
    EditText newPassword;
    EditText renewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        user= Singleton.getInstance().getUser();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_APPEND);
        name=(EditText)findViewById(R.id.editTextname);
        username=(EditText)findViewById(R.id.editTextusername);
        currentPassword=(EditText)findViewById(R.id.editTextCurrentPassword);
        newPassword=(EditText)findViewById(R.id.editTextNewPassword);
        //renewPassword=(EditText)findViewById(R.id.textViewRePassword);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    public void buttonSAVE(View view)
    {

    }
}
