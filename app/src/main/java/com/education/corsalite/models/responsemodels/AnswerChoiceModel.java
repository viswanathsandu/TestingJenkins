package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mt0060 on 03/11/15.
 */
public class AnswerChoiceModel extends BaseModel implements Serializable {


    public String idAnswerKey;
    @SerializedName("IsCorrectAnswer")
    public String isCorrectAnswer;
    @SerializedName("AnswerChoiceTextHtml")
    public String answerChoiceTextHtml;
    @SerializedName("AnswerChoiceExplanationHtml")
    public String answerChoiceExplanationHtml;
    @SerializedName("AnswerKeyText")
    public String answerKeyText;
}
