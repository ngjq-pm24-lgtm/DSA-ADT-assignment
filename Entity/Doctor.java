package Entity;

import java.io.Serializable;

public class Doctor implements Serializable, Comparable<Doctor> {
    private static final long serialVersionUID = 1L;
    private final int doctorID;
    private String name;
    private String phoneNumber;
    private String gender;
    private String email;
    private String position;
    private String qualification;

    public Doctor(int doctorID, String name, String phoneNumber, String gender, String email, String position, String qualification) {
        this.doctorID = doctorID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.email = email;
        this.position = position;
        this.qualification = qualification;
    }

    // Getters
    public int getDoctorID() {
        return doctorID;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getPosition() {
        return position;
    }

    public String getQualification() {
        return qualification;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }


    @Override
    public String toString() {
        return "Doctor ID: " + doctorID +
        "\nName: " + name +
        "\nPhone No.: " + phoneNumber +
        "\nGender: " + gender +
        "\nEmail: " + email +
        "\nPosition: " + position +
        "\nQualification: " + qualification;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.doctorID;
        return hash;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;

        if(!(o instanceof Doctor))
            return false;

        Doctor x = (Doctor)o;
        return this.doctorID == x.doctorID;
    }

    @Override
    public int compareTo(Doctor o) {
        return Integer.compare(this.doctorID, o.doctorID);
    }
}



