package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class GetBillersData implements  Comparable<GetBillersData> {

    @SerializedName("id")
    private String id;
    @SerializedName("billerId")
    private String billerId;
    @SerializedName("billerDesc")
    private String billerDesc;
    @SerializedName("billerName")
    private String billerName;
    @SerializedName("customerField")
    private String customerField;
    @SerializedName("charge")
    private String charge;
    @SerializedName("accnumber")
    private String accnumber;
    public GetBillersData(String id, String billerId,String billerDesc,String billerName,String customerField,String charge,String accnumber) {
        this.id = id;
        this.billerId = billerId;
        this.billerDesc = billerDesc;
        this.billerName = billerName;
        this.customerField = customerField;
        this.charge = charge;
        this.accnumber = accnumber;
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

    public String getAcnumber() {
        return accnumber;
    }

    public void setAcnumber(String accnum) {
        this.accnumber = accnum;
    }

    public String getcharge() {
        return charge;
    }

    public void setcharge(String charge) {
        this.charge = charge;
    }
    public String getcustomerField() {
        return customerField;
    }

    public void setcustomerField(String customerField) {
        this.customerField = customerField;
    }

    @Override
    public int compareTo(GetBillersData o) {
        return this.billerName.compareTo(o.billerName); // dog name sort in ascending order
        //return o.getName().compareTo(this.name); use this line for dog name sort in descending order
    }

    @Override
    public String toString() {
        return this.billerName;
    }

}