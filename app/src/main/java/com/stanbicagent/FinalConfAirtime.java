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
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.vipul.hp_hp.library.Layout_to_Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class FinalConfAirtime extends Fragment  implements View.OnClickListener{
    TextView reccustid,recamo,rectelco,recfee,rectrref,recagcmn,txtrfcd;
    Button btnsub;
    String txtcustid, amou ,narra, ednamee,ednumbb,serviceid,billid,strfee,strtref,stragcms;
    ProgressDialog prgDialog,prgDialog2;
    String telcoop;
    EditText amon, edacc,pno,txtamount,txtnarr,edname,ednumber;


    Button shareImage,repissue;
    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    String   txtrfc;
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

    Layout_to_Image layout_to_image;
    LinearLayout relativeLayout;
    Bitmap bitmap;
    public FinalConfAirtime() {
        // Required empty public constructor
    }
  /*  private static Fragment newInstance(Context context) {
        LayoutOne f = new LayoutOne();

        return f;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.finalconfairtime, null);
        reccustid = (TextView) root.findViewById(R.id.textViewnb2);
        txtrfcd = (TextView) root.findViewById(R.id.txtrfcd);

        recamo = (TextView) root.findViewById(R.id.textViewrrs);
        rectelco = (TextView) root.findViewById(R.id.textViewrr);

        recagcmn = (TextView) root.findViewById(R.id.txtaccom);
        recfee = (TextView) root.findViewById(R.id.txtfee);
        rectrref = (TextView) root.findViewById(R.id.txref);
        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);



        btnsub = (Button) root.findViewById(R.id.button2);
        btnsub.setOnClickListener(this);

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            txtcustid = bundle.getString("mobno");
            amou = bundle.getString("amou");
            telcoop = bundle.getString("telcoop");

 String txtamou = Utility.returnNumberFormat(amou);
            if(txtamou.equals("0.00")){
                txtamou = amou;
            }

            billid = bundle.getString("billid");
            serviceid = bundle.getString("serviceid");
            strfee = bundle.getString("fee");
            strtref = bundle.getString("tref");
            stragcms = Utility.returnNumberFormat(bundle.getString("agcmsn"));

            String   txtrfc = bundle.getString("refcode");
            txtrfcd.setText(txtrfc);
            reccustid.setText(txtcustid);
            recfee.setText(ApplicationConstants.KEY_NAIRA+strfee);
            rectrref.setText(strtref);

            recamo.setText(ApplicationConstants.KEY_NAIRA+txtamou);
            rectelco.setText(telcoop);
            recagcmn.setText(ApplicationConstants.KEY_NAIRA+stragcms);

        /*   try {
                findBT();
                openBT();
            } catch (IOException ex) {
                ex.printStackTrace();
            }*/

        }

        shareImage = (Button) root.findViewById(R.id.share_image);
        shareImage.setOnClickListener(this);

        repissue = (Button) root.findViewById(R.id.reportiss);
        repissue.setOnClickListener(this);




     /*   mUsbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
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
        }
*/

        relativeLayout=(LinearLayout)root.findViewById(R.id.receipt);
        layout_to_image=new Layout_to_Image(getActivity(),relativeLayout);

        //now call the main working function ;) and hold the returned image in bitmap

        bitmap=layout_to_image.convert_layout();
        return root;
    }



    public void StartChartAct(int i){


    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), FMobActivity.class));
        }

        if(view.getId() == R.id.share_image){
          /*  String userid = Utility.gettUtilUserId(getActivity());
            String test =  "   \n \n    AIRTIME  \nUSERID: " + userid + " \nTelco: " + telcoop + " \nRef Number:" + txtrfc + " \nMobile Number:" + txtcustid + " \nAmount:" + amou + " Naira\n Sender Name:" + ednamee + " \n Fee:" + strfee + " Naira \n \n \n \n";


            print(mConnection, mInterface,test);
*/
            if(Utility.checkPermission(getActivity())) {
                shareImage(getImageUri(getActivity(), bitmap));
            }
        }
        if(view.getId() == R.id.reportiss) {
            try {
                String userid = Utility.gettUtilUserId(getActivity());
                sendData("   \n \n    AIRTIME  \nUSERID: " + userid + " \nTelco: " + telcoop + " \nRef Number:" + txtrfc + " \nMobile Number:" + txtcustid + " \nAmount:" + amou + " Naira\n Sender Name:" + ednamee + " \n Fee:" + strfee + " Naira \n \n \n \n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }
    private void shareImage(Uri imagePath) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imagePath);
        startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
    }
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
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
}
