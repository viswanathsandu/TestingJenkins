package com.education.corsalite.models.responsemodels;

import com.education.corsalite.utils.Constants;
import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

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
    @Ignore
    @SerializedName("ParagraphHtml")
    public String paragraphHtml;
    public String idQuestion;
    @Ignore
    @SerializedName("QuestionHtml")
    public String questionHtml;
    @Ignore
    @SerializedName("HintHtml")
    public String hintHtml;
    public String idQuestionType;
    @SerializedName("QuestionType")
    public String questionType;
    @Ignore
    @SerializedName("Comment")
    public String comment;
    @SerializedName("Complexity")
    public String complexity;
    @SerializedName("IsSelectedAnswer")
    public String isSelectedAnswer;
    @SerializedName("EnteredAnswerText")
    public String enteredAnswerText;
    @SerializedName("TimeTaken")
    public String timeTaken;
    @SerializedName("Score")
    public String scroe;

    @Ignore
    @SerializedName("AnswerChoice")
    public List<AnswerChoiceModel> answerChoice;
    public boolean isFlagged = false;

    // To be used by app only. This is not a part of response
    public String sectionName;

    public String answerColorSelection = Constants.AnswerState.UNATTEMPTED.getValue();
    public String selectedAnswers;
    @Ignore
    public String selectedAnswerKeyIds;

    public ExamModel() {
    }

    @Override
    public String toString() {
        return displayName;
    }

    @Override
    public int compareTo(Object another) {
        int compareQuestionId = Integer.valueOf(((ExamModel) another).idQuestionType);
        return Integer.valueOf(this.idQuestionType) - compareQuestionId;
    }
}
