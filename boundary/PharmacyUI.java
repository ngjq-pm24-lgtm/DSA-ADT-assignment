//JQ
package boundary;

import control.PharmacyManager;
import Entity.DispenseOrder;
import java.util.Scanner;

public class PharmacyUI {
    private PharmacyManager manager;
    private Scanner scanner;

    public PharmacyUI() {
        manager = new PharmacyManager();
        scanner = new Scanner(System.in);
    }

    public void start() {
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
                    manager.processNextOrder();
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
        System.out.print("Enter Patient ID: ");
        String patientID = scanner.nextLine();
        System.out.print("Enter Medicine ID: ");
        String medID = scanner.nextLine();
        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        DispenseOrder order = new DispenseOrder(patientID, medID, quantity, date);
        manager.addDispenseOrder(order);
    }
}