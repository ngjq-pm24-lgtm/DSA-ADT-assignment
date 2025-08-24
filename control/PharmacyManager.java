//JQ

package control;

import Entity.Medicine;
import Entity.DispenseOrder;
import ADT.ArrayQueue;

public class PharmacyManager {
    private ArrayQueue<DispenseOrder> dispenseQueue;
    private Medicine[] medicines;
    private int medCount;

    public PharmacyManager() {
        dispenseQueue = new ArrayQueue<>(10);
        medicines = new Medicine[10];
        medCount = 0;
        preloadMedicines();
    }

    private void preloadMedicines() {
        medicines[medCount++] = new Medicine("M001", "Panadol", 100, 2.00);
        medicines[medCount++] = new Medicine("M002", "Cough Syrup", 50, 5.00);
        // Add more medicines if needed
    }

    public void addDispenseOrder(DispenseOrder order) {
        if (!dispenseQueue.isFull()) {
            dispenseQueue.enqueue(order);
        } else {
            System.out.println("Dispense queue is full. Cannot add new order.");
        }
    }

    public boolean processNextOrder() {
        if (dispenseQueue.isEmpty()) {
            System.out.println("No orders to process.");
            return false;
        }
        DispenseOrder order = dispenseQueue.dequeue();
        Medicine med = findMedicineByID(order.getMedID());
        if (med == null) {
            System.out.println("Medicine not found.");
            return false;
        }
        if (med.getStock() >= order.getQuantity()) {
            med.setStock(med.getStock() - order.getQuantity());
            System.out.println("Dispensed " + order.getQuantity() + " of " + med.getName() + " to " + order.getPatientID());
            return true;
        } else {
            System.out.println("Insufficient stock for " + med.getName());
            return false;
        }
    }

    private Medicine findMedicineByID(String medID) {
        for (int i = 0; i < medCount; i++) {
            if (medicines[i].getMedID().equals(medID)) {
                return medicines[i];
            }
        }
        return null;
    }

    public void generateStockReport() {
        System.out.println("=== Stock Report ===");
        for (int i = 0; i < medCount; i++) {
            Medicine med = medicines[i];
            System.out.println(med.getMedID() + " | " + med.getName() + " | Stock: " + med.getStock() + " | Price: RM" + med.getPrice());
        }
    }

    public void generateQueueReport() {
        System.out.println("=== Dispense Queue Report ===");
        ArrayQueue<DispenseOrder> tempQueue = new ArrayQueue<>(dispenseQueue.size());
        while (!dispenseQueue.isEmpty()) {
            DispenseOrder order = dispenseQueue.dequeue();
            System.out.println(order.getPatientID() + " waiting for " + order.getMedID() + " (" + order.getQuantity() + ") on " + order.getDate());
            tempQueue.enqueue(order);
        }
        // Restore original queue
        while (!tempQueue.isEmpty()) {
            dispenseQueue.enqueue(tempQueue.dequeue());
        }
    }
}