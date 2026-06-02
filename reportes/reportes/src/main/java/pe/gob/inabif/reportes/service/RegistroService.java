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
}