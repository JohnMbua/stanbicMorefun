package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class MinistatData {
    @SerializedName("tran_date")
    private String tran_date;
    @SerializedName("tran_remark")
    private String tran_remark;
    @SerializedName("credit_debit")
    private String credit_debit;
    @SerializedName("tran_amt")
    private String tran_amt;

    public MinistatData(String tran_date, String tran_remark,String creddeb,String amnt) {
        this.tran_date = tran_date;
        this.tran_remark = tran_remark;
        this.credit_debit = creddeb;
        this.tran_amt = amnt;
    }


    public String getTranDate() {
        return tran_date;
    }

    public void SetTranDate(String accname) {
        this.tran_date = accname;
    }


    public String getTranRemark() {
        return tran_remark;
    }

    public void setTranRemark(String accnum) {
        this.tran_remark = accnum;
    }


    public String getCredDeb() {
        return credit_debit;
    }

    public void SetCredDeb(String accname) {
        this.credit_debit = accname;
    }


    public String getTranAmount() {
        return tran_amt;
    }

    public void setTranAmount(String accnum) {
        this.tran_amt = accnum;
    }
}