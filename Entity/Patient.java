package Entity;


public class Patient {

    private String patientID;
    private String name;
    private String ICNo;
    private String gender;
    private int age;
    private String bloodType;
    private String dateOfBirth;
    private String patientNo;
    private String emergencyNo;
    private String medicalHistory;
    private String address;
    private String email;

    // Constructor
    public Patient(String patientID, String name, String ICNo, String gender, int age,
                   String bloodType, String dateOfBirth, String patientNo, String emergencyNo,
                   String medicalHistory, String address, String email) {
        this.patientID = patientID;
        this.name = name;
        this.ICNo = ICNo;
        this.gender = gender;
        this.age = age;
        this.bloodType = bloodType;
        this.dateOfBirth = dateOfBirth;
        this.patientNo = patientNo;
        this.emergencyNo = emergencyNo;
        this.medicalHistory = medicalHistory;
        this.address = address;
        this.email = email;
    }

    // Default constructor
    public Patient() {}

    // Getters and Setters
    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getICNo() {
        return ICNo;
    }

    public void setICNo(String ICNo) {
        this.ICNo = ICNo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPatientNo() {
        return patientNo;
    }

    public void setPatientNo(String patientNo) {
        this.patientNo = patientNo;
    }

    public String getEmergencyNo() {
        return emergencyNo;
    }

    public void setEmergencyNo(String emergencyNo) {
        this.emergencyNo = emergencyNo;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Utility Method
    @Override
    public String toString() {
        return "Patient ID: " + patientID + "\n" +
               "Name: " + name + "\n" +
               "IC No: " + ICNo + "\n" +
               "Gender: " + gender + "\n" +
               "Age: " + age + "\n" +
               "Blood Type: " + bloodType + "\n" +
               "Date of Birth: " + dateOfBirth + "\n" +
               "Patient No: " + patientNo + "\n" +
               "Emergency No: " + emergencyNo + "\n" +
               "Medical History: " + medicalHistory + "\n" +
               "Address: " + address + "\n" +
               "Email: " + email;
    }
}

