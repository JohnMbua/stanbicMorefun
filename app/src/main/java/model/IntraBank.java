package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class IntraBank {
    @SerializedName("responseCode")
    private String responseCode;
    @SerializedName("errors")
    private String errors;
    @SerializedName("message")
    private String message;
    @SerializedName("fee")
    private String fee;
    @SerializedName("data")
    private IntraBankData results;

    public IntraBank(String respcode, String errors, String message,String fee,IntraBankData results) {
        this.responseCode = respcode;
        this.errors = errors;
        this.message = message;
        this.fee = fee;
        this.results = results;
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

    public void setMessage(String accname) {
        this.message = accname;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String accname) {
        this.fee = accname;
    }
    public IntraBankData  getResults() {
        return results;
    }

    public void setResults(IntraBankData results) {
        this.results = results;
    }

}