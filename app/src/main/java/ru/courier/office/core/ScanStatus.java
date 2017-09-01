package ru.courier.office.core;

/**
 * Created by rash on 31.08.2017.
 */

public enum ScanStatus {
    None(0),
    Created(1),
    Progress(2),
    Downloaded(3);

    private final int value;

    private ScanStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ScanStatus fromInt(int value) {
        for (ScanStatus e : ScanStatus.values()) {
            if (e.getValue() == value)
                return e;
        }
        return ScanStatus.None;//For values out of enum scope
    }



}

