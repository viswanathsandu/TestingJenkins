package com.corsalite.tabletapp.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 10/25/15.
 */
public class Note extends BaseResponseModel {
    @SerializedName("idSubject")
    public String studentId;
    @SerializedName("Subject")
    public String subject;
    @SerializedName("idChapter")
    public String chapterId;
    @SerializedName("Chapter")
    public String chapter;
    @SerializedName("idTopic")
    public String topicId;
    @SerializedName("Topic")
    public String topic;
    @SerializedName("idContent")
    public String contentId;
    @SerializedName("Content")
    public String content;
    @SerializedName("idNotes")
    public String idNotes;
    @SerializedName("NoteHtml")
    public String noteHtml;

    public Note() {
    }
}
