package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.FileProvider;
import androidx.core.graphics.BitmapCompat;
import android.util.Base64;
import android.util.Log;
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

import java.io.ByteArrayInputStream;
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

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;


public class OpenAccOTP extends Fragment implements View.OnClickListener {
    File finalFile;
    int REQUEST_CAMERA = 3293;

    Button sigin;
    String refnumber;
    TextView gendisp;
    SessionManagement session;
    EditText otp, pin;
    List<String> planetsList = new ArrayList<String>();
    List<String> prodid = new ArrayList<String>();
    ArrayAdapter<String> mArrayAdapter;
    Spinner sp1, sp2, sp5, sp3, sp4;
    Button btn4, next;
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
    String upurl = ApplicationConstants.IMG_URL + "image/acimg?userId=";
    public static final String DATEPICKER_TAG = "datepicker";
    ImageView img;
    String finparams = null;
    String strfname, strlname, strmidnm, stryob, stremail, strhmdd, strmobn, strsalut, strmarst, strgender, strcity, strstate;
    TextView step2, step1, step3, step4;

    public OpenAccOTP() {
        // Required empty public constructor
    }

    /*  private static Fragment newInstance(Context context) {
          LayoutOne f = new LayoutOne();

          return f;
      }
  */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.openaccotp, null);
        sigin = (Button) root.findViewById(R.id.button1);
        sigin.setOnClickListener(this);
        img = (ImageView) root.findViewById(R.id.imgview);
        next = (Button) root.findViewById(R.id.buttonnxt);
        otp = (EditText) root.findViewById(R.id.agentphon);
        pin = (EditText) root.findViewById(R.id.agentpin);
        //    next.setOnClickListener(this);
        sigin = (Button) root.findViewById(R.id.button1);
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Loading ....");
        // Set Cancelable as False

        prgDialog.setCancelable(false);
        session = new SessionManagement(getActivity());
        String usid = Utility.gettUtilUserId(getActivity());
        upurl = upurl + usid;

        strfname = getArguments().getString("fname");
        strlname = getArguments().getString("lname");
        strmidnm = getArguments().getString("midname");
        stryob = getArguments().getString("yob");
        stremail = getArguments().getString("email");
        strhmdd = getArguments().getString("hmadd");
        strmobn = getArguments().getString("mobn");
        strsalut = getArguments().getString("salut");
        strmarst = getArguments().getString("marstatus");
        strgender = getArguments().getString("gender");
        strcity = getArguments().getString("city");
        strstate = getArguments().getString("state");

        String getsign = session.getString("CUSTSIGNPATH");
        Bitmap bitmapsign = BitmapFactory.decodeFile(getsign);

        String getcust = session.getString("CUSTIMGFILEPATH");
        Bitmap bitmapcust = BitmapFactory.decodeFile(getcust);

        Bitmap[] bmap = new Bitmap[2];
        bmap[0] = bitmapcust;
        bmap[1] = bitmapsign;
        Bitmap res = mergeMultiple(bmap);


        step2 = (TextView) root.findViewById(R.id.tv2);
        step2.setOnClickListener(this);

        step1 = (TextView) root.findViewById(R.id.tv);
        step1.setOnClickListener(this);

        step3 = (TextView) root.findViewById(R.id.tv3);
        step3.setOnClickListener(this);


        step4 = (TextView) root.findViewById(R.id.tv4);
        step4.setOnClickListener(this);
        return root;
    }


    public void StartChartAct(int i) {


    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...

    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getActivity());
        // path to /data/data/yourapp/app_data/imageDir
        File directory =
                Environment.getExternalStoragePublicDirectory
                        (
                                "/FirstAgent/"
                        );
        // Create imageDir
        File mypath = new File(Environment.getExternalStorageDirectory(), "/FirstAgent/profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void invokeAccOTP(final String params) {

        prgDialog.show();


        String endpoint = "core/openaccountnew.action";

        String url = "";
        try {
            url = SecurityLayer.genURLCBC(params, endpoint, getActivity());
            SecurityLayer.Log("cbcurl", url);
            SecurityLayer.Log("params", params);
            SecurityLayer.Log("refurl", url);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
        }

      /*  try {
           // MySSLSocketFactory.SecureURL(client, getActivity());
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
        String encimage = getImageBase64();
        SecurityLayer.Log("Image Base 64", encimage);
        session.setString("Base64image", encimage);
        ApiInterface apiService =
                ApiSecurityClient.getClientBase64(getActivity(), encimage).create(ApiInterface.class);


        Call<String> call = apiService.setGenericRequestRaw(url);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    SecurityLayer.Log("response..:", response.body());


                    JSONObject obj = new JSONObject(response.body());
                 /*   JSONObject jsdatarsp = obj.optJSONObject("data");
                    SecurityLayer.Log("JSdata resp", jsdatarsp.toString());
                    //obj = Utility.onresp(obj,getActivity()); */
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());

                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");


                    if (!(response.body() == null)) {
                        if (respcode.equals("00")) {

                            SecurityLayer.Log("Response Message", responsemessage);
                            Toast.makeText(
                                    getActivity(),
                                    "Account Opening request has been successfully received ",
                                    Toast.LENGTH_LONG).show();
                            getActivity().finish();

                            startActivity(new Intent(getActivity(), FMobActivity.class));

                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getActivity(),
                                "There was an error processing your request ",
                                Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    //   Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());

                    if (!(getActivity() == null)) {
                        Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());
                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                    }

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    if (!(getActivity() == null)) {
                        Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());
                        ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                    }
                    // SecurityLayer.Log(e.toString());
                }
                if (!(getActivity() == null)) {
                    session.setString("Base64image", "N");
                    prgDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());

                session.setString("Base64image", "N");


                if (!(getActivity() == null)) {
                    prgDialog.dismiss();
                    Toast.makeText(
                            getActivity(),
                            "There was an error processing your request",
                            Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
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
                    //obj = Utility.onresp(obj,getActivity()); *//*
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
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
                            Intent mIntent = new Intent(getActivity(), ActivateAgent.class);

                            startActivity(mIntent);*//*


                        }
                        else {

                            Toast.makeText(
                                    getActivity(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();


                        }

                    }
                    else {

                        Toast.makeText(
                                getActivity(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();


                    }

                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getActivity(), "We are unable to process your request at the moment. Please try again later", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getActivity(), "We are unable to process your request at the moment. Please try again later", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();

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


    public void uploadImage(File file) {

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
            SecurityLayer.Log("Up Image File Name", file.getName());
            RequestBody formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "tmp_photo_" + System.currentTimeMillis(),
                            RequestBody.create(MediaType.parse("image/jpg"), file))

                    .build();
            SecurityLayer.Log("Upload Url", upurl);
            Request request = new Request.Builder().url(upurl).post(formBody).build();
            //  Response<String> response = null;
            okhttp3.Response response = null;
            try {
                response = okHttpClient.newCall(request).execute();


                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                else {
                    refnumber = response.body().string();
                    SecurityLayer.Log("response..:", refnumber);

                    SecurityLayer.Log("Success upload", "Success Upload");

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

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = getResizedBitmap((Bitmap) data.getExtras().get("data"), 300, 300);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String filename = System.currentTimeMillis() + ".jpg";
        finalFile = new File(Environment.getExternalStorageDirectory(), filename);
        FileOutputStream fo;
        try {
            finalFile.createNewFile();
            fo = new FileOutputStream(finalFile);
            fo.write(bytes.toByteArray());
            fo.close();
            SecurityLayer.Log("Filename stored", filename);
            String filePath = finalFile.getPath();
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            img.setImageBitmap(bitmap);
            session.setString("CUSTSIGNPATH", filePath);
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

    @Override
    public void onClick(View view) {


        if (view.getId() == R.id.button1) {

               /* Fragment  fragment = new OpenAccFullImgPreview();


                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment,"Step Four");
                fragmentTransaction.addToBackStack("Step Four");
                ((FMobActivity)getActivity())
                        .setActionBarTitle("Step Four");
                fragmentTransaction.commit();*/


            String getsign = session.getString("CUSTSIGNPATH");
            Bitmap bitmapsign = BitmapFactory.decodeFile(getsign);

            String getcust = session.getString("CUSTIMGFILEPATH");
            Bitmap bitmapcust = BitmapFactory.decodeFile(getcust);

            Bitmap[] bmap = new Bitmap[2];
            bmap[0] = bitmapcust;
            bmap[1] = bitmapsign;
            Bitmap res = mergeMultiple(bmap);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            res.compress(Bitmap.CompressFormat.JPEG, 90, out);
            Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            int kb = BitmapCompat.getAllocationByteCount(res) / 1024;
            SecurityLayer.Log("KBS of file", Integer.toString(kb));


            Log.e("Original   dimensions", res.getWidth() + " " + res.getHeight());
            Log.e("Compressed dimensions", decoded.getWidth() + " " + decoded.getHeight());
            saveToInternalStorage(decoded);
            String filename = "accopen.jpg";
            String encimage = encodeTobase64(decoded);
            writeToFile(encimage);
            final File path =
                    Environment.getExternalStoragePublicDirectory
                            (
                                    //Environment.DIRECTORY_PICTURES
                                    "FirstAgent/"
                            );

            // Make sure the path directory exists.
            if (!path.exists()) {
                // Make it, if it doesn't exit
                path.mkdirs();
            }

            finalFile = new File(Environment.getExternalStorageDirectory(), "/FirstAgent/base64fl.txt");


/*String edotp = otp.getText().toString();
                String edpin = pin.getText().toString();
String params = CHANNEL_ID+usid+"/"+agentid+"/"+mobnoo+"/"+strsalut+"/"+strfname+"/"+strlname+"/"+strmidnm+"/"+strmarst+"/"+stryob+"/"+stremail+"/"+strgender+"/"+strstate+"/"+strcity+"/"+strhmdd+"/"+strmobn+"/N/"+edotp+"/"+edpin;
   */            // {channel}/{userId}/{merchantId}/{mobileNumber}/{salutation}/{firstName}/{lastName}/{midName}/{maritalStatus}/{dob}/{email}/{gender}/{state}/{city}/{address}/{phone}/{mandateCard}/{otp}/{pin}
            //   invokeAccOTP(params);
            //  uploadImage(file);

            new AsyncUplImg().execute("");
        }

        if (view.getId() == R.id.button4) {

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
            Fragment fragment = new OpenAcc();

            fragment.setArguments(bundle);


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment, "Biller Menu");
            fragmentTransaction.addToBackStack("Biller Menu");
            ((FMobActivity) getActivity())
                    .setActionBarTitle("Biller Menu");
            fragmentTransaction.commit();
        }
        if (view.getId() == R.id.tv3) {
            Bundle bundle = new Bundle();
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
            ((FMobActivity) getActivity())
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
            Fragment fragment = new OpenAccStepTwo();
            fragment.setArguments(bundle);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment, "Biller Menu");
            fragmentTransaction.addToBackStack("Biller Menu");
            ((FMobActivity) getActivity())
                    .setActionBarTitle("Biller Menu");
            fragmentTransaction.commit();
        }
        if (view.getId() == R.id.tv4) {
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
            fragmentTransaction.replace(R.id.container_body, fragment, "Biller Menu");
            fragmentTransaction.addToBackStack("Biller Menu");
            ((FMobActivity) getActivity())
                    .setActionBarTitle("Biller Menu");
            fragmentTransaction.commit();
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
            Activity activity123 = getActivity();
            if(activity123 instanceof MainActivity) {
                ((MainActivity)getActivity())
                        .setActionBarTitle(title);
            }
            if(activity123 instanceof SignInActivity) {
                ((SignInActivity) getActivity())
                        .setActionBarTitle(title);
            }*/
        }

        if (view.getId() == R.id.textView3) {


        }
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
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
            if (!(getActivity() == null)) {
                prgDialog.dismiss();
            }
            String usid = Utility.gettUtilUserId(getActivity());
            String agentid = Utility.gettUtilAgentId(getActivity());
            String mobnoo = Utility.gettUtilMobno(getActivity());

            String edotp = otp.getText().toString();
            String edpin = pin.getText().toString();


            String encrypted = null;

            encrypted = Utility.b64_sha256(edpin);
            if (strmidnm.equals("")) {
                strmidnm = "NA";
            }
            if (stremail.equals("")) {
                stremail = "NA";
            }
            refnumber = "4135056117809250";
            finparams = CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo + "/" + strsalut + "/" + strfname + "/" + strlname + "/" + strmidnm + "/" + strmarst + "/" + stryob + "/" + stremail + "/" + strgender + "/" + strstate + "/" + strcity + "/" + strhmdd + "/" + strmobn + "/" + refnumber + "/" + edotp + "/" + encrypted;
            SecurityLayer.Log("Before InvokeAcc");
            if (!((refnumber == null))) {
                if (!(refnumber.equals(""))) {
                    invokeAccOTP(finparams);
                }
            }


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
                    Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    SetDialog(" The device has not successfully connected to server. Please check your internet settings", "Check Settings");
                }
            }
        });
    }


    public void SetDialog(String msg, String title) {
        new MaterialDialog.Builder(getActivity())
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

    private Bitmap mergeMultiple(Bitmap[] parts) {

        Bitmap result = Bitmap.createBitmap(parts[0].getWidth() * 2, parts[0].getHeight() * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        for (int i = 0; i < parts.length; i++) {

            canvas.drawBitmap(parts[i], parts[i].getWidth() * (i % 2), parts[i].getHeight() * (i / 2), paint);
        }
        return result;
    }


    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public String getImageBase64() {
        String getsign = session.getString("CUSTSIGNPATH");
        Bitmap bitmapsign = BitmapFactory.decodeFile(getsign);

        String getcust = session.getString("CUSTIMGFILEPATH");
        Bitmap bitmapcust = BitmapFactory.decodeFile(getcust);

        Bitmap[] bmap = new Bitmap[2];
        bmap[0] = bitmapcust;
        bmap[1] = bitmapsign;
        Bitmap res = mergeMultiple(bmap);
        String encimage = encodeTobase64(res);
        return encimage;
    }

    public void writeToFile(String data) {
        // Get the directory for the user's public pictures directory.
        final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                //Environment.DIRECTORY_PICTURES
                                "FirstAgent/"
                        );

        // Make sure the path directory exists.
        if (!path.exists()) {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(Environment.getExternalStorageDirectory(), "/FirstAgent/base64fl.txt");


        // Save your stream, don't forget to flush() it before closing it.

        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            SecurityLayer.Log("Exception", "File write failed: " + e.toString());
        }
    }
}
