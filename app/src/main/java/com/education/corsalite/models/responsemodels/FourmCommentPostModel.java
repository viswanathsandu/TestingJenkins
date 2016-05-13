package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vissu on 4/23/16.
 */
public class FourmCommentPostModel extends BaseResponseModel {
    @SerializedName("postData")
    public ForumPost post;
    @SerializedName("postDataReply")
    public List<ForumPost> commentPosts;

    public FourmCommentPostModel() {
    }
}
