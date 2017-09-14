package ru.courier.office.core;

/**
 * Created by rash on 31.08.2017.
 */

public enum ScanStatus {
    None(0),
    Progress(1),
    Completed(2);

    private final int value;

    private ScanStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ScanStatus fromInt(int value) {
        for (ScanStatus status : ScanStatus.values()) {
            int statusValue = status.getValue();
            if (statusValue == value)
            {
                return status;
            }
        }
        return ScanStatus.None;//For values out of enum scope
    }
}

