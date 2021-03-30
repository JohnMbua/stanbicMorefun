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
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.app.Activity.RESULT_CANCELED;


public class OpenAccUpPic extends Fragment implements View.OnClickListener{
    File finalFile;
    int REQUEST_CAMERA =3293;
    Button sigin,next;
    TextView gendisp;
    SessionManagement session;
    EditText idno,mobno,fnam,lnam,yob;
    List<String> planetsList = new ArrayList<String>();
    List<String> prodid = new ArrayList<String>();
    ArrayAdapter<String> mArrayAdapter;
    Spinner sp1,sp2,sp5,sp3,sp4;
    Button btn4;
    static Hashtable<String, String> data1;
    String paramdata = "";
    ProgressDialog prgDialog,prgDialog2,prgDialog7;
    TextView tnc;
    List<String> mobopname  = new ArrayList<String>();
    List<String> mobopid  = new ArrayList<String>();

    TextView tvdate;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
boolean uploadpic = false;
    public static final String DATEPICKER_TAG = "datepicker";
    ImageView img;
    String strfname,strlname,strmidnm,stryob,stremail,strhmdd,strmobn,strsalut,strmarst,strcity,strstate,strgender;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
TextView step2,step1;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

    private Uri fileUri;
    public OpenAccUpPic() {
        // Required empty public constructor
    }
  /*  private static Fragment newInstance(Context context) {
        LayoutOne f = new LayoutOne();

        return f;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.openaccuplpic, null);
        sigin = (Button) root.findViewById(R.id.button1);
        sigin.setOnClickListener(this);
        next = (Button) root.findViewById(R.id.buttonnxt);
        next.setOnClickListener(this);
        img = (ImageView) root.findViewById(R.id.imgview);

        session = new SessionManagement(getActivity());
        strfname = getArguments().getString("fname");
        strlname = getArguments().getString("lname");
        strmidnm = getArguments().getString("midname");
        stryob = getArguments().getString("yob");
        stremail = getArguments().getString("email");
        strhmdd = getArguments().getString("hmadd");
        strmobn = getArguments().getString("mobn");
        strsalut = getArguments().getString("salut");
        strmarst = getArguments().getString("marstatus");
        strcity = getArguments().getString("city");
        strstate = getArguments().getString("state");
        strgender = getArguments().getString("gender");


        step2 = (TextView) root.findViewById(R.id.tv2);
        step2.setOnClickListener(this);

        step1 = (TextView) root.findViewById(R.id.tv);
        step1.setOnClickListener(this);
        return root;
    }



    public void StartChartAct(int i){


    }
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED){
             if (requestCode == REQUEST_CAMERA) {
                 if(data != null) {
                     onCaptureImageResult(data);
                 }
             }
             }
        }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
              ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
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
        float neww = ((float)width)*((float)0.6);
        float newh = ((float)height)*((float)0.6);
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

    private void onCaptureImageResult(Intent data) {
        if(!(data == null)) {
            Bitmap thumbnail = getResizedBitmap((Bitmap) data.getExtras().get("data"), 150, 150);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            String filename = System.currentTimeMillis() + ".jpg";

            final File path =
                    Environment.getExternalStoragePublicDirectory
                            (
                                    //Environment.DIRECTORY_PICTURES
                                    "/FirstAgent/"
                            );

            // Make sure the path directory exists.
            if(!path.exists())
            {
                // Make it, if it doesn't exit
                path.mkdirs();
            }
            finalFile = new File(Environment.getExternalStorageDirectory(), "/FirstAgent/"+filename);
            FileOutputStream fo;
            try {
                finalFile.createNewFile();
                fo = new FileOutputStream(finalFile);
                fo.write(bytes.toByteArray());
                fo.close();
                SecurityLayer.Log("Filename stored", filename);
                String filePath = finalFile.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                if(img != null) {
                    img.setImageBitmap(bitmap);
                }
                session.setString("CUSTIMGFILEPATH", filePath);
                uploadpic = true;
           /* new Thread(new Runnable() {
                public void run() {


                    uploadFile(finalFile);

                }
            }).start();*/
                //    new FragmentDrawer.AsyncUplImg().execute();
           /* getActivity().finish();
            Toast.makeText(
                    getActivity(),
                    "Image Set Successfully",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(),FMobActivity.class));*/
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //   iv.setImageBitmap(thumbnail);

        }

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
            ((FMobActivity)getActivity())
                    .setActionBarTitle("Step Three");
            fragmentTransaction.commit();*/
            boolean camresult= checkCameraPermission(getActivity());
            if(camresult) {
                boolean result=checkPermission(getActivity());
                if(result) {
            //  dispatchTakePictureIntent();
                  cameraIntent();
                }
            }
        }
            if (view.getId() == R.id.tv2) {
                Bundle bundle = new Bundle();
                bundle.putString("fname", strfname);
                bundle.putString("lname", strlname);
                bundle.putString("midname", strmidnm);
                bundle.putString("yob", stryob);
                bundle.putString("gender", strgender);
                bundle.putString("city", strcity);
                bundle.putString("state", strstate);
                Fragment  fragment = new OpenAcc();

                fragment.setArguments(bundle);


                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment,"Biller Menu");
                fragmentTransaction.addToBackStack("Biller Menu");
                ((FMobActivity)getActivity())
                        .setActionBarTitle("Biller Menu");
                fragmentTransaction.commit();
            }
            if (view.getId() == R.id.tv) {
                Bundle bundle = new Bundle();
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
                ((FMobActivity)getActivity())
                        .setActionBarTitle("Biller Menu");
                fragmentTransaction.commit();
            }



        if(view.getId()==  R.id.button4){

        }
        if(view.getId() == R.id.buttonnxt){
            if(uploadpic) {
                Bundle bundle = new Bundle();
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
                ((FMobActivity) getActivity())
                        .setActionBarTitle("Step Four");
                fragmentTransaction.commit();
            }else{
                Toast.makeText(
                        getActivity(),
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
        boolean camresult= Utility.checkCameraPermission(getActivity());
        if(camresult) {
            boolean result=Utility.checkPermission(getActivity());
            if(result) {
                //  dispatchTakePictureIntent();
                cameraIntent();
            }
        }
    }
    private void cameraIntent()
    {
      /*  String defaultCameraPackage = null;

        List<ApplicationInfo> list = getActivity().getPackageManager().getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (int n=0;n<list.size();n++) {
            if((list.get(n).flags & ApplicationInfo.FLAG_SYSTEM)==1)
            {
                SecurityLayer.Log("TAG", "Installed Applications  : " + list.get(n).loadLabel( getActivity().getPackageManager()).toString());
                SecurityLayer.Log("TAG", "package name  : " + list.get(n).packageName);
                if(list.get(n).loadLabel( getActivity().getPackageManager()).toString().equalsIgnoreCase("Camera")) {
                    defaultCameraPackage = list.get(n).packageName;
                    break;
                }
            }
        }*/
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      //  intent.setPackage(defaultCameraPackage);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
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
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            //	new SendTask().execute();
            registerUser();
            //	RegTest();
            return true;
        } else {

            Toast.makeText(
                    getActivity(),
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
                                            new MaterialDialog.Builder(getActivity())
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
                                                            Toast.makeText(getActivity(), "Please enter a valid Id Number/Passport", Toast.LENGTH_LONG).show();

                                                        }
                                                    }else{
                                                        Toast.makeText(getActivity(), "Please enter a valid Last Name", Toast.LENGTH_LONG).show();

                                                    }
                                                }else{
                                                    Toast.makeText(getActivity(), "Please enter a valid First Name", Toast.LENGTH_LONG).show();

                                                }


                                    }else{
                                        Toast.makeText(getActivity(), "Please enter a valid mobile number", Toast.LENGTH_LONG).show();

                                    }
                                }else{
                                    Toast.makeText(getActivity(), "The Year Of Birth field should only contain numeric characters. Please fill in appropiately", Toast.LENGTH_LONG).show();
                                }}else{
                                Toast.makeText(getActivity(), "The Mobile Number field should only contain numeric characters. Please fill in appropiately", Toast.LENGTH_LONG).show();
                            }}else{
                            Toast.makeText(getActivity(), "The Year Of Birth field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();

                        }}else{
                        Toast.makeText(getActivity(), "The Last Name  field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();

                    }}else{
                    Toast.makeText(getActivity(), "The First Name field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();

                }}else{
                Toast.makeText(getActivity(), "The ID Number field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();
                }}else{
                Toast.makeText(getActivity(), "The ID No/Passport  field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();


            }}else{
            Toast.makeText(getActivity(), "The Mobile Number field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();

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
                    Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
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
        new MaterialDialog.Builder(getActivity())
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
