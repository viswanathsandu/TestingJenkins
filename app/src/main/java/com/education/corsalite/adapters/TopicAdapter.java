package com.education.corsalite.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.TopicModel;

import java.util.List;

/**
 * Created by Girish on 03/10/15.
 */
public class TopicAdapter extends BaseAdapter {

    List<TopicModel> topicModelList;
    Context mContext;
    LayoutInflater inflater;
    private int selectedPosition;

    public TopicAdapter(List<TopicModel> topicModelList, Context mContext) {
        this.topicModelList = topicModelList;
        this.mContext = mContext;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(topicModelList != null) {
            return topicModelList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.row_spinner_text, parent, false);
        TextView tv = (TextView)convertView.findViewById(R.id.tv_spn);
        tv.setText(topicModelList.get(position).topicName);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View itemView =  inflater.inflate(R.layout.spinner_drop_down, null);
        TextView textView = (TextView)itemView.findViewById(R.id.text);
        ImageView imageView = (ImageView)itemView.findViewById(R.id.selected_item);
        textView.setText(topicModelList.get(position).topicName);
        imageView.setVisibility(position == selectedPosition ? View.VISIBLE : View.INVISIBLE);
        return itemView;
    }

    public void setSelectedPosition(int position){
        selectedPosition = position;
        notifyDataSetChanged();
    }
}
