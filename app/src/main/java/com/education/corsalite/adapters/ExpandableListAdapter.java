package com.education.corsalite.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.ExamEngineActivity;
import com.education.corsalite.activities.StartMockTestActivity;
import com.education.corsalite.activities.TestStartActivity;
import com.education.corsalite.enums.Tests;
import com.education.corsalite.models.OfflineMockTestModel;
import com.education.corsalite.models.ScheduledTestList;
import com.education.corsalite.models.responsemodels.Chapters;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Created by madhuri on 2/28/16.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<OfflineMockTestModel>> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<OfflineMockTestModel>> listChildData) {
        this.context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        if (_listDataHeader.get(groupPosition).equalsIgnoreCase("Mock Test")) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon).mockTest.examName;
        } else if(_listDataHeader.get(groupPosition).equalsIgnoreCase("Schedule Test")){
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon).scheduledTest.examName;
        }else {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon).chapter.chapterName;
        }
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.offline_test_child_item, null);
        }
        TextView txtListChild = (TextView) convertView.findViewById(R.id.mock_test_txt);
        ImageView ivDownload = (ImageView) convertView.findViewById(R.id.download_test);
        ivDownload.setVisibility(View.GONE);
        txtListChild.setText(childText);
        txtListChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(_listDataHeader.get(groupPosition).equals("Mock Test")) {
                    startMockTest(_listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosition));
                } else if(_listDataHeader.get(groupPosition).equals("Scheduled Test")) {
                    startScheduleTest(_listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosition).scheduledTest);
                }else if(_listDataHeader.get(groupPosition).equals("Take Test")){
                    startTakeTest(_listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosition).chapter,
                            _listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosition).subjectId);
                }
            }
        });
        return convertView;
    }

    private void startMockTest(OfflineMockTestModel model) {

        Intent intent = new Intent(context, StartMockTestActivity.class);
        intent.putExtra("Test_Instructions", new Gson().toJson(model.testPaperIndecies));
        intent.putExtra("test_question_paper_id", model.testQuestionPaperId);
        intent.putExtra(Constants.TEST_TITLE, "Mock Test");
        intent.putExtra(Constants.SELECTED_COURSE, AbstractBaseActivity.selectedCourse.courseId.toString());
        intent.putExtra(Constants.IS_OFFLINE, true);
        intent.putExtra("mock_test_data_json", new Gson().toJson(model.mockTest));
        context.startActivity(intent);

    }

    private void startScheduleTest(ScheduledTestList.ScheduledTestsArray exam) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long startTimeInMillis = df.parse(exam.startTime).getTime();
            if (startTimeInMillis < System.currentTimeMillis() + 1000*60) {
                Intent intent = new Intent(context, ExamEngineActivity.class);
                intent.putExtra(Constants.TEST_TITLE, "Scheduled Test");
                intent.putExtra("test_question_paper_id", exam.testQuestionPaperId);
                context.startActivity(intent);
                return;
            }
        } catch (ParseException e) {
            L.error(e.getMessage(), e);
        }
        Toast.makeText(context, "Please access the test during scheduled time", Toast.LENGTH_SHORT).show();
    }

    private void startTakeTest(Chapters chapter,String subjectId){

        Intent exerciseIntent = new Intent(context, TestStartActivity.class);
        exerciseIntent.putExtra(TestStartActivity.KEY_TEST_TYPE, Tests.CHAPTER.getType());
        exerciseIntent.putExtra(Constants.TEST_TITLE, chapter.chapterName);
        exerciseIntent.putExtra(Constants.SELECTED_COURSE, AbstractBaseActivity.selectedCourse.courseId.toString());
        exerciseIntent.putExtra(Constants.SELECTED_CHAPTERID, chapter.idCourseSubjectchapter);
        exerciseIntent.putExtra(Constants.SELECTED_CHAPTER_NAME, chapter.chapterName);
        exerciseIntent.putExtra(Constants.LEVEL_CROSSED, chapter.passedComplexity);
        exerciseIntent.putExtra(Constants.SELECTED_SUBJECTID,subjectId);
        exerciseIntent.putExtra("chapter",new Gson().toJson(chapter));
        exerciseIntent.putExtra(Constants.IS_OFFLINE,true);
        context.startActivity(exerciseIntent);
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        if(_listDataChild != null && !_listDataChild.isEmpty()) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        if(_listDataHeader != null && !_listDataHeader.isEmpty()) {
            return this._listDataHeader.get(groupPosition);
        } else {
            return 0;
        }
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.offline_test_child_item, null);
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.mock_test_txt);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        ImageView ivDownload = (ImageView) convertView.findViewById(R.id.download_test);
        ivDownload.setImageResource(isExpanded ? R.drawable.ico_offline_arrow_down_white : R.drawable.ico_offline_arrow_white);
        ivDownload.setBackgroundColor(context.getResources().getColor(R.color.white));

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

