package login;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

  private JTextField txtUser;
  private JPasswordField txtPassword;

  public LoginFrame() {
    setTitle("INABIF - Login");
    setSize(900, 520);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setResizable(false);

    JPanel root = new JPanel(new GridLayout(1, 2));

    JPanel left = new JPanel();
    left.setBackground(new Color(192, 39, 26));
    left.setLayout(new GridBagLayout());

    JLabel title = new JLabel("<html><center>Sistema de<br>Reportes<br>INABIF</center></html>");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("Arial", Font.BOLD, 36));

    JLabel desc = new JLabel("<html><center>Gestión de atenciones,<br>beneficiarios y servicios sociales</center></html>");
    desc.setForeground(Color.WHITE);
    desc.setFont(new Font("Arial", Font.PLAIN, 16));

    JPanel leftContent = new JPanel();
    leftContent.setOpaque(false);
    leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));

    title.setAlignmentX(Component.CENTER_ALIGNMENT);
    desc.setAlignmentX(Component.CENTER_ALIGNMENT);

    leftContent.add(title);
    leftContent.add(Box.createVerticalStrut(25));
    leftContent.add(desc);
    left.add(leftContent);

    JPanel right = new JPanel(null);
    right.setBackground(Color.WHITE);

    JLabel lblLogo = new JLabel("INABIF");
    lblLogo.setFont(new Font("Arial", Font.BOLD, 26));
    lblLogo.setForeground(new Color(192, 39, 26));
    lblLogo.setBounds(70, 60, 250, 35);
    right.add(lblLogo);

    JLabel lblWelcome = new JLabel("Bienvenido de vuelta");
    lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
    lblWelcome.setBounds(70, 120, 300, 35);
    right.add(lblWelcome);

    JLabel lblUser = new JLabel("Usuario");
    lblUser.setBounds(70, 185, 250, 25);
    right.add(lblUser);

    txtUser = new JTextField();
    txtUser.setBounds(70, 215, 300, 38);
    right.add(txtUser);

    JLabel lblPassword = new JLabel("Contraseña");
    lblPassword.setBounds(70, 270, 250, 25);
    right.add(lblPassword);

    txtPassword = new JPasswordField();
    txtPassword.setBounds(70, 300, 300, 38);
    right.add(txtPassword);

    JButton btnLogin = new JButton("Iniciar Sesión");
    btnLogin.setBounds(70, 365, 300, 42);
    btnLogin.setBackground(new Color(192, 39, 26));
    btnLogin.setForeground(Color.WHITE);
    btnLogin.setFocusPainted(false);
    btnLogin.setFont(new Font("Arial", Font.BOLD, 15));
    right.add(btnLogin);

    btnLogin.addActionListener(e -> login());

    root.add(left);
    root.add(right);

    add(root);
    setVisible(true);
  }

  private void login() {
    String user = txtUser.getText().trim();
    String pass = new String(txtPassword.getPassword());

    if (user.equals("admin") && pass.equals("123456")) {
      new DashboardFrame(user);
      dispose();
    } else {
      JOptionPane.showMessageDialog(this, "Credenciales incorrectas");
    }
  }
}