package boundary;

import java.util.Scanner;
import ADT.MapInterface;
import ADT.HashMap;
import Entity.Patient;
import control.PatientControl;

public class PatientManagement {

    private Scanner scanner = new Scanner(System.in);

 public int getPatientManagementMenu() {
    int choice = -1;

    do {
        System.out.println("\nPATIENT MANAGEMENT MENU\n------------------------");
        System.out.println("1. Register new patient");
        System.out.println("2. Edit patient record");
        System.out.println("3. Walk-in patient queue");
        System.out.println("4. Appointment");
        System.out.println("5. Payment");
        System.out.println("0. Quit");
        System.out.print("Enter choice: ");

        // Validate input
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number between 0 and 5.");
            scanner.nextLine(); // clear invalid input
            continue;           // redraw menu
        }

        choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice > 5) {
            System.out.println("Invalid choice. Please enter a number between 0 and 5.");
        }

    } while (choice < 0 || choice > 5);

    return choice;
}



public int getPatientRecordMenu() {
    int choice = -1;

    do {
        System.out.println("\nPATIENT RECORD MENU\n-------------------");
        System.out.println("1. View patient record");
        System.out.println("2. Search patient record");
        System.out.println("3. Update patient record");
        System.out.println("4. Delete patient record");
        System.out.println("0. Back to previous menu");
        System.out.print("Enter choice: ");

        // Validate non-integer input
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number between 0 and 4.");
            scanner.nextLine(); // clear invalid input
            continue;           // restart loop, redraw menu
        }

        choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice > 4) {
            System.out.println("Invalid choice. Please enter a number between 0 and 4.");
        }

    } while (choice < 0 || choice > 4);

    return choice;
}


    
public int getPatientUpdateMenu() {
    int choice = -1;

    do {
        System.out.println("\nUPDATE PATIENT RECORD MENU\n-------------------------");
        System.out.println("1. Patient Name");
        System.out.println("2. IC Number");
        System.out.println("3. Gender");
        System.out.println("4. Age");
        System.out.println("5. Blood Type");
        System.out.println("6. Date of Birth");
        System.out.println("7. Contact Number");
        System.out.println("8. Emergency Number");
        System.out.println("9. Medical History");
        System.out.println("10. Address");
        System.out.println("11. Email");
        System.out.println("0. Back to previous menu");
        System.out.print("Select field to update: ");

        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number between 0 and 11.");
            scanner.nextLine(); // clear invalid input
            continue;           // restart loop, redraw menu
        }

        choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice > 11) {
            System.out.println("Invalid choice. Please enter a number between 0 and 11.");
        }

    } while (choice < 0 || choice > 11);

    return choice;
}



// ---------------- Queue Entry Menu ----------------
public int getQueueEntryMenu() {
    int choice = -1;

    do {
        System.out.println("\nWALK-IN PATIENT QUEUE MENU\n-------------------------");
        System.out.println("1. Add Queue Entry");
        System.out.println("2. View Current Queue Entry");
        System.out.println("3. Delete Queue Entry");
        System.out.println("0. Return to Previous Menu");
        System.out.print("Enter choice: ");

        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number between 0 and 3.");
            scanner.nextLine(); // clear invalid input
            continue;           // restart loop and redraw menu
        }

        choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice > 3) {
            System.out.println("Invalid choice. Please enter a number between 0 and 3.");
        }

    } while (choice < 0 || choice > 3);

    return choice;
}


// ---------------- Appointment Menu ----------------
public int getAppointmentMenu() {
    int choice = -1;

    do {
        System.out.println("\nAPPOINTMENT MENU\n----------------");
        System.out.println("1. Schedule new appointment");
        System.out.println("2. View appointment list");
        System.out.println("3. Search patient appointment");
        System.out.println("4. Cancel appointment");
        System.out.println("0. Return to Previous Menu");
        System.out.print("Enter choice: ");

        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number between 0 and 4.");
            scanner.nextLine(); // clear invalid input
            continue;           // restart loop and redraw menu
        }

        choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice > 4) {
            System.out.println("Invalid choice. Please enter a number between 0 and 4.");
        }

    } while (choice < 0 || choice > 4);

    return choice;
}



// ------------------ Payment Menu ------------------ //
public int getPaymentMenu() {
    int choice = -1;

    do {
        System.out.println("\n--- Payment Menu ---");
        System.out.println("1. Generate Receipt");
        System.out.println("2. Payment History");
        System.out.println("3. Clear Payment");
        System.out.println("0. Return to Previous Menu");
        System.out.print("Enter choice: ");

        String input = scanner.nextLine().trim();

        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Enter digits only.");
            continue; // redraw menu
        }

        if (choice < 0 || choice > 3) {
            System.out.println("Invalid choice. Please enter a number between 0 and 3.");
            choice = -1; // force loop again
        }

    } while (choice < 0 || choice > 3);

    return choice;
}



    public String inputPatientId() {
        System.out.print("Enter Patient ID: ");
        return scanner.nextLine().trim();
    }

    public void displayMessage(String msg) {
        System.out.println(msg);
    }

    public void displayPaymentReceipt(String receipt) {
        System.out.println("\n--- Payment Receipt ---");
        System.out.println(receipt);
    }



    
    

    // ---------------- Basic Inputs ---------------- //
    public String inputPatientName() {
        System.out.print("Enter patient name: ");
        return scanner.nextLine();
    }

    public String inputPatientIC() {
        System.out.print("Enter IC No: ");
        return scanner.nextLine();
    }

    public String inputPatientGender() {
        System.out.print("Enter Gender (M/F): ");
        return scanner.nextLine();
    }

   public int inputPatientAge() {
    int age = -1;
    while (true) {
        System.out.print("Enter age: ");
        try {
            age = Integer.parseInt(scanner.nextLine().trim());
            if (age < 0) {
                System.out.println("Age cannot be negative. Please enter again.");
            } else {
                break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter digits only.");
        }
    }
    return age;
}


    public String inputBloodType() {
        System.out.print("Enter Blood Type: ");
        return scanner.nextLine();
    }

    public String inputDateOfBirth() {
        System.out.print("Enter Date of Birth (yyyy-MM-dd): ");
        return scanner.nextLine();
    }

    public String inputContactNo() {
        System.out.print("Enter Personal Contact Number: ");
        return scanner.nextLine();
    }

    public String inputEmergencyNo() {
        System.out.print("Enter Emergency Contact Number: ");
        return scanner.nextLine();
    }

    public String inputMedicalHistory() {
        System.out.print("Enter Medical History: ");
        return scanner.nextLine();
    }

    public String inputAddress() {
        System.out.print("Enter Address: ");
        return scanner.nextLine();
    }

    public String inputEmail() {
        System.out.print("Enter Email: ");
        return scanner.nextLine();
    }

    // ---------------- Output ---------------- //
    public void printPatientDetails(Patient patient) {
        System.out.println("\nPatient Details\n---------------");
        System.out.println(patient.toString());
    }

    public void listAllPatients(String outputStr) {
        System.out.println("\nList of Patients\n----------------\n" + outputStr);
    }
}
