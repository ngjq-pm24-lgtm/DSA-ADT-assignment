package control;

import ADT.*;
import Entity.*;
import dao.GenericDAO;
import boundary.DoctorUI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Iterator;
import java.util.Scanner;

public class DoctorManager {
    private static MapInterface<TimeSlotKey, ListInterface<Doctor>> dutyScheduleTable;
    private static MapInterface<TimeSlotKey, ListInterface<Doctor>> availabilityTable;
    private static MapInterface<Integer, Doctor> doctorRecords;
    private DoctorUI doctorUI = new DoctorUI();
    private static String doctorFile = "data/doctors.dat";
    private static String doctorTextFile = "data/doctors.txt";
    private static String dutyScheduleFile = "data/dutyScheduleTable.dat";
    private static String availabilityTableFile = "data/availabilityTable.dat";
    Scanner scanner = new Scanner(System.in);

    public DoctorManager() {
        doctorRecords = GenericDAO.retrieveFromFile(doctorFile);
        if(doctorRecords == null){
            doctorRecords = GenericDAO.loadDoctorsFromTextFile(doctorTextFile);
            if(doctorRecords != null)
                GenericDAO.saveToFile(doctorRecords, doctorFile);
            else
                System.out.println("Cannot initialize doctor records from text file.");
        }
        
        dutyScheduleTable = GenericDAO.retrieveFromFile(dutyScheduleFile);
        if(dutyScheduleTable == null){
            dutyScheduleTable = new HashMap<>(42);
            GenericDAO.saveToFile(dutyScheduleTable, dutyScheduleFile);
        }
        
        availabilityTable = GenericDAO.retrieveFromFile(availabilityTableFile);
        if(availabilityTable == null){
            availabilityTable = new HashMap<>(42);
            GenericDAO.saveToFile(availabilityTable, availabilityTableFile);
        }
    }
    
    public void getMenu() {
        int choice;
        do {
          choice = doctorUI.getDoctorMaintenanceMenuChoice();
          int doctorID;
          switch(choice) {
            case 0:
                break;
            case 1:
                int docRecordsChoice = doctorUI.getDoctorRecordsMenuChoice();
                switch(docRecordsChoice){
                    case 0:
                        break;
                    case 1:
                        addNewDoctor();
                        break;
                    case 2:
                        doctorID = doctorUI.inputDoctorID();
                        Doctor doctor = doctorRecords.get(doctorID);
                        if (doctor != null) {
                            doctorUI.printDoctorDetails(doctor);
                            int choice2 = doctorUI.getUpdateDoctorChoice();
                            switch (choice2) {
                                case 0:
                                    break;
                                case 1:
                                    String newName = doctorUI.inputDoctorName();
                                    doctor.setName(newName);
                                    System.out.printf("Doctor %d name changed to %s successfully.\n", doctor.getDoctorID(), doctor.getName());
                                    break;
                                case 2:
                                    String newPhone = doctorUI.inputDoctorPhone();
                                    doctor.setPhoneNumber(newPhone);
                                    System.out.printf("Doctor %d phone number changed to %s successfully.\n", doctor.getDoctorID(), doctor.getPhoneNumber());
                                    break;
                                case 3:
                                    String newGender = doctorUI.inputDoctorGender();
                                    doctor.setGender(newGender);
                                    System.out.printf("Doctor %d gender changed to %s successfully.\n", doctor.getDoctorID(), doctor.getGender());
                                    break;
                                case 4:
                                    String newEmail = doctorUI.inputDoctorEmail();
                                    doctor.setEmail(newEmail);
                                    System.out.printf("Doctor %d email changed to %s successfully.\n", doctor.getDoctorID(), doctor.getEmail());
                                    break;
                                case 5:
                                    String newPosition = doctorUI.inputDoctorPosition();
                                    doctor.setPosition(newPosition);
                                    System.out.printf("Doctor %d position changed to %s successfully.\n", doctor.getDoctorID(), doctor.getPosition());
                                    break;
                                case 6:
                                    String newQualification = doctorUI.inputDoctorQualification();
                                    doctor.setQualification(newQualification);
                                    System.out.printf("Doctor %d qualification changed to %s successfully.\n", doctor.getDoctorID(), doctor.getQualification());
                                    break;
                                default:
                                    System.out.println("Invalid option. Please try again.");
                                    break;
                            }
                        } else {
                            System.out.printf("Doctor %d does not exist.\n", doctorID);
                        }
                        break;
                    case 3:
                        doctorID = doctorUI.inputDoctorID();
                        if (isDoctorAssignedToDuty(doctorID)) {
                            System.out.println("This doctor is currently assigned to duty. Deletion rejected.");
                            break;
                        }

                        Doctor docToRemove = doctorRecords.get(doctorID);
                        if (docToRemove != null) {
                            System.out.printf("Are you sure to remove doctor %d (%s)? (Y/N): ", docToRemove.getDoctorID(), docToRemove.getName());
                            char removeDocChoice = scanner.nextLine().charAt(0);
                            switch(removeDocChoice){
                                case 'Y':
                                case 'y':
                                    Doctor removedDoctor = doctorRecords.remove(doctorID);
                                    System.out.printf("Record of doctor %d (%s) removed successfully.\n", removedDoctor.getDoctorID(), removedDoctor.getName());
                                    GenericDAO.saveToFile(doctorRecords, doctorFile);
                                    break;
                                case 'N':
                                case 'n':
                                    System.out.println("Deletion canceled.");
                                    break;
                                default:
                                    System.out.println("Invalid choice, please try again.");
                            }
                        } else {
                            System.out.printf("Doctor %d does not exist.\n", doctorID);
                        }
                        break;
                    case 4:
                        displayAllDoctors();
                        break;
                }
                break;
            case 2:
                int choice3 = doctorUI.getDutyScheduleMenuChoice();
                TimeSlotKey chosenTimeslot;
                int[] doctorIDs, approved, rejected;
                int approveCount, rejectCount;
                switch(choice3){
                    case 0:
                        break;
                    case 1:
                        chosenTimeslot = doctorUI.getTimeslotChoice();
                        doctorIDs = doctorUI.inputDoctorIDCommaSeparated();
                        
                        approved = new int[doctorIDs.length];
                        rejected = new int[doctorIDs.length];
                        approveCount = 0;
                        rejectCount = 0;
                        for(int i=0; i<doctorIDs.length; i++){
                            Doctor chosenDoctor = doctorRecords.get(doctorIDs[i]);
                            if(chosenDoctor == null){
                                System.out.printf("Doctor of ID %d does not exist.\n", doctorIDs[i]);
                                continue;
                            }
                            
                            if (!assignDoctorToSlotDuty(chosenTimeslot, chosenDoctor))
                                rejected[rejectCount++] = chosenDoctor.getDoctorID();
                            else
                                approved[approveCount++] = chosenDoctor.getDoctorID();
                        }
                        
                        if(approveCount > 0){
                            System.out.print("Doctor of ID ");
                            for(int i=0; i<approveCount; i++){
                                System.out.print(approved[i]);
                                if(i < approveCount - 1)
                                    System.out.print(", ");
                            }
                            System.out.println(" assigned to " + chosenTimeslot + " successfully.");
                        }
                        
                        if(rejectCount > 0){
                            System.out.print("Doctor of ID ");
                            for(int i=0; i<rejectCount; i++){
                                System.out.print(rejected[i]);
                                if(i < rejectCount - 1)
                                    System.out.print(", ");
                            }
                            System.out.println(" already assigned to " + chosenTimeslot + ". Operation rejected.");
                        }
                        
                        break;
                    case 2:
                        chosenTimeslot = doctorUI.getTimeslotChoice();
                        
                        ListInterface<Doctor> doctorsOnDuty = dutyScheduleTable.get(chosenTimeslot);
                        
                        doctorUI.showDoctorsInTimeslot(doctorsOnDuty, chosenTimeslot, "On-Duty");
                        
                        if(doctorsOnDuty != null && !doctorsOnDuty.isEmpty()){
                            doctorIDs = doctorUI.inputDoctorIDCommaSeparated();
                            
                            approved = new int[doctorIDs.length];
                            rejected = new int[doctorIDs.length];
                            approveCount = 0;
                            rejectCount = 0;
                            for(int i=0; i<doctorIDs.length; i++){
                                Doctor doctorToRemove = doctorRecords.get(doctorIDs[i]);
                                
                                if (doctorToRemove == null) {
                                    System.out.printf("Doctor of ID %d does not exist.\n", doctorIDs[i]);
                                    continue;
                                }
                                
                                if (!removeDoctorFromSlotDuty(chosenTimeslot, doctorToRemove))
                                    rejected[rejectCount++] = doctorToRemove.getDoctorID();
                                else
                                    approved[approveCount++] = doctorToRemove.getDoctorID();
                            }
                            
                            if (approveCount > 0) {
                                System.out.print("Doctor of ID ");
                                for (int i = 0; i < approveCount; i++) {
                                    System.out.print(approved[i]);
                                    if (i < approveCount - 1) {
                                        System.out.print(", ");
                                    }
                                }
                                System.out.println(" removed from " + chosenTimeslot + " successfully.");
                            }

                            if (rejectCount > 0) {
                                System.out.print("Doctor of ID ");
                                for (int i = 0; i < rejectCount; i++) {
                                    System.out.print(rejected[i]);
                                    if (i < rejectCount - 1) {
                                        System.out.print(", ");
                                    }
                                }
                                System.out.println(" not assigned to " + chosenTimeslot + ". Operation rejected.");
                            }
                            
                        }
                        break;
                    case 3:
                        doctorUI.showDutySchedule(dutyScheduleTable);
                        break;
                }
                break;
            case 3:
                int choice4 = doctorUI.getAvailabilityMenuChoice();
                ListInterface<Doctor> availableDoctors;
                switch(choice4){
                    case 0:
                        break;
                    case 1:
                        chosenTimeslot = doctorUI.getTimeslotChoice();
                        availableDoctors = availabilityTable.get(chosenTimeslot);
                        doctorUI.showDoctorsInTimeslot(availableDoctors, chosenTimeslot, "Available");
                        break;
                    case 2:
                        chosenTimeslot = doctorUI.getTimeslotChoice();
                        availableDoctors = availabilityTable.get(chosenTimeslot);
                        ListInterface<Doctor> unavailableDoctors = new LinkedList<>();
                        MapEntry<Integer,Doctor>[] allDoctors = doctorRecords.getTable();
                        for(MapEntry<Integer,Doctor> doctorEntry : allDoctors){
                            if(doctorEntry != null && !doctorEntry.isRemoved()){
                                if(!availableDoctors.contains(doctorEntry.getValue()))
                                    unavailableDoctors.add(doctorEntry.getValue());
                            }
                        }
                        doctorUI.showDoctorsInTimeslot(unavailableDoctors, chosenTimeslot, "Unavailable");
                        break;
                    case 3:
                        doctorID = doctorUI.inputDoctorID();
                        Doctor chosenDoctor = doctorRecords.get(doctorID);
                        if(chosenDoctor == null)
                            System.out.println("This doctor does not exist.");
                        else{
                            System.out.printf("\nDoctor %d Available Timeslots\n-------------------------------\n", doctorID);
                            boolean noAvailableSlot = true;
                            int index = 1;
                            for (TimeSlotKey slot : TimeSlotKey.values()) {
                                ListInterface<Doctor> doctorList = availabilityTable.get(slot);
                                if (doctorList != null) {
                                    if (doctorList.contains(chosenDoctor)) {
                                        System.out.printf("%2d) %s (%s) %02d:00\n", 
                                                index++,
                                                DoctorManager.getNextWeekdayFormatted(DayOfWeek.valueOf(slot.getTimeslot().getDay().toUpperCase())),
                                                slot.getTimeslot().getDay(),
                                                slot.getTimeslot().getHour());
                                        noAvailableSlot = false;
                                    }
                                }
                            }
                            if(noAvailableSlot) System.out.println(" (No available timeslots for this doctor)");
                        }
                }
                break;
            case 4:
                int reportChoice = doctorUI.getReportGenerationMenuChoice();
                switch(reportChoice){
                    case 0:
                        break;
                    case 1:
                        doctorUI.generateDoctorDutyReport(dutyScheduleTable, availabilityTable, doctorRecords);
                        break;
                    case 2:
                        doctorUI.generateDoctorAvailabilityReport(availabilityTable);
                        break;
                }
          } 
        } while (choice != 0);
    }
    
    public static MapInterface<TimeSlotKey, ListInterface<Doctor>> getDutyScheduleTable() {
        return dutyScheduleTable;
    }

    public static MapInterface<TimeSlotKey, ListInterface<Doctor>> getAvailabilityTable() {
        return availabilityTable;
    }

    public static MapInterface<Integer, Doctor> getDoctorRecords() {
        return doctorRecords;
    }

    public static String getDoctorFile() {
        return doctorFile;
    }

    public static String getDutyScheduleFile() {
        return dutyScheduleFile;
    }

    public static String getAvailabilityTableFile() {
        return availabilityTableFile;
    }
    
    public boolean assignDoctorToSlotDuty(TimeSlotKey slot, Doctor doctor) {
        
        ListInterface<Doctor> dutyList = dutyScheduleTable.get(slot);

        // Create list if this slot is not initialized yet
        if (dutyList == null) {
            dutyList = new LinkedList<>();
            dutyScheduleTable.add(slot, dutyList);
        }

        // Avoid duplicate
        if (!dutyList.contains(doctor)) {
            dutyList.add(doctor);
            dutyScheduleTable.add(slot, dutyList);
            
            // Also add doctor to availability table
            ListInterface<Doctor> availableList = availabilityTable.get(slot);
            if (availableList == null) {
                availableList = new LinkedList<>();
                availabilityTable.add(slot, availableList);
            }
            if (!availableList.contains(doctor)){
                availableList.add(doctor);
                availabilityTable.add(slot, availableList);
            }

            GenericDAO.saveToFile(dutyScheduleTable, dutyScheduleFile);
            GenericDAO.saveToFile(availabilityTable, availabilityTableFile);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean removeDoctorFromSlotDuty(TimeSlotKey chosenTimeslot, Doctor doctorToRemove){
        ListInterface<Doctor> doctorsOnDuty = dutyScheduleTable.get(chosenTimeslot);
        ListInterface<Doctor> availableDoctors = availabilityTable.get(chosenTimeslot);
        if(!availableDoctors.contains(doctorToRemove)) //doctor present in duty table for x timeslot 
          return false;                                      //but absent in availability table for same timeslot
                                                             //means doctor have booking for that timeslot
        doctorsOnDuty.remove(doctorToRemove);
        availableDoctors.remove(doctorToRemove);         
        GenericDAO.saveToFile(dutyScheduleTable, dutyScheduleFile);
        GenericDAO.saveToFile(availabilityTable, availabilityTableFile);
        return true;
    }

    
    public void addNewDoctor() {
        String name = doctorUI.inputDoctorName();
        String phone = doctorUI.inputDoctorPhone();
        String gender = doctorUI.inputDoctorGender();
        String email = doctorUI.inputDoctorEmail();
        String position = doctorUI.inputDoctorPosition();
        String qualification = doctorUI.inputDoctorQualification();
        
        int newDoctorID = doctorRecords.size() == 0 ? 1001 : doctorRecords.findLargestKey() + 1;
        Doctor newDoctor = new Doctor(newDoctorID, name, phone, gender, email, position, qualification);

        doctorRecords.add(newDoctorID, newDoctor);
        GenericDAO.saveToFile(doctorRecords, doctorFile);
        
        System.out.printf("%s added successfully, assigned ID: %d\n", name, newDoctorID);
    }
    
    public static void resetDocAvailabilityForNewday(String weekday){
        for(TimeSlotKey timeslotkey : TimeSlotKey.values()){
            if(timeslotkey.getTimeslot().getDay().equalsIgnoreCase(weekday)){

                ListInterface<Doctor> doctorList;
                if(dutyScheduleTable.get(timeslotkey) == null){
                    doctorList = null;
                }else{
                    doctorList = dutyScheduleTable.get(timeslotkey).deepCopy();
                }
                
                availabilityTable.add(timeslotkey, doctorList);
            }
        }
    }
    
    public static String getNextWeekdayFormatted(DayOfWeek dayOfWeek) {
        LocalDate nextDay = LocalDate.now().with(TemporalAdjusters.next(dayOfWeek));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
        return nextDay.format(formatter);
    }
    

    public void displayAllDoctors() {
        doctorUI.listAllDoctors(getAllDoctors());
    }
    
    public String getAllDoctors() {
        String outputStr = "";
        MapEntry<Integer, Doctor>[] doctorEntries = doctorRecords.getTable();
        int index = 1;
        for (int i = 0; i < doctorEntries.length; i++) {
            MapEntry<Integer, Doctor> doctorEntry = doctorEntries[i];
            if (doctorEntry != null && !doctorEntry.isRemoved()){
                Doctor doctor = doctorEntry.getValue();
                outputStr += String.format("%2d) %-8d %-40s %-12s %-8s %-40s %-12s %-15s\n",
                        index++,
                        doctor.getDoctorID(),
                        doctor.getName(),
                        doctor.getPhoneNumber(),
                        doctor.getGender(),
                        doctor.getEmail(),
                        doctor.getPosition(),
                        doctor.getQualification());
            }
        }
        return outputStr;
    }
    
    public int countTotalAvailableDoctorsByDay(String weekday) {
        int[] availableDoctorsID = new int[30];
        int uniqueCount = 0;

        for (TimeSlotKey slot : TimeSlotKey.values()) {
            if (slot.getTimeslot().getDay().equalsIgnoreCase(weekday)) {
                ListInterface<Doctor> doctors = availabilityTable.get(slot);
                if (doctors != null) {
                    Iterator<Doctor> iterator = doctors.getIterator();
                    while (iterator.hasNext()) {
                        Doctor doctor = iterator.next();
                        int doctorID = doctor.getDoctorID();

                        boolean alreadyCounted = false;
                        for (int i = 0; i < uniqueCount; i++) {
                            if (availableDoctorsID[i] == doctorID) {
                                alreadyCounted = true;
                                break;
                            }
                        }

                        if (!alreadyCounted) {
                            availableDoctorsID[uniqueCount++] = doctorID;
                        }
                    }
                }
            }
        }

        return uniqueCount;
    }
    
    public boolean isDoctorAssignedToDuty(int doctorID){
        boolean assignedToDuty = false;

        for (TimeSlotKey slot : TimeSlotKey.values()) {
            ListInterface<Doctor> doctors = dutyScheduleTable.get(slot);
            if (doctors != null) {
                for (int i = 1; i <= doctors.size(); i++) {
                    if (doctors.get(i).getDoctorID() == doctorID) {
                        assignedToDuty = true;
                        break;
                    }
                }
            }
            if (assignedToDuty) {
                break;
            }
        }
        return assignedToDuty;
    }
    
}
