package pe.gob.inabif.reportes.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

  private final JdbcTemplate jdbcTemplate;

  public DashboardService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Map<String, Object> obtenerKpis() {

    Integer totalAtenciones = jdbcTemplate.queryForObject("""
        SELECT COUNT(*) FROM (
            SELECT ID_USU FROM Atendidos_Servicio
            UNION ALL
            SELECT ID_USU FROM Beneficiarios_Asistencia_clean
            UNION ALL
            SELECT ID_USU FROM Beneficiarios_Atendidos_clean
        ) x
        """, Integer.class);

    Integer totalBeneficiarios = jdbcTemplate.queryForObject("""
        SELECT COUNT(DISTINCT ID_USU) FROM (
            SELECT ID_USU FROM Atendidos_Servicio
            UNION ALL
            SELECT ID_USU FROM Beneficiarios_Asistencia_clean
            UNION ALL
            SELECT ID_USU FROM Beneficiarios_Atendidos_clean
        ) x
        """, Integer.class);

    Integer departamentos = jdbcTemplate.queryForObject("""
        SELECT COUNT(DISTINCT departamento) FROM (
            SELECT DEP_RES AS departamento
            FROM Beneficiarios_Asistencia_clean
            WHERE DEP_RES IS NOT NULL

            UNION ALL

            SELECT DEP_CA AS departamento
            FROM Beneficiarios_Atendidos_clean
            WHERE DEP_CA IS NOT NULL
        ) x
        """, Integer.class);

    return Map.of(
            "totalAtenciones", totalAtenciones,
            "totalBeneficiarios", totalBeneficiarios,
            "serviciosActivos", 3,
            "departamentos", departamentos
    );
  }

  public List<Map<String, Object>> servicios() {
    return jdbcTemplate.queryForList("""
        SELECT TOP 10
            servicio AS label,
            COUNT(DISTINCT ID_USU) AS total
        FROM (
            SELECT 
                TIP_CAR AS servicio,
                ID_USU
            FROM Atendidos_Servicio

            UNION ALL

            SELECT 
                'Asistencia Económica' AS servicio,
                ID_USU
            FROM Beneficiarios_Asistencia_clean

            UNION ALL

            SELECT 
                NOM_SER AS servicio,
                ID_USU
            FROM Beneficiarios_Atendidos_clean
        ) x
        WHERE servicio IS NOT NULL
        GROUP BY servicio
        ORDER BY total DESC
        """);
  }

  public List<Map<String, Object>> genero() {
    return jdbcTemplate.queryForList("""
        SELECT
            genero AS label,
            COUNT(DISTINCT ID_USU) AS total
        FROM (
            SELECT
                CASE
                    WHEN SEX_USU = 1 THEN 'Masculino'
                    WHEN SEX_USU = 2 THEN 'Femenino'
                    ELSE 'No especificado'
                END AS genero,
                ID_USU
            FROM Atendidos_Servicio

            UNION ALL

            SELECT
                CASE
                    WHEN SEX_USU = 1 THEN 'Masculino'
                    WHEN SEX_USU = 2 THEN 'Femenino'
                    ELSE 'No especificado'
                END AS genero,
                ID_USU
            FROM Beneficiarios_Asistencia_clean

            UNION ALL

            SELECT
                CASE
                    WHEN SEX_USU = 1 THEN 'Masculino'
                    WHEN SEX_USU = 2 THEN 'Femenino'
                    ELSE 'No especificado'
                END AS genero,
                ID_USU
            FROM Beneficiarios_Atendidos_clean
        ) x
        GROUP BY genero
        ORDER BY total DESC
        """);
  }

  public List<Map<String, Object>> edades() {
    return jdbcTemplate.queryForList("""
        SELECT
            grupo_edad AS label,
            COUNT(DISTINCT ID_USU) AS total
        FROM (
            SELECT
                ID_USU,
                CASE
                    WHEN EDAD_USU BETWEEN 0 AND 5 THEN 'Primera infancia'
                    WHEN EDAD_USU BETWEEN 6 AND 11 THEN 'Niñez'
                    WHEN EDAD_USU BETWEEN 12 AND 17 THEN 'Adolescencia'
                    WHEN EDAD_USU BETWEEN 18 AND 29 THEN 'Jóvenes'
                    WHEN EDAD_USU BETWEEN 30 AND 59 THEN 'Adultos'
                    ELSE 'Adultos mayores'
                END AS grupo_edad
            FROM Atendidos_Servicio
            WHERE EDAD_USU IS NOT NULL

            UNION ALL

            SELECT
                ID_USU,
                CASE
                    WHEN EDAD_BENEF BETWEEN 0 AND 5 THEN 'Primera infancia'
                    WHEN EDAD_BENEF BETWEEN 6 AND 11 THEN 'Niñez'
                    WHEN EDAD_BENEF BETWEEN 12 AND 17 THEN 'Adolescencia'
                    WHEN EDAD_BENEF BETWEEN 18 AND 29 THEN 'Jóvenes'
                    WHEN EDAD_BENEF BETWEEN 30 AND 59 THEN 'Adultos'
                    ELSE 'Adultos mayores'
                END AS grupo_edad
            FROM Beneficiarios_Asistencia_clean
            WHERE EDAD_BENEF IS NOT NULL

            UNION ALL

            SELECT
                ID_USU,
                CASE
                    WHEN EDAD_USU BETWEEN 0 AND 5 THEN 'Primera infancia'
                    WHEN EDAD_USU BETWEEN 6 AND 11 THEN 'Niñez'
                    WHEN EDAD_USU BETWEEN 12 AND 17 THEN 'Adolescencia'
                    WHEN EDAD_USU BETWEEN 18 AND 29 THEN 'Jóvenes'
                    WHEN EDAD_USU BETWEEN 30 AND 59 THEN 'Adultos'
                    ELSE 'Adultos mayores'
                END AS grupo_edad
            FROM Beneficiarios_Atendidos_clean
            WHERE EDAD_USU IS NOT NULL
        ) x
        GROUP BY grupo_edad
        ORDER BY total DESC
        """);
  }

  public List<Map<String, Object>> departamentos() {
    return jdbcTemplate.queryForList("""
        SELECT TOP 10
            departamento AS label,
            COUNT(DISTINCT ID_USU) AS total
        FROM (
            SELECT 
                DEP_RES AS departamento,
                ID_USU
            FROM Beneficiarios_Asistencia_clean
            WHERE DEP_RES IS NOT NULL

            UNION ALL

            SELECT 
                DEP_CA AS departamento,
                ID_USU
            FROM Beneficiarios_Atendidos_clean
            WHERE DEP_CA IS NOT NULL
        ) x
        GROUP BY departamento
        ORDER BY total DESC
        """);
  }

  public List<Map<String, Object>> comparativo() {
    return jdbcTemplate.queryForList("""
        SELECT
            servicio AS label,
            total
        FROM (
            SELECT
                'Atendidos Servicio' AS servicio,
                COUNT(DISTINCT ID_USU) AS total
            FROM Atendidos_Servicio

            UNION ALL

            SELECT
                'Beneficiarios Asistencia' AS servicio,
                COUNT(DISTINCT ID_USU) AS total
            FROM Beneficiarios_Asistencia_clean

            UNION ALL

            SELECT
                'Beneficiarios Atendidos' AS servicio,
                COUNT(DISTINCT ID_USU) AS total
            FROM Beneficiarios_Atendidos_clean
        ) x
        ORDER BY total DESC
        """);
  }
}