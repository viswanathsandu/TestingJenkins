package com.education.corsalite.models.responsemodels;

import android.content.Context;
import android.text.TextUtils;

import com.education.corsalite.R;
import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

/**
 * Created by vissu on 9/11/15.
 */
public class LoginResponse extends BaseResponseModel {
    @SerializedName("idUser")
    public String idUser;
    @SerializedName("idStudent")
    public String studentId;
    @SerializedName("idEntity")
    public String entitiyId;
    @SerializedName("AuthToken")
    public String authtoken;
    @Ignore
    @SerializedName("disableRewardRedeem")
    public String disableRewardRedeem;

    // Is used for challenge test
    public String displayName = "";

    public LoginResponse() {
    }

    public boolean isSuccessful(Context context) {
        String fgaEntityId = context.getString(R.string.entityId);
        return super.isSuccessful() && (TextUtils.isEmpty(fgaEntityId)
                || fgaEntityId.equalsIgnoreCase(entitiyId));
    }

    public boolean isRewardRedeemEnabled() {
        return !TextUtils.isEmpty(disableRewardRedeem) && disableRewardRedeem.equalsIgnoreCase("N");
    }
}