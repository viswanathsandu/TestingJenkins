package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Girish on 30/09/15.
 */
public class ContentResponse implements Serializable{
    @SerializedName("contentData")
    public List<Content> contents;
}
