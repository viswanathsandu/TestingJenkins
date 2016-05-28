package com.corsalite.tabletapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.corsalite.tabletapp.R;
import com.corsalite.tabletapp.models.responsemodels.ChallengeUser;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Girish on 12/09/15.
 */
public class ChallengeResultsAdapter extends AbstractRecycleViewAdapter {

    LayoutInflater inflater;

    public ChallengeResultsAdapter(List<ChallengeUser> challengeUsers, LayoutInflater inflater) {
        this(challengeUsers);
        this.inflater = inflater;
    }

    public ChallengeResultsAdapter(List<ChallengeUser> challengeUsers) {
        addAll(challengeUsers);
    }

    @Override
    public ChallengeResultDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChallengeResultDataHolder(inflater.inflate(R.layout.row_challenge_results_list, parent, false));
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ChallengeResultDataHolder) holder).bindData((ChallengeUser) getItem(position));
    }

    public class ChallengeResultDataHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.display_name_txt) TextView displayNameTxt;
        @Bind(R.id.score_txt) TextView scoreTxt;
        @Bind(R.id.points_earned_txt) TextView pointsEarnedTxt;
        @Bind(R.id.status_txt) TextView statusTxt;

        View parent;

        public ChallengeResultDataHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void bindData(final ChallengeUser user) {
            displayNameTxt.setText(user.displayName);
            scoreTxt.setText(user.score);
            pointsEarnedTxt.setText(user.virtualCurrencyWon);
            statusTxt.setText(user.getChallengeStatus());
        }
    }
}