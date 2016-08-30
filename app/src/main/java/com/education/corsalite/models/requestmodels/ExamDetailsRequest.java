package com.education.corsalite.models.requestmodels;

import com.education.corsalite.models.responsemodels.ExamDetail;
import com.education.corsalite.utils.TimeUtils;
import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        this.updateTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(TimeUtils.getCurrentDate());
        this.studentId = studentId;
        examDetails = new ArrayList<>();
        examDetails.add(examDetail);
    }

}
