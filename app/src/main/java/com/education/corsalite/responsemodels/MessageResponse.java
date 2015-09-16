package com.education.corsalite.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vissu on 9/12/15.
 */
public class MessageResponse extends BaseResponseModel{
    public List<Message> messages;
}
