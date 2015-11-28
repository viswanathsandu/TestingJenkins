package com.education.corsalite.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.LoginActivity;
import com.education.corsalite.activities.UserProfileActivity;
import com.education.corsalite.activities.WebviewActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.DbManager;
import com.education.corsalite.models.db.CourseList;
import com.education.corsalite.models.requestmodels.Defaultcourserequest;
import com.education.corsalite.models.responsemodels.BasicProfile;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.DefaultCourseResponse;
import com.education.corsalite.models.responsemodels.ExamDetail;
import com.education.corsalite.models.responsemodels.UserProfileResponse;
import com.education.corsalite.models.responsemodels.VirtualCurrencyBalanceResponse;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;
import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit.client.Response;

public class UserProfileDetailsFragment extends BaseFragment implements EditProfilePicDialogFragment.IUpdateProfilePicListener {

    private final String COURSES_ENROLLED_HTML = "<b><font color=#000000>Enrolled Courses:</font></b>&nbsp;";
    @Bind(R.id.iv_userProfilePic) ImageView profilePicImg;
    @Bind(R.id.tv_userName) TextView usernameTxt;
    @Bind(R.id.tv_userFullName) TextView userFullNameTxt;
    @Bind(R.id.tv_emailId) TextView emailTxt;
    @Bind(R.id.tv_institute) TextView instituteTxt;
    @Bind(R.id.tv_enrolled_course) TextView enrolledCoursesTxt;
    @Bind(R.id.tv_virtual_currency_balance) TextView virtualCurrencyBalanceTxt;
    @Bind(R.id.sp_default_course) Spinner coursesSpinner;
    @Bind(R.id.btn_default_course) Button coursesBtn;
    @Bind(R.id.redeem_btn)Button redeemBtn;
    @Bind(R.id.btn_edit_pic)ImageView editProfilePic;

    private UserProfileResponse user;
    private UpdateExamData updateExamData;
    private int defaultcourseIndex;
    private List<Course> mCourses;
    private boolean coursesSpinnerCLicked;

    public static UserProfileDetailsFragment newInstance(String param1, String param2) {
        UserProfileDetailsFragment fragment = new UserProfileDetailsFragment();
        return fragment;
    }

    public UserProfileDetailsFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (updateExamData == null) {
            updateExamData = (UpdateExamData) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile_details, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        setListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUserProfileData();
        fetchVirtualCurrencyBalance();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void loadCourses() {
        mCourses = ((AbstractBaseActivity)getActivity()).getcourses();
    }

    private void setListeners() {
        coursesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coursesSpinner.performClick();
                coursesSpinnerCLicked = true;
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
                showEditProfilePicDialogFragment();
            }
        });
        coursesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                defaultcourseIndex = position;
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
        Bundle bundle = new Bundle();
        bundle.putString("user_profile_response", new Gson().toJson(user));
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(), "Edit Profile");
    }

    private void showEditProfilePicDialogFragment(){
        EditProfilePicDialogFragment dialogFragment = new EditProfilePicDialogFragment();
        dialogFragment.setUpdateProfilePicListener(this);
        Bundle bundle = new Bundle();
        bundle.putString("user_profile_response", new Gson().toJson(user));
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(), "Edit Profile Picture");
    }


    @Override
    public void onUpdateProfilePic(Bitmap image) {
        profilePicImg.setImageBitmap(image);
    }

    private void saveDefaultCourse(Course course) {
        if(course != null) {
            String update = new Gson().toJson(new Defaultcourserequest(
                                LoginUserCache.getInstance().loginResponse.studentId, course.courseId+""));

            ApiManager.getInstance(getActivity()).updateDefaultCourse(update, new ApiCallback<DefaultCourseResponse>(getActivity()) {
                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    showToast("Failed to update Default course...");
                }

                @Override
                public void success(DefaultCourseResponse defaultCourseResponse, Response response) {
                    super.success(defaultCourseResponse, response);
                    showToast("Default course updated successfully...");
                }
            });
        }
    }

    private void redeem() {
        Intent intent = new Intent(getActivity(), WebviewActivity.class);
        intent.putExtra(LoginActivity.TITLE, getString(R.string.redeem));
        intent.putExtra(LoginActivity.URL, Constants.REDEEM_URL);
        startActivity(intent);
    }

    private void fetchUserProfileData() {
        ApiManager.getInstance(getActivity()).getUserProfile(LoginUserCache.getInstance().loginResponse.studentId,
                new ApiCallback<UserProfileResponse>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            showToast(error.message);
                        }
                    }

                    @Override
                    public void success(UserProfileResponse userProfileResponse, Response response) {
                        super.success(userProfileResponse, response);
                        if (userProfileResponse.isSuccessful()) {
                            ApiCacheHolder.getInstance().setUserProfileRespose(userProfileResponse);
                            dbManager.saveReqRes(ApiCacheHolder.getInstance().userProfile);
                            user = userProfileResponse;
                            showProfileData(userProfileResponse.basicProfile);
                            loadCoursesData(userProfileResponse.basicProfile);
                            if (updateExamData != null) {
                                updateExamData.getExamData(userProfileResponse.examDetails);
                            }
                        }
                    }
                });
    }

    private void loadCoursesData(BasicProfile profile) {
        if(profile != null && profile.enrolledCourses != null) {
            String [] coursesArr = profile.enrolledCourses.split(",");
            CourseList courseList = new CourseList(Arrays.asList(coursesArr)) ;
            courseList.defaultCourseIndex = defaultcourseIndex;
            DbManager.getInstance(getActivity()).saveCourseList(courseList);
        }
    }

    private void showCourses(List<Course> courses) {
        ArrayAdapter<Course> dataAdapter = new ArrayAdapter<Course>(getActivity(),
                R.layout.spinner_title_textview, courses);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        coursesSpinner.setAdapter(dataAdapter);
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
        ApiManager.getInstance(getActivity()).getVirtualCurrencyBalance(LoginUserCache.getInstance().loginResponse.studentId,
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
                            UserProfileActivity.BALANCE_CURRENCY = String.valueOf(virtualCurrencyBalanceResponse.balance.intValue());
                            virtualCurrencyBalanceTxt.setText(virtualCurrencyBalanceResponse.balance.intValue() + "");
                        }
                    }
                });
    }

    public void onEvent(Course course) {
        loadCourses();
        setEnrolledCourses();
        showCourses(mCourses);
    }

    private void setEnrolledCourses() {
        String courses = "";
        for(Course course : mCourses) {
            courses += TextUtils.isEmpty(courses) ? course.name : ", "+course.name;
        }
        enrolledCoursesTxt.setText(Html.fromHtml(COURSES_ENROLLED_HTML + courses));
    }

    public interface UpdateExamData {
        void getExamData(List<ExamDetail> examDetailList);
    }
}
