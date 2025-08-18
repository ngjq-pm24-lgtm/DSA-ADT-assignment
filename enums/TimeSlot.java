package enums;

import java.io.Serializable;
import java.util.Objects;


public enum TimeSlot implements Serializable {
    MONDAY_08_00("Monday", 8),
    MONDAY_10_00("Monday", 10),
    MONDAY_12_00("Monday", 12),
    MONDAY_14_00("Monday", 14),
    MONDAY_16_00("Monday", 16),
    MONDAY_18_00("Monday", 18),

    TUESDAY_08_00("Tuesday", 8),
    TUESDAY_10_00("Tuesday", 10),
    TUESDAY_12_00("Tuesday", 12),
    TUESDAY_14_00("Tuesday", 14),
    TUESDAY_16_00("Tuesday", 16),
    TUESDAY_18_00("Tuesday", 18),

    WEDNESDAY_08_00("Wednesday", 8),
    WEDNESDAY_10_00("Wednesday", 10),
    WEDNESDAY_12_00("Wednesday", 12),
    WEDNESDAY_14_00("Wednesday", 14),
    WEDNESDAY_16_00("Wednesday", 16),
    WEDNESDAY_18_00("Wednesday", 18),

    THURSDAY_08_00("Thursday", 8),
    THURSDAY_10_00("Thursday", 10),
    THURSDAY_12_00("Thursday", 12),
    THURSDAY_14_00("Thursday", 14),
    THURSDAY_16_00("Thursday", 16),
    THURSDAY_18_00("Thursday", 18),

    FRIDAY_08_00("Friday", 8),
    FRIDAY_10_00("Friday", 10),
    FRIDAY_12_00("Friday", 12),
    FRIDAY_14_00("Friday", 14),
    FRIDAY_16_00("Friday", 16),
    FRIDAY_18_00("Friday", 18),

    SATURDAY_08_00("Saturday", 8),
    SATURDAY_10_00("Saturday", 10),
    SATURDAY_12_00("Saturday", 12),
    SATURDAY_14_00("Saturday", 14),
    SATURDAY_16_00("Saturday", 16),
    SATURDAY_18_00("Saturday", 18),

    SUNDAY_08_00("Sunday", 8),
    SUNDAY_10_00("Sunday", 10),
    SUNDAY_12_00("Sunday", 12),
    SUNDAY_14_00("Sunday", 14),
    SUNDAY_16_00("Sunday", 16),
    SUNDAY_18_00("Sunday", 18);

    private final String day;
    private final int hour;

    TimeSlot(String day, int hour) {
        this.day = day;
        this.hour = hour;
    }

    public String getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public String toString() {
        return String.format("%s %02d:00", day, hour);
    }

    public static TimeSlot fromString(String day, int hour) {
        for (TimeSlot t : values()) {
            if (t.day.equalsIgnoreCase(day) && t.hour == hour) {
                return t;
            }
        }
        return null;
    }
    
    
}

