package com.corsalite.tabletapp.models.requestmodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 10/15/15.
 */
public class Note {

    @SerializedName("idTopic")
    public String topicId;
    @SerializedName("idContent")
    public String contentId;
    @SerializedName("idContentStartPosition")
    public String idContentStartPosition;
    @SerializedName("idContentEndPosition")
    public String idContentEndPosition;
    @SerializedName("NoteHtml")
    public String noteHtml;

    public Note() {
    }

    public Note(String topicId, String contentId, String noteHtml) {
        this.topicId = topicId;
        this.contentId = contentId;
        this.noteHtml = noteHtml;
    }

}
