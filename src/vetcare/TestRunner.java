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
                dupCaught = ex.getMessage().contains("FR-12");
            }
            check(dupCaught, "Duplicate phone must be rejected with FR-12");

            Owner newOwner = service.addOwner("Pham Minh Tuan", "0909888777", "tuan@example.com", "District 3, HCMC");
            Pet newPet = service.addPet(newOwner.getId(), "Bong", "Dog", "Corgi", 2, "Female");
            check(newPet.getOwnerId() == newOwner.getId(), "Pet linked to Owner (BR-01)");
            System.out.println("   PASS: UC-02");

            System.out.println("3. Testing UC-03 Booking, Anti-Double Booking (BR-02), Check-in (FR-13), Cancel (FR-14)...");
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
                conflictCaught = ex.getMessage().contains("BR-02");
            }
            check(conflictCaught, "Overlapping vet slot must throw BR-02 conflict");

            service.checkInAppointment(booked.getId(), 9.4, 38.6);
            check("Checked-in".equals(service.findAppointmentById(booked.getId()).getStatus()), "Status Checked-in");

            Appointment toCancel = service.bookAppointment(newPet.getId(), vet.getId(), generalConsult.getId(),
                    futureDate, "15:00", "Cancel test");
            service.cancelAppointment(toCancel.getId());
            check("Cancelled".equals(service.findAppointmentById(toCancel.getId()).getStatus()), "Cancelled retained (FR-14)");
            System.out.println("   PASS: UC-03");

            System.out.println("4. Testing BR-05 pre-completion invoicing block...");
            boolean prematureInvoiceBlocked = false;
            try {
                service.generateInvoice(booked.getId());
            } catch (IllegalStateException ex) {
                prematureInvoiceBlocked = ex.getMessage().contains("BR-05");
            }
            check(prematureInvoiceBlocked, "Cannot invoice non-completed consultation (BR-05)");
            System.out.println("   PASS: BR-05 guard");

            System.out.println("5. Testing UC-04 & UC-05 EMR, BR-03, BR-04, FR-07...");
            CatalogItem med = service.getCatalogByCategory("MEDICINE").get(0); // Amoxicillin $8
            CatalogItem vac = service.getCatalogByCategory("VACCINE").get(1); // DHPPi $25, 21 days

            boolean nonVetBlocked = false;
            try {
                service.completeConsultation(reception, booked.getId(), "Sneezing", "Cold", true, "",
                        med.getId(), 1, "1/day", null, null);
            } catch (SecurityException ex) {
                nonVetBlocked = ex.getMessage().contains("BR-03");
            }
            check(nonVetBlocked, "Non-vet blocked from EMR (BR-03)");

            boolean screeningBlocked = false;
            try {
                service.completeConsultation(vet, booked.getId(), "Fever", "Infection", false, "",
                        null, 0, "", vac.getId(), futureDate);
            } catch (IllegalStateException ex) {
                screeningBlocked = ex.getMessage().contains("BR-04");
            }
            check(screeningBlocked, "Vaccine without screening blocked (BR-04)");

            service.completeConsultation(vet, booked.getId(), "Healthy", "Cleared for DHPPi", true, "Good condition",
                    med.getId(), 2, "1 tab daily", vac.getId(), futureDate);
            check("Completed".equals(service.findAppointmentById(booked.getId()).getStatus()), "Appointment Completed");

            Vaccination createdVac = service.getVaccinations().get(service.getVaccinations().size() - 1);
            String expectedNext = LocalDate.parse(futureDate).plusDays(vac.getBoosterCycleDays()).toString();
            check(expectedNext.equals(createdVac.getNextBoosterDate()), "Booster date auto-calculated (FR-07)");
            System.out.println("   PASS: UC-04 & UC-05");

            System.out.println("6. Testing UC-06 Invoicing (FR-09) & UC-08 Booster Check (FR-08)...");
            Invoice inv = service.generateInvoice(booked.getId());
            double expectedTotal = generalConsult.getPrice() + (med.getPrice() * 2) + vac.getPrice();
            check(Math.abs(inv.getTotalAmount() - expectedTotal) < 0.01, "Invoice fee aggregation matches FR-09");
            service.payInvoice(inv.getId(), "Bank Transfer");
            check("Paid".equals(service.findInvoiceByAppointmentId(booked.getId()).getStatus()), "Invoice Paid");

            List<Vaccination> dueDispatched = service.runDailyBoosterCheck();
            check(!dueDispatched.isEmpty(), "Actor Time daily check dispatches due reminder (UC-08)");
            System.out.println("   PASS: UC-06 & UC-08");

            System.out.println("7. Testing UC-07 Admin Catalog & Staff Lock (FR-03, FR-10)...");
            User admin = service.login("admin", "123456");
            boolean nonAdminBlocked = false;
            try {
                service.addOrUpdateCatalogItem(reception, null, "Unauthorized Item", "SERVICE", 50, "visit", 0);
            } catch (SecurityException ex) {
                nonAdminBlocked = ex.getMessage().contains("UC-07");
            }
            check(nonAdminBlocked, "Non-Admin must be blocked from editing catalog");

            CatalogItem addedSrv = service.addOrUpdateCatalogItem(admin, null, "Dental Cleaning", "SERVICE", 45, "visit", 0);
            check("Dental Cleaning".equals(addedSrv.getName()), "Admin added catalog item (FR-03)");

            User newStaff = service.addStaffUser(admin, "dr.linh", "123456", "Dr. Ho Linh", "Veterinarian");
            service.toggleUserLock(admin, newStaff.getId());
            boolean lockedRejected = false;
            try {
                service.login("dr.linh", "123456");
            } catch (SecurityException ex) {
                lockedRejected = ex.getMessage().contains("FR-10");
            }
            check(lockedRejected, "Locked staff account must be rejected (FR-10)");
            System.out.println("   PASS: UC-07");

            System.out.println("\nALL JAVA DEMO TESTS PASSED!");
        } finally {
            service.resetSampleData();
        }
    }
}
