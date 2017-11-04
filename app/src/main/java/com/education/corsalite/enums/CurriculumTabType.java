package com.education.corsalite.enums;

/**
 * Created by vissu on 9/17/15.
 */
public enum CurriculumTabType {
    TODO("ToDo"),
    UPCOMING("Upcoming"),
    COMPLETED("Completed");

    private String value;

    private CurriculumTabType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
