
package Entity;

import java.io.Serializable;

public class Treatment implements Serializable {
	private String treatmentID;
    private String patientID;
    private String doctorID;
    private String diseaseName;
    private String diseaseDesc;
    private String severity;
    private String timeOfTreatment;  
    private String treatmentProvided;

public Treatment (String treatmentID, String patientID, String doctorID, String diseaseName, String diseaseDesc, String severity, String timeOfTreatment, String treatmentProvided) {
	this.treatmentID = treatmentID;
	this.patientID = patientID;
	this.doctorID = doctorID;
	this.diseaseName = diseaseName;
	this.diseaseDesc = diseaseDesc;
	this.severity = severity;
	this.timeOfTreatment = timeOfTreatment;
        this.treatmentProvided = treatmentProvided;

}

public Treatment() {
 	this("","","","","","","","");
}

public String getTreatmentID(){
	return treatmentID;
}

public String getPatientID(){
	return patientID;
}

public String getDoctorID(){
	return doctorID;
}

public String getDiseaseName(){
	return diseaseName;
}
public String getDiseaseDesc(){
	return diseaseDesc;
}

public String getSeverity(){
	return severity;
}

public String getTimeOfTreatment(){
	return timeOfTreatment;
}

public String getTreatmentProvided(){
	return treatmentProvided;
}

public void setTreatmentID(String treatmentID){
	this.treatmentID = treatmentID;
}

public void setPatientID(String patientID){
	this.patientID = patientID;
}

public void setDoctorID(String doctorID){
	this.doctorID = doctorID;
}

public void setDiseaseName(String diseaseName){
	this.diseaseName = diseaseName;
}
public void setDiseaseDesc(String diseaseDesc){
	this.diseaseDesc = diseaseDesc;
}

public void setSeverity(String severity){
	this.severity = severity;
}

public void setTimeOfTreatment(String timeOfTreatment){
	this.timeOfTreatment = timeOfTreatment;
}

public void setTreatmentProvided(String treatmentProvided){
	this.treatmentProvided = treatmentProvided;
}

public String toString() {
	return "Treatment ID: " + treatmentID + "/nPatient ID: " + patientID + "/nDoctor in Charge: " + doctorID
                + "/nDisease: " + diseaseName + "/nTreatment Provided" + treatmentProvided 
                + "/nTime Administered:"  + timeOfTreatment;

    }
}
