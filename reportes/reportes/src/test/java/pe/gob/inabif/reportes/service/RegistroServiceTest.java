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

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class RegistroServiceTest {

  @Mock
  private JdbcTemplate jdbcTemplate;

  private RegistroService registroService;

  @BeforeEach
  void setUp() {
    registroService = new RegistroService(jdbcTemplate);
  }

  @Test
  void guardarAtendidoServicio() {
    Map<String, Object> datos = Map.of("ID_USU", "123", "FEC_ING", "2024-01-15");

    registroService.guardarRegistro("Atendidos_Servicio", datos);

    verify(jdbcTemplate).update(anyString(), eq("123"), eq("2024-01-15"));
  }

  @Test
  void guardarBeneficiarioAsistencia() {
    Map<String, Object> datos = Map.of("ID_USU", "456");

    registroService.guardarRegistro("Beneficiarios_Asistencia_clean", datos);

    verify(jdbcTemplate).update(anyString(), eq("456"));
  }

  @Test
  void guardarBeneficiarioAtendido() {
    Map<String, Object> datos = Map.of("ID_USU", "789");

    registroService.guardarRegistro("Beneficiarios_Atendidos_clean", datos);

    verify(jdbcTemplate).update(anyString(), eq("789"));
  }

  @Test
  void guardarTablaInvalida() {
    Map<String, Object> datos = Map.of("ID_USU", "999");

    assertThrows(IllegalArgumentException.class,
        () -> registroService.guardarRegistro("Tabla_Invalida", datos));
  }

  @Test
  void actualizarAtendidoServicio() {
    Map<String, Object> datos = Map.of("ID_USU", "123", "FEC_ING", "2024-01-15");

    registroService.actualizarRegistro("Atendidos_Servicio", datos);

    verify(jdbcTemplate).update(anyString(), eq("2024-01-15"), eq("123"));
  }

  @Test
  void actualizarTablaInvalida() {
    assertThrows(IllegalArgumentException.class,
        () -> registroService.actualizarRegistro("Tabla_Invalida", Map.of()));
  }

  @Test
  void eliminarAtendidoServicio() {
    registroService.eliminarRegistro("Atendidos_Servicio", "123");

    verify(jdbcTemplate).update(anyString(), eq("123"));
  }

  @Test
  void eliminarBeneficiarioAsistencia() {
    registroService.eliminarRegistro("Beneficiarios_Asistencia_clean", "456");

    verify(jdbcTemplate).update(anyString(), eq("456"));
  }

  @Test
  void eliminarBeneficiarioAtendido() {
    registroService.eliminarRegistro("Beneficiarios_Atendidos_clean", "789");

    verify(jdbcTemplate).update(anyString(), eq("789"));
  }

  @Test
  void eliminarTablaInvalida() {
    assertThrows(IllegalArgumentException.class,
        () -> registroService.eliminarRegistro("Tabla_Invalida", "999"));
  }
}
