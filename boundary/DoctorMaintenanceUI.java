package boundary;

import adt.*;
import java.util.Scanner;
import entity.Doctor;
import enums.TimeSlot;
import control.DoctorMaintenance;
import entity.TimeSlotKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class DoctorMaintenanceUI {
    
    Scanner scanner = new Scanner(System.in);
    
    public int getDoctorMaintenanceMenuChoice() {
        
        int choice;
        do{
            System.out.println("\nDOCTOR MAINTENANCE MENU\n-----------------------");
            System.out.println("1. Add new doctor record");
            System.out.println("2. Search/update doctor record");
            System.out.println("3. Remove doctor record");
            System.out.println("4. Manage duty schedule");
            System.out.println("5. Track doctor availability");
            System.out.println("6. List all doctors");
            System.out.println("7. Generate reports");
            System.out.println("0. Quit");
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();
        
            if(choice<0 || choice>7)
                System.out.println("Please enter 0 to 7 only.");
        }while(choice<0 || choice>7);
        
        return choice;
    }
    
    public int getDutyScheduleMenuChoice(){
        
        int choice;
        do{
            System.out.println("\nDUTY SCHEDULE MENU\n------------------");
            System.out.println("1. Assign doctor to a time slot");
            System.out.println("2. Remove doctor from a timeslot");
            System.out.println("3. View all duty schedules");
            System.out.println("0. Back to previous menu");
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();
            
            if(choice<0 || choice>3)
                System.out.println("Please enter 0 to 3 only.");
        }while(choice<0 || choice>3);
        
        return choice;
    }
    
    public int getReportGenerationMenuChoice(){
        int choice;
        do{
            System.out.println("\nREPORT GENERATION MENU\n----------------------");
            System.out.println("1. Doctor Duty Assignment Report");
            System.out.println("2. Doctor Availability Report");
            System.out.println("0. Back to previous menu");
            System.out.println("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();
            
            if(choice<0 || choice>2)
                System.out.println("Please enter 0 to 2 only.");
        }while(choice<0 || choice>2);
                    
        return choice;
    }
    
    public int getAvailabilityMenuChoice(){
        
        int choice;
        do{
            System.out.println("\nDOCTOR AVAILABILITY MENU\n------------------------");
            System.out.println("1. View available doctors by time slot");
            System.out.println("2. View unavailable doctors by time slot");
            System.out.println("3. View availability of specific doctor");
            System.out.println("0. Back to previous menu");
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();
            
            if(choice<0 || choice>3)
                System.out.println("Please enter 0 to 3 only.");
        }while(choice<0 || choice>3);
        
        return choice;
    }
    
    public TimeSlotKey getTimeslotChoice() {
        Scanner scanner = new Scanner(System.in);

        String chosenDay = getWeekDayChoice();

        int chosenTimeslot;
        int index;
        do{
            index = 1;
            System.out.println("Select a time slot:");
            for (TimeSlot slot : TimeSlot.values()) {
                if (slot.getDay().equalsIgnoreCase(chosenDay)) {
                    System.out.printf("%d. %s\n", index++, slot);
                }
            }

            System.out.print("Enter choice: ");
            chosenTimeslot = scanner.nextInt();
            scanner.nextLine();
            
            if(chosenTimeslot<1 || chosenTimeslot>6)
                System.out.println("Please enter 1 to 6 only.");
        }while(chosenTimeslot<1 || chosenTimeslot>6);

        int i = 1;
        for (TimeSlot slot : TimeSlot.values()) {
            if (slot.getDay().equalsIgnoreCase(chosenDay)) {
                if (i == chosenTimeslot) {
                    return TimeSlotKey.convertTimeslotToKey(slot);
                }
                i++;
            }
        }
        return null;
    }

    public void showDoctorsInTimeslot(ListInterface<Doctor> doctorList, TimeSlotKey chosenTimeslot){
        System.out.println("\nDoctors for " + chosenTimeslot + "\n-----------------------------");
        if(doctorList == null || doctorList.isEmpty()){
            System.out.println("No doctors within this timeslot.");
            return;
        }
        doctorList.sort((a,b) -> a.compareTo(b));
        for (int i = 0; i < doctorList.size(); i++) {
            Doctor d = doctorList.get(i+1);
            System.out.println("  - " + d.getName() + " (ID: " + d.getDoctorID() + ")");
        }
    }
    
    public String getWeekDayChoice(){
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        int chosenDay;
        do {
            System.out.println("Select a day:");
            for (int i = 0; i < days.length; i++) {
                System.out.printf("%d. %s\n", i + 1, days[i]);
            }

            System.out.print("Enter choice: ");
            chosenDay = scanner.nextInt();
            scanner.nextLine();

            if (chosenDay < 1 || chosenDay > 7) {
                System.out.println("Please enter 1 to 7 only.");
            }
        } while (chosenDay<1 || chosenDay>7);
        
        return days[chosenDay - 1];
    }
    
    public void showDutySchedule(MapInterface<TimeSlotKey, ListInterface<Doctor>> dutySchedule) {
        String chosenDay = getWeekDayChoice();

        System.out.printf("\n\n--- Duty Schedule for %s ---\n", chosenDay);
        
        int index = 1;
        for(TimeSlotKey timeslot : TimeSlotKey.values()){
            if(timeslot.getTimeslot().getDay().equalsIgnoreCase(chosenDay)){
                System.out.print(index++ + ". " + timeslot.toString() + " : ");
                ListInterface<Doctor> doctorsOnDuty = dutySchedule.get(timeslot);
                if(doctorsOnDuty == null){ //if this timeslot doesnt exist in duty schedule
                    System.out.println(" (vacant)"); //it means no doctors ever assigned to this timeslot yet
                }else if(doctorsOnDuty instanceof LinkedList){ //if a LinkedList is returned, it means there are doctors assigned to this timeslot before, but unclear if they still assigned or already removed
                    if(doctorsOnDuty.size() > 0){  //so we check whether there are doctors in it
                        doctorsOnDuty.sort((a,b) -> a.compareTo(b));
                        Iterator<Doctor> iterator = doctorsOnDuty.getIterator();
                        int doctorCount = -1;
                        while(iterator.hasNext()){
                            if(++doctorCount % 5 == 0 && doctorCount != 0)
                                System.out.print("\n                  ");
                            Doctor doc = iterator.next();
                            System.out.printf("%s (%d)", doc.getName(), doc.getDoctorID());
                            if(iterator.hasNext()) System.out.print(", ");   
                        }
                        System.out.println();
                    }else{
                        System.out.println(" (vacant)");
                    }

                }
                System.out.println();
            }

        }
        System.out.println("-----------------------------\n");
    }

    
    public int getUpdateDoctorChoice(){
        
        int choice;
        do{
            System.out.println("\n1. Update doctor name");
            System.out.println("2. Update doctor phone number");
            System.out.println("0. Quit");
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();
            
            if(choice<0 || choice>2)
                System.out.println("Please enter 0 to 2 only.");
        }while(choice<0 || choice>2);
        
        return choice;
    }
    
    public void printDoctorDetails(Doctor doctor){
        System.out.println("\nDoctor details\n--------------");
        System.out.println(doctor.toString());
    }
    
    public int inputDoctorID(){
        
        System.out.print("Enter doctor ID: ");
        int doctorID = scanner.nextInt();
        scanner.nextLine();
        
        return doctorID;
    }
    
    public int[] inputDoctorIDCommaSeparated(){
        System.out.print("Enter doctor ID (if more than one, separate with ',') : ");
        String input = scanner.nextLine();
        String[] splitted = input.split(",");
        int[] doctorIDs = new int[splitted.length];
        
        for(int i=0; i<splitted.length; i++){
            doctorIDs[i] = Integer.parseInt(splitted[i].trim());
        }
        
        return doctorIDs;
    }
    
    public String inputDoctorName() {
        String doctorName;
        
        System.out.print("Enter doctor name: ");
        doctorName = scanner.nextLine();
        
        return doctorName;
    }
    
    public String inputDoctorPhone() {
        String doctorPhone;

        System.out.print("Enter doctor phone: ");
        doctorPhone = scanner.nextLine();
        
        return doctorPhone;
    }
    
    
    public void listAllDoctors(String outputStr) {
        System.out.println("\nList of Doctors\n---------------\n" + outputStr);
    }
    
    public void generateDoctorDutyReport(MapInterface<TimeSlotKey, ListInterface<Doctor>> dutyScheduleTable, 
            MapInterface<TimeSlotKey, ListInterface<Doctor>> availabilityTable, 
            MapInterface<Integer, Doctor> doctorRecords) {
        
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy (EEEE) h.mma");

        String formattedTime = now.format(formatter);
        
        System.out.println("===========================================================================================================================================");
        System.out.println("                               CLINIC MANAGEMENT SYSTEM - DOCTOR DUTY SCHEDULING REPORT");
        System.out.println("===========================================================================================================================================");
        System.out.printf("Generated at: %s%n", formattedTime);
        System.out.println("---------------------------------------------\n");

        System.out.println(" ".repeat(50) + "'#' indicates assigned timeslot");
        System.out.println(" ".repeat(50) + "'@' indicates booked timeslot");
        
        MapEntry<Integer, Doctor>[] allDoctors = doctorRecords.getTable();
        System.out.print("Doctor ID");
        
        MapInterface<Integer,Integer> doctorsDutyCount = new HashMap<>(doctorRecords.size());
        Doctor[] mostSlotDoctor = new Doctor[doctorRecords.size()];
        Doctor[] leastSlotDoctor = new Doctor[doctorRecords.size()];
        int mostSlots = Integer.MIN_VALUE, mostSlotDocCount = -1, leastSlots = Integer.MAX_VALUE;
        int leastSlotDocCount = -1, totalTimeslotCount = 0;
        for (int i = 0; i < allDoctors.length; i++) {
            if (allDoctors[i] != null && !allDoctors[i].isRemoved()) {
                System.out.printf("\n%6d  | ", allDoctors[i].getValue().getDoctorID());

                int timeslotCount = 0, availableTimeslotCount = 0;
                MapEntry<TimeSlotKey, ListInterface<Doctor>>[] allDuties = dutyScheduleTable.getTable();
                for (int j = 0; j < allDuties.length; j++) {
                    if(allDuties[j] != null){
                        ListInterface<Doctor> doctorsInTimeslot = allDuties[j].getValue();
                        if (doctorsInTimeslot.contains(allDoctors[i].getValue())) {
                            timeslotCount++;
                        }
                    }
                }
                
                doctorsDutyCount.add(allDoctors[i].getValue().getDoctorID(), timeslotCount);
                totalTimeslotCount += timeslotCount;
                
                if (timeslotCount > mostSlots) {
                    mostSlots = timeslotCount;
                    mostSlotDocCount = 0; // reset
                    mostSlotDoctor[mostSlotDocCount] = allDoctors[i].getValue();
                } else if (timeslotCount == mostSlots) {
                    mostSlotDoctor[++mostSlotDocCount] = allDoctors[i].getValue();
                }

                if (timeslotCount < leastSlots) {
                    leastSlots = timeslotCount;
                    leastSlotDocCount = 0; // reset
                    leastSlotDoctor[leastSlotDocCount] = allDoctors[i].getValue();
                } else if (timeslotCount == leastSlots) {
                    leastSlotDoctor[++leastSlotDocCount] = allDoctors[i].getValue();
                }

                
                MapEntry<TimeSlotKey, ListInterface<Doctor>>[] allBookedDuties = availabilityTable.getTable();
                for (int j = 0; j < allBookedDuties.length; j++) {
                    if(allBookedDuties[j] != null){
                        ListInterface<Doctor> doctorsInTimeslot = allBookedDuties[j].getValue();
                        if (doctorsInTimeslot.contains(allDoctors[i].getValue())) {
                            availableTimeslotCount++;
                        }
                    }
                }

                System.out.print("#".repeat(timeslotCount) + "     (" + timeslotCount + ")\n        | ");
                System.out.print("@".repeat(availableTimeslotCount) + "     (" + availableTimeslotCount + ")\n        |");
                
                
                if(i % 9 == 0){
                    System.out.println("----------------------------------------------------------------->  Assigned Timeslots");
                    System.out.println("                       10             20             30             40\n\n\n");
                    System.out.println(" ".repeat(50) + "'#' indicates assigned timeslot");
                    System.out.println(" ".repeat(50) + "'@' indicates available timeslot");
                    System.out.print("Doctor ID");
                }

            }
        }
        
        System.out.println("----------------------------------------------------------------->  Assigned Timeslots");
        System.out.println("                       10             20             30             40");
        
        System.out.println("\n\n---------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("Doctors with most assigned timeslot  (%2d slots): ", mostSlots);
        for(int i = 0; i <= mostSlotDocCount; ) {
            System.out.printf("%s (%d)", mostSlotDoctor[i].getName(), mostSlotDoctor[i].getDoctorID());
            if(i < mostSlotDocCount) System.out.print(", ");
            i++;
            if(i % 3 == 0) System.out.print("\n                                                 ");
        }
 
        System.out.printf("\nDoctors with least assigned timeslot (%2d slots): ", leastSlots);
        for(int i = 0; i <= leastSlotDocCount; ) {
            System.out.printf("%s (%d)", leastSlotDoctor[i].getName(), leastSlotDoctor[i].getDoctorID());
            if(i < leastSlotDocCount) System.out.print(", ");
            i++;
            if(i % 3 == 0) System.out.print("\n                                                 ");
        }
        
        
        System.out.println("\n---------------------------------------------------------------------------------------------------------------------------------------");
        
        System.out.println("\n" + " ".repeat(35) + "---------------------");
        System.out.println(" ".repeat(35) + "Doctor Workload Share");
        System.out.println(" ".repeat(35) + "---------------------");
                
        System.out.printf("%10s  |   %25s  |   %30s\n", "Doctor ID", "No. of Assigned Timeslots", "% of Total Assigned Timeslots");
        System.out.println("-".repeat(100));
        MapEntry<Integer,Integer>[] arr = doctorsDutyCount.sort((a, b) -> a.compareTo(b));
        for(MapEntry<Integer,Integer> entry : arr){
            System.out.printf("%10d  |   %25d  |   %30.2f\n", entry.getKey(), entry.getValue(), (totalTimeslotCount == 0? 0 : (double) entry.getValue() / totalTimeslotCount * 100));
        }
        System.out.println("-".repeat(100));
        
        System.out.println("\n===========================================================================================================================================");
        System.out.println("                                                     END OF THE REPORT");
        System.out.println("===========================================================================================================================================");
    }

    
    public void generateDoctorAvailabilityReport(MapInterface<TimeSlotKey, ListInterface<Doctor>> availabilityTable) {
        
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy (EEEE) h.mma");

        String formattedTime = now.format(formatter);
        
        System.out.println("===========================================================================================================================================");
        System.out.println("                                       CLINIC MANAGEMENT SYSTEM - DOCTOR AVAILABILITY REPORT");
        System.out.println("===========================================================================================================================================");
        System.out.printf("Generated at: %s%n", formattedTime);
        System.out.println("---------------------------------------------\n");

        int[] totalAvailableDoctorsByDay = new int[7];
        int totalAvailabilities = 0, maxAvailable = 0, minAvailable = 0;
        ListInterface<TimeSlot> maxSlots = new LinkedList<>();
        ListInterface<TimeSlot> minSlots = new LinkedList<>();

        System.out.printf("%-15s | %-25s | %-60s%n", "Timeslot", "Available Doctors Count", "Available Doctors");
        System.out.println("----------------------------------------------------------------------------------------------");

        for (TimeSlotKey slot : TimeSlotKey.values()) {
            ListInterface<Doctor> doctors = availabilityTable.get(slot);
            int count = (doctors != null) ? doctors.size() : 0;
            totalAvailabilities += count;

            if (count > maxAvailable) {
                maxAvailable = count;
                maxSlots.clear();
                maxSlots.add(slot.getTimeslot());
            } else if (count == maxAvailable) {
                maxSlots.add(slot.getTimeslot());
            }
            if (count < minAvailable) {
                minAvailable = count;
                minSlots.clear();
                minSlots.add(slot.getTimeslot());
            } else if (count == minAvailable) {
                minSlots.add(slot.getTimeslot());
            }

            StringBuilder sb = new StringBuilder();
            if (doctors != null && doctors.size() > 0) {
                doctors.sort((a, b) -> a.compareTo(b));
                for (int i = 1; i <= doctors.size(); i++) {
                    sb.append(doctors.get(i).getDoctorID());
                    if(i < doctors.size()) sb.append(", ");
                    if(i%8 == 0 && i < doctors.size()) sb.append("\n                                            | ");
                }
            } else {
                sb.append("-");
            }

            System.out.printf("%-15s | %-25d | %-60s\n", slot.toString(), count, sb.toString());
            
    }
        
        String[] weekdays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        DoctorMaintenance docMaintenance = new DoctorMaintenance();
        int largest = 0;
        for(int i=0; i<7; i++){
            totalAvailableDoctorsByDay[i] = docMaintenance.countTotalAvailableDoctorsByDay(weekdays[i]);
            if(totalAvailableDoctorsByDay[i] > largest)
                largest = totalAvailableDoctorsByDay[i];
        }
        System.out.println("----------------------------------------------------------------------------------------------\n");
    
        
        System.out.println("Total\nAvailable\nDoctors");

        for (int level = largest; level >= 1; level--) {
            System.out.printf("%2d | ", level);
            for (int i=0; i<7; i++) {
                if (totalAvailableDoctorsByDay[i] >= level) {
                    System.out.print("   *   ");
                } else {
                    System.out.print("       ");
                }
            }
            System.out.println();
        }

        System.out.println("-----------------------------------------------------> Weekdays");

        System.out.print("    ");
        for (String weekday : weekdays) {
            System.out.print("   " + weekday.substring(0, 3) + " ");
        }


    System.out.println("\n\n----------------------------------------------------------------------------------------------");
    System.out.printf("Total doctor availabilities in the week: %d%n", totalAvailabilities);
    System.out.printf("Timeslot(s) with the most available doctors  (%2d): %s%n", maxAvailable, formatSlotList(maxSlots));
    System.out.printf("Timeslot(s) with the least available doctors (%2d): %s%n", minAvailable, formatSlotList(minSlots));
    System.out.println("\n===========================================================================================================================================");
    System.out.println("                                                     END OF THE REPORT");
    System.out.println("===========================================================================================================================================");
}
    
    


    private String formatDoctorList(ListInterface<Doctor> doctors) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= doctors.size(); i++) {
            sb.append(doctors.get(i).getDoctorID());
            if (i < doctors.size()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
    
    
    private String formatSlotList(ListInterface<TimeSlot> slots) {
        if(slots==null || slots.isEmpty()){
            return "-";
        }

        StringBuilder sb = new StringBuilder();
        for(int i = 1; i <= slots.size(); i++) {
            sb.append(slots.get(i).toString());
            if(i < slots.size()) {
                sb.append(", ");
            }
            if(i % 5 == 0){
                sb.append("\n                                                   ");
            }
        }
        return sb.toString();
    }


    
}
