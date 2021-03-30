package com.stanbicagent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OpenAccUpPicActivity extends BaseActivity implements View.OnClickListener {
    File finalFile;
    int REQUEST_CAMERA = 3293;
    Button sigin, next;
    int fcsizes = 0;
    TextView gendisp;
    SessionManagement session;
    EditText idno, mobno, fnam, lnam, yob;
    List<String> planetsList = new ArrayList<String>();
    List<String> prodid = new ArrayList<String>();
    ArrayAdapter<String> mArrayAdapter;
    Spinner sp1, sp2, sp5, sp3, sp4;
    Button btn4;
    static Hashtable<String, String> data1;
    String paramdata = "";
    ProgressDialog prgDialog, prgDialog2, prgDialog7;
    TextView tnc;
    List<String> mobopname = new ArrayList<String>();
    List<String> mobopid = new ArrayList<String>();

    TextView tvdate;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    boolean uploadpic = false;
    public static final String DATEPICKER_TAG = "datepicker";
    ImageView img;
    String strfname, strlname, strmidnm, stryob, stremail, strhmdd, strmobn, strsalut, strmarst, strcity, strstate, strgender, straddr;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    TextView step2, step1;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_acc_up_pic);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)


        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);

        sigin = (Button) findViewById(R.id.button1);
        sigin.setOnClickListener(this);
        next = (Button) findViewById(R.id.buttonnxt);
        next.setOnClickListener(this);
        img = (ImageView) findViewById(R.id.imgview);

        session = new SessionManagement(this);

        Intent intent = getIntent();
        if (intent != null) {
            strfname = intent.getStringExtra("fname");
            strlname = intent.getStringExtra("lname");
            strmidnm = intent.getStringExtra("midname");
            stryob = intent.getStringExtra("yob");
            stremail = intent.getStringExtra("email");
            strhmdd = intent.getStringExtra("hmadd");
            strmobn = intent.getStringExtra("mobn");
            strsalut = intent.getStringExtra("salut");
            strmarst = intent.getStringExtra("marstatus");
            strcity = intent.getStringExtra("city");
            strstate = intent.getStringExtra("state");
            strgender = intent.getStringExtra("gender");
            straddr = intent.getStringExtra("straddr");
        }

        step2 = (TextView) findViewById(R.id.tv2);
        step2.setOnClickListener(this);

        step1 = (TextView) findViewById(R.id.tv);
        step1.setOnClickListener(this);


    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == REQUEST_CAMERA) {
                if (data != null) {
                    onCaptureImageResult(data);
                }
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }

        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float neww = ((float) width) * ((float) 0.6);
        float newh = ((float) height) * ((float) 0.6);
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }

  /*  private void runFaceContourDetection(final Bitmap myBitmap, Bitmap origbit) {

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(origbit);
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .build();

        //   mFaceButton.setEnabled(false);
        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
        detector.detectInImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<List<FirebaseVisionFace>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionFace> faces) {

                                int facesizes = faces.size();
                                fcsizes = facesizes;
                                Log.v("Faces found", "There are" + Integer.toString(facesizes) + " faces here");

                                if (fcsizes > 0) {
                                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                                    String filename = System.currentTimeMillis() + ".jpg";

                                    final File path = new File(getFilesDir(), "FirstAgent");

                                    // Make sure the path directory exists.
                                    if (!path.exists()) {
                                        // Make it, if it doesn't exit
                                        path.mkdirs();
                                        Log.v("was it crated", "created");
                                    }
                                    finalFile = new File(getFilesDir(), "FirstAgent/" + filename);
                                    FileOutputStream fo;
                                    try {
                                        finalFile.createNewFile();
                                        fo = new FileOutputStream(finalFile);
                                        fo.write(bytes.toByteArray());
                                        fo.close();
                                        SecurityLayer.Log("Filename stored", filename);
                                        String filePath = finalFile.getPath();
                                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                                        if (img != null) {
                                            img.setImageBitmap(bitmap);
                                        }
                                        session.setString("CUSTIMGFILEPATH", filePath);
                                        uploadpic = true;
           *//* new Thread(new Runnable() {
                public void run() {


                    uploadFile(finalFile);

                }
            }).start();*//*
                                        //    new FragmentDrawer.AsyncUplImg().execute();
           *//* getApplicationContext().finish();
            Toast.makeText(
                    getApplicationContext(),
                    "Image Set Successfully",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(),FMobActivity.class));*//*
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    //   iv.setImageBitmap(thumbnail);

                                } else {
                                    img.setImageBitmap(myBitmap);
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Please ensure you have taken a clear picture of the customer's face"
                                            , Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception

                                e.printStackTrace();

                                Toast.makeText(
                                        getApplicationContext(),
                                        "There was an error running facial detection on the image.Please retake the photo again"
                                        , Toast.LENGTH_LONG).show();
                            }
                        });

    }*/

    private void onCaptureImageResult(Intent data) {
        if (!(data == null)) {

            Bitmap origbit = (Bitmap) data.getExtras().get("data");
            int srcWidth = origbit.getWidth();
            int srcHeight = origbit.getHeight();
            int dstWidth = (int) (srcWidth * 0.8f);
            int dstHeight = (int) (srcHeight * 0.8f);
            Bitmap thumbnail = getResizedBitmap((Bitmap) data.getExtras().get("data"), dstHeight, dstWidth);
          //  runFaceContourDetection(thumbnail, origbit);
            if (img != null) {
                img.setImageBitmap(origbit);
            }
            finalizeup(thumbnail);

        }

    }

    private void finalizeup(Bitmap myBitmap) {


    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    myBitmap.compress(Bitmap.CompressFormat.JPEG,90,bytes);
    String filename = System.currentTimeMillis() + ".jpg";

    final File path = new File(Environment.getExternalStorageDirectory(), "FirstAgent");

    // Make sure the path directory exists.
    if(!path.exists())

    {
        // Make it, if it doesn't exit
        path.mkdirs();
        Log.v("was it crated", "created");
    }

    finalFile = new File(Environment.getExternalStorageDirectory(), "FirstAgent/"+filename);
    FileOutputStream fo;
                                    try

    {
        finalFile.createNewFile();
        fo = new FileOutputStream(finalFile);
        fo.write(bytes.toByteArray());
        fo.close();
        SecurityLayer.Log("Filename stored", filename);
        String filePath = finalFile.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        session.setString("CUSTIMGFILEPATH", filePath);
        uploadpic = true;
           /* new Thread(new Runnable() {
                public void run() {


                    uploadFile(finalFile);

                }
            }).start();*/
        //    new FragmentDrawer.AsyncUplImg().execute();
           /* getApplicationContext().finish();
            Toast.makeText(
                    getApplicationContext(),
                    "Image Set Successfully",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(),FMobActivity.class));*/
    } catch(
    FileNotFoundException e)

    {
        e.printStackTrace();
    } catch(
    IOException e)

    {
        e.printStackTrace();
    }

    //   iv.setImageBitmap(thumbnail);
}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button1) {

           /* Fragment  fragment = new OpenAccStepThree();


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Step Three");
            fragmentTransaction.addToBackStack("Step Three");
            ((FMobActivity)getApplicationContext())
                    .setActionBarTitle("Step Three");
            fragmentTransaction.commit();*/
           fcsizes = 0;
           uploadpic = false;
            boolean camresult= checkCameraPermission(OpenAccUpPicActivity.this);
            if(camresult) {
                boolean result=checkPermission(OpenAccUpPicActivity.this);
                if(result) {
                    boolean readresult=Utility.checkWritePermission(OpenAccUpPicActivity.this);
                    if(readresult) {
                        //  dispatchTakePictureIntent();
                        cameraIntent();
                    }
                }
            }
        }
        if (view.getId() == R.id.tv2) {
           /* Bundle bundle = new Bundle();
            bundle.putString("fname", strfname);
            bundle.putString("lname", strlname);
            bundle.putString("midname", strmidnm);
            bundle.putString("yob", stryob);
            bundle.putString("gender", strgender);
            bundle.putString("city", strcity);
            bundle.putString("state", strstate);
            Fragment fragment = new OpenAcc();

            fragment.setArguments(bundle);


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Biller Menu");
            fragmentTransaction.addToBackStack("Biller Menu");
            ((FMobActivity)getApplicationContext())
                    .setActionBarTitle("Biller Menu");
            fragmentTransaction.commit();*/


            finish();
            Intent intent  = new Intent(OpenAccUpPicActivity.this,OpenAccActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
        if (view.getId() == R.id.tv) {
           /* Bundle bundle = new Bundle();
            bundle.putString("fname", strfname);
            bundle.putString("lname", strlname);
            bundle.putString("midname", strmidnm);
            bundle.putString("gender", strgender);
            bundle.putString("city", strcity);
            bundle.putString("state", strstate);
            bundle.putString("yob", stryob);
            bundle.putString("email", stremail);
            bundle.putString("hmadd", strhmdd);
            bundle.putString("mobn", strmobn);
            bundle.putString("salut", strsalut);
            bundle.putString("marstatus", strmarst);
            Fragment  fragment = new OpenAccStepTwo();
            fragment.setArguments(bundle);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment,"Biller Menu");
            fragmentTransaction.addToBackStack("Biller Menu");
            ((FMobActivity)getApplicationContext())
                    .setActionBarTitle("Biller Menu");
            fragmentTransaction.commit();*/


            finish();
            Intent intent  = new Intent(OpenAccUpPicActivity.this,OpenAccStepTwoActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }



        if(view.getId()==  R.id.button4){

        }
        if(view.getId() == R.id.buttonnxt){
            if(uploadpic) {

                Intent intent  = new Intent(OpenAccUpPicActivity.this,OpenAccCustPicActivity.class);





                intent.putExtra("fname", strfname);
                intent.putExtra("lname", strlname);
                intent.putExtra("midname", strmidnm);
                intent.putExtra("yob", stryob);
                intent.putExtra("email", stremail);
                intent.putExtra("hmadd", strhmdd);
                intent.putExtra("mobn", strmobn);
                intent.putExtra("salut", strsalut);
                intent.putExtra("marstatus", strmarst);

                intent.putExtra("gender", strgender);
                intent.putExtra("city", strcity);
                intent.putExtra("state", strstate);

                intent.putExtra("straddr", straddr);

                startActivity(intent);
               /* Bundle bundle = new Bundle();
                bundle.putString("fname", strfname);
                bundle.putString("lname", strlname);
                bundle.putString("midname", strmidnm);
                bundle.putString("yob", stryob);
                bundle.putString("email", stremail);
                bundle.putString("hmadd", strhmdd);
                bundle.putString("mobn", strmobn);
                bundle.putString("salut", strsalut);
                bundle.putString("marstatus", strmarst);

                bundle.putString("gender", strgender);
                bundle.putString("city", strcity);
                bundle.putString("state", strstate);
                Fragment fragment = new OpenAccCustPic();
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment, "Step Four");
                fragmentTransaction.addToBackStack("Step Four");
                ((FMobActivity) getApplicationContext())
                        .setActionBarTitle("Step Four");
                fragmentTransaction.commit();*/
            }else{
                Toast.makeText(
                        getApplicationContext(),
                        "Please upload customer picture to proceed",
                        Toast.LENGTH_LONG).show();
            }
        }

        if(view.getId() == R.id.textView3){

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public  boolean checkCameraPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.CAMERA)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Permission to use camera is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            boolean camresult= checkCameraPermission(context);
                            if(camresult) {
                                boolean result=checkPermission(context);
                                if(result) {
                                    //  dispatchTakePictureIntent();
                                    cameraIntent();
                                }
                            }
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public  boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            boolean camresult= checkCameraPermission(context);
                            if(camresult) {
                                boolean result=checkPermission(context);
                                if(result) {
                                    //  dispatchTakePictureIntent();
                                    cameraIntent();
                                }
                            }
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void uploadprocess(){
        boolean camresult= Utility.checkCameraPermission(OpenAccUpPicActivity.this);
        if(camresult) {
            boolean result=Utility.checkPermission(OpenAccUpPicActivity.this);
            if(result) {
                //  dispatchTakePictureIntent();
                cameraIntent();
            }
        }
    }
    private void cameraIntent()
    {
      /*  String defaultCameraPackage = null;

        List<ApplicationInfo> list = getApplicationContext().getPackageManager().getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (int n=0;n<list.size();n++) {
            if((list.get(n).flags & ApplicationInfo.FLAG_SYSTEM)==1)
            {
                SecurityLayer.Log("TAG", "Installed Applications  : " + list.get(n).loadLabel( getApplicationContext().getPackageManager()).toString());
                SecurityLayer.Log("TAG", "package name  : " + list.get(n).packageName);
                if(list.get(n).loadLabel( getApplicationContext().getPackageManager()).toString().equalsIgnoreCase("Camera")) {
                    defaultCameraPackage = list.get(n).packageName;
                    break;
                }
            }
        }*/

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //  intent.setPackage(defaultCameraPackage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);

        }
    }
    /* private void captureImage() {
         Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

         fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

         intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

         // start the image capture Intent
         startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
     }
     @Override
     public void onSaveInstanceState(Bundle outState) {
         super.onSaveInstanceState(outState);

         // save file url in bundle as it will be null on scren orientation
         // changes
         outState.putParcelable("file_uri", fileUri);
     }
 */
   /* @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }*/
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                SecurityLayer.Log(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            //	new SendTask().execute();
            registerUser();
            //	RegTest();
            return true;
        } else {

            Toast.makeText(
                    getApplicationContext(),
                    "No Internet Connection. Please check your internet setttings",
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }
    public void registerUser(){
        final   String acty = "01261";
        //  final String prodn = sp1.getSelectedItem().toString();

        String mobnumm = mobno.getText().toString().trim();
        final  String idn = idno.getText().toString().trim();
        final  String fname = fnam.getText().toString().trim();
        final  String lname = lnam.getText().toString().trim();
        final  String yb  = yob.getText().toString().trim();
        final String nt = sp2.getSelectedItem().toString();
        final String    mobnum = setMobFormat(mobnumm);
        SecurityLayer.Log("Mobile Number Formatted",mobnum);
        if( Utility.isNotNull(mobnum)){
            if( Utility.isNotNull(idn)){
                if( Utility.isNotNull(fname)){

                    if( Utility.isNotNull(lname)){
                        if( Utility.isNotNull(yb)){
                            if(Utility.checknum(mobnum) == true) {
                                if(Utility.checknum(yb) == true) {
                                    if(yb.length() == 4) {
                                        if(!(mobnum.equals("N"))) {

                                            if( Utility.isValidWord(fname)){
                                                if( Utility.isValidWord(lname)){
                                                    if( Utility.isValidWord(idn)){
                                                        new MaterialDialog.Builder(getApplicationContext())
                                                                .title("Open Account")
                                                                .content("Are you sure you want to Open an Account with these particulars? \n First Name: "+fname+"  \n  Last Name "+ lname+" \n Mobile Number  "+mobnum+" \n Mobile Operator "+nt+" \n ID Number: "+idn+" \n Year Of Birth  "+yb+"  ")
                                                                .positiveText("YES")
                                                                .negativeText("NO")

                                                                .callback(new MaterialDialog.ButtonCallback() {
                                                                    @Override
                                                                    public void onPositive(MaterialDialog dialog) {
                                                                        invokeWS(acty, mobnum, idn, fname, lname, yb);
                                                                    }

                                                                    @Override
                                                                    public void onNegative(MaterialDialog dialog) {

                                                                    }
                                                                })
                                                                .show();

                                                    }else{
                                                        Toast.makeText(getApplicationContext(), "Please enter a valid Id Number/Passport", Toast.LENGTH_LONG).show();

                                                    }
                                                }else{
                                                    Toast.makeText(getApplicationContext(), "Please enter a valid Last Name", Toast.LENGTH_LONG).show();

                                                }
                                            }else{
                                                Toast.makeText(getApplicationContext(), "Please enter a valid First Name", Toast.LENGTH_LONG).show();

                                            }


                                        }else{
                                            Toast.makeText(getApplicationContext(), "Please enter a valid mobile number", Toast.LENGTH_LONG).show();

                                        }
                                    }else{
                                        Toast.makeText(getApplicationContext(), "The Year Of Birth field should only contain numeric characters. Please fill in appropiately", Toast.LENGTH_LONG).show();
                                    }}else{
                                    Toast.makeText(getApplicationContext(), "The Mobile Number field should only contain numeric characters. Please fill in appropiately", Toast.LENGTH_LONG).show();
                                }}else{
                                Toast.makeText(getApplicationContext(), "The Year Of Birth field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();

                            }}else{
                            Toast.makeText(getApplicationContext(), "The Last Name  field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();

                        }}else{
                        Toast.makeText(getApplicationContext(), "The First Name field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();

                    }}else{
                    Toast.makeText(getApplicationContext(), "The ID Number field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();
                }}else{
                Toast.makeText(getApplicationContext(), "The ID No/Passport  field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();


            }}else{
            Toast.makeText(getApplicationContext(), "The Mobile Number field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();

        }
    }
    public void invokeWS( String acctype,String msisdn,String id,String fname,String lname,String yearob){
        // Show Progress Dialog
        prgDialog.show();

        // Make RESTful webservice call using AsyncHttpClient object
        final AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(35000);
        HashMap<String, String> nurl = session.getNetURL();
        String newurl = nurl.get(SessionManagement.NETWORK_URL);
        client.setURLEncodingEnabled(true);


        HashMap<String, String> stuse = session.getDisp();
        String username = stuse.get(SessionManagement.KEY_DISP);
        String url =   ApplicationConstants.NET_URL+ApplicationConstants.AND_ENPOINT+"agencyopenAccount/1/01261/"+ msisdn+"/1/"+id+"/"+fname+"/"+lname+"/"+yearob+"/ANDROID/"+username;

        SecurityLayer.Log("Open Acc URL",url);

        client.post(url,new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                prgDialog.hide();
                try {
                    // JSON Object
                    SecurityLayer.Log("response..:", response);
                    JSONObject obj = new JSONObject(response);


                    String rpcode = obj.optString("responsecode");
                    String rsmesaage = obj.optString("responsemessage");
                    String fname = obj.optString("fullname");
                    String mno = obj.optString("mobilenumber");
                    SecurityLayer.Log("Response Code", rsmesaage);
                    if (rpcode.equals("00")) {

                    } else {


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    SetDialog(" The device has not successfully connected to server. Please check your internet settings","Check Settings");
                    e.printStackTrace();

                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {

                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    SetDialog(" The device has not successfully connected to server. Please check your internet settings","Check Settings");
                }
            }
        });
    }

    public void ClearOpenAcc(){
        //   sp1.setSelection(0);

        mobno.setText(" ");
        idno.setText(" ");
        fnam.setText(" ");
        lnam.setText(" ");
        //   yob.setText(" ");
    }


   

    public void SetDialog(String msg,String title){
        new MaterialDialog.Builder(this)
                .title(title)
                .content(msg)

                .negativeText("Close")
                .show();
    }

    public String setMobFormat(String mobno){
        String vb = mobno.substring(Math.max(0, mobno.length() - 9));
        SecurityLayer.Log("Logged Number is", vb);
        if(vb.length() == 9 && (vb.substring(0, Math.min(mobno.length(), 1)).equals("7"))){
            return "254"+vb;
        }else{
            return  "N";
        }
    }

}
