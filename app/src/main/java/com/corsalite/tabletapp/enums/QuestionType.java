package com.corsalite.tabletapp.enums;

/**
 * Created on 23/01/16.
 *
 * @author Meeth D Jain
 */
public enum QuestionType {
    INVALID(0),
    SINGLE_SELECT_CHOICE(1),
    MULTI_SELECT_CHOICE(2),
    NUMERIC(3),
    GRID(4),
    FILL_IN_THE_BLANK(5),
    N_BLANK_SINGLE_SELECT(6),
    N_BLANK_MULTI_SELECT(7),
    ALPHANUMERIC(8),
    FRACTION(9),
    WORD_PROPERTIES(10),
    PICK_A_SENTENCE(11);

    private int type;

    QuestionType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public static QuestionType getQuestionType(String value) {
        try {
            int valueInt = Integer.parseInt(value);
            return getQuestionType(valueInt);
        } catch (Exception e) {
            return INVALID;
        }
    }

    public static QuestionType getQuestionType(int value) {
        switch (value) {
            case 1 :
                return SINGLE_SELECT_CHOICE;
            case 2 :
                return MULTI_SELECT_CHOICE;
            case 3 :
                return NUMERIC;
            case 4 :
                return GRID;
            case 5 :
                return FILL_IN_THE_BLANK;
            case 6 :
                return N_BLANK_SINGLE_SELECT;
            case 7 :
                return N_BLANK_MULTI_SELECT;
            case 8 :
                return ALPHANUMERIC;
            case 9 :
                return FRACTION;
            case 10 :
                return WORD_PROPERTIES;
            case 11 :
                return PICK_A_SENTENCE;
            default : {
                return INVALID;
            }
        }
    }
}
