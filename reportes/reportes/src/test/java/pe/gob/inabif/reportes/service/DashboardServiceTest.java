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
class DashboardServiceTest {

  @Mock
  private JdbcTemplate jdbcTemplate;

  private DashboardService dashboardService;

  @BeforeEach
  void setUp() {
    dashboardService = new DashboardService(jdbcTemplate);
  }

  @Test
  void obtenerKpis() {
    when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
        .thenReturn(100, 50, 10);

    Map<String, Object> result = dashboardService.obtenerKpis();

    assertEquals(100, result.get("totalAtenciones"));
    assertEquals(50, result.get("totalBeneficiarios"));
    assertEquals(3, result.get("serviciosActivos"));
    assertEquals(10, result.get("departamentos"));
    verify(jdbcTemplate, times(3)).queryForObject(anyString(), eq(Integer.class));
  }

  @Test
  void servicios() {
    when(jdbcTemplate.queryForList(anyString()))
        .thenReturn(List.of(Map.of("label", "Servicio A", "total", 100)));

    List<Map<String, Object>> result = dashboardService.servicios();

    assertEquals(1, result.size());
  }

  @Test
  void genero() {
    when(jdbcTemplate.queryForList(anyString()))
        .thenReturn(List.of(Map.of("label", "Masculino", "total", 60)));

    List<Map<String, Object>> result = dashboardService.genero();

    assertEquals(1, result.size());
  }

  @Test
  void edades() {
    when(jdbcTemplate.queryForList(anyString()))
        .thenReturn(List.of(Map.of("label", "Adultos", "total", 200)));

    List<Map<String, Object>> result = dashboardService.edades();

    assertEquals(1, result.size());
  }

  @Test
  void departamentos() {
    when(jdbcTemplate.queryForList(anyString()))
        .thenReturn(List.of(Map.of("label", "Lima", "total", 500)));

    List<Map<String, Object>> result = dashboardService.departamentos();

    assertEquals(1, result.size());
  }

  @Test
  void comparativo() {
    when(jdbcTemplate.queryForList(anyString()))
        .thenReturn(List.of(Map.of("label", "Atendidos Servicio", "total", 300)));

    List<Map<String, Object>> result = dashboardService.comparativo();

    assertEquals(1, result.size());
  }
}
