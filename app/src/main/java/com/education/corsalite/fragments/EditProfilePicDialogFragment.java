package com.education.corsalite.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.requestmodels.UserProfileModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.EditProfileModel;
import com.education.corsalite.models.responsemodels.UserProfileResponse;
import com.education.corsalite.utils.L;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Aastha on 20/09/15.
 */
public class EditProfilePicDialogFragment extends DialogFragment {

    IUpdateProfilePicListener updateProfilePicListener;

    private UserProfileResponse user;
    @Bind(R.id.take_pic_camera)
    TextView takePicCamera;
    @Bind(R.id.upload_pic_gallery)
    TextView uploadPicGallery;
    @Bind(R.id.cancel) TextView cancel;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_profile_pic_dialog_fragment, container, false);
        ButterKnife.bind(this, v);
        user = new Gson().fromJson(getArguments().getString("user_profile_response"), UserProfileResponse.class);
        setListeners();
        getDialog().setTitle("Edit Profile Picture");
        return v;
    }

    public void setUpdateProfilePicListener(IUpdateProfilePicListener updateProfilePicListener) {
        this.updateProfilePicListener = updateProfilePicListener;
    }

    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;

    private void setListeners() {
        takePicCamera.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE);
                    }
                });

        uploadPicGallery.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
                        try {
                            startActivityForResult(intent, REQUEST_CODE_PICK_GALLERY);
                        } catch (ActivityNotFoundException e) {
                            ((AbstractBaseActivity) getActivity()).showToast("No image source available");
                        }
                    }
                });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().cancel();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                updateUserProfile(thumbnail);
            } else if (requestCode == REQUEST_CODE_PICK_GALLERY) {

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                updateUserProfile(BitmapFactory.decodeFile(imgDecodableString));
            }
            getDialog().cancel();
        }else{
            //Do nothing
            //User cancelled operation
        }
    }

    private void updateUserProfile(Bitmap image) {
        String userProfileJson = new Gson().toJson(getUserData(image));
        ApiManager.getInstance(getActivity()).updateUserProfile(userProfileJson, new ApiCallback<EditProfileModel>(getActivity()) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                L.error(error.message);
                // TODO : its returning failed. but data is being updated. Need to fix it
                if(getActivity() != null) {
                    ((AbstractBaseActivity) getActivity()).showToast("Updated User Profile Pic Successfully");
                    getDialog().cancel();
                }
            }

            @Override
            public void success(EditProfileModel editProfileResponse, Response response) {
                super.success(editProfileResponse, response);
                if (editProfileResponse.isSuccessful()) {
                    ((AbstractBaseActivity)getActivity()).showToast("Updated User Profile Pic Successfully");
                    getDialog().cancel();
                }
            }
        });
    }

    private UserProfileModel getUserData(Bitmap image) {
        UserProfileModel model = new UserProfileModel();
        model.updateTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        model.userId = LoginUserCache.getInstance().loginResponse.userId;
        model.studentId = LoginUserCache.getInstance().loginResponse.studentId;
        //encode and update
        if(image != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            model.photoBase64Data = Base64.encodeToString(b, Base64.NO_WRAP);
        }else{
            model.photoBase64Data = null;
        }
        //updateProfilePicListener.onUpdateProfilePic(image);
        return model;
    }
    public interface IUpdateProfilePicListener {
        void onUpdateProfilePic(Bitmap image);
    }
}