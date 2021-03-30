package adapter;

import android.os.Parcel;
import android.os.Parcelable;

public class BillMenuParcelable implements Parcelable {
    //property basics

    private String serviceid;
    private String servicename;


    private String billid;
    private String billname;
    private String idd;
    private String servlabel;
    private String stracnumber;
    private String paymentCode;
    private String packid;
    private String charge;
    private String dispname;



    private String recanno;
    private String amou;
    private String narra;
    private String ednamee;
    private String ednumbb;

    //main constructor
    public BillMenuParcelable( String serviceid, String servicename, String billid, String billname,String idd, String servlabel, String stracnumber,String paymentCode, String packid, String charge,String dispname, String recanno,String amou, String narra, String ednamee,String ednumbb){

        this.serviceid = serviceid;
        this.servicename = servicename;
        this.billid = billid;
        this.billname = billname;
        this.idd = idd;
        this.servlabel = servlabel;
        this.stracnumber = stracnumber;
        this.paymentCode = paymentCode;
        this.packid = packid;
        this.charge = charge;
        this.dispname = dispname;


        this.recanno = recanno;
        this.amou = amou;
        this.narra = narra;
        this.ednamee = ednamee;
        this.ednumbb = ednumbb;

    }


    protected BillMenuParcelable(Parcel in) {

        serviceid = in.readString();
        servicename = in.readString();
        billid = in.readString();
        billname = in.readString();
        idd = in.readString();


        servlabel = in.readString();
        stracnumber = in.readString();
        paymentCode = in.readString();
        packid = in.readString();
        charge = in.readString();
        dispname = in.readString();



        recanno = in.readString();
        amou = in.readString();
        narra = in.readString();
        ednamee = in.readString();
        ednumbb = in.readString();
    }

    public static final Creator<BillMenuParcelable> CREATOR = new Creator<BillMenuParcelable>() {
        @Override
        public BillMenuParcelable createFromParcel(Parcel in) {
            return new BillMenuParcelable(in);
        }

        @Override
        public BillMenuParcelable[] newArray(int size) {
            return new BillMenuParcelable[size];
        }
    };


    public String getserviceid() {return serviceid; }
    public String getservicename() {return servicename; }
    public String getbillid() {return billid; }
    public String getbillname() {return billname; }

    public String getidd() { return idd; }
    public String getservlabel() { return servlabel; }
    public String getstracnumber() { return stracnumber; }
    public String getpaymentCode() { return paymentCode; }
    public String getpackid() { return packid; }
    public String getcharge() { return charge; }

    public String getdispname() { return dispname; }




    public String getRecanno() { return recanno; }
    public String getAmou() { return amou; }
    public String getNarra() { return narra; }
    public String getEdnamee() { return ednamee; }

    public String getEdnumbb() { return ednumbb; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(serviceid);
        parcel.writeString(servicename);
        parcel.writeString(billid);
        parcel.writeString(billname);
        parcel.writeString(idd);

        parcel.writeString(servlabel);
        parcel.writeString(stracnumber);
        parcel.writeString(paymentCode);
        parcel.writeString(packid);
        parcel.writeString(charge);
        parcel.writeString(dispname);


        parcel.writeString(recanno);
        parcel.writeString(amou);
        parcel.writeString(narra);
        parcel.writeString(ednamee);
        parcel.writeString(ednumbb);
    }

}