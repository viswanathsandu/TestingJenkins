package com.education.corsalite.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.adapters.SpinnerAdapter;
import com.education.corsalite.analytics.GoogleAnalyticsManager;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.DbAdapter;
import com.education.corsalite.db.DbManager;
import com.education.corsalite.event.OfflineEventClass;
import com.education.corsalite.models.ContentModel;
import com.education.corsalite.models.requestmodels.LogoutModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.LogoutResponse;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.AppPref;
import com.education.corsalite.utils.CookieUtils;
import com.google.gson.Gson;
import com.localytics.android.Localytics;

import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by vissu on 9/11/15.
 */
public abstract class AbstractBaseActivity extends AppCompatActivity {

    public static int selectedVideoPosition;
    public static OfflineEventClass offlineEventClass;
    public static Course selectedCourse;
    private List<Course> courses;
    protected Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    protected FrameLayout frameLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public Dialog dialog;
    protected DbManager dbManager;
    protected AppPref appPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer_layout);
        frameLayout = (FrameLayout) findViewById(R.id.activity_layout_container);
        initNavigationDrawer();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.roboto_medium))
                .setFontAttrId(R.attr.fontPath)
                .build());
        initActivity();
    }

    private void initActivity() {
        DbAdapter.context = this;
        dbManager = DbManager.getInstance(this);
        appPref = AppPref.getInstance(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    protected void setToolbarForVirtualCurrency() {
        toolbar.findViewById(R.id.redeem_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.virtual_currency));
    }

    protected void setToolbarForProfile() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.title_activity_user_profile));
        loadCoursesList();
    }

    protected void setToolbarForStudyCenter() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.study_centre));
        loadCoursesList();
    }

    protected void setToolbarForAnalytics() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.analytics));
        loadCoursesList();
    }

    protected void setToolbarForContentReading() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.content));
        loadCoursesList();
    }

    protected void setToolbarForExamHistory(){
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.exam_history));
    }

    protected void setToolbarForNotes() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.notes));
        loadCoursesList();
    }
    protected void setToolbarForOfflineContent() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.offline_content));
        loadCoursesList();
    }

    protected void setToolbarForUsageAnalysis() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.usage_analysis));
    }


    protected void setToolbarForVideo(List<ContentModel> videos, int position) {
        findViewById(R.id.toolbar_title).setVisibility(View.GONE);
        toolbar.findViewById(R.id.video_layout).setVisibility(View.VISIBLE);
        showVideoInToolbar(videos, position);
    }

    protected void setToolbarForOfflineContentReading() {
        toolbar.findViewById(R.id.download).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.offline_content));
    }

    protected void setToolbarForExercise(String title) {
        setToolbarTitle(title);
    }

    protected void setToolbarForWebActivity(String title) {
        setToolbarTitle(title);
    }

    protected void setDrawerIconInvisible(){

        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
    }

    private void initNavigationDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(!menuItem.isChecked());
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    default:
                        return true;
                }
            }
        });
        setNavigationClickListeners();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void setNavigationClickListeners() {
        navigationView.findViewById(R.id.navigation_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(AbstractBaseActivity.this instanceof UserProfileActivity)) {
                    Localytics.tagEvent("User Profile");
                    Intent intent = new Intent(AbstractBaseActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();
                }
            }
        });

        navigationView.findViewById(R.id.navigation_currency).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Localytics.tagEvent("Virtual Currency");
                Intent intent = new Intent(AbstractBaseActivity.this, VirtualCurrencyActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawers();
            }
        });

        navigationView.findViewById(R.id.navigation_study_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Localytics.tagEvent("Study Center");
                Intent intent = new Intent(AbstractBaseActivity.this, StudyCentreActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        navigationView.findViewById(R.id.navigation_analytics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Localytics.tagEvent("Analytics");
                startActivity(new Intent(AbstractBaseActivity.this, AnalyticsActivity.class));
                drawerLayout.closeDrawers();
            }
        });

        navigationView.findViewById(R.id.navigation_usage_analysis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Localytics.tagEvent("Usage Analysis");
                Intent intent = new Intent(AbstractBaseActivity.this, UsageAnalysisActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawers();
            }
        });

        navigationView.findViewById(R.id.navigation_offline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Localytics.tagEvent("Offline");
                startActivity(new Intent(AbstractBaseActivity.this, OfflineContentActivity.class));
                drawerLayout.closeDrawers();
            }
        });

        navigationView.findViewById(R.id.navigation_challenge_your_friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Localytics.tagEvent(getString(R.string.challenge_your_friends));
                startActivity(new Intent(AbstractBaseActivity.this, ChallengeActivity.class));
            }
        });
    }

    protected void setToolbarTitle(String title) {
        TextView textView = (TextView) toolbar.findViewById(R.id.toolbar_title);
        textView.setText(title);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        LogoutModel logout = new LogoutModel();
        logout.AuthToken = LoginUserCache.getInstance().getLongResponse().authtoken;
        ApiManager.getInstance(this).logout(new Gson().toJson(logout), new ApiCallback<LogoutResponse>(this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                showToast(getResources().getString(R.string.logout_failed));
            }

            @Override
            public void success(LogoutResponse logoutResponse, Response response) {
                if (logoutResponse.isSuccessful()) {
                    showToast(getResources().getString(R.string.logout_successful));
                    LoginUserCache.getInstance().clearCache();
                    deleteSessionCookie();
                    startActivity(new Intent(AbstractBaseActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
    }

    private void loadCoursesList() {
        ApiManager.getInstance(this).getCourses(LoginUserCache.getInstance().loginResponse.studentId, new ApiCallback<List<Course>>(this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
            }

            @Override
            public void success(List<Course> courses, Response response) {
                super.success(courses, response);
                if (courses != null) {
                    AbstractBaseActivity.this.courses = courses;
                    ApiCacheHolder.getInstance().setCoursesResponse(courses);
                    dbManager.saveReqRes(ApiCacheHolder.getInstance().courses);
                    showCoursesInToolbar(courses);
                }
            }
        });
    }

    public void showVideoInToolbar(final List<ContentModel> videos, int selectedPosition) {
        Spinner videoSpinner = (Spinner) toolbar.findViewById(R.id.spinner_video);
        if (videoSpinner == null) return;
        ArrayAdapter<ContentModel> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_title_textview, videos);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        videoSpinner.setAdapter(dataAdapter);

        videoSpinner.setSelection(selectedPosition);
        videoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getEventbus().post(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void showCoursesInToolbar(final List<Course> courses) {
        final Spinner coursesSpinner = (Spinner) toolbar.findViewById(R.id.spinner_courses);
        if (coursesSpinner == null) return;
        final SpinnerAdapter dataAdapter = new SpinnerAdapter(this, R.layout.spinner_title_textview_notes, courses);
        coursesSpinner.setAdapter(dataAdapter);
        if (selectedCourse != null) {
            for (Course course : courses) {
                if (course.courseId == selectedCourse.courseId) {
                    dataAdapter.setSelectedPosition(courses.indexOf(course));
                    coursesSpinner.setSelection(courses.indexOf(course));
                    break;
                }
            }
        } else {
            for (Course course : courses) {
                if (course.isDefault()) {
                    dataAdapter.setSelectedPosition(courses.indexOf(course));
                    coursesSpinner.setSelection(courses.indexOf(course));
                }
            }
        }
        // courseSpinner.setSelection(courseList.defaultCourseIndex);
        coursesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getEventbus().post(courses.get(position));
                dataAdapter.setSelectedPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public List<Course> getcourses() {
        return courses;
    }

    protected EventBus getEventbus() {
        return EventBus.getDefault();
    }

    public void onEvent(Integer position) {
        selectedVideoPosition = position;
    }

    public void onEvent(Course course) {
        selectedCourse = course;
        // this method will be overridden by the classes that subscribes from event bus
    }

    public void onEvent(OfflineEventClass offlineEventClass) {
        offlineEventClass = offlineEventClass;
    }

    public static void saveSessionCookie(Response response) {
        String cookie = CookieUtils.getCookieString(response);
        if (cookie != null) {
            ApiClientService.setSetCookie(cookie);
        }
    }

    public void deleteSessionCookie() {
        ApiClientService.setSetCookie(null);
    }

    public void showProgress(){

        ProgressBar pbar = new ProgressBar(this);
        pbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(pbar);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void closeProgress(){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public void sendAnalytics(String screenName){
        GoogleAnalyticsManager.sendOpenScreenEvent(this, this, screenName);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
