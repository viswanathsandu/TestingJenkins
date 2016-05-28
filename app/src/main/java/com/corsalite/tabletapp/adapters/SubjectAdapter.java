package com.corsalite.tabletapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.corsalite.tabletapp.R;
import com.corsalite.tabletapp.models.SubjectModel;

import java.util.List;

/**
 * Created by Girish on 03/10/15.
 */
public class SubjectAdapter extends BaseAdapter {

    List<SubjectModel> subjectModelList;
    Context mContext;
    LayoutInflater inflater;
    private int selectedPosition;

    public SubjectAdapter(List<SubjectModel> subjectModelList, Context mContext) {
        this.subjectModelList = subjectModelList;
        this.mContext = mContext;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(subjectModelList != null) {
            return subjectModelList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return subjectModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.row_spinner_text, parent, false);
        TextView tv = (TextView)convertView.findViewById(R.id.tv_spn);
        tv.setText(subjectModelList.get(position).subjectName);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View itemView =  inflater.inflate(R.layout.spinner_drop_down, null);
        TextView textView = (TextView)itemView.findViewById(R.id.text);
        ImageView imageView = (ImageView)itemView.findViewById(R.id.selected_item);
        textView.setText(subjectModelList.get(position).subjectName);
        imageView.setVisibility(position == selectedPosition ? View.VISIBLE : View.INVISIBLE);
        return itemView;
    }

    public void setSelectedPosition(int position){
        selectedPosition = position;
        notifyDataSetChanged();
    }
}
