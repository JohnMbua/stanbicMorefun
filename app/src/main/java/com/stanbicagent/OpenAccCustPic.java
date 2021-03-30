package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.FileProvider;
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
import java.util.UUID;

import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

import static android.app.Activity.RESULT_CANCELED;
import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;


public class OpenAccCustPic extends Fragment implements View.OnClickListener {
    File finalFile;
    int REQUEST_CAMERA = 3293;
    ProgressDialog pDialog;
    Button sigin;
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
    String strfname, strlname, strmidnm, stryob, stremail, strhmdd, strmobn, strsalut, strmarst, strcity, strstate, strgender;
    TextView step2, step1, step3, stt;

    public OpenAccCustPic() {
        // Required empty public constructor
    }

    /*  private static Fragment newInstance(Context context) {
          LayoutOne f = new LayoutOne();

          return f;
      }
  */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.opencustupsign, null);
        sigin = (Button) root.findViewById(R.id.button1);
        sigin.setOnClickListener(this);
        img = (ImageView) root.findViewById(R.id.imgview);
        next = (Button) root.findViewById(R.id.buttonnxt);
        next.setOnClickListener(this);
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Loading....");
        // Set Cancelable as False

        prgDialog.setCancelable(false);
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

        step3 = (TextView) root.findViewById(R.id.tv3);
        step3.setOnClickListener(this);
        return root;
    }


    public void StartChartAct(int i) {


    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...

    }

    private void invokeAgent(final String params) {

        prgDialog.show();

        String sessid = UUID.randomUUID().toString();


        String endpoint = "otp/generatecustomerotp.action";

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
        ApiInterface apiService =
                ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);


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

                            Fragment fragment = new OpenAccOTP();
                            fragment.setArguments(bundle);


                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            //  String tag = Integer.toString(title);
                            fragmentTransaction.replace(R.id.container_body, fragment, "Step Four");
                            fragmentTransaction.addToBackStack("Step Four");
                            ((FMobActivity) getActivity())
                                    .setActionBarTitle("Step Four");
                            fragmentTransaction.commit();


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
                prgDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());

                if (!(getActivity() == null)) {
                    Toast.makeText(
                            getActivity(),
                            "There was an error processing your request",
                            Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());
                    ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                }
                prgDialog.dismiss();
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
        if (!(data == null)) {
            Bitmap thumbnail = getResizedBitmap((Bitmap) data.getExtras().get("data"), 150, 150);
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
                if (img != null) {
                    img.setImageBitmap(bitmap);
                }
                session.setString("CUSTSIGNPATH", filePath);
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
            boolean camresult = Utility.checkCameraPermission(getActivity());
            if (camresult) {
                boolean result = Utility.checkPermission(getActivity());
                if (result) {
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
        if (view.getId() == R.id.buttonnxt) {

               /* Fragment  fragment = new OpenAccFullImgPreview();


                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment,"Step Four");
                fragmentTransaction.addToBackStack("Step Four");
                ((FMobActivity)getActivity())
                        .setActionBarTitle("Step Four");
                fragmentTransaction.commit();*/
            if (uploadpic) {

                String usid = Utility.gettUtilUserId(getActivity());
                String agentid = Utility.gettUtilAgentId(getActivity());
                String mobnoo = Utility.gettUtilMobno(getActivity());

                String params = CHANNEL_ID + usid + "/" + agentid + "/" + strmobn;

                invokeAgent(params);
            } else {
                Toast.makeText(
                        getActivity(),
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
        String defaultCameraPackage = null;

        List<ApplicationInfo> list = getActivity().getPackageManager().getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (int n = 0; n < list.size(); n++) {
            if ((list.get(n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                SecurityLayer.Log("TAG", "Installed Applications  : " + list.get(n).loadLabel(getActivity().getPackageManager()).toString());
                SecurityLayer.Log("TAG", "package name  : " + list.get(n).packageName);
                if (list.get(n).loadLabel(getActivity().getPackageManager()).toString().equalsIgnoreCase("Camera")) {
                    defaultCameraPackage = list.get(n).packageName;
                    break;
                }
            }
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setPackage(defaultCameraPackage);
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
                                                        new MaterialDialog.Builder(getActivity())
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
                                                        Toast.makeText(getActivity(), "Please enter a valid Id Number/Passport", Toast.LENGTH_LONG).show();

                                                    }
                                                } else {
                                                    Toast.makeText(getActivity(), "Please enter a valid Last Name", Toast.LENGTH_LONG).show();

                                                }
                                            } else {
                                                Toast.makeText(getActivity(), "Please enter a valid First Name", Toast.LENGTH_LONG).show();

                                            }


                                        } else {
                                            Toast.makeText(getActivity(), "Please enter a valid mobile number", Toast.LENGTH_LONG).show();

                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "The Year Of Birth field should only contain numeric characters. Please fill in appropiately", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "The Mobile Number field should only contain numeric characters. Please fill in appropiately", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "The Year Of Birth field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();

                            }
                        } else {
                            Toast.makeText(getActivity(), "The Last Name  field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();

                        }
                    } else {
                        Toast.makeText(getActivity(), "The First Name field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Toast.makeText(getActivity(), "The ID Number field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "The ID No/Passport  field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();


            }
        } else {
            Toast.makeText(getActivity(), "The Mobile Number field is empty. Please fill in appropiately", Toast.LENGTH_LONG).show();

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


    public void ClearOpenAcc() {
        //   sp1.setSelection(0);

        mobno.setText(" ");
        idno.setText(" ");
        fnam.setText(" ");
        lnam.setText(" ");
        //   yob.setText(" ");
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


}
