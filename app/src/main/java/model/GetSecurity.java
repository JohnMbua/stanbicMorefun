package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class GetSecurity {
    @SerializedName("inp")
    private String inp;
    @SerializedName("pkey")
    private String pkey;
    @SerializedName("skey")
    private String skey;


    public GetSecurity(String inp, String pkey, String skey) {
        this.inp = inp;
        this.pkey = pkey;
        this.skey = skey;

    }


    public String getinp() {
        return inp;
    }

    public void setinp(String accname) {
        this.inp = accname;
    }


    public String getpkey() {
        return pkey;
    }

    public void setpkey(String pkey) {
        this.pkey = pkey;
    }

    public String getskey() {
        return skey;
    }

    public void setskey(String skey) {
        this.skey = skey;
    }


}