package Entity;

import java.io.Serializable;

public class MedicineBatch implements Serializable {
    private String batchID;
    private int stock;
    private String expiryDate; // Format: YYYY-MM-DD
    private double unitCost;

    public MedicineBatch(String batchID, int stock, String expiryDate, double unitCost) {
        this.batchID = batchID;
        this.stock = stock;
        this.expiryDate = expiryDate;
        this.unitCost = unitCost;
    }

    public String getBatchID() {
        return batchID;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public double getUnitCost() {
        return unitCost;
    }

    @Override
    public String toString() {
        return batchID + "," + stock + "," + expiryDate + "," + unitCost;
    }
}
