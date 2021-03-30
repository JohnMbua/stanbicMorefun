package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class LoginData {
    @SerializedName("userName")
    private String userName;
    @SerializedName("mobileNo")
    private String mobileNo;
    @SerializedName("lastLoggedIn")
    private String lastLoggedIn;
    @SerializedName("userId")
    private String userId;
    @SerializedName("email")
    private String email;
    @SerializedName("agent")
    private String agent;
    @SerializedName("acountNumber")
    private String acountNumber;
    public LoginData(String mobno, String usnam,String lastlg,String userid,String email,String agent,String acountNumber) {
        this.mobileNo = mobno;
        this.userName = usnam;
        this.lastLoggedIn = lastlg;
        this.userId = userid;
        this.agent = agent;
        this.email = email;
        this.acountNumber = acountNumber;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String usname) {
        this.userName = usname;
    }


    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String accnum) {
        this.mobileNo = accnum;
    }


    public String getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(String accnum) {
        this.lastLoggedIn = accnum;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserID(String accnum) {
        this.userId = accnum;
    }

    public String getEmaill() {
        return email;
    }
    public void setEmaill(String accnum) {
        this.email = accnum;
    }

    public String getAgentID() {
        return agent;
    }

    public void setAgentID(String accnum) {
        this.agent = accnum;
    }

    public String getAccountNo() {
        return acountNumber;
    }

    public void setAcountNumber(String accnum) {
        this.acountNumber = accnum;
    }


}