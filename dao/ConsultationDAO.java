package dao;

import ADT.*;
import java.io.*;
import java.util.UUID;

public class ConsultationDAO<K, V> extends GenericDAO<K, V> {
    private static final String CONSULTATION_FILE = "data/consultations.dat";
    
    /**
     * Saves consultations to file
     * @param consultations Map of consultations to save
     * @return true if successful, false otherwise
     */
    public boolean saveConsultations(MapInterface<K, V> consultations) {
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdir();
        }
        return saveToFile(consultations, CONSULTATION_FILE);
    }
    
    /**
     * Loads consultations from file
     * @return Map of consultations, or null if file not found
     */
    @SuppressWarnings("unchecked")
    public MapInterface<K, V> loadConsultations() {
        MapInterface<K, V> consultations = retrieveFromFile(CONSULTATION_FILE);
        // Create a new instance of the map implementation being used
        return consultations != null ? consultations : (MapInterface<K, V>) new MyMap<>();
    }
    
    /**
     * Generates a unique consultation ID
     * @return A new unique ID
     */
    public String generateConsultationId() {
        return "CONS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
