package com.education.corsalite.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.LoginActivity;
import com.education.corsalite.activities.UserProfileActivity;
import com.education.corsalite.activities.WebviewActivity;
import com.education.corsalite.adapters.SpinnerAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.requestmodels.Defaultcourserequest;
import com.education.corsalite.models.requestmodels.UserProfileModel;
import com.education.corsalite.models.responsemodels.BasicProfile;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.DefaultCourseResponse;
import com.education.corsalite.models.responsemodels.ExamDetail;
import com.education.corsalite.models.responsemodels.UserProfileResponse;
import com.education.corsalite.models.responsemodels.VirtualCurrencyBalanceResponse;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.SystemUtils;
import com.education.corsalite.utils.WebUrls;
import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

public class UserProfileDetailsFragment extends BaseFragment implements EditProfilePicDialogFragment.IUpdateProfilePicListener,
        EditProfileDialogFragment.IUpdateProfileDetailsListener {

    private final String COURSES_ENROLLED_HTML = "<b><font color=#000000>Enrolled Courses:</font></b>&nbsp;";
    @Bind(R.id.iv_userProfilePic) ImageView profilePicImg;
    @Bind(R.id.tv_userName) TextView usernameTxt;
    @Bind(R.id.tv_userFullName) TextView userFullNameTxt;
    @Bind(R.id.tv_emailId) TextView emailTxt;
    @Bind(R.id.tv_institute) TextView instituteTxt;
    @Bind(R.id.tv_enrolled_course) TextView enrolledCoursesTxt;
    @Bind(R.id.tv_virtual_currency_balance) TextView virtualCurrencyBalanceTxt;
    @Bind(R.id.sp_default_course) Spinner coursesSpinner;
    @Bind(R.id.btn_default_course) TextView coursesBtn;
    @Bind(R.id.redeem_btn)Button redeemBtn;
    @Bind(R.id.btn_edit_pic)ImageView editProfilePic;
    @Bind(R.id.ll_user_details)LinearLayout mainLayout;

    private UserProfileResponse user;
    private UpdateExamData updateExamData;
    private int defaultcourseIndex;
    private List<Course> mCourses;
    private boolean coursesSpinnerCLicked;
    private SpinnerAdapter dataAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (updateExamData == null) {
            updateExamData = (UpdateExamData) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_details, container, false);
        ButterKnife.bind(this, view);
        setListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUserProfileData();
        fetchVirtualCurrencyBalance();
    }

    private void showEnrolledcourses(String enrolledCourses) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Enrolled courses")
                .setMessage(enrolledCourses)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void loadCourses() {
        mCourses = ((AbstractBaseActivity) getActivity()).getCourses();
    }

    private void setListeners() {
        coursesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemUtils.isNetworkConnected(getActivity())) {
                    coursesSpinner.performClick();
                    coursesSpinnerCLicked = true;
                } else {
                    showToast("Default course can not be changed in offline");
                }
            }
        });
        enrolledCoursesTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEnrolledcourses(enrolledCoursesTxt.getText().toString());
            }
        });
        usernameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfileFragment();
            }
        });
        editProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("Under Development");
//                showEditProfilePicDialogFragment();

            }
        });
        coursesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                defaultcourseIndex = position;
                dataAdapter.setSelectedPosition(position);
                if (coursesSpinnerCLicked) {
                    saveDefaultCourse(mCourses.get(position));
                }
                coursesSpinnerCLicked = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                coursesSpinnerCLicked = false;
            }
        });
        redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeem();
            }
        });
    }

    private void showEditProfileFragment() {
        EditProfileDialogFragment dialogFragment = new EditProfileDialogFragment();
        dialogFragment.setUpdateProfileDetailsListener(this);
        Bundle bundle = new Bundle();
        bundle.putString("user_profile_response", new Gson().toJson(user));
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(), "Edit Profile");
    }

    private void showEditProfilePicDialogFragment() {
        EditProfilePicDialogFragment dialogFragment = new EditProfilePicDialogFragment();
        dialogFragment.setUpdateProfilePicListener(this);
        Bundle bundle = new Bundle();
        bundle.putString("user_profile_response", new Gson().toJson(user));
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(), "Edit Profile Picture");
    }

    private void saveDefaultCourse(Course course) {
        if (course != null) {
            String update = new Gson().toJson(new Defaultcourserequest(
                    LoginUserCache.getInstance().getStudentId(), course.courseId + ""));
            showProgress();
            ApiManager.getInstance(getActivity()).updateDefaultCourse(update, new ApiCallback<DefaultCourseResponse>(getActivity()) {
                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    closeProgress();
                    showToast("Failed to update Default course");
                }

                @Override
                public void success(DefaultCourseResponse defaultCourseResponse, Response response) {
                    super.success(defaultCourseResponse, response);
                    closeProgress();
                    showToast("Default course updated successfully");
                }
            });
        }
    }

    private void redeem() {
        Intent intent = new Intent(getActivity(), WebviewActivity.class);
        intent.putExtra(LoginActivity.TITLE, getString(R.string.redeem));
        intent.putExtra(LoginActivity.URL, WebUrls.getRedeemUrl());
        startActivity(intent);
    }

    private void fetchUserProfileData() {
        showProgress();
        ApiManager.getInstance(getActivity()).getUserProfile(LoginUserCache.getInstance().getStudentId(),
                new ApiCallback<UserProfileResponse>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        closeProgress();
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            showToast(error.message);
                        }
                    }

                    @Override
                    public void success(UserProfileResponse userProfileResponse, Response response) {
                        super.success(userProfileResponse, response);
                        closeProgress();
                        mainLayout.setVisibility(View.VISIBLE);
                        if (userProfileResponse.isSuccessful()) {
                            ApiCacheHolder.getInstance().setUserProfileRespose(userProfileResponse);
                            dbManager.saveReqRes(ApiCacheHolder.getInstance().userProfile);
                            user = userProfileResponse;
                            showProfileData(userProfileResponse.basicProfile);
                            if (updateExamData != null) {
                                updateExamData.getExamData(userProfileResponse.examDetails);
                            }
                        }
                    }
                });
    }

    private void showCourses(List<Course> courses) {
        dataAdapter = new SpinnerAdapter(getActivity(),
                R.layout.spinner_title_textview, courses);
        coursesSpinner.setAdapter(dataAdapter);
        dataAdapter.setSelectedPosition(defaultcourseIndex);
        coursesSpinner.setSelection(defaultcourseIndex);
    }

    private void showProfileData(BasicProfile profile) {
        if (profile.photoUrl != null && !profile.photoUrl.isEmpty()) {
            try {
                URL url = new URL(new URL(ApiClientService.getBaseUrl()), profile.photoUrl);
                Glide.with(getActivity()).load(url).into(profilePicImg);
            } catch (MalformedURLException e) {
                L.error(e.getMessage(), e);
            }
        }
        usernameTxt.setText(profile.displayName);
        userFullNameTxt.setText(profile.givenName);
        emailTxt.setText(profile.emailId);
    }

    private void fetchVirtualCurrencyBalance() {
        ApiManager.getInstance(getActivity()).getVirtualCurrencyBalance(LoginUserCache.getInstance().getStudentId(),
                new ApiCallback<VirtualCurrencyBalanceResponse>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            showToast(error.message);
                        }
                    }

                    @Override
                    public void success(VirtualCurrencyBalanceResponse virtualCurrencyBalanceResponse, Response response) {
                        super.success(virtualCurrencyBalanceResponse, response);
                        if (virtualCurrencyBalanceResponse.isSuccessful()) {
                            ApiCacheHolder.getInstance().setVirtualCurrencyBalanceResponse(virtualCurrencyBalanceResponse);
                            dbManager.saveReqRes(ApiCacheHolder.getInstance().virtualCurrencyBalance);
                            UserProfileActivity.BALANCE_CURRENCY = String.valueOf(virtualCurrencyBalanceResponse.balance.intValue());
                            virtualCurrencyBalanceTxt.setText(virtualCurrencyBalanceResponse.balance.intValue() + "");
                        }
                    }
                });
    }

    public void onEventMainThread(Course course) {
        loadCourses();
        setEnrolledCourses();
        showCourses(mCourses);
    }

    private void setEnrolledCourses() {
        String courses = "";
        for (Course course : mCourses) {
            courses += TextUtils.isEmpty(courses) ? course.name : ", " + course.name;
            if (course.isDefault()) {
                defaultcourseIndex = mCourses.indexOf(course);
            }
        }
        enrolledCoursesTxt.setText(Html.fromHtml(COURSES_ENROLLED_HTML + courses));
    }

    @Override
    public void onUpdateProfileDetails(UserProfileModel userProfileModel) {
        fetchUserProfileData();
    }

    @Override
    public void onUpdateProfilePic(Bitmap image) {
        fetchUserProfileData();
    }

    public interface UpdateExamData {
        void getExamData(List<ExamDetail> examDetailList);
    }
}
