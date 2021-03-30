package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class GetCitiesData implements  Comparable<GetCitiesData>  {


    @SerializedName("citycode")
    private String citycode;

    @SerializedName("cityname")
    private String cityname;

    public GetCitiesData(String citycode, String cityname) {

        this.citycode = citycode;

        this.cityname = cityname;

    }


    public String getcitycode() {
        return citycode;
    }

    public void setcitycode(String accnum) {
        this.citycode = accnum;
    }


    public String getcityname() {
        return cityname;
    }

    public void setcityname(String accnum) {
        this.cityname = accnum;
    }

    @Override
    public int compareTo(GetCitiesData o) {
        return this.cityname.compareTo(o.cityname); // dog name sort in ascending order
        //return o.getName().compareTo(this.name); use this line for dog name sort in descending order
    }

    @Override
    public String toString() {
        return this.cityname;
    }


}