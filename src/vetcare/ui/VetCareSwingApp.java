package vetcare.ui;

import vetcare.model.*;
import vetcare.service.ClinicService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class VetCareSwingApp extends JFrame {
    private final ClinicService service;
    private User currentUser;

    // Header & Role Banner
    private JPanel headerPanel;
    private JLabel lblCurrentUser;
    private JPanel roleBannerPanel;
    private JLabel lblRoleBanner;
    private JTabbedPane tabs;

    // Role-specific panels
    private JPanel ownersPetsPanel;
    private JPanel ownersBottomForms;
    private JPanel appointmentsPanel;
    private JPanel apptRowActions;
    private JPanel apptBookWrapper;
    private JPanel emrPanel;
    private JPanel invoicingPanel;
    private JPanel remindersPanel;
    private JPanel adminPanel;

    // Tab 1: Owners & Pets
    private JTextField txtOwnerSearch;
    private DefaultTableModel ownersTableModel;
    private JTable ownersTable;
    private DefaultTableModel petsTableModel;
    private JTable petsTable;
    private JTextField txtOwnerName, txtOwnerPhone, txtOwnerEmail, txtOwnerAddress;
    private JComboBox<Owner> cmbPetOwner;
    private JTextField txtPetName, txtPetBreed, txtPetAge;
    private JComboBox<String> cmbPetSpecies, cmbPetGender;

    // Tab 2: Appointments
    private JTextField txtApptDateFilter;
    private JComboBox<String> cmbApptStatusFilter;
    private DefaultTableModel apptsTableModel;
    private JTable apptsTable;
    private JComboBox<Pet> cmbBookPet;
    private JComboBox<User> cmbBookVet;
    private JComboBox<CatalogItem> cmbBookService;
    private JTextField txtBookDate, txtBookReason;
    private JComboBox<String> cmbBookSlot;

    // Tab 3: Clinical Exam & EMR
    private DefaultTableModel queueTableModel;
    private JTable queueTable;
    private JLabel lblSelectedExam;
    private JTextArea txtSymptoms, txtDiagnosis;
    private JTextField txtExamNotes, txtMedQty, txtMedDosage, txtVacDate;
    private JCheckBox chkScreeningPassed;
    private JComboBox<Object> cmbMedicine, cmbVaccine;
    private JLabel lblBoosterPreview;

    // Tab 4: Counter Invoicing
    private DefaultTableModel invApptsTableModel;
    private JTable invApptsTable;
    private DefaultTableModel invoicesTableModel;
    private JTable invoicesTable;
    private JComboBox<String> cmbPaymentMethod;

    // Tab 5: Booster Reminders
    private DefaultTableModel remindersTableModel;
    private JTable remindersTable;

    // Tab 6: Admin (Catalog & Staff Control)
    private DefaultTableModel catalogTableModel;
    private JTable catalogTable;
    private Integer editingCatalogId = null;
    private JTextField txtCatName, txtCatPrice, txtCatUnit, txtCatCycle;
    private JComboBox<String> cmbCatCategory;
    private JButton btnSaveCatalog;

    private DefaultTableModel staffTableModel;
    private JTable staffTable;
    private JTextField txtStaffUsername, txtStaffPassword, txtStaffFullName;
    private JComboBox<String> cmbStaffRole;

    public VetCareSwingApp() {
        this.service = new ClinicService();
        this.currentUser = service.findUserByUsername("reception");

        setTitle("VetCare Clinic Management System");
        setSize(1180, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
        applyRolePermissions();
        refreshAllViews();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        JPanel northContainer = new JPanel(new BorderLayout(0, 6));
        northContainer.add(buildHeaderPanel(), BorderLayout.NORTH);
        northContainer.add(buildRoleBannerPanel(), BorderLayout.SOUTH);
        root.add(northContainer, BorderLayout.NORTH);

        ownersPetsPanel = buildOwnersPetsTab();
        appointmentsPanel = buildAppointmentsTab();
        emrPanel = buildEmrTab();
        invoicingPanel = buildInvoicingTab();
        remindersPanel = buildRemindersTab();
        adminPanel = buildAdminTab();

        tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 12));
        root.add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout(10, 6));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220)),
                new EmptyBorder(8, 12, 8, 12)));
        headerPanel.setBackground(new Color(240, 249, 248));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        JLabel title = new JLabel("VetCare Clinic Management System");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(new Color(15, 118, 110));
        lblCurrentUser = new JLabel();
        lblCurrentUser.setFont(new Font("SansSerif", Font.BOLD, 12));
        left.add(title);
        left.add(lblCurrentUser);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        right.setOpaque(false);

        JButton btnLogin = new JButton("Staff Login");
        btnLogin.addActionListener(e -> openLoginDialog());

        JButton btnSwitchRec = new JButton("Switch: Receptionist");
        btnSwitchRec.addActionListener(e -> switchUser("reception"));

        JButton btnSwitchVet = new JButton("Switch: Veterinarian");
        btnSwitchVet.addActionListener(e -> switchUser("dr.khoi"));

        JButton btnSwitchAdmin = new JButton("Switch: Admin");
        btnSwitchAdmin.addActionListener(e -> switchUser("admin"));

        JButton btnReset = new JButton("Reset Sample Data");
        btnReset.addActionListener(e -> {
            service.resetSampleData();
            switchUser("reception");
            refreshAllViews();
            JOptionPane.showMessageDialog(this, "Sample data restored cleanly.");
        });

        right.add(btnLogin);
        right.add(btnSwitchRec);
        right.add(btnSwitchVet);
        right.add(btnSwitchAdmin);
        right.add(btnReset);

        headerPanel.add(left, BorderLayout.WEST);
        headerPanel.add(right, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel buildRoleBannerPanel() {
        roleBannerPanel = new JPanel(new BorderLayout());
        roleBannerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(153, 246, 228)),
                new EmptyBorder(6, 12, 6, 12)));
        lblRoleBanner = new JLabel();
        lblRoleBanner.setFont(new Font("SansSerif", Font.BOLD, 12));
        roleBannerPanel.add(lblRoleBanner, BorderLayout.CENTER);
        return roleBannerPanel;
    }

    private void switchUser(String username) {
        User u = service.findUserByUsername(username);
        if (u != null) {
            if (u.isLocked()) {
                showError("Account \"" + u.getUsername() + "\" is currently locked by Admin.");
                return;
            }
            currentUser = u;
            applyRolePermissions();
            refreshAllViews();
        }
    }

    private void applyRolePermissions() {
        if (currentUser == null) return;
        String role = currentUser.getRole();

        lblCurrentUser.setText("Logged in as: " + currentUser.getFullName()
                + "  |  Username: " + currentUser.getUsername()
                + "  |  Role: " + role);

        tabs.removeAll();

        if ("Veterinarian".equals(role)) {
            headerPanel.setBackground(new Color(236, 253, 245));
            roleBannerPanel.setBackground(new Color(209, 250, 229));
            roleBannerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(52, 211, 153)),
                    new EmptyBorder(6, 12, 6, 12)));
            lblRoleBanner.setForeground(new Color(6, 95, 70));
            lblRoleBanner.setText(
                    "ROLE: VETERINARIAN — Clinical Examinations, Diagnosis, Prescriptions & Vaccinations. "
                    + "[Restricted: Read-only Schedule & Patient History; No Front-Desk Billing or Admin Control]");

            apptRowActions.setVisible(false);
            apptBookWrapper.setVisible(false);
            ownersBottomForms.setVisible(false);

            tabs.addTab("Clinical Exam, EMR & Vaccine", emrPanel);
            tabs.addTab("My Schedule (Read-Only)", appointmentsPanel);
            tabs.addTab("Patient EMR Lookup (Read-Only)", ownersPetsPanel);
            tabs.setSelectedIndex(0);
        } else if ("Admin".equals(role)) {
            headerPanel.setBackground(new Color(238, 242, 255));
            roleBannerPanel.setBackground(new Color(224, 231, 255));
            roleBannerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(129, 140, 248)),
                    new EmptyBorder(6, 12, 6, 12)));
            lblRoleBanner.setForeground(new Color(49, 46, 129));
            lblRoleBanner.setText(
                    "ROLE: ADMIN (CLINIC MANAGER) — Service/Medicine/Vaccine Pricing Catalog, Staff Account Lock/Unlock & Front-Desk Oversight. "
                    + "[Restricted: Cannot finalize Clinical EMRs]");

            apptRowActions.setVisible(true);
            apptBookWrapper.setVisible(true);
            ownersBottomForms.setVisible(true);

            tabs.addTab("Catalog & Staff Control", adminPanel);
            tabs.addTab("Appointments & Queue", appointmentsPanel);
            tabs.addTab("Owners & Pets", ownersPetsPanel);
            tabs.addTab("Counter Invoicing", invoicingPanel);
            tabs.addTab("Booster Reminders", remindersPanel);
            tabs.setSelectedIndex(0);
        } else {
            // Receptionist
            headerPanel.setBackground(new Color(240, 249, 248));
            roleBannerPanel.setBackground(new Color(204, 251, 241));
            roleBannerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(45, 212, 191)),
                    new EmptyBorder(6, 12, 6, 12)));
            lblRoleBanner.setForeground(new Color(17, 94, 89));
            lblRoleBanner.setText(
                    "ROLE: RECEPTIONIST — Front-Desk Intake, Owners & Pets, Scheduling & Check-in, Counter Invoicing, Booster Reminders. "
                    + "[Restricted: Cannot record Clinical EMR or edit Catalog/Staff]");

            apptRowActions.setVisible(true);
            apptBookWrapper.setVisible(true);
            ownersBottomForms.setVisible(true);

            tabs.addTab("Appointments & Queue", appointmentsPanel);
            tabs.addTab("Owners & Pets", ownersPetsPanel);
            tabs.addTab("Counter Invoicing", invoicingPanel);
            tabs.addTab("Booster Reminders", remindersPanel);
            tabs.setSelectedIndex(0);
        }

        tabs.revalidate();
        tabs.repaint();
    }

    private void openLoginDialog() {
        JPanel p = new JPanel(new GridLayout(2, 2, 6, 6));
        JTextField txtUser = new JTextField("dr.khoi");
        JPasswordField txtPass = new JPasswordField("123456");
        p.add(new JLabel("Username:"));
        p.add(txtUser);
        p.add(new JLabel("Password:"));
        p.add(txtPass);

        int res = JOptionPane.showConfirmDialog(this, p, "Staff Login", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                currentUser = service.login(txtUser.getText(), new String(txtPass.getPassword()));
                applyRolePermissions();
                refreshAllViews();
                JOptionPane.showMessageDialog(this, "Welcome " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }
    }

    // ==================== TAB 1: OWNERS & PETS ====================
    private JPanel buildOwnersPetsTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchBar.add(new JLabel("Search Owner / Phone / Pet:"));
        txtOwnerSearch = new JTextField(24);
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> refreshOwnersTable());
        JButton btnClearSearch = new JButton("Clear");
        btnClearSearch.addActionListener(e -> {
            txtOwnerSearch.setText("");
            refreshOwnersTable();
        });
        searchBar.add(txtOwnerSearch);
        searchBar.add(btnSearch);
        searchBar.add(btnClearSearch);
        panel.add(searchBar, BorderLayout.NORTH);

        ownersTableModel = new DefaultTableModel(new String[]{"ID", "Full Name", "Phone", "Email", "Address"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        ownersTable = new JTable(ownersTableModel);

        petsTableModel = new DefaultTableModel(new String[]{"ID", "Pet Name", "Species", "Breed", "Age", "Gender", "Owner"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        petsTable = new JTable(petsTableModel);

        JPanel petsCenter = new JPanel(new BorderLayout(4, 6));
        petsCenter.add(new JScrollPane(petsTable), BorderLayout.CENTER);
        JPanel petBtnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        JButton btnViewHistory = new JButton("View Selected Pet EMR & Vaccination History");
        btnViewHistory.addActionListener(e -> handleViewPetHistory());
        petBtnBar.add(btnViewHistory);
        petsCenter.add(petBtnBar, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                wrapTitled("Owners Directory", new JScrollPane(ownersTable)),
                wrapTitled("Registered Pets", petsCenter));
        split.setResizeWeight(0.48);
        panel.add(split, BorderLayout.CENTER);

        ownersBottomForms = new JPanel(new GridLayout(1, 2, 10, 0));

        JPanel ownerForm = new JPanel(new GridLayout(5, 2, 6, 4));
        ownerForm.setBorder(new TitledBorder("Register New Owner"));
        txtOwnerName = new JTextField();
        txtOwnerPhone = new JTextField();
        txtOwnerEmail = new JTextField();
        txtOwnerAddress = new JTextField();
        JButton btnAddOwner = new JButton("Add Owner");
        btnAddOwner.addActionListener(e -> handleAddOwner());
        ownerForm.add(new JLabel("Full Name *:")); ownerForm.add(txtOwnerName);
        ownerForm.add(new JLabel("Phone *:")); ownerForm.add(txtOwnerPhone);
        ownerForm.add(new JLabel("Email:")); ownerForm.add(txtOwnerEmail);
        ownerForm.add(new JLabel("Address:")); ownerForm.add(txtOwnerAddress);
        ownerForm.add(new JLabel()); ownerForm.add(btnAddOwner);

        JPanel petForm = new JPanel(new GridLayout(6, 2, 6, 4));
        petForm.setBorder(new TitledBorder("Register New Pet"));
        cmbPetOwner = new JComboBox<>();
        txtPetName = new JTextField();
        cmbPetSpecies = new JComboBox<>(new String[]{"Dog", "Cat", "Other"});
        txtPetBreed = new JTextField();
        txtPetAge = new JTextField("1");
        cmbPetGender = new JComboBox<>(new String[]{"Male", "Female"});
        JButton btnAddPet = new JButton("Add Pet");
        btnAddPet.addActionListener(e -> handleAddPet());

        JPanel ageGender = new JPanel(new GridLayout(1, 2, 4, 0));
        ageGender.add(txtPetAge);
        ageGender.add(cmbPetGender);

        petForm.add(new JLabel("Owner *:")); petForm.add(cmbPetOwner);
        petForm.add(new JLabel("Pet Name *:")); petForm.add(txtPetName);
        petForm.add(new JLabel("Species *:")); petForm.add(cmbPetSpecies);
        petForm.add(new JLabel("Breed:")); petForm.add(txtPetBreed);
        petForm.add(new JLabel("Age / Gender:")); petForm.add(ageGender);
        petForm.add(new JLabel()); petForm.add(btnAddPet);

        ownersBottomForms.add(ownerForm);
        ownersBottomForms.add(petForm);
        panel.add(ownersBottomForms, BorderLayout.SOUTH);

        return panel;
    }

    private void handleAddOwner() {
        try {
            Owner o = service.addOwner(txtOwnerName.getText(), txtOwnerPhone.getText(),
                    txtOwnerEmail.getText(), txtOwnerAddress.getText());
            txtOwnerName.setText("");
            txtOwnerPhone.setText("");
            txtOwnerEmail.setText("");
            txtOwnerAddress.setText("");
            refreshAllViews();
            JOptionPane.showMessageDialog(this, "Owner added: " + o.getFullName());
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void handleAddPet() {
        try {
            Owner owner = (Owner) cmbPetOwner.getSelectedItem();
            int age = Integer.parseInt(txtPetAge.getText().trim().isEmpty() ? "0" : txtPetAge.getText().trim());
            Pet p = service.addPet(owner != null ? owner.getId() : null, txtPetName.getText(),
                    (String) cmbPetSpecies.getSelectedItem(), txtPetBreed.getText(),
                    age, (String) cmbPetGender.getSelectedItem());
            txtPetName.setText("");
            txtPetBreed.setText("");
            refreshAllViews();
            JOptionPane.showMessageDialog(this, "Pet added: " + p.getName() + " (Owner: " + owner.getFullName() + ")");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void handleViewPetHistory() {
        int row = petsTable.getSelectedRow();
        if (row < 0) {
            showError("Please select a pet from the Registered Pets table first.");
            return;
        }
        int petId = (int) petsTableModel.getValueAt(row, 0);
        JTextArea area = new JTextArea(service.getPetHistoryReport(petId), 18, 55);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Pet EMR & Vaccination History", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== TAB 2: APPOINTMENTS & QUEUE ====================
    private JPanel buildAppointmentsTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        topBar.add(new JLabel("Filter Date (YYYY-MM-DD, blank=All):"));
        txtApptDateFilter = new JTextField(LocalDate.now().toString(), 9);
        topBar.add(txtApptDateFilter);
        topBar.add(new JLabel("Status:"));
        cmbApptStatusFilter = new JComboBox<>(new String[]{"All", "Scheduled", "Checked-in", "Completed", "Cancelled"});
        topBar.add(cmbApptStatusFilter);
        JButton btnFilter = new JButton("Apply Filter");
        btnFilter.addActionListener(e -> refreshAppointmentsTable());
        JButton btnShowAll = new JButton("Show All");
        btnShowAll.addActionListener(e -> {
            txtApptDateFilter.setText("");
            cmbApptStatusFilter.setSelectedItem("All");
            refreshAppointmentsTable();
        });
        topBar.add(btnFilter);
        topBar.add(btnShowAll);
        panel.add(topBar, BorderLayout.NORTH);

        apptsTableModel = new DefaultTableModel(
                new String[]{"ID", "Date", "Slot", "Pet", "Owner", "Veterinarian", "Service", "Vitals (kg / °C)", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        apptsTable = new JTable(apptsTableModel);

        JPanel centerPanel = new JPanel(new BorderLayout(6, 6));
        centerPanel.add(wrapTitled("Appointments Schedule", new JScrollPane(apptsTable)), BorderLayout.CENTER);

        apptRowActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        JButton btnCheckIn = new JButton("Check-in & Record Vitals");
        btnCheckIn.addActionListener(e -> handleCheckIn());
        JButton btnCancelAppt = new JButton("Cancel Appointment");
        btnCancelAppt.addActionListener(e -> handleCancelAppointment());
        apptRowActions.add(btnCheckIn);
        apptRowActions.add(btnCancelAppt);
        centerPanel.add(apptRowActions, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel bookPanel = new JPanel(new GridLayout(3, 4, 8, 6));
        bookPanel.setBorder(new TitledBorder("Book New Appointment"));

        cmbBookPet = new JComboBox<>();
        cmbBookVet = new JComboBox<>();
        cmbBookService = new JComboBox<>();
        txtBookDate = new JTextField(LocalDate.now().toString());
        cmbBookSlot = new JComboBox<>(new String[]{"08:30", "09:30", "10:30", "11:30", "14:00", "15:00", "16:00"});
        txtBookReason = new JTextField();
        JButton btnBook = new JButton("Book Appointment");
        btnBook.addActionListener(e -> handleBookAppointment());

        bookPanel.add(new JLabel("Pet:")); bookPanel.add(cmbBookPet);
        bookPanel.add(new JLabel("Veterinarian:")); bookPanel.add(cmbBookVet);
        bookPanel.add(new JLabel("Service:")); bookPanel.add(cmbBookService);
        bookPanel.add(new JLabel("Date (YYYY-MM-DD):")); bookPanel.add(txtBookDate);
        bookPanel.add(new JLabel("Time Slot:")); bookPanel.add(cmbBookSlot);
        bookPanel.add(new JLabel("Reason / Notes:")); bookPanel.add(txtBookReason);

        apptBookWrapper = new JPanel(new BorderLayout(8, 0));
        apptBookWrapper.add(bookPanel, BorderLayout.CENTER);
        apptBookWrapper.add(btnBook, BorderLayout.EAST);
        panel.add(apptBookWrapper, BorderLayout.SOUTH);

        return panel;
    }

    private void handleBookAppointment() {
        try {
            Pet pet = (Pet) cmbBookPet.getSelectedItem();
            User vet = (User) cmbBookVet.getSelectedItem();
            CatalogItem srv = (CatalogItem) cmbBookService.getSelectedItem();
            Appointment a = service.bookAppointment(
                    pet.getId(), vet.getId(), srv.getId(),
                    txtBookDate.getText(), (String) cmbBookSlot.getSelectedItem(), txtBookReason.getText());
            txtBookReason.setText("");
            refreshAllViews();
            JOptionPane.showMessageDialog(this, "Appointment #" + a.getId() + " booked for " + pet.getName()
                    + " with " + vet.getFullName() + " at " + a.getDate() + " " + a.getTimeSlot());
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void handleCheckIn() {
        int row = apptsTable.getSelectedRow();
        if (row < 0) {
            showError("Please select a 'Scheduled' appointment to check in.");
            return;
        }
        int apptId = (int) apptsTableModel.getValueAt(row, 0);

        JPanel p = new JPanel(new GridLayout(2, 2, 6, 6));
        JTextField txtWeight = new JTextField("5.5");
        JTextField txtTemp = new JTextField("38.5");
        p.add(new JLabel("Weight (kg):")); p.add(txtWeight);
        p.add(new JLabel("Temperature (°C):")); p.add(txtTemp);

        int res = JOptionPane.showConfirmDialog(this, p, "Check-in & Record Vitals", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                double w = Double.parseDouble(txtWeight.getText().trim());
                double t = Double.parseDouble(txtTemp.getText().trim());
                service.checkInAppointment(apptId, w, t);
                refreshAllViews();
                JOptionPane.showMessageDialog(this, "Checked in successfully. Pet added to Veterinarian Waiting Queue.");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void handleCancelAppointment() {
        int row = apptsTable.getSelectedRow();
        if (row < 0) {
            showError("Please select an appointment to cancel.");
            return;
        }
        int apptId = (int) apptsTableModel.getValueAt(row, 0);
        try {
            service.cancelAppointment(apptId);
            refreshAllViews();
            JOptionPane.showMessageDialog(this, "Appointment #" + apptId + " marked as Cancelled.");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // ==================== TAB 3: CLINICAL EXAM / EMR & VACCINATION ====================
    private JPanel buildEmrTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        queueTableModel = new DefaultTableModel(
                new String[]{"Appt ID", "Date", "Slot", "Pet", "Owner", "Assigned Vet", "Weight (kg)", "Temp (°C)", "Reason"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        queueTable = new JTable(queueTableModel);
        queueTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectedExamLabel();
        });

        JScrollPane queueScroll = wrapTitled("Checked-in Waiting Queue (Select a patient to examine)", new JScrollPane(queueTable));
        queueScroll.setPreferredSize(new Dimension(1000, 170));
        panel.add(queueScroll, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new BorderLayout(8, 8));
        formPanel.setBorder(new TitledBorder("Clinical Examination, Prescription & Vaccination"));

        lblSelectedExam = new JLabel("Select a Checked-in pet from the queue above.");
        lblSelectedExam.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblSelectedExam.setForeground(new Color(15, 118, 110));
        formPanel.add(lblSelectedExam, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(6, 2, 8, 6));
        txtSymptoms = new JTextArea(2, 20);
        txtDiagnosis = new JTextArea(2, 20);
        txtExamNotes = new JTextField();
        chkScreeningPassed = new JCheckBox("Pet passed basic clinical screening (Required before administering vaccine)", true);

        cmbMedicine = new JComboBox<>();
        txtMedQty = new JTextField("1", 4);
        txtMedDosage = new JTextField("1 tablet twice daily", 16);
        JPanel medRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        medRow.add(cmbMedicine);
        medRow.add(new JLabel("Qty:"));
        medRow.add(txtMedQty);
        medRow.add(new JLabel("Dosage:"));
        medRow.add(txtMedDosage);

        cmbVaccine = new JComboBox<>();
        txtVacDate = new JTextField(LocalDate.now().toString(), 9);
        lblBoosterPreview = new JLabel("Next Booster: N/A");
        lblBoosterPreview.setForeground(new Color(15, 118, 110));
        cmbVaccine.addActionListener(e -> updateBoosterPreview());
        txtVacDate.addActionListener(e -> updateBoosterPreview());

        JPanel vacRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        vacRow.add(cmbVaccine);
        vacRow.add(new JLabel("Date:"));
        vacRow.add(txtVacDate);
        vacRow.add(lblBoosterPreview);

        fields.add(new JLabel("Clinical Symptoms *:")); fields.add(new JScrollPane(txtSymptoms));
        fields.add(new JLabel("Diagnosis *:")); fields.add(new JScrollPane(txtDiagnosis));
        fields.add(new JLabel("Clinical Notes:")); fields.add(txtExamNotes);
        fields.add(new JLabel("Clinical Screening:")); fields.add(chkScreeningPassed);
        fields.add(new JLabel("Prescription Medicine:")); fields.add(medRow);
        fields.add(new JLabel("Administer Vaccine:")); fields.add(vacRow);

        formPanel.add(fields, BorderLayout.CENTER);

        JButton btnCompleteEmr = new JButton("Complete Consultation & Save EMR");
        btnCompleteEmr.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCompleteEmr.addActionListener(e -> handleCompleteConsultation());
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrap.add(btnCompleteEmr);
        formPanel.add(btnWrap, BorderLayout.SOUTH);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private void updateSelectedExamLabel() {
        int row = queueTable.getSelectedRow();
        if (row < 0) {
            lblSelectedExam.setText("Select a Checked-in pet from the queue above.");
            return;
        }
        int apptId = (int) queueTableModel.getValueAt(row, 0);
        String petName = String.valueOf(queueTableModel.getValueAt(row, 3));
        lblSelectedExam.setText("Examining Appointment #" + apptId + " — Patient: " + petName);
    }

    private void updateBoosterPreview() {
        Object sel = cmbVaccine.getSelectedItem();
        if (!(sel instanceof CatalogItem)) {
            lblBoosterPreview.setText("Next Booster: N/A");
            return;
        }
        try {
            CatalogItem vac = (CatalogItem) sel;
            LocalDate date = LocalDate.parse(txtVacDate.getText().trim());
            LocalDate next = date.plusDays(vac.getBoosterCycleDays());
            lblBoosterPreview.setText("Next Booster: " + next);
        } catch (Exception ex) {
            lblBoosterPreview.setText("Next Booster: invalid date");
        }
    }

    private void handleCompleteConsultation() {
        int row = queueTable.getSelectedRow();
        if (row < 0) {
            showError("Please select a Checked-in appointment from the Waiting Queue first.");
            return;
        }
        int apptId = (int) queueTableModel.getValueAt(row, 0);
        try {
            Object medObj = cmbMedicine.getSelectedItem();
            Integer medId = (medObj instanceof CatalogItem) ? ((CatalogItem) medObj).getId() : null;
            int qty = Integer.parseInt(txtMedQty.getText().trim().isEmpty() ? "1" : txtMedQty.getText().trim());

            Object vacObj = cmbVaccine.getSelectedItem();
            Integer vacId = (vacObj instanceof CatalogItem) ? ((CatalogItem) vacObj).getId() : null;

            service.completeConsultation(
                    currentUser,
                    apptId,
                    txtSymptoms.getText(),
                    txtDiagnosis.getText(),
                    chkScreeningPassed.isSelected(),
                    txtExamNotes.getText(),
                    medId,
                    qty,
                    txtMedDosage.getText(),
                    vacId,
                    txtVacDate.getText()
            );

            txtSymptoms.setText("");
            txtDiagnosis.setText("");
            txtExamNotes.setText("");
            refreshAllViews();
            JOptionPane.showMessageDialog(this, "EMR saved & Consultation marked as Completed! Ready for Counter Invoicing.");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // ==================== TAB 4: COUNTER INVOICING ====================
    private JPanel buildInvoicingTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        invApptsTableModel = new DefaultTableModel(
                new String[]{"Appt ID", "Date", "Slot", "Pet", "Owner", "Service", "Status", "Invoice Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        invApptsTable = new JTable(invApptsTableModel);

        JPanel topPanel = new JPanel(new BorderLayout(4, 6));
        topPanel.add(new JScrollPane(invApptsTable), BorderLayout.CENTER);
        JButton btnGenerateInv = new JButton("Generate Invoice for Selected Visit");
        btnGenerateInv.addActionListener(e -> handleGenerateInvoice());
        JPanel genWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        genWrap.add(btnGenerateInv);
        topPanel.add(genWrap, BorderLayout.SOUTH);

        invoicesTableModel = new DefaultTableModel(
                new String[]{"Invoice ID", "Appt ID", "Pet", "Consult ($)", "Medicine ($)", "Vaccine ($)", "Total ($)", "Method", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        invoicesTable = new JTable(invoicesTableModel);

        JPanel bottomPanel = new JPanel(new BorderLayout(4, 6));
        bottomPanel.add(new JScrollPane(invoicesTable), BorderLayout.CENTER);
        JPanel payBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        payBar.add(new JLabel("Payment Method:"));
        cmbPaymentMethod = new JComboBox<>(new String[]{"Cash", "Bank Transfer"});
        payBar.add(cmbPaymentMethod);
        JButton btnPay = new JButton("Confirm Payment");
        btnPay.addActionListener(e -> handlePayInvoice());
        payBar.add(btnPay);
        bottomPanel.add(payBar, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                wrapTitled("Step 1: Visits (Only 'Completed' visits can be invoiced)", topPanel),
                wrapTitled("Step 2: Generated Invoices", bottomPanel));
        split.setResizeWeight(0.5);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private void handleGenerateInvoice() {
        int row = invApptsTable.getSelectedRow();
        if (row < 0) {
            showError("Please select an appointment row first.");
            return;
        }
        int apptId = (int) invApptsTableModel.getValueAt(row, 0);
        try {
            Invoice inv = service.generateInvoice(apptId);
            refreshAllViews();
            JOptionPane.showMessageDialog(this, String.format(
                    "Invoice #%d ready!\nConsultation: $%.0f\nMedicine: $%.0f\nVaccine: $%.0f\nTOTAL: $%.0f",
                    inv.getId(), inv.getConsultationFee(), inv.getMedicineFee(), inv.getVaccineFee(), inv.getTotalAmount()));
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void handlePayInvoice() {
        int row = invoicesTable.getSelectedRow();
        if (row < 0) {
            showError("Please select an invoice row first.");
            return;
        }
        int invId = (int) invoicesTableModel.getValueAt(row, 0);
        try {
            String method = (String) cmbPaymentMethod.getSelectedItem();
            service.payInvoice(invId, method);
            refreshAllViews();
            JOptionPane.showMessageDialog(this, "Invoice #" + invId + " marked as Paid via " + method + ".");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // ==================== TAB 5: BOOSTER REMINDERS ====================
    private JPanel buildRemindersTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        remindersTableModel = new DefaultTableModel(
                new String[]{"Vac ID", "Pet", "Owner & Contact", "Vaccine", "Administered", "Next Booster Date", "Due Status", "Reminder"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        remindersTable = new JTable(remindersTableModel);

        panel.add(wrapTitled("Vaccination Booster Schedule", new JScrollPane(remindersTable)), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        JButton btnSendSelected = new JButton("Send Selected Reminder");
        btnSendSelected.addActionListener(e -> handleSendSelectedReminder());
        JButton btnDailyCheck = new JButton("Run Daily Booster Check");
        btnDailyCheck.addActionListener(e -> handleRunDailyBoosterCheck());
        actions.add(btnSendSelected);
        actions.add(btnDailyCheck);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private void handleSendSelectedReminder() {
        int row = remindersTable.getSelectedRow();
        if (row < 0) {
            showError("Please select a vaccination row first.");
            return;
        }
        int vacId = (int) remindersTableModel.getValueAt(row, 0);
        try {
            service.sendReminder(vacId);
            refreshAllViews();
            JOptionPane.showMessageDialog(this, "Booster reminder sent to owner!");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void handleRunDailyBoosterCheck() {
        List<Vaccination> dispatched = service.runDailyBoosterCheck();
        refreshAllViews();
        if (dispatched.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Daily Check: No pending due boosters today.");
        } else {
            StringBuilder sb = new StringBuilder("Dispatched " + dispatched.size() + " due booster reminder(s):\n");
            for (Vaccination v : dispatched) {
                Pet pet = service.findPetById(v.getPetId());
                Owner owner = pet != null ? service.findOwnerById(pet.getOwnerId()) : null;
                CatalogItem vac = service.findCatalogById(v.getVaccineItemId());
                sb.append("- ").append(pet != null ? pet.getName() : "Pet")
                        .append(" (").append(vac != null ? vac.getName() : "Vaccine").append(")")
                        .append(" -> ").append(owner != null ? owner.getFullName() + " [" + owner.getEmail() + "]" : "Owner")
                        .append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString());
        }
    }

    // ==================== TAB 6: ADMIN - CATALOG & STAFF CONTROL ====================
    private JPanel buildAdminTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        catalogTableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Category", "Price ($)", "Unit / Cycle"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        catalogTable = new JTable(catalogTableModel);

        JPanel catForm = new JPanel(new GridLayout(6, 2, 6, 4));
        catForm.setBorder(new TitledBorder("Add / Edit Service, Medicine or Vaccine"));
        txtCatName = new JTextField();
        cmbCatCategory = new JComboBox<>(new String[]{"SERVICE", "MEDICINE", "VACCINE"});
        txtCatPrice = new JTextField("15");
        txtCatUnit = new JTextField("visit");
        txtCatCycle = new JTextField("365");

        btnSaveCatalog = new JButton("Add Catalog Item");
        btnSaveCatalog.addActionListener(e -> handleSaveCatalog());
        JButton btnLoadCatalog = new JButton("Edit Selected");
        btnLoadCatalog.addActionListener(e -> handleLoadSelectedCatalog());
        JButton btnClearCatalog = new JButton("Clear");
        btnClearCatalog.addActionListener(e -> clearCatalogForm());

        JPanel catButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        catButtons.add(btnLoadCatalog);
        catButtons.add(btnClearCatalog);
        catButtons.add(btnSaveCatalog);

        catForm.add(new JLabel("Item Name *:")); catForm.add(txtCatName);
        catForm.add(new JLabel("Category:")); catForm.add(cmbCatCategory);
        catForm.add(new JLabel("Price ($) *:")); catForm.add(txtCatPrice);
        catForm.add(new JLabel("Unit (visit/box/dose):")); catForm.add(txtCatUnit);
        catForm.add(new JLabel("Vaccine Cycle (days):")); catForm.add(txtCatCycle);
        catForm.add(new JLabel()); catForm.add(catButtons);

        JPanel leftPanel = new JPanel(new BorderLayout(6, 6));
        leftPanel.add(wrapTitled("Pricing Catalog", new JScrollPane(catalogTable)), BorderLayout.CENTER);
        leftPanel.add(catForm, BorderLayout.SOUTH);

        staffTableModel = new DefaultTableModel(
                new String[]{"ID", "Username", "Full Name", "Role", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        staffTable = new JTable(staffTableModel);

        JPanel staffActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        JButton btnToggleLock = new JButton("Lock / Unlock Selected Account");
        btnToggleLock.addActionListener(e -> handleToggleUserLock());
        staffActions.add(btnToggleLock);

        JPanel staffTableWrap = new JPanel(new BorderLayout(4, 6));
        staffTableWrap.add(new JScrollPane(staffTable), BorderLayout.CENTER);
        staffTableWrap.add(staffActions, BorderLayout.SOUTH);
        wrapTitled("Clinic Staff Accounts", staffTableWrap);

        JPanel staffForm = new JPanel(new GridLayout(5, 2, 6, 4));
        staffForm.setBorder(new TitledBorder("Create Staff Account"));
        txtStaffUsername = new JTextField();
        txtStaffPassword = new JTextField("123456");
        txtStaffFullName = new JTextField();
        cmbStaffRole = new JComboBox<>(new String[]{"Veterinarian", "Receptionist", "Admin"});
        JButton btnAddStaff = new JButton("Create Staff User");
        btnAddStaff.addActionListener(e -> handleCreateStaffUser());

        staffForm.add(new JLabel("Username *:")); staffForm.add(txtStaffUsername);
        staffForm.add(new JLabel("Password *:")); staffForm.add(txtStaffPassword);
        staffForm.add(new JLabel("Full Name *:")); staffForm.add(txtStaffFullName);
        staffForm.add(new JLabel("Role:")); staffForm.add(cmbStaffRole);
        staffForm.add(new JLabel()); staffForm.add(btnAddStaff);

        JPanel rightPanel = new JPanel(new BorderLayout(6, 6));
        rightPanel.add(staffTableWrap, BorderLayout.CENTER);
        rightPanel.add(staffForm, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setResizeWeight(0.52);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private void handleLoadSelectedCatalog() {
        int row = catalogTable.getSelectedRow();
        if (row < 0) {
            showError("Please select a catalog item row to edit.");
            return;
        }
        int id = (int) catalogTableModel.getValueAt(row, 0);
        CatalogItem item = service.findCatalogById(id);
        if (item != null) {
            editingCatalogId = item.getId();
            txtCatName.setText(item.getName());
            cmbCatCategory.setSelectedItem(item.getCategory());
            txtCatPrice.setText(String.valueOf(item.getPrice()));
            txtCatUnit.setText(item.getUnit());
            txtCatCycle.setText(String.valueOf(item.getBoosterCycleDays()));
            btnSaveCatalog.setText("Update Item #" + item.getId());
        }
    }

    private void clearCatalogForm() {
        editingCatalogId = null;
        txtCatName.setText("");
        cmbCatCategory.setSelectedItem("SERVICE");
        txtCatPrice.setText("15");
        txtCatUnit.setText("visit");
        txtCatCycle.setText("365");
        btnSaveCatalog.setText("Add Catalog Item");
    }

    private void handleSaveCatalog() {
        try {
            double price = Double.parseDouble(txtCatPrice.getText().trim());
            int cycle = Integer.parseInt(txtCatCycle.getText().trim().isEmpty() ? "365" : txtCatCycle.getText().trim());
            CatalogItem saved = service.addOrUpdateCatalogItem(
                    currentUser,
                    editingCatalogId,
                    txtCatName.getText(),
                    (String) cmbCatCategory.getSelectedItem(),
                    price,
                    txtCatUnit.getText(),
                    cycle);
            clearCatalogForm();
            refreshAllViews();
            JOptionPane.showMessageDialog(this, "Saved catalog item: " + saved.getName() + " ($" + saved.getPrice() + ")");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void handleCreateStaffUser() {
        try {
            User created = service.addStaffUser(
                    currentUser,
                    txtStaffUsername.getText(),
                    txtStaffPassword.getText(),
                    txtStaffFullName.getText(),
                    (String) cmbStaffRole.getSelectedItem());
            txtStaffUsername.setText("");
            txtStaffFullName.setText("");
            refreshAllViews();
            JOptionPane.showMessageDialog(this, "Created staff account: " + created.getUsername() + " (" + created.getRole() + ")");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void handleToggleUserLock() {
        int row = staffTable.getSelectedRow();
        if (row < 0) {
            showError("Please select a staff account row first.");
            return;
        }
        int targetId = (int) staffTableModel.getValueAt(row, 0);
        try {
            User updated = service.toggleUserLock(currentUser, targetId);
            refreshAllViews();
            JOptionPane.showMessageDialog(this, "Account \"" + updated.getUsername() + "\" is now "
                    + (updated.isLocked() ? "LOCKED" : "ACTIVE") + ".");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // ==================== REFRESH HELPERS ====================
    private void refreshAllViews() {
        refreshOwnersTable();
        refreshPetsTable();
        refreshCombos();
        refreshAppointmentsTable();
        refreshQueueTable();
        refreshInvoicingTables();
        refreshRemindersTable();
        refreshAdminTables();
        updateBoosterPreview();
    }

    private void refreshOwnersTable() {
        ownersTableModel.setRowCount(0);
        for (Owner o : service.searchOwners(txtOwnerSearch.getText())) {
            ownersTableModel.addRow(new Object[]{o.getId(), o.getFullName(), o.getPhone(), o.getEmail(), o.getAddress()});
        }
    }

    private void refreshPetsTable() {
        petsTableModel.setRowCount(0);
        for (Pet p : service.getPets()) {
            Owner o = service.findOwnerById(p.getOwnerId());
            petsTableModel.addRow(new Object[]{p.getId(), p.getName(), p.getSpecies(), p.getBreed(), p.getAge(),
                    p.getGender(), o != null ? o.getFullName() : "N/A"});
        }
    }

    private void refreshCombos() {
        cmbPetOwner.removeAllItems();
        for (Owner o : service.getOwners()) cmbPetOwner.addItem(o);

        cmbBookPet.removeAllItems();
        for (Pet p : service.getPets()) cmbBookPet.addItem(p);

        cmbBookVet.removeAllItems();
        for (User v : service.getVets()) cmbBookVet.addItem(v);

        cmbBookService.removeAllItems();
        for (CatalogItem s : service.getCatalogByCategory("SERVICE")) cmbBookService.addItem(s);

        cmbMedicine.removeAllItems();
        cmbMedicine.addItem("-- None --");
        for (CatalogItem m : service.getCatalogByCategory("MEDICINE")) cmbMedicine.addItem(m);

        cmbVaccine.removeAllItems();
        cmbVaccine.addItem("-- None --");
        for (CatalogItem vac : service.getCatalogByCategory("VACCINE")) cmbVaccine.addItem(vac);
    }

    private void refreshAppointmentsTable() {
        apptsTableModel.setRowCount(0);
        List<Appointment> list = service.getAppointmentsFiltered(txtApptDateFilter.getText(),
                (String) cmbApptStatusFilter.getSelectedItem());
        for (Appointment a : list) {
            Pet p = service.findPetById(a.getPetId());
            Owner o = p != null ? service.findOwnerById(p.getOwnerId()) : null;
            User vet = service.findUserById(a.getVetId());
            CatalogItem srv = service.findCatalogById(a.getServiceId());
            String vitals = a.getWeightKg() > 0 ? (a.getWeightKg() + " kg / " + a.getTemperatureC() + " °C") : "—";
            apptsTableModel.addRow(new Object[]{
                    a.getId(), a.getDate(), a.getTimeSlot(),
                    p != null ? p.getName() + " (" + p.getSpecies() + ")" : "N/A",
                    o != null ? o.getFullName() : "N/A",
                    vet != null ? vet.getFullName() : "N/A",
                    srv != null ? srv.getName() : "N/A",
                    vitals,
                    a.getStatus()
            });
        }
    }

    private void refreshQueueTable() {
        queueTableModel.setRowCount(0);
        for (Appointment a : service.getAppointmentsFiltered("", "Checked-in")) {
            Pet p = service.findPetById(a.getPetId());
            Owner o = p != null ? service.findOwnerById(p.getOwnerId()) : null;
            User vet = service.findUserById(a.getVetId());
            queueTableModel.addRow(new Object[]{
                    a.getId(), a.getDate(), a.getTimeSlot(),
                    p != null ? p.getName() + " (" + p.getSpecies() + ")" : "N/A",
                    o != null ? o.getFullName() : "N/A",
                    vet != null ? vet.getFullName() : "N/A",
                    a.getWeightKg(), a.getTemperatureC(), a.getReason()
            });
        }
        if (queueTableModel.getRowCount() > 0) {
            queueTable.setRowSelectionInterval(0, 0);
        }
        updateSelectedExamLabel();
    }

    private void refreshInvoicingTables() {
        invApptsTableModel.setRowCount(0);
        for (Appointment a : service.getAppointments()) {
            Pet p = service.findPetById(a.getPetId());
            Owner o = p != null ? service.findOwnerById(p.getOwnerId()) : null;
            CatalogItem srv = service.findCatalogById(a.getServiceId());
            Invoice inv = service.findInvoiceByAppointmentId(a.getId());
            invApptsTableModel.addRow(new Object[]{
                    a.getId(), a.getDate(), a.getTimeSlot(),
                    p != null ? p.getName() : "N/A",
                    o != null ? o.getFullName() : "N/A",
                    srv != null ? srv.getName() + " ($" + (int) srv.getPrice() + ")" : "N/A",
                    a.getStatus(),
                    inv != null ? ("INV #" + inv.getId() + " (" + inv.getStatus() + ")") : "Not generated"
            });
        }

        invoicesTableModel.setRowCount(0);
        for (Invoice inv : service.getInvoices()) {
            Appointment a = service.findAppointmentById(inv.getAppointmentId());
            Pet p = a != null ? service.findPetById(a.getPetId()) : null;
            invoicesTableModel.addRow(new Object[]{
                    inv.getId(), inv.getAppointmentId(),
                    p != null ? p.getName() : "N/A",
                    inv.getConsultationFee(), inv.getMedicineFee(), inv.getVaccineFee(),
                    inv.getTotalAmount(), inv.getPaymentMethod(), inv.getStatus()
            });
        }
    }

    private void refreshRemindersTable() {
        remindersTableModel.setRowCount(0);
        String today = LocalDate.now().toString();
        for (Vaccination v : service.getVaccinations()) {
            Pet p = service.findPetById(v.getPetId());
            Owner o = p != null ? service.findOwnerById(p.getOwnerId()) : null;
            CatalogItem vac = service.findCatalogById(v.getVaccineItemId());
            String dueStatus = v.getNextBoosterDate().compareTo(today) < 0 ? "Overdue"
                    : (v.getNextBoosterDate().equals(today) ? "Due Today" : "Upcoming");
            remindersTableModel.addRow(new Object[]{
                    v.getId(),
                    p != null ? p.getName() + " (" + p.getSpecies() + ")" : "N/A",
                    o != null ? o.getFullName() + " (" + o.getEmail() + ")" : "N/A",
                    vac != null ? vac.getName() : "N/A",
                    v.getDateAdministered(),
                    v.getNextBoosterDate(),
                    dueStatus,
                    v.isReminderSent() ? "Sent" : "Pending"
            });
        }
    }

    private void refreshAdminTables() {
        catalogTableModel.setRowCount(0);
        for (CatalogItem c : service.getAllCatalogItems()) {
            String unitOrCycle = "VACCINE".equals(c.getCategory())
                    ? (c.getUnit() + " (" + c.getBoosterCycleDays() + "d cycle)")
                    : c.getUnit();
            catalogTableModel.addRow(new Object[]{c.getId(), c.getName(), c.getCategory(), c.getPrice(), unitOrCycle});
        }

        staffTableModel.setRowCount(0);
        for (User u : service.getAllUsers()) {
            staffTableModel.addRow(new Object[]{
                    u.getId(), u.getUsername(), u.getFullName(), u.getRole(),
                    u.isLocked() ? "Locked" : "Active"
            });
        }
    }

    private <T extends JComponent> T wrapTitled(String title, T inner) {
        inner.setBorder(new TitledBorder(title));
        return inner;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Alert", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new VetCareSwingApp().setVisible(true));
    }
}
