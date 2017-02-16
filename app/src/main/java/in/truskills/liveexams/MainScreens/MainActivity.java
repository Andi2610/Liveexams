package in.truskills.liveexams.MainScreens;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.pkmmte.view.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import in.truskills.liveexams.Miscellaneous.CheckForPermissions;
import in.truskills.liveexams.R;
import in.truskills.liveexams.authentication.Signup_Login;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomeInterface {

    //Declare variables..
    NavigationView navigationView;
    CircularImageView navImage;
    TextView navName, navEmail;
    static final int REQUEST_CAMERA = 1, SELECT_FILE = 1;
    String userChoosenTask, defaultImage;
    Bundle bundle;
    SharedPreferences prefs;
    Bitmap icon;
    CharSequence[] items;
    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set toolbar..
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get shared preferences..
        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        bundle = new Bundle();

        //Initially load Home fragment..
        HomeFragment fragment = new HomeFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.replace(R.id.fragment, fragment, "HomeFragment");
        trans.commit();

        getSupportActionBar().setTitle("HOME");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_home);

        View view = navigationView.getHeaderView(0);
        navImage = (CircularImageView) view.findViewById(R.id.navImage);
        navName = (TextView) view.findViewById(R.id.navName);
        navEmail = (TextView) view.findViewById(R.id.navEmail);

        icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.camera);

        defaultImage = BitmapToString(icon);

        Bitmap myImage = StringToBitmap(prefs.getString("navImage", defaultImage));

        navImage.setImageBitmap(myImage);

        navName.setText(prefs.getString("userName", ""));
        navEmail.setText(prefs.getString("emailAddress", ""));

        Typeface tff=Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");

        navName.setTypeface(tff);
        navEmail.setTypeface(tff);

        navImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Display image chooser..
//                selectImage();
                boolean statusForGallery=CheckForPermissions.checkForGallery(MainActivity.this);
                if(statusForGallery){
                    boolean statusForCamera=CheckForPermissions.checkForCamera(MainActivity.this);
                    if(statusForCamera){
                        //Show options..
                        selectImage();
                    }

                }
            }
        });
    }

    private void selectImage() {

        //If no image set.. do not include "Remove Photo" option
        //Else include "Remove Photo" option..
            if (prefs.getString("navImage", defaultImage).equals(defaultImage)) {
                items = new CharSequence[3];
                items[0] = "Take Photo";
                items[1] = "Choose from Library";
                items[2] = "Cancel";
            } else {
                items = new CharSequence[4];
                items[0] = "Take Photo";
                items[1] = "Choose from Library";
                items[2] = "Remove Photo";
                items[3] = "Cancel";
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Add Photo!");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {

                    if (items[item].equals("Take Photo")) {
                        cameraIntent();

                    } else if (items[item].equals("Choose from Library")) {
                        galleryIntent();

                    } else if (items[item].equals("Remove Photo")) {
                        SharedPreferences.Editor e = prefs.edit();
                        e.putString("navImage", defaultImage);
                        e.apply();
                        navImage.setImageBitmap(icon);
                    } else if (items[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
    }

    //If capture an image from camera is requested..
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    //If choose an image from gallery is requested..
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException
    {
        BitmapFactory.Options o = new BitmapFactory.Options();

        o.inJustDecodeBounds = true;

        ContentResolver cr = this.getContentResolver();

        BitmapFactory.decodeStream(cr.openInputStream(selectedImage) ,null, o);

//        BitmapFactory.decodeStream(getContentResolver()
//                .openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 72;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;

        int scale = 1;

        while (true)
        {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
            {
                break;
            }
            width_tmp /= 2;

            height_tmp /= 2;

            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();

        o2.inSampleSize = scale;

//        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
//                .openInputStream(selectedImage), null, o2);
        ContentResolver cr2 = this.getContentResolver();

        Bitmap bitmap=BitmapFactory.decodeStream(cr2.openInputStream(selectedImage) ,null, o2);

        return bitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CheckForPermissions.STORAGE_PERMISSION_CODE:
                //If permission is granted
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    boolean statusForCamera=CheckForPermissions.checkForCamera(MainActivity.this);
                    if(statusForCamera){
                        //Show options..
                        selectImage();
                    }
                }else{
                    //Displaying another toast if permission is not granted
                    Toast.makeText(this,"Oops you have denied the permission for storage access\nGo to settings and grant them", Toast.LENGTH_LONG).show();
                }
                break;

            case CheckForPermissions.CAMERA_PERMISSION_CODE:
                //If permission is granted
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Show options..
                    selectImage();
                }else{
                    //Displaying another toast if permission is not granted
                    Toast.makeText(this,"Oops you have denied the permission for camera\nGo to settings and grant them", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                try {
                    onCaptureImageResult(data);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
        }else if(requestCode==10){
            FrameLayout frameLayout=(FrameLayout) findViewById(R.id.fragment);
            frameLayout.removeAllViewsInLayout();
            HomeFragment f=new HomeFragment();
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.fragment,f,"HomeFragment");
            fragTransaction.commit();
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
//                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                Uri fileUri = data.getData();
                bm = decodeUri(fileUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String tempString = BitmapToString(bm);
        //Edit shared preference of navImage to currently obtained image..
        SharedPreferences.Editor e = prefs.edit();
        e.putString("navImage", tempString);
        e.apply();
        navImage.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) throws FileNotFoundException {

        Uri fileUri = data.getData();
        Bitmap thumbnail = decodeUri(fileUri);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String tempString = BitmapToString(thumbnail);
        //Edit shared preference of navImage to currently obtained image..
        SharedPreferences.Editor e = prefs.edit();
        e.putString("navImage", tempString);
        e.apply();
        navImage.setImageBitmap(thumbnail);
    }

    @Override
    public void onBackPressed() {

        //If drawer is open, close it..
        //Else If home fragment is loaded, then exit from the app..
        //Else If any fragment other than home fragment is visible, then load home fragment..

        manager = getSupportFragmentManager();
        HomeFragment f = (HomeFragment) manager.findFragmentByTag("HomeFragment");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (f != null && f.isVisible()) {
            finish();
        } else {
            HomeFragment fragment = new HomeFragment();
            FragmentTransaction t = manager.beginTransaction();
            t.replace(R.id.fragment, fragment, "HomeFragment");
            t.commit();
            navigationView.setCheckedItem(R.id.nav_home);
            getSupportActionBar().setTitle("HOME");

        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        //Load the desired fragment only when they are not initially visible..

        manager = getSupportFragmentManager();

        if (id == R.id.nav_home) {
            HomeFragment f = (HomeFragment) manager.findFragmentByTag("HomeFragment");
            if (!(f != null && f.isVisible())) {
                HomeFragment fragment = new HomeFragment();
                FragmentTransaction t = manager.beginTransaction();
                t.replace(R.id.fragment, fragment, "HomeFragment");
                t.commit();
                navigationView.setCheckedItem(R.id.nav_home);
                getSupportActionBar().setTitle("HOME");
            }
        } else if (id == R.id.nav_calendar) {
            CalendarFragment f = (CalendarFragment) manager.findFragmentByTag("CalendarFragment");
            if (!(f != null && f.isVisible())) {

                CalendarFragment fragment = new CalendarFragment();
                FragmentTransaction t = manager.beginTransaction();
                t.replace(R.id.fragment, fragment, "CalendarFragment");
                t.commit();
                navigationView.setCheckedItem(R.id.nav_calendar);
                getSupportActionBar().setTitle("CALENDAR");
            }
        } else if (id == R.id.nav_logout) {
            Answers.getInstance().logCustom(new CustomEvent("Logout button clicked")
                    .putCustomAttribute("userName",prefs.getString("userName","")));
            SharedPreferences.Editor e = prefs.edit();
            e.clear();
            e.apply();
            Intent i = new Intent(MainActivity.this, Signup_Login.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //A method to convert bitmap to string..
    public Bitmap StringToBitmap(String string) {
        try {
            byte[] encodeByte = Base64.decode(string, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //A method to convert string to bitmap..
    public String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    //Whenever a new fragment is loaded, when home fragment is initially visible..
    @Override
    public void changeFragmentFromHome(Fragment f) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction t = manager.beginTransaction();
        t.replace(R.id.fragment, f, "AllExamsFragment");
        t.commit();
        String title = "ADD NEW EXAMS";
        getSupportActionBar().setTitle(title);
    }

}
