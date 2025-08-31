package boundary;

import ADT.ListInterface;
import ADT.MyList;
import control.ConsultationControl;
import Entity.Consultation;
import Entity.Doctor;
import Entity.TimeSlotKey;
import control.DoctorManager;
import dao.GenericDAO;
import exception.ValidationException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import java.util.Scanner;

public class ConsultationUI {
    private DoctorUI doctorUI = new DoctorUI();
    private DoctorManager doctorManager = new DoctorManager();
    private ConsultationControl consultationControl;
    private GenericDAO dao = new GenericDAO();
    private Scanner scanner = new Scanner(System.in);

    public ConsultationUI() {
        try{
            consultationControl = new ConsultationControl();
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    public void menu() {
        int choice;
        do {
            System.out.println("\n=== CONSULTATION MANAGEMENT MENU ===");
            System.out.println("1. Book New Consultation Appointment");
            System.out.println("2. View All Consultation Records");
            System.out.println("3. Add Follow-Up Appointment");
            System.out.println("4. Cancel Existing Appointment");
            System.out.println("5. Generate Consultation Report");
            System.out.println("6. Return to Main Menu");
            System.out.print("Enter choice: ");
            choice = getIntInput();

            switch (choice) {
                case 1:
                    bookNewConsultation();
                    break;
                case 2:
                    viewAllConsultationRecords();
                    break;
                case 3:
                    addFollowUpConsultation();
                    break;
                case 4:
                    cancelConsultation();
                    break;
                case 5:
                    generateConsultationReport();
                    break;
                case 6:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 6);
    }

    public void bookNewConsultation() {
        System.out.println("\n=== Book New Consultation Appointment ===");
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine();

        ListInterface<Doctor> availableDoctors;
        Doctor chosenDoctor = null;
        TimeSlotKey chosenTimeslot;
        do{
            chosenTimeslot = doctorUI.getTimeslotChoice();
            availableDoctors = DoctorManager.getAvailabilityTable().get(chosenTimeslot);
            if (availableDoctors == null || availableDoctors.isEmpty()) {
                System.out.println("No doctors available for this slot, please choose another.");
            }else{
                chosenDoctor = doctorUI.chooseDoctor(availableDoctors, chosenTimeslot);
            }
        }while(availableDoctors == null || availableDoctors.isEmpty());

        System.out.print("Enter Reason/Remarks: ");
        String reason = scanner.nextLine();

        // Create consultation object (requires Patient & Doctor objects fetched via control)
        try{
            Consultation consultation = consultationControl.createConsultation(patientId, chosenDoctor, chosenTimeslot, reason);
            consultationControl.addConsultation(consultation);
            DoctorManager.getAvailabilityTable().get(chosenTimeslot).remove(chosenDoctor);
            GenericDAO.saveToFile(DoctorManager.getAvailabilityTable(), DoctorManager.getAvailabilityTableFile());
            System.out.println("Consultation appointment booked successfully.");
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void viewAllConsultationRecords() {
        System.out.println("\n=== View All Consultation Records ===");
        consultationControl.displayAllConsultationRecords();
    }

    public void addFollowUpConsultation() {
        System.out.println("\n=== Add Follow-Up Appointment ===");

        // Display current consultations
        consultationControl.displayAllConsultationRecords();
        if (consultationControl.getConsultationCount() == 0) return;

        System.out.print("Enter the number of the consultation to follow-up: ");
        int index = getIntInput() - 1;

        Consultation baseConsultation;
        if (!consultationControl.isValidConsultationIndex(index)) {
            System.out.println("Invalid index. No follow-up added.");
            return;
        }else{
            baseConsultation = consultationControl.getAllConsultations().get(index);
        }

        ListInterface<Doctor> availableDoctors;
        Doctor chosenDoctor = null;
        TimeSlotKey chosenTimeslot;
        boolean repeat;
        do {
            repeat = false;
            chosenTimeslot = doctorUI.getTimeslotChoice();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
            LocalDate baseDate = LocalDate.parse(baseConsultation.getDate(), formatter);
            LocalDate followUpDate = LocalDate.parse(
                    DoctorManager.getNextWeekdayFormatted(
                            DayOfWeek.valueOf(chosenTimeslot.getTimeslot().getDay().toUpperCase())), formatter);

            availableDoctors = DoctorManager.getAvailabilityTable().get(chosenTimeslot);
            if (availableDoctors == null || availableDoctors.isEmpty()) {
                System.out.println("No doctors available for this slot, please choose another.");
                repeat = true;
            }else if(followUpDate.isBefore(baseDate)){
                System.out.println("Follow-up consultation's date must not be earlier than base consultation.");
                repeat = true;
            }else if(followUpDate.isEqual(baseDate) && 
                    Integer.compare(chosenTimeslot.getTimeslot().getHour(), 
                            baseConsultation.getTimeslot().getTimeslot().getHour()) < 1 ){
                System.out.println("Follow-up consultation's time must not be earlier than base consultation if both are on same day.");
                repeat = true;
            }else {
                chosenDoctor = doctorUI.chooseDoctor(availableDoctors, chosenTimeslot);
            }
        } while (repeat);

        
        System.out.print("Enter Follow-Up Reason/Remarks: ");
        String reason = scanner.nextLine();

        Consultation followUp = new Consultation(
            "FU-" + System.currentTimeMillis(),      // Follow-up ID
            baseConsultation.getPatient(),
            chosenDoctor,
        DoctorManager.getNextWeekdayFormatted(DayOfWeek.valueOf(chosenTimeslot.getTimeslot().getDay().toUpperCase())),
            chosenTimeslot,
            reason,
            "", "", // diagnosis, prescription
            "Follow-up consultation based on consultation ID: " + baseConsultation.getConsultationID()
        );
        DoctorManager.getAvailabilityTable().get(chosenTimeslot).remove(chosenDoctor);
        
        try{
            consultationControl.insertFollowUp(index + 1, followUp);
        }catch(Exception e){
            System.err.println("Error:" + e.getMessage());
        }

        System.out.println("Follow-up consultation added after original consultation.");
    }

    private int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next(); // consume invalid input
        }
        int input = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return input;
    }

    private void generateConsultationReport() {
        System.out.println("\n=== Generate Consultation Report ===");
        if (consultationControl.getConsultationCount() == 0) {
            System.out.println("No consultation records found to generate report.");
            return;
        }
        
        System.out.println("\n1. All Consultations Report");
        System.out.println("2. Consultations by Date Range");
        System.out.println("3. Consultations by Doctor");
        System.out.print("\nSelect report type (0 to cancel): ");
        
        int choice = getIntInput();
        if (choice == 0) {
            System.out.println("Operation cancelled.");
            return;
        }
        
        switch (choice) {
            case 1:
                consultationControl.generateAllConsultationsReport();
                break;
            case 2:
                System.out.print("\nEnter start date (YYYY-MM-DD): ");
                String startDate = scanner.nextLine();
                System.out.print("Enter end date (YYYY-MM-DD): ");
                String endDate = scanner.nextLine();
                consultationControl.generateConsultationsByDateRangeReport(startDate, endDate);
                break;
            case 3:
                System.out.print("\nEnter Doctor ID: ");
                String doctorId = scanner.nextLine();
                try{
                    consultationControl.generateConsultationsByDoctorReport(doctorId);
                }catch(Exception e){
                    System.err.println("Error:" + e.getMessage());
                }
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    private void cancelConsultation() {
        System.out.println("\n=== Cancel Consultation Appointment ===");
        if (consultationControl.getConsultationCount() == 0) {
            System.out.println("No consultations found to cancel.");
            return;
        }

        // Display all consultations with numbers
        System.out.println("\nCurrent Consultations:");
        viewAllConsultationRecords();

        System.out.print("\nEnter the number of the consultation to cancel (0 to cancel): ");
        int choice = getIntInput();

        if (choice == 0) {
            System.out.println("Operation cancelled.");
            return;
        }

        int index = choice - 1;
        if (consultationControl.isValidConsultationIndex(index)) {
            // Get the consultation object by index first
            Consultation consultationToCancel = consultationControl.getConsultationByIndex(index);
            if (consultationToCancel != null) {
                // Use the ID to cancel via the control method
                boolean success = false;
                try{
                    success = consultationControl.cancelConsultation(consultationToCancel.getConsultationID());
                }catch(Exception e){
                    System.err.println("Error:" + e.getMessage());
                }
                if (success) {
                    System.out.println("\nSuccessfully cancelled consultation for " +
                        consultationToCancel.getPatient().getName() + " on " + consultationToCancel.getTimeslot().getTimeslot().getDay() + 
                            " at " + consultationToCancel.getTimeslot().getTimeslot().getHour() + ":00.");
                    DoctorManager.getAvailabilityTable().get(consultationToCancel.getTimeslot()).add(consultationToCancel.getDoctor());
                    
                } else {
                    System.out.println("Failed to cancel consultation. It may have already been completed or cancelled.");
                }
            } else {
                 System.out.println("Error: Could not retrieve consultation details.");
            }
        } else {
            System.out.println("Invalid selection. No consultation was cancelled.");
        }
    }
}
