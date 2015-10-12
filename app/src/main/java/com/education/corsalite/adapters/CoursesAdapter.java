package com.education.corsalite.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.SubjectModel;
import com.education.corsalite.models.responsemodels.Course;

import java.util.List;

/**
 * Created by Girish on 03/10/15.
 */
public class CoursesAdapter extends BaseAdapter {

    private List<Course> courses;
    private Context mContext;
    private LayoutInflater inflater;

    public CoursesAdapter(Context context, List<Course> courses) {
        this.courses= courses;
        this.mContext = context;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return (courses != null) ? courses.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return courses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.spinner_title_textview, parent, false);
        TextView tv = (TextView)convertView.findViewById(R.id.text1);
        tv.setText(courses.get(position).name);
        return convertView;
    }
}
