//JQ

package control;

import Entity.Medicine;
import Entity.MedicineBatch;
import Entity.DispenseOrder;
import Entity.Patient;
import Entity.Doctor;
import ADT.ArrayQueue;
import ADT.MapInterface;
import java.util.ArrayList;
import java.io.*;
import java.time.LocalDate;

public class PharmacyManager {
    private ArrayQueue<DispenseOrder> dispenseQueue;
    private ArrayList<Medicine> medicines;

    public PharmacyManager() {
        dispenseQueue = new ArrayQueue<>(10);
        medicines = new ArrayList<>();
        preloadMedicines();
        loadDispenseOrdersFromFile();
    }

    private void preloadMedicines() {
        // Load medicine main info
        try (BufferedReader br = new BufferedReader(new FileReader("data/medicine.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 3) {
                    Medicine med = new Medicine(fields[0], fields[1], Double.parseDouble(fields[2]));
                    medicines.add(med);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading medicines from file: " + e.getMessage());
        }

        // Load batches
        try (BufferedReader br = new BufferedReader(new FileReader("data/medicinebatch.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 5) {
                    String medID = fields[0];
                    Medicine med = findMedicineByID(medID);
                    if (med != null) {
                        MedicineBatch batch = new MedicineBatch(fields[1], Integer.parseInt(fields[2]), fields[3], Double.parseDouble(fields[4]));
                        med.addBatch(batch);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading medicine batches from file: " + e.getMessage());
        }
    }

    private void loadDispenseOrdersFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/dispenseorders.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 5) {
                    DispenseOrder order = new DispenseOrder(
                        fields[0], fields[1], fields[2], Integer.parseInt(fields[3]), fields[4]
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
        for (Medicine med : medicines) {
            if (med.getMedID().equals(medID)) {
                return med;
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
            saveOrderToFile(order);
        } else {
            System.out.println("Dispense queue is full. Cannot add new order.");
        }
    }

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

    // FEFO dispensing
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
        int quantity = order.getQuantity();
        while (quantity > 0) {
            MedicineBatch batch = med.getEarliestBatch();
            if (batch == null || batch.getStock() == 0) {
                System.out.println("Insufficient stock for " + med.getName());
                return false;
            }
            int deduct = Math.min(batch.getStock(), quantity);
            batch.setStock(batch.getStock() - deduct);
            quantity -= deduct;
            System.out.println("Dispensed " + deduct + " of " + med.getName() + " (Batch: " + batch.getBatchID() + ", Expiry: " + batch.getExpiryDate() + ")");
        }
        saveMedicinesToFile();
        removeOrderFromFile(order);
        return true;
    }

    private void removeOrderFromFile(DispenseOrder processedOrder) {
        ArrayList<DispenseOrder> orders = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/dispenseorders.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 5) {
                    DispenseOrder order = new DispenseOrder(
                        fields[0], fields[1], fields[2], Integer.parseInt(fields[3]), fields[4]
                    );
                    if (!isSameOrder(order, processedOrder)) {
                        orders.add(order);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading dispenseorders.txt: " + e.getMessage());
        }
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

    private boolean isSameOrder(DispenseOrder a, DispenseOrder b) {
        return a.getPatientID().equals(b.getPatientID()) &&
               a.getDoctorID().equals(b.getDoctorID()) &&
               a.getMedID().equals(b.getMedID()) &&
               a.getQuantity() == b.getQuantity() &&
               a.getDate().equals(b.getDate());
    }

  public void generateStockReport() {
    System.out.println("\n=== Stock Report ===");
    System.out.printf("%-8s | %-20s | %-10s | %-10s\n", "MedID", "Name", "TotalStock", "Price (RM)");
    System.out.println("--------------------------------------------------------------------------");
    for (Medicine med : medicines) {
        System.out.printf("%-8s | %-20s | %-10d | %-10.2f\n",
            med.getMedID(), med.getName(), med.getTotalStock(), med.getPrice());
        if (med.getBatches().isEmpty()) {
            System.out.println("    No batches available.");
        } else {
            System.out.println("    ------------------------------------------------------------------");
            System.out.printf("    %-8s | %-6s | %-12s | %-8s\n", "BatchID", "Stock", "Expiry Date", "UnitCost");
            for (MedicineBatch batch : med.getBatches()) {
                System.out.printf("    %-8s | %-6d | %-12s | %-8.2f\n",
                    batch.getBatchID(), batch.getStock(), batch.getExpiryDate(), batch.getUnitCost());
            }
            System.out.println("    ------------------------------------------------------------------");
        }
        System.out.println(); // Extra line for spacing between medicines
    }
    System.out.println("--------------------------------------------------------------------------");
}

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

        // FEFO dispensing for selected order
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
        int quantity = order.getQuantity();
        while (quantity > 0) {
            MedicineBatch batch = med.getEarliestBatch();
            if (batch == null || batch.getStock() == 0) {
                System.out.println("Insufficient stock for " + med.getName());
                return;
            }
            int deduct = Math.min(batch.getStock(), quantity);
            batch.setStock(batch.getStock() - deduct);
            quantity -= deduct;
            System.out.println("Dispensed " + deduct + " of " + med.getName() + " (Batch: " + batch.getBatchID() + ", Expiry: " + batch.getExpiryDate() + ")");
        }
        saveMedicinesToFile();
        removeOrderFromFile(order);
    }

    private void saveMedicinesToFile() {
        // Save medicine main info
        try (PrintWriter pw = new PrintWriter(new FileWriter("data/medicine.txt"))) {
            for (Medicine med : medicines) {
                pw.println(med.getMedID() + "," + med.getName() + "," + med.getPrice());
            }
        } catch (IOException e) {
            System.out.println("Error saving medicines to file: " + e.getMessage());
        }
        // Save batches
        try (PrintWriter pw = new PrintWriter(new FileWriter("data/medicinebatch.txt"))) {
            for (Medicine med : medicines) {
                for (MedicineBatch batch : med.getBatches()) {
                    pw.println(med.getMedID() + "," + batch.getBatchID() + "," + batch.getStock() + "," + batch.getExpiryDate() + "," + batch.getUnitCost());
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving medicine batches to file: " + e.getMessage());
        }
    }

    // Add new medicine (no batches yet)
    public void addNewMedicine(Medicine med) {
        medicines.add(med);
        saveMedicinesToFile();
    }

    // Add new batch to existing medicine
    public void addNewBatch(String medID, String batchID, int quantity, String expiryDate, double unitCost) {
        Medicine med = findMedicineByID(medID);
        if (med != null) {
            MedicineBatch batch = new MedicineBatch(batchID, quantity, expiryDate, unitCost);
            med.addBatch(batch);
            saveMedicinesToFile();
        }
    }

    // Remove medicine (all batches)
    public void removeMedicine(String medID) {
        Medicine med = findMedicineByID(medID);
        if (med != null) {
            medicines.remove(med);
            saveMedicinesToFile();
        }
    }

    // Expired batch management
    public void viewExpiredBatches() {
        System.out.println("=== Expired Medicine Batches ===");
        String today = LocalDate.now().toString();
        for (Medicine med : medicines) {
            for (MedicineBatch batch : med.getBatches()) {
                if (batch.getExpiryDate().compareTo(today) < 0 && batch.getStock() > 0) {
                    System.out.printf("MedID: %-8s | BatchID: %-8s | Stock: %-6d | Expiry: %-12s | UnitCost: %-8.2f%n",
                        med.getMedID(), batch.getBatchID(), batch.getStock(), batch.getExpiryDate(), batch.getUnitCost());
                }
            }
        }
    }

    public void removeExpiredBatches() {
        String today = LocalDate.now().toString();
        for (Medicine med : medicines) {
            ArrayList<MedicineBatch> toRemove = new ArrayList<>();
            for (MedicineBatch batch : med.getBatches()) {
                if (batch.getExpiryDate().compareTo(today) < 0) {
                    toRemove.add(batch);
                }
            }
            med.getBatches().removeAll(toRemove);
        }
        saveMedicinesToFile();
        System.out.println("Expired batches removed.");
    }
    public ArrayList<Medicine> getMedicines() {
    return medicines;
}
}