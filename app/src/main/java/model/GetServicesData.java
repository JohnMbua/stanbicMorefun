package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class GetServicesData implements  Comparable<GetServicesData>  {
    @SerializedName("id")
    private String id;
    @SerializedName("label")
    private String label;
    @SerializedName("serviceName")
    private String serviceName;

    public GetServicesData(String id, String label, String serviceName) {
        this.serviceName = serviceName;
        this.label = label;
        this.id = id;

    }


    public String getId() {
        return id;
    }

    public void SetId(String accname) {
        this.id = accname;
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String accnum) {
        this.label = accnum;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String accnum) {
        this.serviceName = accnum;
    }

    @Override
    public int compareTo(GetServicesData o) {
        return this.serviceName.compareTo(o.serviceName); // dog name sort in ascending order
        //return o.getName().compareTo(this.name); use this line for dog name sort in descending order
    }

    @Override
    public String toString() {
        return this.serviceName;
    }

}
