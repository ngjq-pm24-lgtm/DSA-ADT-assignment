package control;

import Entity.TimeSlotResetFlag;
import boundary.*;
import dao.GenericDAO;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Scanner;

public class ClinicSystemManager {
    
    public static void main(String[] args){
        DoctorManager doctorManager = new DoctorManager();
        ConsultationUI consultationUI = new ConsultationUI();
        PatientUI patientUI = new PatientUI();
        PatientControl patientControl = new PatientControl(patientUI);
        Scanner scanner = new Scanner(System.in);
        

        String today = LocalDate.now()
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        //reset doctor availability of current weekday if it's a new day
        TimeSlotResetFlag timeslotFlag = GenericDAO.getTimeSlotResetFlagFromFile();
        
        if(timeslotFlag == null){
            timeslotFlag = new TimeSlotResetFlag();
            timeslotFlag.setLastRunWeekday(today);
            GenericDAO.saveTimeSlotResetFlag(timeslotFlag);
        }
        
        //if it's a new day
        if(!today.equalsIgnoreCase(timeslotFlag.getLastRunWeekday())){
            DoctorManager.resetDocAvailabilityForNewday(today);
            //save weekday of current run to be referred by next run
            timeslotFlag.setLastRunWeekday(today);
            GenericDAO.saveTimeSlotResetFlag(timeslotFlag);
        }

        
        int choice;
        do{
            System.out.println("=".repeat(50));
            System.out.println(" ".repeat(10) + "CLINIC MANAGEMENT SYSTEM");
            System.out.println("=".repeat(50));
            System.out.println("1. Patient Management Menu");
            System.out.println("2. Consultation Management Menu");
            System.out.println("3. Doctor Management Menu");
            System.out.println("4. Medical Treatment Management Menu");
            System.out.println("5. Pharmacy Management Menu");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    boolean exit = false;
                    while (!exit) {
                        int patientMainMenuChoice = patientUI.getPatientManagementMenu();

                        switch (patientMainMenuChoice) {
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
                                System.out.println("Exiting to main menu...");
                                exit = true;
                                break;

                            default:
                                System.out.println("Invalid choice. Please try again.");
                        }
                    }
                    break;
                case 2:
                    consultationUI.menu();
                    break;
                case 3:
                    doctorManager.runDoctorManager();
                    break;
                case 4:
                    break;
                case 5:
                    break;
            }
        }while(choice != 0);
    }
    
    
}
