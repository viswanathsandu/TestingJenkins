package com.education.corsalite.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vissu on 9/12/15.
 */
public class VirtualCurrencyBalanceResponse extends BaseResponseModel{
    @SerializedName("VirtualCurrencyBalance")
    public Double balance;

    @Override
    public String toString() {
        return null;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
