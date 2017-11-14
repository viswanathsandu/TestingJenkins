package com.education.corsalite.enums;

/**
 * Created by vissu on 9/17/15.
 */
public enum TestanswerPaperState {
    STARTED("started"),
    SUSPENDED("Suspended"),
    COMPLETED("Completed");

    private String value;

    private TestanswerPaperState(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
