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
}