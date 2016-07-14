package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by vissu on 9/12/15.
 */
public class VirtualCurrencyTransaction extends BaseModel implements Serializable {

    @SerializedName("EventDate")
    public String eventDate;
    @SerializedName("EventName")
    public String eventName;
    @SerializedName("EarnedVirtualCurrency")
    public String earnedVirtualCurrency;
    @SerializedName("Balance")
    public String balance;
}
