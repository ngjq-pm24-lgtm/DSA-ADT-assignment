package Entity;

import java.io.Serializable;

public class Medicine implements Serializable {
    private String medID;
    private String name;
    private int stock;
    private double price;

    public Medicine(String medID, String name, int stock, double price) {
        this.medID = medID;
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    // Constructor for loading from text file (all fields as String)
    public Medicine(String[] fields) {
        this.medID = fields[0];
        this.name = fields[1];
        this.stock = Integer.parseInt(fields[2]);
        this.price = Double.parseDouble(fields[3]);
    }

    // Static method to parse a line from medicine.txt
    public static Medicine fromTextLine(String line) {
        String[] fields = line.split(",");
        return new Medicine(fields);
    }

    public String getMedID() {
        return medID;
    }

    public void setMedID(String medID) {
        this.medID = medID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return medID + "," + name + "," + stock + "," + price;
    }
}