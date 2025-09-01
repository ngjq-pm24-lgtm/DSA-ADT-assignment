//JQ

package control;

import Entity.Medicine;
import Entity.DispenseOrder;
import Entity.Patient;
import Entity.Doctor;
import ADT.ArrayQueue;
import ADT.MapInterface;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;

public class PharmacyManager {
    private ArrayQueue<DispenseOrder> dispenseQueue;
    private Medicine[] medicines;
    private int medCount;

    public PharmacyManager() {
        dispenseQueue = new ArrayQueue<>(10);
        medicines = new Medicine[20];
        medCount = 0;
        preloadMedicines();
        loadDispenseOrdersFromFile(); // <-- Add this line
    }

    private void preloadMedicines() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/medicine.txt"))) {
            String line;
            while ((line = br.readLine()) != null && medCount < medicines.length) {
                // Each line: medID,name,stock,price
                String[] fields = line.split(",");
                if (fields.length == 4) {
                    Medicine med = new Medicine(fields[0], fields[1], Integer.parseInt(fields[2]), Double.parseDouble(fields[3]));
                    medicines[medCount++] = med;
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading medicines from file: " + e.getMessage());
        }
    }

    private void loadDispenseOrdersFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/dispenseorders.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 5) {
                    DispenseOrder order = new DispenseOrder(
                        fields[0], // patientID
                        fields[1], // doctorID
                        fields[2], // medID
                        Integer.parseInt(fields[3]), // quantity
                        fields[4] // date
                    );
                    if (!dispenseQueue.isFull()) {
                        dispenseQueue.enqueue(order);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("No existing dispense orders found.");
        }
    }

    public Medicine findMedicineByID(String medID) {
        for (int i = 0; i < medCount; i++) {
            if (medicines[i].getMedID().equals(medID)) {
                return medicines[i];
            }
        }
        return null;
    }

    // Patient validation using PatientControl's patientMap
    public boolean isValidPatientID(String patientID) {
        MapInterface<String, Patient> patientMap = control.PatientControl.getPatientMap();
        return patientMap != null && patientMap.contains(patientID);
    }

    // Doctor validation using DoctorManager's doctorRecords
    public boolean isValidDoctorID(String doctorID) {
        try {
            int id = Integer.parseInt(doctorID);
            MapInterface<Integer, Doctor> doctorRecords = control.DoctorManager.getDoctorRecords();
            return doctorRecords != null && doctorRecords.get(id) != null;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void addDispenseOrder(DispenseOrder order) {
        if (!isValidPatientID(order.getPatientID())) {
            System.out.println("Error: Patient ID not found (" + order.getPatientID() + ")");
            return;
        }
        if (!isValidDoctorID(order.getDoctorID())) {
            System.out.println("Error: Doctor ID not found (" + order.getDoctorID() + ")");
            return;
        }
        if (findMedicineByID(order.getMedID()) == null) {
            System.out.println("Error: Medicine ID not found (" + order.getMedID() + ")");
            return;
        }
        if (!dispenseQueue.isFull()) {
            dispenseQueue.enqueue(order);
            System.out.println("Dispense order added successfully.");
            // Save to file
            saveOrderToFile(order);
        } else {
            System.out.println("Dispense queue is full. Cannot add new order.");
        }
    }
//-----------------------------------
    private void saveOrderToFile(DispenseOrder order) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("data/dispenseorders.txt", true))) {
            pw.println(order.getPatientID() + "," +
                       order.getDoctorID() + "," +
                       order.getMedID() + "," +
                       order.getQuantity() + "," +
                       order.getDate());
        } catch (IOException e) {
            System.out.println("Error saving dispense order to file: " + e.getMessage());
        }
    }

    public boolean processNextOrder() {
        if (dispenseQueue.isEmpty()) {
            System.out.println("No orders to process.");
            return false;
        }
        DispenseOrder order = dispenseQueue.dequeue();

        if (!isValidPatientID(order.getPatientID())) {
            System.out.println("Error: Patient ID not found (" + order.getPatientID() + ")");
            return false;
        }
        if (!isValidDoctorID(order.getDoctorID())) {
            System.out.println("Error: Doctor ID not found (" + order.getDoctorID() + ")");
            return false;
        }
        Medicine med = findMedicineByID(order.getMedID());
        if (med == null) {
            System.out.println("Error: Medicine ID not found (" + order.getMedID() + ")");
            return false;
        }
        if (med.getStock() >= order.getQuantity()) {
            med.setStock(med.getStock() - order.getQuantity());
            System.out.println("Dispensed " + order.getQuantity() + " of " + med.getName() +
                " to " + order.getPatientID() + " by Doctor " + order.getDoctorID());
            saveMedicinesToFile(); // Save updated stock
             removeOrderFromFile(order);
            return true;
        } else {
            System.out.println("Insufficient stock for " + med.getName());
            return false;
        }
    }

    private void removeOrderFromFile(DispenseOrder processedOrder) {
    ArrayList<DispenseOrder> orders = new ArrayList<>();
    // Reload all orders except the processed one
    try (BufferedReader br = new BufferedReader(new FileReader("data/dispenseorders.txt"))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] fields = line.split(",");
            if (fields.length == 5) {
                DispenseOrder order = new DispenseOrder(
                    fields[0], fields[1], fields[2], Integer.parseInt(fields[3]), fields[4]
                );
                // Only add orders that are NOT the processed one
                if (!isSameOrder(order, processedOrder)) {
                    orders.add(order);
                }
            }
        }
    } catch (IOException e) {
        System.out.println("Error reading dispenseorders.txt: " + e.getMessage());
    }
    // Rewrite the file with remaining orders
    try (PrintWriter pw = new PrintWriter(new FileWriter("data/dispenseorders.txt"))) {
        for (DispenseOrder order : orders) {
            pw.println(order.getPatientID() + "," +
                       order.getDoctorID() + "," +
                       order.getMedID() + "," +
                       order.getQuantity() + "," +
                       order.getDate());
        }
    } catch (IOException e) {
        System.out.println("Error writing dispenseorders.txt: " + e.getMessage());
    }
}

// Helper to compare orders (by all fields)
private boolean isSameOrder(DispenseOrder a, DispenseOrder b) {
    return a.getPatientID().equals(b.getPatientID()) &&
           a.getDoctorID().equals(b.getDoctorID()) &&
           a.getMedID().equals(b.getMedID()) &&
           a.getQuantity() == b.getQuantity() &&
           a.getDate().equals(b.getDate());
}
//--------------------------------------
    public void generateStockReport() {
        System.out.println("=== Stock Report ===");
        System.out.printf("%-8s | %-20s | %-8s | %-10s%n", "MedID", "Name", "Stock", "Price (RM)");
        System.out.println("---------------------------------------------------------------");
        for (int i = 0; i < medCount; i++) {
            Medicine med = medicines[i];
            System.out.printf("%-8s | %-20s | %-8d | %-10.2f%n",
                med.getMedID(), med.getName(), med.getStock(), med.getPrice());
        }
        System.out.println("---------------------------------------------------------------");
    }
//--------------------------------------
    public void generateQueueReport() {
        System.out.println("=== Dispense Queue Report ===");
        System.out.printf("%-12s | %-20s | %-10s | %-8s | %-10s | %-12s | %-20s%n",
            "Patient ID", "Patient Name", "Doctor ID", "MedID", "Quantity", "Date", "Doctor Name");
        System.out.println("---------------------------------------------------------------------------------------------------------------");

        ArrayQueue<DispenseOrder> tempQueue = new ArrayQueue<>(dispenseQueue.size());
        MapInterface<String, Patient> patientMap = control.PatientControl.getPatientMap();
        MapInterface<Integer, Doctor> doctorRecords = control.DoctorManager.getDoctorRecords();

        while (!dispenseQueue.isEmpty()) {
            DispenseOrder order = dispenseQueue.dequeue();
            String patientName = "Unknown";
            if (patientMap != null && patientMap.contains(order.getPatientID())) {
                Patient patient = patientMap.get(order.getPatientID());
                patientName = patient.getName();
            }
            String doctorName = "Unknown";
            try {
                int docID = Integer.parseInt(order.getDoctorID());
                if (doctorRecords != null && doctorRecords.get(docID) != null) {
                    Doctor doctor = doctorRecords.get(docID);
                    doctorName = doctor.getName();
                }
            } catch (NumberFormatException e) {
                // doctorName remains "Unknown"
            }
            System.out.printf("%-12s | %-20s | %-10s | %-8s | %-10d | %-12s | %-20s%n",
                order.getPatientID(), patientName, order.getDoctorID(), order.getMedID(),
                order.getQuantity(), order.getDate(), doctorName);
            tempQueue.enqueue(order);
        }
        System.out.println("---------------------------------------------------------------------------------------------------------------");
        // Restore original queue
        while (!tempQueue.isEmpty()) {
            dispenseQueue.enqueue(tempQueue.dequeue());
        }
    }

    public ArrayList<DispenseOrder> getAllDispenseOrders() {
        ArrayList<DispenseOrder> list = new ArrayList<>();
        for (int i = 0; i < dispenseQueue.size(); i++) {
            list.add(dispenseQueue.get(i));
        }
        return list;
    }

    public void processOrderByIndex(int index) {
        if (index < 0 || index >= dispenseQueue.size()) {
            System.out.println("Invalid order index.");
            return;
        }
        DispenseOrder order = dispenseQueue.get(index);

        // Remove the selected order from the queue
        ArrayQueue<DispenseOrder> tempQueue = new ArrayQueue<>(dispenseQueue.size());
        for (int i = 0; i < dispenseQueue.size(); i++) {
            if (i != index) {
                tempQueue.enqueue(dispenseQueue.get(i));
            }
        }
        dispenseQueue.clear();
        for (int i = 0; i < tempQueue.size(); i++) {
            dispenseQueue.enqueue(tempQueue.get(i));
        }

        // Process the selected order
        if (!isValidPatientID(order.getPatientID())) {
            System.out.println("Error: Patient ID not found (" + order.getPatientID() + ")");
            return;
        }
        if (!isValidDoctorID(order.getDoctorID())) {
            System.out.println("Error: Doctor ID not found (" + order.getDoctorID() + ")");
            return;
        }
        Medicine med = findMedicineByID(order.getMedID());
        if (med == null) {
            System.out.println("Error: Medicine ID not found (" + order.getMedID() + ")");
            return;
        }
        if (med.getStock() >= order.getQuantity()) {
            med.setStock(med.getStock() - order.getQuantity());
            System.out.println("Dispensed " + order.getQuantity() + " of " + med.getName() +
                " to " + order.getPatientID() + " by Doctor " + order.getDoctorID());
            saveMedicinesToFile(); // Save updated stock
             removeOrderFromFile(order);
        } else {
            System.out.println("Insufficient stock for " + med.getName());
        }
    }
//------------------------------------------
    private void saveMedicinesToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("data/medicine.txt"))) {
            for (int i = 0; i < medCount; i++) {
                Medicine med = medicines[i];
                pw.println(med.getMedID() + "," + med.getName() + "," + med.getStock() + "," + med.getPrice());
            }
        } catch (IOException e) {
            System.out.println("Error saving medicines to file: " + e.getMessage());
        }
    }
}