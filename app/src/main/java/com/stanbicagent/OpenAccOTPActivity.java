package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Base64;
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
import java.io.OutputStreamWriter;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import rest.TLSSocketFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class OpenAccOTPActivity extends BaseActivity implements View.OnClickListener {
    File finalFile;
    int REQUEST_CAMERA =3293;

    Button sigin;
    String refnumber;
    TextView gendisp;
    SessionManagement session;
    EditText otp,pin;
    List<String> planetsList = new ArrayList<String>();
    List<String> prodid = new ArrayList<String>();
    ArrayAdapter<String> mArrayAdapter;
    Spinner sp1,sp2,sp5,sp3,sp4;
    Button btn4,next;
    static Hashtable<String, String> data1;
    String paramdata = "";
    ProgressDialog prgDialog,prgDialog7;
    TextView tnc;
    List<String> mobopname  = new ArrayList<String>();
    List<String> mobopid  = new ArrayList<String>();

    TextView tvdate;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    public static final String DATEPICKER_TAG = "datepicker";
    ImageView img;
    String finparams = null;
    String upurl = ApplicationConstants.IMG_UPURL;
    String strfname,strlname,strmidnm,stryob,stremail,strhmdd,strmobn,strsalut,strmarst,strgender,strcity,strstate,straddr;
    TextView step2, step1, step3, step4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_acc_otp);

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
        otp = (EditText) findViewById(R.id.agentphon);
        pin = (EditText) findViewById(R.id.agentpin);
        //    next.setOnClickListener(this);
        sigin = (Button) findViewById(R.id.button1);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Loading ....");
        // Set Cancelable as False

        prgDialog.setCancelable(false);
        session = new SessionManagement(this);
        String usid = Utility.gettUtilUserId(getApplicationContext());
        upurl = upurl+usid;


        Intent intent = getIntent();
        if (intent != null) {
            strfname = intent.getStringExtra("fname");
            strlname = intent.getStringExtra("lname");
            strmidnm = intent.getStringExtra("midname");
            stryob = intent.getStringExtra("yob");
            stremail = intent.getStringExtra("email");
            strhmdd = intent.getStringExtra("hmadd");
            strmobn = intent.getStringExtra("mobn");
            strgender = intent.getStringExtra("gender");

        }


        step2 = (TextView) findViewById(R.id.tv2);
        step2.setOnClickListener(this);

        step1 = (TextView) findViewById(R.id.tv);
        step1.setOnClickListener(this);

        step3 = (TextView)findViewById(R.id.tv3);
        step3.setOnClickListener(this);


        step4 = (TextView) findViewById(R.id.tv4);
        step4.setOnClickListener(this);

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


    private void invokeAccOTP(final String params) {

        prgDialog.show();





        String endpoint= "core/openaccount.action";

        String url = "";
        try {
            url = SecurityLayer.genURLCBC(params,endpoint,getApplicationContext());
            SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("params", params);
            SecurityLayer.Log("refurl", url);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror",e.toString());
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
                    JSONObject datas = obj.optJSONObject("data");


                    if(!(response.body() == null)) {
                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                if (respcode.equals("00")) {
                                    String acno = "";
                                    if(!(datas == null)){
                                      acno = datas.optString("accountNumber")  ;
                                    }

                                    SecurityLayer.Log("Response Message", responsemessage);
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Account Opening request has been successfully received ",
                                            Toast.LENGTH_LONG).show();
                                    finish();
                                    Intent i = new Intent(OpenAccOTPActivity.this, FinalConfAccountOpening.class);
                                    i.putExtra("accountno", acno);
                                    startActivity(i);

                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            responsemessage,
                                            Toast.LENGTH_LONG).show();
                                }
                            }else{
                                LogOut();
                            }
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "There was an error processing your request ",
                                    Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(
                                getApplicationContext(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    //   Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());

                    if(!(getApplicationContext() == null)) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    }

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    if(!(getApplicationContext() == null)) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());
                        SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                    }
                    // SecurityLayer.Log(e.toString());
                }
                if(!(getApplicationContext() == null)) {
                    session.setString("Base64image", "N");
                    prgDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error",t.toString());

                session.setString("Base64image","N");


                if(!(getApplicationContext() == null)) {
                    prgDialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error processing your request",
                            Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());
                   SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                }
            }
        });
     /*   client.post(url, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                pDialog.dismiss();
                try {
                    // JSON Object
                    SecurityLayer.Log("response..:", response);


                    JSONObject obj = new JSONObject(response);
                 *//*   JSONObject jsdatarsp = obj.optJSONObject("data");
                    SecurityLayer.Log("JSdata resp", jsdatarsp.toString());
                    //obj = Utility.onresp(obj,getApplicationContext()); *//*
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");



                    //session.setString(SecurityLayer.KEY_APP_ID,appid);

                    if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                        SecurityLayer.Log("Response Message", responsemessage);

                        if (respcode.equals("00")) {
                           *//* JSONObject datas = obj.optJSONObject("data");

                            final   String agid = agentid.getText().toString();

                            String status = datas.optString("status");




                            finish();
                            Intent mIntent = new Intent(getApplicationContext(), ActivateAgent.class);

                            startActivity(mIntent);*//*


                        }
                        else {

                            Toast.makeText(
                                    getApplicationContext(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();


                        }

                    }
                    else {

                        Toast.makeText(
                                getApplicationContext(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();


                    }

                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // SecurityLayer.Log(e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {

                // Hide Progress Dialog
                pDialog.dismiss();
                SecurityLayer.Log("error:", error.toString());
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "We are unable to process your request at the moment. Please try again later", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "We are unable to process your request at the moment. Please try again later", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();

                }
            }
        });*/
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA) {
            onCaptureImageResult(data);
        }
    }


    public void uploadImage(File file){

        //  OkHttpClient client = new OkHttpClient();


        OkHttpClient okHttpClient = null;

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager

            SSLSocketFactory sslSocketFactory = null;
            try {
                sslSocketFactory = new TLSSocketFactory();

            } catch (KeyManagementException ignored) {

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            //  final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    String encpin = session.getString("ENC_PIN");
                    Request request = original.newBuilder()

                            .header("secret", encpin)

                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            okHttpClient = builder
                    .connectTimeout(70, TimeUnit.SECONDS)
                    .writeTimeout(70, TimeUnit.SECONDS)
                    .readTimeout(70, TimeUnit.SECONDS)
                    .build();
            SecurityLayer.Log("Up Image File Name",file.getName());
            RequestBody formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "tmp_photo_" + System.currentTimeMillis(),
                            RequestBody.create(MediaType.parse("image/jpg"), file))

                    .build();
            SecurityLayer.Log("Upload Url",upurl);
            Request request = new Request.Builder().url(upurl).post(formBody).build();
            //  Response<String> response = null;
            okhttp3.Response response = null;
            try {
                response = okHttpClient.newCall(request).execute();


                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                else{
                    refnumber = response.body().string();
                    SecurityLayer.Log("response..:",refnumber );

                    SecurityLayer.Log("Success upload","Success Upload");

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
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
        Bitmap thumbnail = getResizedBitmap((Bitmap) data.getExtras().get("data"),300,300);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String filename = System.currentTimeMillis()+".jpg";
        finalFile = new File(Environment.getExternalStorageDirectory(),filename);
        FileOutputStream fo;
        try {
            finalFile.createNewFile();
            fo = new FileOutputStream(finalFile);
            fo.write(bytes.toByteArray());
            fo.close();
            SecurityLayer.Log("Filename stored",filename);
            String filePath = finalFile.getPath();
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            img.setImageBitmap(bitmap);
            session.setString("CUSTSIGNPATH",filePath);
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
    @Override
    public void onClick(View view) {


        if(view.getId() == R.id.button1) {

               /* Fragment  fragment = new OpenAccFullImgPreview();


                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment,"Step Four");
                fragmentTransaction.addToBackStack("Step Four");
                ((FMobActivity)getApplicationContext())
                        .setActionBarTitle("Step Four");
                fragmentTransaction.commit();*/

            String edotp = otp.getText().toString();
            String edpin = pin.getText().toString();


            if (Utility.isNotNull(edotp)) {
                if (Utility.isNotNull(edpin)) {


                    String usid = Utility.gettUtilUserId(getApplicationContext());
                    String agentid = Utility.gettUtilAgentId(getApplicationContext());
                    String mobnoo = Utility.gettUtilMobno(getApplicationContext());



                    String encrypted = Utility.b64_sha256(edpin);

                    http://196.22.6.52:7777/agencyapi/app/core/openaccount.action/1/114784407/8128697448/ian/gggy/20010703/null/NA/M/12344/6a697a7579387463656a426e6b714d515335736b6e78626a625844614874417365366c4961516f4947624d
                    finparams = CHANNEL_ID + usid + "/" + strmobn + "/" + strfname + "/" + strlname +  "/"+ stryob + "/" + stremail + "/NA/"  + strgender + "/" + edotp + "/" + encrypted;
                    //  finparams = CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo + "/" + strsalut + "/" + strfname + "/" + strlname + "/" + strmidnm + "/" + strmarst + "/" + stryob + "/" + stremail + "/" + strgender + "/" + strstate + "/" + strcity + "/" + strhmdd + "/" + strmobn + "/" + refnumber + "/" + edotp + "/" + encrypted;
                //    SecurityLayer.Log("Before InvokeAcc");

               //     String bvnparams = CHANNEL_ID + usid + "/" + strsalut + "/" + strfname + "/" + strlname + "/" + strmidnm + "/" + strmarst + "/" + stryob + "/" + stremail + "/" + strgender + "/" + strstate + "/" + strcity + "/" + straddr + "/" + strmobn + "/" + refnumber + "/" + strmobn + "/" + edotp + "/" + encrypted;



                                invokeAccOTP(finparams);

                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter a valid value for PIN", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter a valid value for OTP", Toast.LENGTH_LONG).show();
                    }
                }

        if(view.getId()==  R.id.button4){

        }
        if (view.getId() == R.id.tv2) {

         /*   Bundle bundle = new Bundle();
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
            fragmentTransaction.replace(R.id.container_body, fragment, "Biller Menu");
            fragmentTransaction.addToBackStack("Biller Menu");
            ((FMobActivity) getApplicationContext())
                    .setActionBarTitle("Biller Menu");
            fragmentTransaction.commit();*/

            finish();
            Intent intent  = new Intent(OpenAccOTPActivity.this,OpenAccActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
        if (view.getId() == R.id.tv3) {
           /* Bundle bundle = new Bundle();
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
            Intent intent  = new Intent(OpenAccOTPActivity.this,OpenAccUpPicActivity.class);

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
            Intent intent  = new Intent(OpenAccOTPActivity.this,OpenAccStepTwoActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
        if (view.getId() == R.id.tv4) {
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
            Fragment fragment = new OpenAccCustPic();
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
            Intent intent  = new Intent(OpenAccOTPActivity.this,OpenAccCustPicActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
        if(view.getId() == R.id.tdispedit){

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

        if(view.getId() == R.id.textView3){


        }
    }
    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
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

    class AsyncUplImg extends AsyncTask<String, String, String> {
        Bitmap bmp = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgDialog.show();
        }

        // Download Music File from Internet
        @Override
        protected String doInBackground(String... f_url) {


            uploadImage(finalFile);
            return "34";
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (!(getApplicationContext() == null)) {
                prgDialog.dismiss();
            }
            String usid = Utility.gettUtilUserId(getApplicationContext());
            String agentid = Utility.gettUtilAgentId(getApplicationContext());
            String mobnoo = Utility.gettUtilMobno(getApplicationContext());
            String edotp = otp.getText().toString();
            String edpin = pin.getText().toString();


            String encrypted = Utility.b64_sha256(edpin);

            http://196.22.6.52:7777/agencyapi/app/core/openaccount.action/1/114784407/8128697448/ian/gggy/20010703/null/NA/M/12344/6a697a7579387463656a426e6b714d515335736b6e78626a625844614874417365366c4961516f4947624d
            finparams = CHANNEL_ID + usid + "/" + mobnoo + "/" + strsalut + "/" + strfname + "/" + strlname + "/" + strmidnm + "/" + strmarst + "/" + stryob + "/" + stremail + "/" + strgender + "/" + strstate + "/" + strcity + "/" + strhmdd + "/" + strmobn + "/" + refnumber + "/" + edotp + "/" + encrypted+"/"+straddr;
          //  finparams = CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo + "/" + strsalut + "/" + strfname + "/" + strlname + "/" + strmidnm + "/" + strmarst + "/" + stryob + "/" + stremail + "/" + strgender + "/" + strstate + "/" + strcity + "/" + strhmdd + "/" + strmobn + "/" + refnumber + "/" + edotp + "/" + encrypted;
            SecurityLayer.Log("Before InvokeAcc");

           String bvnparams = CHANNEL_ID + usid + "/" + strsalut + "/" + strfname + "/" + strlname + "/" + strmidnm + "/" + strmarst + "/" + stryob + "/" + stremail + "/" + strgender + "/" + strstate + "/" + strcity + "/" + straddr + "/" + strmobn + "/" + refnumber + "/" + strmobn + "/" + edotp + "/" + encrypted;
         //   {channel}/{userId}/{salutation}/{firstName}/{lastName}/{midName}/{maritalStatus}/{dob}/{email}/{gender}/{state}/{city}/{address}/{phone}/{mandateCard}/{bvn}/{otp}/{pin}
        //    1/112128164/Chief/Ranae/Benjamin/L/UNMARR/19931003/Michaellhenderson@armyspy.com/M/05/NA/2922 Rocky Road/08013952719/424655476196982/08013952719/12344/6a697a7579387463656a426e6b714d515335736b6e78626a625844614874417365366c4961516f4947624d



            if (!((refnumber == null))) {
                if (!(refnumber.equals(""))) {
if(session.getString("ISBVN").equals("Y")) {

    BVNOpenAcc(bvnparams);
}else{
    invokeAccOTP(finparams);
}
                }/**/
            }

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



    public void SetDialog(String msg,String title){
        new MaterialDialog.Builder(getApplicationContext())
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

    private Bitmap mergeMultiple(Bitmap[] parts){

        Bitmap result = Bitmap.createBitmap(parts[0].getWidth() * 2, parts[0].getHeight() * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        for (int i = 0; i < parts.length; i++) {

            canvas.drawBitmap(parts[i], parts[i].getWidth() * (i % 2), parts[i].getHeight() * (i / 2), paint);
        }
        return result;
    }





    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }

    public static String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public void writeToFile(String data)
    {
        // Get the directory for the user's public pictures directory.
        final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                //Environment.DIRECTORY_PICTURES
                                "FirstAgent/"
                        );

        // Make sure the path directory exists.
        if(!path.exists())
        {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(Environment.getExternalStorageDirectory(), "/FirstAgent/base64fl.txt");


        // Save your stream, don't forget to flush() it before closing it.

        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            SecurityLayer.Log("Exception", "File write failed: " + e.toString());
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

        if(prgDialog!=null && prgDialog.isShowing()){

            prgDialog.dismiss();
        }
        super.onDestroy();
    }

    public  void LogOut(){
        session.logoutUser();

        // After logout redirect user to Loing Activity
        finish();
        Intent i = new Intent(getApplicationContext(), SignInActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Staring Login Activity
        startActivity(i);
        Toast.makeText(
                getApplicationContext(),
                "You have been locked out of the app.Please call customer care for further details",
                Toast.LENGTH_LONG).show();
        // Toast.makeText(getApplicationContext(), "You have logged out successfully", Toast.LENGTH_LONG).show();

    }

    private void BVNOpenAcc(final String params) {

        prgDialog.show();


        String endpoint = "bvn/openaccount.action";

        String url = "";
        try {
            url = SecurityLayer.genURLCBC(params, endpoint, getApplicationContext());
            SecurityLayer.Log("cbcurl", url);
            SecurityLayer.Log("params", params);
            SecurityLayer.Log("refurl", url);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
        }

      /*  try {
           // MySSLSocketFactory.SecureURL(client, getApplicationContext());
        } catch (KeyStoreException e) {
            SecurityLayer.Log(e.toString());
            SecurityLayer.Log(e.toString());
        } catch (IOException e) {
            SecurityLayer.Log(e.toString());
        } catch (NoSuchAlgorithmException e) {
            SecurityLayer.Log(e.toString());
        } catch (CertificateException e) {
            SecurityLayer.Log(e.toString());
        } catch (UnrecoverableKeyException e) {
            SecurityLayer.Log(e.toString());
        } catch (KeyManagementException e) {
            SecurityLayer.Log(e.toString());
        }*/


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
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Account Opening request has been successfully received ",
                                            Toast.LENGTH_LONG).show();
                                    finish();
                                    Intent i = new Intent(OpenAccOTPActivity.this, FMobActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);

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
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    //   Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());

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
                if (!(getApplicationContext() == null)) {
                    session.setString("Base64image", "N");
                    prgDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());

                session.setString("Base64image", "N");


                if (!(getApplicationContext() == null)) {
                    prgDialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error processing your request",
                            Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());
                    SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getApplicationContext());
                }
            }
        });
    }
}
