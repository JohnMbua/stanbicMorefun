package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class BalInquiryData {
    @SerializedName("balance")
    private String balance;
    @SerializedName("commision")
    private String commision;


    public BalInquiryData(String balance, String commision) {
        this.balance = balance;
        this.commision = commision;

    }


    public String getbalance() {
        return balance;
    }

    public void setbalance(String accname) {
        this.balance = accname;
    }


    public String getcommision() {
        return commision;
    }

    public void setcommision(String accnum) {
        this.commision = accnum;
    }

}