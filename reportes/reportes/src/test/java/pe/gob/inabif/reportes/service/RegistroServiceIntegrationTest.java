package pe.gob.inabif.reportes.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;

@JdbcTest
@Sql(scripts = "/schema.sql")
class RegistroServiceIntegrationTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private RegistroService registroService;

  @BeforeEach
  void setUp() {
    registroService = new RegistroService(jdbcTemplate);
  }

  @Test
  void guardarYConsultarAtendidoServicio() {
    registroService.guardarRegistro("Atendidos_Servicio", Map.of("ID_USU", "INT001", "FEC_ING", "2024-01-15"));

    Integer count = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM Atendidos_Servicio WHERE ID_USU = ?", Integer.class, "INT001");
    assertEquals(1, count);
  }

  @Test
  void guardarBeneficiarioAsistencia() {
    registroService.guardarRegistro("Beneficiarios_Asistencia_clean", Map.of("ID_USU", "INT002"));

    Integer count = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM Beneficiarios_Asistencia_clean WHERE ID_USU = ?", Integer.class, "INT002");
    assertEquals(1, count);
  }

  @Test
  void guardarBeneficiarioAtendido() {
    registroService.guardarRegistro("Beneficiarios_Atendidos_clean", Map.of("ID_USU", "INT003"));

    Integer count = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM Beneficiarios_Atendidos_clean WHERE ID_USU = ?", Integer.class, "INT003");
    assertEquals(1, count);
  }

  @Test
  void actualizarAtendidoServicio() {
    jdbcTemplate.update("INSERT INTO Atendidos_Servicio (ID_USU, FEC_ING) VALUES (?, ?)", "INT001", "2024-01-01");
    registroService.actualizarRegistro("Atendidos_Servicio", Map.of("ID_USU", "INT001", "FEC_ING", "2024-06-15"));

    String fecha = jdbcTemplate.queryForObject(
        "SELECT FEC_ING FROM Atendidos_Servicio WHERE ID_USU = ?", String.class, "INT001");
    assertEquals("2024-06-15", fecha);
  }

  @Test
  void eliminarAtendidoServicio() {
    jdbcTemplate.update("INSERT INTO Atendidos_Servicio (ID_USU, FEC_ING) VALUES (?, ?)", "INT001", "2024-01-01");
    registroService.eliminarRegistro("Atendidos_Servicio", "INT001");

    Integer count = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM Atendidos_Servicio WHERE ID_USU = ?", Integer.class, "INT001");
    assertEquals(0, count);
  }
}
