package control;

import java.util.Scanner;

public class ClinicSystemManager {
    
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=".repeat(50));
        System.out.println(" ".repeat(10) + "CLINIC MANAGEMENT SYSTEM");
        System.out.println("=".repeat(50));
        System.out.println("1. Patient Management");
        System.out.println("2. Doctor Management");
        System.out.println("3. Consultation Management");
        System.out.println("4. Medical Treatment Management");
        System.out.println("5. Pharmacy Management");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        
        switch(choice){
            
        }
    }
    
}
