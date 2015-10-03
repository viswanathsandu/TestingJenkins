package com.education.corsalite.models;

import com.education.corsalite.models.responsemodels.BaseModel;

/**
 * Created by mt0060 on 03/10/15.
 */
public class ContentModel extends BaseModel implements Comparable<ContentModel>{

    public String idContent;
    public String type;
    public String contentName;
    public String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContentModel that = (ContentModel) o;

        if (idContent != null ? !idContent.equals(that.idContent) : that.idContent != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (contentName != null ? !contentName.equals(that.contentName) : that.contentName != null)
            return false;
        return !(status != null ? !status.equals(that.status) : that.status != null);

    }

    @Override
    public int hashCode() {
        int result = idContent != null ? idContent.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (contentName != null ? contentName.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }


    @Override
    public int compareTo(ContentModel another) {
        return Integer.valueOf(this.idContent) - Integer.valueOf(another.idContent);
    }
}
