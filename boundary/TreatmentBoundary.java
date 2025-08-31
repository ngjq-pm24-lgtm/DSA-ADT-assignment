package ADT;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Khoo Sheng Hao
 */

import java.util.Scanner;
import ADT.Treatment;
public class TreatmentBoundary {

    private static int treatmentID = 1000;
    private TreatmentCtrl control;
    private Scanner scanner;

    public TreatmentBoundary(TreatmentCtrl control) {
        this.control = control;
        scanner = new Scanner(System.in);
    }

    public void start() {
        int choice = -1;
        do {
            System.out.println("=== Medical Treatment Management ===");
            System.out.println("1. Add Treatment Type");
            System.out.println("2. Remove Treatment Type");
            System.out.println("3. View Treatment Type");
            System.out.println("4. View All Current Available Treatments");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            
            try {
            choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
            System.out.println("\nInvalid input. Please enter a number only.");
            choice = -1; // reset choice to avoid accidental exit
            }

            switch (choice) {
                case 1:
                    addTreatmentUI();
                    break;
                case 2:
                    removeTreatmentUI();
                    break;
                case 3:
                    viewDiseaseUI();
                    break;
                case 4:
                    control.printAllTreatmentsSummary();
                    break;
                case 0:
                    System.out.println("\nExiting module...");
                    break;
                case -1:
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 0);
    }

    private void addTreatmentUI() {
        System.out.println("\n===== Add New Disease Treatment =====");
        String disease;
        String symptoms;
        String severity;
        String medicines;
        int duration = 0;
        String formatDuration = "";
        
        do{
        System.out.print("Enter Disease Name: ");
        disease = scanner.nextLine().trim();
        if(disease.isEmpty()){
            System.out.println("Disease Name cannot be empty. Please try again");
            }
        }
        while (disease.isEmpty());  

        do{
        System.out.print("Enter Symptoms: ");
        symptoms = scanner.nextLine().trim();
        if(symptoms.isEmpty()){
            System.out.println("Symptoms Cannot be Empty. Please try again");
        }
        }
        while(symptoms.isEmpty());

        do{
        System.out.print("Enter Severity of the Disease (Low/Moderate/High): ");
        severity = scanner.nextLine().trim();
        if(severity.equalsIgnoreCase("Low") ||
            severity.equalsIgnoreCase("Moderate") ||
                severity.equalsIgnoreCase("High")){
            break;
        }
        else{
            System.out.println("Invalid Input, Severity must be Low, Moderate or High. Please try again");
        }
        }
        while(severity.isEmpty());

        do{
        System.out.print("Enter Medicines / Treatments to be Provided: ");
        medicines = scanner.nextLine().trim();
         if(medicines.isEmpty()){
            System.out.println("Medicines / Treatments Cannot be Empty. Please try again");
        }
        }while(medicines.isEmpty());

        while (true) {
        System.out.print("Enter Duration of Treatment (Days): ");
        String input = scanner.nextLine().trim();
        try {
            duration = Integer.parseInt(input);
            if (duration > 0) {
                formatDuration = String.valueOf(duration) + " days";
                break;
        }
            System.out.println("Duration must be greater than 0.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }

        
    }

        Treatment treatment = new Treatment("T" + treatmentID, disease, symptoms, severity, medicines, formatDuration);
        control.addTreatment(disease, treatment);
        treatmentID++;
        System.out.println("\n...");
        System.out.println("\nRecord Added\n");
    }

    private void removeTreatmentUI() {
        System.out.print("Enter treatment to remove: ");
        String treatment = scanner.nextLine();
        if (control.removeTreatment(treatment)) {
            System.out.println("Treatment removed successfully!");
        } else {
            System.out.println("Treatment not found.");
        }
    }

    private void viewDiseaseUI() {
        System.out.print("Enter Disease Name: ");
        String disease = scanner.nextLine();
        TreatmentList<Treatment> diseaseType = control.getTreatmentDesc(disease);
        if (diseaseType == null || diseaseType.isEmpty()) {
            System.out.println("No Treatment Type found: " + disease);
        } else {
            System.out.println("Treatment for " + disease + ":");
            System.out.printf("%-14s %-20s %-30s %-12s %-30s %-18s",
                            "TreatmentID", "Disease Name", "Symptoms", "Severity", "Medication / Treatment", "Duration (Days)");
            System.out.println("\n====================================================================================================================================");
            for (int i = 1; i <= diseaseType.size(); i++) {
                System.out.println(diseaseType.get(i));
            }
        }
    }
}


>>>>>>> 88aa35ab7ffdfecc9ea80462c8f25835fd1aa97c
