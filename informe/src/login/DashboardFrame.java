package login;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class DashboardFrame extends JFrame {

  private final Color ROJO = new Color(192, 39, 26);
  private final Color NARANJA = new Color(232, 88, 26);

  public DashboardFrame(String usuario) {
    setTitle("INABIF - Dashboard");
    setSize(1100, 650);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    JPanel root = new JPanel(new BorderLayout());

    JPanel topbar = new JPanel(new BorderLayout());
    topbar.setBackground(Color.WHITE);
    topbar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

    JLabel titulo = new JLabel("INABIF - Sistema de Reportes");
    titulo.setFont(new Font("Arial", Font.BOLD, 20));
    titulo.setForeground(ROJO);

    JButton btnSalir = new JButton("Salir");
    btnSalir.addActionListener(e -> {
      new LoginFrame();
      dispose();
    });

    topbar.add(titulo, BorderLayout.WEST);
    topbar.add(btnSalir, BorderLayout.EAST);

    JPanel sidebar = new JPanel();
    sidebar.setPreferredSize(new Dimension(220, 0));
    sidebar.setBackground(new Color(245, 245, 245));
    sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
    sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

    JButton btnGestion = crearBotonMenu("Gestión de Tablas");
    JButton btnReportes = crearBotonMenu("Ver Reportes");

    sidebar.add(btnGestion);
    sidebar.add(Box.createVerticalStrut(10));
    sidebar.add(btnReportes);
    sidebar.add(Box.createVerticalGlue());

    JLabel lblConexion = new JLabel("<html><b>BD:</b> informe<br><span style='color:green'>● Conectado</span></html>");
    sidebar.add(lblConexion);

    JPanel content = new JPanel(new BorderLayout());
    content.setBackground(new Color(249, 246, 245));
    content.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

    JPanel dashboardPanel = crearPanelGestion();

    content.add(dashboardPanel, BorderLayout.CENTER);

    btnGestion.addActionListener(e -> {
      content.removeAll();
      content.add(crearPanelGestion(), BorderLayout.CENTER);
      content.revalidate();
      content.repaint();
    });

    btnReportes.addActionListener(e -> {
      content.removeAll();
      content.add(new ReporteFrame(false), BorderLayout.CENTER);
      content.revalidate();
      content.repaint();
    });

    root.add(topbar, BorderLayout.NORTH);
    root.add(sidebar, BorderLayout.WEST);
    root.add(content, BorderLayout.CENTER);

    add(root);
    setVisible(true);
  }

  private JButton crearBotonMenu(String texto) {
    JButton btn = new JButton(texto);
    btn.setMaximumSize(new Dimension(190, 40));
    btn.setFocusPainted(false);
    btn.setBackground(Color.WHITE);
    btn.setHorizontalAlignment(SwingConstants.LEFT);
    return btn;
  }

  private JPanel crearPanelGestion() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);

    JLabel titulo = new JLabel("Gestión de Tablas");
    titulo.setFont(new Font("Arial", Font.BOLD, 26));

    JPanel cards = new JPanel(new GridLayout(1, 3, 20, 20));
    cards.setOpaque(false);

    cards.add(crearCard("Atendidos_Servicio"));
    cards.add(crearCard("Beneficiarios_Asistencias_clean"));
    cards.add(crearCard("Beneficiarios_Atendidos_clean"));

    JButton btnReporte = new JButton("Ver Reportes");
    btnReporte.setBackground(NARANJA);
    btnReporte.setForeground(Color.WHITE);
    btnReporte.setFocusPainted(false);
    btnReporte.addActionListener(e -> new ReporteFrame(true));

    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);
    header.add(titulo, BorderLayout.WEST);
    header.add(btnReporte, BorderLayout.EAST);

    panel.add(header, BorderLayout.NORTH);
    panel.add(cards, BorderLayout.CENTER);

    return panel;
  }

  private JPanel crearCard(String tabla) {
    JPanel card = new JPanel(null);
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

    JLabel lbl = new JLabel("<html><b>" + tabla + "</b></html>");
    lbl.setBounds(20, 25, 260, 30);
    card.add(lbl);

    JLabel desc = new JLabel("<html>Registro y administración de datos de la tabla.</html>");
    desc.setBounds(20, 70, 250, 60);
    card.add(desc);

    JButton btnAgregar = new JButton("Agregar Registro");
    btnAgregar.setBounds(20, 150, 210, 38);
    btnAgregar.setBackground(ROJO);
    btnAgregar.setForeground(Color.WHITE);
    btnAgregar.setFocusPainted(false);
    btnAgregar.addActionListener(e -> abrirModal(tabla));

    card.add(btnAgregar);

    return card;
  }

  private void abrirModal(String tabla) {
    JDialog modal = new JDialog(this, "Agregar Registro - " + tabla, true);
    modal.setSize(420, 300);
    modal.setLocationRelativeTo(this);
    modal.setLayout(null);

    JLabel lblId = new JLabel("ID_USU:");
    lblId.setBounds(40, 40, 100, 25);
    modal.add(lblId);

    JTextField txtId = new JTextField();
    txtId.setBounds(150, 40, 200, 30);
    modal.add(txtId);

    JLabel lblFecha = new JLabel("Fecha Ingreso:");
    lblFecha.setBounds(40, 90, 100, 25);
    modal.add(lblFecha);

    JTextField txtFecha = new JTextField("2026-06-01");
    txtFecha.setBounds(150, 90, 200, 30);
    modal.add(txtFecha);

    JButton btnGuardar = new JButton("Guardar");
    btnGuardar.setBounds(80, 170, 120, 35);
    btnGuardar.setBackground(ROJO);
    btnGuardar.setForeground(Color.WHITE);

    JButton btnCerrar = new JButton("Cerrar");
    btnCerrar.setBounds(220, 170, 120, 35);

    btnGuardar.addActionListener(e -> {
      try {
        guardarRegistro(tabla, txtId.getText(), txtFecha.getText());
        JOptionPane.showMessageDialog(modal, "Registro guardado correctamente");
        modal.dispose();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(modal, "Error al guardar:\n" + ex.getMessage());
      }
    });

    btnCerrar.addActionListener(e -> modal.dispose());

    modal.add(btnGuardar);
    modal.add(btnCerrar);

    modal.setVisible(true);
  }

  private void guardarRegistro(String tabla, String idUsu, String fecha) throws Exception {
    String sql;

    if (tabla.equals("Atendidos_Servicio")) {
      sql = "INSERT INTO Atendidos_Servicio (ID_USU, FEC_ING) VALUES (?, ?)";
    } else {
      sql = "INSERT INTO " + tabla + " (ID_USU) VALUES (?)";
    }

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, idUsu);

      if (tabla.equals("Atendidos_Servicio")) {
        ps.setString(2, fecha);
      }

      ps.executeUpdate();
    }
  }
}