package app;

import boundary.PatientManagement;
import control.PatientControl;

import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PatientManagement patientUI = new PatientManagement();
        PatientControl patientControl = new PatientControl(patientUI);

        boolean exit = false;

        while (!exit) {
            int choice = patientUI.getPatientManagementMenu();

            switch (choice) {
                case 1: // Register new patient
                    patientControl.registerNewPatient();
                    break;

                case 2: // Edit patient record
                    boolean recordExit = false;
                    while (!recordExit) {
                        int recordChoice = patientUI.getPatientRecordMenu();
                        switch (recordChoice) {
                            case 1: // View
                                System.out.print("Enter Patient ID to view: ");
                                patientControl.listAllPatients();
                                break;
                            case 2: // Search
                                System.out.print("Enter Patient ID to search: ");
                                String viewId = scanner.nextLine().trim();
                                patientControl.searchPatient(viewId);
                                break;    
                            case 3: // Update
                                System.out.print("Enter Patient ID to update: ");
                                String updateId = scanner.nextLine().trim();
                                patientControl.updatePatientField(updateId);
                                break;
                            case 4: // Delete
                                System.out.print("Enter Patient ID to delete: ");
                                String delId = scanner.nextLine().trim();
                                patientControl.deletePatient(delId);
                                break;
                            case 0: // Back
                                recordExit = true;
                                break;
                            default:
                                System.out.println("Invalid choice.");
                        }
                    }
                    break;

               case 3: // Walk-in patient queue
               boolean queueExit = false;
               while (!queueExit) {
               int queueChoice = patientUI.getQueueEntryMenu(); 
               switch (queueChoice) {
            case 1: // Add Queue Entry
                patientControl.addQueueEntry();
                break;
            case 2: // View Queue Entries
                patientControl.viewQueueEntries();
                break;
            case 3: // Delete Queue Entry
                patientControl.deleteQueueEntry();
                break;
            case 0: // Return to previous menu
                queueExit = true;
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
    break;

                case 4: // Appointment
                patientControl.handleAppointmentMenu();
    break;

                case 5: // Payment
                patientControl.handlePaymentMenu();                  
                break;
                
                case 0: // Quit
                    System.out.println("Exiting system... Goodbye!");
                    exit = true;
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }
}
