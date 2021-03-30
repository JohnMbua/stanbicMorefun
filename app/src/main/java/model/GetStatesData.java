package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class GetStatesData  implements  Comparable<GetStatesData>  {


    @SerializedName("stateCode")
    private String stateCode;

    @SerializedName("stateName")
    private String stateName;

    public GetStatesData(String stateCode, String stateName) {

        this.stateCode = stateCode;

        this.stateName = stateName;

    }


    public String getstateCode() {
        return stateCode;
    }

    public void setstateCode(String accnum) {
        this.stateCode = accnum;
    }


    public String getstateName() {
        return stateName;
    }

    public void setstateName(String accnum) {
        this.stateName = accnum;
    }

    @Override
    public int compareTo(GetStatesData o) {
        return this.stateName.compareTo(o.stateName); // dog name sort in ascending order
        //return o.getName().compareTo(this.name); use this line for dog name sort in descending order
    }

    @Override
    public String toString() {
        return this.stateName;
    }


}