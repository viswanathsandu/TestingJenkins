package com.corsalite.tabletapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.corsalite.tabletapp.R;
import com.corsalite.tabletapp.models.responsemodels.Course;

import java.util.List;

/**
 * Created by Madhuri on 29-11-2015.
 */
public class SpinnerAdapter extends ArrayAdapter<Course> {

    private Context context;
    private List<Course> data;
    private int selectedPosition;

    public SpinnerAdapter(Context context, int resource,List<Course> data) {
        super(context, resource,data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View itemView =  ((Activity)context).getLayoutInflater().inflate(R.layout.spinner_drop_down,null);
        TextView textView = (TextView)itemView.findViewById(R.id.text);
        ImageView imageView = (ImageView)itemView.findViewById(R.id.selected_item);
        Course course = data.get(position);
        textView.setText(course.name);
        imageView.setVisibility(position == selectedPosition ? View.VISIBLE : View.INVISIBLE);
        return itemView;
    }

   public void setSelectedPosition(int position){
       selectedPosition = position;
   }
}
