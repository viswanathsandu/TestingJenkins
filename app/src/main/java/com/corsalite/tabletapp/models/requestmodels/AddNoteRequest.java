package com.corsalite.tabletapp.models.requestmodels;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vissu on 10/15/15.
 */
public class AddNoteRequest {

    @SerializedName("UpdateTime")
    public String updateTime;
    @SerializedName("idStudent")
    public String studentId;
    @Ignore
    @SerializedName("Notes")
    public List<Note> notes;

    public AddNoteRequest() {
    }

    public AddNoteRequest(String studentId, List<Note> notes) {
        this.updateTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.studentId = studentId;
        this.notes = notes;
    }

    public AddNoteRequest(String studentId, Note note) {
        this.updateTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.studentId = studentId;
        this.notes = new ArrayList<>();
        notes.add(note);
    }

}
