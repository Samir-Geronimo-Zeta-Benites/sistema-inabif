package pe.gob.inabif.reportes.dto;

public class LoginRequest {

  private String user;
  private String password;

  public LoginRequest() {
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}