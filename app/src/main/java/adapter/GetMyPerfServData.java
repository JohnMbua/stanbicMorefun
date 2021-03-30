package adapter;

/**
 * Created by deeru on 18-10-2016.
 */

public class GetMyPerfServData  {

    private String servname;

    private String servcount;
private String servcommvalue;

    public GetMyPerfServData(String servname, String servcount,String servcommvalue) {
        this.servname = servname;
        this.servcount = servcount;
        this.servcommvalue = servcommvalue;

    }


    public String getServname() {
        return servname;
    }

    public void SetServname(String accname) {
        this.servname = accname;
    }


    public String getServcount() {
        return servcount;
    }

    public void setServcount(String accnum) {
        this.servcount = accnum;
    }

    public String getServCommValue() {
        return servcommvalue;
    }

    public void setServCommValue(String accnum) {
        this.servcommvalue = accnum;
    }


}