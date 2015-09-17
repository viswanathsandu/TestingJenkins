package com.education.corsalite.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vissu on 9/12/15.
 */
public class VirtualCurrencyBalanceResponse extends BaseResponseModel implements Serializable{
    @SerializedName("VirtualCurrencyBalance")
    public Double balance;
}
