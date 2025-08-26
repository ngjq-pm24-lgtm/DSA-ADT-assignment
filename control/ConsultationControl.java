package control;

import ADT.ListInterface;
import ADT.MapInterface;
import ADT.MyQueue;
import ADT.MyList;
import ADT.QueueInterface;
import ADT.MyMap;
import Entity.Consultation;
import Entity.Patient;
import Entity.Doctor;
import dao.ConsultationDAO;
import exception.DataAccessException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsultationControl {
    private static final Logger LOGGER = Logger.getLogger(ConsultationControl.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private QueueInterface<Consultation> consultationQueue;
    private ListInterface<Consultation> consultationList;
    private MapInterface<String, Consultation> consultationMap;
    private final ListInterface<Patient> patientList;
    private final ListInterface<Doctor> doctorList;
    private final ConsultationDAO<String, Consultation> consultationDAO;
    
    // File path for data persistence
    private static final String CONSULTATION_FILE = "data/consultations.dat";

    public ConsultationControl(QueueInterface<Consultation> queue, 
                             ListInterface<Consultation> list,
                             MapInterface<String, Consultation> map,
                             ListInterface<Patient> patientList, 
                             ListInterface<Doctor> doctorList) 
            throws DataAccessException {
        this.consultationQueue = queue != null ? queue : new MyQueue<>();
        this.consultationList = list != null ? list : new MyList<>();
        this.consultationMap = map != null ? map : new MyMap<>();
        this.patientList = patientList;
        this.doctorList = doctorList;
        this.consultationDAO = new ConsultationDAO<>();
        
        // Load data on initialization
        loadConsultations();
    }

    // Create consultation using full Patient and Doctor objects from the shared ADT
    public Consultation createConsultation(String patientId, String doctorId,
                                         String date, String time, String reason) 
            throws ValidationException, EntityNotFoundException, DataAccessException {
        try {
            // Input validation
            validateInput(patientId, "Patient ID");
            validateInput(doctorId, "Doctor ID");
            validateInput(date, "Date");
            validateInput(time, "Time");
            validateInput(reason, "Reason");
            
            // Validate date and time formats and check if date is in the future
            if (validateAndParseDate(date).isBefore(LocalDate.now())) {
                throw new ValidationException("Date", "Cannot schedule consultation in the past");
            }
            
            // Find entities
            Patient patient = findPatientById(patientId);
            if (patient == null) {
                throw new EntityNotFoundException("Patient", patientId);
            }
            
            Doctor doctor = findDoctorById(doctorId);
            if (doctor == null) {
                throw new EntityNotFoundException("Doctor", doctorId);
            }

            // Check for scheduling conflicts
            if (isDoctorScheduled(doctor, date, time)) {
                throw new ValidationException("Schedule", 
                    String.format("Dr. %s is already scheduled for a consultation at %s %s", 
                        doctor.getName(), date, time));
            }

            String newId = consultationDAO.generateConsultationId();
            Consultation consultation = new Consultation(newId, patient, doctor, date, time, reason,
                    "", "", ""); // diagnosis, prescription, notes default empty
            
            addConsultation(consultation);
            LOGGER.info(String.format("Created new consultation with ID: %s", newId));
            return consultation;
            
        } catch (DateTimeParseException e) {
            throw new ValidationException("Date/Time", "Invalid date or time format. Use YYYY-MM-DD for date and HH:MM for time.", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating consultation", e);
            throw new DataAccessException("Failed to create consultation", e);
        }
    }

    // Add consultation to queue, list, and map
    public void addConsultation(Consultation consultation) 
            throws ValidationException, DataAccessException {
        if (consultation == null) {
            throw new ValidationException("Consultation", "Consultation cannot be null");
        }
        if (consultationMap.containsKey(consultation.getConsultationID())) {
            throw new ValidationException("Consultation ID", 
                String.format("Consultation with ID %s already exists", 
                    consultation.getConsultationID()));
        }
        
        try {
            consultationQueue.enqueue(consultation);
            consultationList.add(consultation);
            consultationMap.put(consultation.getConsultationID(), consultation);
            saveConsultations();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding consultation", e);
            throw new DataAccessException("Failed to add consultation", e);
        }
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
        
        int scheduledCount = 0, completedCount = 0, cancelledCount = 0;
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

            // Tally counts in the same loop
            switch (c.getStatus()) {
                case SCHEDULED:
                    scheduledCount++;
                    break;
                case COMPLETED:
                    completedCount++;
                    break;
                case CANCELLED:
                    cancelledCount++;
                    break;
                default:
                    break;
            }
        }
        
        // Display counts
        System.out.println("\nSummary: " + scheduledCount + " scheduled, " +
                          completedCount + " completed, " +
                          cancelledCount + " cancelled");
    }

    public Consultation attendNextConsultation() throws DataAccessException {
        if (consultationQueue.isEmpty()) {
            return null;
        }
        
        try {
            Consultation next = consultationQueue.dequeue();
            if (next != null) {
                next.setStatus(Consultation.Status.COMPLETED);
                saveConsultations();
            }
            return next;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error attending next consultation", e);
            throw new DataAccessException("Failed to attend next consultation", e);
        }
    }

    public Consultation peekNextConsultation() throws DataAccessException {
        try {
            return consultationQueue.peek();
        } catch (Exception e) {
            throw new DataAccessException("Failed to peek next consultation", e);
        }
    }

    public Consultation getConsultationById(String consultationId) throws DataAccessException, ValidationException {
        if (consultationId == null || consultationId.trim().isEmpty()) {
            throw new ValidationException("Consultation ID", "cannot be null or empty");
        }
        try {
            return consultationMap.get(consultationId);
        } catch (Exception e) {
            throw new DataAccessException("Failed to get consultation by ID", e);
        }
    }

    public boolean isValidConsultationIndex(int index) {
        return index >= 0 && index < consultationList.size();
    }

    public int getTotalConsultations() throws DataAccessException {
        try {
            return consultationList.size();
        } catch (Exception e) {
            throw new DataAccessException("Failed to get total consultations", e);
        }
    }
    
    /**
     * Cancels and removes a consultation by its index
     * @param index The index of the consultation to cancel
     * @return The cancelled Consultation object, or null if index is invalid
     */
    public void cancelConsultation(String consultationId) 
            throws EntityNotFoundException, ValidationException, DataAccessException {
        validateInput(consultationId, "Consultation ID");
        
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation == null) {
            throw new EntityNotFoundException("Consultation", consultationId);
        }

        if (!consultation.isScheduled()) {
            throw new ValidationException("Consultation status", 
                String.format("Cannot cancel consultation with status: %s", consultation.getStatus()));
        }

        try {
            consultation.setStatus(Consultation.Status.CANCELLED);
            removeFromQueue(consultation);
            saveConsultations();
            LOGGER.info(String.format("Cancelled consultation with ID: %s", consultationId));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cancelling consultation", e);
            throw new DataAccessException("Failed to cancel consultation", e);
        }
    }
    
    private void removeFromQueue(Consultation consultation) {
        if (consultation == null) {
            return;
        }
        
        // Create a temporary queue to hold consultations
        QueueInterface<Consultation> tempQueue = new MyQueue<>();
        
        // Dequeue consultations until we find the one to remove
        while (!consultationQueue.isEmpty()) {
            Consultation c = consultationQueue.dequeue();
            if (!c.equals(consultation)) {
                tempQueue.enqueue(c);
            }
        }
        
        // Restore the queue
        while (!tempQueue.isEmpty()) {
            consultationQueue.enqueue(tempQueue.dequeue());
        }
    }
    
    /**
     * Saves all consultations to file
     * @return true if save was successful, false otherwise
     */
    private boolean saveConsultations() throws DataAccessException {
        try {
            boolean saved = consultationDAO.saveConsultations(consultationMap);
            return saved;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving consultations", e);
            throw new DataAccessException("Failed to save consultations", e);
        }
    }
    
    /**
     * Loads consultations from file
     */
    @SuppressWarnings("unchecked")
    private void loadConsultations() throws DataAccessException {
        try {
            MapInterface<String, Consultation> loadedConsultations = 
                (MapInterface<String, Consultation>) consultationDAO.loadConsultations();
            
            if (loadedConsultations != null && !loadedConsultations.isEmpty()) {
                // Clear existing data
                consultationMap = new MyMap<>();
                consultationList = new MyList<>();
                consultationQueue = new MyQueue<>();
                
                // This is a workaround since MapInterface doesn't support iteration
                // In a real implementation, consider adding an iterator to MapInterface
                for (int i = 0; i < loadedConsultations.size(); i++) {
                    Consultation c = loadedConsultations.get("" + i);
                    if (c != null) {
                        consultationMap.put(c.getConsultationID(), c);
                        consultationList.add(c);
                        
                        // Add to queue if it's scheduled
                        if (c.getStatus() == Consultation.Status.SCHEDULED) {
                            consultationQueue.enqueue(c);
                        }
                    }
                }
                LOGGER.info(String.format("Loaded %d consultations from file", consultationMap.size()));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading consultations", e);
            throw new DataAccessException("Failed to load consultations", e);
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
    public void generateConsultationsByDoctorReport(String doctorId) 
            throws EntityNotFoundException, ValidationException, DataAccessException {
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
            if (String.valueOf(c.getDoctor().getDoctorID()).equals(doctorId)) {
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

    public boolean insertFollowUp(int index, Consultation followUp) throws DataAccessException {
        if (index >= 0 && index <= consultationList.size()) {
            consultationList.add(index, followUp);
            consultationMap.put(followUp.getConsultationID(), followUp);
            return saveConsultations();
        }
        return false;
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
        saveConsultations();
    }
    
    public boolean updateConsultationStatus(String consultationId, Consultation.Status newStatus) {
        Consultation consultation = consultationMap.get(consultationId);
        if (consultation != null) {
            consultation.setStatus(newStatus);
            
            // If moving to completed, remove from queue if present
            if (newStatus == Consultation.Status.COMPLETED) {
                removeFromQueue(consultation);
            }
            
            // Save changes to file
            return saveConsultations();
        }
        return false;
    }

    // ====================
    // Validation Helpers
    // ====================
    
    private void validateInput(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, "cannot be empty");
        }
    }
    
    private LocalDate validateAndParseDate(String dateStr) throws ValidationException {
        try {
            return LocalDate.parse(dateStr, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Date", "must be in YYYY-MM-DD format", e);
        }
    }
    
    private LocalTime validateAndParseTime(String timeStr) throws ValidationException {
        try {
            return LocalTime.parse(timeStr, TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Time", "must be in HH:MM format", e);
        }
    }
    
    // ====================
    // Data Access Helpers
    // ====================
    
    private Patient findPatientById(String patientId) throws ValidationException, EntityNotFoundException {
        validateInput(patientId, "Patient ID");
        for (int i = 0; i < patientList.size(); i++) {
            Patient p = patientList.get(i);
            if (p.getPatientID().equals(patientId)) {
                return p;
            }
        }
        throw new EntityNotFoundException("Patient", patientId);
    }

    private Doctor findDoctorById(String doctorId) throws ValidationException, EntityNotFoundException {
        validateInput(doctorId, "Doctor ID");
        for (int i = 0; i < doctorList.size(); i++) {
            Doctor d = doctorList.get(i);
            if (String.valueOf(d.getDoctorID()).equals(doctorId)) {
                return d;
            }
        }
        throw new EntityNotFoundException("Doctor", doctorId);
    }

    private boolean isDoctorScheduled(Doctor doctor, String date, String time) 
            throws ValidationException {
        if (doctor == null) {
            throw new ValidationException("Doctor", "cannot be null");
        }
        validateInput(date, "Date");
        validateInput(time, "Time");
        
        for (int i = 0; i < consultationList.size(); i++) {
            Consultation c = consultationList.get(i);
            if (c.getDoctor().equals(doctor) && 
                c.getDate().equals(date) && 
                c.getTime().equals(time) && 
                c.isScheduled()) {
                return true; // Found a conflicting scheduled consultation
            }
        }
        return false;
    }
}