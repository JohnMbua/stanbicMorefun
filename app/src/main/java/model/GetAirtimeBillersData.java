package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class GetAirtimeBillersData implements  Comparable<GetAirtimeBillersData> {

    @SerializedName("sid")
    private String sid;
    @SerializedName("id")
    private String billerId;

    @SerializedName("billerName")
    private String billerName;

    public GetAirtimeBillersData(String sid, String billerId,  String billerName) {
        this.sid = sid;
        this.billerId = billerId;

        this.billerName = billerName;

    }


    public String getSId() {
        return sid;
    }

    public void SetSID(String accname) {
        this.sid = accname;
    }


    public String getBillerId() {
        return billerId;
    }

    public void setBillerId(String accnum) {
        this.billerId = accnum;
    }


    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String accnum) {
        this.billerName = accnum;
    }

    @Override
    public int compareTo(GetAirtimeBillersData o) {
        return this.billerName.compareTo(o.billerName); // dog name sort in ascending order
        //return o.getName().compareTo(this.name); use this line for dog name sort in descending order
    }

    @Override
    public String toString() {
        return this.billerName;
    }

}