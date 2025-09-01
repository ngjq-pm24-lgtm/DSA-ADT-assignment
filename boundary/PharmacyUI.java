package boundary;

import control.PharmacyManager;
import Entity.DispenseOrder;
import Entity.Medicine;
import java.util.Scanner;
import java.util.ArrayList;

public class PharmacyUI {
    private PharmacyManager manager;
    private Scanner scanner;

    public PharmacyUI() {
        manager = new PharmacyManager();
        scanner = new Scanner(System.in);
    }

    public void getMenu() {
        int choice;
        do {
            System.out.println("\n=== Pharmacy Management ===");
            System.out.println("1. Add Dispense Order");
            System.out.println("2. Process Next Order");
            System.out.println("3. Generate Stock Report");
            System.out.println("4. Generate Queue Report");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addDispenseOrder();
                    break;
                case 2:
                    processSelectedOrder();
                    break;
                case 3:
                    manager.generateStockReport();
                    break;
                case 4:
                    manager.generateQueueReport();
                    break;
                case 5:
                    System.out.println("Exiting Pharmacy Management.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5);
    }

    private void addDispenseOrder() {
        String patientID, doctorID, medID;
        int quantity;
        String date;

        // Patient ID validation loop
        do {
            System.out.print("Enter Patient ID: ");
            patientID = scanner.nextLine();
            if (!manager.isValidPatientID(patientID)) {
                System.out.println("Patient ID not found. Please try again or type 'exit' to cancel.");
                if (patientID.equalsIgnoreCase("exit")) return;
            }
        } while (!manager.isValidPatientID(patientID));

        // Doctor ID validation loop
        do {
            System.out.print("Enter Doctor ID: ");
            doctorID = scanner.nextLine();
            if (!manager.isValidDoctorID(doctorID)) {
                System.out.println("Doctor ID not found. Please try again or type 'exit' to cancel.");
                if (doctorID.equalsIgnoreCase("exit")) return;
            }
        } while (!manager.isValidDoctorID(doctorID));

        // Medicine ID validation loop
        Medicine selectedMedicine;
        do {
            System.out.print("Enter Medicine ID: ");
            medID = scanner.nextLine();
            selectedMedicine = manager.findMedicineByID(medID);
            if (selectedMedicine == null) {
                System.out.println("Medicine ID not found. Please try again or type 'exit' to cancel.");
                if (medID.equalsIgnoreCase("exit")) return;
            }
        } while (selectedMedicine == null);

        // Quantity validation loop
        do {
            System.out.print("Enter Quantity: ");
            quantity = scanner.nextInt();
            scanner.nextLine(); // consume newline
            if (quantity <= 0) {
                System.out.println("Quantity must be greater than 0. Please try again.");
            } else if (quantity > selectedMedicine.getStock()) {
                System.out.println("Quantity exceeds available stock (" + selectedMedicine.getStock() + "). Please enter a lower quantity.");
            }
        } while (quantity <= 0 || quantity > selectedMedicine.getStock());

        System.out.print("Enter Date (YYYY-MM-DD): ");
        date = scanner.nextLine();

        DispenseOrder order = new DispenseOrder(patientID, doctorID, medID, quantity, date);
        manager.addDispenseOrder(order);
    }

    private void processSelectedOrder() {
        ArrayList<DispenseOrder> orders = manager.getAllDispenseOrders();
        if (orders.isEmpty()) {
            System.out.println("No dispense orders to process.");
            return;
        }
        System.out.println("Existing Dispense Orders:");
        for (int i = 0; i < orders.size(); i++) {
            DispenseOrder order = orders.get(i);
            System.out.println((i + 1) + ". PatientID: " + order.getPatientID() +
                               ", DoctorID: " + order.getDoctorID() +
                               ", MedicineID: " + order.getMedID() +
                               ", Quantity: " + order.getQuantity() +
                               ", Date: " + order.getDate());
        }
        System.out.print("Select order number to process (or 0 to cancel): ");
        int selection = scanner.nextInt();
        scanner.nextLine(); // consume newline
        if (selection < 1 || selection > orders.size()) {
            System.out.println("Cancelled or invalid selection.");
            return;
        }
        manager.processOrderByIndex(selection - 1);
    }
}