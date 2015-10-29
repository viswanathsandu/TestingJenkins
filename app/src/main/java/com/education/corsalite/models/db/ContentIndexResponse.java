package com.education.corsalite.models.db;

import com.education.corsalite.models.responsemodels.BaseResponseModel;
import com.education.corsalite.models.responsemodels.ContentIndex;

import java.util.List;

/**
 * Created by Girish on 30/09/15.
 */
public class ContentIndexResponse extends BaseResponseModel {
    public List<ContentIndex> contentIndexes;

    public ContentIndexResponse() {
        super();
    }

    public ContentIndexResponse(List<ContentIndex> contentIndexes) {
        this.contentIndexes = contentIndexes;
    }
}
