package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class GetAgentRequests  {
    @SerializedName("requestedVisitDate")
    private String requestedVisitDate;
    @SerializedName("reason")
    private String reason;
    @SerializedName("status")
    private String status;
    @SerializedName("id")
    private String id;
    public GetAgentRequests(String requestedVisitDate, String reason,String status,String idd) {
        this.requestedVisitDate = requestedVisitDate;
        this.reason = reason;
        this.status = status;
        this.id = idd;

    }


    public String getrequestedVisitDate() {
        return requestedVisitDate;
    }

    public void SetrequestedVisitDate(String accname) {
        this.requestedVisitDate = accname;
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