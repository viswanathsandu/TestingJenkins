package com.education.corsalite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.models.requestmodels.EditProfileModel;
import com.education.corsalite.models.responsemodels.BasicProfile;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.services.ApiClientService;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Aastha on 15/09/15.
 */
public class EditProfileActivity extends AbstractBaseActivity{

    @Bind(R.id.profile_save) Button save;
    //@Bind()




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        final BasicProfile user =(BasicProfile) getIntent().getBundleExtra("user_profile").getSerializable("basic_profile");
        populateUserProfile(user);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BasicProfile userProfile = saveUserProfile();

                ApiClientService.get().updateUserProfile(new Gson().toJson(userProfile), new ApiCallback<EditProfileModel>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        showToast("Failed to update user Profile");
                    }

                    @Override
                    public void success(EditProfileModel editProfileResponse, Response response) {
                        if (editProfileResponse.isSuccessful()) {
                            showToast("Updated User Profile Successfully");
                            Intent intent = new Intent(EditProfileActivity.this, UserProfileActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    BasicProfile saveUserProfile(){
        BasicProfile userProfileResponse = new BasicProfile();


        return userProfileResponse;

    }

    private void populateUserProfile(BasicProfile user){


    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}