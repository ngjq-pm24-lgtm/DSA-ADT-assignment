//JQ

package Entity;
public class DispenseOrder {
    private String patientID;
    private String medID;
    private int quantity;
    private String date;

    public DispenseOrder(String patientID, String medID, int quantity, String date) {
        this.patientID = patientID;
        this.medID = medID;
        this.quantity = quantity;
        this.date = date;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getMedID() {
        return medID;
    }

    public void setMedID(String medID) {
        this.medID = medID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}