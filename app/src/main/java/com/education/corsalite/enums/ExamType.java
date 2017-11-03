package com.education.corsalite.enums;

/**
 * Created by vissu on 9/17/15.
 */
public enum ExamType {
    PRACTICE_TEST("Exercise Test"),
    TAKE_TEST("Take Test"),
    PART_TEST("Part Test"),
    MOCK_TEST("Mock Test"),
    SCHEDULED_TEST("Scheduled Test"),
    CHALLENGE_TEST("Challenge Test");

    private String value;

    private ExamType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
