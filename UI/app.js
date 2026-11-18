/**
 * VetCare Clinic Management Dashboard
 * Modern SaaS Web Application Logic (English UI with Role-Based Access Control)
 */

// Initialize Lucide Icons on DOM ready
document.addEventListener('DOMContentLoaded', () => {
  if (window.lucide) {
    window.lucide.createIcons();
  }
  startLiveClock();
  setupKeyboardShortcuts();
  initSampleData();
  setRole('receptionist'); // Default view: Receptionist
});

/* ========================================================================= */
/* 1. ROLE-BASED ACCESS CONTROL (UC-01, BR-03, BR-05, FR-03, FR-10)          */
/* ========================================================================= */
let currentRole = 'receptionist'; // 'receptionist' | 'veterinarian' | 'admin'

const roleConfigs = {
  receptionist: {
    name: 'Sarah Jenkins',
    title: 'Front-Desk Receptionist',
    roleLabel: 'Receptionist',
    avatar: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=100&h=100&fit=crop&crop=faces',
    bannerText: 'ROLE: RECEPTIONIST — Front-Desk Intake, Scheduling (UC-03), Vitals Check-in (FR-13) & Cashier Invoicing (UC-06). [Restricted: Clinical EMR Finalization locked per BR-03]',
    bannerBg: 'bg-teal-50 border-teal-200 text-teal-800',
    allowedScreens: ['screen-dashboard', 'screen-vaccine', 'screen-billing', 'screen-patients'],
    restrictedNotice: 'Clinical EMR modification is restricted to Veterinarians only (Rule BR-03).'
  },
  veterinarian: {
    name: 'Dr. Minh Khoi, DVM',
    title: 'Lead Veterinarian',
    roleLabel: 'Veterinarian',
    avatar: 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=100&h=100&fit=crop&crop=faces',
    bannerText: 'ROLE: VETERINARIAN — Clinical Examinations, Definitive Diagnosis, Prescriptions (UC-04) & Vaccinations (UC-05, BR-03, BR-04). [Restricted: Front-Desk Billing & Admin Catalog]',
    bannerBg: 'bg-emerald-50 border-emerald-200 text-emerald-800',
    allowedScreens: ['screen-dashboard', 'screen-emr', 'screen-vaccine', 'screen-patients'],
    restrictedNotice: 'Front-Desk cashier checkout is handled by Reception staff (Rule BR-05).'
  },
  admin: {
    name: 'Clinic Manager (Admin)',
    title: 'Administrator & Operations',
    roleLabel: 'Clinic Admin',
    avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&h=100&fit=crop&crop=faces',
    bannerText: 'ROLE: ADMIN (CLINIC MANAGER) — Service & Medicine Pricing Catalog (FR-03), Staff Account Lock/Unlock (FR-10) & Clinic Oversight. [Restricted: Cannot finalize clinical diagnoses]',
    bannerBg: 'bg-indigo-50 border-indigo-200 text-indigo-800',
    allowedScreens: ['screen-dashboard', 'screen-emr', 'screen-vaccine', 'screen-billing', 'screen-admin', 'screen-patients'],
    restrictedNotice: 'Admin oversight mode active.'
  }
};

function setRole(roleKey) {
  currentRole = roleKey;
  const config = roleConfigs[roleKey];
  if (!config) return;

  // Update Buttons
  ['receptionist', 'veterinarian', 'admin'].forEach(r => {
    const btn = document.getElementById(`btn-role-${r}`);
    if (btn) {
      if (r === roleKey) {
        btn.className = 'px-3 py-1.5 rounded-md bg-white text-teal-800 font-bold shadow-sm transition border border-teal-200';
      } else {
        btn.className = 'px-3 py-1.5 rounded-md text-slate-600 hover:text-slate-900 font-medium transition';
      }
    }
  });

  // Update Topbar Profile
  const userNameEl = document.getElementById('user-profile-name');
  const userTitleEl = document.getElementById('user-profile-title');
  const userAvatarEl = document.getElementById('user-profile-avatar');
  if (userNameEl) userNameEl.textContent = config.name;
  if (userTitleEl) userTitleEl.textContent = config.title;
  if (userAvatarEl) userAvatarEl.src = config.avatar;

  // Update Role Banner
  const banner = document.getElementById('role-permission-banner');
  const bannerText = document.getElementById('role-banner-content');
  if (banner && bannerText) {
    banner.className = `px-6 py-2 border-b text-xs font-medium flex items-center justify-between transition-colors ${config.bannerBg}`;
    bannerText.innerHTML = `<strong>${config.bannerText}</strong>`;
  }

  // Update Sidebar Menu Permissions
  applySidebarRolePermissions();

  // If current active screen is not allowed in new role, jump to dashboard
  const activeScreen = getCurrentActiveScreen();
  if (!config.allowedScreens.includes(activeScreen)) {
    navigateTo('screen-dashboard');
    showToast(`Switched to ${config.roleLabel} view.`);
  } else {
    showToast(`Active Role: ${config.roleLabel}`);
  }
}

function applySidebarRolePermissions() {
  const config = roleConfigs[currentRole];
  
  // EMR menu item lock for Receptionist (BR-03)
  const navEmr = document.getElementById('nav-emr');
  const emrBadge = document.getElementById('badge-emr-role');
  if (navEmr) {
    if (currentRole === 'receptionist') {
      navEmr.classList.add('opacity-50');
      if (emrBadge) {
        emrBadge.textContent = 'Vet Only';
        emrBadge.className = 'text-[10px] px-1.5 py-0.5 rounded bg-amber-100 text-amber-800 font-semibold';
      }
    } else {
      navEmr.classList.remove('opacity-50');
      if (emrBadge) {
        emrBadge.textContent = 'Active';
        emrBadge.className = 'text-[10px] px-1.5 py-0.5 rounded bg-emerald-100 text-emerald-800 font-semibold';
      }
    }
  }

  // Billing menu item lock for Vet (BR-05)
  const navBilling = document.getElementById('nav-billing');
  const billingBadge = document.getElementById('badge-billing-role');
  if (navBilling) {
    if (currentRole === 'veterinarian') {
      navBilling.classList.add('opacity-50');
      if (billingBadge) {
        billingBadge.textContent = 'Front-Desk';
        billingBadge.className = 'text-[10px] px-1.5 py-0.5 rounded bg-slate-200 text-slate-700 font-semibold';
      }
    } else {
      navBilling.classList.remove('opacity-50');
      if (billingBadge) {
        billingBadge.textContent = 'Open';
        billingBadge.className = 'text-[10px] px-1.5 py-0.5 rounded bg-teal-100 text-teal-800 font-semibold';
      }
    }
  }

  // Admin menu item visibility
  const navAdmin = document.getElementById('nav-admin');
  if (navAdmin) {
    if (currentRole === 'admin') {
      navAdmin.classList.remove('hidden');
    } else {
      navAdmin.classList.add('hidden');
    }
  }
}

function getCurrentActiveScreen() {
  for (let s of screens) {
    const el = document.getElementById(s);
    if (el && el.style.display !== 'none') return s;
  }
  return 'screen-dashboard';
}

/* ========================================================================= */
/* 2. NAVIGATION & SCREEN VIEW MANAGEMENT                                    */
/* ========================================================================= */
const screens = ['screen-dashboard', 'screen-emr', 'screen-vaccine', 'screen-billing', 'screen-admin', 'screen-patients'];
const screenTitles = {
  'screen-dashboard': 'Clinic Dashboard & Patient Intake Queue',
  'screen-emr': 'Clinical Examination & Electronic Medical Record (EMR)',
  'screen-vaccine': 'Vaccination Passport & Periodic Booster Tracker',
  'screen-billing': 'Cashier Invoicing & Service Fee Checkout',
  'screen-admin': 'System Pricing Catalog & Staff Access Control',
  'screen-patients': 'Customer & Pet Medical Directory'
};

const navItems = {
  'screen-dashboard': 'nav-dashboard',
  'screen-emr': 'nav-emr',
  'screen-vaccine': 'nav-vaccine',
  'screen-billing': 'nav-billing',
  'screen-admin': 'nav-admin',
  'screen-patients': 'nav-patients'
};

function navigateTo(screenId) {
  // Check role authorization
  const config = roleConfigs[currentRole];
  if (!config.allowedScreens.includes(screenId)) {
    if (screenId === 'screen-emr' && currentRole === 'receptionist') {
      showToast('⚠️ Rule BR-03: Receptionists cannot finalize or edit EMRs. Switch to Veterinarian.');
      return;
    }
    if (screenId === 'screen-billing' && currentRole === 'veterinarian') {
      showToast('⚠️ Rule BR-05: Invoicing is reserved for Front-Desk Cashiers.');
      return;
    }
    if (screenId === 'screen-admin' && currentRole !== 'admin') {
      showToast('⚠️ Access Denied: Admin privileges required (UC-07).');
      return;
    }
  }

  // Hide all screens
  screens.forEach(id => {
    const el = document.getElementById(id);
    if (el) {
      el.classList.remove('active');
      el.style.display = 'none';
    }
  });

  // Show target screen
  const target = document.getElementById(screenId);
  if (target) {
    target.style.display = 'block';
    setTimeout(() => {
      target.classList.add('active');
    }, 10);
  }

  // Update Topbar Breadcrumb
  const breadcrumb = document.getElementById('topbar-breadcrumb');
  if (breadcrumb && screenTitles[screenId]) {
    breadcrumb.textContent = screenTitles[screenId];
  }

  // Update Sidebar active state
  Object.values(navItems).forEach(navId => {
    const navBtn = document.getElementById(navId);
    if (navBtn) {
      navBtn.classList.remove('bg-teal-50', 'text-teal-700', 'font-semibold');
      navBtn.classList.add('text-slate-600');
    }
  });

  const activeNavBtn = document.getElementById(navItems[screenId]);
  if (activeNavBtn) {
    activeNavBtn.classList.add('bg-teal-50', 'text-teal-700', 'font-semibold');
    activeNavBtn.classList.remove('text-slate-600');
  }

  window.scrollTo({ top: 0, behavior: 'smooth' });

  if (window.lucide) {
    window.lucide.createIcons();
  }
}

/* ========================================================================= */
/* 3. SAMPLE DATA & APPOINTMENT FLOW LOGIC                                    */
/* ========================================================================= */
let appointmentsData = [
  { id: 1, time: '08:30', pet: 'Milo', breed: 'Golden Retriever', owner: 'Nguyen Minh Dang', phone: '0901234567', vet: 'Dr. Minh Khoi', service: 'General Exam & Vaccine', vitals: '26.5 kg / 38.5 °C', status: 'Completed', invoicePaid: true },
  { id: 2, time: '09:30', pet: 'Bong', breed: 'Poodle', owner: 'Nguyen Van A', phone: '0901234567', vet: 'Dr. Minh Khoi', service: 'General Exam & Allergy', vitals: '4.2 kg / 38.6 °C', status: 'Checked-in', invoicePaid: false },
  { id: 3, time: '10:30', pet: 'Miu', breed: 'British Shorthair', owner: 'Tran Thi B', phone: '0988776655', vet: 'Dr. Gia Huy', service: 'Routine Ultrasound', vitals: '3.8 kg / 38.4 °C', status: 'Checked-in', invoicePaid: false },
  { id: 4, time: '11:15', pet: 'Lu', breed: 'Corgi', owner: 'Le Van Cuong', phone: '0977665544', vet: 'Dr. Minh Khoi', service: 'Minor Surgery Follow-up', vitals: '11.0 kg / 38.8 °C', status: 'Scheduled', invoicePaid: false },
  { id: 5, time: '14:00', pet: 'Kiki', breed: 'Poodle', owner: 'Tran Thi Mai', phone: '0912345678', vet: 'Dr. Gia Huy', service: 'Vaccine 5-in-1', vitals: '—', status: 'Scheduled', invoicePaid: false }
];

function initSampleData() {
  renderAppointmentsTable();
}

function renderAppointmentsTable() {
  const tbody = document.getElementById('dashboard-queue-tbody');
  if (!tbody) return;
  tbody.innerHTML = '';

  appointmentsData.forEach(row => {
    let badgeClass = 'badge-scheduled';
    let statusLabel = 'Scheduled';
    if (row.status === 'Checked-in') { badgeClass = 'badge-checkedin'; statusLabel = 'Checked-in'; }
    if (row.status === 'Completed') { badgeClass = 'badge-completed'; statusLabel = 'Completed'; }
    if (row.status === 'Cancelled') { badgeClass = 'badge-cancelled'; statusLabel = 'Cancelled'; }

    let actionBtn = '';
    if (row.status === 'Scheduled') {
      actionBtn = `<button onclick="openCheckinModal(${row.id})" class="px-2.5 py-1 text-xs font-semibold rounded bg-teal-50 text-teal-700 hover:bg-teal-100 transition">Check-in (FR-13)</button>`;
    } else if (row.status === 'Checked-in') {
      actionBtn = `<button onclick="startExamForPatient('${row.pet}')" class="px-2.5 py-1 text-xs font-semibold rounded bg-emerald-50 text-emerald-700 hover:bg-emerald-100 transition">Start Exam</button>`;
    } else if (row.status === 'Completed') {
      actionBtn = `<button onclick="navigateTo('screen-billing')" class="px-2.5 py-1 text-xs font-semibold rounded bg-slate-100 text-slate-700 hover:bg-slate-200 transition">View Invoice</button>`;
    }

    const tr = document.createElement('tr');
    tr.className = 'hover:bg-slate-50 transition border-b border-slate-100';
    tr.innerHTML = `
      <td class="px-4 py-3 font-semibold text-slate-900">${row.time}</td>
      <td class="px-4 py-3">
        <div class="font-bold text-slate-900 flex items-center gap-1.5 cursor-pointer hover:text-teal-700" onclick="startExamForPatient('${row.pet}')">
          <span>🐾 ${row.pet}</span>
          ${row.pet === 'Bong' ? '<span class="text-[10px] px-1.5 py-0.2 rounded bg-rose-100 text-rose-700 font-bold">Allergy</span>' : ''}
        </div>
        <div class="text-[11px] text-slate-500">${row.owner} • ${row.phone}</div>
      </td>
      <td class="px-4 py-3 text-xs text-slate-600">${row.breed}</td>
      <td class="px-4 py-3 text-xs font-medium text-slate-800">${row.vet}</td>
      <td class="px-4 py-3 text-xs text-slate-600">${row.service}</td>
      <td class="px-4 py-3 text-xs font-semibold text-slate-700">${row.vitals}</td>
      <td class="px-4 py-3"><span class="badge ${badgeClass}">${statusLabel}</span></td>
      <td class="px-4 py-3 text-right">${actionBtn}</td>
    `;
    tbody.appendChild(tr);
  });
}

function startExamForPatient(petName) {
  if (currentRole === 'receptionist') {
    showToast('⚠️ Rule BR-03: Switching to Veterinarian view to open clinical examination.');
    setRole('veterinarian');
  }
  showToast(`Opening clinical EMR for patient: ${petName}`);
  navigateTo('screen-emr');
}

/* ========================================================================= */
/* 4. MODALS & FORMS (BOOKING, CHECK-IN, INVOICING)                           */
/* ========================================================================= */
function openNewAppointmentModal() {
  const modal = document.getElementById('modalNewAppointment');
  if (modal) {
    modal.classList.remove('hidden');
    modal.classList.add('flex');
  }
}

function closeNewAppointmentModal() {
  const modal = document.getElementById('modalNewAppointment');
  if (modal) {
    modal.classList.add('hidden');
    modal.classList.remove('flex');
  }
}

function handleConfirmBooking() {
  const ownerName = document.getElementById('book-owner-name')?.value || 'Guest';
  const ownerPhone = document.getElementById('book-owner-phone')?.value || '0900000000';
  const petName = document.getElementById('book-pet-name')?.value || 'Pet';
  const vet = document.getElementById('book-vet-select')?.value || 'Dr. Minh Khoi';
  const slot = document.getElementById('book-time-slot')?.value || '14:30';

  // BR-02 Conflict Check
  const conflict = appointmentsData.find(a => a.vet === vet && a.time === slot && a.status !== 'Cancelled');
  if (conflict) {
    alert(`❌ [RULE BR-02 CONFLICT]: ${vet} is already fully booked at ${slot}! Please select another slot or another veterinarian.`);
    return;
  }

  appointmentsData.push({
    id: appointmentsData.length + 1,
    time: slot,
    pet: petName,
    breed: 'Canine/Feline',
    owner: ownerName,
    phone: ownerPhone,
    vet: vet,
    service: 'General Consultation',
    vitals: '—',
    status: 'Scheduled',
    invoicePaid: false
  });

  closeNewAppointmentModal();
  renderAppointmentsTable();
  showToast(`✅ Booked visit for ${petName} with ${vet} at ${slot}!`);
}

function openCheckinModal(apptId) {
  const target = appointmentsData.find(a => a.id === apptId);
  if (!target) return;
  const weight = prompt(`Check-in for ${target.pet} (Owner: ${target.owner}).\nEnter Body Weight in kg (FR-13):`, '4.5');
  if (weight === null) return;
  const temp = prompt(`Enter Body Temperature in °C (FR-13):`, '38.6');
  if (temp === null) return;

  target.status = 'Checked-in';
  target.vitals = `${weight} kg / ${temp} °C`;
  renderAppointmentsTable();
  showToast(`✅ Patient ${target.pet} checked-in! Placed into Veterinarian Queue.`);
}

function handleFinalizeEmr() {
  if (currentRole === 'receptionist') {
    alert('❌ [RULE BR-03 RESTRICTION]: Only licensed Veterinarians can sign and finalize clinical EMRs!');
    return;
  }

  // Pre-vaccination screening gate (BR-04)
  const screeningPassed = document.getElementById('chk-screening-passed')?.checked;
  const vaccineItem = document.getElementById('select-vaccine-item')?.value;
  if (vaccineItem && !screeningPassed) {
    alert('❌ [RULE BR-04 SCREENING GATE]: Cannot administer vaccine! Patient did not pass clinical temperature screening.');
    return;
  }

  // Mark Bong as completed
  const target = appointmentsData.find(a => a.pet === 'Bong');
  if (target) {
    target.status = 'Completed';
  }
  renderAppointmentsTable();
  alert('✅ [EMR SIGNED]: Consultation completed and signed by Dr. Minh Khoi!\nVisit fee transferred to Cashier for Invoicing (UC-06).');
  setRole('receptionist');
  navigateTo('screen-billing');
}

function handleConfirmPayment() {
  const target = appointmentsData.find(a => a.pet === 'Bong');
  if (target) {
    target.invoicePaid = true;
  }
  const badge = document.getElementById('billing-status-badge');
  if (badge) {
    badge.className = 'badge badge-completed';
    badge.textContent = 'Paid (Settled)';
  }
  alert('💵 Payment collected successfully! Official receipt printed.');
}

function openPrintInvoiceModal() {
  const modal = document.getElementById('modalPrintInvoice');
  if (modal) {
    modal.classList.remove('hidden');
    modal.classList.add('flex');
  }
}

function closePrintInvoiceModal() {
  const modal = document.getElementById('modalPrintInvoice');
  if (modal) {
    modal.classList.add('hidden');
    modal.classList.remove('flex');
  }
}

/* ========================================================================= */
/* 5. UTILITIES: TOASTS, CLOCK & KEYBOARD SHORTCUTS                          */
/* ========================================================================= */
function showToast(message) {
  const container = document.getElementById('toast-container');
  if (!container) return;

  const toast = document.createElement('div');
  toast.className = 'px-4 py-3 bg-slate-900 text-white text-xs font-semibold rounded-xl shadow-xl flex items-center gap-2 transform transition-all duration-300 translate-y-4 opacity-0 border border-slate-700';
  toast.innerHTML = `<span>🔔</span><span>${message}</span>`;

  container.appendChild(toast);

  setTimeout(() => {
    toast.classList.remove('translate-y-4', 'opacity-0');
  }, 20);

  setTimeout(() => {
    toast.classList.add('translate-y-4', 'opacity-0');
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

function startLiveClock() {
  const clockEl = document.getElementById('topbar-live-clock');
  function update() {
    if (clockEl) {
      const now = new Date();
      clockEl.textContent = now.toLocaleTimeString('en-US', { hour12: false });
    }
  }
  setInterval(update, 1000);
  update();
}

function setupKeyboardShortcuts() {
  document.addEventListener('keydown', (e) => {
    if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k') {
      e.preventDefault();
      const searchInput = document.getElementById('global-quick-search');
      if (searchInput) searchInput.focus();
    }
  });
}
