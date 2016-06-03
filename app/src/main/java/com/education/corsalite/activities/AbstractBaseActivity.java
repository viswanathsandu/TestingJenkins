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
import android.text.TextUtils;
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
import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.event.ContentReadingEvent;
import com.education.corsalite.event.ExerciseAnsEvent;
import com.education.corsalite.event.ForumPostingEvent;
import com.education.corsalite.event.NetworkStatusChangeEvent;
import com.education.corsalite.event.TakingTestEvent;
import com.education.corsalite.event.UpdateUserEvents;
import com.education.corsalite.fragments.ChallengeTestRequestDialogFragment;
import com.education.corsalite.fragments.ScheduledTestDialog;
import com.education.corsalite.helpers.WebSocketHelper;
import com.education.corsalite.models.ContentModel;
import com.education.corsalite.models.requestmodels.LogoutModel;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.models.responsemodels.FriendsData;
import com.education.corsalite.models.responsemodels.LogoutResponse;
import com.education.corsalite.models.responsemodels.VirtualCurrencyBalanceResponse;
import com.education.corsalite.models.socket.response.ChallengeTestRequestEvent;
import com.education.corsalite.models.socket.response.ChallengeTestStartEvent;
import com.education.corsalite.models.socket.response.ChallengeTestUpdateEvent;
import com.education.corsalite.notifications.ChallengeUtils;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.AppConfig;
import com.education.corsalite.utils.AppPref;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.CookieUtils;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.SystemUtils;
import com.education.corsalite.utils.WebUrls;
import com.google.gson.Gson;
import com.localytics.android.Localytics;

import java.util.ArrayList;
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
    public static Course selectedCourse;
    private static List<ExamModel> sharedExamModels;
    private List<Course> courses;
    public Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    protected FrameLayout frameLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public Dialog dialog;
    protected SugarDbManager dbManager;
    protected AppPref appPref;

    public List<FriendsData.Friend> selectedFriends = new ArrayList<>();

    ChallengeTestRequestDialogFragment challengeTestRequestDialogFragment;

    public static void setSharedExamModels(List<ExamModel> examModels) {
        sharedExamModels = examModels;
    }

    public static List<ExamModel> getSharedExamModels() {
        return sharedExamModels;
    }

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
        dbManager = SugarDbManager.get(getApplicationContext());
        appPref = AppPref.getInstance(this);
    }

    protected void refreshScreen() {
        onCreate(null);
//        Intent currentIntent = getIntent();
//        finish();
//        startActivity(currentIntent);
//        overridePendingTransition(0, 0);
    }

    public void onEventMainThread(NetworkStatusChangeEvent event) {
        showToast(event.isconnected ? "Netowrk connection restored" : "Network connection failure");
        if(!(this instanceof ExamEngineActivity || this instanceof ChallengeActivity)) {
            refreshScreen();
        }
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

    public List<Course> getCourses() {
        return courses;
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
        showVirtualCurrency();
        loadCoursesList();
    }

    protected void setToolbarForAnalytics() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.analytics));
        showVirtualCurrency();
        loadCoursesList();
    }

    protected void setToolbarForChallengeTest(boolean showButtons) {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.GONE);
        if(showButtons) {
            toolbar.findViewById(R.id.challenge_buttons_layout).setVisibility(View.VISIBLE);
        }
        toolbar.setBackgroundColor(getResources().getColor(R.color.red));
        setToolbarTitle(getResources().getString(R.string.challenge_your_friends));
    }

    protected void setToolbarForPostcomments() {
        toolbar.findViewById(R.id.new_post).setVisibility(View.VISIBLE);
        setToolbarTitle("Comments");
    }

    protected void setToolbarForTestStartScreen() {
        toolbar.findViewById(R.id.start_layout).setVisibility(View.VISIBLE);
        setToolbarTitle("Chapter Test");
        showVirtualCurrency();
        loadCoursesList();
    }

    protected void setToolbarForWelcomeScreen() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle("Corsalite");
        loadCoursesList();
    }

    protected void setToolbarForTestIndexScreen() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.GONE);
        setToolbarTitle("Test Instructions");
    }

    protected void setToolbarForContentReading() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.content));
        showVirtualCurrency();
        loadCoursesList();
    }

    protected void setToolbarForExamHistory() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.GONE);
        setToolbarTitle(getResources().getString(R.string.exam_history));
    }

    protected void setToolbarForNotes() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getString(R.string.notes));
        showVirtualCurrency();
        loadCoursesList();
    }

    protected void setToolbarForOfflineContent() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.offline_content));
        showVirtualCurrency();
        loadCoursesList();
    }

    protected void setToolbarForUsageAnalysis() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.usage_analysis));
    }

    protected void setToolbarForVideo(List<ContentModel> videos, int position) {
        findViewById(R.id.toolbar_title).setVisibility(View.GONE);
        toolbar.findViewById(R.id.video_layout).setVisibility(View.VISIBLE);
        hideDrawerIcon();
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        showVideoInToolbar(videos, position);
    }

    protected void setToolbarForOfflineContentReading() {
        toolbar.findViewById(R.id.download).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.offline_content));
    }

    protected void setToolbarForForum() {
        toolbar.findViewById(R.id.new_post).setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.forum));
        loadCoursesList();
    }

    protected void setToolbarForPost() {
        toolbar.findViewById(R.id.new_post1).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.post));
    }

    protected void setToolbarForExercise(String title, boolean showDrawer) {
        setToolbarTitle(title);
        if(!showDrawer) {
            hideDrawerIcon();
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    protected void setToolbarForWebActivity(String title) {
        setToolbarTitle(title);
    }

    protected void hideDrawerIcon() {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
    }

    protected void showDrawerIcon() {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
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
        enableNavigationOptions();
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

    private void enableNavigationOptions() {
        AppConfig config = AppConfig.getInstance();
        if (config == null) {
            return;
        }

        navigationView.findViewById(R.id.navigation_welcome).setVisibility(View.VISIBLE);

        if (config.isMyProfileEnabled()) {
            navigationView.findViewById(R.id.navigation_profile).setVisibility(View.VISIBLE);
        }
        if (config.isStudyCenterEnabled()) {
            navigationView.findViewById(R.id.navigation_study_center).setVisibility(View.VISIBLE);
        }
        if (config.isSmartClassEnabled()) {
            navigationView.findViewById(R.id.navigation_smart_class).setVisibility(View.VISIBLE);
        }
        if (config.isAnalyticsEnabled()) {
            navigationView.findViewById(R.id.navigation_analytics).setVisibility(View.VISIBLE);
        }
        if (config.isOfflineEnabled()) {
            navigationView.findViewById(R.id.navigation_offline).setVisibility(View.VISIBLE);
        }
        if (config.isChallengeTestEnabled()) {
            navigationView.findViewById(R.id.navigation_challenge_your_friends).setVisibility(View.VISIBLE);
        }
        if (config.isForumEnabled()) {
            navigationView.findViewById(R.id.navigation_forum).setVisibility(View.VISIBLE);
        }
        if (config.isLogoutEnabled()) {
            navigationView.findViewById(R.id.navigation_logout).setVisibility(View.VISIBLE);
        }
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

        navigationView.findViewById(R.id.navigation_welcome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWelcomeScreen();
                drawerLayout.closeDrawers();
            }
        });

        navigationView.findViewById(R.id.navigation_smart_class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSmartClass();
                drawerLayout.closeDrawers();
            }
        });

        navigationView.findViewById(R.id.navigation_study_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Localytics.tagEvent("Study Center");
                loadStudyCenterScreen();
            }
        });

        navigationView.findViewById(R.id.navigation_analytics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Localytics.tagEvent("Analytics");
                startActivity(new Intent(AbstractBaseActivity.this, NewAnalyticsActivity.class));
                drawerLayout.closeDrawers();
            }
        });

        navigationView.findViewById(R.id.navigation_offline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Localytics.tagEvent("Offline");
                startActivity(new Intent(AbstractBaseActivity.this, OfflineActivity.class));
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

        navigationView.findViewById(R.id.navigation_forum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemUtils.isNetworkConnected(AbstractBaseActivity.this)) {
                    Localytics.tagEvent(getString(R.string.forum));
                    startActivity(new Intent(AbstractBaseActivity.this, ForumActivity.class));
                } else {
                    showToast("Forum requires network connection");
                }
            }
        });

        navigationView.findViewById(R.id.navigation_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Localytics.tagEvent(getString(R.string.log_out));
                logout();
            }
        });
    }

    protected void loadStudyCenterScreen() {
        Intent intent = new Intent(AbstractBaseActivity.this, StudyCenterActivity.class);
        startActivity(intent);
        finish();
    }

    protected void loadWelcomeScreen() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    protected void showScheduledTestsDialog() {
        if (SystemUtils.isNetworkConnected(this)) {
            ScheduledTestDialog dialog = new ScheduledTestDialog();
            dialog.show(getFragmentManager(), "ScheduledTestsListDialog");
        } else {
            Intent exerciseIntent = new Intent(this, OfflineActivity.class);
            exerciseIntent.putExtra("selection", 1);
            startActivity(exerciseIntent);
        }
    }

    private void loadSmartClass() {
        Intent intent = new Intent(this, WebviewActivity.class);
        intent.putExtra(LoginActivity.TITLE, "Smart Class");
        intent.putExtra(LoginActivity.URL, WebUrls.getSmartClassUrl());
        startActivity(intent);
    }

    protected void setToolbarTitle(String title) {
        TextView textView = (TextView) toolbar.findViewById(R.id.toolbar_title);
        textView.setText(title);
    }

    protected void showVirtualCurrency() {
        try {
            final TextView textView = (TextView) toolbar.findViewById(R.id.tv_virtual_currency);
            final ProgressBar progressBar = (ProgressBar) toolbar.findViewById(R.id.ProgressBar);
            progressBar.setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.currency_layout).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.currency_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AbstractBaseActivity.this, VirtualCurrencyActivity.class);
                    startActivity(intent);
                }
            });
            ApiManager.getInstance(this).getVirtualCurrencyBalance(LoginUserCache.getInstance().loginResponse.studentId, new ApiCallback<VirtualCurrencyBalanceResponse>(this) {
                @Override
                public void success(VirtualCurrencyBalanceResponse virtualCurrencyBalanceResponse, Response response) {
                    super.success(virtualCurrencyBalanceResponse, response);
                    progressBar.setVisibility(View.GONE);
                    if (virtualCurrencyBalanceResponse != null && virtualCurrencyBalanceResponse.balance != null)
                        textView.setText(virtualCurrencyBalanceResponse.balance.intValue() + "");
                }

                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void showToast(String message) {
        showToast(this, message);
    }

    public void showToast(Context context, String message) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
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
        try {
            WebSocketHelper.get(this).disconnectWebSocket();
            LogoutModel logout = new LogoutModel();
            logout.AuthToken = LoginUserCache.getInstance().getLongResponse().authtoken;
            appPref.remove("loginId");
            appPref.remove("passwordHash");
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
                        AbstractBaseActivity.selectedCourse = null;
                        AbstractBaseActivity.selectedVideoPosition= 0;
                        AbstractBaseActivity.sharedExamModels = null;
                        Intent intent = new Intent(AbstractBaseActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
                        finish();
                    }
                }
            });
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            LoginUserCache.getInstance().clearCache();
            deleteSessionCookie();
            Intent intent = new Intent(AbstractBaseActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void loadCoursesList() {

        if (LoginUserCache.getInstance().loginResponse == null || LoginUserCache.getInstance().loginResponse.studentId == null) {
            return;
        }
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
        videoSpinner.setBackgroundColor(Color.WHITE);
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

    protected EventBus getEventbus() {
        return EventBus.getDefault();
    }

    public void onEvent(Integer position) {
        selectedVideoPosition = position;
    }

    // this method will be overridden by the classes that subscribes from event bus
    public void onEvent(Course course) {
        selectedCourse = course;
        getContentIndex(selectedCourse.courseId+"", LoginUserCache.getInstance().loginResponse.studentId);
    }

    private void getContentIndex(String courseId, String studentId) {
        ApiManager.getInstance(this).getContentIndex(courseId, studentId,
                new ApiCallback<List<ContentIndex>>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            L.info(error.message);

                        }
                    }

                    @Override
                    public void success(List<ContentIndex> mContentIndexs, Response response) {
                        super.success(mContentIndexs, response);
                        if (mContentIndexs != null) {
                            ApiCacheHolder.getInstance().setcontentIndexResponse(mContentIndexs);
                            dbManager.saveReqRes(ApiCacheHolder.getInstance().contentIndex);
                        }
                    }
                });
    }

    public static void saveSessionCookie(Response response) {
        String cookie = CookieUtils.getCookieString(response);
        if (response.getStatus() != 401 && cookie != null) {
            L.info("Intercept : save session cookie : " + cookie);
            ApiClientService.setSetCookie(cookie);
        }
    }

    public static void saveSessionCookie(com.squareup.okhttp.Response response) {
        String cookie = CookieUtils.getCookieString(response);
        if (response.code() != 401 && cookie != null) {
            L.info("Intercept : save session cookie : " + cookie);
            ApiClientService.setSetCookie(cookie);
        }
    }

    public void deleteSessionCookie() {
        ApiClientService.setSetCookie(null);
    }

    public void showProgress() {
        ProgressBar pbar = new ProgressBar(this);
        pbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(pbar);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void closeProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void sendAnalytics(String screenName) {
        GoogleAnalyticsManager.sendOpenScreenEvent(this, this, screenName);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void onEvent(ContentReadingEvent event) {
        L.debug("ContentReadingEvent id", event.id);
        new UpdateUserEvents().postContentReading(this, event);
    }

    public void onEvent(ForumPostingEvent event) {
        L.debug("ForumPostingEvent id", event.id);
        new UpdateUserEvents().postForumPosting(this, event);
    }

    public void onEvent(ExerciseAnsEvent event) {
        if (event != null && event.id != null) {
            L.debug("ExerciseAnsEvent id", event.id + "");
            new UpdateUserEvents().postExerciseAns(this, event);
        }
    }

    public void onEvent(TakingTestEvent event) {
        L.debug("TakingTestEvent id", event.id);
        new UpdateUserEvents().postTakingTest(this, event);
    }

    protected void redeem() {
        Intent intent = new Intent(this, WebviewActivity.class);
        intent.putExtra(LoginActivity.TITLE, getString(R.string.redeem));
        intent.putExtra(LoginActivity.URL, WebUrls.getRedeemUrl());
        startActivity(intent);
    }

    // trigger challenge test request
    public void onEventMainThread(ChallengeTestRequestEvent event) {
        ChallengeUtils.get(this).showChallengeRequestNotification(event);
    }

    public void onEventMainThread(ChallengeTestUpdateEvent event) {
        if(!TextUtils.isEmpty(event.challengerStatus) && event.challengerStatus.equalsIgnoreCase("Canceled")) {
            ChallengeUtils.get(this).clearChallengeNotifications(event);
            if(this instanceof ChallengeActivity) {
                finish();
                showToast("Challenger has cancelled the challenge");
            }
        } else if(!(this instanceof ChallengeActivity)) {
            ChallengeUtils.get(this).showChallengeUpdateNotification(event);
        }
    }

    public void onEventMainThread(ChallengeTestStartEvent event) {
        if(!(this instanceof ExamEngineActivity) && !ChallengeUtils.get(this).challengeStarted) {
            ChallengeUtils.get(this).challengeStarted = true;
            Intent intent = new Intent(this, ExamEngineActivity.class);
            intent.putExtra(Constants.TEST_TITLE, "Challenge Test");
            intent.putExtra("test_question_paper_id", event.testQuestionPaperId);
            startActivity(intent);
        }
        if(this instanceof ChallengeActivity) {
            finish();
        }
    }

    protected void startWebSocket() {
        WebSocketHelper.get(this).connectWebSocket();
    }
}
