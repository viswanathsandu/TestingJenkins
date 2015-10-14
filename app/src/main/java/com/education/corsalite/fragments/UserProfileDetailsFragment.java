package com.education.corsalite.fragments;

import android.app.Activity;
import android.content.Intent;
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
import com.education.corsalite.activities.EditProfileActivity;
import com.education.corsalite.activities.LoginActivity;
import com.education.corsalite.activities.UserProfileActivity;
import com.education.corsalite.activities.WebviewActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.DbManager;
import com.education.corsalite.models.db.CourseList;
import com.education.corsalite.models.responsemodels.BasicProfile;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ExamDetail;
import com.education.corsalite.models.responsemodels.UserProfileResponse;
import com.education.corsalite.models.responsemodels.VirtualCurrencyBalanceResponse;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

public class UserProfileDetailsFragment extends BaseFragment {

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
    @Bind(R.id.btn_edit)Button editProfileBtn;
    @Bind(R.id.redeem_btn)Button redeemBtn;

    private UserProfileResponse user;
    private UpdateExamData updateExamData;
    private int defaultcourseIndex;

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
        setListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCourseDataFromDb();
        fetchUserProfileData();
        fetchVirtualCurrencyBalance();
    }

    private void loadCourseDataFromDb() {
        CourseList list = DbManager.getInstance(getActivity()).getCourseList();
        if(list != null && list.courses != null) {
            this.defaultcourseIndex = list.defaultCourseIndex;
            showCourses(list.courses);
        }
    }

    private void saveDefaultCourseIndex(int index) {
        DbManager.getInstance(getActivity()).saveDefaultCourse(index);
    }

    private void setListeners() {
        coursesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coursesSpinner.performClick();
            }
        });
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user_profile_response", user);
                intent.putExtra("user_profile", bundle);
                startActivity(intent);
            }
        });
        coursesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                defaultcourseIndex = position;
                L.info("Position : " + position);
                saveDefaultCourseIndex(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeem();
            }
        });
    }

    private void redeem() {
        Intent intent = new Intent(getActivity(), WebviewActivity.class);
        intent.putExtra(LoginActivity.TITLE, getString(R.string.redeem));
        intent.putExtra(LoginActivity.URL, Constants.REDEEM_URL);
        startActivity(intent);
    }

    private void fetchUserProfileData() {
        ApiManager.getInstance(getActivity()).getUserProfile(LoginUserCache.getInstance().loginResponse.studentId,
            new ApiCallback<UserProfileResponse>() {
                @Override
                public void failure(CorsaliteError error) {
                    if (error != null && !TextUtils.isEmpty(error.message)) {
                        showToast(error.message);
                    }
                }

                @Override
                public void success(UserProfileResponse userProfileResponse, Response response) {
                    if (userProfileResponse.isSuccessful()) {
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
            showCourses(courseList.courses);
            DbManager.getInstance(getActivity()).saveCourseList(courseList);
        }
    }

    private void showCourses(List<String> courses) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_title_textview, courses);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
        setEnrolledCourses(profile.enrolledCourses);
    }

    private void fetchVirtualCurrencyBalance() {
        ApiManager.getInstance(getActivity()).getVirtualCurrencyBalance(LoginUserCache.getInstance().loginResponse.studentId,
                new ApiCallback<VirtualCurrencyBalanceResponse>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            showToast(error.message);
                        }
                    }

                    @Override
                    public void success(VirtualCurrencyBalanceResponse virtualCurrencyBalanceResponse, Response response) {
                        if (virtualCurrencyBalanceResponse.isSuccessful()) {
                            UserProfileActivity.BALANCE_CURRENCY = String.valueOf(virtualCurrencyBalanceResponse.balance.intValue());
                            virtualCurrencyBalanceTxt.setText(virtualCurrencyBalanceResponse.balance.intValue() + "");
                        }
                }
            });
    }





    private void setEnrolledCourses(String courses) {
        enrolledCoursesTxt.setText(Html.fromHtml(COURSES_ENROLLED_HTML + courses));
    }

    public interface UpdateExamData {
        void getExamData(List<ExamDetail> examDetailList);
    }
}
