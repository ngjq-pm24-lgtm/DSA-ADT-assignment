package dao;

import ADT.*;
import Entity.Doctor;
import Entity.Patient;
import Entity.TimeSlotResetFlag;
import java.io.*;


public class GenericDAO<K,V> {
    
    public static <T> boolean saveToFile(T dataToSave, String fileName) {
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdir();
        }
        
        File file = new File(fileName);
        try {
            ObjectOutputStream ooStream = new ObjectOutputStream(new FileOutputStream(file));
            ooStream.writeObject(dataToSave);
            ooStream.close();
            return true;
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }
    }
    
    public static <T> T retrieveFromFile(String fileName) {
        File file = new File(fileName);
        try{
            ObjectInputStream oiStream = new ObjectInputStream(new FileInputStream(file));
            return (T) oiStream.readObject();
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    public static boolean loadPatientsFromTextFile(String filename, MapInterface<String,Patient> patientMap, QueueInterface<Patient> patientQueue){

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                // skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 13) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String ic = parts[2].trim();
                    String gender = parts[3].trim();
                    int age = Integer.parseInt(parts[4].trim());
                    String bloodType = parts[5].trim();
                    String dob = parts[6].trim();
                    String contactNo = parts[7].trim();
                    String emergencyNo = parts[8].trim();
                    String medicalHistory = parts[9].trim();
                    String address = parts[10].trim();
                    String email = parts[11].trim();
                    String course = parts[12].trim();
                    
                    Patient patient = new Patient(id, name, ic, gender, age, bloodType, dob, contactNo,
                                  emergencyNo, medicalHistory, address, email, course);
                    patientMap.add(id, patient);
                    patientQueue.enqueue(patient);
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public static MapInterface<Integer,Doctor> loadDoctorsFromTextFile(String filename) {
        MapInterface<Integer,Doctor> doctorRecords = new HashMap<>(30);
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                // skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    int doctorID = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String phone = parts[2].trim();

                    Doctor doctor = new Doctor(doctorID, name, phone);
                    doctorRecords.add(doctorID, doctor);
                }
            }
            return doctorRecords;
        } catch (IOException e) {
            return null;
        }
    }
    
    public static boolean saveTimeSlotResetFlag(TimeSlotResetFlag flag) {
        File file = new File("data/timeslotFlag.dat");
        try {
            ObjectOutputStream ooStream = new ObjectOutputStream(new FileOutputStream(file));
            ooStream.writeObject(flag);
            ooStream.close();
            return true;
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException ex) {
            return false;
}
    }

    public static TimeSlotResetFlag getTimeSlotResetFlagFromFile() {
        File file = new File("data/timeslotFlag.dat");
        TimeSlotResetFlag flag;
        try {
            ObjectInputStream oiStream = new ObjectInputStream(new FileInputStream(file));
            flag = (TimeSlotResetFlag) (oiStream.readObject());
            oiStream.close();
            return flag;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }
    
}
