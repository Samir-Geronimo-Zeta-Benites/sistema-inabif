package pe.gob.inabif.reportes.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pe.gob.inabif.reportes.dto.LoginRequest;

import java.util.Map;

class AuthControllerTest {

  private final AuthController controller = new AuthController();

  @Test
  void loginExitoso() {
    LoginRequest request = new LoginRequest();
    request.setUser("admin");
    request.setPassword("123456");

    Map<String, Object> result = controller.login(request);

    assertTrue((Boolean) result.get("success"));
    assertEquals("Login correcto", result.get("message"));
  }

  @Test
  void loginFallido() {
    LoginRequest request = new LoginRequest();
    request.setUser("admin");
    request.setPassword("wrong");

    Map<String, Object> result = controller.login(request);

    assertFalse((Boolean) result.get("success"));
    assertEquals("Credenciales incorrectas", result.get("message"));
  }

  @Test
  void loginUsuarioVacio() {
    LoginRequest request = new LoginRequest();
    request.setUser("");
    request.setPassword("123456");

    Map<String, Object> result = controller.login(request);

    assertFalse((Boolean) result.get("success"));
  }
}
