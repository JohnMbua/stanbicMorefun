package com.stanbicagent;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;

import java.util.HashMap;
import java.util.Iterator;

public class USBPrintTwo extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usbprint_two);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mDeviceList = mUsbManager.getDeviceList();

        mContext = this.getApplicationContext();

        mDeviceIterator = mDeviceList.values().iterator();

        Button print = (Button)findViewById(R.id.print);

        Toast.makeText(this, "Device List Size: " + String.valueOf(mDeviceList.size()), Toast.LENGTH_SHORT).show();
        TextView textView = (TextView) findViewById(R.id.usbDevice);
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
            Toast.makeText(this, "INTERFACE COUNT: " + String.valueOf(interfaceCount), Toast.LENGTH_SHORT).show();

            mDevice = usbDevice1;

            if (mDevice == null) {
                Toast.makeText(this, "mDevice is null", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "mDevice is not null", Toast.LENGTH_SHORT).show();
            }
            textView.setText(usbDevice);
        }

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        mUsbManager.requestPermission(mDevice, mPermissionIntent);

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                print(mConnection, mInterface);
            }
        });
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


    private void print(final UsbDeviceConnection connection, final UsbInterface usbInterface) {
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

                    String test = "THIS IS A PRINT TEST\r\n";
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
