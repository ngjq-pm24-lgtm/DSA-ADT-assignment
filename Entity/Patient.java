package Entity;

import java.io.Serializable;


public class Patient implements Serializable {

    private String patientId;
    private String name;
    private String ICNo;
    private String gender;
    private int age;
    private String bloodType;
    private String dateOfBirth;
    private String contactNo;
    private String emergencyNo;
    private String medicalHistory;
    private String address;
    private String email;
    private String course;

    // Constructor
    public Patient(String patientId, String name, String ICNo, String gender, int age,
                   String bloodType, String dateOfBirth, String contactNo, String emergencyNo,
                   String medicalHistory, String address, String email, String course) {
        this.patientId = patientId;
        this.name = name;
        this.ICNo = ICNo;
        this.gender = gender;
        this.age = age;
        this.bloodType = bloodType;
        this.dateOfBirth = dateOfBirth;
        this.contactNo = contactNo;
        this.emergencyNo = emergencyNo;
        this.medicalHistory = medicalHistory;
        this.address = address;
        this.email = email;
        this.course = course;
    }

    // Default constructor
    public Patient() {}

    // Getters and Setters
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
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
    
    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
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
    
    public String getCourse(){
        return course;
    }
    
    public void setCourse(String course){
        this.course = course;
    }

    // Utility Method
    @Override
    public String toString() {
        return "Patient ID: " + patientId + "\n" +
               "Name: " + name + "\n" +
               "IC No: " + ICNo + "\n" +
               "Gender: " + gender + "\n" +
               "Age: " + age + "\n" +
               "Blood Type: " + bloodType + "\n" +
               "Date of Birth: " + dateOfBirth + "\n" +
               "Patient No: " + contactNo + "\n" +
               "Emergency No: " + emergencyNo + "\n" +
               "Medical History: " + medicalHistory + "\n" +
               "Address: " + address + "\n" +
               "Email: " + email + "\n" +
               "Course: " + course;
               
    }
}

