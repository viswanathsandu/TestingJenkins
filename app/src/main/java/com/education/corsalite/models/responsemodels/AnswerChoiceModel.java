package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mt0060 on 03/11/15.
 */
public class AnswerChoiceModel extends BaseModel implements Serializable {


    private String idAnswerKey;
    @SerializedName("IsCorrectAnswer")
    private String isCorrectAnswer;
    @SerializedName("AnswerChoiceTextHtml")
    private String answerChoiceTextHtml;
    @SerializedName("AnswerChoiceExplanationHtml")
    private String answerChoiceExplanationHtml;
    @SerializedName("AnswerKeyText")
    private String answerKeyText;
}
