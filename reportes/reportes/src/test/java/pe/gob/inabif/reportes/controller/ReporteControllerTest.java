package pe.gob.inabif.reportes.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.gob.inabif.reportes.service.ReporteService;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class ReporteControllerTest {

  @Mock
  private ReporteService reporteService;

  private ReporteController controller;

  @BeforeEach
  void setUp() {
    controller = new ReporteController(reporteService);
  }

  @Test
  void obtenerReporte() {
    when(reporteService.obtenerReporte("Atendidos_Servicio", 1, 2024))
        .thenReturn(List.of(Map.of("ID_USU", "123")));

    List<Map<String, Object>> result = controller.obtenerReporte("Atendidos_Servicio", 1, 2024);

    assertEquals(1, result.size());
    verify(reporteService).obtenerReporte("Atendidos_Servicio", 1, 2024);
  }
}
