package Entity;

public class Consultation {
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
        this.consultationID = consultationID;
        this.patient = patient;
        this.doctor = doctor;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.notes = notes;
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

    @Override
    public String toString() {
        return "Consultation ID: " + consultationID +
               "\nPatient: " + patient.getName() +
               "\nDoctor: " + doctor.getName() +
               "\nDate: " + date +
               "\nTime: " + time +
               "\nReason: " + reason +
               "\n----------------------------";
    }
}
