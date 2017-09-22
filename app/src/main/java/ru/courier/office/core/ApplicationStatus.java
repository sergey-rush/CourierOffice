package ru.courier.office.core;

/**
 * ApplicationStatus corresponds DeliveryStatus on server application
 */

public enum ApplicationStatus {
    None(0),
    Delivered(1),
    Rejected(2);

    private final int value;

    private ApplicationStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ApplicationStatus fromInt(int value) {
        for (ApplicationStatus status : ApplicationStatus.values()) {
            int statusValue = status.getValue();
            if (statusValue == value)
            {
                return status;
            }
        }
        return ApplicationStatus.None;//For values out of enum scope
    }
}
