package com.education.corsalite.fragments.examengine;

import android.text.InputType;

/**
 * Created by vissu on 12/22/16.
 */

public class NumericQuestionFragment extends TextBoxQuestionFragment {

    @Override
    protected void setTextProperties() {
        answerTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
        answerTxt.setHint("Numeric");
    }
}
