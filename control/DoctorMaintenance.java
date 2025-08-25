package control;

import ADT.*;
import Entity.*;
import dao.GenericDAO;
import boundary.DoctorMaintenanceUI;
import java.util.Iterator;

public class DoctorMaintenance {
    private MapInterface<TimeSlotKey, ListInterface<Doctor>> dutyScheduleTable;
    private MapInterface<TimeSlotKey, ListInterface<Doctor>> availabilityTable;
    private MapInterface<Integer, Doctor> doctorRecords;
    private GenericDAO DAO = new GenericDAO();
    private DoctorMaintenanceUI doctorUI = new DoctorMaintenanceUI();
    private String doctorFile = "doctors.dat";
    private String doctorTextFile = "doctors.txt";
    private String dutyScheduleFile = "dutyScheduleTable.dat";
    private String availabilityTableFile = "availabilityTable.dat";


    public DoctorMaintenance() {
        doctorRecords = DAO.retrieveFromFile(doctorFile);
        if(doctorRecords == null){
            doctorRecords = DAO.loadDoctorsFromTextFile(doctorTextFile);
            if(doctorRecords != null)
                DAO.saveToFile(doctorRecords, doctorFile);
            else
                System.out.println("Cannot initialize doctor records from text file.");
        }
        
        dutyScheduleTable = DAO.retrieveFromFile(dutyScheduleFile);
        if(dutyScheduleTable == null){
            dutyScheduleTable = new HashMap<>(42);
            DAO.saveToFile(dutyScheduleTable, dutyScheduleFile);
        }
        
        availabilityTable = DAO.retrieveFromFile(availabilityTableFile);
        if(availabilityTable == null){
            availabilityTable = new HashMap<>(42);
            DAO.saveToFile(availabilityTable, availabilityTableFile);
        }
    }
    
    public void runDoctorMaintenance() {
        int choice;
        do {
          choice = doctorUI.getDoctorMaintenanceMenuChoice();
          int doctorID;
          switch(choice) {
            case 0:
                break;
            case 1:
                addNewDoctor();
                displayAllDoctors();
                break;
            case 2:
                doctorID = doctorUI.inputDoctorID();
                Doctor doctor = doctorRecords.get(doctorID);
                if(doctor != null){
                    doctorUI.printDoctorDetails(doctor);
                    int choice2 = doctorUI.getUpdateDoctorChoice();
                    switch(choice2){
                        case 0:
                            break;
                        case 1:
                            String newName = doctorUI.inputDoctorName();
                            doctor.setName(newName);
                            break;
                        case 2:
                            String newPhone = doctorUI.inputDoctorPhone();
                            doctor.setPhoneNumber(newPhone);
                    }
                }else{
                    System.out.println("This doctor does not exist.");
                }
                break;
            case 3:
                doctorID = doctorUI.inputDoctorID();
                if(isDoctorAssignedToDuty(doctorID)){
                    System.out.println("This doctor is currently assigned to duty. Operation rejected");
                    break;
                }
                
                Doctor removedDoctor = doctorRecords.remove(doctorID);
                if(removedDoctor != null){
                    System.out.println(removedDoctor.getName() + " removed.");
                    DAO.saveToFile(doctorRecords, doctorFile);
                }
                else
                    System.out.println("This doctor record does not exist.");
                break;
            case 4:
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
                        
                        doctorUI.showDoctorsInTimeslot(doctorsOnDuty, chosenTimeslot);
                        
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
            case 5:
                int choice4 = doctorUI.getAvailabilityMenuChoice();
                ListInterface<Doctor> availableDoctors;
                switch(choice4){
                    case 0:
                        break;
                    case 1:
                        chosenTimeslot = doctorUI.getTimeslotChoice();
                        availableDoctors = availabilityTable.get(chosenTimeslot);
                        doctorUI.showDoctorsInTimeslot(availableDoctors, chosenTimeslot);
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
                        doctorUI.showDoctorsInTimeslot(unavailableDoctors, chosenTimeslot);
                        break;
                    case 3:
                        doctorID = doctorUI.inputDoctorID();
                        Doctor chosenDoctor = doctorRecords.get(doctorID);
                        if(chosenDoctor == null)
                            System.out.println("This doctor does not exist.");
                        else{
                            System.out.printf("\nDoctor %d Available Timeslots\n-------------------------------\n", doctorID);
                            boolean noAvailableSlot = true;
                            for (TimeSlotKey slot : TimeSlotKey.values()) {
                                ListInterface<Doctor> doctorList = availabilityTable.get(slot);
                                if (doctorList != null) {
                                    if (doctorList.contains(chosenDoctor)) {
                                        System.out.println("  - " + slot + "\n");
                                        noAvailableSlot = false;
                                    }
                                }
                            }
                            if(noAvailableSlot) System.out.println(" (No available timeslots for this doctor)");
                        }
                }
                break;
            case 6:
                displayAllDoctors();
                break;
            case 7:
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

            DAO.saveToFile(dutyScheduleTable, dutyScheduleFile);
            DAO.saveToFile(availabilityTable, availabilityTableFile);
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
        DAO.saveToFile(dutyScheduleTable, dutyScheduleFile);
        DAO.saveToFile(availabilityTable, availabilityTableFile);
        return true;
    }

    
    public void addNewDoctor() {
        String name = doctorUI.inputDoctorName();
        String phone = doctorUI.inputDoctorPhone();

        int newDoctorID = doctorRecords.size() == 0 ? 1001 : doctorRecords.findLargestKey() + 1;
        Doctor newDoctor = new Doctor(newDoctorID, name, phone);

        doctorRecords.add(newDoctorID, newDoctor);
        DAO.saveToFile(doctorRecords, doctorFile);
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
            if (doctorEntry != null && !doctorEntry.isRemoved()) 
                outputStr += String.format("%d) %s\n\n", index++, doctorEntry.getValue());
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



    public ListInterface<Doctor> getDoctorsOnDuty(TimeSlotKey slot) {
        return dutyScheduleTable.get(slot);
    }

    public ListInterface<Doctor> getAvailableDoctors(TimeSlotKey slot) {
        return availabilityTable.get(slot);
    }

    public MapInterface<TimeSlotKey, ListInterface<Doctor>> getAvailabilityTable() {
        return availabilityTable;
    }
    
    
    
    public static void main(String[] args) {
        DoctorMaintenance doctorMaintenance = new DoctorMaintenance();
        doctorMaintenance.runDoctorMaintenance();
    }
}
