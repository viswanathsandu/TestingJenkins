package com.education.corsalite.enums;

/**
 * Created by vissu on 9/17/15.
 */
public enum OfflineContentStatus {
    IN_PROGRESS ("In-Progress"),
    STARTED("Started"),
    WAITING("Waiting"),
    FAILED("Failed"),
    COMPLETED("Completed");

    private String value;

    private OfflineContentStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
