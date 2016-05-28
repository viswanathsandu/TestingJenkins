package com.corsalite.tabletapp.models.responsemodels;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Girish on 30/09/15.
 */
public class ContentResponse implements Serializable{
    @Ignore
    @SerializedName("contentData")
    public List<Content> contents;
}
