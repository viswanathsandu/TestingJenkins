package com.corsalite.tabletapp.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 9/12/15.
 */
public class Message extends BaseModel {

    @SerializedName("idStudent")
    public String studentId;
    @SerializedName("idMessage")
    public String messageId;
    @SerializedName("Message")
    public String message;
    @SerializedName("EffectiveDate")
    public String effectiveDate;
    @SerializedName("TermDate")
    public String termDate;
}
