package pe.gob.inabif.reportes.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.gob.inabif.reportes.service.DashboardService;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

  @Mock
  private DashboardService dashboardService;

  private DashboardController controller;

  @BeforeEach
  void setUp() {
    controller = new DashboardController(dashboardService);
  }

  @Test
  void obtenerKpis() {
    when(dashboardService.obtenerKpis()).thenReturn(Map.of("totalAtenciones", 100));

    Map<String, Object> result = controller.obtenerKpis();

    assertEquals(100, result.get("totalAtenciones"));
    verify(dashboardService).obtenerKpis();
  }

  @Test
  void servicios() {
    when(dashboardService.servicios()).thenReturn(List.of(Map.of("label", "Servicio A")));

    List<Map<String, Object>> result = controller.servicios();

    assertEquals(1, result.size());
    verify(dashboardService).servicios();
  }

  @Test
  void genero() {
    when(dashboardService.genero()).thenReturn(List.of(Map.of("label", "Masculino")));

    List<Map<String, Object>> result = controller.genero();

    assertEquals(1, result.size());
    verify(dashboardService).genero();
  }

  @Test
  void edades() {
    when(dashboardService.edades()).thenReturn(List.of(Map.of("label", "Adultos")));

    List<Map<String, Object>> result = controller.edades();

    assertEquals(1, result.size());
    verify(dashboardService).edades();
  }

  @Test
  void departamentos() {
    when(dashboardService.departamentos()).thenReturn(List.of(Map.of("label", "Lima")));

    List<Map<String, Object>> result = controller.departamentos();

    assertEquals(1, result.size());
    verify(dashboardService).departamentos();
  }

  @Test
  void comparativo() {
    when(dashboardService.comparativo()).thenReturn(List.of(Map.of("label", "2024")));

    List<Map<String, Object>> result = controller.comparativo();

    assertEquals(1, result.size());
    verify(dashboardService).comparativo();
  }
}
