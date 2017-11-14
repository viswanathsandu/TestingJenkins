package com.education.corsalite.models.requestmodels;

import com.education.corsalite.utils.TimeUtils;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;

/**
 * Created by vissu on 10/15/15.
 */
public class UpdateNoteRequest {

    @SerializedName("UpdateTime")
    public String updateTime;
    @SerializedName("idStudent")
    public String studentId;
    @SerializedName("idNotes")
    public String notesId;
    @SerializedName("NoteHtml")
    public String notesHtml;

    public UpdateNoteRequest() {
    }

    public UpdateNoteRequest(String studentId, String notesId, String notesHtml) {
        this.updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(TimeUtils.getCurrentDate());
        this.studentId = studentId;
        this.notesId = notesId;
        this.notesHtml = notesHtml;
    }
}
