package control;

import Entity.TimeSlotResetFlag;
import boundary.ConsultationUI;
import dao.GenericDAO;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Scanner;

public class ClinicSystemManager {
    
    public static void main(String[] args){
        DoctorManager doctorManager = new DoctorManager();
        ConsultationUI consultationUI = new ConsultationUI();
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

            switch (choice) {
                case 1:
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
