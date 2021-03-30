package printer;

import android.content.Context;

import adapter.USBAdapter;

/**
 * Created by ian on 4/10/2018.
 */

public class PrintOrder {

    public PrintOrder(){

    }

    public void Print(Context context, String msg) {
        USBAdapter usba = new USBAdapter();
        usba.createConn(context);
        try {
            usba.printMessage(context, msg);
            usba.closeConnection(context);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
