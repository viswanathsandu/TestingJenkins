package com.education.corsalite.db;

import android.content.Context;
import android.text.TextUtils;

import com.education.corsalite.models.db.ContentIndexResponse;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.utils.L;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vissu on 5/11/16.
 */
public class SugarDbManager {
    private static SugarDbManager instance;
    private Context context;

    private SugarDbManager() {
    }

    public static SugarDbManager get(Context context) {
        if (instance == null) {
            instance = new SugarDbManager();
        }
        instance.context = context;
        return instance;
    }

    public List<OfflineContent> getOfflineContents(String courseId) {
        List<OfflineContent> results = new ArrayList<>();
        try {
            List<OfflineContent> offlineContents = OfflineContent.listAll(OfflineContent.class);
            if (TextUtils.isEmpty(courseId)) {
                return offlineContents;
            }
            for (OfflineContent offlineContent : offlineContents) {
                if (offlineContent.courseId.equals(courseId)) {
                    results.add(offlineContent);
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return results;
    }

    public void saveOfflineContent(OfflineContent content) {
        try {
            List<OfflineContent> offlineContents = OfflineContent.listAll(OfflineContent.class);
            for (OfflineContent offlineContent : offlineContents) {
                if (offlineContent.equals(content)) {
                    offlineContent.delete();
                }
            }
            content.save();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void saveOfflineContents(List<OfflineContent> offlineContents) {
        for (OfflineContent content : offlineContents) {
            saveOfflineContent(content);
        }
    }

    public void deleteOfflineContent(OfflineContent offlineContent) {
        try {
            offlineContent.delete();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void deleteOfflineContents(List<OfflineContent> offlineContents) {
        for (OfflineContent content : offlineContents) {
            deleteOfflineContent(content);
        }
    }

    public ContentIndexResponse getContentIndexList(String courseId, String studentId) {
        Select specificAuthorQuery = Select.from(ContentIndexResponse.class)
                .where(Condition.prop("course_id").eq(courseId),
                        Condition.prop("student_id").eq(studentId))
                .limit("1");
        ContentIndexResponse contentIndexResponses = (ContentIndexResponse) specificAuthorQuery.first();
        return contentIndexResponses;
    }

    public void saveContentIndexList(String contentIndexJson, String courseId, String studentId) {
        Select specificAuthorQuery = Select.from(ContentIndexResponse.class)
                .where(Condition.prop("course_id").eq(courseId),
                        Condition.prop("student_id").eq(studentId))
                .limit("1");
        ContentIndexResponse contentIndexResponses = (ContentIndexResponse) specificAuthorQuery.first();
        if (contentIndexResponses != null) {
            contentIndexResponses.courseId = courseId;
            contentIndexResponses.studentId = studentId;
            contentIndexResponses.contentIndexesJson = contentIndexJson;
        } else {
            contentIndexResponses = new ContentIndexResponse(contentIndexJson, courseId, studentId);
        }
        contentIndexResponses.save();
    }

}
