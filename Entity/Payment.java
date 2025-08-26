package Entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Payment {
    private String paymentId;
    private String patientId;
    private String paymentMethod;
    private double amount;
    private String date;   // YYYY-MM-DD
    private String time;   // HH:MM (24-hour format)

    public Payment(String paymentId, String patientId, String paymentMethod, double amount) {
        this.paymentId = paymentId;
        this.patientId = patientId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;

        // ✅ Auto-generate current date & time
        LocalDateTime now = LocalDateTime.now();
        this.date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.time = now.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // Getters
    public String getPaymentId() { return paymentId; }
    public String getPatientId() { return patientId; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public String getTime() { return time; }

    // Setters if you still want them (optional)
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setAmount(double amount) { this.amount = amount; }

    // No setters for date/time → always generated automatically

    @Override
    public String toString() {
        return "Payment ID: " + paymentId +
               "\nPatient ID: " + patientId +
               "\nPayment Method: " + paymentMethod +
               "\nAmount: RM" + amount +
               "\nDate: " + date +
               "\nTime: " + time;
    }
}
