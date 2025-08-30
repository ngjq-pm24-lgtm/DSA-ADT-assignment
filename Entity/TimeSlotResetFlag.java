package Entity;


public class TimeSlotResetFlag implements java.io.Serializable {

    private String lastRunWeekday;
    
    public String getLastRunWeekday() {
        return lastRunWeekday;
    }
    
    public void setLastRunWeekday(String lastRunWeekday) {
        this.lastRunWeekday = lastRunWeekday;
    }
}
