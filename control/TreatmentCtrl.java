/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ADT;

/**
 *
 * @author Khoo Sheng Hao
 */
public class TreatmentCtrl {


    private TreatmentMap<String, TreatmentList<Treatment>> patientTreatments;

    public TreatmentCtrl() {
        patientTreatments = new TreatmentMap<>();
    }

    // Add treatment information for a disease
    public void addTreatment(String diseaseName, Treatment treatmentDesc) {
        TreatmentList<Treatment> treatments;
        if (patientTreatments.containsKey(diseaseName)) {
            treatments = patientTreatments.get(diseaseName);
        } else {
            treatments = new TreatmentList<>();
        }
        treatments.add(treatmentDesc);
        patientTreatments.put(diseaseName, treatments);
    }

    // Remove the treatment information
    public boolean removeTreatment(String diseaseName) {
    if (!patientTreatments.containsKey(diseaseName)) {
        return false;  // no treatment with this disease name
    }
    patientTreatments.remove(diseaseName);  // removes the entire entry
    return true;
}


    // Get treatment information
    public TreatmentList<Treatment> getTreatmentDesc(String diseaseName) {
        return patientTreatments.get(diseaseName);
    }

    // Summary report of all Treatments
    public void printAllTreatmentsSummary() {
        System.out.println("=== All Diseases & Treatment Types Summary  ===");
        patientTreatments.printAll(); // 
    }

    // Other helper methods
    public boolean hasTreatment(String diseaseName) {
        return patientTreatments.containsKey(diseaseName);
    }

    public int getTotalTreatments() {
        return patientTreatments.size();
    }
}


