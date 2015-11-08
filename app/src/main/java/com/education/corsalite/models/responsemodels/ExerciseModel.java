package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mt0060 on 03/11/15.
 */
public class ExerciseModel extends BaseModel implements Serializable {

    public String idQuestionParagraph;
    @SerializedName("ParagraphHtml")
    public String paragraphHtml;
    public String idQuestion;
    @SerializedName("ExerciseNumber")
    public String exerciseNumber;
    @SerializedName("QuestionHtml")
    public String questionHtml;
    @SerializedName("HintHtml")
    public String hintHtml;
    public String idQuestionType;
    @SerializedName("QuestionType")
    public String questionType;
    @SerializedName("Comment")
    public String comment;
    @SerializedName("Complexity")
    public String complexity;
    @SerializedName("LastModifiedDateTime")
    public String lastModifiedDateTime;
    @SerializedName("DisplayName")
    public String displayName;
    @SerializedName("AnswerChoice")
    public List<AnswerChoiceModel> answerChoice;

    @Override
    public String toString() {
        return displayName;
    }
}
