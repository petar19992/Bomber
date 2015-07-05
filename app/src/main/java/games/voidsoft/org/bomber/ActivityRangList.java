package games.voidsoft.org.bomber;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import games.voidsoft.org.bomber.objects.Friends;
import games.voidsoft.org.bomber.objects.Singleton;


public class ActivityRangList extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_rang_list);
        List<Friends> rangList= Singleton.getInstance().getTop3();
        ImageView imageFirst=(ImageView) findViewById(R.id.imageViewFirstAvatar);
        imageFirst.setImageBitmap(rangList.get(0).getFriendAvatar());
        ImageView imageSecond=(ImageView) findViewById(R.id.imageViewSecondAvatar);
        imageSecond.setImageBitmap(rangList.get(1).getFriendAvatar());
        ImageView imageThird=(ImageView) findViewById(R.id.imageViewThirdAvatar);
        imageThird.setImageBitmap(rangList.get(2).getFriendAvatar());
        TextView usernameFirst=(TextView) findViewById(R.id.textViewFirstUsername);
        usernameFirst.setText(rangList.get(0).getFriendUsername());
        TextView usernameSecond=(TextView) findViewById(R.id.textViewSecondUsername);
        usernameSecond.setText(rangList.get(1).getFriendUsername());
        TextView usernameThird=(TextView) findViewById(R.id.textViewThirdUsername);
        usernameThird.setText(rangList.get(2).getFriendUsername());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_rang_list, menu);
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
}
