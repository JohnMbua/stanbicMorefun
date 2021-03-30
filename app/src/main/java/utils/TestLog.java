package utils;

import android.util.Log;

public class TestLog {

    private String childName = "";

    public TestLog() {
        childName = getClass().getSimpleName() + ".";
    }

    public void logTrue(String method) {
        Log.i("IPPITest", childName + method);
    }

    public void logErr(String method, String errString) {

    }
}
