package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class GetFee {
    @SerializedName("responseCode")
    private String responseCode;
    @SerializedName("fee")
    private String fee;
    @SerializedName("message")
    private String message;


    public GetFee(String respcode, String fee, String message) {
        this.responseCode = respcode;
        this.fee = fee;
        this.message = message;

    }


    public String getRespCode() {
        return responseCode;
    }

    public void setResponseCode(String accname) {
        this.responseCode = accname;
    }


    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String mess) {
        this.message = mess;
    }


}