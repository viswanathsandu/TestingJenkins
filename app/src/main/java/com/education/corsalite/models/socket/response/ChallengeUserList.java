package com.education.corsalite.models.socket.response;

import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vissu on 4/3/16.
 */
public class ChallengeUserList {
    public String event;
    public Set<String> users;

    public void setUsers(String usersTxt) {
        users = new HashSet<>();
        if (!TextUtils.isEmpty(usersTxt)) {
            String[] usersArray = usersTxt.split(",");
            for (int i = 0; i < usersArray.length; i++) {
                if (!usersArray[i].trim().isEmpty()) {
                    users.add(usersArray[i]);
                }
            }
        }
    }
}
