package pe.gob.inabif.reportes.controller;

import org.springframework.web.bind.annotation.*;
import pe.gob.inabif.reportes.service.ReporteService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

  private final ReporteService reporteService;

  public ReporteController(ReporteService reporteService) {
    this.reporteService = reporteService;
  }

  @GetMapping
  public List<Map<String, Object>> obtenerReporte(
          @RequestParam String tabla,
          @RequestParam int mes,
          @RequestParam int anio
  ) {
    return reporteService.obtenerReporte(tabla, mes, anio);
  }
}