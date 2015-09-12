package com.education.corsalite.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.education.corsalite.R;
import com.education.corsalite.activities.UserProfileActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.responsemodels.BasicProfile;
import com.education.corsalite.responsemodels.CorsaliteError;
import com.education.corsalite.responsemodels.ExamDetail;
import com.education.corsalite.responsemodels.UserProfileResponse;
import com.education.corsalite.responsemodels.VirtualCurrencyBalanceResponse;
import com.education.corsalite.services.ApiClientService;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

public class UserProfileDetailsFragment extends BaseFragment {

    @Bind(R.id.iv_userProfilePic) ImageView profilePicImg;
    @Bind(R.id.tv_userName) TextView usernameTxt;
    @Bind(R.id.tv_userFullName) TextView userFullNameTxt;
    @Bind(R.id.tv_emailId) TextView emailTxt;
    @Bind(R.id.tv_institute) TextView instituteTxt;
    @Bind(R.id.tv_enrolled_course) TextView enrolledCoursesTxt;
    @Bind(R.id.tv_virtual_currency_balance) TextView virtualCurrencyBalanceTxt;

    UpdateExamData updateExamData;

    public static UserProfileDetailsFragment newInstance(String param1, String param2) {
        UserProfileDetailsFragment fragment = new UserProfileDetailsFragment();
        return fragment;
    }

    public UserProfileDetailsFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(updateExamData == null) {
            updateExamData = (UpdateExamData)activity;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUserProfileData();
        fetchVirtualCurrencyBalance();
    }

    private void fetchUserProfileData() {
        ApiClientService.get().getUserProfile(LoginUserCache.getInstance().loginResponse.studentId,
                new ApiCallback<UserProfileResponse>() {
                    @Override
                    public void failure(CorsaliteError error) {

                    }

                    @Override
                    public void success(UserProfileResponse userProfileResponse, Response response) {
                        if (userProfileResponse.isSuccessful()) {
                            showToast("User Profile fetched successfully...");
                            showProfileData(userProfileResponse.basicProfile);

                            if(updateExamData != null) {
                                updateExamData.getExamData(userProfileResponse.examDetails);
                            }

                        } else {
                            showToast("Failed to fetch user profile information");
                        }
                    }
                });
    }

    private void showProfileData(BasicProfile profile) {
        if(profile.photoUrl != null && !profile.photoUrl.isEmpty()) {
            Glide.with(getActivity()).load(profile.photoUrl).into(profilePicImg);
        }
        usernameTxt.setText(profile.displayName);
        userFullNameTxt.setText(profile.givenName);
        emailTxt.setText(profile.emailId);
        setEnrolledCourses(profile.enrolledCourses);
    }

    private void fetchVirtualCurrencyBalance() {
        ApiClientService.get().getVirtualCurrencyBalance(LoginUserCache.getInstance().loginResponse.studentId,
                new ApiCallback<VirtualCurrencyBalanceResponse>() {
                    @Override
                    public void failure(CorsaliteError error) {

                    }

                    @Override
                    public void success(VirtualCurrencyBalanceResponse virtualCurrencyBalanceResponse, Response response) {
                        if (virtualCurrencyBalanceResponse.isSuccessful()) {
                            showToast("virtual currency fetched successfully...");
                            UserProfileActivity.BALANCE_CURRENCY = String.valueOf(virtualCurrencyBalanceResponse.balance.intValue());
                            virtualCurrencyBalanceTxt.setText(virtualCurrencyBalanceResponse.balance.intValue()+"");
                        } else {
                            showToast("Failed to fetch virtual currency information");
                        }
                    }
                });
    }

    private void setEnrolledCourses(String courses) {
        enrolledCoursesTxt.setText(Html.fromHtml("<b><font color=#000000>Courses Enrolled:</font></b>&nbsp; "+courses));
    }

    public interface UpdateExamData{
        void getExamData(List<ExamDetail> examDetailList);
    }

}
