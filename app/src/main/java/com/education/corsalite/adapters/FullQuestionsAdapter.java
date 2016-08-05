package com.education.corsalite.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.enums.QuestionType;
import com.education.corsalite.models.responsemodels.AnswerChoiceModel;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.L;
import com.education.corsalite.views.CorsaliteWebViewClient;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Viswanath
 */
public class FullQuestionsAdapter extends AbstractRecycleViewAdapter {

    private LayoutInflater inflater;
    private Context mContext;

    public FullQuestionsAdapter(List<ExamModel> questions) {
        addAll(questions);
    }

    public FullQuestionsAdapter(Context context, List<ExamModel> questions, LayoutInflater inflater) {
        this(questions);
        this.mContext = context;
        this.inflater = inflater;
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.question_recyclerview_item, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            ((QuestionViewHolder) holder).loadQuestion(position);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {

        public View parent;
        @Bind(R.id.comment_txt)
        TextView commentTxt;
        @Bind(R.id.tv_serial_no)
        TextView questionNumberTxt;
        @Bind(R.id.question_webview)
        WebView questionWebview;
        @Bind(R.id.paragraph_webview)
        WebView paragraphWebview;
        @Bind(R.id.answer_layout)
        LinearLayout answersContainer;

        public QuestionViewHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void loadQuestion(int position) {
            try {
                ExamModel question = (ExamModel) getItem(position);
                commentTxt.setText(question.comment);
                questionNumberTxt.setText("Q" + (position + 1) + ")");
                if (TextUtils.isEmpty(question.paragraphHtml)) {
                    paragraphWebview.setVisibility(View.GONE);
                } else {
                    paragraphWebview.setVisibility(View.VISIBLE);
                    paragraphWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    paragraphWebview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                    paragraphWebview.loadData(question.paragraphHtml, "text/html; charset=UTF-8", null);
                }
                questionWebview.setVisibility(View.GONE);
                if (question.questionHtml != null) {
                    questionWebview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                    questionWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    questionWebview.loadData(question.questionHtml, "text/html; charset=UTF-8", null);
                    questionWebview.setVisibility(View.VISIBLE);
                }
                switch (QuestionType.getQuestionType(question.idQuestionType)) {
                    case SINGLE_SELECT_CHOICE:
                        loadSingleSelectChoiceAnswers(question);
                        break;
                    case MULTI_SELECT_CHOICE:
                        loadMultiSelectChoiceAnswers(question);
                        break;
                    case ALPHANUMERIC:
                        loadEditTextAnswer(QuestionType.ALPHANUMERIC, question);
                        break;
                    case NUMERIC:
                        loadEditTextAnswer(QuestionType.NUMERIC, question);
                        break;
                    case FILL_IN_THE_BLANK:
                        loadEditTextAnswer(QuestionType.FILL_IN_THE_BLANK, question);
                        break;
                    case N_BLANK_SINGLE_SELECT:
                        loadNBlankAnswer(QuestionType.N_BLANK_SINGLE_SELECT, question);
                        break;
                    case N_BLANK_MULTI_SELECT:
                        loadNBlankAnswer(QuestionType.N_BLANK_MULTI_SELECT, question);
                        break;
                    case GRID:
                        loadGridTypeAnswer(question);
                        break;
                    case FRACTION:
                    case PICK_A_SENTENCE:
                    case WORD_PROPERTIES:
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                L.error(e.getMessage(), e);
            }
        }

        private void loadSingleSelectChoiceAnswers(ExamModel question) {
            answersContainer.removeAllViews();
            List<AnswerChoiceModel> answerChoiceModels = question.answerChoice;
            final int size = answerChoiceModels.size();
            final RadioButton[] optionRadioButtons = new RadioButton[size];
            for (int i = 0; i < size; i++) {
                final AnswerChoiceModel answerChoiceModel = answerChoiceModels.get(i);
                LinearLayout container = (LinearLayout) inflater.inflate(R.layout.exam_engine_radio_btn, null);
                TextView optionNumberTxt = (TextView) container.findViewById(R.id.option_number_txt);
                optionNumberTxt.setText((i + 1) + "");
                RadioButton optionRBtn = (RadioButton) container.findViewById(R.id.option_radio_button);
                optionRBtn.setId(Integer.valueOf(answerChoiceModel.idAnswerKey));
                optionRBtn.setTag(answerChoiceModel);
                optionRBtn.setClickable(false);
                optionRadioButtons[i] = optionRBtn;
                WebView webview = (WebView) container.findViewById(R.id.webview);
                webview.setScrollbarFadingEnabled(true);
                webview.getSettings().setLoadsImagesAutomatically(true);
                webview.getSettings().setJavaScriptEnabled(true);
                webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                webview.setWebChromeClient(new WebChromeClient());
                webview.setWebViewClient(new MyWebViewClient(mContext));
                webview.loadData(answerChoiceModel.answerChoiceTextHtml, "text/html; charset=UTF-8", null);
                answersContainer.addView(container);
            }
        }

        private void loadMultiSelectChoiceAnswers(ExamModel question) {
            answersContainer.removeAllViews();
            List<AnswerChoiceModel> answerChoiceModels = question.answerChoice;
            final int size = answerChoiceModels.size();
            final CheckBox[] checkBoxes = new CheckBox[size];
            for (int i = 0; i < size; i++) {
                final AnswerChoiceModel answerChoiceModel = answerChoiceModels.get(i);
                LinearLayout container = (LinearLayout) inflater.inflate(R.layout.exam_engine_check_box, null);
                TextView optionNumberTxt = (TextView) container.findViewById(R.id.option_number_txt);
                optionNumberTxt.setText((i + 1) + "");
                CheckBox optionCheckbox = (CheckBox) container.findViewById(R.id.option_check_box);
                optionCheckbox.setId(Integer.valueOf(answerChoiceModel.idAnswerKey));
                optionCheckbox.setTag(answerChoiceModel);
                optionCheckbox.setClickable(false);
                checkBoxes[i] = optionCheckbox;
                checkBoxes[i].setId(Integer.valueOf(answerChoiceModel.idAnswerKey));
                checkBoxes[i].setTag(answerChoiceModel);
                WebView optionWebView = (WebView) container.findViewById(R.id.webview);
                optionWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                optionWebView.setScrollbarFadingEnabled(true);
                optionWebView.getSettings().setLoadsImagesAutomatically(true);
                optionWebView.getSettings().setJavaScriptEnabled(true);
                optionWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                optionWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                optionWebView.setWebChromeClient(new WebChromeClient());
                optionWebView.setWebViewClient(new MyWebViewClient(mContext));
                if (answerChoiceModel.answerChoiceTextHtml.startsWith("./") && answerChoiceModel.answerChoiceTextHtml.endsWith(".html")) {
                    answerChoiceModel.answerChoiceTextHtml = answerChoiceModel.answerChoiceTextHtml.replace("./", ApiClientService.getBaseUrl());
                    optionWebView.loadUrl(answerChoiceModel.answerChoiceTextHtml);
                } else {
                    optionWebView.loadData(answerChoiceModel.answerChoiceTextHtml, "text/html; charset=UTF-8", null);
                }
                answersContainer.addView(container);
            }
        }

        private void loadEditTextAnswer(QuestionType type, ExamModel question) {
            answersContainer.removeAllViews();
            List<AnswerChoiceModel> answerChoiceModels = question.answerChoice;
            if (!answerChoiceModels.isEmpty()) {
                LinearLayout container = (LinearLayout) inflater.inflate(R.layout.exam_engine_alphanumeric, null);
                EditText answerTxt = (EditText) container.findViewById(R.id.answer_txt);
                answerTxt.setEnabled(false);
                switch (type) {
                    case ALPHANUMERIC:
                    case FILL_IN_THE_BLANK:
                        answerTxt.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case NUMERIC:
                        answerTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
                        answerTxt.setHint("Numeric");
                        break;
                }
                answersContainer.addView(container);
            }
        }

        private void loadNBlankAnswer(QuestionType type, ExamModel question) {
            answersContainer.removeAllViews();
            List<AnswerChoiceModel> answerChoiceModels = question.answerChoice;
            if (answerChoiceModels == null || answerChoiceModels.isEmpty()) {
                return;
            }
            AnswerChoiceModel answerModel = answerChoiceModels.get(0);
            String[] lists = answerModel.answerChoiceTextHtml.split(":");
            final ListView[] listViews = new ListView[lists.length];
            for (int i = 0; i < lists.length; i++) {
                String list = lists[i];
                String[] data = list.split("~");
                String header = data[0];
                String[] items = data[1].split(",");
                LinearLayout container = (LinearLayout) inflater.inflate(R.layout.exam_engine_n_blank_answer, null);
                TextView headerTxt = (TextView) container.findViewById(R.id.header_txt);
                headerTxt.setText(header);
                ListView optionsListView = (ListView) container.findViewById(R.id.options_listview);
                optionsListView.setEnabled(false);
                optionsListView.setTag(header);
                listViews[i] = optionsListView;
                optionsListView.setChoiceMode(type == QuestionType.N_BLANK_SINGLE_SELECT
                        ? AbsListView.CHOICE_MODE_SINGLE
                        : AbsListView.CHOICE_MODE_MULTIPLE);
                optionsListView.setAdapter(new ArrayAdapter<String>(mContext,
                        android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, items));
                answersContainer.addView(container);
            }
        }

        private void loadGridTypeAnswer(ExamModel question) {
            try {
                answersContainer.removeAllViews();
                List<AnswerChoiceModel> answerChoiceModels = question.answerChoice;
                if (answerChoiceModels == null || answerChoiceModels.isEmpty()) {
                    return;
                }
                AnswerChoiceModel answerModel = answerChoiceModels.get(0);
                final String[] leftLabels = answerModel.answerChoiceTextHtml.split("-")[0].split(",");
                final String[] topLabels = answerModel.answerChoiceTextHtml.split("-")[1].split(",");
                CheckBox[][] checkBoxes = new CheckBox[leftLabels.length][topLabels.length];
                View container = inflater.inflate(R.layout.grid_table_layout, null);
                TableLayout tableLayout = (TableLayout) container.findViewById(R.id.grid_layout);
                for (int i = 0; i < leftLabels.length; i++) {
                    if (i == 0) {
                        TableRow row = new TableRow(mContext);
                        row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        for (int j = 0; j < topLabels.length + 1; j++) {
                            View titleLayout = inflater.inflate(R.layout.grid_question_title_layout, null);
                            TextView titleTxt = (TextView) titleLayout.findViewById(R.id.title_txt);
                            titleTxt.setText(j == 0 ? "" : topLabels[j - 1]);
                            row.addView(titleLayout);
                        }
                        tableLayout.addView(row);
                    }
                    TableRow row = new TableRow(mContext);
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
                answersContainer.addView(container);
            } catch (Exception e) {
                L.error(e.getMessage(), e);
            }
        }


    }

    private class MyWebViewClient extends CorsaliteWebViewClient {
        public MyWebViewClient(Context context) {
            super(context);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (checkNetconnection(view, url)) {
                return true;
            }
            if (Uri.parse(url).getHost().contains("corsalite.com")) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mContext.startActivity(intent);
            return true;
        }
    }

}