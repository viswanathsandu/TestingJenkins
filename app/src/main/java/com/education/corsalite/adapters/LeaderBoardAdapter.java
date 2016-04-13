package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.education.corsalite.R;
import com.education.corsalite.models.socket.response.LeaderBoardStudent;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.L;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LeaderBoardAdapter extends AbstractRecycleViewAdapter {

    LayoutInflater inflater;

    public LeaderBoardAdapter(List<LeaderBoardStudent> challengeUsers, LayoutInflater inflater) {
        this(challengeUsers);
        this.inflater = inflater;
    }

    public LeaderBoardAdapter(List<LeaderBoardStudent> challengeUsers) {
        addAll(challengeUsers);
    }

    @Override
    public LeaderBoardDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LeaderBoardDataHolder(inflater.inflate(R.layout.leader_board_item, parent, false));
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((LeaderBoardDataHolder) holder).bindData((LeaderBoardStudent) getItem(position));
    }

    public class LeaderBoardDataHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.rank_txt) TextView rankTxt;
        @Bind(R.id.display_name_txt) TextView displayNameTxt;
        @Bind(R.id.questions_answered_txt) TextView questionsAnsweredTxt;
        @Bind(R.id.player_img) ImageView playerImg;

        private View parent;

        public LeaderBoardDataHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void bindData(final LeaderBoardStudent user) {
            rankTxt.setVisibility(View.GONE);
            displayNameTxt.setText(user.title);
            questionsAnsweredTxt.setText(user.questionsAnswered);
            if(!TextUtils.isEmpty(user.imageUrl)) {
                String url = "";
                if(user.imageUrl.startsWith("./")) {
                    url = ApiClientService.getBaseUrl() + user.imageUrl.replaceFirst("./", "");
                } else if(user.imageUrl.startsWith("./")) {
                    url = ApiClientService.getBaseUrl() + user.imageUrl.replaceFirst("/", "");
                } else {
                    url = ApiClientService.getBaseUrl() + user.imageUrl;
                }
                    L.info("ImageURL : "+url);
                Glide.with(parent.getContext()).load(url).into(playerImg);
            }
        }
    }
}