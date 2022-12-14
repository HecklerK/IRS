package irs.client.irs.Forms;

import irs.client.irs.WebClient.Services.UserService;
import irs.server.irs_server.payload.request.LoginRequest;
import irs.server.irs_server.payload.response.JwtResponse;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClientRequestException;

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
    private String text;
    private Timer clearButton;

    public AuthForm(boolean isChangeForm)
    {
        this.isChangeForm = isChangeForm;
        signInB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tLogin.getText().equals("") | tPass.getText().equals("")) {
                    message("Заполните поля логин и пароль");
                    return;
                }
                singIn();
            }
        });
    }

    private void singIn()
    {
        UserService userService = new UserService();
        loginRequest = new LoginRequest(tLogin.getText(), tPass.getText());
        try {
            JwtResponse response = userService.authenticateUserSync(loginRequest);
            openForm(response);
        }
        catch (WebClientRequestException ex)
        {
            JPanel pnl = (JPanel) getContentPane();
            JOptionPane.showMessageDialog(pnl, "Не удаётся соединиться с сервером", "Ошибка", JOptionPane.WARNING_MESSAGE);
        }
        catch (Exception ex) {
            JPanel pnl = (JPanel) getContentPane();
            JOptionPane.showMessageDialog(pnl, ex.getMessage(), "Ошибка", JOptionPane.WARNING_MESSAGE);
        }
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
            message("У вас нет доступа к просмотру");
        }
    }

    private void message(String string)
    {
        text = signInB.getText();
        signInB.setText(string);
        signInB.setForeground(Color.RED);
        signInB.setEnabled(false);

        clearButton = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearButton();
            }
        });

        clearButton.setRepeats(false);
        clearButton.start();
    }

    private void clearButton()
    {
        signInB.setForeground(new Color(252, 253, 255));
        signInB.setText(text);
        signInB.setEnabled(true);
    }

    public void open(){
        setTitle("IRS");
        setContentPane(new AuthForm(isChangeForm).panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(250, 200);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Icons/scooter-Freepik.png")));
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
