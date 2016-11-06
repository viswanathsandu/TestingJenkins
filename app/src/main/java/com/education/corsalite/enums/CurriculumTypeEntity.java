package com.education.corsalite.enums;

import android.text.TextUtils;

/**
 * Created by vissu on 9/17/15.
 */
public enum CurriculumTypeEntity {
    READING("Topic"),
    CUSTOM_EXERCISE("Custom Exercise"),
    PRACTIVE_TEST("Practice Test"),
    SCHEDULED_EXAM("Scheduled Exam"),
    REVISION_TEST("Revision Test"),
    SCHEDULED_TEST("Schedule Test");

    private String value;

    private CurriculumTypeEntity(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static CurriculumTypeEntity getCurriculumEntityType(String entityValue) {
        if(!TextUtils.isEmpty(entityValue)) {
            for (CurriculumTypeEntity entity : CurriculumTypeEntity.values()) {
                if (entity.toString().equalsIgnoreCase(entityValue)) {
                    return entity;
                }
            }
        }
        return null;
    }
}
