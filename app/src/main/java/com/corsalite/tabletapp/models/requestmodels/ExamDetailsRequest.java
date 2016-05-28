package com.corsalite.tabletapp.models.requestmodels;

import com.corsalite.tabletapp.models.responsemodels.ExamDetail;
import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vissu on 10/15/15.
 */
public class ExamDetailsRequest {

    @SerializedName("UpdateTime")
    public String updateTime;
    @SerializedName("idStudent")
    public String studentId;
    @Ignore
    @SerializedName("ExamDetails")
    public List<ExamDetail> examDetails;

    public ExamDetailsRequest() {
    }

    public ExamDetailsRequest(String studentId, ExamDetail examDetail) {
        this.updateTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.studentId = studentId;
        examDetails = new ArrayList<>();
        examDetails.add(examDetail);
    }

}
