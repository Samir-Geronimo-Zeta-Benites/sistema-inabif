package pe.gob.inabif.reportes.controller;

import org.springframework.web.bind.annotation.*;
import pe.gob.inabif.reportes.dto.LoginRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @PostMapping("/login")
  public Map<String, Object> login(@RequestBody LoginRequest request) {

    boolean correcto =
            "admin".equals(request.getUser()) &&
                    "123456".equals(request.getPassword());

    return Map.of(
            "success", correcto,
            "message", correcto ? "Login correcto" : "Credenciales incorrectas"
    );
  }
}