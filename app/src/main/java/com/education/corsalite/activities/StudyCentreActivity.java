package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.education.corsalite.R;

/**
 * Created by ayush on 25/09/15.
 */
public class StudyCentreActivity extends AbstractBaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_user_profile, null);
        frameLayout.addView(myView);
    }
}
