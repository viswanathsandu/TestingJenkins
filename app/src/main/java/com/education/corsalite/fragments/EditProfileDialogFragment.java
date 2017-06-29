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
import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.requestmodels.UserProfileModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.EditProfileModel;
import com.education.corsalite.models.responsemodels.UserProfileResponse;
import com.education.corsalite.security.Encrypter;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.TimeUtils;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Aastha on 20/09/15.
 */
public class EditProfileDialogFragment extends BaseDialogFragment {

    IUpdateProfileDetailsListener updateProfileDetailsListener;
    private boolean enableEditEmail;
    private UserProfileResponse user;
    @Bind(R.id.password_txt) EditText passwordTxt;
    @Bind(R.id.confirm_password_txt) EditText confirmPasswordTxt;
    @Bind(R.id.username_txt) EditText usernameTxt;
    @Bind(R.id.firstname_txt) EditText firstNameTxt;
    @Bind(R.id.lastname_txt) EditText lastNameTxt;
    @Bind(R.id.emailid_txt) EditText emailIdTxt;
    @Bind(R.id.btn_submit) Button submitBtn;
    @Bind(R.id.btn_cancel) Button cancelBtn;
    private String password;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_profile_dialog_fragment, container, false);
        ButterKnife.bind(this, v);
        try {
            user = Gson.get().fromJson(getArguments().getString("user_profile_response"), UserProfileResponse.class);
            enableEditEmail = getArguments().getBoolean("enable_edit_email");
            loadData();
            setListeners();
            getDialog().setTitle("Edit Profile");
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        } catch(OutOfMemoryError e) {
            L.error(e.getMessage(), e);
        }
         return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        emailIdTxt.setEnabled(enableEditEmail);
    }

    private void setListeners() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(isValidData()) {
                updateUserProfile();
            }
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

    private boolean isValidData() {
        try {
            String passwordString = passwordTxt.getText().toString();
            String confirmPasswordString = confirmPasswordTxt.getText().toString();
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailIdTxt.getText().toString()).matches()) {
                showToast("Please enter valid email id");
                return false;
            } else if((!passwordString.isEmpty() || !confirmPasswordString.isEmpty())) {
                if(passwordString.length() < 8) {
                    showToast("Password should have at least 8 characters");
                    return false;
                }
                if((!passwordString.equalsIgnoreCase(confirmPasswordString))) {
                    showToast("Password and Confirm Password do not match. Try again.");
                    return false;
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return true;
    }

    private void updateUserProfile() {
        final UserProfileModel model = getUserData();
        String userProfileJson = Gson.get().toJson(model);
        if(!TextUtils.isEmpty(model.password)) {
            password = model.password;
        }
        showProgress();
        ApiManager.getInstance(getActivity()).updateUserProfile(userProfileJson, new ApiCallback<EditProfileModel>(getActivity()) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                L.error(error.message);
                if(getActivity() != null) {
                    closeProgress();
                    showToast("Failed to Update User Profile");
                    getDialog().dismiss();
                }
            }

            @Override
            public void success(EditProfileModel editProfileResponse, Response response) {
                super.success(editProfileResponse, response);
                try {
                    if (getActivity() != null && editProfileResponse.isSuccessful()) {
                        closeProgress();
                        showToast("Updated User Profile Successfully");
                        if (updateProfileDetailsListener != null)
                            updateProfileDetailsListener.onUpdateProfileDetails(model);
                            if (!TextUtils.isEmpty(password)
                                    && ApiCacheHolder.getInstance().login != null
                                    && ApiCacheHolder.getInstance().login.request != null) {
                                SugarDbManager.get(getActivity()).delete(ApiCacheHolder.getInstance().login);
                                appPref.save("passwordHash", password);
                                ApiCacheHolder.getInstance().login.request.passwordHash = password;
                                SugarDbManager.get(getActivity()).saveReqRes(ApiCacheHolder.getInstance().login);
                            }
                        getDialog().dismiss();
                    }
                } catch (Exception e) {
                    L.error(e.getMessage(), e);
                }
            }
        });
    }

    private UserProfileModel getUserData() {
        UserProfileModel model = new UserProfileModel();
        model.updateTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(TimeUtils.getCurrentDate());
        model.userId = appPref.getUserId();
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
        if(!TextUtils.isEmpty(usernameTxt.getText().toString())) {
            model.displayName = usernameTxt.getText().toString();
        }
        if(!passwordTxt.getText().toString().isEmpty()) {
            model.password = Encrypter.md5(passwordTxt.getText().toString());
        }
        // TODO : critical. Need to remvoe this after having discusison with sunil
        model.gender = "Male";
        return model;
    }

    private void loadData() {
        if(user != null && user.basicProfile != null) {
            usernameTxt.setText(user.basicProfile.displayName+"");
            firstNameTxt.setText(user.basicProfile.surName+"");
            lastNameTxt.setText(user.basicProfile.givenName+"");
            emailIdTxt.setText(user.basicProfile.emailId+"");
        }
    }

    public interface IUpdateProfileDetailsListener {
        void onUpdateProfileDetails(UserProfileModel userProfileModel);
    }

}