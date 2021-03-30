package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class BalanceInquiry {
    @SerializedName("responseCode")
    private String responseCode;
    @SerializedName("errors")
    private String errors;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private BalInquiryData data;

    public BalanceInquiry(String respcode, String errors, String message,BalInquiryData data) {
        this.responseCode = respcode;
        this.errors = errors;
        this.message = message;
        this.data = data;
    }


    public String getRespCode() {
        return responseCode;
    }

    public void setResponseCode(String accname) {
        this.responseCode = accname;
    }


    public String getErrors() {
        return errors;
    }

    public void setErrors(String accname) {
        this.errors = accname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String mess) {
        this.message = mess;
    }

    public BalInquiryData getData() {
        return data;
    }

    public void setData(BalInquiryData mess) {
        this.data = mess;
    }

}