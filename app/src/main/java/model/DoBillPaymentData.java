package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class DoBillPaymentData {
    @SerializedName("refNumber")
    private String refNumber;
    @SerializedName("fee")
    private String fee;



    public DoBillPaymentData(String fee, String refnum) {
        this.refNumber = refnum;
        this.fee = fee;

    }


    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String accname) {
        this.refNumber = accname;
    }

    public String getfee() {
        return fee;
    }

    public void Setfee(String accname) {
        this.fee = accname;
    }


}