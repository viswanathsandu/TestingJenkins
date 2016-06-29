package com.education.corsalite.adapters;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.enums.CurriculumEntityType;
import com.education.corsalite.enums.CurriculumTabType;
import com.education.corsalite.models.responsemodels.CurriculumEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sridharnalam on 1/8/16.
 */
public class CurriculumAdapter extends RecyclerView.Adapter<CurriculumAdapter.CurriculumHolder> {

    private List<CurriculumEntity> mCurriculumEntities;
    private CurriculumTabType mTabType;
    private Activity mActivity;

    public CurriculumAdapter(Activity activity, CurriculumTabType tabType) {
        mCurriculumEntities = new ArrayList<>();
        mTabType = tabType;
        this.mActivity = activity;
    }

    @Override
    public CurriculumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_item_curriculum, parent, false);
        return new CurriculumHolder(view);
    }

    @Override
    public void onBindViewHolder(CurriculumHolder holder, int position) {
        final CurriculumEntity entity = mCurriculumEntities.get(position);
        holder.typeTxt.setText(entity.recType);
        holder.descTxt.setText(entity.description);
        holder.duedataTxt.setText(entity.dueDate);
        holder.overdueTxt.setText(entity.overDueDays);
        holder.totalPointsTxt.setText(entity.points);
        Drawable typeImgDrawable = null;
        CurriculumEntityType type = CurriculumEntityType.getCurriculumEntityType(entity.recType);
        if (type != null) {
            switch (type) {
                case READING:
                    typeImgDrawable = mActivity.getResources().getDrawable(R.drawable.reading_ico);
                    break;
                case CUSTOM_EXERCISE:
                    typeImgDrawable = mActivity.getResources().getDrawable(R.drawable.custome_exercise);
                    break;
                case SCHEDULED_EXAM:
                    typeImgDrawable = mActivity.getResources().getDrawable(R.drawable.scheduled_test);
                    break;
                case PRACTIVE_TEST:
                    typeImgDrawable = mActivity.getResources().getDrawable(R.drawable.practive_test);
                    break;
                case REVISION_TEST:
                    typeImgDrawable = mActivity.getResources().getDrawable(R.drawable.revision_test);
                    break;
                case SCHEDULED_TEST:
                    typeImgDrawable = mActivity.getResources().getDrawable(R.drawable.scheduled_test);
                    break;
            }
        }
        if (typeImgDrawable != null) {
            holder.typeImg.setImageDrawable(typeImgDrawable);
        }
    }

    @Override
    public int getItemCount() {
        return mCurriculumEntities.size();
    }

    public CurriculumEntity getItem(int position) {
        return mCurriculumEntities.get(position);
    }

    public void setCurrilumList(List<CurriculumEntity> curriculumEntities) {
        if (mCurriculumEntities != null) {
            mCurriculumEntities.clear();
            if (curriculumEntities != null) {
                mCurriculumEntities.addAll(curriculumEntities);
            }
            notifyDataSetChanged();
        }
    }

    public void updateCurrentItem(int position) {
        notifyItemChanged(position);
    }

    public class CurriculumHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.type_img)
        ImageView typeImg;
        @Bind(R.id.type_txt)
        TextView typeTxt;
        @Bind(R.id.desc_txt)
        TextView descTxt;
        @Bind(R.id.duedate_txt)
        TextView duedataTxt;
        @Bind(R.id.overdue_txt)
        TextView overdueTxt;
        @Bind(R.id.total_points_txt)
        TextView totalPointsTxt;

        public CurriculumHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
