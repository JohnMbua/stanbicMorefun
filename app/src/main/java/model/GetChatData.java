package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class GetChatData  {
    @SerializedName("id")
    private String id;
    @SerializedName("message")
    private String message;

    @SerializedName("creationDate")
    private String creationDate;
    @SerializedName("responseMessage")
    private String responseMessage;

    @SerializedName("responseTime")
    private String responseTime;
    @SerializedName("makerId")
    private String makerId;

    public GetChatData(String id, String message,String creationDate, String responseMessage,String responseTime, String makerId) {
        this.id = id;
        this.message = message;

        this.creationDate = creationDate;
        this.responseMessage = responseMessage;

        this.responseTime = responseTime;
        this.message = message;

    }


    public String getid() {
        return id;
    }

    public void Setid(String accname) {
        this.id = accname;
    }


    public String getmessage() {
        return message;
    }

    public void setmessage(String accnum) {
        this.message = accnum;
    }


    public String getcreationDate() {
        return creationDate;
    }

    public void SetcreationDate(String accname) {
        this.creationDate = accname;
    }


    public String getresponseMessage() {
        return responseMessage;
    }

    public void setresponseMessage(String accnum) {
        this.responseMessage = accnum;
    }

    public String getresponseTime() {
        return responseTime;
    }

    public void SetresponseTime(String accname) {
        this.responseTime = accname;
    }


    public String getmakerId() {
        return makerId;
    }

    public void setmakerId(String accnum) {
        this.makerId = accnum;
    }



}