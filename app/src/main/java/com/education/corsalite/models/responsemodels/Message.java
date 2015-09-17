package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by vissu on 9/12/15.
 */
public class Message extends BaseModel  implements Serializable {

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
