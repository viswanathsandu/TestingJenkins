package com.education.corsalite.enums;

/**
 * Created by vissu on 9/17/15.
 */
public enum LoggerMode {
    SILENT("silent"),
    DEVELOPMENT ("development"),
    QA("qa"),
    RELEASE("release");

    private String value;

    private LoggerMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
