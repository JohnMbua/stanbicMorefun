package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class GetAgentId {
    @SerializedName("responseCode")
    private String responseCode;
    @SerializedName("errors")
    private String errors;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<GetAgentIdData> data;

    public GetAgentId(String respcode, String errors, String message, List<GetAgentIdData> data) {
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

    public List<GetAgentIdData> getData() {
        return data;
    }

    public void setData(List<GetAgentIdData> mess) {
        this.data = mess;
    }

}