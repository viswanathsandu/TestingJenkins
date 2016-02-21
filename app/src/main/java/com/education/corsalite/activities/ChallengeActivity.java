package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.fragments.FriendsListFragment;
import com.education.corsalite.fragments.TestSetupFragment;
import com.education.corsalite.models.responsemodels.FriendsData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChallengeActivity extends AbstractBaseActivity {
    private FriendsListCallback mFriendsListCallback;
    private TestSetupCallback mTestSetupCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_challenge, null);
        frameLayout.addView(myView);
        initListeners();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, FriendsListFragment.newInstance(mFriendsListCallback)).commit();
    }

    private void initListeners() {
        mFriendsListCallback = new FriendsListCallback() {
            @Override
            public void onNextClick(ArrayList<FriendsData.Friends> selectedFriends) {
                Toast.makeText(ChallengeActivity.this, selectedFriends.size() + "", Toast.LENGTH_LONG).show();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, TestSetupFragment.newInstance(mTestSetupCallback, selectedFriends)).commit();
            }
        };
    }

    public interface FriendsListCallback extends Serializable {
        public void onNextClick(ArrayList<FriendsData.Friends> selectedFriends);
    }

    public interface TestSetupCallback extends Serializable {

    }
}
