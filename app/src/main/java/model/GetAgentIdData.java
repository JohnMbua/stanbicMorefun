package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;


public class GetAgentIdData {
    @SerializedName("id")
    private String id;
    @SerializedName("imgLoc")
    private String imgLoc;



    public GetAgentIdData(String id,String imgLoc) {
        this.id = id;
        this.imgLoc = imgLoc;


    }


    public String getId() {
        return id;
    }

    public void setId(String accname) {
        this.id = accname;
    }


    public String getimgLoc() {
        return imgLoc;
    }

    public void setimgLoc(String accname) {
        this.imgLoc = accname;
    }

}