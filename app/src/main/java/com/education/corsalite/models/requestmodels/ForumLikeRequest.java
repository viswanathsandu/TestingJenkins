package com.education.corsalite.models.requestmodels;

/**
 * Created by sridharnalam on 1/24/16.
 */
public class ForumLikeRequest
{
    private String idUserPost;

    private String idUser;

    public ForumLikeRequest() {
    }

    public ForumLikeRequest(String idUser, String idUserPost) {
        this.idUser = idUser;
        this.idUserPost=idUserPost;
    }

    public String getIdUserPost ()
    {
        return idUserPost;
    }

    public void setIdUserPost (String idUserPost)
    {
        this.idUserPost = idUserPost;
    }

    public String getIdUser ()
    {
        return idUser;
    }

    public void setIdUser (String idUser)
    {
        this.idUser = idUser;
    }

}

