package irs.client.irs.Forms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

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
                dispose();
            }
        });
    }

    public void open () {
        setContentPane(new MainForm().panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(250, 120);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
