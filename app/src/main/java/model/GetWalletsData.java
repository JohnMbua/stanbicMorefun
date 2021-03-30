package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class GetWalletsData implements  Comparable<GetWalletsData> {
    @SerializedName("instName")
    private String instName;
    @SerializedName("bankCode")
    private String bankCode;


    public GetWalletsData(String bankname, String bankCode) {
        this.instName = bankname;
        this.bankCode = bankCode;

    }


    public String getBankName() {
        return instName;
    }

    public void SetBankname(String accname) {
        this.instName = accname;
    }


    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String accnum) {
        this.bankCode = accnum;
    }

    @Override
    public int compareTo(GetWalletsData o) {
        return this.instName.compareTo(o.instName); // dog name sort in ascending order
        //return o.getName().compareTo(this.name); use this line for dog name sort in descending order
    }

    @Override
    public String toString() {
        return this.instName;
    }

}