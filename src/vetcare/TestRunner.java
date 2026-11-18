package vetcare;

import vetcare.model.*;
import vetcare.service.ClinicService;

import java.time.LocalDate;
import java.util.List;

public class TestRunner {
    private static void check(boolean condition, String msg) {
        if (!condition) {
            throw new AssertionError("ASSERTION FAILED: " + msg);
        }
    }

    public static void main(String[] args) {
        ClinicService service = new ClinicService();
        service.resetSampleData();

        try {
            System.out.println("1. Testing UC-01 Login...");
            User reception = service.login("reception", "123456");
            User vet = service.login("dr.khoi", "123456");
            check("Receptionist".equals(reception.getRole()), "Receptionist role check");
            check("Veterinarian".equals(vet.getRole()), "Veterinarian role check");

            boolean loginFailed = false;
            try {
                service.login("dr.khoi", "wrongpass");
            } catch (SecurityException ex) {
                loginFailed = true;
            }
            check(loginFailed, "Invalid password must fail");
            System.out.println("   PASS: UC-01");

            System.out.println("2. Testing UC-02 Owners & Pets (BR-01, FR-12)...");
            boolean dupCaught = false;
            try {
                service.addOwner("Dup Owner", "0901234567", "", "");
            } catch (IllegalArgumentException ex) {
                dupCaught = ex.getMessage().contains("Duplicate Owner phone");
            }
            check(dupCaught, "Duplicate phone must be rejected");

            Owner newOwner = service.addOwner("Pham Minh Tuan", "0909888777", "tuan@example.com", "District 3, HCMC");
            Pet newPet = service.addPet(newOwner.getId(), "Bong", "Dog", "Corgi", 2, "Female");
            check(newPet.getOwnerId() == newOwner.getId(), "Pet linked to Owner");
            System.out.println("   PASS: Owners & Pets");

            System.out.println("3. Testing Booking, Anti-Double Booking, Check-in, Cancel...");
            String futureDate = LocalDate.now().plusDays(3).toString();
            List<CatalogItem> services = service.getCatalogByCategory("SERVICE");
            CatalogItem generalConsult = services.get(0); // General Consultation ($15)

            Appointment booked = service.bookAppointment(newPet.getId(), vet.getId(), generalConsult.getId(),
                    futureDate, "14:00", "Checkup & vaccine");

            boolean conflictCaught = false;
            try {
                service.bookAppointment(newPet.getId(), vet.getId(), generalConsult.getId(),
                        futureDate, "14:00", "Double booking");
            } catch (IllegalStateException ex) {
                conflictCaught = ex.getMessage().contains("Schedule Conflict");
            }
            check(conflictCaught, "Overlapping vet slot must throw Schedule Conflict");

            service.checkInAppointment(booked.getId(), 9.4, 38.6);
            check("Checked-in".equals(service.findAppointmentById(booked.getId()).getStatus()), "Status Checked-in");

            Appointment toCancel = service.bookAppointment(newPet.getId(), vet.getId(), generalConsult.getId(),
                    futureDate, "15:00", "Cancel test");
            service.cancelAppointment(toCancel.getId());
            check("Cancelled".equals(service.findAppointmentById(toCancel.getId()).getStatus()), "Cancelled retained");
            System.out.println("   PASS: Appointments");

            System.out.println("4. Testing pre-completion invoicing block...");
            boolean prematureInvoiceBlocked = false;
            try {
                service.generateInvoice(booked.getId());
            } catch (IllegalStateException ex) {
                prematureInvoiceBlocked = ex.getMessage().contains("Invoicing Rule");
            }
            check(prematureInvoiceBlocked, "Cannot invoice non-completed consultation");
            System.out.println("   PASS: Invoicing guard");

            System.out.println("5. Testing EMR, Vet Authority, Screening Gate, Booster Date...");
            CatalogItem med = service.getCatalogByCategory("MEDICINE").get(0); // Amoxicillin $8
            CatalogItem vac = service.getCatalogByCategory("VACCINE").get(1); // DHPPi $25, 21 days

            boolean nonVetBlocked = false;
            try {
                service.completeConsultation(reception, booked.getId(), "Sneezing", "Cold", true, "",
                        med.getId(), 1, "1/day", null, null);
            } catch (SecurityException ex) {
                nonVetBlocked = ex.getMessage().contains("Only a Veterinarian");
            }
            check(nonVetBlocked, "Non-vet blocked from EMR");

            boolean screeningBlocked = false;
            try {
                service.completeConsultation(vet, booked.getId(), "Fever", "Infection", false, "",
                        null, 0, "", vac.getId(), futureDate);
            } catch (IllegalStateException ex) {
                screeningBlocked = ex.getMessage().contains("Vaccination Prerequisite");
            }
            check(screeningBlocked, "Vaccine without screening blocked");

            service.completeConsultation(vet, booked.getId(), "Healthy", "Cleared for DHPPi", true, "Good condition",
                    med.getId(), 2, "1 tab daily", vac.getId(), futureDate);
            check("Completed".equals(service.findAppointmentById(booked.getId()).getStatus()), "Appointment Completed");

            Vaccination createdVac = service.getVaccinations().get(service.getVaccinations().size() - 1);
            String expectedNext = LocalDate.parse(futureDate).plusDays(vac.getBoosterCycleDays()).toString();
            check(expectedNext.equals(createdVac.getNextBoosterDate()), "Booster date auto-calculated");
            System.out.println("   PASS: EMR & Vaccination");

            System.out.println("6. Testing Counter Invoicing & Booster Check...");
            Invoice inv = service.generateInvoice(booked.getId());
            double expectedTotal = generalConsult.getPrice() + (med.getPrice() * 2) + vac.getPrice();
            check(Math.abs(inv.getTotalAmount() - expectedTotal) < 0.01, "Invoice fee aggregation matches");
            service.payInvoice(inv.getId(), "Bank Transfer");
            check("Paid".equals(service.findInvoiceByAppointmentId(booked.getId()).getStatus()), "Invoice Paid");

            List<Vaccination> dueDispatched = service.runDailyBoosterCheck();
            check(!dueDispatched.isEmpty(), "Daily check dispatches due reminder");
            System.out.println("   PASS: Invoicing & Reminders");

            System.out.println("7. Testing Admin Catalog & Staff Lock...");
            User admin = service.login("admin", "123456");
            boolean nonAdminBlocked = false;
            try {
                service.addOrUpdateCatalogItem(reception, null, "Unauthorized Item", "SERVICE", 50, "visit", 0);
            } catch (SecurityException ex) {
                nonAdminBlocked = ex.getMessage().contains("Only Admin");
            }
            check(nonAdminBlocked, "Non-Admin must be blocked from editing catalog");

            CatalogItem addedSrv = service.addOrUpdateCatalogItem(admin, null, "Dental Cleaning", "SERVICE", 45, "visit", 0);
            check("Dental Cleaning".equals(addedSrv.getName()), "Admin added catalog item");

            User newStaff = service.addStaffUser(admin, "dr.linh", "123456", "Dr. Ho Linh", "Veterinarian");
            service.toggleUserLock(admin, newStaff.getId());
            boolean lockedRejected = false;
            try {
                service.login("dr.linh", "123456");
            } catch (SecurityException ex) {
                lockedRejected = ex.getMessage().contains("locked");
            }
            check(lockedRejected, "Locked staff account must be rejected");
            System.out.println("   PASS: Admin Controls");

            System.out.println("\nALL JAVA DEMO TESTS PASSED!");
        } finally {
            service.resetSampleData();
        }
    }
}
