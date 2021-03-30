package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class IntraBankData {
    @SerializedName("fee")
    private String fee;
    @SerializedName("referenceCode")
    private String referenceCode;


    public IntraBankData(String fee, String referenceCode) {
        this.fee = fee;
        this.referenceCode = referenceCode;

    }


    public String getfee() {
        return fee;
    }

    public void Setfee(String accname) {
        this.fee = accname;
    }


    public String getreferenceCode() {
        return referenceCode;
    }

    public void setreferenceCode(String accnum) {
        this.referenceCode = accnum;
    }


}