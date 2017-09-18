package com.education.corsalite.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.education.corsalite.BuildConfig;
import com.education.corsalite.R;
import com.education.corsalite.adapters.SpinnerAdapter;
import com.education.corsalite.analytics.FireBaseHelper;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.event.ConnectExceptionEvent;
import com.education.corsalite.event.ContentReadingEvent;
import com.education.corsalite.event.ExerciseAnsEvent;
import com.education.corsalite.event.ForumPostingEvent;
import com.education.corsalite.event.InvalidAuthenticationEvent;
import com.education.corsalite.event.NetworkStatusChangeEvent;
import com.education.corsalite.event.ScheduledTestStartEvent;
import com.education.corsalite.event.TakingTestEvent;
import com.education.corsalite.event.TimeChangedEvent;
import com.education.corsalite.event.UpdateUserEvents;
import com.education.corsalite.fragments.MockTestDialog;
import com.education.corsalite.fragments.ScheduledTestDialog;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.helpers.WebSocketHelper;
import com.education.corsalite.models.ContentModel;
import com.education.corsalite.models.db.AppConfig;
import com.education.corsalite.models.db.MockTest;
import com.education.corsalite.models.db.OfflineTestObjectModel;
import com.education.corsalite.models.db.ScheduledTestList;
import com.education.corsalite.models.db.SyncModel;
import com.education.corsalite.models.db.reqres.AppentityconfigReqRes;
import com.education.corsalite.models.requestmodels.LogoutModel;
import com.education.corsalite.models.responsemodels.ClientEntityAppConfig;
import com.education.corsalite.models.responsemodels.CommonResponseModel;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.models.responsemodels.FriendsData;
import com.education.corsalite.models.responsemodels.LoginResponse;
import com.education.corsalite.models.responsemodels.LogoutResponse;
import com.education.corsalite.models.responsemodels.VirtualCurrencyBalanceResponse;
import com.education.corsalite.models.socket.response.ChallengeTestRequestEvent;
import com.education.corsalite.models.socket.response.ChallengeTestStartEvent;
import com.education.corsalite.models.socket.response.ChallengeTestUpdateEvent;
import com.education.corsalite.notifications.ChallengeUtils;
import com.education.corsalite.receivers.NotifyReceiver;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.services.ContentDownloadService;
import com.education.corsalite.utils.AppPref;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.CookieUtils;
import com.education.corsalite.utils.Data;
import com.education.corsalite.utils.DbUtils;
import com.education.corsalite.utils.FileUtils;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.SystemUtils;
import com.education.corsalite.utils.TimeUtils;
import com.education.corsalite.utils.WebUrls;
import com.localytics.android.Localytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.goncalves.pugnotification.notification.PugNotification;
import de.greenrobot.event.EventBus;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by vissu on 9/11/15.
 */
public abstract class AbstractBaseActivity extends AppCompatActivity {

    public static int selectedVideoPosition;
    private static Course selectedCourse;
    private static List<ExamModel> sharedExamModels;
    public static AppConfig appConfig;

    private List<Course> courses;
    public Toolbar toolbar;
    private NavigationView navigationView;
    protected DrawerLayout drawerLayout;
    protected FrameLayout frameLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public Dialog dialog;
    protected SugarDbManager dbManager;
    protected AppPref appPref;
    private boolean isLoginApiRunningInBackground = false;
    protected boolean isAppConfigApiFinished = true;
    protected boolean isClientEntityConfigApiFinished = true;
    private boolean isShown = false;
    protected boolean isDaFuDialogShown = false;

    public boolean isShown() {
        return isShown;
    }

    public List<FriendsData.Friend> selectedFriends = new ArrayList<>();
    public List<FriendsData.Friend> selectedFriends = new ArrayList<>();

    public static void setSharedExamModels(List<ExamModel> examModels) {
        sharedExamModels = examModels;
    }

    public static List<ExamModel> getSharedExamModels() {
        return sharedExamModels;
    }

    public static Course getSelectedCourse() {
        return selectedCourse;
    }

    public static String getSelectedCourseId() {
        if (selectedCourse != null && selectedCourse.courseId != null) {
            return selectedCourse.courseId.toString();
        }
        return "";
    }

    public static String getSelectedCourseName() {
        if (selectedCourse != null && selectedCourse.name != null) {
            return selectedCourse.name;
        }
        return "";
    }

    public static AppConfig getAppConfig(Context context) {
        if (appConfig == null) {
            String jsonResponse = FileUtils.get(context).loadJSONFromAsset(context.getAssets(), "config.json");
            appConfig = Gson.get().fromJson(jsonResponse, com.education.corsalite.models.db.AppConfig.class);
        }
        return appConfig;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer_layout);
        FireBaseHelper.initFireBase(this);
        frameLayout = (FrameLayout) findViewById(R.id.activity_layout_container);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.roboto_medium))
                .setFontAttrId(R.attr.fontPath)
                .build());
        initActivity();
        initNavigationDrawer();
        logScreen(this.getClass().getSimpleName());
        if (selectedCourse != null) {
            enableNavigationOpitons(selectedCourse);
        }
    }

    public void logScreen(String screenName) {
        FireBaseHelper.logScreen(screenName);
        Localytics.tagScreen(screenName);
        Localytics.tagEvent(screenName);
    }

    public void logEvent(String tag) {
        FireBaseHelper.logEvent(tag);
        Localytics.tagEvent(this.getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        isShown = true;
        ApiClientService.setupRestClient();
        checkForceUpgrade();
        checkDataSync();
    }

    @Override
    protected void onPause() {
        try {
            if (this instanceof LoginActivity
                    || this instanceof StudyCenterActivity
                    || this instanceof ExamEngineActivity
                    || this instanceof OfflineActivity
                    || this instanceof SaveForOfflineActivity
                    || this instanceof ContentReadingActivity
                    || this instanceof WelcomeActivity) {
                DbUtils.get(this).backupDatabase();
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        super.onPause();
        isShown = false;
        L.writeLogToFile();
    }

    private void initActivity() {
        dbManager = SugarDbManager.get(getApplicationContext());
        appPref = AppPref.get(this);
    }

    protected void refreshScreen() {
        AppPref.get(getApplicationContext()).remove("data_sync_later");
        if (SystemUtils.isNetworkConnected(this)) {
            relogin();
        } else {
            recreate();
        }
    }

    public void setCrashlyticsUserData() {
        try {
            String emailId = appPref.getValue("loginId");
            Crashlytics.setUserEmail(emailId);
            FireBaseHelper.setUsername(emailId);
            Localytics.setCustomerEmail(emailId);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void resetCrashlyticsUserData() {
        try {
            Crashlytics.setUserEmail("");
            FireBaseHelper.setUsername("");
            Localytics.setCustomerEmail("");
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void checkDataSync() {
        try {
            if (SystemUtils.isNetworkConnected(this)) {
                if (!(this instanceof DataSyncActivity || this instanceof SplashActivity)
                        && LoginUserCache.getInstance().getLoginResponse() != null) {
                    long eventsCount = dbManager.getcount(SyncModel.class);
                    if (eventsCount > 0) {
                        showDataSyncAlert(eventsCount);
                    }
                }
            } else if (dataSyncAlertDialog != null && dataSyncAlertDialog.isShowing()) {
                dataSyncAlertDialog.dismiss();
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void syncDataWithServer() {
        // Start download service if its not started
//        stopService(new Intent(getApplicationContext(), DataSyncService.class));
//        startService(new Intent(getApplicationContext(), DataSyncService.class));
    }

    public void onEventMainThread(NetworkStatusChangeEvent event) {
        try {
            showToast(event.isconnected ? "Network connection restored" : "Network connection failure");
            L.info("WIFICONNECT : " + event.isconnected);
            if (!(this instanceof ExamEngineActivity || this instanceof ChallengeActivity)) {
                refreshScreen();
            }
            if (event.isconnected) {
                stopService(new Intent(getApplicationContext(), ContentDownloadService.class));
                startService(new Intent(getApplicationContext(), ContentDownloadService.class));
            } else {
                stopService(new Intent(getApplicationContext(), ContentDownloadService.class));
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
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

    public void loadAppConfig() {
        isAppConfigApiFinished = false;
        ApiManager.getInstance(this).getAppConfig(new ApiCallback<AppConfig>(this) {
            @Override
            public void success(AppConfig appConfig, Response response) {
                super.success(appConfig, response);
                isAppConfigApiFinished = true;
                AbstractBaseActivity.appConfig = appConfig;
                ApiCacheHolder.getInstance().setAppConfigResponse(appConfig);
                dbManager.saveReqRes(ApiCacheHolder.getInstance().appConfigReqRes);
                requestClientEntityConfig();
            }

            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                isAppConfigApiFinished = true;
                onLoginFlowCompleted();
            }
        });
    }

    protected void requestClientEntityConfig() {
        LoginResponse loginResponse = LoginUserCache.getInstance().getLoginResponse();
        if (loginResponse != null && !TextUtils.isEmpty(loginResponse.idUser) && !TextUtils.isEmpty(loginResponse.entitiyId)) {
            isClientEntityConfigApiFinished = false;
            ApiManager.getInstance(this).getClientEntityAppConfig(loginResponse.idUser, loginResponse.entitiyId,
                    new ApiCallback<ClientEntityAppConfig>(this) {
                        @Override
                        public void success(ClientEntityAppConfig clientEntityAppConfig, Response response) {
                            super.success(clientEntityAppConfig, response);
                            isClientEntityConfigApiFinished = true;
                            ApiCacheHolder.getInstance().setAppEntityConfigResponse(clientEntityAppConfig);
                            dbManager.saveReqRes(ApiCacheHolder.getInstance().appentityconfigReqRes);
                            if (clientEntityAppConfig != null && !isDeviceAffinityOrUpgradeAlertShown(clientEntityAppConfig)) {
                                onLoginFlowCompleted();
                            }
                        }

                        @Override
                        public void failure(CorsaliteError error) {
                            super.failure(error);
                            isClientEntityConfigApiFinished = true;
                            onLoginFlowCompleted();
                        }
                    });
        }
    }

    /**
     * please override this method to do something after auto login
     */
    protected void onLoginFlowCompleted() {

    }

    private boolean isDeviceAffinityOrUpgradeAlertShown(ClientEntityAppConfig config) {
        try {
            if (config != null) {
                if (config.isDeviceAffinityEnabled()) {
                    if (TextUtils.isEmpty(config.deviceId)) {
                        LoginResponse loginResponse = LoginUserCache.getInstance().getLoginResponse();
                        postClientEntityConfig(loginResponse.idUser);
                    } else if (!config.deviceId.toLowerCase().contains(SystemUtils.getUniqueID(this))) {
                        showDeviceAffinityAlert();
                        return true;
                    }
                }
                if (isEntityAppUpgradeAlertShown(config)) {
                    return true;
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return false;
    }

    private boolean isEntityAppUpgradeAlertShown(ClientEntityAppConfig config) {
        try {
            if (config != null && config.isUpdateAvailable() && !TextUtils.isEmpty(config.getAppVersionNumber())) {
                int latestAppVersion = Integer.parseInt(config.getAppVersionNumber());
                if (latestAppVersion > BuildConfig.VERSION_CODE) {
                    showUpdateAlert(config.isForceUpgradeEnabled(), config.isAppFromPlayStore(), config.getUpdateUrl());
                    return true;
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return false;
    }

    private void showDeviceAffinityAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Login Failure")
                .setMessage("Please Login from your assigned device")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logout(true);
                        AbstractBaseActivity.this.isDaFuDialogShown = false;
                        dialog.dismiss();
                    }
                })
                .show();
        isDaFuDialogShown = true;
    }

    private void postClientEntityConfig(String userId) {
        final String uniqueId = SystemUtils.getUniqueID(this);
        ApiManager.getInstance(this).postClientEntityAppConfig(userId, uniqueId,
                new ApiCallback<CommonResponseModel>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                    }

                    @Override
                    public void success(CommonResponseModel commonResponseModel, Response response) {
                        super.success(commonResponseModel, response);
                        AppentityconfigReqRes reqRes = ApiCacheHolder.getInstance().appentityconfigReqRes;
                        if (reqRes != null && reqRes.response != null) {
                            reqRes.response.deviceId = uniqueId;
                        }
                    }
                });
    }

    private void relogin() {
        if (isLoginApiRunningInBackground || this instanceof LoginActivity || this instanceof SplashActivity) {
            return;
        }
        isLoginApiRunningInBackground = true;
        showProgress();
        final String username = appPref.getValue("loginId");
        final String passwordHash = appPref.getValue("passwordHash");
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(passwordHash)) {
            ApiManager.getInstance(this).login(username, passwordHash, new ApiCallback<LoginResponse>(this) {
                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    closeProgress();
                    isLoginApiRunningInBackground = false;
                    if (error != null && !TextUtils.isEmpty(error.message)) {
                        showToast(error.message);
                        EventBus.getDefault().post(new InvalidAuthenticationEvent());
                    }
                }

                @Override
                public void success(LoginResponse loginResponse, Response response) {
                    super.success(loginResponse, response);
                    closeProgress();
                    if (loginResponse.isSuccessful(AbstractBaseActivity.this)) {
                        setCrashlyticsUserData();
                        isLoginApiRunningInBackground = false;
                        ApiCacheHolder.getInstance().setLoginResponse(loginResponse);
                        LoginUserCache.getInstance().setLoginResponse(loginResponse);
                        dbManager.saveReqRes(ApiCacheHolder.getInstance().login);
                        appPref.save("loginId", username);
                        appPref.save("passwordHash", passwordHash);
                        appPref.setUserId(loginResponse.idUser);
                        if (SystemUtils.isNetworkConnected(AbstractBaseActivity.this)) {
                            startWebSocket();
                            syncDataWithServer();
                            checkDataSync();
                            loadAppConfig();
                        }
                        recreate();
                    } else {
                        showToast(getResources().getString(R.string.login_failed));
                        EventBus.getDefault().post(new InvalidAuthenticationEvent());
                    }
                }
            }, !SystemUtils.isNetworkConnected(this));

        }
    }

    public List<Course> getCourses() {
        return courses;
    }

    protected void setToolbarForVirtualCurrency() {
        try {
            if (LoginUserCache.getInstance().getLoginResponse().isRewardRedeemEnabled()) {
                toolbar.findViewById(R.id.redeem_layout).setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
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

    protected void setToolbarForTestSeries() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.test_series));
        loadCoursesList();
    }

    protected void setToolbarForAnalytics() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.analytics));
        showVirtualCurrency();
        loadCoursesList();
    }

    protected void setToolbarForMessages() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle("Messages");
        showVirtualCurrency();
        loadCoursesList();
    }

    protected void setToolbarForChallengeTest(boolean showButtons) {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.GONE);
        if (showButtons) {
            toolbar.findViewById(R.id.challenge_buttons_layout).setVisibility(View.VISIBLE);
        }
        showVirtualCurrency();
        toolbar.setBackgroundColor(getResources().getColor(R.color.red));
        setToolbarTitle(getResources().getString(R.string.challenge_your_friends));
    }

    protected void setToolbarForPostcomments() {
        toolbar.findViewById(R.id.new_comment).setVisibility(View.VISIBLE);
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
        setToolbarTitle(getString(R.string.app_name));
        loadCoursesList();
    }

    protected void setToolbarForCurriculumScreen() {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle("Curriculum");
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
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.exam_history));
        loadCoursesList();
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
        showVideoInToolbar(videos, position);
    }

    protected void setToolbarForOfflineContentReading() {
        toolbar.findViewById(R.id.download).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.download_content));
    }

    protected void setToolbarForForum() {
        toolbar.findViewById(R.id.new_post).setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        setToolbarTitle(getResources().getString(R.string.forum));
        loadCoursesList();
    }

    protected void setToolbarForExercise(String title, boolean showDrawer) {
        setToolbarTitle(title);
        showVirtualCurrency();
        if (!showDrawer) {
            hideDrawerIcon();
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    protected void setToolbarForFlaggedQuestions(String title) {
        toolbar.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.spinner_courses).setVisibility(View.GONE);
        setToolbarTitle(title);
        showVirtualCurrency();
        showDrawerIcon();
    }

    protected void setToolbarForWebActivity(String title) {
        setToolbarTitle(title);
    }

    protected void hideDrawerIcon() {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    protected void showDrawerIcon() {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
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
        AppConfig config = getAppConfig(this);
        if (config == null) {
            return;
        }
        navigationView.findViewById(R.id.navigation_welcome).setVisibility(View.VISIBLE);
        navigationView.findViewById(R.id.navigation_exam_history).setVisibility(View.VISIBLE);
        navigationView.findViewById(R.id.navigation_settings).setVisibility(View.VISIBLE);
        navigationView.findViewById(R.id.navigation_logout).setVisibility(View.VISIBLE);
    }

    private void setNavigationClickListeners() {
        navigationView.findViewById(R.id.navigation_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCourseEnded(selectedCourse)) {
                    showToast("Please Select different Course");
                    return;
                }
                if (!(AbstractBaseActivity.this instanceof UserProfileActivity)) {
                    Intent intent = new Intent(AbstractBaseActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();
                }
            }
        });

        navigationView.findViewById(R.id.navigation_welcome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCourseEnded(selectedCourse)) {
                    showToast("Please Select different Course");
                    return;
                }
                loadWelcomeScreen();
                drawerLayout.closeDrawers();
            }
        });

        navigationView.findViewById(R.id.navigation_smart_class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCourseEnded(selectedCourse)) {
                    showToast("Please Select different Course");
                    return;
                }
                if (SystemUtils.isNetworkConnected(AbstractBaseActivity.this)) {
                    loadSmartClass();
                } else {
                    showToast("Smart class requires network connection");
                }
                drawerLayout.closeDrawers();
            }
        });

        navigationView.findViewById(R.id.navigation_study_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCourseEnded(selectedCourse)) {
                    showToast("Please Select different Course");
                    return;
                }
                loadStudyCenterScreen();
            }
        });

        navigationView.findViewById(R.id.navigation_test_series).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCourseEnded(selectedCourse)) {
                    showToast("Please Select different Course");
                    return;
                }
                loadStudyCenterScreen();
            }
        });

        navigationView.findViewById(R.id.navigation_analytics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCourseEnded(selectedCourse)) {
                    showToast("Please Select different Course");
                    return;
                }
                if (SystemUtils.isNetworkConnected(AbstractBaseActivity.this)) {
                    startActivity(new Intent(AbstractBaseActivity.this, NewAnalyticsActivity.class));
                } else {
                    showToast("Analytics requires network connection");
                }
                drawerLayout.closeDrawers();
            }
        });

        navigationView.findViewById(R.id.navigation_recommended_reading).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCourseEnded(selectedCourse)) {
                    showToast("Please Select different Course");
                    return;
                }
                if (SystemUtils.isNetworkConnected(AbstractBaseActivity.this)) {
                    loadRecommendedReading();
                } else {
                    showToast("Recommended Reading requires network connection");
                }
                drawerLayout.closeDrawers();
            }
        });

        navigationView.findViewById(R.id.navigation_progress_report).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCourseEnded(selectedCourse)) {
                    showToast("Please Select different Course");
                    return;
                }
                if (SystemUtils.isNetworkConnected(AbstractBaseActivity.this)) {
                    loadProgressReport();
                } else {
                    showToast("Progress Report requires network connection");
                }
                drawerLayout.closeDrawers();
            }
        });

        navigationView.findViewById(R.id.navigation_time_management).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCourseEnded(selectedCourse)) {
                    showToast("Please Select different Course");
                    return;
                }
                if (SystemUtils.isNetworkConnected(AbstractBaseActivity.this)) {
                    loadTimeManagement();
                } else {
                    showToast("Time Management requires network connection");
                }
                drawerLayout.closeDrawers();
            }
        });

        navigationView.findViewById(R.id.navigation_curriculum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCourseEnded(selectedCourse)) {
                    showToast("Please Select different Course");
                    return;
                }
                startActivity(new Intent(AbstractBaseActivity.this, CurriculumActivity.class));
                drawerLayout.closeDrawers();
            }
        });

        navigationView.findViewById(R.id.navigation_offline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCourseEnded(selectedCourse)) {
                    showToast("Please Select different Course");
                    return;
                }
                startActivity(new Intent(AbstractBaseActivity.this, OfflineActivity.class));
                drawerLayout.closeDrawers();
            }
        });

        navigationView.findViewById(R.id.navigation_challenge_your_friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCourseEnded(selectedCourse)) {
                    showToast("Please Select different Course");
                    return;
                }
                if (SystemUtils.isNetworkConnected(AbstractBaseActivity.this)) {
                    startActivity(new Intent(AbstractBaseActivity.this, ChallengeActivity.class));
                } else {
                    showToast("Challenge Test requires network connection");
                }
                drawerLayout.closeDrawers();
            }
        });

        navigationView.findViewById(R.id.navigation_exam_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCourseEnded(selectedCourse)) {
                    showToast("Please Select different Course");
                    return;
                }
                if (SystemUtils.isNetworkConnected(AbstractBaseActivity.this)) {
                    startActivity(new Intent(AbstractBaseActivity.this, ExamHistoryActivity.class));
                } else {
                    showToast("Exam history requires network connection");
                }
            }
        });

        navigationView.findViewById(R.id.navigation_scheduled_tests).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCourseEnded(selectedCourse)) {
                    showToast("Please Select different Course");
                    return;
                }
                showScheduledTestsDialog();
            }
        });

        navigationView.findViewById(R.id.navigation_mock_tests).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCourseEnded(selectedCourse)) {
                    showToast("Please Select different Course");
                    return;
                }
                showMockTestsDialog();
            }
        });

        navigationView.findViewById(R.id.navigation_forum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCourseEnded(selectedCourse)) {
                    showToast("Please Select different Course");
                    return;
                }
                if (SystemUtils.isNetworkConnected(AbstractBaseActivity.this)) {
                    startActivity(new Intent(AbstractBaseActivity.this, ForumActivity.class));
                } else {
                    showToast("Forum requires network connection");
                }
            }
        });

        navigationView.findViewById(R.id.navigation_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemUtils.isNetworkConnected(AbstractBaseActivity.this)) {
                    startActivity(new Intent(AbstractBaseActivity.this, SettingsActivity.class));
                } else {
                    showToast("Settings required network connection");
                }
            }
        });

        navigationView.findViewById(R.id.navigation_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
    }

    protected void loadStudyCenterScreen() {
        if (selectedCourse != null && selectedCourse.isTestSeries()) {
            startActivity(new Intent(this, TestSeriesActivity.class));
        } else {
            startActivity(new Intent(AbstractBaseActivity.this, StudyCenterActivity.class));
        }
        if (!(this instanceof StudyCenterActivity)) {
            finish();
        }
    }

    protected void loadWelcomeScreen() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    protected void showMockTestsDialog() {
        showMockTestsDialog(null);
    }

    protected void showMockTestsDialog(List<MockTest> mockTests) {
        if (SystemUtils.isNetworkConnected(this)) {
            MockTestDialog dialog = new MockTestDialog();
            if (mockTests != null) {
                String mockTestsGson = Gson.get().toJson(mockTests);
                Bundle bundle = new Bundle();
                bundle.putString("mock_tests", mockTestsGson);
                dialog.setArguments(bundle);
            }
            dialog.show(getFragmentManager(), "MockTestsListDialog");
        } else {
            Intent exerciseIntent = new Intent(this, OfflineActivity.class);
            exerciseIntent.putExtra("selection", 1);
            startActivity(exerciseIntent);
        }
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

    private void loadRecommendedReading() {
        Intent intent = new Intent(this, WebviewActivity.class);
        intent.putExtra(LoginActivity.TITLE, "Recommended Reading");
        intent.putExtra(LoginActivity.URL, WebUrls.getRecommendedReadingUrl(getSelectedCourseId()));
        startActivity(intent);
    }

    private void loadProgressReport() {
        Intent intent = new Intent(this, WebviewActivity.class);
        intent.putExtra(LoginActivity.TITLE, "Progress Report");
        intent.putExtra(LoginActivity.URL, WebUrls.getProgressReportUrl(getSelectedCourseId()));
        startActivity(intent);
    }

    private void loadTimeManagement() {
        Intent intent = new Intent(this, WebviewActivity.class);
        intent.putExtra(LoginActivity.TITLE, "Time Management");
        intent.putExtra(LoginActivity.URL, WebUrls.getTimeManagementUrl(getSelectedCourseId()));
        startActivity(intent);
    }

    protected void setToolbarTitle(String title) {
        TextView textView = (TextView) toolbar.findViewById(R.id.toolbar_title);
        textView.setText(title);
    }

    protected void showVirtualCurrency() {
        try {
            // Enable Vc for Welcome activity. do not display anywhere else
            if (this instanceof WelcomeActivity) {
                toolbar.findViewById(R.id.ProgressBar).setVisibility(View.VISIBLE);
                toolbar.findViewById(R.id.currency_layout).setVisibility(View.VISIBLE);
                toolbar.findViewById(R.id.currency_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(AbstractBaseActivity.this, VirtualCurrencyActivity.class);
                        startActivity(intent);
                    }
                });
                ApiManager.getInstance(this).getVirtualCurrencyBalance(LoginUserCache.getInstance().getStudentId(), new ApiCallback<VirtualCurrencyBalanceResponse>(this) {
                    @Override
                    public void success(VirtualCurrencyBalanceResponse virtualCurrencyBalanceResponse, Response response) {
                        super.success(virtualCurrencyBalanceResponse, response);
                        toolbar.findViewById(R.id.ProgressBar).setVisibility(View.GONE);
                        if (virtualCurrencyBalanceResponse != null && virtualCurrencyBalanceResponse.balance != null) {
                            appPref.setVirtualCurrency(virtualCurrencyBalanceResponse.balance.intValue() + "");
                            TextView textView = (TextView) toolbar.findViewById(R.id.tv_virtual_currency);
                            textView.setText(virtualCurrencyBalanceResponse.balance.intValue() + "");
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        toolbar.findViewById(R.id.ProgressBar).setVisibility(View.GONE);
                    }
                });
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            toolbar.findViewById(R.id.ProgressBar).setVisibility(View.GONE);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void showToast(String message) {
        if (this != null && !TextUtils.isEmpty(message)) {
            showToast(this, message);
        }
    }

    public void showToast(Context context, String message) {
        if (context != null && !TextUtils.isEmpty(message)) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void showLongToast(String message) {
        if (this != null && !TextUtils.isEmpty(message)) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                showLogoutDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void logout() {
        logout(true);
    }

    protected void logout(final boolean navigateToLogin) {
        try {
            if (!SystemUtils.isNetworkConnected(this)) {
                logoutAccount(navigateToLogin);
                return;
            }
            LogoutModel logout = new LogoutModel();
            logout.AuthToken = LoginUserCache.getInstance().getLoginResponse().authtoken;
            appPref.remove("loginId");
            appPref.remove("passwordHash");
            appPref.clearUserId();
            ApiManager.getInstance(this).logout(Gson.get().toJson(logout), new ApiCallback<LogoutResponse>(this) {
                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    WebSocketHelper.get(AbstractBaseActivity.this).disconnectWebSocket();
                }

                @Override
                public void success(LogoutResponse logoutResponse, Response response) {
                    if (logoutResponse.isSuccessful()) {
                        if (navigateToLogin) {
                            showToast(getResources().getString(R.string.logout_successful));
                        }
                        WebSocketHelper.get(AbstractBaseActivity.this).disconnectWebSocket();
                    }
                }
            });
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        logoutAccount(navigateToLogin);
    }

    private void logoutAccount(boolean navigateToLogin) {
        LoginUserCache.getInstance().clearCache();
        appPref.remove("loginId");
        appPref.remove("passwordHash");
        resetCrashlyticsUserData();
        deleteSessionCookie();
        AbstractBaseActivity.selectedCourse = null;
        AbstractBaseActivity.selectedVideoPosition = 0;
        AbstractBaseActivity.sharedExamModels = null;
        if (navigateToLogin) {
            Intent intent = new Intent(AbstractBaseActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void loadCoursesList() {
        ApiManager.getInstance(this).getCourses(LoginUserCache.getInstance().getStudentId(),
                !(this instanceof WelcomeActivity || this instanceof StudyCenterActivity || this instanceof TestSeriesActivity),
                new ApiCallback<List<Course>>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        L.error(error.message);
                        showToast("Failed to fetch courses");
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

    protected EventBus getEventbus() {
        return EventBus.getDefault();
    }

    public void onEvent(Integer position) {
        selectedVideoPosition = position;
    }

    public void onEventMainThread(InvalidAuthenticationEvent event) {
        dbManager.deleteAuthentication(appPref.getValue("loginId"));
        showToast("Authentication failed. Please login to continue");
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void checkForceUpgrade() {
        try {
            AppConfig config = getAppConfig(this);
            if (SystemUtils.isNetworkConnected(AbstractBaseActivity.this)) {
                int appVersionCode = BuildConfig.VERSION_CODE;
                if (config.isforceUpgradeEnabled() && appVersionCode < config.getLatestVersionCode()) {
                    showUpdateAlert(true, true, null);
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private static AlertDialog dataSyncAlertDialog;

    private void showDataSyncAlert(long eventsCount) {
        if (dataSyncAlertDialog == null || !dataSyncAlertDialog.isShowing()) {
            try {
                long dataSyncTime = Long.parseLong(AppPref.get(getApplicationContext()).getValue("data_sync_later"));
                Calendar lastTime = Calendar.getInstance();
                lastTime.setTimeInMillis(dataSyncTime);
                lastTime.add(Constants.DATA_SYNC_ALERT_SKIP_DURATION_UNITS, Constants.DATA_SYNC_ALERT_SKIP_DURATION);
                Calendar currentTime = Calendar.getInstance();
                if (currentTime.getTimeInMillis() < lastTime.getTimeInMillis()) {
                    return;
                }
            } catch (Exception e) {
                L.error(e.getMessage(), e);
            }
            dataSyncAlertDialog = new AlertDialog.Builder(this)
                    .setTitle("Data Sync")
                    .setPositiveButton("Sync Now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                AppPref.get(getApplicationContext()).remove("data_sync_later");
                                startActivity(new Intent(AbstractBaseActivity.this, DataSyncActivity.class));
                                dialog.dismiss();
                            } catch (Exception e) {
                                L.error(e.getMessage(), e);
                            }
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setMessage("There are " + eventsCount + " offline events available to be synced to the server. Do you want to sync now?")
                    /*.setNegativeButton("Ask Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                AppPref.get(getApplicationContext()).save("data_sync_later", String.valueOf(new Date().getTime()));
                                dialogInterface.dismiss();
                            }catch (Exception e) {
                                L.error(e.getMessage(), e);
                            }
                        }
                    })*/.show();
        }
    }

    private void showUpdateAlert(boolean isForceUpdrage, final boolean isPlayStoreUpdate, final String apkUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("App Update")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (isPlayStoreUpdate) {
                                updateAppFromPlayStore();
                            } else {
                                updateAppFromThirdParty(apkUrl);
                            }
                            AbstractBaseActivity.this.isDaFuDialogShown = false;
                        } catch (Exception e) {
                            L.error(e.getMessage(), e);
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false);
        if (!isForceUpdrage) {
            builder.setMessage("There is a new version of this app available, would you like to upgrade now?");
            builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    AbstractBaseActivity.this.isDaFuDialogShown = false;
                    onLoginFlowCompleted();
                }
            });
        } else {
            builder.setMessage("There is a new version of this app available, please upgrade to continue");
        }
        builder.show();
        isDaFuDialogShown = true;
    }

    protected void updateAppFromPlayStore() {
        try {
            final String appPackageName = BuildConfig.APPLICATION_ID;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    protected void updateAppFromThirdParty(String apkUrl) {
        Intent intent = new Intent(this, AppUpdateActivity.class);
        intent.putExtra("apk_url", apkUrl);
        startActivity(intent);
        finish();
    }

    // this method will be overridden by the classes that subscribes from event bus
    public void onEvent(Course course) {
        enableNavigationOpitons(course);
        if (selectedCourse == null || (selectedCourse != null && selectedCourse.courseId != course.courseId)) {
            selectedCourse = course;
            if (isCourseEnded(course) && !(this instanceof WelcomeActivity)
                    && !(this instanceof TestSeriesActivity) && !(this instanceof StudyCenterActivity)) {
                Intent newIntent = new Intent(this, WelcomeActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(newIntent);
                finish();
                return;
            }
            onCourseChanged(course);
        } else {
            return;
        }
    }

    protected void onCourseChanged(Course course) {
        // This method will be overridden in all activities
    }

    private void enableNavigationOpitons(Course course) {
        if (course != null) {
            navigationView.findViewById(R.id.navigation_study_center).setVisibility(isTrue(course.isStudyCenter) ? View.VISIBLE : View.GONE);
            navigationView.findViewById(R.id.navigation_forum).setVisibility(isTrue(course.isForums) ? View.VISIBLE : View.GONE);
            navigationView.findViewById(R.id.navigation_mock_tests).setVisibility(isTrue(course.isMockTest) ? View.VISIBLE : View.GONE);
            navigationView.findViewById(R.id.navigation_smart_class).setVisibility(isTrue(course.isSmartClass) ? View.VISIBLE : View.GONE);
            navigationView.findViewById(R.id.navigation_scheduled_tests).setVisibility(isTrue(course.isScheduledTests) ? View.VISIBLE : View.GONE);
            navigationView.findViewById(R.id.navigation_analytics).setVisibility(isTrue(course.isAnalytics) ? View.VISIBLE : View.GONE);
            navigationView.findViewById(R.id.navigation_challenge_your_friends).setVisibility(isTrue(course.isChallengeTest) ? View.VISIBLE : View.GONE);
            navigationView.findViewById(R.id.navigation_curriculum).setVisibility(isTrue(course.isCurriculum) ? View.VISIBLE : View.GONE);
            navigationView.findViewById(R.id.navigation_offline).setVisibility(isTrue(course.isOffline) ? View.VISIBLE : View.GONE);
            navigationView.findViewById(R.id.navigation_profile).setVisibility(isTrue(course.isProfile) ? View.VISIBLE : View.GONE);
            navigationView.findViewById(R.id.navigation_recommended_reading).setVisibility(isTrue(course.isRecommendedReading) ? View.VISIBLE : View.GONE);
            navigationView.findViewById(R.id.navigation_time_management).setVisibility(isTrue(course.isTimeManagement) ? View.VISIBLE : View.GONE);
            navigationView.findViewById(R.id.navigation_welcome).setVisibility(isTrue(course.isWelcome) ? View.VISIBLE : View.GONE);
            navigationView.findViewById(R.id.navigation_test_series).setVisibility(course.isTestSeries() ? View.VISIBLE : View.GONE);
        }
    }

    private boolean isTrue(Integer value) {
        return value != null && value == 1;
    }

    protected void getContentIndex(Course course, String studentId) {
        if (course == null || course.courseId == null || course.isTestSeries()) {
            return;
        }
        ApiManager.getInstance(this).getContentIndex(course.courseId.toString(), studentId,
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

    public boolean isCourseEnded(Course course) {
        if (course != null && course.isEnded()) {
            return true;
        }
        return false;
    }

    public void showProgress() {
        if (isShown()) {
            ProgressBar pbar = new ProgressBar(this);
            pbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(pbar);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }

    public void closeProgress() {
        if (isShown() && dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void onEvent(ContentReadingEvent event) {
        L.debug("ContentReadingEvent id", event.idContent);
        new UpdateUserEvents(this).postContentReading(this, event);
    }

    public void onEvent(ForumPostingEvent event) {
        L.debug("ForumPostingEvent id", event.id);
        new UpdateUserEvents(this).postForumPosting(this, event);
    }

    public void onEvent(ExerciseAnsEvent event) {
        if (event != null && event.id != null) {
            L.debug("ExerciseAnsEvent id", event.id + "");
            new UpdateUserEvents(this).postExerciseAns(this, event);
        }
    }

    public void onEvent(TakingTestEvent event) {
        L.debug("TakingTestEvent id", event.id);
        new UpdateUserEvents(this).postTakingTest(this, event);
    }

    protected void redeem() {
        Intent intent = new Intent(this, WebviewActivity.class);
        intent.putExtra(LoginActivity.TITLE, getString(R.string.redeem));
        intent.putExtra(LoginActivity.URL, WebUrls.getRedeemUrl());
        startActivity(intent);
    }

    protected void loadScheduledTests() {
        ApiManager.getInstance(this).getScheduledTestsList(
                LoginUserCache.getInstance().getStudentId(),
                new ApiCallback<ScheduledTestList>(this) {

                    @Override
                    public void success(ScheduledTestList scheduledTests, Response response) {
                        super.success(scheduledTests, response);
                        if (scheduledTests != null && scheduledTests.MockTest != null) {
                            ApiCacheHolder.getInstance().setScheduleTestsResponse(scheduledTests);
                            dbManager.saveReqRes(ApiCacheHolder.getInstance().scheduleTests);
                            dbManager.deleteExpiredScheduleTests(scheduledTests.MockTest);
                            scheduleNotificationsForScheduledTests(scheduledTests);
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        L.error(error.message);
                    }
                });
    }

    public void scheduleNotificationsForScheduledTests(ScheduledTestList scheduledTestList) {
        try {
            for (int i = 0; i < scheduledTestList.MockTest.size(); i++) {
                Date scheduledTestTime = TimeUtils.getDate(TimeUtils.getMillisFromDate(scheduledTestList.MockTest.get(i).startTime));
                examDownloadNotification(scheduledTestList.MockTest.get(i).testQuestionPaperId,
                        scheduledTestList.MockTest.get(i).examName,
                        scheduledTestTime);
                examAdvancedNotification(scheduledTestList.MockTest.get(i).testQuestionPaperId,
                        scheduledTestList.MockTest.get(i).examName,
                        scheduledTestTime);
                examStartedNotification(scheduledTestList.MockTest.get(i).testQuestionPaperId,
                        scheduledTestList.MockTest.get(i).examName,
                        scheduledTestTime);
                examForceStartNotification(scheduledTestList.MockTest.get(i).testQuestionPaperId,
                        scheduledTestList.MockTest.get(i).examName,
                        scheduledTestTime);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void examDownloadNotification(String examId, String examName, Date scheduledTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(scheduledTime);
        cal.add(Calendar.MINUTE, -15);
        if (TimeUtils.currentTimeInMillis() > cal.getTimeInMillis()) {
            return;
        }
        cal = Calendar.getInstance();
        Calendar expiry = Calendar.getInstance();
        expiry.setTimeInMillis(scheduledTime.getTime());
        PugNotification.with(this)
                .load()
                .identifier(Data.getInt(examId))
                .title(examName)
                .autoCancel(true)
                .message("Exam starts at " + new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(scheduledTime) + ". Please open app and Download")
                .bigTextStyle("Exam starts at " + new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(scheduledTime) + ". Please open app and Download")
                .smallIcon(R.drawable.ic_launcher)
                .largeIcon(R.drawable.ic_launcher)
//                .click(TestInstructionsActivity.class, getScheduledTestBundle(examId))
                .flags(Notification.DEFAULT_ALL)
                .simple()
                .build();
        L.info("Scheduled Download Notification for " + TimeUtils.getDateString(cal.getTimeInMillis()));
        // cancel notification
        Intent intent = new Intent(this, NotifyReceiver.class);
        intent.setAction("CANCEL_NOTIFICATION");
        intent.putExtra("id", Data.getInt(examId + Constants.EXAM_DOWNLOADED_REQUEST_ID));
        PendingIntent cancelDownloadNotifIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, expiry.getTimeInMillis(), cancelDownloadNotifIntent);
    }

    private void examAdvancedNotification(String examId, String examName, Date scheduledTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(scheduledTime);
        cal.add(Calendar.MINUTE, -15);
        if (TimeUtils.currentTimeInMillis() > cal.getTimeInMillis()) {
            return;
        }
        Calendar expiry = Calendar.getInstance();
        expiry.setTimeInMillis(scheduledTime.getTime());
        if (cal.getTimeInMillis() < TimeUtils.currentTimeInMillis()) {
            cal = Calendar.getInstance();
        }
        PugNotification.with(this)
                .load()
                .identifier(Data.getInt(examId))
                .title(examName)
                .message("Exam starts at " + new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(scheduledTime))
                .bigTextStyle("Exam starts at " + new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(scheduledTime))
                .smallIcon(R.drawable.ic_launcher)
                .largeIcon(R.drawable.ic_launcher)
//                .click(TestInstructionsActivity.class, getScheduledTestBundle(examId))
                .flags(Notification.DEFAULT_ALL)
                .simple()
                .build();
        L.info("Scheduled Advanced Notification for " + TimeUtils.getDateString(cal.getTimeInMillis()));
        // cancel notification
        Intent intent = new Intent(this, NotifyReceiver.class);
        intent.setAction("CANCEL_NOTIFICATION");
        intent.putExtra("id", Data.getInt(examId + Constants.EXAM_ADVANCED_NOTIFICATION_REQUEST_ID));
        PendingIntent cancelDownloadNotifIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, expiry.getTimeInMillis(), cancelDownloadNotifIntent);
    }

    private void examStartedNotification(String examId, String examName, Date scheduledTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(scheduledTime);
        cal.add(Calendar.MINUTE, -5);
        if (TimeUtils.currentTimeInMillis() > cal.getTimeInMillis()) {
            return;
        }
        Intent broadCastIntent = new Intent(this, NotifyReceiver.class);
        broadCastIntent.putExtra("test_question_paper_id", examId);
        broadCastIntent.putExtra("start_exam", true);
        PugNotification.with(this)
                .load()
                .identifier(Data.getInt(examId))
                .when(cal.getTimeInMillis())
                .title(examName)
                .message("Exam will start at " + new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(scheduledTime))
                .bigTextStyle("Exam will start at " + new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(scheduledTime))
                .smallIcon(R.drawable.ic_launcher)
                .largeIcon(R.drawable.ic_launcher)
//                .click(TestInstructionsActivity.class, getScheduledTestBundle(examId))
                .flags(Notification.DEFAULT_ALL)
                .simple()
                .build();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) TimeUtils.currentTimeInMillis(),
                broadCastIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        L.info("Scheduled Started Notification for " + TimeUtils.getDateString(cal.getTimeInMillis()));
    }

    private Bundle getScheduledTestBundle(String examTemplateId) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TEST_TITLE, "Scheduled Test");
        bundle.putString("test_question_paper_id", examTemplateId);
        return bundle;
    }

    private void examForceStartNotification(String examId, String examName, Date scheduledTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(scheduledTime);
        cal.add(Calendar.SECOND, -15);
        if (TimeUtils.currentTimeInMillis() > cal.getTimeInMillis()) {
            return;
        }
        Intent broadCastIntent = new Intent(this, NotifyReceiver.class);
        broadCastIntent.putExtra("test_question_paper_id", examId);
        broadCastIntent.putExtra("start_exam", true);
        broadCastIntent.putExtra("id", Data.getInt(examId));
        broadCastIntent.putExtra("time", scheduledTime.getTime());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) TimeUtils.currentTimeInMillis(),
                broadCastIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        L.info("Scheduled Started Notification for " + TimeUtils.getDateTimeString(cal.getTimeInMillis()));
    }

    public void onEventMainThread(com.education.corsalite.event.Toast toast) {
        if (!TextUtils.isEmpty(toast.message)) {
            showToast(toast.message);
        }
    }

    private AlertDialog connectAlert;

    public void onEventMainThread(ConnectExceptionEvent event) {
        if (connectAlert == null || !connectAlert.isShowing()) {
            connectAlert = new AlertDialog.Builder(this)
                    .setTitle("Network Timeout")
                    .setMessage("Sorry, your data connection timeout error has occurred. Click OK to retry.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            refreshScreen();
                        }
                    }).show();
        }
    }

    public void onEventMainThread(TimeChangedEvent event) {
        showToast("Time is modifed externally. Please reset the time to use the app");
    }

    // trigger challenge test request
    public void onEventMainThread(ChallengeTestRequestEvent event) {
        ChallengeUtils.get(this).showChallengeRequestNotification(event);
    }

    public void onEventMainThread(ChallengeTestUpdateEvent event) {
        if (!TextUtils.isEmpty(event.challengerStatus) && event.challengerStatus.equalsIgnoreCase("Canceled")) {
            ChallengeUtils.get(this).clearChallengeNotifications(event.challengeTestParentId);
            if (this instanceof ChallengeActivity) {
                finish();
                showToast("Challenger has cancelled the challenge");
            }
        } else if (!(this instanceof ChallengeActivity)) {
            ChallengeUtils.get(this).showChallengeUpdateNotification(event);
        }
    }

    public void onEventMainThread(ChallengeTestStartEvent event) {
        if (!(this instanceof ExamEngineActivity) && !ChallengeUtils.get(this).challengeStarted) {
            ChallengeUtils.get(this).challengeStarted = true;
            ChallengeUtils.get(this).clearChallengeNotifications(event.challengeTestParentId);
            Intent intent = new Intent(this, ExamEngineActivity.class);
            intent.putExtra(Constants.TEST_TITLE, "Challenge Test");
            intent.putExtra("test_question_paper_id", event.testQuestionPaperId);
            startActivity(intent);
        }
        if (this instanceof ChallengeActivity) {
            finish();
        }
    }

    public void onEventMainThread(ScheduledTestStartEvent event) {
        try {
            if (this instanceof TestInstructionsActivity || this instanceof ExamEngineActivity) {
                return;
            }
            OfflineTestObjectModel model = dbManager.fetchOfflineTestRecord(event.testQuestionPaperId);
            if (model != null && model.testQuestionPaperId.equalsIgnoreCase(event.testQuestionPaperId)) {
                Intent intent = new Intent(this, TestInstructionsActivity.class);
                intent.putExtra(Constants.TEST_TITLE, "Scheduled Test");
                intent.putExtra("test_question_paper_id", model.testQuestionPaperId);
                intent.putExtra("test_answer_paper_id", model.testAnswerPaperId);
                if (model.status == Constants.STATUS_SUSPENDED) {
                    intent.putExtra("test_status", "Suspended");
                }
                startActivity(intent);
                return;
            } else if (SystemUtils.isNetworkConnected(this)) {
                // start the test
                Intent examIntent = new Intent(this, TestInstructionsActivity.class);
                examIntent.putExtra(Constants.TEST_TITLE, "Scheduled Test");
                examIntent.putExtra("test_question_paper_id", event.testQuestionPaperId);
                startActivity(examIntent);
            }

        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    protected void startWebSocket() {
        WebSocketHelper.get(this).connectWebSocket();
    }

    private AlertDialog socketAlert;

    protected void showSocketDisconnectionAlert(boolean isConnected) {
        if (!isConnected && socketAlert == null) {
            socketAlert = new AlertDialog.Builder(this)
                    .setTitle("Connection Failure")
                    .setMessage("Failed to connect with server. Please try later")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
            socketAlert.setCancelable(false);
        } else {
            if (!isConnected) {
                socketAlert.show();
            } else {
                socketAlert.dismiss();
            }
        }
    }

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
