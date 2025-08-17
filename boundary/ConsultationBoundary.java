package boundary;

import control.ConsultationControl;
import Entity.Consultation;

import java.util.Scanner;

public class ConsultationBoundary {
    private ConsultationControl control;
    private Scanner scanner;

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
                    // cancelConsultation(); // to be implemented
                    System.out.println("[Feature not implemented yet]");
                    break;
                case 5:
                    // generateConsultationReport(); // to be implemented
                    System.out.println("[Feature not implemented yet]");
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
        Consultation consultation = control.createConsultation(patientId, doctorId, date, time, reason);
        control.addConsultation(consultation);
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

        control.insertFollowUp(index + 1, followUp);
        System.out.println("Follow-up consultation added after original consultation.");
    }

    private int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}
