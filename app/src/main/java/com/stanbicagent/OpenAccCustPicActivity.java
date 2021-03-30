package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.gcacace.signaturepad.views.SignaturePad;
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
import java.util.UUID;

import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class OpenAccCustPicActivity extends BaseActivity implements View.OnClickListener {
    File finalFile;
    int REQUEST_CAMERA = 3293;
    ProgressDialog pDialog;
    Button sigin, next2;
    TextView gendisp;
    SessionManagement session;
    EditText idno, mobno, fnam, lnam, yob;
    List<String> planetsList = new ArrayList<String>();
    List<String> prodid = new ArrayList<String>();
    ArrayAdapter<String> mArrayAdapter;
    Spinner sp1, sp2, sp5, sp3, sp4;
    Button btn4, next;
    static Hashtable<String, String> data1;
    String paramdata = "";
    ProgressDialog prgDialog, prgDialog7;
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
    TextView step2, step1, step3, stt;
    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_acc_cust_pic);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)


        sigin = (Button) findViewById(R.id.button1);
        sigin.setOnClickListener(this);
        img = (ImageView) findViewById(R.id.imgview);
        next = (Button) findViewById(R.id.buttonnxt);
        next.setOnClickListener(this);

        next2 = (Button) findViewById(R.id.buttonnxt2);
        next2.setOnClickListener(this);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Loading....");
        // Set Cancelable as False

        prgDialog.setCancelable(false);
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

        step3 = (TextView) findViewById(R.id.tv3);
        step3.setOnClickListener(this);


        final LinearLayout lyupl = (LinearLayout) findViewById(R.id.lyuploadpic);
        final LinearLayout lysigni = (LinearLayout) findViewById(R.id.lysignsi);


        RadioGroup rg = (RadioGroup) findViewById(R.id.rgropchoose);
        RadioButton rbupl = (RadioButton) findViewById(R.id.radiphoto);
        RadioButton rbsign = (RadioButton) findViewById(R.id.radiosign);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radiphoto) {
                    lyupl.setVisibility(View.VISIBLE);
                    lysigni.setVisibility(View.GONE);
                }
                if (checkedId == R.id.radiosign) {
                    lyupl.setVisibility(View.GONE);
                    lysigni.setVisibility(View.VISIBLE);
                }
            }
        });


        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //   Toast.makeText(OpenAccCustPicActivity.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton = (Button) findViewById(R.id.clear_button);
        mSaveButton = (Button) findViewById(R.id.save_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                convertSignedImage(signatureBitmap);
            }
        });
    }


    private void invokeAgent(final String params) {

        prgDialog.show();

        String sessid = UUID.randomUUID().toString();


        String endpoint = "otp/generatecustomerotp.action";

        String url = "";
        try {
            url = SecurityLayer.genURLCBC(params, endpoint, getApplicationContext());
            SecurityLayer.Log("cbcurl", url);
            SecurityLayer.Log("params", params);
            SecurityLayer.Log("refurl", url);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
        }

        ApiInterface apiService =
                ApiSecurityClient.getClient(getApplicationContext()).create(ApiInterface.class);


        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    SecurityLayer.Log("response..:", response.body());


                    JSONObject obj = new JSONObject(response.body());
                 /*   JSONObject jsdatarsp = obj.optJSONObject("data");
                    SecurityLayer.Log("JSdata resp", jsdatarsp.toString());
                    //obj = Utility.onresp(obj,getApplicationContext()); */
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");


                    if (!(response.body() == null)) {
                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                if (respcode.equals("00")) {

                                    SecurityLayer.Log("Response Message", responsemessage);
                            /*Bundle bundle = new Bundle();
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

                            Fragment fragment = new OpenAccOTP();
                            fragment.setArguments(bundle);


                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            //  String tag = Integer.toString(title);
                            fragmentTransaction.replace(R.id.container_body, fragment,"Step Four");
                            fragmentTransaction.addToBackStack("Step Four");
                            ((FMobActivity)getApplicationContext())
                                    .setActionBarTitle("Step Four");
                            fragmentTransaction.commit();*/

                                    Intent intent = new Intent(OpenAccCustPicActivity.this, OpenAccOTPActivity.class);


                                    intent.putExtra("fname", strfname);
                                    intent.putExtra("lname", strlname);
                                    intent.putExtra("midname", strmidnm);
                                    intent.putExtra("yob", stryob);
                                    intent.putExtra("email", stremail);
                                    intent.putExtra("hmadd", strhmdd);
                                    intent.putExtra("mobn", strmobn);
                                    intent.putExtra("salut", strsalut);
                                    intent.putExtra("marstatus", strmarst);
                                    intent.putExtra("straddr", straddr);
                                    intent.putExtra("gender", strgender);
                                    intent.putExtra("city", strcity);
                                    intent.putExtra("state", strstate);


                                    startActivity(intent);


                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            responsemessage,
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                LogOut();
                            }

                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "There was an error processing your request ",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "There was an error processing your request ",
                                Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    if (!(getApplicationContext() == null)) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    }

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    if (!(getApplicationContext() == null)) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    }
                    // SecurityLayer.Log(e.toString());
                }
                prgDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());

                if (!(getApplicationContext() == null)) {
                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error processing your request",
                            Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());
                    SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                }
                prgDialog.dismiss();
            }
        });

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
        if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
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

    private void convertSignedImage(Bitmap origbit) {


        int srcWidth = origbit.getWidth();
        int srcHeight = origbit.getHeight();
        int dstWidth = (int) (srcWidth * 0.8f);
        int dstHeight = (int) (srcHeight * 0.8f);
        Bitmap thumbnail = origbit;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String filename = "signed.jpg";
        finalFile = new File(Environment.getExternalStorageDirectory(), "FirstAgent/" + filename);
        FileOutputStream fo;
        try {
            finalFile.createNewFile();
            fo = new FileOutputStream(finalFile);
            fo.write(bytes.toByteArray());
            fo.close();
            SecurityLayer.Log("Filename stored", filename);
            String filePath = finalFile.getPath();
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);

            session.setString("CUSTSIGNPATH", filePath);
            uploadpic = true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //   iv.setImageBitmap(thumbnail);
    }

    private void onCaptureImageResult(Intent data) {
        if (!(data == null)) {
            Bitmap origbit = (Bitmap) data.getExtras().get("data");
            if (img != null) {
                img.setImageBitmap(origbit);
            }

            int srcWidth = origbit.getWidth();
            int srcHeight = origbit.getHeight();
            int dstWidth = (int) (srcWidth * 0.8f);
            int dstHeight = (int) (srcHeight * 0.8f);
            Bitmap thumbnail = getResizedBitmap((Bitmap) data.getExtras().get("data"), dstHeight, dstWidth);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            String filename = "camerapic.jpg";
            finalFile = new File(Environment.getExternalStorageDirectory(), "FirstAgent/" + filename);
            FileOutputStream fo;
            try {
                finalFile.createNewFile();
                fo = new FileOutputStream(finalFile);
                fo.write(bytes.toByteArray());
                fo.close();
                SecurityLayer.Log("Filename stored", filename);
                String filePath = finalFile.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);

                session.setString("CUSTSIGNPATH", filePath);
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
            ((FMobActivity)getApplicationContext())
                    .setActionBarTitle("Step Three");
            fragmentTransaction.commit();*/
            boolean camresult = Utility.checkCameraPermission(this);
            if (camresult) {
                boolean result = Utility.checkPermission(this);
                if (result) {
                    cameraIntent();
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
            Fragment  fragment = new OpenAcc();

            fragment.setArguments(bundle);


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment, "Biller Menu");
            fragmentTransaction.addToBackStack("Biller Menu");
            ((FMobActivity) getApplicationContext())
                    .setActionBarTitle("Biller Menu");
            fragmentTransaction.commit();*/


            finish();
            Intent intent = new Intent(OpenAccCustPicActivity.this, OpenAccActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
        if (view.getId() == R.id.tv3) {
          /*  Bundle bundle = new Bundle();
            bundle.putString("fname", strfname);
            bundle.putString("lname", strlname);
            bundle.putString("midname", strmidnm);
            bundle.putString("yob", stryob);
            bundle.putString("gender", strgender);
            bundle.putString("city", strcity);
            bundle.putString("state", strstate);
            bundle.putString("email", stremail);
            bundle.putString("hmadd", strhmdd);
            bundle.putString("mobn", strmobn);
            bundle.putString("salut", strsalut);
            bundle.putString("marstatus", strmarst);
            Fragment fragment = new OpenAccUpPic();
            fragment.setArguments(bundle);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment, "Biller Menu");
            fragmentTransaction.addToBackStack("Biller Menu");
            ((FMobActivity) getApplicationContext())
                    .setActionBarTitle("Biller Menu");
            fragmentTransaction.commit();*/

            finish();
            Intent intent = new Intent(OpenAccCustPicActivity.this, OpenAccUpPicActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
        if (view.getId() == R.id.tv) {
            /*Bundle bundle = new Bundle();
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
            fragmentTransaction.replace(R.id.container_body, fragment, "Biller Menu");
            fragmentTransaction.addToBackStack("Biller Menu");
            ((FMobActivity) getApplicationContext())
                    .setActionBarTitle("Biller Menu");
            fragmentTransaction.commit();*/

            finish();
            Intent intent = new Intent(OpenAccCustPicActivity.this, OpenAccStepTwoActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
        if (view.getId() == R.id.buttonnxt) {

               /* Fragment  fragment = new OpenAccFullImgPreview();


                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment,"Step Four");
                fragmentTransaction.addToBackStack("Step Four");
                ((FMobActivity)getApplicationContext())
                        .setActionBarTitle("Step Four");
                fragmentTransaction.commit();*/
            if (uploadpic) {

                String usid = Utility.gettUtilUserId(getApplicationContext());
                String agentid = Utility.gettUtilAgentId(getApplicationContext());
                String mobnoo = Utility.gettUtilMobno(getApplicationContext());

                String params = CHANNEL_ID + usid + "/" + agentid + "/" + strmobn;

                invokeAgent(params);
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Please upload customer picture to proceed",
                        Toast.LENGTH_LONG).show();
            }
        }
        if (view.getId() == R.id.buttonnxt2) {

               /* Fragment  fragment = new OpenAccFullImgPreview();


                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment,"Step Four");
                fragmentTransaction.addToBackStack("Step Four");
                ((FMobActivity)getApplicationContext())
                        .setActionBarTitle("Step Four");
                fragmentTransaction.commit();*/
            if (uploadpic) {

                String usid = Utility.gettUtilUserId(getApplicationContext());
                String agentid = Utility.gettUtilAgentId(getApplicationContext());
                String mobnoo = Utility.gettUtilMobno(getApplicationContext());


                String params = CHANNEL_ID + usid + "/" + agentid + "/" + strmobn;

                invokeAgent(params);
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Please upload customer picture to proceed",
                        Toast.LENGTH_LONG).show();
            }
        }
        if (view.getId() == R.id.button4) {

        }
        if (view.getId() == R.id.tdispedit) {

          /*  Fragment fragment =  new NatWebProd();;
String title = "Bank Info";
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment, title);
            fragmentTransaction.addToBackStack(title);
            fragmentTransaction.commit();
            Activity activity123 = getApplicationContext();
            if(activity123 instanceof MainActivity) {
                ((MainActivity)getApplicationContext())
                        .setActionBarTitle(title);
            }
            if(activity123 instanceof SignInActivity) {
                ((SignInActivity) getApplicationContext())
                        .setActionBarTitle(title);
            }*/
        }

        if (view.getId() == R.id.textView3) {


        }
    }
   /* private void cameraIntent()
    {
        String defaultCameraPackage = null;

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
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setPackage(defaultCameraPackage);
        startActivityForResult(intent, REQUEST_CAMERA);
    }*/

    private void cameraIntent() {
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
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
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

    public void registerUser() {
        final String acty = "01261";
        //  final String prodn = sp1.getSelectedItem().toString();

        String mobnumm = mobno.getText().toString().trim();
        final String idn = idno.getText().toString().trim();
        final String fname = fnam.getText().toString().trim();
        final String lname = lnam.getText().toString().trim();
        final String yb = yob.getText().toString().trim();
        final String nt = sp2.getSelectedItem().toString();
        final String mobnum = setMobFormat(mobnumm);
        SecurityLayer.Log("Mobile Number Formatted", mobnum);
        if (Utility.isNotNull(mobnum)) {
            if (Utility.isNotNull(idn)) {
                if (Utility.isNotNull(fname)) {

                    if (Utility.isNotNull(lname)) {
                        if (Utility.isNotNull(yb)) {
                            if (Utility.checknum(mobnum) == true) {
                                if (Utility.checknum(yb) == true) {
                                    if (yb.length() == 4) {
                                        if (!(mobnum.equals("N"))) {

                                            if (Utility.isValidWord(fname)) {
                                                if (Utility.isValidWord(lname)) {
                                                    if (Utility.isValidWord(idn)) {
                                                        new MaterialDialog.Builder(getApplicationContext())
                                                                .title("Open Account")
                                                                .content("Are you sure you want to Open an Account with these particulars? \n First Name: " + fname + "  \n  Last Name " + lname + " \n Mobile Number  " + mobnum + " \n Mobile Operator " + nt + " \n ID Number: " + idn + " \n Year Of Birth  " + yb + "  ")
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

                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Please enter a valid Id Number/Passport", Toast.LENGTH_LONG).show();

                                                    }
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Please enter a valid Last Name", Toast.LENGTH_LONG).show();

                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Please enter a valid First Name", Toast.LENGTH_LONG).show();

                                            }


                                        } else {
                                            Toast.makeText(getApplicationContext(), "Please enter a valid mobile number", Toast.LENGTH_LONG).show();

                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "The Year Of Birth field should only contain numeric characters. Please fill in appropiately", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "The Mobile Number field should only contain numeric characters. Please fill in appropiately", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "The Year Of Birth field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();

                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "The Last Name  field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "The First Name field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "The ID Number field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "The ID No/Passport  field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();


            }
        } else {
            Toast.makeText(getApplicationContext(), "The Mobile Number field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();

        }
    }

    public void invokeWS(String acctype, String msisdn, String id, String fname, String lname, String yearob) {
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
        String url = ApplicationConstants.NET_URL + ApplicationConstants.AND_ENPOINT + "agencyopenAccount/1/01261/" + msisdn + "/1/" + id + "/" + fname + "/" + lname + "/" + yearob + "/ANDROID/" + username;

        SecurityLayer.Log("Open Acc URL", url);

        client.post(url, new AsyncHttpResponseHandler() {
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
                    SetDialog(" The device has not successfully connected to server. Please check your internet settings", "Check Settings");
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {

                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    SetDialog(" The device has not successfully connected to server. Please check your internet settings", "Check Settings");
                }
            }
        });
    }


    public void ClearOpenAcc() {
        //   sp1.setSelection(0);

        mobno.setText(" ");
        idno.setText(" ");
        fnam.setText(" ");
        lnam.setText(" ");
        //   yob.setText(" ");
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void SetDialog(String msg, String title) {
        new MaterialDialog.Builder(this)
                .title(title)
                .content(msg)

                .negativeText("Close")
                .show();
    }

    public String setMobFormat(String mobno) {
        String vb = mobno.substring(Math.max(0, mobno.length() - 9));
        SecurityLayer.Log("Logged Number is", vb);
        if (vb.length() == 9 && (vb.substring(0, Math.min(mobno.length(), 1)).equals("7"))) {
            return "254" + vb;
        } else {
            return "N";
        }
    }


    public void SetForceOutDialog(String msg, final String title, final Context c) {
        if (!(c == null)) {
            new MaterialDialog.Builder(this)
                    .title(title)
                    .content(msg)

                    .negativeText("CONTINUE")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {

                            dialog.dismiss();
                            finish();
                            session.logoutUser();

                            // After logout redirect user to Loing Activity
                            Intent i = new Intent(c, SignInActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            // Staring Login Activity
                            startActivity(i);

                        }
                    })
                    .show();
        }
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub


        if (prgDialog != null && prgDialog.isShowing()) {

            prgDialog.dismiss();
        }
        super.onDestroy();
    }
}
