package com.corsalite.tabletapp.models.socket.response;

/**
 * Created by vissu on 4/1/16.
 */
public class LeaderBoardStudent {

    public String title;
    public String imageUrl;
    public String questionsAnswered;

    public LeaderBoardStudent(String title, String imageUrl, String questionsAnswered) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.questionsAnswered = questionsAnswered;
    }
}
