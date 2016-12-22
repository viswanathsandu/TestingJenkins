package com.education.corsalite.fragments.examengine;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.education.corsalite.R;
import com.education.corsalite.event.UpdateAnswerEvent;
import com.education.corsalite.models.responsemodels.AnswerChoiceModel;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by vissu on 12/21/16.
 */

public abstract class TextBoxQuestionFragment extends BaseQuestionFragment {

    protected EditText answerTxt;

    protected abstract void setTextProperties();

    @Override
    public void loadAnswerLayout() {
        answerLayout.setOrientation(LinearLayout.VERTICAL);
        List<AnswerChoiceModel> answerChoiceModels = question.answerChoice;
        if(question.answerChoice != null && !question.answerChoice.isEmpty()) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout container = (LinearLayout) inflater.inflate(R.layout.exam_engine_alphanumeric, null);
            answerTxt = (EditText) container.findViewById(R.id.answer_txt);
            answerTxt.addTextChangedListener(getTextWatcher());
            answerLayout.addView(container);
        }
        updateAnswer();
    }

    private TextWatcher getTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                question.selectedAnswers = s.toString();
                question.selectedAnswerKeyIds = question.answerChoice.get(0).idAnswerKey;
                EventBus.getDefault().post(new UpdateAnswerEvent());
            }
        };
    }

    @Override
    public void updateAnswer() {
        answerTxt.setText(TextUtils.isEmpty(question.selectedAnswers) ? "" : question.selectedAnswers);
    }

    @Override
    public String getCorrectAnswer() {
        return question.answerChoice.get(0).answerChoiceTextHtml;
    }

    @Override
    public void clearAnswer() {
        question.selectedAnswers = null;
        question.selectedAnswerKeyIds = null;
        updateAnswer();
        EventBus.getDefault().post(new UpdateAnswerEvent());
    }
}
