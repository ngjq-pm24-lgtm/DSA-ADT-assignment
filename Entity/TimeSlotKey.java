package Entity;

import enums.TimeSlot;
import java.io.Serializable;
import java.util.Objects;

public class TimeSlotKey implements Serializable {

    private final TimeSlot timeslot;

    public TimeSlotKey(TimeSlot timeslot) {
        this.timeslot = timeslot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeslot.getDay().toLowerCase(), timeslot.getHour());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        
        if (!(o instanceof TimeSlotKey)) return false;
        
        TimeSlotKey other = (TimeSlotKey) o;
        return timeslot.getDay().equals(other.timeslot.getDay())
                && timeslot.getHour() == other.timeslot.getHour();
    }

    public TimeSlot getTimeslot() {
        return timeslot;
    }
    
    public static TimeSlotKey[] values() {
        TimeSlot[] slots = TimeSlot.values();
        TimeSlotKey[] keys = new TimeSlotKey[slots.length];
        for (int i = 0; i < slots.length; i++) {
            keys[i] = new TimeSlotKey(slots[i]);
        }
        return keys;
    }
    
    public static TimeSlotKey convertTimeslotToKey(TimeSlot timeslot){
        return new TimeSlotKey(timeslot);
    }
    
    @Override
    public String toString() {
        return timeslot.toString();
    }
    
    public int compareTo(String day){
        int thisDayOrder = getDayOrder(this.timeslot.getDay());
        int otherDayOrder = getDayOrder(day);
    
        return Integer.compare(thisDayOrder, otherDayOrder);
}
    
    public int compareTo(TimeSlotKey other) {
        int thisDayOrder = getDayOrder(this.timeslot.getDay());
        int otherDayOrder = getDayOrder(other.timeslot.getDay());

        return Integer.compare(thisDayOrder, otherDayOrder);
    }
    
    private int getDayOrder(String day){
        switch(day.toLowerCase()){
            case "monday": return 1;
            case "tuesday": return 2;
            case "wednesday": return 3;
            case "thursday": return 4;
            case "friday": return 5;
            case "saturday": return 6;
            case "sunday": return 7;
            default: return -1;
        }
    }
    
}
