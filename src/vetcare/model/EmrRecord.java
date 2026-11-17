package vetcare.model;

import java.io.Serializable;

public class EmrRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int appointmentId;
    private int petId;
    private int vetId;
    private String symptoms;
    private String diagnosis;
    private boolean screeningPassed;
    private String notes;
    private Integer medicineItemId;
    private int medicineQty;
    private String dosage;

    public EmrRecord(int id, int appointmentId, int petId, int vetId, String symptoms, String diagnosis,
                     boolean screeningPassed, String notes, Integer medicineItemId, int medicineQty, String dosage) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.petId = petId;
        this.vetId = vetId;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.screeningPassed = screeningPassed;
        this.notes = notes;
        this.medicineItemId = medicineItemId;
        this.medicineQty = medicineQty;
        this.dosage = dosage;
    }

    public int getId() { return id; }
    public int getAppointmentId() { return appointmentId; }
    public int getPetId() { return petId; }
    public int getVetId() { return vetId; }
    public String getSymptoms() { return symptoms; }
    public String getDiagnosis() { return diagnosis; }
    public boolean isScreeningPassed() { return screeningPassed; }
    public String getNotes() { return notes; }
    public Integer getMedicineItemId() { return medicineItemId; }
    public int getMedicineQty() { return medicineQty; }
    public String getDosage() { return dosage; }
}
