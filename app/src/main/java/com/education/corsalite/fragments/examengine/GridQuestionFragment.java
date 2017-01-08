package com.education.corsalite.fragments.examengine;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
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
    private ListView[] listViews;

    @Override
    public void loadAnswerLayout() {

        answerLayout.removeAllViews();
        List<AnswerChoiceModel> answerChoiceModels = question.answerChoice;
        if (answerChoiceModels == null || answerChoiceModels.isEmpty()) {
            return;
        }
        AnswerChoiceModel answerModel = answerChoiceModels.get(0);
        final String[] leftLabels = answerModel.answerChoiceTextHtml.split("-")[0].split(",");
        final String[] topLabels = answerModel.answerChoiceTextHtml.split("-")[1].split(",");
        CheckBox[][] checkBoxes = new CheckBox[leftLabels.length][topLabels.length];
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
                checkBoxes[i][j] = checkBox;
                row.addView(checkBoxLayout);
            }
            tableLayout.addView(row);
        }
        setupAnswerLogic(leftLabels, topLabels, checkBoxes);
        answerLayout.addView(container);
        loadAnswersIntoGridType(checkBoxes, leftLabels, topLabels, question.selectedAnswers);
    }

    private void setupAnswerLogic(final String[] leftLabels, final String[] topLabels, final CheckBox[][] checkboxes) {
        for (int i = 0; i < checkboxes.length; i++) {
            for (int j = 0; j < checkboxes[i].length; j++) {
                checkboxes[i][j].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String answersText = getAnswerForGridType(leftLabels, topLabels, checkboxes);
                        question.selectedAnswers = answersText;
                        btnVerify.setEnabled(!answersText.toString().trim().isEmpty());
                    }
                });
            }
        }
    }

    private String getAnswerForGridType(String[] leftLabels, String[] topLabels, final CheckBox[][] checkboxes) {
        String answers = "";
        for (int i = 0; i < checkboxes.length; i++) {
            for (int j = 0; j < checkboxes[i].length; j++) {
                if (checkboxes[i][j].isChecked()) {
                    answers += (answers.isEmpty() ? "" : ",") + leftLabels[i] + "-" + topLabels[j];
                }
            }
        }
        return answers;
    }

    private void loadAnswersIntoGridType(CheckBox[][] checkboxes, String[] leftLabels, String[] topLabels, String answers) {
        try {
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
                        checkboxes[leftLabelsList.indexOf(labels[0])][topLabelsList.indexOf(labels[1])].setChecked(true);
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
        if (listViews == null) {
            return;
        }
        if (TextUtils.isEmpty(answer)) {
            for (ListView listView : listViews) {
                for (int j = 0; j < listView.getAdapter().getCount(); j++) {
                    listView.setItemChecked(j, false);
                }
            }
        } else {
            String[] lists = answer.split(":");
            for (int i = 0; i < lists.length; i++) {
                String[] data = lists[i].split("~");
                String header = data[0];
                List<String> items = Arrays.asList(data[1].split(","));
                for (ListView listView : listViews) {
                    if (listView.getTag().equals(header)) {
                        for (int j = 0; j < listView.getAdapter().getCount(); j++) {
                            for (String item : items) {
                                if (item.equals(listView.getAdapter().getItem(j))) {
                                    listView.setItemChecked(j, true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getCorrectAnswer() {
        return question.answerChoice.get(0).answerKeyText;
    }

    @Override
    public void clearAnswer() {
        question.selectedAnswers = null;
        question.selectedAnswerKeyIds = null;
        updateAnswer();
        EventBus.getDefault().post(new UpdateAnswerEvent());
    }
}
