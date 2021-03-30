package com.stanbicagent;


import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.MediaStore;

import androidx.fragment.app.Fragment;
import security.SecurityLayer;
import utils.FileUtils;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class FinalConfDepoTrans extends Fragment implements View.OnClickListener{
    TextView recacno,recname,recamo,recnarr,recsendnum,recsendnam,recfee,recagcmn,txtrfcd;
    Button btnsub;
    RelativeLayout rlsave, rlshare,rlprint;
    String recanno, amou ,narra, ednamee,ednumbb,txtname,strfee,stragcms ;
    ProgressDialog prgDialog,prgDialog2;
    RelativeLayout rlsendname,rlsendno;
    EditText etpin;
    int check = 0;
    String   txtrfc;
    Layout_to_Image layout_to_image;  //Create Object of Layout_to_Image Class

    LinearLayout relativeLayout;   //Define Any Layout
    Button shareImage,repissue;
    Bitmap bitmap;                  //Bitmap for holding Image of layout


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

    HashMap<String, UsbDevice> mDeviceList;
    Iterator<UsbDevice> mDeviceIterator;

    DeviceServiceEngine mSDKManager;
    public FinalConfDepoTrans() {
        // Required empty public constructor
    }
  /*  private static Fragment newInstance(Context context) {
        LayoutOne f = new LayoutOne();

        return f;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.finalconfirmcashdepo, null);
        recacno = (TextView) root.findViewById(R.id.textViewnb2);
        txtrfcd = (TextView) root.findViewById(R.id.txtrfcd);
        recname = (TextView) root.findViewById(R.id.textViewcvv);
        etpin = (EditText) root.findViewById(R.id.pin);
        recamo = (TextView) root.findViewById(R.id.textViewrrs);
        recnarr = (TextView) root.findViewById(R.id.textViewrr);
        recfee = (TextView) root.findViewById(R.id.txtfee);
        recagcmn = (TextView) root.findViewById(R.id.txtaccom);
        recsendnam = (TextView) root.findViewById(R.id.sendnammm);
        recsendnum = (TextView) root.findViewById(R.id.sendno);
        shareImage = (Button) root.findViewById(R.id.share_image);
        shareImage.setOnClickListener(this);

        repissue = (Button) root.findViewById(R.id.reportiss);
        repissue.setOnClickListener(this);
        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);
        rlsendname = (RelativeLayout) root.findViewById(R.id.rlsendnam);
        rlsendno = (RelativeLayout) root.findViewById(R.id.rlsendnum);
        btnsub = (Button) root.findViewById(R.id.button2);
        btnsub.setOnClickListener(this);
        relativeLayout=(LinearLayout)root.findViewById(R.id.receipt);

        rlprint = (RelativeLayout) root.findViewById(R.id.rlprint);

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            recanno = bundle.getString("recanno");
            amou = bundle.getString("amou");
            narra = bundle.getString("narra");
            ednamee = bundle.getString("ednamee");
            ednumbb = bundle.getString("ednumbb");
            txtname = bundle.getString("txtname");
          txtrfc = bundle.getString("refcode");
            txtrfcd.setText(txtrfc);
            stragcms = Utility.returnNumberFormat(bundle.getString("agcmsn"));
            String trantype =  bundle.getString("trantype");
            strfee = bundle.getString("fee");
            if(trantype.equals("D")){
                rlsendname.setVisibility(View.GONE);
                rlsendno.setVisibility(View.GONE);
            }
            recacno.setText(recanno);
            recname.setText(txtname);
            recamo.setText(ApplicationConstants.KEY_NAIRA+amou);
            recnarr.setText(narra);
            recfee.setText(ApplicationConstants.KEY_NAIRA+strfee);
            recsendnam.setText(ednamee);
            recsendnum.setText(ednumbb);
            recagcmn.setText(ApplicationConstants.KEY_NAIRA+stragcms);

          /*  try {
                findBT();
                openBT();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
*/


        }


        layout_to_image=new Layout_to_Image(getActivity(),relativeLayout);

        //now call the main working function ;) and hold the returned image in bitmap

        bitmap=layout_to_image.convert_layout();



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



/*
        mUsbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        mDeviceList = mUsbManager.getDeviceList();

        mContext = this.getActivity().getApplicationContext();

        mDeviceIterator = mDeviceList.values().iterator();



        Toast.makeText(getActivity(), "Device List Size: " + String.valueOf(mDeviceList.size()), Toast.LENGTH_SHORT).show();

        String usbDevice = "";
        Log.v("Device List Size",Integer.toString(mDeviceList.size()));
        while (mDeviceIterator.hasNext()) {
            UsbDevice usbDevice1 = mDeviceIterator.next();
            usbDevice += "\n" +
                    "DeviceID: " + usbDevice1.getDeviceId() + "\n" +
                    "DeviceName: " + usbDevice1.getDeviceName() + "\n" +
                    "Protocol: " + usbDevice1.getDeviceProtocol() + "\n" +
                    //"Product Name: " + usbDevice1.getProductName() +"\n" +
                    // "Manufacturer Name: " + usbDevice1.getManufacturerName() + "\n" +
                    "DeviceClass: " + usbDevice1.getDeviceClass() + " - " + translateDeviceClass(usbDevice1.getDeviceClass()) + "\n" +
                    "DeviceSubClass: " + usbDevice1.getDeviceSubclass() + "\n" +
                    "VendorID: " + usbDevice1.getVendorId() + "\n" +
                    "ProductID: " + usbDevice1.getProductId() + "\n";

            int interfaceCount = usbDevice1.getInterfaceCount();
            Toast.makeText(getActivity(), "INTERFACE COUNT: " + String.valueOf(interfaceCount), Toast.LENGTH_SHORT).show();

            mDevice = usbDevice1;

            if (mDevice == null) {
                Toast.makeText(getActivity(), "mDevice is null", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(), "mDevice is not null", Toast.LENGTH_SHORT).show();
            }

        }

        mPermissionIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        getActivity().registerReceiver(mUsbReceiver, filter);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        if(mDeviceList.size() > 0) {
            mUsbManager.requestPermission(mDevice, mPermissionIntent);
        }else{
            Toast.makeText(getActivity(), "No USB printers have connected", Toast.LENGTH_SHORT).show();
        }*/




        return root;
    }

    private void Check_Printer() {
        try {
            com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
            if (printer.getStatus() != 0) {
                check = -1;
            } else {
                check = 0;
            }
            Print_Card_Receipt();;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void Print_Card_Receipt() throws RemoteException {
        com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();

        String auth_id = ApplicationClass.get().getGlobalVals().getSessAuth();
        amou= ApplicationClass.get().getGlobalVals().getSessAmt();
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

        String mer_name="";
        String loc=" ";
        String tid="";
        String mid="";
        String send_date="";
        String send_time="";
        String cholder_name="";
        String tntp="Card test";
        String txn_status="Great";
        String ctyp="";
        String aid="";
        String tsi="";
        String rrn="123456789012";
        String rpc="00";
        String narration="Nah";
        String appvers="ApVer";

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
                bitmap = BitmapFactory.decodeStream(getContext().getAssets().open("receipt_logo.bmp"));
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

            int i = printer.appendPrnStr("AGENT: " + mer_name, fontSize, false);
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
            printer.appendPrnStr("SERVED BY: " + Utility.gettUtilUserId(getContext()), fontSize, false);
            printer.appendPrnStr("   **** MERCHANT COPY ****", fontSize, false);
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
//        ApplicationClass.get().getGlobalVals().setReprintData(tntp + "^" + crdN + "^" + amou + "^" +  recanno + "^" + rrn + "^" + rpc + "^" + narra + "^"+cholder_name+ "^" + auth_id + "^" + narration+"^"+""+ctyp+"^"+aid+"^"+tsi+"^"+loc);

    }


    private String maskString(String strText, int start, int end, char maskChar)
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
/*    private void Print_Attempt_Cash_Transfer(final String rpc_code, final String rpc_message, final String refno) {
        DateFormat t_df = new SimpleDateFormat("H:mm a");
        final DateFormat d_df = new SimpleDateFormat("EEE, MMM d, y");
        final String time = t_df.format(Calendar.getInstance().getTime());
        final String date = d_df.format(Calendar.getInstance().getTime());
        final String usid = Utility.gettUtilUserId(getActivity());
        final String agentid = Utility.gettUtilAgentId(getActivity());
        final String store = "WESTGATE";
        String [] Tags = new String[]{"ACCOUNT NUMBER", "ACCOUNT HOLDER", "AGENT NAME", "LOCATION", "STORE", "AGENT ID"};
        String [] Values = new String[]{"0726773920", "RAYMOND RONO", "WESTGATE STALL", "NAIROBI", store, agentid};
        new Thread(new Runnable() {
            public void run() {
                PrinterTester.getInstance().init();
                //PrinterTester.getInstance().fontSet((EFontTypeAscii) FONT_16_24,
                //		(EFontTypeExtCode) FONT_16_16);
			*//*	PrinterTester.getInstance().printBitmap(
						BitmapFactory.decodeResource(getResources(), R.drawable.safaricom));*//*
                PrinterTester.getInstance().spaceSet(Byte.parseByte("0"),
                        Byte.parseByte("0"));
                PrinterTester.getInstance().leftIndents(Short.parseShort("0"));
                PrinterTester.getInstance().setGray(Integer.parseInt("1"));

                PrinterTester.getInstance().printStr(" FIRSTBANK OF NIGERIA LIMITED\n\n\n", null);
                PrinterTester.getInstance().printStr("ACCOUNT NUMBER:       " + recanno + "\n\n", null);
                PrinterTester.getInstance().printStr("ACCOUNT HOLDER: " + txtname + "\n\n\n", null);
              //  PrinterTester.getInstance().printStr("AGENT NAME: " + "WESTGATE STALL" + "\n\n", null);
            //    PrinterTester.getInstance().printStr("LOCATION: " + "LAGOS" + "\n\n", null);
              //  PrinterTester.getInstance().printStr("STORE ID:           " + store + "\n\n", null);
                PrinterTester.getInstance().printStr("AGENT NAME:             " + agentid + "\n\n", null);
                PrinterTester.getInstance().printStr("--------------------------------\n", null);
                //PrinterTester.getInstance().fontSet((EFontTypeAscii) FONT_16_32,
                //		(EFontTypeExtCode) FONT_16_16);
                PrinterTester.getInstance().printStr("CASH DEPOSIT \n", null);
                PrinterTester.getInstance().printStr("AMT: " + amou + "\n", null);
                PrinterTester.getInstance().printStr("FEE: " + strfee + "\n", null);
                if (rpc_code.equals("00")) {
                    PrinterTester.getInstance().printStr("APPROVED\n", null);
                }else{
                    PrinterTester.getInstance().printStr("DECLINED\n", null);
                }
                //PrinterTester.getInstance().fontSet((EFontTypeAscii) FONT_16_24,
                //		(EFontTypeExtCode) FONT_16_16);
                PrinterTester.getInstance().printStr("--------------------------------\n\n", null);
                if (!rpc_code.equals("00")) {
                    PrinterTester.getInstance().printStr("RN: " + rpc_message + "\n\n", null);
                }
                PrinterTester.getInstance().printStr("RESPONSE CODE:              00 \n\n", null);
                if (!refno.equals("")) {
                    PrinterTester.getInstance().printStr("REF NO       : " + txtrfc + "\n\n", null);
                }
                PrinterTester.getInstance().printStr("DATE         : " + date + "\n\n", null);
                PrinterTester.getInstance().printStr("TIME         :          " + time + "\n\n", null);
                //PrinterTester.getInstance().printStr("APP VER      :             " + getText(R.string.app_version) + "\n\n\n", null);
                PrinterTester.getInstance().printStr("YOU WERE SERVED BY " + usid + "\n\n", null);
                PrinterTester.getInstance().printStr("<<<<<     CUSTOMER COPY    >>>>>\n", null);
                PrinterTester.getInstance().step(150);
                PrinterTester.getInstance().start();
            }
        }).start();
    }*/

    // Share image
    private void shareImage(Uri imagePath) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imagePath);
        startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
    }
    public void StartChartAct(int i){


    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
           getActivity().finish();
            startActivity(new Intent(getActivity(), FMobActivity.class));
        }
        if(view.getId() == R.id.share_image){
           /* String userid = Utility.gettUtilUserId(getActivity());
          String test =  "   \n \n    TRANSFER TO FIRSTBANK  \nUSERID: "+userid+" \nReceipient Name: "+txtname+" \nRef Number:"+txtrfc+" \nAccount Number:"+recanno+" \nAmount:"+amou+" Naira\n Sender Name:"+ednamee+" \n Fee:"+strfee+" Naira \n \n \n \n";


            print(mConnection, mInterface,test);*/
           if(Utility.checkPermission(getActivity())) {
               shareImage(getImageUri(getActivity(), bitmap));
           }
        }
        if(view.getId() == R.id.reportiss) {
            try {
                String userid = Utility.gettUtilUserId(getActivity());
                sendData("   \n \n    TRANSFER TO FIRSTBANK  \nUSERID: "+userid+" \nReceipient Name: "+txtname+" \nRef Number:"+txtrfc+" \nAccount Number:"+recanno+" \nAmount:"+amou+" Naira\n Sender Name:"+ednamee+" \n Fee:"+strfee+" Naira \n \n \n \n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            /*Bundle b = new Bundle();
            b.putString("recanno", recanno);
            b.putString("amou", amou);
            b.putString("narra", narra);
            b.putString("ednamee", ednamee);
            b.putString("refno", txtrfc);
            b.putString("txtime", txtname);

            Fragment fragment = new LogComplaint();

            fragment.setArguments(b);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //  String tag = Integer.toString(title);
            fragmentTransaction.replace(R.id.container_body, fragment, "Confirm Transfer");
            fragmentTransaction.addToBackStack("Confirm Transfer");
            ((FMobActivity) getActivity())
                    .setActionBarTitle("Confirm Transfer");
            fragmentTransaction.commit();*/
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

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

            if(mBluetoothAdapter == null) {
                Toast.makeText(
                        getActivity(),
                        "No bluetooth available" ,
                        Toast.LENGTH_LONG).show();
            }

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.getName().equals("BlueTooth Printer")) {
                        mmDevice = device;
                        break;
                    }
                }
            }



        }catch(Exception e){
            e.printStackTrace();
        }
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
                bmp =  getResizedBitmap(bmp, 120, 360);
                if(bmp!=null){
                    byte[] command = UtilsPhoto.decodeBitmap(bmp);
                  //  mmOutputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    mmOutputStream.write(command);

                    mmOutputStream.write(msg.getBytes());
                }else{
                    Log.e("Print Photo error", "the file isn't exists");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PrintTools", "the file isn't exists");
            }
            //  mmOutputStream.write(msg.getBytes());
            //  sendData(bytes);
            //   mmOutputStream.write(bytes);


            //  ImageIO.write(mmOutputStream, "PNG", myNewPNGFile);


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




    private String translateDeviceClass(int deviceClass){
        switch(deviceClass){
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
            default: return "Unknown USB class!";
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


    private void print(final UsbDeviceConnection connection, final UsbInterface usbInterface,final String test) {
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
                    byte[] testBytes =  test.getBytes();

                    int length = testBytes.length;
                    Log.v("Bytes Length",Integer.toString(length));

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
                    if(connection.claimInterface(usbInterface, forceCLaim)){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "INTERFACE CLAIM SUCCESSFUL", Toast.LENGTH_SHORT).show();
                            }
                        });
                        if (mEndPoint == null){
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

                        if(transferResult == length || transferResult == 0) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "TRANSFER SUCCESSFUL", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else{
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "TRANSFER UNSUCCESSFUL", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else{
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
