package games.voidsoft.org.bomber;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

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
        User user=new User(new ArrayList<Bomb>(), name.getText().toString(),username.getText().toString(),password.getText().toString(),email.getText().toString(), "100", new Place(),new ArrayList<User>(), bmp);
        Singleton.getInstance().setUser(user);
        Intent mainIntent = new Intent(RegistrationActivity.this,MapsActivity.class);
        RegistrationActivity.this.startActivity(mainIntent);
        RegistrationActivity.this.finish();
    }
    public void buttonGallery(View view)
    {
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"select from gallery"),1);
    }
}