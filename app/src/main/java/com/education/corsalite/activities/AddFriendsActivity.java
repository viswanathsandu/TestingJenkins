package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.education.corsalite.R;
import com.education.corsalite.fragments.AddFriendFragment;

public class AddFriendsActivity extends AbstractBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myView = inflater.inflate(R.layout.activity_add_friends, null);
        frameLayout.addView(myView);
        setToolbarForChallengeTest(false);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, AddFriendFragment.newInstance(), "AddFriend").commit();
    }
}
