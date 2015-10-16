package com.education.corsalite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.fragments.DatePickerDialogFragment;
import com.education.corsalite.models.requestmodels.UserProfileModel;
import com.education.corsalite.models.responsemodels.EditProfileModel;
import com.education.corsalite.models.responsemodels.BasicProfile;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.UserProfileResponse;
import com.education.corsalite.utils.L;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Aastha on 15/09/15.
 */
public class EditProfileActivity extends AbstractBaseActivity implements DatePickerDialogFragment.IDateSelectionListener {

    @Bind(R.id.profile_save) Button save;
    @Bind(R.id.et_given_name)EditText givenName;
    @Bind(R.id.et_address1)EditText address1;
    @Bind(R.id.et_address2)EditText address2;
    @Bind(R.id.et_admission_number)EditText admissionNumber;
    @Bind(R.id.et_city)EditText city;
    @Bind(R.id.et_confirm_password)EditText confirmPassword;
    @Bind(R.id.et_display_name)EditText displayName;
    @Bind(R.id.et_email)EditText email;
    @Bind(R.id.et_facebook_id)EditText faceBookId;
    @Bind(R.id.et_mobile)EditText mobile;
    @Bind(R.id.et_phone)EditText phone;
    @Bind(R.id.et_password)EditText password;
    @Bind(R.id.et_pincode)EditText pincode;
    @Bind(R.id.et_residence_status)EditText residenceStatus;
    @Bind(R.id.et_surname)EditText surname;
    @Bind(R.id.et_state)EditText state;
    @Bind(R.id.dob)EditText dob;
    @Bind(R.id.rb_gender_female)RadioButton genderFemale;
    @Bind(R.id.rb_gender_male)RadioButton genderMale;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        final UserProfileResponse user =(UserProfileResponse) getIntent().getBundleExtra("user_profile").getSerializable("user_profile_response");
        populateUserProfile(user.basicProfile);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!password.getText().toString().equals(confirmPassword.getText().toString())){
                    Toast.makeText(EditProfileActivity.this,"Password and Confirm Password do not match",Toast.LENGTH_LONG).show();
                    return;
                }
                String userProfileJson = new Gson().toJson(saveUserProfile());
                ApiManager.getInstance(EditProfileActivity.this).updateUserProfile(userProfileJson, new ApiCallback<EditProfileModel>(EditProfileActivity.this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        L.error(error.message);
                        showToast("Failed to update user Profile");
                    }

                    @Override
                    public void success(EditProfileModel editProfileResponse, Response response) {
                        super.success(editProfileResponse, response);
                        if (editProfileResponse.isSuccessful()) {
                            showToast("Updated User Profile Successfully");
                            Intent intent = new Intent(EditProfileActivity.this, UserProfileActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    private UserProfileModel saveUserProfile(){
        UserProfileModel profile = new UserProfileModel();
        profile.updateTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        profile.userId = LoginUserCache.getInstance().loginResponse.userId;
        if(!givenName.getText().toString().isEmpty()) {
            profile.givenName = givenName.getText().toString();
        }
        if(!displayName.getText().toString().isEmpty()) {
            profile.displayName = displayName.getText().toString();
        }
        if(!dob.getText().toString().isEmpty()) {
            profile.dob = dob.getText().toString();
        }
        if(!email.getText().toString().isEmpty()) {
            profile.emailId = email.getText().toString();
        }
        if(!mobile.getText().toString().isEmpty()) {
            profile.mobile = mobile.getText().toString();
        }
        if(!surname.getText().toString().isEmpty()) {
            profile.surName = surname.getText().toString();
        }
        String gender = null;
        if(genderFemale.isSelected()){
            profile.gender = "Female";
        }else if(genderMale.isSelected()){
            profile.gender = "Male";
        }
        return profile;

    }

    private void populateUserProfile(BasicProfile user){
        givenName.setText(user.givenName);
        displayName.setText(user.displayName);
        address1.setText(user.address1);
        address2.setText(user.address2);
        phone.setText(user.phone);
        mobile.setText(user.mobile);
        admissionNumber.setText(user.admissionNumber);
        faceBookId.setText(user.faceBookId);
        dob.setText(user.dob);
        city.setText(user.city);
        state.setText(user.state);
        residenceStatus.setText(user.residenceStatus);
        pincode.setText(user.pincode);
        surname.setText(user.surName);
        email.setText(user.emailId);

        if(user.gender != null && user.gender.equalsIgnoreCase("male")){
            genderMale.setChecked(true);
            genderFemale.setChecked(false);
        }else if(user.gender !=null && user.gender.equalsIgnoreCase("female")){
            genderFemale.setChecked(true);
            genderMale.setChecked(false);
        }

        initDatePicker();
    }


    private void initDatePicker(){
        dob.setKeyListener(null);

        dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showDatePicker();
                }
            }
        });
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }
    private void showDatePicker(){
        DatePickerDialogFragment datePicker = new DatePickerDialogFragment();
        datePicker.show(getFragmentManager(), "datePicker");
    }


    @Override
    public void onDateSelect(int day,int month, int year) {
        dob.setText(day + "-" + month + "-" + year);
    }
}