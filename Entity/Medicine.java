package Entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Medicine implements Serializable {
    private String medID;
    private String name;
    private double price;
    private ArrayList<MedicineBatch> batches;

    public Medicine(String medID, String name, double price) {
        this.medID = medID;
        this.name = name;
        this.price = price;
        this.batches = new ArrayList<>();
    }

    public String getMedID() {
        return medID;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public ArrayList<MedicineBatch> getBatches() {
        return batches;
    }

    public void addBatch(MedicineBatch batch) {
        batches.add(batch);
    }

    // Get total stock from all batches
    public int getTotalStock() {
        int total = 0;
        for (MedicineBatch batch : batches) {
            total += batch.getStock();
        }
        return total;
    }

    // FEFO: Get batch with earliest expiry and available stock
    public MedicineBatch getEarliestBatch() {
        MedicineBatch earliest = null;
        for (MedicineBatch batch : batches) {
            if (batch.getStock() > 0) {
                if (earliest == null || batch.getExpiryDate().compareTo(earliest.getExpiryDate()) < 0) {
                    earliest = batch;
                }
            }
        }
        return earliest;
    }

    @Override
    public String toString() {
        return medID + "," + name + "," + price;
    }
}