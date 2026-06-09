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
class DashboardServiceIntegrationTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private DashboardService dashboardService;

  @BeforeEach
  void setUp() {
    dashboardService = new DashboardService(jdbcTemplate);
  }

  private void insertarDatos() {
    jdbcTemplate.update("INSERT INTO Atendidos_Servicio (ID_USU, FEC_ING, TIP_CAR, SEX_USU, EDAD_USU) VALUES (?, ?, ?, ?, ?)",
        "U001", "2024-01-15", "Orientación", 1, 25);
    jdbcTemplate.update("INSERT INTO Atendidos_Servicio (ID_USU, FEC_ING, TIP_CAR, SEX_USU, EDAD_USU) VALUES (?, ?, ?, ?, ?)",
        "U002", "2024-03-10", "Consejería", 2, 30);
    jdbcTemplate.update("INSERT INTO Atendidos_Servicio (ID_USU, FEC_ING, TIP_CAR, SEX_USU, EDAD_USU) VALUES (?, ?, ?, ?, ?)",
        "U003", "2024-06-20", "Orientación", 1, 5);
    jdbcTemplate.update("INSERT INTO Beneficiarios_Asistencia_clean (ID_USU, EDAD_BENEF, SEX_USU, DEP_RES) VALUES (?, ?, ?, ?)",
        "U001", 25, 1, "Lima");
    jdbcTemplate.update("INSERT INTO Beneficiarios_Asistencia_clean (ID_USU, EDAD_BENEF, SEX_USU, DEP_RES) VALUES (?, ?, ?, ?)",
        "U004", 40, 2, "Arequipa");
    jdbcTemplate.update("INSERT INTO Beneficiarios_Atendidos_clean (ID_USU, NOM_SER, SEX_USU, EDAD_USU, DEP_CA) VALUES (?, ?, ?, ?, ?)",
        "U002", "Apoyo Familiar", 2, 30, "Lima");
    jdbcTemplate.update("INSERT INTO Beneficiarios_Atendidos_clean (ID_USU, NOM_SER, SEX_USU, EDAD_USU, DEP_CA) VALUES (?, ?, ?, ?, ?)",
        "U005", "Atención Residencial", 1, 70, "Cusco");
  }

  @Test
  void obtenerKpis() {
    insertarDatos();
    Map<String, Object> result = dashboardService.obtenerKpis();
    assertNotNull(result.get("totalAtenciones"));
    assertNotNull(result.get("totalBeneficiarios"));
    assertNotNull(result.get("departamentos"));
  }

  @Test
  void servicios() {
    insertarDatos();
    List<Map<String, Object>> result = dashboardService.servicios();
    assertFalse(result.isEmpty());
  }

  @Test
  void genero() {
    insertarDatos();
    List<Map<String, Object>> result = dashboardService.genero();
    assertFalse(result.isEmpty());
  }

  @Test
  void edades() {
    insertarDatos();
    List<Map<String, Object>> result = dashboardService.edades();
    assertFalse(result.isEmpty());
  }

  @Test
  void departamentos() {
    insertarDatos();
    List<Map<String, Object>> result = dashboardService.departamentos();
    assertFalse(result.isEmpty());
  }

  @Test
  void comparativo() {
    insertarDatos();
    List<Map<String, Object>> result = dashboardService.comparativo();
    assertFalse(result.isEmpty());
  }
}
