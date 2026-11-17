package vetcare.model;

import java.io.Serializable;

public class Vaccination implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int appointmentId;
    private int petId;
    private int vetId;
    private int vaccineItemId;
    private String dateAdministered;
    private String nextBoosterDate;
    private boolean reminderSent;

    public Vaccination(int id, int appointmentId, int petId, int vetId, int vaccineItemId,
                       String dateAdministered, String nextBoosterDate, boolean reminderSent) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.petId = petId;
        this.vetId = vetId;
        this.vaccineItemId = vaccineItemId;
        this.dateAdministered = dateAdministered;
        this.nextBoosterDate = nextBoosterDate;
        this.reminderSent = reminderSent;
    }

    public int getId() { return id; }
    public int getAppointmentId() { return appointmentId; }
    public int getPetId() { return petId; }
    public int getVetId() { return vetId; }
    public int getVaccineItemId() { return vaccineItemId; }
    public String getDateAdministered() { return dateAdministered; }
    public String getNextBoosterDate() { return nextBoosterDate; }
    public boolean isReminderSent() { return reminderSent; }
    public void setReminderSent(boolean reminderSent) { this.reminderSent = reminderSent; }
}
