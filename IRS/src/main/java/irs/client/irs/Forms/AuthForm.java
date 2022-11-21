package irs.client.irs.Forms;

import irs.client.irs.WebClient.Services.UserService;
import irs.server.irs_server.payload.request.LoginRequest;
import irs.server.irs_server.payload.response.JwtResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthForm extends JFrame {
    private JTextField tLogin;
    private JPasswordField tPass;
    private JButton signin;
    private JLabel lLogin;
    private JLabel lPass;
    private JPanel panel;

    private boolean changeForm;

    public AuthForm(boolean changeForm)
    {
        this.changeForm = changeForm;
        signin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tLogin.getText().equals("") | tPass.getText().equals("")) {
                    signin.setForeground(Color.RED);
                    signin.setText("Заполните поля логин и пароль");
                    return;
                }
                UserService userService = new UserService();
                LoginRequest request = new LoginRequest(tLogin.getText(), tPass.getText());
                userService.authenticateUser(request)
                        .subscribe(JwtResponse -> openChangeForm(JwtResponse));
            }
        });
    }

    private void openChangeForm(JwtResponse response)
    {
        if (response.getRoles().contains("ROLE_ADMIN"))
        {

        }
    }

    public void open(){
        setContentPane(new AuthForm(changeForm).panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(250, 200);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
