# VetCare Clinic Management System — Java Swing Implementation

## Requirements
- Java JDK 11+ (`java` and `javac` available in PATH).

## Quick Start (Windows)
- Double-click `run-java-demo.bat`, or run in terminal:
  ```bat
  cd java-demo
  compile-and-test.bat
  java -cp out vetcare.ui.VetCareSwingApp
  ```

## Demo Accounts (Password: `123456`)
- `reception` — Nguyen Thanh Tien (Receptionist)
- `dr.khoi` — Dr. Minh Khoi (Veterinarian)
- `admin` — Clinic Manager (Admin)

## Implemented Functions & Business Rules
1. **Login & Role Switching (UC-01, FR-01)**
2. **Owners & Pets Directory + EMR History (UC-02, BR-01, FR-12 duplicate phone check)**
3. **Appointments, Conflict Detection, Check-in Vitals & Cancel (UC-03, BR-02, FR-11, FR-13, FR-14)**
4. **Clinical Exam / EMR & Prescriptions (UC-04, BR-03 Vet-only authority, FR-06)**
5. **Vaccination Logging, Screening Gate & Auto Booster Reminders (UC-05, UC-08, BR-04, FR-07, FR-08)**
6. **Counter Invoicing with Auto Fee Aggregation (UC-06, BR-05, FR-09)**
