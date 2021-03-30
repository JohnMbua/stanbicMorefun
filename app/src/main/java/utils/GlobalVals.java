package utils;

import com.stanbicagent.BuildConfig;

public class GlobalVals {


    private String SimSerial;
    private String TermID;
    private String tranType;
    private String sessAccNo;
    private String sessAmt;
    private String reprintData;
    private String txnSta;
    private String resp;
    private String narr;
    private String feep;

    public String getFeep() {
        return feep;
    }

    public void setFeep(String feep) {
        this.feep = feep;
    }

    public String getTxnSta() {
        return txnSta;
    }

    public void setTxnSta(String txnSta) {
        this.txnSta = txnSta;
    }

    public String getResp() {
        return resp;
    }

    public void setResp(String resp) {
        this.resp = resp;
    }

    public String getNarr() {
        return narr;
    }

    public void setNarr(String narr) {
        this.narr = narr;
    }

    public String getReprintData() {
        return reprintData;
    }

    public void setReprintData(String reprintData) {
        this.reprintData = reprintData;
    }

    public String getSessPan() {
        return sessPan;
    }

    public void setSessPan(String sessPan) {
        this.sessPan = sessPan;
    }

    private String sessPan;

    public String getSessAmt() {
        return sessAmt;
    }

    public void setSessAmt(String sessAmt) {
        this.sessAmt = sessAmt;
    }

    public String getSessAccNo() {
        return sessAccNo;
    }

    public void setSessAccNo(String sessAccNo) {
        this.sessAccNo = sessAccNo;
    }

    public String getSessAuth() {
        return sessAuth;
    }

    public void setSessAuth(String sessAuth) {
        this.sessAuth = sessAuth;
    }

    private String sessAuth;

    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
    }

    public String getTermID() {
        return TermID;
    }

    public void setTermID(String termID) {
        if(BuildConfig.DEBUG)
             TermID = termID;
        else
            TermID = termID;
    }

    public GlobalVals(){

    }

    public String getSimSerial() {
//        return     SimSerial;
        return "89882390000043429797";
    }

    public void setSimSerial(String simSerial) {
        if(BuildConfig.DEBUG)
            SimSerial = simSerial;
        else
            SimSerial = simSerial;
    }

}
