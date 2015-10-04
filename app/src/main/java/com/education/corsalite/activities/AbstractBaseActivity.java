package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.requestmodels.LogoutModel;
import com.education.corsalite.models.responsemodels.Content;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.ContentResponse;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.LogoutResponse;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.CookieUtils;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by vissu on 9/11/15.
 */
public abstract class AbstractBaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    protected FrameLayout frameLayout;

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
    }

    private void initNavigationDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {

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
        navigationView.findViewById(R.id.menu_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(AbstractBaseActivity.this instanceof UserProfileActivity)) {
                    Intent intent = new Intent(AbstractBaseActivity.this, UserProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });

        navigationView.findViewById(R.id.menu_virtual_currency).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AbstractBaseActivity.this, VirtualCurrencyActivity.class);
                startActivity(intent);
            }
        });

        navigationView.findViewById(R.id.menu_study_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AbstractBaseActivity.this, StudyCentreActivity.class);
                startActivity(intent);
            }
        });

        navigationView.findViewById(R.id.menu_content_reading).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AbstractBaseActivity.this, WebActivity.class);
                startActivity(intent);

            }
        });

        navigationView.findViewById(R.id.menu_analytics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AbstractBaseActivity.this, AnalyticsActivity.class));
            }
        });
    }

    protected void setToolbarTitle(String title){
        TextView textView = (TextView) findViewById(R.id.toolbar_title);
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
        ApiManager.getInstance(this).logout(new Gson().toJson(logout), new ApiCallback<LogoutResponse>() {
            @Override
            public void failure(CorsaliteError error) {
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

    private void goToStudyCentre() {
        startActivity(new Intent(AbstractBaseActivity.this, StudyCentreActivity.class));
    }

    protected void getContentData(String courseId, String updateTime) {
        // TODO : passing static data
        ApiManager.getInstance(this).getContent(courseId, updateTime,
                new ApiCallback<List<Content>>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            showToast(error.message);
                        }
                    }

                    @Override
                    public void success(List<Content> mContentResponse, Response response) {
                        if (mContentResponse != null) {
                            Intent intent = new Intent(AbstractBaseActivity.this, WebActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("contentData", (Serializable) mContentResponse);
                            intent.putExtras(bundle);
                            startActivity(intent);

                        }
                    }
                });
    }


    public void saveSessionCookie(Response response) {
        String cookie = CookieUtils.getCookieString(response);
        if (cookie != null) {
            ApiClientService.setSetCookie(cookie);
        }
    }

    public void deleteSessionCookie() {
        ApiClientService.setSetCookie(null);
    }
}
