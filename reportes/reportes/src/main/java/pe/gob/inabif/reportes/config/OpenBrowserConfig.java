package pe.gob.inabif.reportes.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

@Component
public class OpenBrowserConfig implements CommandLineRunner {

  @Override
  public void run(String... args) {
    try {
      if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(new URI("http://localhost:8080"));
      }
    } catch (Exception e) {
      System.out.println("No se pudo abrir el navegador automáticamente.");
    }
  }
}