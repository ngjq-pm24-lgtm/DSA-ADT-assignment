package dao;

import ADT.*;
import Entity.Doctor;
import java.io.*;


public class GenericDAO<K,V> {
    
    public boolean saveToFile(MapInterface<K, V> map, String fileName) {
        File file = new File(fileName);
        try {
          ObjectOutputStream ooStream = new ObjectOutputStream(new FileOutputStream(file));
          ooStream.writeObject(map);
          ooStream.close();
          return true;
        } catch (FileNotFoundException ex) {
          return false;
        } catch (IOException ex) {
          return false;
        }
    }
    
    
    public MapInterface<K, V> retrieveFromFile(String fileName) {
        File file = new File(fileName);
        MapInterface<K, V> map;
        try {
            ObjectInputStream oiStream = new ObjectInputStream(new FileInputStream(file));
            map = (HashMap<K, V>) (oiStream.readObject());
            oiStream.close();
            return map;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }
    
    public MapInterface<Integer,Doctor> loadDoctorsFromTextFile(String filename) {
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
    
}
