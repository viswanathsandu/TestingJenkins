package com.education.corsalite.models.responsemodels;

import com.education.corsalite.utils.Constants;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Girish on 03/11/15.
 */
public class ExamModel extends BaseModel implements Serializable, Comparable {

    public String idTestQuestion;
    @SerializedName("VideoUrl")
    public String videoUrl;
    @SerializedName("QueSortOrder")
    public String queSortOrder;
    @SerializedName("RecommendedTime")
    public String recommendedTime;
    @SerializedName("MultiChoiceQuestionOption")
    public String multiChoiceQuestionOption;
    @SerializedName("DisplayName")
    public String displayName;
    @SerializedName("LastModifiedDateTime")
    public String lastModifiedDateTime;
    @SerializedName("ExerciseNumber")
    public String exerciseNumber;
    public String idQuestionParagraph;
    @SerializedName("ParagraphHtml")
    public String paragraphHtml;
    public String idQuestion;
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
    @SerializedName("AnswerChoice")
    public List<AnswerChoiceModel> answerChoice;

    public Constants.AnswerState answerColorSelection = Constants.AnswerState.UNATTEMPTED;
    public String selectedAnswers;

    @Override
    public String toString() {
        return displayName;
    }

    @Override
    public int compareTo(Object another) {
        int compareQuestionId=Integer.valueOf(((ExamModel) another).idQuestionType);
        return Integer.valueOf(this.idQuestionType) - compareQuestionId;
    }
}
