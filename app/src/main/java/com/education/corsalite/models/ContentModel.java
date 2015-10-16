package com.education.corsalite.models;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by mt0060 on 03/10/15.
 */
public class ContentModel extends BaseModel implements Comparable<ContentModel>{

    public String idContent;
    @SerializedName("Type")
    public String type;
    @SerializedName("ContentName")
    public String contentName;
    @SerializedName("Status")
    public String status;
    @SerializedName("AuthorCopyright")
    public String authorCopyright;
    @SerializedName("UpdateTime")
    String updateTime;
    @SerializedName("NamePrefix")
    String namePrefix;
    @SerializedName("GivenName")
    String givenName;
    @SerializedName("SurName")
    String surName;
    @SerializedName("Rating")
    String rating;


    @Override
    public int compareTo(ContentModel another) {
        return Integer.valueOf(this.idContent) - Integer.valueOf(another.idContent);
    }
}
