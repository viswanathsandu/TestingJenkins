package com.education.corsalite.fragments.examengine;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.enums.QuestionType;
import com.education.corsalite.event.UpdateAnswerEvent;
import com.education.corsalite.models.responsemodels.AnswerChoiceModel;
import com.education.corsalite.utils.L;

import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by vissu on 12/21/16.
 */

public class GridQuestionFragment extends BaseQuestionFragment {

    private QuestionType type;
    private String[] leftLabels;
    private String[] topLabels;
    private CheckBox[][] checkBoxes;

    @Override
    public void loadAnswerLayout() {
        answerLayout.removeAllViews();
        List<AnswerChoiceModel> answerChoiceModels = question.answerChoice;
        if (answerChoiceModels == null || answerChoiceModels.isEmpty()) {
            return;
        }
        AnswerChoiceModel answerModel = answerChoiceModels.get(0);
        leftLabels = answerModel.answerChoiceTextHtml.split("-")[0].split(",");
        topLabels = answerModel.answerChoiceTextHtml.split("-")[1].split(",");
        checkBoxes = new CheckBox[leftLabels.length][topLabels.length];
        View container = getActivity().getLayoutInflater().inflate(R.layout.grid_table_layout, null);
        TableLayout tableLayout = (TableLayout) container.findViewById(R.id.grid_layout);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        for (int i = 0; i < leftLabels.length; i++) {
            if (i == 0) {
                TableRow row = new TableRow(getActivity());
                row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                for (int j = 0; j < topLabels.length + 1; j++) {
                    View titleLayout = inflater.inflate(R.layout.grid_question_title_layout, null);
                    TextView titleTxt = (TextView) titleLayout.findViewById(R.id.title_txt);
                    titleTxt.setText(j == 0 ? "" : topLabels[j - 1]);
                    row.addView(titleLayout);
                }
                tableLayout.addView(row);
            }
            TableRow row = new TableRow(getActivity());
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            for (int j = 0; j < topLabels.length; j++) {
                if (j == 0) {
                    View titleLayout = inflater.inflate(R.layout.grid_question_title_layout, null);
                    TextView titleTxt = (TextView) titleLayout.findViewById(R.id.title_txt);
                    titleTxt.setText(leftLabels[i]);
                    row.addView(titleLayout);
                }
                View checkBoxLayout = inflater.inflate(R.layout.grid_question_checkbox_layout, null);
                CheckBox checkBox = (CheckBox) checkBoxLayout.findViewById(R.id.checkbox);
                checkBox.setTag(String.format("%s-%s", leftLabels[i], topLabels[j]));
                checkBoxes[i][j] = checkBox;
                row.addView(checkBoxLayout);
            }
            tableLayout.addView(row);
        }
        setupAnswerLogic();
        answerLayout.addView(container);
        loadAnswersIntoGridType();
    }

    private void setupAnswerLogic() {
        for (int i = 0; i < checkBoxes.length; i++) {
            for (int j = 0; j < checkBoxes[i].length; j++) {
                checkBoxes[i][j].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String answersText = getAnswerForGridType();
                        question.selectedAnswers = answersText;
                        btnVerify.setEnabled(!answersText.toString().trim().isEmpty());
                        updateAnswer();
                    }
                });
            }
        }
    }

    private String getAnswerForGridType() {
        String answers = "";
        for (int i = 0; i < checkBoxes.length; i++) {
            for (int j = 0; j < checkBoxes[i].length; j++) {
                if (checkBoxes[i][j].isChecked()) {
                    answers += (answers.isEmpty() ? "" : ",") + checkBoxes[i][j].getTag().toString();
                }
            }
        }
        return answers;
    }

    private void loadAnswersIntoGridType() {
        try {
            String answers = question.selectedAnswers;
            if (TextUtils.isEmpty(answers)) {
                return;
            }
            List<String> leftLabelsList = Arrays.asList(leftLabels);
            List<String> topLabelsList = Arrays.asList(topLabels);
            String[] answersArr = answers.split(",");
            if (answersArr != null) {
                for (int i = 0; i < answersArr.length; i++) {
                    String[] labels = answersArr[i].split("-");
                    if (labels != null && labels.length >= 2) {
                        checkBoxes[leftLabelsList.indexOf(labels[0])][topLabelsList.indexOf(labels[1])].setChecked(true);
                    }
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    @Override
    public void updateAnswer() {
        String answer = question.selectedAnswers;
        if (checkBoxes == null) {
            return;
        }
        if (TextUtils.isEmpty(answer)) {
            for (int i = 0; i < checkBoxes.length; i++) {
                for (int j = 0; j < checkBoxes[i].length; j++) {
                    checkBoxes[i][j].setChecked(false);
                }
            }
        } else {
            loadAnswersIntoGridType();
        }
        EventBus.getDefault().post(new UpdateAnswerEvent());
    }

    @Override
    public String getCorrectAnswer() {
        return question.answerChoice.get(0).answerKeyText;
    }

    @Override
    public String getDisplayedCorrectAnswer() {
        return getCorrectAnswer();
    }

    @Override
    public void clearAnswer() {
        question.selectedAnswers = null;
        question.selectedAnswerKeyIds = null;
        updateAnswer();
        EventBus.getDefault().post(new UpdateAnswerEvent());
    }
}
