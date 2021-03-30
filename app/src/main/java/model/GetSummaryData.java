package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class GetSummaryData {

    @SerializedName("txnCode")
    private String txnCode;

    @SerializedName("amount")
    private String amount;
    @SerializedName("status")
    private String status;

    public GetSummaryData(String txnCode, String txndateTime, String amount, String status) {
        this.txnCode = txnCode;

        this.amount = amount;
        this.status = status;

    }


    public String getTxnCode() {
        return txnCode;
    }

    public void SetTxnCode(String accname) {
        this.txnCode = accname;
    }




    public String getamount() {
        return amount;
    }

    public void setamount(String accname) {
        this.amount = accname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String accname) {
        this.status = accname;
    }

}