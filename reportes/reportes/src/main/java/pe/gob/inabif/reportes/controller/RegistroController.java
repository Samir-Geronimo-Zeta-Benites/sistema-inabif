package pe.gob.inabif.reportes.controller;

import org.springframework.web.bind.annotation.*;
import pe.gob.inabif.reportes.dto.RegistroRequest;
import pe.gob.inabif.reportes.service.RegistroService;

import java.util.Map;

@RestController
@RequestMapping("/api/registros")
public class RegistroController {

  private final RegistroService registroService;

  public RegistroController(RegistroService registroService) {
    this.registroService = registroService;
  }

  @PostMapping
  public Map<String, Object> guardar(@RequestBody RegistroRequest request) {
    registroService.guardarRegistro(request.getTabla(), request.getDatos());

    return Map.of(
            "success", true,
            "message", "Registro guardado correctamente"
    );
  }

  @PutMapping
  public Map<String, Object> actualizar(@RequestBody RegistroRequest request) {
    registroService.actualizarRegistro(request.getTabla(), request.getDatos());

    return Map.of(
            "success", true,
            "message", "Registro actualizado correctamente"
    );
  }

  @DeleteMapping
  public Map<String, Object> eliminar(@RequestBody Map<String, Object> body) {
    String tabla = (String) body.get("tabla");
    String id = (String) body.get("id");

    if (tabla == null || id == null) {
      return Map.of(
              "success", false,
              "message", "Faltan parámetros: tabla e id son requeridos"
      );
    }

    registroService.eliminarRegistro(tabla, id);

    return Map.of(
            "success", true,
            "message", "Registro eliminado correctamente"
    );
  }
}