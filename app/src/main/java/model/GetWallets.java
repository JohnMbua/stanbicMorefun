package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class GetWallets {
    @SerializedName("responseCode")
    private String responseCode;
    @SerializedName("errors")
    private String errors;
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<GetWalletsData> results;

    public GetWallets(String respcode, String errors, String message, List<GetWalletsData> results) {
        this.responseCode = respcode;
        this.errors = errors;
        this.message = message;
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


    public List<GetWalletsData>  getResults() {
        return results;
    }

    public void setResults(List<GetWalletsData> results) {
        this.results = results;
    }
}