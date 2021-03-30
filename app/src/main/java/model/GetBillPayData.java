package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class GetBillPayData implements  Comparable<GetBillPayData> {

    @SerializedName("packid")
    private String packid;
    @SerializedName("billerId")
    private String billerId;

    @SerializedName("displayName")
    private String displayName;
    @SerializedName("paymentCode")
    private String paymentCode;
    @SerializedName("charge")
    private String charge;
    public GetBillPayData(String packid, String displayName, String billerId,  String paymentCode,String charge) {
        this.packid = packid;
        this.billerId = billerId;

        this.displayName = displayName;
        this.paymentCode = paymentCode;
        this.charge = charge;
    }


    public String getpackid() {
        return packid;
    }

    public void Setpackid(String packid) {
        this.packid = packid;
    }


    public String getBillerId() {
        return billerId;
    }

    public void setBillerId(String accnum) {
        this.billerId = accnum;
    }




    public String getdisplayName() {
        return displayName;
    }

    public void setdisplayName(String accnum) {
        this.displayName = accnum;
    }

    public String getpaymentCode() {
        return paymentCode;
    }

    public void setpaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }
    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    @Override
    public int compareTo(GetBillPayData o) {
        return this.displayName.compareTo(o.displayName); // dog name sort in ascending order
        //return o.getName().compareTo(this.name); use this line for dog name sort in descending order
    }

    @Override
    public String toString() {
        return this.displayName;
    }

}