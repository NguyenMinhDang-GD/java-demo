package vetcare.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClinicDataStore implements Serializable {
    private static final long serialVersionUID = 1L;

    public List<User> users = new ArrayList<>();
    public List<Owner> owners = new ArrayList<>();
    public List<Pet> pets = new ArrayList<>();
    public List<CatalogItem> catalogItems = new ArrayList<>();
    public List<Appointment> appointments = new ArrayList<>();
    public List<EmrRecord> emrRecords = new ArrayList<>();
    public List<Vaccination> vaccinations = new ArrayList<>();
    public List<Invoice> invoices = new ArrayList<>();

    public int nextOwnerId = 1;
    public int nextPetId = 1;
    public int nextApptId = 1;
    public int nextEmrId = 1;
    public int nextVacId = 1;
    public int nextInvoiceId = 1;
}
