package com.education.corsalite.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.requestmodels.UserProfileModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.EditProfileModel;
import com.education.corsalite.models.responsemodels.UserProfileResponse;
import com.education.corsalite.utils.L;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Aastha on 20/09/15.
 */
public class EditProfileDialogFragment extends BaseDialogFragment {

    IUpdateProfileDetailsListener updateProfileDetailsListener;
    private UserProfileResponse user;
    @Bind(R.id.username_txt) EditText usernameTxt;
    @Bind(R.id.firstname_txt) EditText firstNameTxt;
    @Bind(R.id.lastname_txt) EditText lastNameTxt;
    @Bind(R.id.emailid_txt) EditText emailIdTxt;
    @Bind(R.id.btn_submit) Button submitBtn;
    @Bind(R.id.btn_cancel) Button cancelBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_profile_dialog_fragment, container, false);
        ButterKnife.bind(this, v);
        user = new Gson().fromJson(getArguments().getString("user_profile_response"), UserProfileResponse.class);
        loadData();
        setListeners();
        getDialog().setTitle("Edit Profile");
        return v;
    }

    private void setListeners() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });
    }

    public void setUpdateProfileDetailsListener(IUpdateProfileDetailsListener listener){
        this.updateProfileDetailsListener = listener;
    }

    private void updateUserProfile() {
        final UserProfileModel model = getUserData();
        String userProfileJson = new Gson().toJson(model);
        ApiManager.getInstance(getActivity()).updateUserProfile(userProfileJson, new ApiCallback<EditProfileModel>(getActivity()) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                L.error(error.message);
                showToast("Failed to Update User Profile");
                getDialog().dismiss();
            }

            @Override
            public void success(EditProfileModel editProfileResponse, Response response) {
                super.success(editProfileResponse, response);
                if (editProfileResponse.isSuccessful()) {
                    showToast("Updated User Profile Successfully");
                    if(updateProfileDetailsListener != null)
                        updateProfileDetailsListener.onUpdateProfileDetails(model);
                    getDialog().dismiss();
                }
            }
        });
    }

    private UserProfileModel getUserData() {
        UserProfileModel model = new UserProfileModel();
        model.updateTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        model.userId = LoginUserCache.getInstance().getUserId();
        model.studentId = LoginUserCache.getInstance().getStudentId();
        if(!TextUtils.isEmpty(usernameTxt.getText().toString())) {
            model.displayName = usernameTxt.getText().toString();
        }
        if(!TextUtils.isEmpty(firstNameTxt.getText().toString())) {
            model.surName = firstNameTxt.getText().toString();
        }
        if(!TextUtils.isEmpty(lastNameTxt.getText().toString())) {
            model.givenName = lastNameTxt.getText().toString();
        }
        if(!TextUtils.isEmpty(emailIdTxt.getText().toString())) {
            model.emailId = emailIdTxt.getText().toString();
        }
        // TODO : critical. Need to remvoe this after having discusison with sunil
        model.gender = "Male";
        return model;
    }

    private void loadData() {
        usernameTxt.setText(user.basicProfile.displayName);
        firstNameTxt.setText(user.basicProfile.surName);
        lastNameTxt.setText(user.basicProfile.givenName);
        emailIdTxt.setText(user.basicProfile.emailId);
    }

    public interface IUpdateProfileDetailsListener {
        void onUpdateProfileDetails(UserProfileModel userProfileModel);
    }

}