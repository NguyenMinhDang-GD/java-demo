package vetcare.model;

import java.io.Serializable;

public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int petId;
    private int vetId;
    private int serviceId;
    private String date; // YYYY-MM-DD
    private String timeSlot;
    private String status; // Scheduled, Checked-in, Completed, Cancelled
    private double weightKg;
    private double temperatureC;
    private String reason;

    public Appointment(int id, int petId, int vetId, int serviceId, String date, String timeSlot, String status, double weightKg, double temperatureC, String reason) {
        this.id = id;
        this.petId = petId;
        this.vetId = vetId;
        this.serviceId = serviceId;
        this.date = date;
        this.timeSlot = timeSlot;
        this.status = status;
        this.weightKg = weightKg;
        this.temperatureC = temperatureC;
        this.reason = reason;
    }

    public int getId() { return id; }
    public int getPetId() { return petId; }
    public int getVetId() { return vetId; }
    public int getServiceId() { return serviceId; }
    public String getDate() { return date; }
    public String getTimeSlot() { return timeSlot; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    public double getTemperatureC() { return temperatureC; }
    public void setTemperatureC(double temperatureC) { this.temperatureC = temperatureC; }
    public String getReason() { return reason; }
}
