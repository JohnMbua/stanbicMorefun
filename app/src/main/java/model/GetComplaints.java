package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class GetComplaints {
    @SerializedName("complaindate")
    private String complaindate;
    @SerializedName("reason")
    private String reason;
    @SerializedName("status")
    private String status;
    @SerializedName("id")
    private String id;
    @SerializedName("type")
    private String type;
    public GetComplaints(String complaindate, String reason, String status, String idd, String type) {
        this.complaindate = complaindate;
        this.reason = reason;
        this.status = status;
        this.id = idd;
        this.type = type;

    }


    public String getrequestedVisitDate() {
        return complaindate;
    }

    public void SetrequestedVisitDate(String accname) {
        this.complaindate = accname;
    }


    public String getreason() {
        return reason;
    }

    public void setreason(String accnum) {
        this.reason = accnum;
    }

    public String getstatus() {
        return status;
    }

    public void setstatus(String accnum) {
        this.status = accnum;
    }

    public String getIdd() {
        return id;
    }

    public void setIdd(String accnum) {
        this.id = accnum;
    }

}