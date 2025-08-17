package control;

import ADT.ListInterface;
import ADT.QueueInterface;
import Entity.Consultation;
import Entity.Patient;
import Entity.Doctor;

public class ConsultationControl {
    private QueueInterface<Consultation> consultationQueue;
    private ListInterface<Consultation> consultationList;

    // Simulated patient and doctor lookup systems (you'll replace these with actual module calls)
    private ListInterface<Patient> patientList;
    private ListInterface<Doctor> doctorList;

    public ConsultationControl(QueueInterface<Consultation> queue, ListInterface<Consultation> list,
                               ListInterface<Patient> patientList, ListInterface<Doctor> doctorList) {
        this.consultationQueue = queue;
        this.consultationList = list;
        this.patientList = patientList;
        this.doctorList = doctorList;
    }

    // Create consultation using full Patient and Doctor objects from the shared ADT
    public Consultation createConsultation(String patientId, String doctorId,
                                           String date, String time, String reason) {
        Patient patient = findPatientById(patientId);
        Doctor doctor = findDoctorById(doctorId);

        if (patient == null || doctor == null) {
            System.out.println("Error: Patient or Doctor not found.");
            return null;
        }

        return new Consultation(patient, doctor, date, time, reason,
                "", "", ""); // diagnosis, prescription, notes default empty
    }

    // Add consultation to both queue and list
    public void addConsultation(Consultation consultation) {
        consultationQueue.enqueue(consultation);
        consultationList.add(consultation);
    }

    public void displayAllConsultationRecords() {
        if (consultationList.isEmpty()) {
            System.out.println("No consultation records found.");
            return;
        }

        System.out.println("=== All Consultation Records ===");
        for (int i = 0; i < consultationList.size(); i++) {
            System.out.println((i + 1) + ". " + consultationList.get(i));
        }
    }

    public Consultation cancelNextConsultation() {
        return consultationQueue.dequeue();
    }

    public Consultation peekNextConsultation() {
        return consultationQueue.peek();
    }

    public Consultation getConsultationByIndex(int index) {
        if (index < 0 || index >= consultationList.size()) return null;
        return consultationList.get(index);
    }

    public boolean isValidConsultationIndex(int index) {
        return index >= 0 && index < consultationList.size();
    }

    public int getConsultationCount() {
        return consultationList.size();
    }

    public void insertFollowUp(int index, Consultation consultation) {
        consultationList.add(index, consultation);
    }

    private Patient findPatientById(String patientId) {
        for (int i = 0; i < patientList.size(); i++) {
            Patient p = patientList.get(i);
            if (p.getPatientId().equals(patientId)) {
                return p;
            }
        }
        return null;
    }

    private Doctor findDoctorById(String doctorId) {
        for (int i = 0; i < doctorList.size(); i++) {
            Doctor d = doctorList.get(i);
            if (d.getDoctorId().equals(doctorId)) {
                return d;
            }
        }
        return null;
    }
}
