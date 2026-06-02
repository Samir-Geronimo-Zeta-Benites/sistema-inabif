package pe.gob.inabif.reportes.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

  private final JdbcTemplate jdbcTemplate;

  public ReporteService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<Map<String, Object>> obtenerReporte(String tabla, int mes, int anio) {

    String sql;

    switch (tabla) {
      case "Atendidos_Servicio":
        sql = """
                        SELECT *
                        FROM Atendidos_Servicio
                        WHERE MONTH(FEC_ING) = ?
                        AND YEAR(FEC_ING) = ?
                        """;
        break;

      case "Beneficiarios_Asistencia_clean":
        sql = """
                        SELECT b.*, a.FEC_ING
                        FROM Beneficiarios_Asistencia_clean b
                        INNER JOIN Atendidos_Servicio a
                            ON a.ID_USU = b.ID_USU
                        WHERE MONTH(a.FEC_ING) = ?
                        AND YEAR(a.FEC_ING) = ?
                        """;
        break;

      case "Beneficiarios_Atendidos_clean":
        sql = """
                        SELECT b.*, a.FEC_ING
                        FROM Beneficiarios_Atendidos_clean b
                        INNER JOIN Atendidos_Servicio a
                            ON a.ID_USU = b.ID_USU
                        WHERE MONTH(a.FEC_ING) = ?
                        AND YEAR(a.FEC_ING) = ?
                        """;
        break;

      default:
        throw new IllegalArgumentException("Tabla no permitida: " + tabla);
    }

    return jdbcTemplate.queryForList(sql, mes, anio);
  }
}