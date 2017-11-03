package com.education.corsalite.models.requestmodels;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 9/14/16.
 */

public class ClientEntityConfigRequest extends BaseModel {

    public String idUser;
    @SerializedName("DeviceID")
    public String deviceId;

    public ClientEntityConfigRequest() {
    }

    public ClientEntityConfigRequest(String idUser, String deviceId) {
        this.idUser = idUser;
        this.deviceId = deviceId;
    }
}
