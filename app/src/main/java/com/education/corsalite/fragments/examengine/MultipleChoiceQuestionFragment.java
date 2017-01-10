package com.education.corsalite.fragments.examengine;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.education.corsalite.R;
import com.education.corsalite.event.UpdateAnswerEvent;
import com.education.corsalite.models.responsemodels.AnswerChoiceModel;

import de.greenrobot.event.EventBus;

/**
 * Created by vissu on 12/21/16.
 */

public class MultipleChoiceQuestionFragment extends ChoiceQuestionFragment {

    @Override
    public String getCorrectAnswer() {
        String answer = null;
        for (int i = 0; i < question.answerChoice.size(); i++) {
            if (question.answerChoice.get(i).isCorrectAnswer.equalsIgnoreCase("Y")) {
                answer = (answer == null) ? String.valueOf(i) : String.format("%s,%s", answer, i);
            }
        }
        return answer;
    }

    @Override
    public String getDisplayedCorrectAnswer() {
        String answer = null;
        for (int i = 0; i < question.answerChoice.size(); i++) {
            if (question.answerChoice.get(i).isCorrectAnswer.equalsIgnoreCase("Y")) {
                answer = (answer == null) ? String.valueOf(i+1) : String.format("%s,%s", answer, i+1);
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
        CheckBox optionChk = (CheckBox) container.findViewById(R.id.option_check_box);
        optionChk.setId(Integer.valueOf(choice.idAnswerKey));
        optionChk.setTag(choice);
        return optionChk;
    }

    @Override
    protected int getOptionsLayoutId() {
        return R.layout.exam_engine_check_box;
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

    @Override
    public void updateAnswer(AnswerChoiceModel model) {
        selectedAnswers.add(question.answerChoice.indexOf(model));
        formatSelectedAnswers();
        updateAnswer();
        EventBus.getDefault().post(new UpdateAnswerEvent());
    }
}
