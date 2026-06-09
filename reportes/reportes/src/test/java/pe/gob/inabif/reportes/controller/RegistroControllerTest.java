package pe.gob.inabif.reportes.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.gob.inabif.reportes.dto.RegistroRequest;
import pe.gob.inabif.reportes.service.RegistroService;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class RegistroControllerTest {

  @Mock
  private RegistroService registroService;

  private RegistroController controller;

  @BeforeEach
  void setUp() {
    controller = new RegistroController(registroService);
  }

  @Test
  void guardar() {
    RegistroRequest request = new RegistroRequest();
    request.setTabla("Atendidos_Servicio");
    request.setDatos(Map.of("ID_USU", "123"));

    Map<String, Object> result = controller.guardar(request);

    assertTrue((Boolean) result.get("success"));
    assertEquals("Registro guardado correctamente", result.get("message"));
    verify(registroService).guardarRegistro("Atendidos_Servicio", Map.of("ID_USU", "123"));
  }

  @Test
  void actualizar() {
    RegistroRequest request = new RegistroRequest();
    request.setTabla("Atendidos_Servicio");
    request.setDatos(Map.of("ID_USU", "123", "FEC_ING", "2024-01-15"));

    Map<String, Object> result = controller.actualizar(request);

    assertTrue((Boolean) result.get("success"));
    assertEquals("Registro actualizado correctamente", result.get("message"));
    verify(registroService).actualizarRegistro("Atendidos_Servicio", Map.of("ID_USU", "123", "FEC_ING", "2024-01-15"));
  }

  @Test
  void eliminar() {
    Map<String, Object> body = Map.of("tabla", "Atendidos_Servicio", "id", "123");

    Map<String, Object> result = controller.eliminar(body);

    assertTrue((Boolean) result.get("success"));
    assertEquals("Registro eliminado correctamente", result.get("message"));
    verify(registroService).eliminarRegistro("Atendidos_Servicio", "123");
  }

  @Test
  void eliminarFaltanParametros() {
    Map<String, Object> body = Map.of("tabla", "Atendidos_Servicio");

    Map<String, Object> result = controller.eliminar(body);

    assertFalse((Boolean) result.get("success"));
    assertEquals("Faltan parámetros: tabla e id son requeridos", result.get("message"));
    verify(registroService, never()).eliminarRegistro(anyString(), anyString());
  }
}
