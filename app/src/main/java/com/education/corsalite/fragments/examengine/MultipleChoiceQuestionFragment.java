package com.education.corsalite.fragments.examengine;

import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.AnswerChoiceModel;

/**
 * Created by vissu on 12/21/16.
 */

public class MultipleChoiceQuestionFragment extends ChoiceQuestionFragment {

    @Override
    public String getCorrectAnswer() {
        String answer = null;
        for (int i = 0; i < question.answerChoice.size(); i++) {
            if (question.answerChoice.get(i).isCorrectAnswer.equalsIgnoreCase("Y")) {
                answer = (answer == null) ? String.valueOf(i) : String.format(",%s", i);
            }
        }
        return answer;
    }

    @Override
    protected CompoundButton[] createOptions(int size) {
        return new CompoundButton[size];
    }

    @Override
    protected CompoundButton getOption(View container, AnswerChoiceModel choice) {
        RadioButton optionRBtn = (RadioButton) container.findViewById(R.id.option_radio_button);
        optionRBtn.setId(Integer.valueOf(choice.idAnswerKey));
        optionRBtn.setTag(choice);
        return optionRBtn;
    }

    @Override
    protected int getOptionsLayoutId() {
        return R.layout.exam_engine_radio_btn;
    }

    @Override
    protected void loadSelectedAnswers() {
        if (!TextUtils.isEmpty(question.selectedAnswers)) {
            if (!question.selectedAnswers.contains(",")) {
                int index = Integer.parseInt(question.selectedAnswers);
                options[index].setChecked(true);
            } else {
                String [] selectedChoices = question.selectedAnswers.split(",");
                for(int i=0; i<selectedChoices.length; i++) {
                    int index = Integer.parseInt(selectedChoices[i]);
                    options[index].setChecked(true);
                }
            }
        }
    }
}
