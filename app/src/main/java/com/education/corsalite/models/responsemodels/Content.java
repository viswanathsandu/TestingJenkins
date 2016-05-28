package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Girish on 30/09/15.
 */
public class Content extends BaseModel implements Serializable {

    public String idContent;
    public String idEntity;
    @SerializedName("Type")
    public String type;
    @SerializedName("Name")
    public String name;
    @SerializedName("Url")
    public String url;
    @SerializedName("ContentHtml")
    public String contentHtml;
    @SerializedName("Status")
    public String status;
    @SerializedName("UpdateTime")
    public String updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Content content = (Content) o;

        if (idContent != null ? !idContent.equals(content.idContent) : content.idContent != null)
            return false;
        if (idEntity != null ? !idEntity.equals(content.idEntity) : content.idEntity != null)
            return false;
        if (type != null ? !type.equals(content.type) : content.type != null) return false;
        if (name != null ? !name.equals(content.name) : content.name != null) return false;
        if (url != null ? !url.equals(content.url) : content.url != null) return false;
        if (contentHtml != null ? !contentHtml.equals(content.contentHtml) : content.contentHtml != null)
            return false;
        if (status != null ? !status.equals(content.status) : content.status != null) return false;
        return !(updateTime != null ? !updateTime.equals(content.updateTime) : content.updateTime != null);

    }

    @Override
    public int hashCode() {
        int result = idContent != null ? idContent.hashCode() : 0;
        result = 31 * result + (idEntity != null ? idEntity.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (contentHtml != null ? contentHtml.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Content{" +
                "idContent=" + idContent +
                ", idEntity=" + idEntity +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", contentHtml='" + contentHtml + '\'' +
                ", status='" + status + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}