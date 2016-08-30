package com.education.corsalite.fragments;

import android.app.Activity;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.requestmodels.UserProfileModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.EditProfileModel;
import com.education.corsalite.models.responsemodels.UserProfileResponse;
import com.education.corsalite.utils.AppPref;
import com.education.corsalite.utils.ImageUtils;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.TimeUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Aastha on 20/09/15.
 */
public class EditProfilePicDialogFragment extends DialogFragment {

    IUpdateProfilePicListener updateProfilePicListener;
    String TAG = EditProfilePicDialogFragment.class.getCanonicalName();

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
        user = Gson.get().fromJson(getArguments().getString("user_profile_response"), UserProfileResponse.class);
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
        takePicCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File file = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE);
                        } catch (ActivityNotFoundException e) {
                            ((AbstractBaseActivity) getActivity()).showToast("Device couldn't start camera");
                        }

                    }
                });

        uploadPicGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
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
                /*Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                updateUserProfile(thumbnail);*/
                getCameraPic();
            } else if (requestCode == REQUEST_CODE_PICK_GALLERY) {
                try {
                    Uri selectedImage = data.getData();
                    updateUserProfile(BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage)));
                }catch (FileNotFoundException e){
                    Log.e(TAG,"File not found");
                }
            }
        }else{
            //Do nothing
            //User cancelled operation
        }
    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    private void getCameraPic(){
        File file = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        String path = file.getAbsolutePath();
        //Not saving the captured image in the gallery
        if(path != null){
           Bitmap selectedImage = new ImageUtils(getActivity()).getCompressedProfileImage(path);
            if(selectedImage != null)
                updateUserProfile(selectedImage);
        }
    }

    protected void deleteTempFile() {
        File file = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    private void updateUserProfile(final Bitmap image) {
        if(image == null){
            return;
        }
        String userProfileJson = Gson.get().toJson(getUserData(image));
        ApiManager.getInstance(getActivity()).updateUserProfile(userProfileJson, new ApiCallback<EditProfileModel>(getActivity()) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                L.error(error.message);
                if(getActivity() != null) {
                    ((AbstractBaseActivity) getActivity()).showToast("Failed to update profile pic");
                    getDialog().cancel();
                }
                deleteTempFile();
            }

            @Override
            public void success(EditProfileModel editProfileResponse, Response response) {
                super.success(editProfileResponse, response);
                if (editProfileResponse.isSuccessful() && getActivity() != null) {
                    ((AbstractBaseActivity)getActivity()).showToast("Updated User Profile Pic Successfully");
                    getDialog().cancel();
                    updateProfilePicListener.onUpdateProfilePic(image);
                    deleteTempFile();
                }
            }
        });

    }

    private UserProfileModel getUserData(Bitmap image) {
        UserProfileModel model = new UserProfileModel();
        model.updateTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(TimeUtils.getCurrentDate());
        model.userId = AppPref.get(getActivity()).getUserId();
        model.studentId = LoginUserCache.getInstance().getStudentId();
        //encode and update
        if(image != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            model.photoBase64Data = Base64.encodeToString(b, Base64.DEFAULT);
            model.photoBase64Data = model.photoBase64Data.replace("+","%2B");
        }else{
            model.photoBase64Data = null;
        }
        return model;
    }
    public interface IUpdateProfilePicListener {
        void onUpdateProfilePic(Bitmap image);
    }
}