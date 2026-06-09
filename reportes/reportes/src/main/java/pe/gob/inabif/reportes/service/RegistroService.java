package pe.gob.inabif.reportes.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RegistroService {

  private final JdbcTemplate jdbcTemplate;

  public RegistroService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void guardarRegistro(String tabla, Map<String, Object> datos) {

    switch (tabla) {
      case "Atendidos_Servicio":
        guardarAtendidoServicio(datos);
        break;

      case "Beneficiarios_Asistencia_clean":
        guardarBeneficiarioAsistencia(datos);
        break;

      case "Beneficiarios_Atendidos_clean":
        guardarBeneficiarioAtendido(datos);
        break;

      default:
        throw new IllegalArgumentException("Tabla no permitida: " + tabla);
    }
  }

  private void guardarAtendidoServicio(Map<String, Object> datos) {
    String sql = """
                INSERT INTO Atendidos_Servicio
                (ID_USU, FEC_ING)
                VALUES (?, ?)
                """;

    jdbcTemplate.update(
            sql,
            datos.get("ID_USU"),
            datos.get("FEC_ING")
    );
  }

  private void guardarBeneficiarioAsistencia(Map<String, Object> datos) {
    String sql = """
                INSERT INTO Beneficiarios_Asistencia_clean
                (ID_USU)
                VALUES (?)
                """;

    jdbcTemplate.update(
            sql,
            datos.get("ID_USU")
    );
  }

  private void guardarBeneficiarioAtendido(Map<String, Object> datos) {
    String sql = """
                INSERT INTO Beneficiarios_Atendidos_clean
                (ID_USU)
                VALUES (?)
                """;

    jdbcTemplate.update(
            sql,
            datos.get("ID_USU")
    );
  }

  public void actualizarRegistro(String tabla, Map<String, Object> datos) {
    switch (tabla) {
      case "Atendidos_Servicio":
        String sql1 = """
                UPDATE Atendidos_Servicio
                SET FEC_ING = ?
                WHERE ID_USU = ?
                """;
        jdbcTemplate.update(sql1, datos.get("FEC_ING"), datos.get("ID_USU"));
        break;

      case "Beneficiarios_Asistencia_clean":
      case "Beneficiarios_Atendidos_clean":
        break;

      default:
        throw new IllegalArgumentException("Tabla no permitida: " + tabla);
    }
  }

  public void eliminarRegistro(String tabla, String id) {
    switch (tabla) {
      case "Atendidos_Servicio":
        jdbcTemplate.update("DELETE FROM Atendidos_Servicio WHERE ID_USU = ?", id);
        break;

      case "Beneficiarios_Asistencia_clean":
        jdbcTemplate.update("DELETE FROM Beneficiarios_Asistencia_clean WHERE ID_USU = ?", id);
        break;

      case "Beneficiarios_Atendidos_clean":
        jdbcTemplate.update("DELETE FROM Beneficiarios_Atendidos_clean WHERE ID_USU = ?", id);
        break;

      default:
        throw new IllegalArgumentException("Tabla no permitida: " + tabla);
    }
  }
}