package model;

/**
 * Created by deeru on 18-10-2016.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class GetPerfData {
    @SerializedName("summary")
    private List<GetSummaryData> summdata;

    @SerializedName("transaction")
    private List<GetCommPerfData> results;

    public GetPerfData(List<GetSummaryData> summdata, List<GetCommPerfData> results) {

        this.summdata = summdata;
        this.results = results;
    }


    public List<GetSummaryData> getsummdata() {
        return summdata;
    }

    public void setsummdata(List<GetSummaryData> accname) {
        this.summdata = accname;
    }




    public List<GetCommPerfData>  getResults() {
        return results;
    }

    public void setResults(List<GetCommPerfData> results) {
        this.results = results;
    }
}