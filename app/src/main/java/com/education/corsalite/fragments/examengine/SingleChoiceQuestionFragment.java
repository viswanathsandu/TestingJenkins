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

public class SingleChoiceQuestionFragment extends ChoiceQuestionFragment {

    @Override
    public String getCorrectAnswer() {
        for (int i=0; i<question.answerChoice.size(); i++) {
            if(question.answerChoice.get(i).isCorrectAnswer.equalsIgnoreCase("Y")) {
                return String.valueOf(i);
            }
        }
        return null;
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
        if(!TextUtils.isEmpty(question.selectedAnswers)) {
            int index = Integer.parseInt(question.selectedAnswers);
            options[index].setChecked(true);
        }
    }
}
