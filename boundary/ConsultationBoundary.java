package boundary;

import control.ConsultationControl;
import Entity.Consultation;
import exception.ValidationException;

import java.util.Scanner;

public class ConsultationBoundary {
    private final ConsultationControl control;
    private final Scanner scanner;

    public ConsultationBoundary(ConsultationControl control) {
        this.control = control;
        this.scanner = new Scanner(System.in);
    }

    public void menu() {
        int choice;
        do {
            System.out.println("\n=== Consultation Management ===");
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

        System.out.print("Enter Doctor ID: ");
        String doctorId = scanner.nextLine();

        System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        System.out.print("Enter Appointment Time (HH:MM): ");
        String time = scanner.nextLine();

        System.out.print("Enter Reason/Remarks: ");
        String reason = scanner.nextLine();

        // Create consultation object (requires Patient & Doctor objects fetched via control)
        try{
            Consultation consultation = control.createConsultation(patientId, doctorId, date, time, reason);
            control.addConsultation(consultation);
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        System.out.println("Consultation appointment booked successfully.");
    }

    public void viewAllConsultationRecords() {
        System.out.println("\n=== View All Consultation Records ===");
        control.displayAllConsultationRecords();
    }

    public void addFollowUpConsultation() {
        System.out.println("\n=== Add Follow-Up Appointment ===");

        // Display current consultations
        control.displayAllConsultationRecords();
        if (control.getConsultationCount() == 0) return;

        System.out.print("Enter the number of the consultation to follow-up: ");
        int index = getIntInput() - 1;

        if (!control.isValidConsultationIndex(index)) {
            System.out.println("Invalid index. No follow-up added.");
            return;
        }

        System.out.print("Enter Follow-Up Appointment Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        System.out.print("Enter Follow-Up Appointment Time (HH:MM): ");
        String time = scanner.nextLine();

        System.out.print("Enter Follow-Up Reason/Remarks: ");
        String reason = scanner.nextLine();

        Consultation base = control.getConsultationByIndex(index);
        Consultation followUp = new Consultation(
            "FU-" + System.currentTimeMillis(),      // Follow-up ID
            base.getPatient(),
            base.getDoctor(),
            date,
            time,
            reason,
            "", "", // diagnosis, prescription
            "Follow-up consultation based on consultation ID: " + base.getConsultationID()
        );
        
        try{
            control.insertFollowUp(index + 1, followUp);
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
        if (control.getConsultationCount() == 0) {
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
                control.generateAllConsultationsReport();
                break;
            case 2:
                System.out.print("\nEnter start date (YYYY-MM-DD): ");
                String startDate = scanner.nextLine();
                System.out.print("Enter end date (YYYY-MM-DD): ");
                String endDate = scanner.nextLine();
                control.generateConsultationsByDateRangeReport(startDate, endDate);
                break;
            case 3:
                System.out.print("\nEnter Doctor ID: ");
                String doctorId = scanner.nextLine();
                try{
                    control.generateConsultationsByDoctorReport(doctorId);
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
        if (control.getConsultationCount() == 0) {
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
        if (control.isValidConsultationIndex(index)) {
            // Get the consultation object by index first
            Consultation consultationToCancel = control.getConsultationByIndex(index);
            if (consultationToCancel != null) {
                // Use the ID to cancel via the control method
                boolean success = false;
                try{
                    success = control.cancelConsultation(consultationToCancel.getConsultationID());
                }catch(Exception e){
                    System.err.println("Error:" + e.getMessage());
                }
                if (success) {
                    System.out.println("\nSuccessfully cancelled consultation for " +
                        consultationToCancel.getPatient().getName() + " on " + consultationToCancel.getDate() + " at " + consultationToCancel.getTime());
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
