package pe.gob.inabif.reportes.controller;

import org.springframework.web.bind.annotation.*;
import pe.gob.inabif.reportes.service.DashboardService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping("/kpis")
  public Map<String, Object> obtenerKpis() {
    return dashboardService.obtenerKpis();
  }

  @GetMapping("/servicios")
  public List<Map<String, Object>> servicios() {
    return dashboardService.servicios();
  }

  @GetMapping("/genero")
  public List<Map<String, Object>> genero() {
    return dashboardService.genero();
  }

  @GetMapping("/edades")
  public List<Map<String, Object>> edades() {
    return dashboardService.edades();
  }

  @GetMapping("/departamentos")
  public List<Map<String, Object>> departamentos() {
    return dashboardService.departamentos();
  }

  @GetMapping("/comparativo")
  public List<Map<String, Object>> comparativo() {
    return dashboardService.comparativo();
  }
}