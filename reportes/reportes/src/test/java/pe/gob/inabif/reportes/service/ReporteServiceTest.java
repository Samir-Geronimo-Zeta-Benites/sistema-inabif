package pe.gob.inabif.reportes.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

  @Mock
  private JdbcTemplate jdbcTemplate;

  private ReporteService reporteService;

  @BeforeEach
  void setUp() {
    reporteService = new ReporteService(jdbcTemplate);
  }

  @Test
  void obtenerReporteAtendidosServicio() {
    when(jdbcTemplate.queryForList(anyString(), eq(1), eq(2024)))
        .thenReturn(List.of(Map.of("ID_USU", "123")));

    List<Map<String, Object>> result = reporteService.obtenerReporte("Atendidos_Servicio", 1, 2024);

    assertEquals(1, result.size());
    assertEquals("123", result.get(0).get("ID_USU"));
  }

  @Test
  void obtenerReporteBeneficiariosAsistencia() {
    when(jdbcTemplate.queryForList(anyString(), eq(3), eq(2025)))
        .thenReturn(List.of(Map.of("ID_USU", "456")));

    List<Map<String, Object>> result = reporteService.obtenerReporte("Beneficiarios_Asistencia_clean", 3, 2025);

    assertEquals(1, result.size());
    assertEquals("456", result.get(0).get("ID_USU"));
  }

  @Test
  void obtenerReporteBeneficiariosAtendidos() {
    when(jdbcTemplate.queryForList(anyString(), eq(6), eq(2024)))
        .thenReturn(List.of(Map.of("ID_USU", "789")));

    List<Map<String, Object>> result = reporteService.obtenerReporte("Beneficiarios_Atendidos_clean", 6, 2024);

    assertEquals(1, result.size());
    assertEquals("789", result.get(0).get("ID_USU"));
  }

  @Test
  void obtenerReporteTablaInvalida() {
    assertThrows(IllegalArgumentException.class,
        () -> reporteService.obtenerReporte("Tabla_Inexistente", 1, 2024));
  }
}
