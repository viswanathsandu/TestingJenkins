package com.corsalite.tabletapp.enums;

/**
 * Created on 23/01/16.
 *
 * @author Meeth D Jain
 */
public enum Tests {
    INVALID(0), SCHEDULED(1), CHAPTER(2), PART(3), MOCK(4);

    private int type;

    Tests(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public static Tests getTest(int testType) {
        switch (testType) {
            case 1 : {
                return SCHEDULED;
            }
            case 2 : {
                return CHAPTER;
            }
            case 3 : {
                return PART;
            }
            case 4 : {
                return MOCK;
            }
            default : {
                return INVALID;
            }
        }
    }
}
