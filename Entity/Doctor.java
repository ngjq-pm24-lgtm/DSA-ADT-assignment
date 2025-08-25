package Entity;

import java.io.Serializable;

public class Doctor implements Serializable, Comparable<Doctor> {
    private static final long serialVersionUID = 1L;
    private int doctorID;
    private String name;
    private String phoneNumber;
    private boolean isAvailable;

    public Doctor(int doctorID, String name, String phoneNumber) {
        this.doctorID = doctorID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.isAvailable = false;
    }

    public int getDoctorID() {
        return doctorID;
    }

    public String getName() {
        return name;
    }

    public boolean getAvailability(){
        return isAvailable;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }
    
    public void setName(String name){
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber){
       this.phoneNumber = phoneNumber;
    }

    public void setAvailability(boolean isAvailable){
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return "Doctor ID: " + doctorID + "\n" +
        "Name: " + name + "\n" + "Currently available: " + isAvailable;
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



