package Entity;

import java.io.Serializable;

public class Appointment implements Serializable {
    private String patientId;       // Patient ID
    private String consultationType;
    private String reason;
    private String date;            // YYYYMMDD
    private String time;            // HHMM

    public Appointment(String patientId, String consultationType, String reason, String date, String time) {
        this.patientId = patientId;
        this.consultationType = consultationType;
        this.reason = reason;
        this.date = date;
        this.time = time;
    }

    // Getters
    public String getPatientId() {
        return patientId;
    }

    public String getConsultationType() {
        return consultationType;
    }

    public String getReason() {
        return reason;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    // Optional: toString() for printing nicely
    @Override
    public String toString() {
        return "Patient: " + patientId +
               "\nConsultation: " + consultationType +
               "\nReason: " + reason +
               "\nDate: " + date +
               "\nTime: " + time;
    }
}
