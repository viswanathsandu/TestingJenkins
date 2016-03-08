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

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.ExamEngineActivity;
import com.education.corsalite.models.OfflineMockTestModel;
import com.education.corsalite.utils.Constants;
import com.google.gson.Gson;

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
        } else {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon).scheduledTest.examName;
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
            convertView = infalInflater.inflate(R.layout.mocktest_spinner_item, null);
        }
        TextView txtListChild = (TextView) convertView.findViewById(R.id.mock_test_txt);
        ImageView ivDownload = (ImageView) convertView.findViewById(R.id.download_test);
        ivDownload.setVisibility(View.GONE);
        txtListChild.setText(childText);
        txtListChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent exerciseIntent = new Intent(context, ExamEngineActivity.class);
                exerciseIntent.putExtra(Constants.TEST_TITLE, "Mock Test");
                exerciseIntent.putExtra(Constants.SELECTED_COURSE, AbstractBaseActivity.selectedCourse.courseId.toString());
                exerciseIntent.putExtra(Constants.IS_OFFLINE, true);
                exerciseIntent.putExtra("mock_test_data_json", new Gson().toJson(_listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosition).mockTest));
                context.startActivity(exerciseIntent);
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
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
            convertView = infalInflater.inflate(R.layout.mocktest_spinner_item, null);
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

