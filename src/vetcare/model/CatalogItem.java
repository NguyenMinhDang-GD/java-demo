package vetcare.model;

import java.io.Serializable;

public class CatalogItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String category; // SERVICE, MEDICINE, VACCINE
    private double price;
    private String unit;
    private int boosterCycleDays;

    public CatalogItem(int id, String name, String category, double price, String unit, int boosterCycleDays) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.unit = unit;
        this.boosterCycleDays = boosterCycleDays;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public int getBoosterCycleDays() { return boosterCycleDays; }
    public void setBoosterCycleDays(int boosterCycleDays) { this.boosterCycleDays = boosterCycleDays; }

    @Override
    public String toString() {
        if ("VACCINE".equals(category)) {
            return name + " ($" + (int) price + ", " + boosterCycleDays + "d cycle)";
        }
        return name + " ($" + (int) price + "/" + unit + ")";
    }
}
