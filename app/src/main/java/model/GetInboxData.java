package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class GetInboxData implements  Comparable<GetInboxData> {

    @SerializedName("id")
    private String id;
    @SerializedName("billerId")
    private String billerId;
    @SerializedName("billerDesc")
    private String billerDesc;
    @SerializedName("billerName")
    private String billerName;

    public GetInboxData(String id, String billerId, String billerDesc, String billerName) {
        this.id = id;
        this.billerId = billerId;
        this.billerDesc = billerDesc;
        this.billerName = billerName;

    }


    public String getId() {
        return id;
    }

    public void SetID(String accname) {
        this.id = accname;
    }


    public String getBillerId() {
        return billerId;
    }

    public void setBillerId(String accnum) {
        this.billerId = accnum;
    }

    public String getBillerDesc() {
        return billerDesc;
    }

    public void SetBillerDesc(String accname) {
        this.billerDesc = accname;
    }


    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String accnum) {
        this.billerName = accnum;
    }

    @Override
    public int compareTo(GetInboxData o) {
        return this.billerName.compareTo(o.billerName); // dog name sort in ascending order
        //return o.getName().compareTo(this.name); use this line for dog name sort in descending order
    }

    @Override
    public String toString() {
        return this.billerName;
    }

}