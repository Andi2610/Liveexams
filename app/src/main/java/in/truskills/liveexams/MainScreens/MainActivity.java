package in.truskills.liveexams.MainScreens;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
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

import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.pkmmte.view.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import in.truskills.liveexams.Miscellaneous.CheckForPermissions;
import in.truskills.liveexams.Miscellaneous.RealPathUtil;
import in.truskills.liveexams.R;
import in.truskills.liveexams.authentication.Signup_Login;

import static android.R.attr.key;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomeInterface {

    //Declare variables..
    NavigationView navigationView;
    CircularImageView navImage;
    String my_path;
    TextView navName, navEmail;
    static final int REQUEST_CAMERA = 2, SELECT_FILE = 1;
    String defaultImage;
    Bundle bundle;
    SharedPreferences prefs;
    Bitmap icon,MyBitmap;
    CharSequence[] items;
    FragmentManager manager;
    private String selectedImagePath;
    private String filename;
    private final String CAMERA = "camera";
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    String accessKeyId="AKIAIJUGKGFIXTWNTTQA",
    secretAccessKey= "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";
    AWSCredentials credentials;
    AmazonS3 s3client;
    Handler h;
    ProgressDialog dialog;
    public static boolean error=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set toolbar..
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        h=new Handler();

        //Get shared preferences..
        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        s3client = new AmazonS3Client(credentials);
        s3client.setRegion( (com.amazonaws.services.s3.model.Region.AP_Mumbai).toAWSRegion() );

        bundle = new Bundle();

        //Initially load Home fragment..
        HomeFragment fragment = new HomeFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.replace(R.id.fragment, fragment, "HomeFragment");
        trans.commit();

        getSupportActionBar().setTitle("HOME");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
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

//        icon = BitmapFactory.decodeResource(getResources(),
//                R.drawable.ic_add_a_photo_white_24dp);
//
//        savebitmap(ico)
//
//        defaultImage = BitmapToString(icon);


        Bitmap myImage = getBitmap(prefs.getString("navImage",""));

        navImage.setImageBitmap(myImage);

        navName.setText(prefs.getString("userName", ""));
        navEmail.setText(prefs.getString("emailAddress", ""));

        Typeface tff = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");

        navName.setTypeface(tff);
        navEmail.setTypeface(tff);

        navImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Display image chooser..
                boolean statusForCamera = CheckForPermissions.checkForCamera(MainActivity.this);
                if (statusForCamera) {
                    boolean statusForGallery = CheckForPermissions.checkForGallery(MainActivity.this);
                    if (statusForGallery) {
                        selectImage();
                    }
                }
            }
        });
    }

    private void selectImage() {

        //If no image set.. do not include "Remove Photo" option
        //Else include "Remove Photo" option..
        try {
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
                        boolean statusForCamera = CheckForPermissions.checkForCamera(MainActivity.this);
//                        if (statusForCamera) {
//                            Log.d(CAMERA, "permission granted for camera");
                        cameraIntent();
//                        }
                    } else if (items[item].equals("Choose from Library")) {
//                        boolean statusForGallery = CheckForPermissions.checkForGallery(MainActivity.this);
//                        if (statusForGallery) {
                        galleryIntent();
//                        }
                    } else if (items[item].equals("Remove Photo")) {
//                        SharedPreferences.Editor e = prefs.edit();
//                        e.putString("navImage", defaultImage);
//                        e.apply();
                        navImage.setImageBitmap(icon);

                        File f= null;
                        try {
                            f = savebitmap(icon);
                            String myPath=f.getPath();
                            SharedPreferences.Editor e = prefs.edit();
                            e.putString("navImage", myPath);
                            e.apply();


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else if (items[item].equals("Cancel")) {
                        if(dialog!=null)
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        } catch (Exception e) {
            Log.d(CAMERA, e.toString());
        }
    }

    //If capture an image from camera is requested..
    private void cameraIntent() {
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getFile();
        Log.d(CAMERA, "we got file for camera");
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(MainActivity.this, "in.truskills.liveexams.fileprovider", file));
                Log.d(CAMERA, "we got file for camera");
            } else {
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            }

            startActivityForResult(camera_intent, REQUEST_CAMERA);
        } catch (Exception e) {
            Log.d(CAMERA, "camera intent start error" + e.toString());
        }
    }

    private File getFile() {
        File imagePath;
        //implementation will be different according to android version
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            imagePath = new File(getFilesDir(), "images");
            if (!imagePath.exists()) {
                imagePath.mkdir();
            }
            selectedImagePath = imagePath.getAbsolutePath();
        } else {
            imagePath = new File(Environment.getExternalStorageDirectory()+"/camera_app");
            if (!imagePath.exists()) {
                imagePath.mkdir();
            }
            selectedImagePath = Environment.getExternalStorageDirectory()+"/camera_app";
        }
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        File image_file = new File(imagePath, "cam_image" + ts + ".jpeg");
        filename = "cam_image" + ts + ".jpeg";
        return image_file;
    }

    //If choose an image from gallery is requested..
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();

        o.inJustDecodeBounds = true;

        ContentResolver cr = this.getContentResolver();

        BitmapFactory.decodeStream(cr.openInputStream(selectedImage), null, o);

//        BitmapFactory.decodeStream(getContentResolver()
//                .openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 72;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;

        int scale = 1;

        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
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

        Bitmap bitmap = BitmapFactory.decodeStream(cr2.openInputStream(selectedImage), null, o2);

        return bitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CheckForPermissions.STORAGE_PERMISSION_CODE:
                //If permission is granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    galleryIntent();
                    selectImage();
                } else {
                    //Displaying another toast if permission is not granted
                    Toast.makeText(this, "Oops you have denied the permission for storage access\nGo to settings and grant them", Toast.LENGTH_LONG).show();
                }
                break;

            case CheckForPermissions.CAMERA_PERMISSION_CODE:
                //If permission is granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean statusForGallery = CheckForPermissions.checkForGallery(MainActivity.this);
                    if (statusForGallery) {
                        selectImage();
                    }
//                    cameraIntent();
                } else {
                    //Displaying another toast if permission is not granted
                    Toast.makeText(this, "Oops you have denied the permission for camera\nGo to settings and grant them", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Log.d(CAMERA, requestCode + "");
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA) {
                if (resultCode == RESULT_OK) {
                    Log.d(CAMERA, "camera1");
                    my_path = selectedImagePath + "/" + filename;
                    Log.d(CAMERA, my_path + " CAMERA");
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap bitmap = BitmapFactory.decodeFile(my_path, options);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    ExifInterface ei = null;
                    Bitmap myBitmap=null;
                    try {
                        ei = new ExifInterface(my_path);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        Log.d("orientation", "onActivityResult: "+orientation+" "+ExifInterface.ORIENTATION_ROTATE_90+" "+ExifInterface.ORIENTATION_ROTATE_180+" "+ExifInterface.ORIENTATION_ROTATE_270+" "+ExifInterface.ORIENTATION_NORMAL);

                        switch(orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90://6
                                myBitmap=rotateImage(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180://3
                                myBitmap=rotateImage(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270://8
                                myBitmap=rotateImage(bitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL://1
                                myBitmap=bitmap;
                                break;
                            default:myBitmap=bitmap;
                                break;
                        }
                        byte[] b = baos.toByteArray();
                        MyBitmap=myBitmap;

                        uploadImageToServer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                    SharedPreferences.Editor e = prefs.edit();
//                    e.putString("navImage", Base64.encodeToString(b, Base64.DEFAULT));
//                    e.apply();
                } else if (resultCode == RESULT_CANCELED) {
                    Log.d(CAMERA, "User cancelled image capture");
                } else {
                    Log.d(CAMERA, "Sorry! Failed to capture image");
                    // failed to capture image
                }
            }
        } else if (requestCode == 10) {
            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fragment);
            frameLayout.removeAllViewsInLayout();
            HomeFragment f = new HomeFragment();
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.fragment, f, "HomeFragment");
            fragTransaction.commit();
        }

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Log.d("here", "onSelectFromGalleryResult: ");
        Bitmap bm = null;
        if (data != null) {
            try {
//                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                Uri fileUri = data.getData();
                String realPath;
                // SDK < API11
                if (Build.VERSION.SDK_INT < 11)
                    realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

                    // SDK >= 11 && SDK < 19
                else if (Build.VERSION.SDK_INT <= 19)
                    realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());

                    // SDK > 19 (Android 4.4)
                else
                    realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());

                bm = decodeUri(fileUri);

                my_path=realPath;

                Log.d("gallery", "onSelectFromGalleryResult: "+my_path);

                ExifInterface ei = null;
                Bitmap myBitmap=null;
                try {
                    ei = new ExifInterface(realPath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Log.d("orientation", "onActivityResult: "+orientation+" "+ExifInterface.ORIENTATION_ROTATE_90+" "+ExifInterface.ORIENTATION_ROTATE_180+" "+ExifInterface.ORIENTATION_ROTATE_270+" "+ExifInterface.ORIENTATION_NORMAL);

                    switch(orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90://6
                            myBitmap=rotateImage(bm, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180://3
                            myBitmap=rotateImage(bm, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270://8
                            myBitmap=rotateImage(bm, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL://1
                            myBitmap=bm;
                            break;
                        default:myBitmap=bm;
                            break;
                    }
//                    navImage.setImageBitmap(myBitmap);
                    MyBitmap=myBitmap;
                    uploadImageToServer();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        String tempString = BitmapToString(bm);
//        //Edit shared preference of navImage to currently obtained image..
//        SharedPreferences.Editor e = prefs.edit();
//        e.putString("navImage", tempString);
//        e.apply();
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
                    .putCustomAttribute("userName", prefs.getString("userName", "")));
            SharedPreferences.Editor e = prefs.edit();
            e.clear();
            e.apply();

            String folder_main = "LiveExamsProfileImage";
            File f = new File(Environment.getExternalStorageDirectory(), folder_main);
            if (f.exists()) {
                deleteDir(f);
            }

            String folder_main2 = "LiveExams";
            File f2 = new File(Environment.getExternalStorageDirectory(), folder_main2);
            if (f2.exists()) {
                deleteDir(f2);
            }

            Intent i = new Intent(MainActivity.this, Signup_Login.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_statistics) {
            StatisticsFragment f = (StatisticsFragment) manager.findFragmentByTag("StatisticsFragment");
            if (!(f != null && f.isVisible())) {

                StatisticsFragment fragment = new StatisticsFragment();
                FragmentTransaction t = manager.beginTransaction();
                t.replace(R.id.fragment, fragment, "StatisticsFragment");
                t.commit();
                navigationView.setCheckedItem(R.id.nav_statistics);
                getSupportActionBar().setTitle("STATISTICS");
            }
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
//            bitmap.recycle();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void releaseBitmap(Bitmap mDrawBitmap) {
        if (mDrawBitmap != null) {
            mDrawBitmap.recycle();
            mDrawBitmap = null;
        }
    }

    //A method to convert string to bitmap..
    public String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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

    // capture image orientation

    public void uploadImageToServer(){


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                try{
//
////                dialog = new ProgressDialog(MainActivity.this);
////                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
////                dialog.setMessage("Loading.. Please wait...");
////                dialog.setIndeterminate(true);
////                dialog.setCancelable(false);
//
//                    System.setProperty(SDKGlobalConfiguration.ENFORCE_S3_SIGV4_SYSTEM_PROPERTY, "true");
////                s3client.
//                    // upload file to folder and set it to public
//                    String fileName = "Users/" + prefs.getString("userId","");
//                    s3client.putObject(new PutObjectRequest("live-exams", fileName,
////                        new File(Environment.getExternalStorageDirectory()+"/DCIM/Cymera2/CYMERA_20170316_125312.jpg"))
//                            new File(my_path))
//                            .withCannedAcl(CannedAccessControlList.PublicRead));
////                dialog.dismiss();
//
////                runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            navImage.setImageBitmap(MyBitmap);
////                        }
////                    });
//
//                }catch(Exception e){
//                    Log.d("exception",e.toString());
////                    dialog.dismiss();
////                    runOnUiThread(new Runnable() {
////                                @Override
////                                public void run() {
////                                    Toast.makeText(MainActivity.this, "Sorry! No internet connection..\nPlease try again..", Toast.LENGTH_SHORT).show();
////                                }
////                            });
//                }
//            }
//        }).start();

        AsyncTaskRunner async = new AsyncTaskRunner();
        async.execute();

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {



        @Override
        protected String doInBackground(String... params) {
            try{

                System.setProperty(SDKGlobalConfiguration.ENFORCE_S3_SIGV4_SYSTEM_PROPERTY, "true");
//                s3client.
                // upload file to folder and set it to public
                String fileName = "Users/" + prefs.getString("userId","");
                s3client.putObject(new PutObjectRequest("live-exams", fileName,
                        new File(my_path))
                        .withCannedAcl(CannedAccessControlList.PublicRead));

            }catch (Exception e){
                error=true;
            }
            return "done";
        }


        @Override
        protected void onPostExecute(String result) {

          if(error){
              Toast.makeText(MainActivity.this, "Sorry! Couldn't update your profile pic..", Toast.LENGTH_SHORT).show();
              dialog.dismiss();

          }else{
             navImage.setImageBitmap(MyBitmap);
              try {
                  File f=savebitmap(MyBitmap);
                  String myPath=f.getPath();

                  Log.d("myPath", "onResponse: "+myPath);

                  SharedPreferences.Editor e = prefs.edit();
                  e.putString("navImage", myPath);
                  e.apply();

              } catch (Exception e) {
                  e.printStackTrace();
              }
              dialog.dismiss();

          }

        }


        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Updating your profile pic.. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
          dialog.show();
        }
    }

    public static File savebitmap(Bitmap bmp) throws Exception {

        String folder_main = "LiveExamsProfileImage";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);

        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }

        String pp = f.getAbsolutePath();
        File file = new File(pp
                + File.separator + "profileImage.jpg");
        file.createNewFile();
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(bytes.toByteArray());
        fo.close();

        return f;
    }

    public Bitmap getBitmap(String myPath){

        File imgFile = new  File(myPath);

        if(imgFile.exists()){

            File f[]=imgFile.listFiles();

            Bitmap myBitmap = BitmapFactory.decodeFile(f[0].getPath());

            Log.d("imagePath", "getBitmap: "+imgFile.getPath());

            return myBitmap;
        }

        return null;

    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

}
