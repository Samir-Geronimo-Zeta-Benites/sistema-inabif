package pe.gob.inabif.reportes.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

@JdbcTest
@Sql(scripts = "/schema.sql")
class ReporteServiceIntegrationTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private ReporteService reporteService;

  @BeforeEach
  void setUp() {
    reporteService = new ReporteService(jdbcTemplate);
    jdbcTemplate.update("INSERT INTO Atendidos_Servicio (ID_USU, FEC_ING, TIP_CAR, SEX_USU, EDAD_USU) VALUES (?, ?, ?, ?, ?)",
        "U001", "2024-01-15", "Orientación", 1, 25);
    jdbcTemplate.update("INSERT INTO Atendidos_Servicio (ID_USU, FEC_ING, TIP_CAR, SEX_USU, EDAD_USU) VALUES (?, ?, ?, ?, ?)",
        "U002", "2024-03-10", "Consejería", 2, 30);
    jdbcTemplate.update("INSERT INTO Beneficiarios_Asistencia_clean (ID_USU, EDAD_BENEF, SEX_USU, DEP_RES) VALUES (?, ?, ?, ?)",
        "U001", 25, 1, "Lima");
    jdbcTemplate.update("INSERT INTO Beneficiarios_Atendidos_clean (ID_USU, NOM_SER, SEX_USU, EDAD_USU, DEP_CA) VALUES (?, ?, ?, ?, ?)",
        "U002", "Apoyo Familiar", 2, 30, "Lima");
  }

  @Test
  void obtenerReporteAtendidosServicio() {
    List<Map<String, Object>> result = reporteService.obtenerReporte("Atendidos_Servicio", 1, 2024);
    assertEquals(1, result.size());
    assertEquals("U001", result.get(0).get("ID_USU"));
  }

  @Test
  void obtenerReporteBeneficiariosAsistencia() {
    List<Map<String, Object>> result = reporteService.obtenerReporte("Beneficiarios_Asistencia_clean", 1, 2024);
    assertFalse(result.isEmpty());
    assertEquals("U001", result.get(0).get("ID_USU"));
    assertNotNull(result.get(0).get("FEC_ING"));
  }

  @Test
  void obtenerReporteBeneficiariosAtendidos() {
    List<Map<String, Object>> result = reporteService.obtenerReporte("Beneficiarios_Atendidos_clean", 3, 2024);
    assertFalse(result.isEmpty());
    assertEquals("U002", result.get(0).get("ID_USU"));
    assertNotNull(result.get(0).get("FEC_ING"));
  }

  @Test
  void obtenerReporteSinResultados() {
    List<Map<String, Object>> result = reporteService.obtenerReporte("Atendidos_Servicio", 12, 2025);
    assertTrue(result.isEmpty());
  }
}
