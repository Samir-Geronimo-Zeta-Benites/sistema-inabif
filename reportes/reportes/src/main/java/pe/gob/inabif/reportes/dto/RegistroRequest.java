package pe.gob.inabif.reportes.dto;

import java.util.Map;

public class RegistroRequest {

  private String tabla;
  private Map<String, Object> datos;

  public RegistroRequest() {
  }

  public String getTabla() {
    return tabla;
  }

  public void setTabla(String tabla) {
    this.tabla = tabla;
  }

  public Map<String, Object> getDatos() {
    return datos;
  }

  public void setDatos(Map<String, Object> datos) {
    this.datos = datos;
  }
}