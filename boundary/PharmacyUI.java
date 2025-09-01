package boundary;

import control.PharmacyManager;
import Entity.DispenseOrder;
import Entity.Medicine;
import Entity.MedicineBatch;
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
            System.out.println("5. Medicine Management");
            System.out.println("6. Expired Medicine Management");
            System.out.println("7. Exit");
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
                    medicineManagementMenu();
                    break;
                case 6:
                    expiredMedicineMenu();
                    break;
                case 7:
                    System.out.println("Exiting Pharmacy Management.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 7);
    }

    private void medicineManagementMenu() {
        int choice;
        do {
            System.out.println("\n--- Medicine Management ---");
            System.out.println("1. Add New Medicine");
            System.out.println("2. Add New Batch (Restock)");
            System.out.println("3. Remove Medicine");
            System.out.println("4. List Medicines (Sort)");
            System.out.println("5. Back");
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addNewMedicine();
                    break;
                case 2:
                    addNewBatch();
                    break;
                case 3:
                    removeMedicine();
                    break;
                case 4:
                    listMedicinesSorted();
                    break;
                case 5:
                    System.out.println("Returning to Pharmacy Menu.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5);
    }

    private void expiredMedicineMenu() {
        int choice;
        do {
            System.out.println("\n--- Expired Medicine Management ---");
            System.out.println("1. View Expired Batches");
            System.out.println("2. Remove Expired Batches");
            System.out.println("3. Back");
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    manager.viewExpiredBatches();
                    break;
                case 2:
                    manager.removeExpiredBatches();
                    break;
                case 3:
                    System.out.println("Returning to Pharmacy Menu.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);
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

        // Quantity validation loop (use total stock from batches)
        do {
            System.out.print("Enter Quantity: ");
            quantity = scanner.nextInt();
            scanner.nextLine(); // consume newline
            if (quantity <= 0) {
                System.out.println("Quantity must be greater than 0. Please try again.");
            } else if (quantity > selectedMedicine.getTotalStock()) {
                System.out.println("Quantity exceeds available stock (" + selectedMedicine.getTotalStock() + "). Please enter a lower quantity.");
            }
        } while (quantity <= 0 || quantity > selectedMedicine.getTotalStock());

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

    private void addNewMedicine() {
        System.out.print("Enter Medicine ID: ");
        String medID = scanner.nextLine();
        if (manager.findMedicineByID(medID) != null) {
            System.out.println("Medicine ID already exists.");
            return;
        }
        System.out.print("Enter Medicine Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        Medicine med = new Medicine(medID, name, price);
        manager.addNewMedicine(med);
        System.out.println("New medicine added successfully.");
    }

    private void addNewBatch() {
        System.out.print("Enter Medicine ID for batch: ");
        String medID = scanner.nextLine();
        Medicine med = manager.findMedicineByID(medID);
        if (med == null) {
            System.out.println("Medicine ID not found.");
            return;
        }
        System.out.print("Enter Batch ID: ");
        String batchID = scanner.nextLine();
        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Expiry Date (YYYY-MM-DD): ");
        String expiryDate = scanner.nextLine();
        System.out.print("Enter Unit Cost: ");
        double unitCost = scanner.nextDouble();
        scanner.nextLine();

        manager.addNewBatch(medID, batchID, quantity, expiryDate, unitCost);
        System.out.println("New batch added successfully.");
    }

    private void removeMedicine() {
        System.out.print("Enter Medicine ID to remove: ");
        String medID = scanner.nextLine();
        if (manager.findMedicineByID(medID) == null) {
            System.out.println("Medicine ID not found.");
            return;
        }
        manager.removeMedicine(medID);
        System.out.println("Medicine removed successfully.");
    }

private void listMedicinesSorted() {
    System.out.println("\nList Medicines - Sort Options:");
    System.out.println("1. Sort by Earliest Expiry Date");
    System.out.println("2. Sort by Total Stock (Descending)");
    System.out.print("Enter choice: ");
    int sortChoice = scanner.nextInt();
    scanner.nextLine();

    ArrayList<Medicine> meds = manager.getMedicines();
    if (sortChoice == 1) {
        meds.sort((m1, m2) -> {
            MedicineBatch b1 = m1.getEarliestBatch();
            MedicineBatch b2 = m2.getEarliestBatch();
            if (b1 == null && b2 == null) return 0;
            if (b1 == null) return 1;
            if (b2 == null) return -1;
            return b1.getExpiryDate().compareTo(b2.getExpiryDate());
        });
        System.out.println("=== Medicines Sorted by Earliest Expiry Date ===");
        System.out.printf("%-10s | %-15s | %-12s | %-12s | %-18s | %-10s | %-8s | %-16s | %-10s\n",
            "MedID", "Name", "TotalStock", "Price(RM)", "Earliest Expiry", "BatchID", "Stock", "Expiry Date", "UnitCost");
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
        for (Medicine med : meds) {
            MedicineBatch earliest = med.getEarliestBatch();
            String expiry = (earliest != null) ? earliest.getExpiryDate() : "N/A";
            if (med.getBatches().isEmpty()) {
                System.out.printf("%-10s | %-15s | %-12d | %-12.2f | %-18s | %-10s | %-8s | %-16s | %-10s\n",
                    med.getMedID(), med.getName(), med.getTotalStock(), med.getPrice(), expiry, "-", "-", "-", "-");
            } else {
                for (MedicineBatch batch : med.getBatches()) {
                    System.out.printf("%-10s | %-15s | %-12d | %-12.2f | %-18s | %-10s | %-8d | %-16s | %-10.2f\n",
                        med.getMedID(), med.getName(), med.getTotalStock(), med.getPrice(), expiry,
                        batch.getBatchID(), batch.getStock(), batch.getExpiryDate(), batch.getUnitCost());
                }
            }
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
    } else if (sortChoice == 2) {
        meds.sort((m1, m2) -> Integer.compare(m2.getTotalStock(), m1.getTotalStock()));
        System.out.println("=== Medicines Sorted by Total Stock (Descending) ===");
        System.out.printf("%-10s | %-15s | %-12s | %-12s | %-10s | %-8s | %-16s | %-10s\n",
            "MedID", "Name", "TotalStock", "Price(RM)", "BatchID", "Stock", "Expiry Date", "UnitCost");
        System.out.println("---------------------------------------------------------------------------------------------------------------");
        for (Medicine med : meds) {
            if (med.getBatches().isEmpty()) {
                System.out.printf("%-10s | %-15s | %-12d | %-12.2f | %-10s | %-8s | %-16s | %-10s\n",
                    med.getMedID(), med.getName(), med.getTotalStock(), med.getPrice(), "-", "-", "-", "-");
            } else {
                for (MedicineBatch batch : med.getBatches()) {
                    System.out.printf("%-10s | %-15s | %-12d | %-12.2f | %-10s | %-8d | %-16s | %-10.2f\n",
                        med.getMedID(), med.getName(), med.getTotalStock(), med.getPrice(),
                        batch.getBatchID(), batch.getStock(), batch.getExpiryDate(), batch.getUnitCost());
                }
            }
        }
        System.out.println("---------------------------------------------------------------------------------------------------------------");
    } else {
        System.out.println("Invalid sort choice.");
    }
}
}