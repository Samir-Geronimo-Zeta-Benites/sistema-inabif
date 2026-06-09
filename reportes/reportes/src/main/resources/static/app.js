/* ===================================================
   sistema-inabif / reportes — app.js
   =================================================== */
let currentModal = "";
const REMEMBER_KEY = "inabif_remember_user";
const USER_NAME_KEY = "inabif_user_name";
const USER_EMAIL_KEY = "inabif_user_email";
let loadingBar = null;
let loadingCount = 0;

/* ------- FORM TEMPLATES ------- */
const formTemplates = {
  "Atendidos_Servicio": `
    <div class="modal-form-grid">
      <div>
        <label class="modal-label" for="form-id-usu">ID_USU</label>
        <input class="modal-input" id="form-id-usu" type="text" placeholder="Ingrese ID_USU">
      </div>
      <div>
        <label class="modal-label" for="form-fec-ing">Fecha de Ingreso</label>
        <input class="modal-input" id="form-fec-ing" type="date">
      </div>
      <div class="full">
        <label class="modal-label" for="form-observacion">Observaciones</label>
        <textarea class="modal-textarea" id="form-observacion" placeholder="Notas adicionales..."></textarea>
      </div>
    </div>`,
  "Beneficiarios_Asistencia_clean": `
    <div class="modal-form-grid">
      <div>
        <label class="modal-label" for="form-id-usu">ID_USU</label>
        <input class="modal-input" id="form-id-usu" type="text" placeholder="Ingrese ID_USU">
      </div>
      <div>
        <label class="modal-label" for="form-fec-ing">Fecha Referencial</label>
        <input class="modal-input" id="form-fec-ing" type="date">
      </div>
      <div class="full">
        <label class="modal-label" for="form-observacion">Observaciones</label>
        <textarea class="modal-textarea" id="form-observacion" placeholder="Notas adicionales..."></textarea>
      </div>
    </div>`,
  "Beneficiarios_Atendidos_clean": `
    <div class="modal-form-grid">
      <div>
        <label class="modal-label" for="form-id-usu">ID_USU</label>
        <input class="modal-input" id="form-id-usu" type="text" placeholder="Ingrese ID_USU">
      </div>
      <div>
        <label class="modal-label" for="form-fec-ing">Fecha Referencial</label>
        <input class="modal-input" id="form-fec-ing" type="date">
      </div>
      <div class="full">
        <label class="modal-label" for="form-observacion">Observaciones</label>
        <textarea class="modal-textarea" id="form-observacion" placeholder="Notas adicionales..."></textarea>
      </div>
    </div>`
};

/* ===================================================
   LOADING BAR
   =================================================== */
function showLoading() {
  if (!loadingBar) loadingBar = document.getElementById("loading-bar");
  loadingCount++;
  if (loadingCount === 1) loadingBar.classList.add("active");
}

function hideLoading() {
  loadingCount = Math.max(0, loadingCount - 1);
  if (loadingCount === 0) loadingBar.classList.remove("active");
}

/* ===================================================
   SKELETON LOADING STATES
   =================================================== */
function showSkeleton(id) {
  const el = document.getElementById(id);
  if (el) el.classList.remove("hidden");
}

function hideSkeleton(id) {
  const el = document.getElementById(id);
  if (el) el.classList.add("hidden");
}

function showDashboardSkeletons() {
  for (let i = 1; i <= 4; i++) showSkeleton("skel-kpi-" + i);
  for (let i = 1; i <= 5; i++) showSkeleton("skel-chart-" + i);
}

function hideDashboardSkeletons() {
  for (let i = 1; i <= 4; i++) hideSkeleton("skel-kpi-" + i);
  for (let i = 1; i <= 5; i++) hideSkeleton("skel-chart-" + i);
}

/* ===================================================
   LOGIN — Password Toggle, Validation, Remember Me
   =================================================== */
document.addEventListener("DOMContentLoaded", function () {

  /* Password toggle */
  const toggleBtn = document.getElementById("pass-toggle-btn");
  const passInput = document.getElementById("inp-pass");
  if (toggleBtn && passInput) {
    toggleBtn.addEventListener("click", function () {
      const visible = passInput.type === "text";
      passInput.type = visible ? "password" : "text";
      toggleBtn.setAttribute("aria-label", visible ? "Mostrar contraseña" : "Ocultar contraseña");
      toggleBtn.setAttribute("aria-pressed", !visible);
      toggleBtn.classList.toggle("visible", !visible);
      toggleBtn.querySelector("i").className = visible ? "ti ti-eye" : "ti ti-eye-off";
      passInput.focus();
    });
  }

  /* Remember user */
  const userInput = document.getElementById("inp-user");
  const rememberCheck = document.getElementById("remember-check");
  const savedUser = localStorage.getItem(REMEMBER_KEY);
  if (savedUser && userInput) {
    userInput.value = savedUser;
    if (rememberCheck) rememberCheck.checked = true;
    if (passInput) passInput.focus();
  } else {
    if (userInput) userInput.focus();
  }

  /* Keyboard: Enter to login */
  document.addEventListener("keydown", function (e) {
    if (e.key === "Enter") {
      const login = document.getElementById("screen-login");
      if (login && login.style.display !== "none") doLogin();
    }
  });

  /* Theme restore */
  const savedTheme = localStorage.getItem("inabif_theme");
  if (savedTheme) {
    document.documentElement.setAttribute("data-theme", savedTheme);
    updateThemeIcon(savedTheme);
  }

  /* User data restore */
  const savedName = localStorage.getItem(USER_NAME_KEY);
  const savedEmail = localStorage.getItem(USER_EMAIL_KEY);
  if (savedName) updateUserUI(savedName, savedEmail);

  /* Report filter restore */
  const savedTbl = localStorage.getItem("inabif_filter_table");
  const savedMon = localStorage.getItem("inabif_filter_month");
  const savedYr = localStorage.getItem("inabif_filter_year");
  if (savedTbl) document.getElementById("sel-table").value = savedTbl;
  if (savedMon) document.getElementById("sel-month").value = savedMon;
  if (savedYr) document.getElementById("sel-year").value = savedYr;
});

async function doLogin() {
  const user = document.getElementById("inp-user").value.trim();
  const password = document.getElementById("inp-pass").value;
  const btn = document.getElementById("btn-login");
  const rememberCheck = document.getElementById("remember-check");

  /* Validación cliente */
  if (!user) {
    showToast("Ingrese su usuario", "error");
    document.getElementById("inp-user").focus();
    return;
  }
  if (!password) {
    showToast("Ingrese su contraseña", "error");
    document.getElementById("inp-pass").focus();
    return;
  }
  if (password.length < 3) {
    showToast("Contraseña muy corta", "error");
    document.getElementById("inp-pass").focus();
    return;
  }

  btn.classList.add("loading");
  btn.disabled = true;
  showLoading();

  try {
    const response = await fetch("/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ user, password })
    });

    const data = await response.json();

    if (!data.success) {
      showToast(data.message || "Credenciales incorrectas", "error");
      return;
    }

    /* Guardar o limpiar remember */
    if (rememberCheck && rememberCheck.checked) {
      localStorage.setItem(REMEMBER_KEY, user);
    } else {
      localStorage.removeItem(REMEMBER_KEY);
    }

    /* Guardar datos del usuario */
    const nombre = data.nombre || user;
    const email = data.email || "";
    localStorage.setItem(USER_NAME_KEY, nombre);
    localStorage.setItem(USER_EMAIL_KEY, email);
    updateUserUI(nombre, email);

    /* Transición */
    document.getElementById("screen-login").style.display = "none";
    const dash = document.getElementById("screen-dashboard");
    dash.style.display = "flex";
    dash.style.flexDirection = "column";

    showSection("tables");
    showToast("Bienvenido al sistema", "ok");

  } catch (error) {
    console.error(error);
    showToast("Error al iniciar sesión", "error");
  } finally {
    btn.classList.remove("loading");
    btn.disabled = false;
    hideLoading();
  }
}

/* ===================================================
   USER MENU DROPDOWN
   =================================================== */
function toggleUserMenu(event) {
  event.stopPropagation();
  document.getElementById("user-menu").classList.toggle("open");
}

function closeUserMenu() {
  document.getElementById("user-menu").classList.remove("open");
}

document.addEventListener("click", function (e) {
  if (!e.target.closest(".topbar-user")) closeUserMenu();
});

document.addEventListener("keydown", function (e) {
  if (e.key === "Escape") closeUserMenu();
});

function getInitials(name) {
  if (!name) return "AD";
  return name.split(/\s+/).map(w => w[0]).join("").toUpperCase().slice(0, 2) || "AD";
}

function updateUserUI(nombre, email) {
  const initials = getInitials(nombre);
  document.querySelectorAll(".topbar-avatar").forEach(el => el.textContent = initials);
  document.querySelectorAll(".topbar-user-name").forEach(el => el.textContent = nombre);
  document.querySelectorAll(".user-menu-name").forEach(el => el.textContent = nombre);
  document.querySelectorAll(".user-menu-role, .topbar-user-role").forEach(el => el.textContent = email || "INABIF · Lima");
}

function doLogout() {
  closeUserMenu();
  document.getElementById("screen-dashboard").style.display = "none";
  document.getElementById("screen-login").style.display = "flex";
  document.getElementById("inp-user").value = "";
  document.getElementById("inp-pass").value = "";
  document.getElementById("remember-check").checked = false;
  localStorage.removeItem(REMEMBER_KEY);
  localStorage.removeItem(USER_NAME_KEY);
  localStorage.removeItem(USER_EMAIL_KEY);
  showToast("Sesión cerrada", "ok");
}

/* ===================================================
   NAVEGACIÓN
   =================================================== */
function showSection(sec) {
  const sections = {
    tables: document.getElementById("section-tables"),
    reports: document.getElementById("section-reports"),
    dashboard: document.getElementById("section-dashboard")
  };

  Object.keys(sections).forEach(key => {
    if (sections[key]) sections[key].style.display = key === sec ? "block" : "none";
  });

  const navs = {
    tables: document.getElementById("nav-tables"),
    reports: document.getElementById("nav-reports"),
    dashboard: document.getElementById("nav-dashboard")
  };

  Object.keys(navs).forEach(key => {
    if (navs[key]) navs[key].className = "sidebar-item" + (key === sec ? " active" : "");
  });

  /* Close mobile sidebar on navigation */
  const sidebar = document.getElementById("sidebar");
  const overlay = document.getElementById("sidebar-overlay");
  if (sidebar) sidebar.classList.remove("open");
  if (overlay) overlay.classList.remove("active");
  document.body.style.overflow = "";

  if (sec === "dashboard") setTimeout(() => cargarDashboard(), 120);
}

/* ===================================================
   REPORT TABLE — State
   =================================================== */
let reportData = {
  allRows: [],
  filteredRows: [],
  cols: [],
  currentPage: 1,
  pageSize: 10,
  editingIndex: -1
};

/* ===================================================
   MODAL
   =================================================== */
function openModal(tableName, editData) {
  currentModal = tableName;
  document.getElementById("modal-form-content").innerHTML = formTemplates[tableName] || "";

  if (editData) {
    document.getElementById("modal-title").textContent = "Editar Registro";
    document.getElementById("modal-sub").textContent = "Tabla: " + tableName + " · ID: " + (editData.ID_USU || "");
    document.getElementById("form-id-usu").value = editData.ID_USU || "";
    document.getElementById("form-fec-ing").value = editData.FEC_ING || "";
    document.getElementById("form-observacion").value = editData.OBSERVACION || "";
    reportData.editingIndex = editData._index;
  } else {
    document.getElementById("modal-title").textContent = "Agregar Registro";
    document.getElementById("modal-sub").textContent = "Tabla: " + tableName;
    reportData.editingIndex = -1;
  }

  document.getElementById("modal-overlay").classList.add("open");
  document.body.style.overflow = "hidden";

  setTimeout(() => {
    const firstInput = document.querySelector("#modal-form-content input, #modal-form-content textarea");
    if (firstInput) firstInput.focus();
  }, 100);
}

function closeModal() {
  document.getElementById("modal-overlay").classList.remove("open");
  document.body.style.overflow = "";
  currentModal = "";
  reportData.editingIndex = -1;
}

function handleOverlayClick(e) {
  if (e.target === document.getElementById("modal-overlay")) closeModal();
}

document.addEventListener("keydown", function (e) {
  if (e.key === "Escape") {
    const overlay = document.getElementById("modal-overlay");
    if (overlay && overlay.classList.contains("open")) closeModal();
  }
});

async function saveRecord() {
  const idUsu = document.getElementById("form-id-usu")?.value?.trim();
  const fecIng = document.getElementById("form-fec-ing")?.value;
  const observacion = document.getElementById("form-observacion")?.value?.trim();

  if (!idUsu) {
    showToast("Ingrese ID_USU", "error");
    document.getElementById("form-id-usu")?.focus();
    return;
  }

  showLoading();

  const payload = { tabla: currentModal, datos: { ID_USU: idUsu, FEC_ING: fecIng, OBSERVACION: observacion } };

  try {
    const isEdit = reportData.editingIndex >= 0;
    const url = isEdit ? "/api/registros" : "/api/registros";
    const method = isEdit ? "PUT" : "POST";

    const response = await fetch(url, {
      method: method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    const data = await response.json();

    if (!response.ok || !data.success) {
      showToast(data.message || "No se pudo guardar", "error");
      return;
    }

    closeModal();

    if (isEdit && reportData.allRows.length) {
      reportData.allRows[reportData.editingIndex] = { ...reportData.allRows[reportData.editingIndex], ID_USU: idUsu, FEC_ING: fecIng, OBSERVACION: observacion };
      reportData.filteredRows = applySearch(reportData.allRows);
      renderReportTable();
    }

    showToast(isEdit ? "Registro actualizado correctamente" : "Registro guardado correctamente", "ok");

  } catch (error) {
    console.error(error);
    showToast("Error al guardar registro", "error");
  } finally {
    hideLoading();
  }
}

/* ===================================================
   REPORTES — Paginación, Búsqueda, Edición, Eliminar
   =================================================== */
async function loadReport() {
  const tbl = document.getElementById("sel-table").value;
  const mon = document.getElementById("sel-month").value;
  const yr = document.getElementById("sel-year").value;
  const btn = document.querySelector(".btn-load");

  if (!tbl || !mon || !yr) {
    showToast("Completa todos los filtros", "error");
    return;
  }

  localStorage.setItem("inabif_filter_table", tbl);
  localStorage.setItem("inabif_filter_month", mon);
  localStorage.setItem("inabif_filter_year", yr);

  showLoading();
  btn.classList.add("btn-loading");
  document.getElementById("report-preview").classList.add("visible");
  showSkeleton("skel-table");

  try {
    const response = await fetch(`/api/reportes?tabla=${encodeURIComponent(tbl)}&mes=${parseInt(mon)}&anio=${yr}`);

    if (!response.ok) {
      showToast("Error al cargar reporte", "error");
      return;
    }

    const rows = await response.json();
    hideSkeleton("skel-table");

    if (!rows || rows.length === 0) {
      document.getElementById("preview-title").textContent = `${tbl} — ${mon}/${yr}`;
      document.getElementById("preview-badge").textContent = "0 registros";
      document.getElementById("table-toolbar").style.display = "none";
      document.getElementById("pagination").style.display = "none";
      document.getElementById("report-thead").innerHTML = "";
      document.getElementById("report-tbody").innerHTML = "";
      showToast("No se encontraron registros", "error");
      return;
    }

    const cols = Object.keys(rows[0]);
    reportData.allRows = rows.map((r, i) => ({ ...r, _index: i }));
    reportData.cols = cols;
    reportData.filteredRows = [...reportData.allRows];
    reportData.currentPage = 1;

    document.getElementById("preview-title").textContent = `${tbl} — ${mon}/${yr}`;
    document.getElementById("preview-badge").textContent = `${rows.length} registros`;

    renderReportTable();

    const preview = document.getElementById("report-preview");
    preview.scrollIntoView({ behavior: "smooth", block: "start" });

    showToast(`Tabla cargada: ${rows.length} registros`, "ok");

  } catch (error) {
    console.error(error);
    showToast("Error al consultar datos", "error");
    hideSkeleton("skel-table");
  } finally {
    hideLoading();
    btn.classList.remove("btn-loading");
  }
}

function renderReportTable() {
  const { cols, filteredRows, currentPage, pageSize } = reportData;
  const totalPages = Math.max(1, Math.ceil(filteredRows.length / pageSize));
  const start = (currentPage - 1) * pageSize;
  const end = Math.min(start + pageSize, filteredRows.length);
  const pageRows = filteredRows.slice(start, end);

  document.getElementById("table-toolbar").style.display = "flex";
  document.getElementById("pagination").style.display = "flex";
  document.getElementById("table-info").textContent = `Mostrando ${start + 1}-${end} de ${filteredRows.length} registros`;
  toggleSearchClear();

  let thead = "<tr><th class=\"th-actions\">Acciones</th>";
  cols.forEach(col => thead += `<th>${escapeHtml(col)}</th>`);
  thead += "</tr>";

  let tbody = "";
  pageRows.forEach(row => {
    const idx = row._index;
    tbody += `<tr>
      <td class="td-actions">
        <button class="btn-icon edit" onclick="editRow(${idx})" title="Editar"><i class="ti ti-pencil"></i></button>
        <button class="btn-icon delete" onclick="confirmDelete(${idx})" title="Eliminar"><i class="ti ti-trash"></i></button>
      </td>`;
    cols.forEach(col => {
      const value = row[col] ?? "";
      tbody += `<td>${escapeHtml(String(value))}</td>`;
    });
    tbody += "</tr>";
  });

  document.getElementById("report-thead").innerHTML = thead;
  document.getElementById("report-tbody").innerHTML = tbody;

  renderPagination(totalPages);
}

/* ------- BÚSQUEDA ------- */
function applySearch(rows) {
  const q = document.getElementById("search-input").value.trim().toLowerCase();
  if (!q) return [...rows];
  return rows.filter(row => {
    return Object.keys(row).some(key => {
      if (key === "_index") return false;
      return String(row[key] ?? "").toLowerCase().includes(q);
    });
  });
}

function toggleSearchClear() {
  const btn = document.getElementById("search-clear");
  btn.classList.toggle("visible", document.getElementById("search-input").value.length > 0);
}

function searchTable() {
  reportData.filteredRows = applySearch(reportData.allRows);
  reportData.currentPage = 1;
  renderReportTable();
  toggleSearchClear();
}

function clearSearch() {
  document.getElementById("search-input").value = "";
  toggleSearchClear();
  searchTable();
}

/* ------- PAGINACIÓN ------- */
function renderPagination(totalPages) {
  const info = document.getElementById("pagination-info");
  info.textContent = `Página ${reportData.currentPage} de ${totalPages}`;

  const container = document.getElementById("pagination-pages");
  let html = "";

  html += `<button onclick="goToPage(${reportData.currentPage - 1})" ${reportData.currentPage <= 1 ? "disabled" : ""}>‹</button>`;

  let from = Math.max(1, reportData.currentPage - 2);
  let to = Math.min(totalPages, from + 4);
  if (to - from < 4) from = Math.max(1, to - 4);

  for (let i = from; i <= to; i++) {
    html += `<button class="${i === reportData.currentPage ? "active" : ""}" onclick="goToPage(${i})">${i}</button>`;
  }

  html += `<button onclick="goToPage(${reportData.currentPage + 1})" ${reportData.currentPage >= totalPages ? "disabled" : ""}>›</button>`;

  container.innerHTML = html;
}

function goToPage(page) {
  const totalPages = Math.ceil(reportData.filteredRows.length / reportData.pageSize);
  if (page < 1 || page > totalPages) return;
  reportData.currentPage = page;
  renderReportTable();
}

function changePageSize(size) {
  reportData.pageSize = parseInt(size);
  reportData.currentPage = 1;
  renderReportTable();
}

/* ------- EDITAR FILA ------- */
function editRow(index) {
  const row = reportData.allRows[index];
  if (!row) return;
  const tableName = document.getElementById("sel-table").value || "Atendidos_Servicio";
  openModal(tableName, row);
}

/* ------- ELIMINAR FILA ------- */
function confirmDelete(index) {
  const row = reportData.allRows[index];
  if (!row) return;

  const overlay = document.getElementById("modal-overlay");
  const title = document.getElementById("modal-title");
  const sub = document.getElementById("modal-sub");
  const body = document.getElementById("modal-form-content");
  const footer = document.querySelector(".modal-footer");

  const originalTitle = title.textContent;
  const originalSub = sub.textContent;
  const originalBody = body.innerHTML;
  const originalFooter = footer.innerHTML;

  title.textContent = "Confirmar Eliminación";
  sub.textContent = "Esta acción no se puede deshacer";

  const idVal = row.ID_USU || "—";
  body.innerHTML = `
    <div style="text-align:center;padding:16px 0">
      <div style="font-size:48px;color:var(--inabif-red);margin-bottom:12px"><i class="ti ti-alert-triangle"></i></div>
      <p style="font-size:15px;color:var(--inabif-gray-800);margin-bottom:4px">¿Estás seguro de eliminar este registro?</p>
      <p style="font-size:13px;color:var(--inabif-gray-400)">ID: <strong>${escapeHtml(idVal)}</strong></p>
    </div>`;

  footer.innerHTML = `
    <button class="btn-cancel" onclick="restoreModal()"><i class="ti ti-x"></i> Cancelar</button>
    <button class="btn-save" onclick="deleteRecord(${index})" style="background:var(--inabif-red)"><i class="ti ti-trash"></i> Eliminar</button>`;

  overlay.classList.add("open");
  document.body.style.overflow = "hidden";

  window._modalRestore = { title: originalTitle, sub: originalSub, body: originalBody, footer: originalFooter };
}

function restoreModal() {
  if (!window._modalRestore) return closeModal();
  const r = window._modalRestore;
  document.getElementById("modal-title").textContent = r.title;
  document.getElementById("modal-sub").textContent = r.sub;
  document.getElementById("modal-form-content").innerHTML = r.body;
  document.querySelector(".modal-footer").innerHTML = r.footer;
  window._modalRestore = null;
}

async function deleteRecord(index) {
  const row = reportData.allRows[index];
  if (!row) return;

  showLoading();
  try {
    const response = await fetch("/api/registros", {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ tabla: document.getElementById("sel-table").value, id: row.ID_USU })
    });

    const data = await response.json();

    if (!response.ok || !data.success) {
      showToast(data.message || "No se pudo eliminar", "error");
      return;
    }

    reportData.allRows.splice(index, 1);
    reportData.filteredRows = applySearch(reportData.allRows);
    if (reportData.currentPage > Math.ceil(reportData.filteredRows.length / reportData.pageSize)) {
      reportData.currentPage = Math.max(1, Math.ceil(reportData.filteredRows.length / reportData.pageSize));
    }
    renderReportTable();

    closeModal();
    showToast("Registro eliminado correctamente", "ok");

  } catch (error) {
    console.error(error);
    showToast("Error al eliminar registro", "error");
  } finally {
    hideLoading();
  }
}

function exportExcel() {
  const table = document.getElementById("report-table");

  if (!table || !table.querySelector("tbody tr")) {
    showToast("Primero carga un reporte", "error");
    return;
  }

  let csv = "";
  const rows = table.querySelectorAll("tr");

  rows.forEach(row => {
    const cols = row.querySelectorAll("th, td");
    const values = Array.from(cols).map(col => {
      const text = col.innerText.replace(/"/g, '""');
      return `"${text}"`;
    });
    csv += values.join(";") + "\n";
  });

  const blob = new Blob(["\ufeff" + csv], { type: "text/csv;charset=utf-8;" });
  const url = URL.createObjectURL(blob);

  const a = document.createElement("a");
  a.href = url;
  a.download = "reporte.csv";
  a.click();

  URL.revokeObjectURL(url);
  showToast("Reporte exportado a Excel/CSV", "ok");
}

function exportPdf() {
  const table = document.getElementById("report-table");

  if (!table || !table.querySelector("tbody tr")) {
    showToast("Primero carga un reporte", "error");
    return;
  }

  const title = document.getElementById("preview-title").textContent;
  const { jsPDF } = window.jspdf;
  const doc = new jsPDF({ orientation: "landscape", unit: "mm", format: "a4" });

  const pageW = doc.internal.pageSize.getWidth();
  doc.setFont("Helvetica", "bold");
  doc.setFontSize(14);
  doc.text(title, pageW / 2, 16, { align: "center" });

  doc.setFontSize(9);
  doc.setFont("Helvetica", "normal");
  doc.text("Generado por INABIF — Sistema de Reportes", pageW / 2, 23, { align: "center" });

  const thead = table.querySelectorAll("thead th");
  const headers = Array.from(thead).map(th => th.textContent.trim()).filter(h => h);

  const body = [];
  const tbody = table.querySelectorAll("tbody tr");
  tbody.forEach(tr => {
    const cells = tr.querySelectorAll("td");
    if (cells.length === 0) return;
    const row = Array.from(cells).map(td => td.textContent.trim());
    body.push(row);
  });

  doc.autoTable({
    head: [headers],
    body: body,
    startY: 28,
    styles: { fontSize: 7, cellPadding: 2.5, overflow: "linebreak" },
    headStyles: { fillColor: [192, 39, 26], textColor: 255, fontStyle: "bold", fontSize: 7 },
    alternateRowStyles: { fillColor: [245, 224, 222] },
    margin: { top: 28, left: 8, right: 8 },
    tableWidth: "auto"
  });

  const pageCount = doc.internal.getNumberOfPages();
  for (let i = 1; i <= pageCount; i++) {
    doc.setPage(i);
    doc.setFontSize(7);
    doc.setTextColor(158, 152, 149);
    doc.text(`Página ${i} de ${pageCount}`, pageW - 16, doc.internal.pageSize.getHeight() - 8, { align: "right" });
  }

  doc.save("reporte.pdf");
  showToast("PDF generado correctamente", "ok");
}

/* ===================================================
   SIDEBAR TOGGLE (mobile)
   =================================================== */
function toggleSidebar() {
  const sidebar = document.getElementById("sidebar");
  const overlay = document.getElementById("sidebar-overlay");
  const isOpen = sidebar.classList.toggle("open");
  overlay.classList.toggle("active", isOpen);
  document.body.style.overflow = isOpen ? "hidden" : "";
}

/* ===================================================
   THEME TOGGLE
   =================================================== */
function updateThemeIcon(theme) {
  const btn = document.getElementById("theme-toggle");
  if (!btn) return;
  const icon = btn.querySelector("i");
  icon.className = theme === "dark" ? "ti ti-sun" : "ti ti-moon";
}

function toggleTheme() {
  const html = document.documentElement;
  const current = html.getAttribute("data-theme");
  const next = current === "dark" ? "light" : "dark";
  html.setAttribute("data-theme", next);
  localStorage.setItem("inabif_theme", next);
  updateThemeIcon(next);
  showToast(`Tema ${next === "dark" ? "oscuro" : "claro"} activado`, "ok");
}

/* ===================================================
   TOAST
   =================================================== */
let toastTimer;

function showToast(msg, type) {
  const toast = document.getElementById("toast");
  const icon = document.getElementById("toast-icon");

  document.getElementById("toast-msg").textContent = msg;
  toast.className = "toast " + (type === "error" ? "error" : "");
  icon.className = type === "error" ? "ti ti-alert-circle" : "ti ti-circle-check";

  clearTimeout(toastTimer);
  requestAnimationFrame(() => toast.classList.add("show"));
  toastTimer = setTimeout(() => toast.classList.remove("show"), 3200);
}

function dismissToast() {
  const toast = document.getElementById("toast");
  toast.classList.remove("show");
  clearTimeout(toastTimer);
}

function escapeHtml(value) {
  return value
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

/* ===================================================
   DASHBOARD — Gráficos
   =================================================== */
let chartServicios = null;
let chartGenero = null;
let chartEdades = null;
let chartDepartamentos = null;
let chartComparativo = null;

async function cargarDashboard() {
  try {
    await cargarKpis();
    await cargarGraficoServicios();
    await cargarGraficoGenero();
    await cargarGraficoEdades();
    await cargarGraficoDepartamentos();
    await cargarGraficoComparativo();
  } catch (error) {
    console.error(error);
    showToast("Error al cargar dashboard", "error");
  }
}

async function cargarKpis() {
  showLoading();
  showDashboardSkeletons();
  try {
    const response = await fetch("/api/dashboard/kpis");
    if (!response.ok) throw new Error("Error consultando KPIs");

    const data = await response.json();

    document.getElementById("kpi-atenciones").textContent = data.totalAtenciones ?? 0;
    document.getElementById("kpi-beneficiarios").textContent = data.totalBeneficiarios ?? 0;
    document.getElementById("kpi-servicios").textContent = data.serviciosActivos ?? 0;
    document.getElementById("kpi-departamentos").textContent = data.departamentos ?? 0;

    for (let i = 1; i <= 4; i++) hideSkeleton("skel-kpi-" + i);
  } finally {
    hideLoading();
  }
}

async function cargarGraficoServicios() {
  const data = await obtenerData("/api/dashboard/servicios");
  chartServicios = crearBarChart("chartServicios", chartServicios, data, "Total beneficiarios");
  hideSkeleton("skel-chart-1");
}

async function cargarGraficoGenero() {
  const data = await obtenerData("/api/dashboard/genero");
  chartGenero = crearDoughnutChart("chartGenero", chartGenero, data);
  hideSkeleton("skel-chart-2");
}

async function cargarGraficoEdades() {
  const data = await obtenerData("/api/dashboard/edades");
  chartEdades = crearBarChart("chartEdades", chartEdades, data, "Total atenciones");
  hideSkeleton("skel-chart-3");
}

async function cargarGraficoDepartamentos() {
  const data = await obtenerData("/api/dashboard/departamentos");
  chartDepartamentos = crearBarChart("chartDepartamentos", chartDepartamentos, data, "Demanda");
  hideSkeleton("skel-chart-4");
}

async function cargarGraficoComparativo() {
  const data = await obtenerData("/api/dashboard/comparativo");
  chartComparativo = crearBarChart("chartComparativo", chartComparativo, data, "Total registros");
  hideSkeleton("skel-chart-5");
}

async function obtenerData(url) {
  const response = await fetch(url);

  if (!response.ok) {
    const errorText = await response.text();
    console.error("Error en endpoint:", url, errorText);
    throw new Error("Error consultando " + url);
  }

  const data = await response.json();
  return Array.isArray(data) ? data : [];
}

function normalizarDatos(data) {
  return data.map(item => ({
    label: item.label ?? item.LABEL ?? "Sin clasificar",
    total: Number(item.total ?? item.TOTAL ?? 0)
  }));
}

function crearBarChart(canvasId, chartInstance, data, label) {
  const canvas = document.getElementById(canvasId);
  if (!canvas) {
    console.warn("No existe canvas:", canvasId);
    return chartInstance;
  }

  const chartData = normalizarDatos(data);
  if (chartInstance) chartInstance.destroy();

  return new Chart(canvas, {
    type: "bar",
    data: {
      labels: chartData.map(item => item.label),
      datasets: [{
        label: label,
        data: chartData.map(item => item.total),
        backgroundColor: [
          "#C0271A", "#E8581A", "#D4941A", "#1A7A3C", "#8B1A10",
          "#FA8A4A", "#B65C18", "#A63B2C", "#6B6563", "#3D3432"
        ],
        borderRadius: 8
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: true } },
      scales: { y: { beginAtZero: true, ticks: { precision: 0 } } }
    }
  });
}

function crearDoughnutChart(canvasId, chartInstance, data) {
  const canvas = document.getElementById(canvasId);
  if (!canvas) {
    console.warn("No existe canvas:", canvasId);
    return chartInstance;
  }

  const chartData = normalizarDatos(data);
  if (chartInstance) chartInstance.destroy();

  return new Chart(canvas, {
    type: "doughnut",
    data: {
      labels: chartData.map(item => item.label),
      datasets: [{
        data: chartData.map(item => item.total),
        backgroundColor: ["#C0271A", "#E8581A", "#D4941A", "#1A7A3C", "#8B1A10"],
        borderWidth: 2,
        borderColor: "#FFFFFF"
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { position: "bottom" } }
    }
  });
}