package com.corsalite.tabletapp.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ayush on 9/12/15.
 */
public class Notes extends BaseModel {

    @SerializedName("idSubject")
    public String subjectId;
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
    public String notesId;
    @SerializedName("NoteHtml")
    public String noteHtml;
}
