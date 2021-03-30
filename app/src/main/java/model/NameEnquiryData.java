package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class NameEnquiryData {
    @SerializedName("accountName")
    private String accountName;
    @SerializedName("accountNumber")
    private String accountNumber;


    public NameEnquiryData(String accname, String accnum) {
        this.accountName = accname;
        this.accountNumber = accnum;

    }


    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accname) {
        this.accountName = accname;
    }


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accnum) {
        this.accountNumber = accnum;
    }

}