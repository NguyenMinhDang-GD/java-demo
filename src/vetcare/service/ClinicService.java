package vetcare.service;

import vetcare.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClinicService {
    private final File storageFile;
    private ClinicDataStore store;

    public ClinicService() {
        File baseDir = new File("java-demo").isDirectory() ? new File("java-demo") : new File(".");
        File dataDir = new File(baseDir, "data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        this.storageFile = new File(dataDir, "vetcare_data.ser");
        loadOrSeed();
    }

    private void loadOrSeed() {
        if (storageFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storageFile))) {
                store = (ClinicDataStore) ois.readObject();
                return;
            } catch (Exception ignored) {
                // Fall back to fresh seed if serialization version changed
            }
        }
        resetSampleData();
    }

    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storageFile))) {
            oos.writeObject(store);
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist data: " + e.getMessage(), e);
        }
    }

    public void resetSampleData() {
        store = new ClinicDataStore();

        // Users (UC-01, FR-01, FR-10)
        store.users.add(new User(1, "admin", "123456", "Clinic Manager (Admin)", "Admin", false));
        store.users.add(new User(2, "reception", "123456", "Nguyen Thanh Tien", "Receptionist", false));
        store.users.add(new User(3, "dr.khoi", "123456", "Dr. Minh Khoi", "Veterinarian", false));
        store.users.add(new User(4, "dr.huy", "123456", "Dr. Gia Huy", "Veterinarian", false));

        // Catalog Items (FR-03)
        store.catalogItems.add(new CatalogItem(1, "General Consultation", "SERVICE", 15, "visit", 0));
        store.catalogItems.add(new CatalogItem(2, "Specialist Check-up", "SERVICE", 25, "visit", 0));
        store.catalogItems.add(new CatalogItem(3, "Vaccination Visit", "SERVICE", 10, "visit", 0));

        store.catalogItems.add(new CatalogItem(4, "Amoxicillin 250mg", "MEDICINE", 8, "box", 0));
        store.catalogItems.add(new CatalogItem(5, "Meloxicam Pain Relief", "MEDICINE", 12, "bottle", 0));
        store.catalogItems.add(new CatalogItem(6, "NexGard Chewable", "MEDICINE", 18, "tablet", 0));
        store.catalogItems.add(new CatalogItem(7, "Eye Drops VetCare", "MEDICINE", 6, "bottle", 0));

        store.catalogItems.add(new CatalogItem(8, "Rabies Vaccine", "VACCINE", 20, "dose", 365));
        store.catalogItems.add(new CatalogItem(9, "DHPPi 5-in-1 Dog Vaccine", "VACCINE", 25, "dose", 21));
        store.catalogItems.add(new CatalogItem(10, "FVRCP Cat Vaccine", "VACCINE", 22, "dose", 30));

        // Owners & Pets (UC-02, BR-01, FR-12)
        Owner o1 = new Owner(store.nextOwnerId++, "Nguyen Minh Dang", "0901234567", "dang.nguyen@example.com", "District 7, HCMC");
        Owner o2 = new Owner(store.nextOwnerId++, "Tran Thi Mai", "0912345678", "mai.tran@example.com", "District 1, HCMC");
        Owner o3 = new Owner(store.nextOwnerId++, "Le Hoang Nam", "0987654321", "nam.le@example.com", "Binh Thanh, HCMC");
        store.owners.add(o1);
        store.owners.add(o2);
        store.owners.add(o3);

        Pet pMilo = new Pet(store.nextPetId++, o1.getId(), "Milo", "Dog", "Golden Retriever", 3, "Male");
        Pet pLuna = new Pet(store.nextPetId++, o2.getId(), "Luna", "Cat", "British Shorthair", 2, "Female");
        Pet pKiki = new Pet(store.nextPetId++, o2.getId(), "Kiki", "Dog", "Poodle", 1, "Female");
        Pet pSimba = new Pet(store.nextPetId++, o3.getId(), "Simba", "Cat", "Maine Coon", 4, "Male");
        store.pets.add(pMilo);
        store.pets.add(pLuna);
        store.pets.add(pKiki);
        store.pets.add(pSimba);

        String today = LocalDate.now().toString();
        String threeWeeksAgo = LocalDate.now().minusDays(21).toString();

        // 1. Milo - Completed today (ready for Counter Invoicing: $15 + $8 + $20 = $43)
        Appointment a1 = new Appointment(store.nextApptId++, pMilo.getId(), 3, 1, today, "08:30", "Completed",
                26.5, 38.5, "Skin itchiness & annual rabies shot");
        store.appointments.add(a1);
        store.emrRecords.add(new EmrRecord(store.nextEmrId++, a1.getId(), pMilo.getId(), 3,
                "Mild scratching near ears, normal temperature",
                "Mild seasonal allergy; cleared for vaccination",
                true, "Administered Rabies booster after clinical screening.", 4, 1, "1 tablet twice daily for 5 days"));
        store.vaccinations.add(new Vaccination(store.nextVacId++, a1.getId(), pMilo.getId(), 3, 8,
                today, LocalDate.now().plusDays(365).toString(), false));

        // 2. Luna - Checked-in today (ready for Veterinarian EMR & Vaccination)
        Appointment a2 = new Appointment(store.nextApptId++, pLuna.getId(), 3, 2, today, "09:30", "Checked-in",
                4.2, 38.6, "Mild sneezing and watery eyes");
        store.appointments.add(a2);

        // 3. Simba - Scheduled today (ready for Check-in)
        Appointment a3 = new Appointment(store.nextApptId++, pSimba.getId(), 4, 1, today, "10:30", "Scheduled",
                0, 0, "Annual wellness check-up");
        store.appointments.add(a3);

        // 4. Kiki - Past visit 21 days ago with DHPPi booster Due Today (for UC-08 Reminder demo)
        Appointment a4 = new Appointment(store.nextApptId++, pKiki.getId(), 4, 3, threeWeeksAgo, "14:00", "Completed",
                4.8, 38.4, "Puppy DHPPi first dose");
        store.appointments.add(a4);
        store.emrRecords.add(new EmrRecord(store.nextEmrId++, a4.getId(), pKiki.getId(), 4,
                "Active, normal appetite, clear lungs",
                "Healthy puppy cleared for DHPPi 5-in-1 vaccination",
                true, "Schedule 21-day follow-up booster.", 7, 1, "2 drops per eye once daily if dusty"));
        store.vaccinations.add(new Vaccination(store.nextVacId++, a4.getId(), pKiki.getId(), 4, 9,
                threeWeeksAgo, today, false));
        store.invoices.add(new Invoice(store.nextInvoiceId++, a4.getId(), 10, 6, 25, 41, "Cash", "Paid"));

        save();
    }

    // 1. Login & Authentication (UC-01, FR-01, FR-10)
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password are required.");
        }
        for (User u : store.users) {
            if (u.getUsername().equalsIgnoreCase(username.trim()) && u.getPassword().equals(password)) {
                if (u.isLocked()) {
                    throw new SecurityException("Account is locked (FR-10). Contact Clinic Manager (Admin).");
                }
                return u;
            }
        }
        throw new SecurityException("Invalid username or password (FR-01).");
    }

    public User findUserByUsername(String username) {
        for (User u : store.users) {
            if (u.getUsername().equalsIgnoreCase(username)) return u;
        }
        return null;
    }

    public User findUserById(int id) {
        for (User u : store.users) {
            if (u.getId() == id) return u;
        }
        return null;
    }

    public List<User> getVets() {
        List<User> list = new ArrayList<>();
        for (User u : store.users) {
            if ("Veterinarian".equals(u.getRole()) && !u.isLocked()) {
                list.add(u);
            }
        }
        return list;
    }

    // 2. Owner & Pet Management (UC-02, FR-02, FR-12, BR-01)
    public List<Owner> getOwners() {
        return new ArrayList<>(store.owners);
    }

    public List<Owner> searchOwners(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getOwners();
        }
        String q = query.trim().toLowerCase();
        List<Owner> matched = new ArrayList<>();
        for (Owner o : store.owners) {
            boolean petMatches = false;
            for (Pet p : getPetsByOwner(o.getId())) {
                if (p.getName().toLowerCase().contains(q)) {
                    petMatches = true;
                    break;
                }
            }
            if (o.getFullName().toLowerCase().contains(q)
                    || o.getPhone().toLowerCase().contains(q)
                    || (o.getEmail() != null && o.getEmail().toLowerCase().contains(q))
                    || petMatches) {
                matched.add(o);
            }
        }
        return matched;
    }

    public Owner findOwnerById(int id) {
        for (Owner o : store.owners) {
            if (o.getId() == id) return o;
        }
        return null;
    }

    public Owner addOwner(String fullName, String phone, String email, String address) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid field: Owner full name is required.");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid field: Owner phone number is required.");
        }
        String cleanPhone = phone.trim();
        for (Owner existing : store.owners) {
            if (existing.getPhone().equals(cleanPhone)) {
                throw new IllegalArgumentException("Duplicate Owner phone number detected (FR-12): Phone \""
                        + cleanPhone + "\" is already registered to " + existing.getFullName() + ".");
            }
        }
        Owner created = new Owner(store.nextOwnerId++, fullName.trim(), cleanPhone,
                email != null ? email.trim() : "", address != null ? address.trim() : "");
        store.owners.add(created);
        save();
        return created;
    }

    public List<Pet> getPets() {
        return new ArrayList<>(store.pets);
    }

    public List<Pet> getPetsByOwner(int ownerId) {
        List<Pet> list = new ArrayList<>();
        for (Pet p : store.pets) {
            if (p.getOwnerId() == ownerId) list.add(p);
        }
        return list;
    }

    public Pet findPetById(int id) {
        for (Pet p : store.pets) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public Pet addPet(Integer ownerId, String name, String species, String breed, int age, String gender) {
        if (ownerId == null || findOwnerById(ownerId) == null) {
            throw new IllegalArgumentException("Pet Identification Rule (BR-01): Every pet must be linked to a valid Owner.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid field: Pet name is required.");
        }
        if (species == null || species.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid field: Pet species is required.");
        }
        Pet created = new Pet(store.nextPetId++, ownerId, name.trim(), species.trim(),
                breed != null ? breed.trim() : "", Math.max(0, age), gender != null ? gender : "Male");
        store.pets.add(created);
        save();
        return created;
    }

    public String getPetHistoryReport(int petId) {
        Pet pet = findPetById(petId);
        if (pet == null) return "Pet not found.";
        Owner owner = findOwnerById(pet.getOwnerId());
        StringBuilder sb = new StringBuilder();
        sb.append("PET PROFILE: ").append(pet.getName()).append(" (").append(pet.getSpecies())
                .append(" - ").append(pet.getBreed()).append(", ").append(pet.getAge()).append(" yrs, ")
                .append(pet.getGender()).append(")\n");
        sb.append("OWNER: ").append(owner != null ? owner.getFullName() + " (" + owner.getPhone() + ")" : "N/A").append("\n\n");

        sb.append("=== ELECTRONIC MEDICAL RECORDS (EMR) ===\n");
        boolean hasEmr = false;
        for (EmrRecord e : store.emrRecords) {
            if (e.getPetId() == petId) {
                hasEmr = true;
                Appointment a = findAppointmentById(e.getAppointmentId());
                User vet = findUserById(e.getVetId());
                sb.append("- Date: ").append(a != null ? a.getDate() + " " + a.getTimeSlot() : "N/A")
                        .append(" | Vet: ").append(vet != null ? vet.getFullName() : "N/A").append("\n");
                sb.append("  Symptoms: ").append(e.getSymptoms()).append("\n");
                sb.append("  Diagnosis: ").append(e.getDiagnosis()).append("\n");
                if (e.getMedicineItemId() != null) {
                    CatalogItem med = findCatalogById(e.getMedicineItemId());
                    sb.append("  Prescription: ").append(med != null ? med.getName() : "Medicine")
                            .append(" x").append(e.getMedicineQty()).append(" (").append(e.getDosage()).append(")\n");
                }
                sb.append("\n");
            }
        }
        if (!hasEmr) sb.append("No clinical examinations recorded yet.\n\n");

        sb.append("=== VACCINATION HISTORY ===\n");
        boolean hasVac = false;
        for (Vaccination v : store.vaccinations) {
            if (v.getPetId() == petId) {
                hasVac = true;
                CatalogItem vac = findCatalogById(v.getVaccineItemId());
                sb.append("- ").append(vac != null ? vac.getName() : "Vaccine")
                        .append(" | Administered: ").append(v.getDateAdministered())
                        .append(" | Next Booster: ").append(v.getNextBoosterDate()).append("\n");
            }
        }
        if (!hasVac) sb.append("No vaccinations recorded yet.\n");
        return sb.toString();
    }

    // Catalog lookup
    public List<CatalogItem> getCatalogByCategory(String category) {
        List<CatalogItem> list = new ArrayList<>();
        for (CatalogItem item : store.catalogItems) {
            if (category == null || category.equals(item.getCategory())) {
                list.add(item);
            }
        }
        return list;
    }

    public CatalogItem findCatalogById(int id) {
        for (CatalogItem item : store.catalogItems) {
            if (item.getId() == id) return item;
        }
        return null;
    }

    // 3. Appointment Booking, Conflict Detection, Check-in & Cancel (UC-03, BR-02, FR-04, FR-05, FR-11, FR-13, FR-14)
    public List<Appointment> getAppointments() {
        return new ArrayList<>(store.appointments);
    }

    public List<Appointment> getAppointmentsFiltered(String dateFilter, String statusFilter) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment a : store.appointments) {
            if (dateFilter != null && !dateFilter.trim().isEmpty() && !a.getDate().equals(dateFilter.trim())) {
                continue;
            }
            if (statusFilter != null && !statusFilter.isEmpty() && !"All".equals(statusFilter)
                    && !a.getStatus().equals(statusFilter)) {
                continue;
            }
            result.add(a);
        }
        return result;
    }

    public Appointment findAppointmentById(int id) {
        for (Appointment a : store.appointments) {
            if (a.getId() == id) return a;
        }
        return null;
    }

    public Appointment bookAppointment(int petId, int vetId, int serviceId, String date, String timeSlot, String reason) {
        if (findPetById(petId) == null) throw new IllegalArgumentException("Invalid pet selected.");
        User vet = findUserById(vetId);
        if (vet == null || !"Veterinarian".equals(vet.getRole())) {
            throw new IllegalArgumentException("Invalid Veterinarian selected.");
        }
        if (date == null || date.trim().isEmpty() || timeSlot == null || timeSlot.trim().isEmpty()) {
            throw new IllegalArgumentException("Date and time slot are required.");
        }
        String cleanDate = date.trim();
        // Validate date format and future-only constraint (FR-11)
        LocalDate parsedDate = LocalDate.parse(cleanDate);
        if (!parsedDate.isAfter(LocalDate.now().minusDays(1))) {
            throw new IllegalArgumentException(
                "Scheduling Rule (FR-11): Appointment date must be today or in the future. Received: " + cleanDate);
        }

        // BR-02 & FR-05: Anti-Double Booking
        for (Appointment existing : store.appointments) {
            if (existing.getVetId() == vetId
                    && existing.getDate().equals(cleanDate)
                    && existing.getTimeSlot().equals(timeSlot)
                    && !"Cancelled".equals(existing.getStatus())) {
                throw new IllegalStateException("Schedule Conflict (BR-02): " + vet.getFullName()
                        + " already has an appointment on " + cleanDate + " at " + timeSlot + ".");
            }
        }

        Appointment created = new Appointment(store.nextApptId++, petId, vetId, serviceId, cleanDate,
                timeSlot, "Scheduled", 0, 0, reason != null ? reason.trim() : "");
        store.appointments.add(created);
        save();
        return created;
    }

    public Appointment checkInAppointment(int appointmentId, double weightKg, double temperatureC) {
        Appointment a = findAppointmentById(appointmentId);
        if (a == null) throw new IllegalArgumentException("Appointment not found.");
        if (!"Scheduled".equals(a.getStatus())) {
            throw new IllegalStateException("Only 'Scheduled' appointments can be checked in (FR-13).");
        }
        if (weightKg <= 0 || temperatureC <= 0) {
            throw new IllegalArgumentException("Weight (kg) and Temperature (°C) must be greater than 0.");
        }
        a.setWeightKg(weightKg);
        a.setTemperatureC(temperatureC);
        a.setStatus("Checked-in");
        save();
        return a;
    }

    public Appointment cancelAppointment(int appointmentId) {
        Appointment a = findAppointmentById(appointmentId);
        if (a == null) throw new IllegalArgumentException("Appointment not found.");
        if ("Completed".equals(a.getStatus())) {
            throw new IllegalStateException("Cannot cancel a completed consultation.");
        }
        a.setStatus("Cancelled"); // Retained in list per FR-14
        save();
        return a;
    }

    // 4 & 5. EMR Consultation & Vaccination (UC-04, UC-05, BR-03, BR-04, FR-06, FR-07)
    public EmrRecord completeConsultation(User currentUser, int appointmentId, String symptoms, String diagnosis,
                                          boolean screeningPassed, String notes,
                                          Integer medicineItemId, int medicineQty, String dosage,
                                          Integer vaccineItemId, String vaccineDate) {
        // BR-03: Medical Authority
        if (currentUser == null || !"Veterinarian".equals(currentUser.getRole())) {
            throw new SecurityException("Medical Authority Error (BR-03): Only a Veterinarian is authorized to finalize diagnoses, prescriptions, and EMRs.");
        }

        Appointment a = findAppointmentById(appointmentId);
        if (a == null) throw new IllegalArgumentException("Appointment not found.");
        if ("Cancelled".equals(a.getStatus())) {
            throw new IllegalStateException("Cannot record EMR for a cancelled appointment.");
        }
        // FR-13: EMR can only proceed after patient has been checked in (Checked-in status)
        if (!"Checked-in".equals(a.getStatus()) && !"Completed".equals(a.getStatus())) {
            throw new IllegalStateException(
                "Clinical Flow Error (FR-13): EMR recording requires the patient to be checked in first. Current status: " + a.getStatus());
        }
        for (EmrRecord existing : store.emrRecords) {
            if (existing.getAppointmentId() == appointmentId) {
                throw new IllegalStateException("EMR already recorded for this appointment.");
            }
        }
        if (symptoms == null || symptoms.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid field: Clinical symptoms are required.");
        }
        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid field: Diagnosis is required.");
        }

        // BR-04: Vaccination Prerequisite
        if (vaccineItemId != null && !screeningPassed) {
            throw new IllegalStateException("Vaccination Prerequisite Error (BR-04): Pet must pass basic clinical screening before any vaccine is administered.");
        }

        if (vaccineItemId != null) {
            CatalogItem vac = findCatalogById(vaccineItemId);
            if (vac == null || !"VACCINE".equals(vac.getCategory())) {
                throw new IllegalArgumentException("Selected item is not a valid vaccine.");
            }
            String adminDate = (vaccineDate != null && !vaccineDate.trim().isEmpty())
                    ? vaccineDate.trim()
                    : LocalDate.now().toString();
            String nextBooster = LocalDate.parse(adminDate).plusDays(vac.getBoosterCycleDays()).toString();
            store.vaccinations.add(new Vaccination(store.nextVacId++, a.getId(), a.getPetId(),
                    currentUser.getId(), vac.getId(), adminDate, nextBooster, false));
        }

        EmrRecord emr = new EmrRecord(
                store.nextEmrId++,
                a.getId(),
                a.getPetId(),
                currentUser.getId(),
                symptoms.trim(),
                diagnosis.trim(),
                screeningPassed,
                notes != null ? notes.trim() : "",
                medicineItemId,
                medicineItemId != null ? Math.max(1, medicineQty) : 0,
                dosage != null ? dosage.trim() : ""
        );
        store.emrRecords.add(emr);
        a.setStatus("Completed");
        save();
        return emr;
    }

    // UC-08: Booster Reminders (FR-08)
    public List<Vaccination> getVaccinations() {
        return new ArrayList<>(store.vaccinations);
    }

    public List<Vaccination> runDailyBoosterCheck() {
        String today = LocalDate.now().toString();
        List<Vaccination> dispatched = new ArrayList<>();
        for (Vaccination v : store.vaccinations) {
            if (!v.isReminderSent() && v.getNextBoosterDate().compareTo(today) <= 0) {
                v.setReminderSent(true);
                dispatched.add(v);
            }
        }
        if (!dispatched.isEmpty()) {
            save();
        }
        return dispatched;
    }

    public void sendReminder(int vaccinationId) {
        for (Vaccination v : store.vaccinations) {
            if (v.getId() == vaccinationId) {
                v.setReminderSent(true);
                save();
                return;
            }
        }
        throw new IllegalArgumentException("Vaccination record not found.");
    }

    // 6. Counter Invoicing (UC-06, FR-09, BR-05)
    public List<Invoice> getInvoices() {
        return new ArrayList<>(store.invoices);
    }

    public Invoice findInvoiceByAppointmentId(int appointmentId) {
        for (Invoice inv : store.invoices) {
            if (inv.getAppointmentId() == appointmentId) return inv;
        }
        return null;
    }

    public Invoice generateInvoice(int appointmentId) {
        Appointment a = findAppointmentById(appointmentId);
        if (a == null) throw new IllegalArgumentException("Appointment not found.");

        // BR-05: Invoicing Rule
        if (!"Completed".equals(a.getStatus())) {
            throw new IllegalStateException("Invoicing Rule Violation (BR-05): Invoice can only be generated after the Veterinarian completes the consultation (current status: " + a.getStatus() + ").");
        }

        Invoice existing = findInvoiceByAppointmentId(appointmentId);
        if (existing != null) {
            return existing;
        }

        CatalogItem srv = findCatalogById(a.getServiceId());
        double consultFee = srv != null ? srv.getPrice() : 0;

        double medFee = 0;
        for (EmrRecord e : store.emrRecords) {
            if (e.getAppointmentId() == appointmentId && e.getMedicineItemId() != null) {
                CatalogItem med = findCatalogById(e.getMedicineItemId());
                if (med != null) {
                    medFee += med.getPrice() * e.getMedicineQty();
                }
            }
        }

        double vacFee = 0;
        for (Vaccination v : store.vaccinations) {
            if (v.getAppointmentId() == appointmentId) {
                CatalogItem vac = findCatalogById(v.getVaccineItemId());
                if (vac != null) {
                    vacFee += vac.getPrice();
                }
            }
        }

        double total = consultFee + medFee + vacFee;
        Invoice created = new Invoice(store.nextInvoiceId++, appointmentId, consultFee, medFee, vacFee, total, "Cash", "Unpaid");
        store.invoices.add(created);
        save();
        return created;
    }

    public Invoice payInvoice(int invoiceId, String paymentMethod) {
        for (Invoice inv : store.invoices) {
            if (inv.getId() == invoiceId) {
                inv.setPaymentMethod(paymentMethod != null ? paymentMethod : "Cash");
                inv.setStatus("Paid");
                save();
                return inv;
            }
        }
        throw new IllegalArgumentException("Invoice not found.");
    }

    // 7. Admin: Catalog & Staff Control (UC-07, FR-03, FR-10)
    public List<CatalogItem> getAllCatalogItems() {
        return new ArrayList<>(store.catalogItems);
    }

    public CatalogItem addOrUpdateCatalogItem(User currentUser, Integer id, String name, String category,
                                              double price, String unit, int boosterCycleDays) {
        if (currentUser == null || !"Admin".equals(currentUser.getRole())) {
            throw new SecurityException("Access Denied (UC-07): Only Admin can configure services and prices (FR-03).");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid field: Catalog item name is required.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Invalid field: Price must be >= 0.");
        }
        int cycle = "VACCINE".equals(category) ? Math.max(1, boosterCycleDays) : 0;
        String cleanUnit = (unit != null && !unit.trim().isEmpty()) ? unit.trim() : "unit";

        if (id != null) {
            CatalogItem existing = findCatalogById(id);
            if (existing == null) {
                throw new IllegalArgumentException("Catalog item not found.");
            }
            existing.setName(name.trim());
            existing.setCategory(category);
            existing.setPrice(price);
            existing.setUnit(cleanUnit);
            existing.setBoosterCycleDays(cycle);
            save();
            return existing;
        }

        int nextId = 1;
        for (CatalogItem item : store.catalogItems) {
            if (item.getId() >= nextId) nextId = item.getId() + 1;
        }
        CatalogItem created = new CatalogItem(nextId, name.trim(), category, price, cleanUnit, cycle);
        store.catalogItems.add(created);
        save();
        return created;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(store.users);
    }

    public User addStaffUser(User currentUser, String username, String password, String fullName, String role) {
        if (currentUser == null || !"Admin".equals(currentUser.getRole())) {
            throw new SecurityException("Access Denied (UC-07): Only Admin can manage staff accounts (FR-10).");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid field: Username is required.");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Invalid field: Password must be at least 4 characters.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid field: Full name is required.");
        }
        if (!"Admin".equals(role) && !"Receptionist".equals(role) && !"Veterinarian".equals(role)) {
            throw new IllegalArgumentException("Invalid role.");
        }
        String cleanUser = username.trim();
        for (User u : store.users) {
            if (u.getUsername().equalsIgnoreCase(cleanUser)) {
                throw new IllegalArgumentException("Username \"" + cleanUser + "\" already exists.");
            }
        }
        int nextId = 1;
        for (User u : store.users) {
            if (u.getId() >= nextId) nextId = u.getId() + 1;
        }
        User created = new User(nextId, cleanUser, password, fullName.trim(), role, false);
        store.users.add(created);
        save();
        return created;
    }

    public User toggleUserLock(User currentUser, int targetUserId) {
        if (currentUser == null || !"Admin".equals(currentUser.getRole())) {
            throw new SecurityException("Access Denied (UC-07): Only Admin can lock/unlock staff accounts (FR-10).");
        }
        if (currentUser.getId() == targetUserId) {
            throw new IllegalArgumentException("You cannot lock your own currently logged-in Admin account.");
        }
        User target = findUserById(targetUserId);
        if (target == null) {
            throw new IllegalArgumentException("User account not found.");
        }
        target.setLocked(!target.isLocked());
        save();
        return target;
    }
}
