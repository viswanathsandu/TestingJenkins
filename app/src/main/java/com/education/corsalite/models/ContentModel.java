package com.education.corsalite.models;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Girish on 03/10/15.
 */
public class ContentModel extends BaseModel implements Comparable<ContentModel>, Serializable {

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
    public String updateTime;
    @SerializedName("NamePrefix")
    public String namePrefix;
    @SerializedName("GivenName")
    public String givenName;
    @SerializedName("SurName")
    public String surName;
    @SerializedName("Rating")
    public String rating;

    public String topicId;
    public String topicName;


    @Override
    public int compareTo(ContentModel another) {
        return Integer.valueOf(this.idContent) - Integer.valueOf(another.idContent);
    }

    @Override
    public String toString() {
        return contentName;
    }
}
