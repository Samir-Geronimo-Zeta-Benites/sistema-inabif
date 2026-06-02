package login;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ReporteFrame extends JPanel {

  JTable tabla;
  JComboBox<String> comboTablas;
  JComboBox<String> comboMes;
  JComboBox<String> comboAnio;

  public ReporteFrame(boolean ventanaIndependiente) {
    setLayout(new BorderLayout());
    setBackground(new Color(249, 246, 245));

    JPanel topPanel = new JPanel();
    topPanel.setBackground(Color.WHITE);

    comboTablas = new JComboBox<>();
    comboTablas.addItem("Atendidos_Servicio");
    comboTablas.addItem("Beneficiarios_Asistencias_clean");
    comboTablas.addItem("Beneficiarios_Atendidos_clean");

    comboMes = new JComboBox<>();
    String[] meses = {
            "01-Enero", "02-Febrero", "03-Marzo", "04-Abril",
            "05-Mayo", "06-Junio", "07-Julio", "08-Agosto",
            "09-Septiembre", "10-Octubre", "11-Noviembre", "12-Diciembre"
    };
    for (String m : meses) comboMes.addItem(m);

    comboAnio = new JComboBox<>();
    for (int y = 2022; y <= 2026; y++) comboAnio.addItem(String.valueOf(y));

    JButton btnCargar = new JButton("Cargar Tabla");
    JButton btnExcel = new JButton("Exportar Excel");
    JButton btnPdf = new JButton("Exportar PDF");

    topPanel.add(new JLabel("Tabla:"));
    topPanel.add(comboTablas);
    topPanel.add(new JLabel("Mes:"));
    topPanel.add(comboMes);
    topPanel.add(new JLabel("Año:"));
    topPanel.add(comboAnio);
    topPanel.add(btnCargar);
    topPanel.add(btnExcel);
    topPanel.add(btnPdf);

    tabla = new JTable();

    add(topPanel, BorderLayout.NORTH);
    add(new JScrollPane(tabla), BorderLayout.CENTER);

    btnCargar.addActionListener(e -> cargarDatos());
    btnExcel.addActionListener(e -> JOptionPane.showMessageDialog(this, "Exportación Excel pendiente"));
    btnPdf.addActionListener(e -> JOptionPane.showMessageDialog(this, "Exportación PDF pendiente"));

    if (ventanaIndependiente) {
      JFrame frame = new JFrame("Reportes");
      frame.setSize(1200, 600);
      frame.setLocationRelativeTo(null);
      frame.add(this);
      frame.setVisible(true);
    }
  }

  private void cargarDatos() {
    try {
      String mesStr = comboMes.getSelectedItem().toString().substring(0, 2);
      int mes = Integer.parseInt(mesStr);
      int anio = Integer.parseInt(comboAnio.getSelectedItem().toString());
      String tablaSeleccionada = comboTablas.getSelectedItem().toString();

      String query;

      switch (tablaSeleccionada) {
        case "Beneficiarios_Atendidos_clean":
          query = """
                            SELECT b.*, a.FEC_ING
                            FROM Beneficiarios_Atendidos_clean b
                            INNER JOIN Atendidos_Servicio a ON a.ID_USU = b.ID_USU
                            WHERE MONTH(a.FEC_ING) = ?
                            AND YEAR(a.FEC_ING) = ?
                            """;
          break;

        case "Beneficiarios_Asistencias_clean":
          query = """
                            SELECT b.*, a.FEC_ING
                            FROM Beneficiarios_Asistencia_clean b
                            INNER JOIN Atendidos_Servicio a ON a.ID_USU = b.ID_USU
                            WHERE MONTH(a.FEC_ING) = ?
                            AND YEAR(a.FEC_ING) = ?
                            """;
          break;

        default:
          query = """
                            SELECT *
                            FROM Atendidos_Servicio
                            WHERE MONTH(FEC_ING) = ?
                            AND YEAR(FEC_ING) = ?
                            """;
          break;
      }

      try (Connection conn = DatabaseConnection.getConnection();
           PreparedStatement ps = conn.prepareStatement(query)) {

        ps.setInt(1, mes);
        ps.setInt(2, anio);

        ResultSet rs = ps.executeQuery();

        DefaultTableModel model = new DefaultTableModel();
        ResultSetMetaData meta = rs.getMetaData();
        int columnas = meta.getColumnCount();

        for (int i = 1; i <= columnas; i++) {
          model.addColumn(meta.getColumnName(i));
        }

        while (rs.next()) {
          Object[] fila = new Object[columnas];
          for (int i = 0; i < columnas; i++) {
            fila[i] = rs.getObject(i + 1);
          }
          model.addRow(fila);
        }

        tabla.setModel(model);
      }

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error:\n" + e.getMessage());
      e.printStackTrace();
    }
  }
}