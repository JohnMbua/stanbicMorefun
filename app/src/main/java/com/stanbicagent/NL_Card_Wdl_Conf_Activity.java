package com.stanbicagent;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.morefun.yapi.device.printer.FontFamily;
import com.morefun.yapi.device.printer.OnPrintListener;
import com.morefun.yapi.device.printer.PrinterConfig;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.vipul.hp_hp.library.Layout_to_Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stanbicagent.ApplicationConstants.album_folder_name;

public class NL_Card_Wdl_Conf_Activity extends BaseActivity implements View.OnClickListener {
    //TextView recacno, recname, recamo, recnarr, recsendnum, recsendnam, recfee, recagcmn, txtrfcd, recdatetimee;
    Button btnsub;
    ProgressDialog prgDialog, prgDialog2;
    Layout_to_Image layout_to_image;  //Create Object of Layout_to_Image Class
    LinearLayout relativeLayout, whole_layout;   //Define Any Layout
    Button shareImage, repissue;
    Bitmap bitmap;                  //Bitmap for holding Image of layout
    //RelativeLayout rlagfee, rlaccom;
    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    private UsbDeviceConnection mConnection;
    private UsbInterface mInterface;
    private UsbEndpoint mEndPoint;
    private PendingIntent mPermissionIntent;
    private Context mContext;
    // private BroadcastReceiver mUsbReceiver;
    private static final String ACTION_USB_PERMISSION = "com.stanbicagent.USB_PERMISSION";
    private static Boolean forceCLaim = true;
    RelativeLayout rlsave, rlshare, rlprint;
    HashMap<String, UsbDevice> mDeviceList;
    Iterator<UsbDevice> mDeviceIterator;
    SharedPreferences pref;
    String tid, mid, mer_name, loc, ccode, user_only, fld_43, totfee, send_date, send_time, trantype, appvers = "";
    int check = 0;
    String cardNo, cholder_name, amt, rrn, card_label, ctyp, aid, tsi, stan, txtamou, mask_card_no, auth = "",str_formtted_fee,prn_fee_amount;
    TextView txt_card_no, txt_holder_name, txt_amount, txt_ref_code, textfinaldatet = null;
    DeviceServiceEngine mSDKManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nl_activity_final_conf_card_wdl_trans);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appvers = Utility.getAppVersion(getApplicationContext());
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        user_only = pref.getString("user_only", "");
        tid = pref.getString("tid", "");
        mid = pref.getString("mid", "");
        mer_name = pref.getString("mer_name", "");
        loc = pref.getString("loc", "");
        ccode = pref.getString("ccode", "");
        fld_43 = pref.getString("fld_43", "");
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdate_df = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat stime_df = new SimpleDateFormat("HH:mm");
        send_date = sdate_df.format(c);
        send_time = stime_df.format(c);

        txt_card_no = (TextView) findViewById(R.id.txt_card_no);
        txt_holder_name = (TextView) findViewById(R.id.txt_holder_name);
        txt_amount = (TextView) findViewById(R.id.txt_amount);
        txt_ref_code = (TextView) findViewById(R.id.txt_ref_code);
        textfinaldatet = (TextView) findViewById(R.id.textfinaldatet);
        shareImage = (Button) findViewById(R.id.share_image);
        shareImage.setOnClickListener(this);
        repissue = (Button) findViewById(R.id.reportiss);
        repissue.setOnClickListener(this);
        rlsave = (RelativeLayout) findViewById(R.id.rlsave);
        rlshare = (RelativeLayout) findViewById(R.id.rlshare);
        rlprint = (RelativeLayout) findViewById(R.id.rlprint);

        cardNo = pref.getString("cardNo", "");
        try {
            mask_card_no = maskString(cardNo, 4, 12, '*');
            txt_card_no.setText(mask_card_no);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cholder_name = pref.getString("cholder_name", "");
        amt = pref.getString("txn_amount", "");
        rrn = pref.getString("rrn", "");
        card_label = pref.getString("card_label", "");
        ctyp = pref.getString("ctyp", "");
        aid = pref.getString("aid", "");
        tsi = pref.getString("tsi", "");
        stan = pref.getString("stan", "");
        auth = pref.getString("auth", "");
        txtamou = Utility.returnNumberFormat(amt);
        txt_amount.setText(ApplicationConstants.KEY_NAIRA + txtamou);
        textfinaldatet.setText(send_date + " " + send_time);
        txt_ref_code.setText(rrn);
        txt_holder_name.setText(cholder_name);

        rlsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_to_image = new Layout_to_Image(NL_Card_Wdl_Conf_Activity.this, relativeLayout);
                //now call the main working function ;) and hold the returned image in bitmap
                bitmap = layout_to_image.convert_layout();
                String filename = "ShareRec" + System.currentTimeMillis() + ".jpg";
                if (Utility.checkPermission(NL_Card_Wdl_Conf_Activity.this)) {
                    saveImage(bitmap, filename);
                    Toast.makeText(
                            getApplicationContext(),
                            "Receipt downloaded successfully to gallery",
                            Toast.LENGTH_LONG).show();
                }
                whole_layout.setVisibility(View.GONE);
                whole_layout.setVisibility(View.VISIBLE);
            }
        });

        rlshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_to_image = new Layout_to_Image(getApplicationContext(), relativeLayout);
                //now call the main working function ;) and hold the returned image in bitmap
                bitmap = layout_to_image.convert_layout();
                if (Utility.checkWriteStoragePermission(NL_Card_Wdl_Conf_Activity.this)) {
                    if (!(getApplicationContext() == null)) {
                        shareImage(getImageUri(bitmap));
                    }
                }
                whole_layout.setVisibility(View.GONE);
                whole_layout.setVisibility(View.VISIBLE);
            }
        });

        rlprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
                if (mSDKManager == null) {
                    SecurityLayer.Log("mSDKManager", "ServiceEngine is Null");
                    return;
                }
                Check_Printer();
            }
        });

        prgDialog2 = new ProgressDialog(this);
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);
        btnsub = (Button) findViewById(R.id.button2);
        btnsub.setOnClickListener(this);
        whole_layout = (LinearLayout) findViewById(R.id.whole_layout);
        relativeLayout = (LinearLayout) findViewById(R.id.receipt);
    }

    private void shareImage(Uri imagePath) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imagePath);
        startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
    }

    public void StartChartAct(int i) {
    }

    public void Check_Printer() {
        try {
            com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
            if (printer.getStatus() != 0) {
                check = -1;
            } else {
                check = 0;
            }
            After_Printer_Check();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void After_Printer_Check() {
        if (check == 0) {
            try {
                Print_Card_Txn();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please insert paper receipt in order to print.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void Print_Card_Txn() throws RemoteException {
        com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
        String recanno="";
        String tntp=ApplicationClass.get().getGlobalVals().getTranType();
        String auth_id = ApplicationClass.get().getGlobalVals().getSessAuth();
        String amou= ApplicationClass.get().getGlobalVals().getSessAmt();
        String txn_status=ApplicationClass.get().getGlobalVals().getTxnSta();
        String rpc=ApplicationClass.get().getGlobalVals().getResp();
        String narration=ApplicationClass.get().getGlobalVals().getNarr();
        String fee= ApplicationClass.get().getGlobalVals().getFeep();

        if (amou != null) {
            amou = Utility.returnNumberFormat(amou.substring(0, amou.length() - 2));
        }
        String crdN=ApplicationClass.get().getGlobalVals().getSessPan();
        if (crdN != null) {
            crdN=Utility.MaskPan(ApplicationClass.get().getGlobalVals().getSessPan());
            try {
                recanno = maskString(recanno, 0, 6, '*');
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ctyp != null){
            ctyp=hexToAscii(ctyp);
        }



        str_formtted_fee = fee.substring(2);
        SecurityLayer.Log("str_formtted_fee", "str_formtted_fee >> " + str_formtted_fee);
        while (str_formtted_fee.length() > 0 && str_formtted_fee.charAt(0) == '0') {
            str_formtted_fee = str_formtted_fee.substring(1);
        }
        str_formtted_fee = str_formtted_fee.substring(0, str_formtted_fee.length() - 2);
        SecurityLayer.Log("str_formtted_fee_2", "str_formtted_fee_2 >> " + str_formtted_fee);
        prn_fee_amount = Utility.returnNumberFormat(str_formtted_fee);
        SecurityLayer.Log("prn_fee_amount", "prn_fee_amount >> " + prn_fee_amount);




        try {
            if (printer.getStatus() != 0) {
                SecurityLayer.Log("getStatus", "getStatus = " + (R.string.msg_nopaper));
                return;
            }
            if (printer.initPrinter() != 0) {
                return;
            }

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getApplicationContext().getAssets().open("receipt_logo.bmp"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            printer.appendImage(bitmap);
            int fontSize = FontFamily.MIDDLE;
            Bundle bundle = new Bundle();
//            if (!TextUtils.isEmpty(mTypeFacePath)) {
//                bundle.putString(PrinterConfig.COMMON_TYPEFACE, mTypeFacePath);
//            }
            bundle.putInt(PrinterConfig.COMMON_GRAYLEVEL, 15);
            printer.setConfig(bundle);

            printer.appendPrnStr("AGENT: " + mer_name, fontSize, false);
            printer.appendPrnStr("LOC: " + loc, fontSize, false);
            printer.appendPrnStr("TID: " + tid + "  MID: " + mid, fontSize, false);
            printer.appendPrnStr("DATE: " + send_date + "    TIME: " + send_time, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("CARD #: " + crdN, fontSize, false);
            printer.appendPrnStr(cholder_name, fontSize, false);
            fontSize = FontFamily.BIG;

            printer.appendPrnStr(tntp, fontSize, false);
            if (tntp.equals("BALANCE INQUIRY")) {
                // printer.appendPrnStr("TOTAL: KES " + amou, fontSize, false); //get balance enquiry details
            } else if (tntp.equals("MINI STATEMENT")) {
                // printer.appendPrnStr("TOTAL: KES " + amou, fontSize, false); //get ministatement data
            } else{
                printer.appendPrnStr("TOTAL: KES " + amou, fontSize, false);
            }
            fontSize = FontFamily.MIDDLE;
            // printer.appendPrnStr("Narration: " + narra, fontSize, false);
            printer.appendPrnStr("Fee: KES " + prn_fee_amount, fontSize, false);
            fontSize = FontFamily.BIG;
            printer.appendPrnStr("          "+txn_status, fontSize, false);
            fontSize = FontFamily.SMALL;
            printer.appendPrnStr("Card: "+ctyp, fontSize, false);
            printer.appendPrnStr("Aid: "+aid, fontSize, false);
            printer.appendPrnStr("Tsi: "+tsi, fontSize, false);
            fontSize = FontFamily.MIDDLE;
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("Response Code: " + rpc, fontSize, false);
            printer.appendPrnStr("Ref ID: " + rrn, fontSize, false);
            //if(tntp.equals("CARD WITHDRAWAL")) {
            if (rpc.equals("00")) {
                printer.appendPrnStr("Auth: " + auth_id, fontSize, false);
            }else{
                printer.appendPrnStr("Reason: " + narration, fontSize, false);
            }
            // }
            printer.appendPrnStr("App Version: " + appvers, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("SERVED BY: " + Utility.gettUtilUserId(getApplicationContext()), fontSize, false);
            printer.appendPrnStr("   **** CUSTOMER COPY ****", fontSize, false);
            printer.appendPrnStr("\n\n", fontSize, false);
            printer.startPrint(new OnPrintListener.Stub() {
                @Override
                public void onPrintResult(int ret) throws RemoteException {
                    Log.d("onPrintResult", "onPrintResult = " + ret);
                    //listener.showMessage(ret == ServiceResult.Success ? context.getString(R.string.msg_succ) : context.getString(R.string.msg_fail));
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*public void Print_Card_Wdl_Txn() {
        try {
            cardNo = maskString(cardNo, 4, 12, '*');
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (n900Device.isDeviceAlive()) {
            final Printer printer;
            printer = n900Device.getPrinter();
            printer.init();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.v("Running ", "running Print_Card_Wdl_Txn thread");
                    if (printer.getStatus() == PrinterStatus.NORMAL) {
                        try {
                            StringBuffer scriptBuffer = new StringBuffer();
                            String fontsPath = printer.getFontsPath(NL_FBN_CardWdl_Conf_Activity.this, "arial.ttf", true);
                            if (fontsPath != null) {
                                scriptBuffer.append("!font " + fontsPath + "\n");//set ttf font path
                                Log.e("Font Success", "Font Success");
                                Map<String, Bitmap> map = new HashMap<String, Bitmap>();
                                Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.receipt_logo);
                                String bmp1 = "logo";
                                map.put(bmp1, bitmap1);
                                scriptBuffer.append("*image c 300*100 path:" + bmp1 + "\n");//set image large threshold and  align left
                                scriptBuffer.append("!asc n\n !gray 4\n");//   Set  body content font as medium.
                                scriptBuffer.append("*text l AGENT: " + mer_name + "\n");
                                scriptBuffer.append("*text l LOC: " + loc + "\n");
                                scriptBuffer.append("*text l TID: " + tid + "  MID: " + mid + "\n");
                                scriptBuffer.append("*text l CARD NUMBER: " + mask_card_no + "\n");
                                scriptBuffer.append("*text l HOLDER: " + cholder_name + "\n");
                                scriptBuffer.append("*text l DATE: " + send_date + "    TIME: " + send_time + "\n");
                                scriptBuffer.append("*line" + "\n");// Print a dotted line
                                scriptBuffer.append("*text l CARD: " + card_label.toUpperCase() + "\n");
                                scriptBuffer.append("*text l AID: " + aid + "\n");
                                scriptBuffer.append("*text l TVR: " + tvr + "\n");
                                scriptBuffer.append("*line" + "\n");// Print a dotted line
                                scriptBuffer.append("*text c CARD WITHDRAWAL\n");
                                scriptBuffer.append("!asc nl\n !gray 7\n");// Set  font as large.
                                scriptBuffer.append("*text l TOTAL: " + String.format("%22s", "KES " + txtamou).replace(' ', ' ') + "\n");
                                scriptBuffer.append("!asc n\n !gray 4\n !yspace 20\n");//   Set  body content font as medium.
                                scriptBuffer.append("!asc n\n !gray 4\n !yspace 6\n");
                                scriptBuffer.append("!asc nl\n !gray 7\n");// Set  font as large.
                                scriptBuffer.append("*text c APPROVED\n");
                                scriptBuffer.append("!asc n\n !gray 4\n");//   Set  body content font as medium.
                                scriptBuffer.append("*line" + "\n");// Print a dotted line
                                scriptBuffer.append("*text l Response Code: " + "00" + "\n");
                                scriptBuffer.append("*text l Trace No: " + stan + "\n");
                                if (rrn != null) {
                                    scriptBuffer.append("*text l rrn: " + rrn + "\n");
                                }
                                scriptBuffer.append("*text l Auth: " + auth + "\n");
                                scriptBuffer.append("*text l App Version: " + appvers + "\n");
                                scriptBuffer.append("*line" + "\n");// Print a dotted line
                                scriptBuffer.append("*text l Served By: " + Utility.gettUtilUserId(getApplicationContext()) + "\n");
                                scriptBuffer.append("!asc n\n !gray 4\n !yspace 30\n");//   Set  body content font as medium.
                                scriptBuffer.append("*text c **** AGENT COPY ****\n");
                                scriptBuffer.append("*feedline 2\n");
                                PrinterResult printerResult = printer.printByScript(PrintContext.defaultContext(),
                                        scriptBuffer.toString(), map, 60, TimeUnit.SECONDS);
                            } else {
                                Log.e("Font Error", "Font Error");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Printer error", "Printer Exception" + e);
                        }
                    }
                }
            }).start();
        } else {
            Log.v("devmanager", "device manager is not alive");
        }
    }*/

    private static String maskString(String strText, int start, int end, char maskChar)
            throws Exception {
        if (strText == null || strText.equals(""))
            return "";

        if (start < 0)
            start = 0;

        if (end > strText.length())
            end = strText.length();

        if (start > end)
            throw new Exception("End index cannot be greater than start index");

        int maskLength = end - start;

        if (maskLength == 0)
            return strText;

        StringBuilder sbMaskString = new StringBuilder(maskLength);

        for (int i = 0; i < maskLength; i++) {
            sbMaskString.append(maskChar);
        }

        return strText.substring(0, start)
                + sbMaskString.toString()
                + strText.substring(start + maskLength);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), FMobActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button2) {
            finish();
            Intent i = new Intent(NL_Card_Wdl_Conf_Activity.this, FMobActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Staring Login Activity
            startActivity(i);
        }
        if (view.getId() == R.id.share_image) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("SELECT AN OPTION");
            String[] animals = {"Save to Gallery", "Share Receipt"};
            builder.setItems(animals, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: // horse
                            layout_to_image = new Layout_to_Image(getApplicationContext(), relativeLayout);
                            //now call the main working function ;) and hold the returned image in bitmap
                            bitmap = layout_to_image.convert_layout();
                            String filename = "ShareRec" + System.currentTimeMillis() + ".jpg";
                            if (Utility.checkPermission(NL_Card_Wdl_Conf_Activity.this)) {
                                saveImage(bitmap, filename);
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Receipt downloaded successfully to gallery",
                                        Toast.LENGTH_LONG).show();
                            }
                            break;
                        case 1: // cow
                            layout_to_image = new Layout_to_Image(getApplicationContext(), relativeLayout);
                            //now call the main working function ;) and hold the returned image in bitmap
                            bitmap = layout_to_image.convert_layout();
                            if (Utility.checkPermission(NL_Card_Wdl_Conf_Activity.this)) {
                                if (!(getApplicationContext() == null)) {
                                    shareImage(getImageUri(bitmap));
                                }
                            }
                            break;
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private Uri getImageUri(Bitmap image) {
        Uri uri = null;
        try {
            File file = new File(getAlbumStorageDir(album_folder_name), "to-share.png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.close();
            uri = Uri.fromFile(file);
        } catch (IOException e) {
            Log.d("TAG", "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void saveImage(Bitmap finalBitmap, String image_name) {
        addJpgSignatureToGallery(finalBitmap);
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            String flname = String.format("ShareRec_%d", System.currentTimeMillis());
            File photo = new File(getAlbumStorageDir(album_folder_name), String.format("ShareR%d.jpg", System.currentTimeMillis()));
            File filename = photo;
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        NL_Card_Wdl_Conf_Activity.this.sendBroadcast(mediaScanIntent);
    }

    // close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this will find a bluetooth printer device
    void findBT() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(
                        getApplicationContext(),
                        "No bluetooth available",
                        Toast.LENGTH_LONG).show();
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.getName().equals("BlueTooth Printer")) {
                        mmDevice = device;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }
    // tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            beginListenForData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this will send text data to be printed by the bluetooth printer
    void sendData(String msg) throws IOException {
        try {
            int img = R.drawable.monochrome;
            try {
                Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                        img);
                bmp = getResizedBitmap(bmp, 120, 360);
                if (bmp != null) {
                    byte[] command = UtilsPhoto.decodeBitmap(bmp);
                    //  mmOutputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    mmOutputStream.write(command);

                    mmOutputStream.write(msg.getBytes());
                } else {
                    Log.e("Print Photo error", "the file isn't exists");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PrintTools", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            closeBT();
        } catch (IOException ex) {
            ex.printStackTrace();
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

    /*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */
    void beginListenForData() {
        try {
            final Handler handler = new Handler();
            // this is the ASCII code for a newline charactersend
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );
                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;
                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                //   myLabel.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
            workerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String translateDeviceClass(int deviceClass) {
        switch (deviceClass) {
            case UsbConstants.USB_CLASS_APP_SPEC:
                return "Application specific USB class";
            case UsbConstants.USB_CLASS_AUDIO:
                return "USB class for audio devices";
            case UsbConstants.USB_CLASS_CDC_DATA:
                return "USB class for CDC devices (communications device class)";
            case UsbConstants.USB_CLASS_COMM:
                return "USB class for communication devices";
            case UsbConstants.USB_CLASS_CONTENT_SEC:
                return "USB class for content security devices";
            case UsbConstants.USB_CLASS_CSCID:
                return "USB class for content smart card devices";
            case UsbConstants.USB_CLASS_HID:
                return "USB class for human interface devices (for example, mice and keyboards)";
            case UsbConstants.USB_CLASS_HUB:
                return "USB class for USB hubs";
            case UsbConstants.USB_CLASS_MASS_STORAGE:
                return "USB class for mass storage devices";
            case UsbConstants.USB_CLASS_MISC:
                return "USB class for wireless miscellaneous devices";
            case UsbConstants.USB_CLASS_PER_INTERFACE:
                return "USB class indicating that the class is determined on a per-interface basis";
            case UsbConstants.USB_CLASS_PHYSICA:
                return "USB class for physical devices";
            case UsbConstants.USB_CLASS_PRINTER:
                return "USB class for printers";
            case UsbConstants.USB_CLASS_STILL_IMAGE:
                return "USB class for still image devices (digital cameras)";
            case UsbConstants.USB_CLASS_VENDOR_SPEC:
                return "Vendor specific USB class";
            case UsbConstants.USB_CLASS_VIDEO:
                return "USB class for video devices";
            case UsbConstants.USB_CLASS_WIRELESS_CONTROLLER:
                return "USB class for wireless controller devices";
            default:
                return "Unknown USB class!";
        }
    }


    final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
                            mInterface = device.getInterface(0);
                            mEndPoint = mInterface.getEndpoint(1);
                            mConnection = mUsbManager.openDevice(device);

                            //setup();
                        }
                    } else {
                        //Log.d("SUB", "permission denied for device " + device);
                        Toast.makeText(context, "PERMISSION DENIED FOR THIS DEVICE", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };


    private void print(final UsbDeviceConnection connection, final UsbInterface usbInterface, final String test) {
        final Handler handler = new Handler();
        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    UsbRequest request = new UsbRequest();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "PRINT IS CALLED", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //   String test = "  \n \n AIRTIME TRANSACTION  \n USERID:SURESHD \n Reference Number: 203939494944 \n Mobile number: 08128697448 \n Amount:500 Naira \n Telco Operator: Airtel \n Fee:0.00 Naira \n \n \n \n\r\n";
                    byte[] testBytes = test.getBytes();

                    int length = testBytes.length;
                    Log.v("Bytes Length", Integer.toString(length));

                    if (usbInterface == null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "INTERFACE IS NULL", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if (connection == null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "CONNECTION IS NULL", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                    }
                    if (forceCLaim == null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "FORCE CLAIM IS NULL", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                    }
                    if (connection.claimInterface(usbInterface, forceCLaim)) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "INTERFACE CLAIM SUCCESSFUL", Toast.LENGTH_SHORT).show();
                            }
                        });
                        if (mEndPoint == null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "END POINT IS NULL", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        connection.controlTransfer(0x40, 0x03, 0x4138, 0, null, 0, 0);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "CONTROL TRANSFER SET", Toast.LENGTH_SHORT).show();
                            }
                        });

                        final int transferResult = connection.bulkTransfer(mEndPoint, testBytes, length, 10000);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "BULK TRANSFER RESULT: " + String.valueOf(transferResult), Toast.LENGTH_LONG).show();
                            }
                        });

                        if (transferResult == length || transferResult == 0) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "TRANSFER SUCCESSFUL", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "TRANSFER UNSUCCESSFUL", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "INTERFACE CLAIM UNSUCCESSFUL", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

}
