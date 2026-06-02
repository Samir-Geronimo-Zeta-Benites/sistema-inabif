let currentModal = "";

const formTemplates = {
  "Atendidos_Servicio": `
    <div class="modal-form-grid">
      <div>
        <label class="modal-label">ID_USU</label>
        <input class="modal-input" id="form-id-usu" type="text" placeholder="Ingrese ID_USU">
      </div>
      <div>
        <label class="modal-label">Fecha de Ingreso</label>
        <input class="modal-input" id="form-fec-ing" type="date">
      </div>
      <div class="full">
        <label class="modal-label">Observaciones</label>
        <textarea class="modal-textarea" id="form-observacion" placeholder="Notas adicionales..."></textarea>
      </div>
    </div>`,

  "Beneficiarios_Asistencia_clean": `
    <div class="modal-form-grid">
      <div>
        <label class="modal-label">ID_USU</label>
        <input class="modal-input" id="form-id-usu" type="text" placeholder="Ingrese ID_USU">
      </div>
      <div>
        <label class="modal-label">Fecha Referencial</label>
        <input class="modal-input" id="form-fec-ing" type="date">
      </div>
      <div class="full">
        <label class="modal-label">Observaciones</label>
        <textarea class="modal-textarea" id="form-observacion" placeholder="Notas adicionales..."></textarea>
      </div>
    </div>`,

  "Beneficiarios_Atendidos_clean": `
    <div class="modal-form-grid">
      <div>
        <label class="modal-label">ID_USU</label>
        <input class="modal-input" id="form-id-usu" type="text" placeholder="Ingrese ID_USU">
      </div>
      <div>
        <label class="modal-label">Fecha Referencial</label>
        <input class="modal-input" id="form-fec-ing" type="date">
      </div>
      <div class="full">
        <label class="modal-label">Observaciones</label>
        <textarea class="modal-textarea" id="form-observacion" placeholder="Notas adicionales..."></textarea>
      </div>
    </div>`
};

async function doLogin() {
  const user = document.getElementById("inp-user").value.trim();
  const password = document.getElementById("inp-pass").value;

  const btn = document.getElementById("btn-login");
  btn.classList.add("loading");

  try {
    const response = await fetch("/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ user, password })
    });

    const data = await response.json();

    if (!data.success) {
      showToast("Credenciales incorrectas", "error");
      return;
    }

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
  }
}

function doLogout() {
  document.getElementById("screen-dashboard").style.display = "none";
  document.getElementById("screen-login").style.display = "flex";
  document.getElementById("inp-user").value = "";
  document.getElementById("inp-pass").value = "";
  showToast("Sesión cerrada", "ok");
}

function showSection(sec) {
  const sections = {
    tables: document.getElementById("section-tables"),
    reports: document.getElementById("section-reports"),
    dashboard: document.getElementById("section-dashboard")
  };

  Object.keys(sections).forEach(key => {
    if (sections[key]) {
      sections[key].style.display = key === sec ? "block" : "none";
    }
  });

  const navs = {
    tables: document.getElementById("nav-tables"),
    reports: document.getElementById("nav-reports"),
    dashboard: document.getElementById("nav-dashboard")
  };

  Object.keys(navs).forEach(key => {
    if (navs[key]) {
      navs[key].className = "sidebar-item" + (key === sec ? " active" : "");
    }
  });

  if (sec === "dashboard") {
    setTimeout(() => cargarDashboard(), 120);
  }
}

function openModal(tableName) {
  currentModal = tableName;
  document.getElementById("modal-title").textContent = "Agregar Registro";
  document.getElementById("modal-sub").textContent = "Tabla: " + tableName;
  document.getElementById("modal-form-content").innerHTML = formTemplates[tableName] || "";
  document.getElementById("modal-overlay").classList.add("open");
  document.body.style.overflow = "hidden";
}

function closeModal() {
  document.getElementById("modal-overlay").classList.remove("open");
  document.body.style.overflow = "";
  currentModal = "";
}

function handleOverlayClick(e) {
  if (e.target === document.getElementById("modal-overlay")) closeModal();
}

async function saveRecord() {
  const idUsu = document.getElementById("form-id-usu")?.value?.trim();
  const fecIng = document.getElementById("form-fec-ing")?.value;
  const observacion = document.getElementById("form-observacion")?.value?.trim();

  if (!idUsu) {
    showToast("Ingrese ID_USU", "error");
    return;
  }

  const payload = {
    tabla: currentModal,
    datos: {
      ID_USU: idUsu,
      FEC_ING: fecIng,
      OBSERVACION: observacion
    }
  };

  try {
    const response = await fetch("/api/registros", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    const data = await response.json();

    if (!response.ok || !data.success) {
      showToast(data.message || "No se pudo guardar", "error");
      return;
    }

    closeModal();
    showToast("Registro guardado correctamente", "ok");

  } catch (error) {
    console.error(error);
    showToast("Error al guardar registro", "error");
  }
}

async function loadReport() {
  const tbl = document.getElementById("sel-table").value;
  const mon = document.getElementById("sel-month").value;
  const yr = document.getElementById("sel-year").value;

  if (!tbl || !mon || !yr) {
    showToast("Completa todos los filtros", "error");
    return;
  }

  try {
    const response = await fetch(`/api/reportes?tabla=${encodeURIComponent(tbl)}&mes=${parseInt(mon)}&anio=${yr}`);

    if (!response.ok) {
      showToast("Error al cargar reporte", "error");
      return;
    }

    const rows = await response.json();

    if (!rows || rows.length === 0) {
      document.getElementById("report-thead").innerHTML = "";
      document.getElementById("report-tbody").innerHTML = "";
      document.getElementById("preview-title").textContent = `${tbl} — ${mon}/${yr}`;
      document.getElementById("preview-badge").textContent = "0 registros";
      document.getElementById("report-preview").classList.add("visible");
      showToast("No se encontraron registros", "error");
      return;
    }

    const cols = Object.keys(rows[0]);
    document.getElementById("preview-title").textContent = `${tbl} — ${mon}/${yr}`;
    document.getElementById("preview-badge").textContent = `${rows.length} registros`;

    let thead = "<tr>";
    cols.forEach(col => thead += `<th>${escapeHtml(col)}</th>`);
    thead += "</tr>";

    let tbody = "";
    rows.forEach(row => {
      tbody += "<tr>";
      cols.forEach(col => {
        const value = row[col] ?? "";
        tbody += `<td>${escapeHtml(String(value))}</td>`;
      });
      tbody += "</tr>";
    });

    document.getElementById("report-thead").innerHTML = thead;
    document.getElementById("report-tbody").innerHTML = tbody;

    const preview = document.getElementById("report-preview");
    preview.classList.add("visible");
    preview.scrollIntoView({ behavior: "smooth", block: "start" });

    showToast(`Tabla cargada: ${rows.length} registros`, "ok");

  } catch (error) {
    console.error(error);
    showToast("Error al consultar datos", "error");
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

  window.print();
  showToast("Vista lista para PDF", "ok");
}

let toastTimer;

function showToast(msg, type) {
  const toast = document.getElementById("toast");
  const icon = document.getElementById("toast-icon");

  document.getElementById("toast-msg").textContent = msg;
  toast.className = "toast " + (type === "error" ? "error" : "");
  icon.className = type === "error" ? "ti ti-alert-circle" : "ti ti-circle-check";

  setTimeout(() => toast.classList.add("show"), 10);
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toast.classList.remove("show"), 3200);
}

function escapeHtml(value) {
  return value
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

document.addEventListener("keydown", function (e) {
  if (e.key === "Enter") {
    const login = document.getElementById("screen-login");
    if (login && login.style.display !== "none") doLogin();
  }
});

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
  const response = await fetch("/api/dashboard/kpis");
  if (!response.ok) throw new Error("Error consultando KPIs");

  const data = await response.json();

  document.getElementById("kpi-atenciones").textContent = data.totalAtenciones ?? 0;
  document.getElementById("kpi-beneficiarios").textContent = data.totalBeneficiarios ?? 0;
  document.getElementById("kpi-servicios").textContent = data.serviciosActivos ?? 0;
  document.getElementById("kpi-departamentos").textContent = data.departamentos ?? 0;
}

async function cargarGraficoServicios() {
  const data = await obtenerData("/api/dashboard/servicios");
  chartServicios = crearBarChart("chartServicios", chartServicios, data, "Total beneficiarios");
}

async function cargarGraficoGenero() {
  const data = await obtenerData("/api/dashboard/genero");
  chartGenero = crearDoughnutChart("chartGenero", chartGenero, data);
}

async function cargarGraficoEdades() {
  const data = await obtenerData("/api/dashboard/edades");
  chartEdades = crearBarChart("chartEdades", chartEdades, data, "Total atenciones");
}

async function cargarGraficoDepartamentos() {
  const data = await obtenerData("/api/dashboard/departamentos");
  chartDepartamentos = crearBarChart("chartDepartamentos", chartDepartamentos, data, "Demanda");
}

async function cargarGraficoComparativo() {
  const data = await obtenerData("/api/dashboard/comparativo");
  chartComparativo = crearBarChart("chartComparativo", chartComparativo, data, "Total registros");
}

async function obtenerData(url) {
  const response = await fetch(url);

  if (!response.ok) {
    const errorText = await response.text();
    console.error("Error en endpoint:", url);
    console.error(errorText);
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
