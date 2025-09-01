//JQ

package control;

import Entity.Medicine;
import Entity.DispenseOrder;
import Entity.Patient;
import Entity.Doctor;
import ADT.ArrayQueue;
import ADT.MapInterface;

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
            return true;
        } else {
            System.out.println("Insufficient stock for " + med.getName());
            return false;
        }
    }

    public void generateStockReport() {
        System.out.println("=== Stock Report ===");
        for (int i = 0; i < medCount; i++) {
            Medicine med = medicines[i];
            System.out.println(med.getMedID() + " | " + med.getName() + " | Stock: " +
                med.getStock() + " | Price: RM" + med.getPrice());
        }
    }

    public void generateQueueReport() {
        System.out.println("=== Dispense Queue Report ===");
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
            System.out.println(patientName + " (Patient ID: " + order.getPatientID() + ") waiting for " +
                order.getMedID() + " (" + order.getQuantity() + ") on " + order.getDate() +
                " prescribed by Dr. " + doctorName + " (Doctor ID: " + order.getDoctorID() + ")");
            tempQueue.enqueue(order);
        }
        // Restore original queue
        while (!tempQueue.isEmpty()) {
            dispenseQueue.enqueue(tempQueue.dequeue());
        }
    }

    
}