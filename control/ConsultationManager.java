package control;

import ADT.QueueInterface;
import ADT.ListInterface;
import ADT.MapInterface;
import ADT.MyList;
import ADT.HashMap;
import ADT.ArrayQueue;
import Entity.Consultation;
import Entity.Patient;
import Entity.Doctor;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class ConsultationManager {
    // ADTs for managing consultations
    private final QueueInterface<Consultation> consultationQueue; // For upcoming consultations
    private final ListInterface<Consultation> pastConsultations;  // For completed consultations
    private final MapInterface<String, Consultation> consultationMap; // For O(1) lookup by ID
    
    // Reference to patient and doctor lists (injected via constructor)
    private final ListInterface<Patient> patientList;
    private final ListInterface<Doctor> doctorList;
    
    // For date formatting
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Constructor for ConsultationManager
     * @param patientList List of all patients
     * @param doctorList List of all doctors
     */
    public ConsultationManager(ListInterface<Patient> patientList, ListInterface<Doctor> doctorList) {
        this.consultationQueue = new ArrayQueue<>(30);
        this.pastConsultations = new MyList<>();
        this.consultationMap = new HashMap<>();
        this.patientList = patientList;
        this.doctorList = doctorList;
    }
    
    // =================== Queue Operations ===================
    
    /**
     * Add a new consultation to the queue
     * @param consultation The consultation to add
     * @return true if added successfully, false otherwise
     */
    public boolean addConsultation(Consultation consultation) {
        if (consultation == null || consultation.getStatus() != Consultation.Status.SCHEDULED) {
            return false;
        }
        
        // Add to queue and map
        consultationQueue.enqueue(consultation);
        consultationMap.add(consultation.getConsultationID(), consultation);
        return true;
    }
    
    /**
     * View all upcoming consultations in the queue
     * @return List of upcoming consultations
     */
    public ListInterface<Consultation> viewUpcomingConsultations() {
        // Create a copy of the queue to avoid modifying it
        QueueInterface<Consultation> tempQueue = new ArrayQueue<>(30);
        ListInterface<Consultation> result = new MyList<>();
        
        // Dequeue and enqueue to view all elements
        while (!consultationQueue.isEmpty()) {
            Consultation c = consultationQueue.dequeue();
            result.add(c);
            tempQueue.enqueue(c);
        }
        
        // Restore the original queue
        while (!tempQueue.isEmpty()) {
            consultationQueue.enqueue(tempQueue.dequeue());
        }
        
        return result;
    }
    
    /**
     * Process the next consultation in the queue
     * @return The processed consultation, or null if queue is empty
     */
    public Consultation attendNextConsultation() {
        if (consultationQueue.isEmpty()) {
            return null;
        }
        
        Consultation next = consultationQueue.dequeue();
        next.setStatus(Consultation.Status.COMPLETED);
        pastConsultations.add(next);
        return next;
    }
    
    // =================== List Operations ===================
    
    /**
     * View all past consultations
     * @return List of past consultations
     */
    public ListInterface<Consultation> viewPastConsultations() {
        return pastConsultations;
    }
    
    /**
     * Sort past consultations by date
     * @param ascending true for ascending order, false for descending
     */
    public void sortPastConsultationsByDate(boolean ascending) {
        pastConsultations.sort((c1, c2) -> {
            try {
                LocalDate date1 = LocalDate.parse(c1.getDate(), DATE_FORMAT);
                LocalDate date2 = LocalDate.parse(c2.getDate(), DATE_FORMAT);
                return ascending ? date1.compareTo(date2) : date2.compareTo(date1);
            } catch (DateTimeParseException e) {
                return 0; // In case of parsing error, consider them equal
            }
        });
    }
    
    // =================== Map Operations ===================
    
    /**
     * Search for a consultation by ID
     * @param id The consultation ID to search for
     * @return The found consultation, or null if not found
     */
    public Consultation searchConsultationByID(String id) {
        return consultationMap.get(id);
    }
    
    // =================== Status Management ===================
    
    /**
     * Update the status of a consultation
     * @param consultationId The ID of the consultation to update
     * @param newStatus The new status to set
     * @return true if updated successfully, false otherwise
     */
    public boolean updateConsultationStatus(String consultationId, Consultation.Status newStatus) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation == null) {
            return false;
        }
        
        // Handle status transitions
        switch (newStatus) {
            case CANCELLED:
                // Remove from queue if it's there and add to past consultations
                if (removeFromQueue(consultationId)) {
                    pastConsultations.add(consultation);
                }
                break;
                
            case COMPLETED:
                // If it's in the queue, remove it and add to past consultations
                if (removeFromQueue(consultationId)) {
                    pastConsultations.add(consultation);
                }
                break;
                
            case SCHEDULED:
                // If it's not in the queue, add it
                if (!isInQueue(consultationId)) {
                    consultationQueue.enqueue(consultation);
                }
                break;
        }
        
        consultation.setStatus(newStatus);
        return true;
    }
    
    // =================== Reporting ===================
    
    /**
     * Generate a consultation report
     * @return A string containing the report
     */
    public String generateConsultationReport() {
        int total = consultationQueue.size() + pastConsultations.size();
        int scheduled = consultationQueue.size();
        int completed = 0;
        int cancelled = 0;
        
        // Count completed and cancelled consultations
        for (int i = 0; i < pastConsultations.size(); i++) {
            Consultation c = pastConsultations.get(i);
            if (c.getStatus() == Consultation.Status.COMPLETED) {
                completed++;
            } else if (c.getStatus() == Consultation.Status.CANCELLED) {
                cancelled++;
            }
        }
        
        return String.format(
            "=== Consultation Report ===\n" +
            "Total Consultations: %d\n" +
            "Scheduled: %d\n" +
            "Completed: %d\n" +
            "Cancelled: %d",
            total, scheduled, completed, cancelled
        );
    }
    
    // =================== Helper Methods ===================
    
    /**
     * Remove a consultation from the queue by ID
     * @param consultationId The ID of the consultation to remove
     * @return true if removed, false if not found
     */
    private boolean removeFromQueue(String consultationId) {
        QueueInterface<Consultation> tempQueue = new ArrayQueue<>(30);
        boolean found = false;
        
        // Look for the consultation in the queue
        while (!consultationQueue.isEmpty()) {
            Consultation c = consultationQueue.dequeue();
            if (c.getConsultationID().equals(consultationId)) {
                found = true;
            } else {
                tempQueue.enqueue(c);
            }
        }
        
        // Restore the queue without the removed item
        while (!tempQueue.isEmpty()) {
            consultationQueue.enqueue(tempQueue.dequeue());
        }
        
        return found;
    }
    
    /**
     * Check if a consultation is in the queue
     * @param consultationId The ID to check
     * @return true if found in queue, false otherwise
     */
    private boolean isInQueue(String consultationId) {
        QueueInterface<Consultation> tempQueue = new ArrayQueue<>(30);
        boolean found = false;
        
        // Check each consultation in the queue
        while (!consultationQueue.isEmpty()) {
            Consultation c = consultationQueue.dequeue();
            if (c.getConsultationID().equals(consultationId)) {
                found = true;
            }
            tempQueue.enqueue(c);
        }
        
        // Restore the queue
        while (!tempQueue.isEmpty()) {
            consultationQueue.enqueue(tempQueue.dequeue());
        }
        
        return found;
    }
    
    // =================== Getters ===================
    
    public QueueInterface<Consultation> getConsultationQueue() {
        return consultationQueue;
    }
    
    public ListInterface<Consultation> getPastConsultations() {
        return pastConsultations;
    }
    
    public MapInterface<String, Consultation> getConsultationMap() {
        return consultationMap;
    }
}
