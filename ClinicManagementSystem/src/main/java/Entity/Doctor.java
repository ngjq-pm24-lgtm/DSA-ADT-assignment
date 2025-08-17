
package Entity;

public class Doctor {
    private int doctorID;
    private String name;
    private String phoneNumber;
    private boolean isAvailable;
    private static int nextDoctorID;

    public Doctor(String name, String phoneNumber) {
        this.doctorID = nextDoctorID++;
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

    public void setPhoneNumber(String phoneNumber){
       this.phoneNumber = phoneNumber;
    }

    public void setAvailability(boolean isAvailable){
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return "Doctor ID: " + doctorID + "\n" +
                "Name: " + name + "\n" +"Currently available: " + isAvailable;

    }
}


