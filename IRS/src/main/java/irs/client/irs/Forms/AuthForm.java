package irs.client.irs.Forms;

import irs.client.irs.WebClient.Services.UserService;
import irs.server.irs_server.payload.request.LoginRequest;
import irs.server.irs_server.payload.response.JwtResponse;
import org.springframework.beans.factory.annotation.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthForm extends JFrame {
    private JTextField tLogin;
    private JPasswordField tPass;
    private JButton signInB;
    private JLabel lLogin;
    private JLabel lPass;
    private JPanel panel;
    private boolean isChangeForm;
    private ChangeForm changeForm;
    private ViewForm viewForm;
    @Value("${irs.app.UserLogin}")
    private String UserLogin = "user";
    @Value("${irs.app.UserPassword}")
    private String UserPassword = "user123";
    private LoginRequest loginRequest;

    public AuthForm(boolean isChangeForm)
    {
        this.isChangeForm = isChangeForm;
        signInB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tLogin.getText().equals("") | tPass.getText().equals("")) {
                    signInB.setForeground(Color.RED);
                    signInB.setText("Заполните поля логин и пароль");
                    return;
                }
                try {
                    singIn();
                }
                catch (Exception exception) {
                    JPanel pnl = (JPanel) getContentPane();
                    JOptionPane.showMessageDialog(pnl, exception.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    private void singIn()
    {
        UserService userService = new UserService();
        loginRequest = new LoginRequest(tLogin.getText(), tPass.getText());
        userService.authenticateUser(loginRequest)
                .subscribe(JwtResponse -> openForm(JwtResponse));
    }

    private void openForm(JwtResponse response)
    {
        if (this.isChangeForm)
            openChangeForm(response);
        else
            openViewForm(response);

    }

    private void openChangeForm(JwtResponse response)
    {
        if (response.getRoles().contains("ROLE_ADMIN"))
        {
            changeForm = new ChangeForm(response, loginRequest);
            changeForm.open();
            JFrame.getFrames()[2].dispose();
        }
        else
        {
            signInB.setForeground(Color.RED);
            signInB.setText("У вас нет доступа к изменению");
        }
    }

    private void openViewForm(JwtResponse response)
    {
        if (response.getRoles().contains("ROLE_ADMIN") | response.getRoles().contains("ROLE_USER"))
        {
            viewForm = new ViewForm(response, loginRequest);
            viewForm.open();
            JFrame.getFrames()[2].dispose();
        }
        else
        {
            signInB.setForeground(Color.RED);
            signInB.setText("У вас нет доступа к просмотру");
        }
    }

    public void open(){
        setTitle("IRS");
        setContentPane(new AuthForm(isChangeForm).panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(250, 200);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        if (!isChangeForm) {
            tLogin.setText(UserLogin);
            tPass.setText(UserPassword);
            singIn();
        }
    }
}
