package Entity;

public class Consultation {
    public enum Status {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
    
    private Status status;
    private String consultationID;
    private Patient patient;
    private Doctor doctor;
    private String date;
    private String time;
    private String reason;
    private String diagnosis;
    private String prescription;
    private String notes;

    public Consultation(String consultationID, Patient patient, Doctor doctor,
                       String date, String time, String reason,
                       String diagnosis, String prescription, String notes) {
        this(consultationID, patient, doctor, date, time, reason, diagnosis, prescription, notes, Status.SCHEDULED);
    }
    
    public Consultation(String consultationID, Patient patient, Doctor doctor,
                       String date, String time, String reason,
                       String diagnosis, String prescription, String notes, Status status) {
        this.consultationID = consultationID;
        this.patient = patient;
        this.doctor = doctor;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.notes = notes;
        this.status = status;
    }

    // Getters & Setters
    public String getConsultationID() { return consultationID; }
    public void setConsultationID(String id) { this.consultationID = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // Status management
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public boolean isScheduled() { return status == Status.SCHEDULED; }
    public boolean isInProgress() { return status == Status.IN_PROGRESS; }
    public boolean isCompleted() { return status == Status.COMPLETED; }
    public boolean isCancelled() { return status == Status.CANCELLED; }
    
    @Override
    public String toString() {
        return String.format("Consultation[ID: %s, Status: %s, Patient: %s, Doctor: %s, Date: %s, Time: %s]",
                consultationID, status, patient.getName(), doctor.getName(), date, time);
    }
}
