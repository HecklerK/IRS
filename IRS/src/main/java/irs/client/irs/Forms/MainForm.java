package irs.client.irs.Forms;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MainForm extends JFrame {
    public JPanel panel;
    private JButton ChangeButton;
    private JButton ViewButton;
    AuthForm authForm;

    public MainForm() {
        ChangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authForm = new AuthForm(true);
                authForm.open();
                JFrame.getFrames()[0].dispose();
            }
        });
        ViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authForm = new AuthForm(false);
                authForm.open();
                JFrame.getFrames()[0].dispose();
            }
        });
    }

    public void open () {
        setTitle("IRS");
        setContentPane(new MainForm().panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(250, 120);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Icons/scooter-Freepik.png")));
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
