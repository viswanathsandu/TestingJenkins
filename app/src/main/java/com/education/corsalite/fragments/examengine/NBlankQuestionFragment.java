package com.education.corsalite.fragments.examengine;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.enums.QuestionType;
import com.education.corsalite.event.UpdateAnswerEvent;
import com.education.corsalite.models.responsemodels.AnswerChoiceModel;

import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by vissu on 12/21/16.
 */

public class NBlankQuestionFragment extends BaseQuestionFragment {

    private QuestionType type;
    private ListView[] listViews;

    @Override
    public void loadAnswerLayout() {
        type = QuestionType.getQuestionType(question.idQuestionType);
        answerLayout.removeAllViews();
        List<AnswerChoiceModel> answerChoiceModels = question.answerChoice;
        if (answerChoiceModels == null || answerChoiceModels.isEmpty()) {
            return;
        }
        AnswerChoiceModel answerModel = answerChoiceModels.get(0);
        String[] lists = answerModel.answerChoiceTextHtml.split(":");
        listViews = new ListView[lists.length];
        answerLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < lists.length; i++) {
            String list = lists[i];
            String[] data = list.split("~");
            String header = data[0];
            String[] items = data[1].split(",");
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout container = (LinearLayout) inflater.inflate(R.layout.exam_engine_n_blank_answer, null);
            TextView headerTxt = (TextView) container.findViewById(R.id.header_txt);
            headerTxt.setText(header);
            ListView optionsListView = (ListView) container.findViewById(R.id.options_listview);
            optionsListView.setTag(header);
            listViews[i] = optionsListView;
            optionsListView.setChoiceMode(type == QuestionType.N_BLANK_SINGLE_SELECT
                    ? AbsListView.CHOICE_MODE_SINGLE
                    : AbsListView.CHOICE_MODE_MULTIPLE);
            optionsListView.setAdapter(new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, items));
            answerLayout.addView(container);
        }
        setupNBlankAnswerLogic(listViews);
        updateAnswer();
    }

    private void setupNBlankAnswerLogic(final ListView[] listviews) {
        for (int i = 0; i < listviews.length; i++) {
            listviews[i].setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String answersText = getAnswerForNBlankType(listviews);
                    question.selectedAnswers = answersText;
                    EventBus.getDefault().post(new UpdateAnswerEvent());
                }
            });
        }
    }

    private String getAnswerForNBlankType(ListView[] listviews) {
        String answer = "";
        for (int i = 0; i < listviews.length; i++) {
            String blankAnswer = "";
            SparseBooleanArray checkedItems = listviews[i].getCheckedItemPositions();
            for (int j = 0; j < checkedItems.size(); j++) {
                int key = checkedItems.keyAt(j);
                if (checkedItems.get(key)) {
                    blankAnswer += blankAnswer.isEmpty() ? "" : ",";
                    blankAnswer += listviews[i].getItemAtPosition(key);
                }
            }
            if (!blankAnswer.isEmpty()) {
                answer += answer.isEmpty() ? "" : ":";
                answer += listviews[i].getTag() + "~" + blankAnswer;
            }
        }
        return answer;
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
