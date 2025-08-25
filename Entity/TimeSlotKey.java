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
    
    
}
