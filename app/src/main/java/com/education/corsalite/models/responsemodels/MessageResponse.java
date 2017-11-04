package com.education.corsalite.models.responsemodels;

import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by vissu on 9/12/15.
 */
public class MessageResponse extends BaseResponseModel {
    @Ignore
    public List<Message> messages;

    public MessageResponse() {
    }
}
