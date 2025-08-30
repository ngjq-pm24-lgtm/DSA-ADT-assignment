//JQ
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


    //getters and setters
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
}