package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.education.corsalite.R;
import com.education.corsalite.responsemodels.BaseModel;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by Aastha on 05/09/15.
 */
public class UserProfileDetailAdapter extends AbstractRecycleViewAdapter {


    private static String USERPROFILE_EXAMS = "exams";
    private static String USERPROFILE_MESSAGES = "messages";
    private static String USERPROFILE_VIRTUAL_CURRENCY = "virtualCurrency";
    private String type;
    LayoutInflater inflater;

    public UserProfileDetailAdapter(ArrayList<BaseModel> data, String type, LayoutInflater inflater) {
        this.type = type;
        this.inflater = inflater;
    }

    @Override
    public UserProfileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if (type.equalsIgnoreCase(USERPROFILE_EXAMS)) {
            v = inflater.inflate(R.layout.row_exams_list, parent, false);
        } else if (type.equalsIgnoreCase(USERPROFILE_MESSAGES)) {
            v = inflater.inflate(R.layout.row_exams_list, parent, false);
        } else if (type.equalsIgnoreCase(USERPROFILE_VIRTUAL_CURRENCY)) {
            v = inflater.inflate(R.layout.row_virtual_currency_list, parent, false);
        }
        return new UserProfileHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((UserProfileHolder) holder).bindData(position, (BaseModel)getItem(position));
    }


    @Override
    public int getItemCount() {
        return 0;
    }

    public class UserProfileHolder extends RecyclerView.ViewHolder {

        public UserProfileHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindData(final int position, final BaseModel userProfileHolder) {

        }

    }
}
