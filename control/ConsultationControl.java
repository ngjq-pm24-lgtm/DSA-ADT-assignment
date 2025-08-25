package control;

import ADT.ListInterface;
import ADT.MapInterface;
import ADT.QueueInterface;
import Entity.Consultation;
import Entity.Patient;
import Entity.Doctor;

import java.util.Comparator;
import java.util.Objects;

public class ConsultationControl {
    private final QueueInterface<Consultation> consultationQueue;
    private final ListInterface<Consultation> consultationList;
    private final MapInterface<String, Consultation> consultationMap;

    // Simulated patient and doctor lookup systems (you'll replace these with actual module calls)
    private final ListInterface<Patient> patientList;
    private final ListInterface<Doctor> doctorList;

    public ConsultationControl(QueueInterface<Consultation> queue, 
                             ListInterface<Consultation> list,
                             MapInterface<String, Consultation> map,
                             ListInterface<Patient> patientList, 
                             ListInterface<Doctor> doctorList) {
        this.consultationQueue = queue;
        this.consultationList = list;
        this.consultationMap = map;
        this.patientList = patientList;
        this.doctorList = doctorList;
    }

    // Create consultation using full Patient and Doctor objects from the shared ADT
    public Consultation createConsultation(String patientId, String doctorId,
                                           String date, String time, String reason) {
        Patient patient = findPatientById(patientId);
        Doctor doctor = findDoctorById(doctorId);

        if (patient == null || doctor == null) {
            System.out.println("Error: Patient or Doctor not found.");
            return null;
        }

        return new Consultation(patient, doctor, date, time, reason,
                "", "", ""); // diagnosis, prescription, notes default empty
    }

    // Add consultation to queue, list, and map
    public boolean addConsultation(Consultation consultation) {
        if (consultation == null || consultationMap.containsKey(consultation.getConsultationID())) {
            return false;
        }
        consultationQueue.enqueue(consultation);
        consultationList.add(consultation);
        consultationMap.put(consultation.getConsultationID(), consultation);
        return true;
    }

    public void displayAllConsultationRecords() {
        if (consultationList.isEmpty()) {
            System.out.println("No consultation records found.");
            return;
        }

        System.out.println("=== All Consultation Records ===");
        System.out.println(String.format("%-8s %-10s %-15s %-15s %-12s %-8s %-20s", 
            "ID", "Status", "Patient", "Doctor", "Date", "Time", "Reason"));
        System.out.println("-".repeat(90));
        
        for (int i = 0; i < consultationList.size(); i++) {
            Consultation c = consultationList.get(i);
            System.out.println(String.format("%-8s %-10s %-15s %-15s %-12s %-8s %-20s",
                c.getConsultationID().substring(0, 6) + "...",
                c.getStatus(),
                c.getPatient().getName().substring(0, Math.min(12, c.getPatient().getName().length())),
                c.getDoctor().getName().substring(0, Math.min(12, c.getDoctor().getName().length())),
                c.getDate(),
                c.getTime(),
                c.getReason().substring(0, Math.min(17, c.getReason().length())) + 
                    (c.getReason().length() > 17 ? "..." : "")
            ));
        }
        
        // Display counts
        System.out.println("\nSummary: " + getScheduledConsultations().size() + " scheduled, " +
                          getCompletedConsultations().size() + " completed, " +
                          getCancelledConsultations().size() + " cancelled");
    }

    public Consultation attendNextConsultation() {
        Consultation next = consultationQueue.dequeue();
        if (next != null) {
            next.setStatus(Consultation.Status.IN_PROGRESS);
            // Move to completed list after processing
            next.setStatus(Consultation.Status.COMPLETED);
        }
        return next;
    }

    public Consultation peekNextConsultation() {
        return consultationQueue.peek();
    }

    public Consultation getConsultationByIndex(int index) {
        if (index < 0 || index >= consultationList.size()) return null;
        return consultationList.get(index);
    }

    public boolean isValidConsultationIndex(int index) {
        return index >= 0 && index < consultationList.size();
    }

    public int getConsultationCount() {
        return consultationList.size();
    }
    
    /**
     * Cancels and removes a consultation by its index
     * @param index The index of the consultation to cancel
     * @return The cancelled Consultation object, or null if index is invalid
     */
    public boolean cancelConsultation(String consultationId) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation != null && consultation.isScheduled()) {
            consultation.setStatus(Consultation.Status.CANCELLED);
            // Remove from queue if present
            removeFromQueue(consultation);
            return true;
        }
        return false;
    }
    
    private void removeFromQueue(Consultation consultation) {
        // Create a temporary queue to hold consultations
        QueueInterface<Consultation> tempQueue = new MyQueue<>();
        boolean found = false;
        
        // Move all consultations to temp queue except the one to remove
        while (!consultationQueue.isEmpty()) {
            Consultation c = consultationQueue.dequeue();
            if (!c.getConsultationID().equals(consultation.getConsultationID())) {
                tempQueue.enqueue(c);
            } else {
                found = true;
            }
        }
        
        // Move everything back to the original queue
        while (!tempQueue.isEmpty()) {
            consultationQueue.enqueue(tempQueue.dequeue());
        }
    }
    
    /**
     * Generates a report of all consultations
     */
    public void generateAllConsultationsReport() {
        System.out.println("\n=== All Consultations Report ===");
        System.out.println(String.format("%-8s %-15s %-15s %-12s %-8s %-20s", 
            "ID", "Patient", "Doctor", "Date", "Time", "Reason"));
        System.out.println("-".repeat(80));
        
        for (int i = 0; i < consultationList.size(); i++) {
            Consultation c = consultationList.get(i);
            System.out.println(String.format("%-8s %-15s %-15s %-12s %-8s %-20s",
                c.getConsultationID().substring(0, 6) + "...",
                c.getPatient().getName().substring(0, Math.min(12, c.getPatient().getName().length())),
                c.getDoctor().getName().substring(0, Math.min(12, c.getDoctor().getName().length())),
                c.getDate(),
                c.getTime(),
                c.getReason().substring(0, Math.min(17, c.getReason().length())) + (c.getReason().length() > 17 ? "..." : "")
            ));
        }
        
        System.out.println("\nTotal Consultations: " + consultationList.size());
    }
    
    /**
     * Generates a report of consultations within a date range
     * @param startDate Start date in YYYY-MM-DD format
     * @param endDate End date in YYYY-MM-DD format
     */
    public void generateConsultationsByDateRangeReport(String startDate, String endDate) {
        System.out.println("\n=== Consultations Report " + startDate + " to " + endDate + " ===");
        System.out.println(String.format("%-8s %-15s %-15s %-12s %-8s %-20s", 
            "ID", "Patient", "Doctor", "Date", "Time", "Reason"));
        System.out.println("-".repeat(80));
        
        int count = 0;
        for (int i = 0; i < consultationList.size(); i++) {
            Consultation c = consultationList.get(i);
            if (c.getDate().compareTo(startDate) >= 0 && c.getDate().compareTo(endDate) <= 0) {
                System.out.println(String.format("%-8s %-15s %-15s %-12s %-8s %-20s",
                    c.getConsultationID().substring(0, 6) + "...",
                    c.getPatient().getName().substring(0, Math.min(12, c.getPatient().getName().length())),
                    c.getDoctor().getName().substring(0, Math.min(12, c.getDoctor().getName().length())),
                    c.getDate(),
                    c.getTime(),
                    c.getReason().substring(0, Math.min(17, c.getReason().length())) + (c.getReason().length() > 17 ? "..." : "")
                ));
                count++;
            }
        }
        
        System.out.println("\nTotal Consultations in Date Range: " + count);
    }
    
    /**
     * Generates a report of consultations for a specific doctor
     * @param doctorId ID of the doctor
     */
    public void generateConsultationsByDoctorReport(String doctorId) {
        Doctor doctor = findDoctorById(doctorId);
        if (doctor == null) {
            System.out.println("Doctor not found with ID: " + doctorId);
            return;
        }
        
        System.out.println("\n=== Consultations Report for Dr. " + doctor.getName() + " ===");
        System.out.println(String.format("%-8s %-15s %-12s %-8s %-20s", 
            "ID", "Patient", "Date", "Time", "Reason"));
        System.out.println("-".repeat(70));
        
        int count = 0;
        for (int i = 0; i < consultationList.size(); i++) {
            Consultation c = consultationList.get(i);
            if (c.getDoctor().getDoctorId().equals(doctorId)) {
                System.out.println(String.format("%-8s %-15s %-12s %-8s %-20s",
                    c.getConsultationID().substring(0, 6) + "...",
                    c.getPatient().getName().substring(0, Math.min(12, c.getPatient().getName().length())),
                    c.getDate(),
                    c.getTime(),
                    c.getReason().substring(0, Math.min(17, c.getReason().length())) + (c.getReason().length() > 17 ? "..." : "")
                ));
                count++;
            }
        }
        
        System.out.println("\nTotal Consultations for Dr. " + doctor.getName() + ": " + count);
    }

    public void insertFollowUp(int index, Consultation followUp) {
        if (index >= 0 && index <= consultationList.size()) {
            consultationList.add(index, followUp);
            consultationMap.put(followUp.getConsultationID(), followUp);
        }
    }
    
    public Consultation searchConsultationByID(String id) {
        return consultationMap.get(id);
    }
    
    public ListInterface<Consultation> getScheduledConsultations() {
        return filterConsultationsByStatus(Consultation.Status.SCHEDULED);
    }
    
    public ListInterface<Consultation> getCompletedConsultations() {
        return filterConsultationsByStatus(Consultation.Status.COMPLETED);
    }
    
    public ListInterface<Consultation> getCancelledConsultations() {
        return filterConsultationsByStatus(Consultation.Status.CANCELLED);
    }
    
    private ListInterface<Consultation> filterConsultationsByStatus(Consultation.Status status) {
        ListInterface<Consultation> result = new MyList<>();
        for (int i = 0; i < consultationList.size(); i++) {
            Consultation c = consultationList.get(i);
            if (c.getStatus() == status) {
                result.add(c);
            }
        }
        return result;
    }
    
    public void sortConsultationsByDate(boolean ascending) {
        consultationList.sort((c1, c2) -> {
            int dateCompare = c1.getDate().compareTo(c2.getDate());
            if (dateCompare == 0) {
                return c1.getTime().compareTo(c2.getTime());
            }
            return ascending ? dateCompare : -dateCompare;
        });
    }
    
    public void updateConsultationStatus(String consultationId, Consultation.Status newStatus) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation != null) {
            consultation.setStatus(newStatus);
            
            // If moving to completed, remove from queue if present
            if (newStatus == Consultation.Status.COMPLETED) {
                removeFromQueue(consultation);
            }
        }
    }

    private Patient findPatientById(String patientId) {
        for (int i = 0; i < patientList.size(); i++) {
            Patient p = patientList.get(i);
            if (p.getPatientId().equals(patientId)) {
                return p;
            }
        }
        return null;
    }

    private Doctor findDoctorById(String doctorId) {
        for (int i = 0; i < doctorList.size(); i++) {
            Doctor d = doctorList.get(i);
            if (d.getDoctorId().equals(doctorId)) {
                return d;
            }
        }
        return null;
    }
}
